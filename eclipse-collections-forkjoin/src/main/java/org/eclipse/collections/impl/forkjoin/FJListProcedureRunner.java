/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.forkjoin;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.parallel.Combiner;
import org.eclipse.collections.impl.parallel.ProcedureFactory;

/**
 * Coordinates the parallel execution of procedures across sections of a List using the ForkJoin framework.
 * <p>
 * This runner divides a List into multiple sections and creates {@link FJListProcedureTask} instances
 * to process each section in parallel. The list is divided based on the task count, with each task
 * processing a contiguous range of elements. After all tasks complete, their results are combined
 * using the provided {@link Combiner}.
 * </p>
 * <p>
 * The runner supports two combination strategies:
 * <ul>
 * <li><b>combineOne:</b> Results are combined incrementally as each task completes, using a blocking queue</li>
 * <li><b>combineAll:</b> All task results are collected first, then combined together at the end</li>
 * </ul>
 * </p>
 * <p>
 * Performance Characteristics: This runner is optimized for List implementations, particularly those
 * with efficient random access. It divides the list by index ranges, making it highly efficient for
 * ArrayList and similar structures.
 * </p>
 *
 * @param <T> the type of elements in the list
 * @param <PT> the type of procedure that processes the elements
 * @see FJListProcedureTask
 * @see Combiner
 */
public class FJListProcedureRunner<T, PT extends Procedure<? super T>> implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Throwable error;
    private final Combiner<PT> combiner;
    private final int taskCount;
    private final BlockingQueue<PT> outputQueue;

    /**
     * Creates a new runner for coordinating parallel procedure execution across list sections.
     * <p>
     * The runner initializes an output queue if the combiner uses incremental combination
     * (combineOne), allowing results to be collected as tasks complete. Otherwise, results
     * are collected after all tasks finish.
     * </p>
     *
     * @param newCombiner the combiner that will merge results from all parallel tasks
     * @param taskCount the number of parallel tasks to create and execute
     */
    public FJListProcedureRunner(Combiner<PT> newCombiner, int taskCount)
    {
        this.combiner = newCombiner;
        this.taskCount = taskCount;
        this.outputQueue = this.combiner.useCombineOne() ? new ArrayBlockingQueue<>(taskCount) : null;
    }

    /**
     * Creates and submits parallel tasks to the ForkJoin executor.
     * <p>
     * The list is divided into sections of approximately equal size. Each task is assigned
     * a contiguous range of indices to process. The last task may process slightly more
     * elements to account for any remainder from the division.
     * </p>
     *
     * @param executor the ForkJoinPool to execute the tasks
     * @param procedureFactory the factory that creates procedure instances for each task
     * @param list the list to process in parallel
     * @return a list of submitted ForkJoinTask instances
     */
    private FastList<ForkJoinTask<PT>> createAndExecuteTasks(ForkJoinPool executor, ProcedureFactory<PT> procedureFactory, List<T> list)
    {
        FastList<ForkJoinTask<PT>> tasks = FastList.newList(this.taskCount);
        int sectionSize = list.size() / this.taskCount;
        int taskCountMinusOne = this.taskCount - 1;
        for (int index = 0; index < this.taskCount; index++)
        {
            ForkJoinTask<PT> task = this.createTask(procedureFactory, list, sectionSize, taskCountMinusOne, index);
            tasks.add(task);
            executor.execute(task);
        }
        return tasks;
    }

    /**
     * Creates a single task for processing a section of the list.
     * <p>
     * This method is protected to allow subclasses to customize task creation.
     * The task will process elements from index * sectionSize up to (index + 1) * sectionSize - 1,
     * except for the last task which processes all remaining elements.
     * </p>
     *
     * @param procedureFactory the factory that creates the procedure instance
     * @param list the list to process
     * @param sectionSize the number of elements per section
     * @param taskCountMinusOne the index of the last task (taskCount - 1)
     * @param index the index of this task (0-based)
     * @return a new FJListProcedureTask instance
     */
    protected FJListProcedureTask<T, PT> createTask(ProcedureFactory<PT> procedureFactory, List<T> list, int sectionSize, int taskCountMinusOne, int index)
    {
        return new FJListProcedureTask<>(this, procedureFactory, list, index, sectionSize, index == taskCountMinusOne);
    }

    /**
     * Records an error that occurred during task execution.
     * <p>
     * When any task encounters an exception, it calls this method to record the error.
     * The error will be propagated as a RuntimeException after all tasks complete or fail.
     * Only the first error is recorded if multiple tasks fail.
     * </p>
     *
     * @param newError the exception that occurred during task execution
     */
    public void setFailed(Throwable newError)
    {
        this.error = newError;
    }

    /**
     * Notifies the runner that a task has completed successfully.
     * <p>
     * If the combiner uses incremental combination (combineOne), the task's result
     * is added to the output queue for immediate processing. Otherwise, the result
     * will be retrieved later using the task's getRawResult() method.
     * </p>
     *
     * @param task the task that has completed
     */
    public void taskCompleted(ForkJoinTask<PT> task)
    {
        if (this.combiner.useCombineOne())
        {
            this.outputQueue.add(task.getRawResult());
        }
    }

    /**
     * Executes all tasks in parallel and combines their results.
     * <p>
     * This is the main entry point for parallel processing. It performs the following steps:
     * <ol>
     * <li>Divides the list into sections and creates tasks</li>
     * <li>Submits all tasks to the executor for parallel execution</li>
     * <li>If using combineOne strategy, waits for and combines results as tasks complete</li>
     * <li>Checks for errors and throws an exception if any task failed</li>
     * <li>If using combineAll strategy, combines all results together at the end</li>
     * </ol>
     * </p>
     * <p>
     * This method blocks until all tasks complete and results are combined.
     * </p>
     *
     * @param executor the ForkJoinPool to execute tasks on
     * @param procedureFactory the factory that creates procedure instances for each task
     * @param list the list to process in parallel
     * @throws RuntimeException if any task fails during execution
     */
    public void executeAndCombine(ForkJoinPool executor, ProcedureFactory<PT> procedureFactory, List<T> list)
    {
        FastList<ForkJoinTask<PT>> tasks = this.createAndExecuteTasks(executor, procedureFactory, list);
        if (this.combiner.useCombineOne())
        {
            this.join();
        }
        if (this.error != null)
        {
            throw new RuntimeException("One or more parallel tasks failed", this.error);
        }
        if (!this.combiner.useCombineOne())
        {
            this.combiner.combineAll(tasks.asLazy().collect(new ProcedureExtractor()));
        }
    }

    /**
     * Waits for all tasks to complete and incrementally combines their results.
     * <p>
     * This method is called when the combiner uses the combineOne strategy. It blocks
     * on the output queue, retrieving and combining results as each task completes.
     * This allows for memory-efficient streaming combination without holding all
     * results in memory simultaneously.
     * </p>
     *
     * @throws RuntimeException if the thread is interrupted while waiting for results
     */
    private void join()
    {
        try
        {
            int remainingTaskCount = this.taskCount;
            while (remainingTaskCount > 0)
            {
                this.combiner.combineOne(this.outputQueue.take());
                remainingTaskCount--;
            }
        }
        catch (InterruptedException e)
        {
            throw new RuntimeException("Combine failed", e);
        }
    }

    /**
     * A function that extracts the procedure result from a completed ForkJoinTask.
     * <p>
     * This helper class is used when the combineAll strategy is employed. It retrieves
     * the computed result from each task by calling its get() method, which blocks if
     * the task is not yet complete.
     * </p>
     */
    private final class ProcedureExtractor implements Function<ForkJoinTask<PT>, PT>
    {
        private static final long serialVersionUID = 1L;

        /**
         * Extracts and returns the procedure from a ForkJoinTask.
         * <p>
         * This method calls the task's get() method, which waits for the task to complete
         * if necessary and returns its result. Any exceptions during task execution are
         * wrapped in a RuntimeException.
         * </p>
         *
         * @param object the ForkJoinTask to extract the result from
         * @return the procedure result from the task
         * @throws RuntimeException if the task was interrupted or failed during execution
         */
        @Override
        public PT valueOf(ForkJoinTask<PT> object)
        {
            try
            {
                return object.get();
            }
            catch (InterruptedException | ExecutionException e)
            {
                throw new RuntimeException(e);
            }
        }
    }
}
