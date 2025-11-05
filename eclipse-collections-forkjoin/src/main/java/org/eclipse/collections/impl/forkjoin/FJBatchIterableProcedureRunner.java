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
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.parallel.BatchIterable;
import org.eclipse.collections.impl.parallel.Combiner;
import org.eclipse.collections.impl.parallel.ProcedureFactory;

/**
 * Coordinates the parallel execution of procedures across multiple sections of a BatchIterable using
 * the ForkJoin framework.
 * <p>
 * This runner creates and manages multiple {@link FJBatchIterableProcedureTask} instances, each processing
 * a different section of the BatchIterable. After all tasks complete, it combines their results using
 * the provided {@link Combiner}.
 * </p>
 * <p>
 * The runner supports two combination strategies:
 * <ul>
 * <li><b>combineOne:</b> Results are combined incrementally as each task completes, using a blocking queue</li>
 * <li><b>combineAll:</b> All task results are collected first, then combined together at the end</li>
 * </ul>
 * </p>
 * <p>
 * Thread Safety: This class uses blocking queues and proper synchronization to coordinate parallel task execution.
 * However, the procedures themselves must be thread-safe or use concurrent-aware data structures if they share state.
 * </p>
 *
 * @param <T> the type of elements being processed
 * @param <PT> the type of procedure that processes the elements
 * @see FJBatchIterableProcedureTask
 * @see Combiner
 * @see BatchIterable
 */
public class FJBatchIterableProcedureRunner<T, PT extends Procedure<? super T>> implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Throwable error;
    private final Combiner<PT> combiner;
    private final int taskCount;
    private final BlockingQueue<PT> outputQueue;

    /**
     * Creates a new runner for coordinating parallel procedure execution across multiple tasks.
     * <p>
     * The runner initializes an output queue if the combiner uses incremental combination
     * (combineOne), allowing results to be collected as tasks complete. Otherwise, results
     * are collected after all tasks finish.
     * </p>
     *
     * @param newCombiner the combiner that will merge results from all parallel tasks
     * @param taskCount the number of parallel tasks to create and execute
     */
    public FJBatchIterableProcedureRunner(Combiner<PT> newCombiner, int taskCount)
    {
        this.combiner = newCombiner;
        this.taskCount = taskCount;
        this.outputQueue = this.combiner.useCombineOne() ? new ArrayBlockingQueue<>(taskCount) : null;
    }

    /**
     * Creates and submits parallel tasks to the ForkJoin executor.
     * <p>
     * Each task is assigned a unique section index and will process its portion of the
     * BatchIterable independently. All tasks are submitted to the executor immediately.
     * </p>
     *
     * @param executor the ForkJoinPool to execute the tasks
     * @param procedureFactory the factory that creates procedure instances for each task
     * @param iterable the batch iterable to process in parallel
     * @return a list of submitted ForkJoinTask instances
     */
    private FastList<ForkJoinTask<PT>> createAndExecuteTasks(ForkJoinPool executor, ProcedureFactory<PT> procedureFactory, BatchIterable<T> iterable)
    {
        FastList<ForkJoinTask<PT>> tasks = FastList.newList(this.taskCount);
        for (int index = 0; index < this.taskCount; index++)
        {
            ForkJoinTask<PT> voidBlockFJTask = new FJBatchIterableProcedureTask<>(this, procedureFactory, iterable, index, this.taskCount);
            tasks.add(voidBlockFJTask);
            executor.execute(voidBlockFJTask);
        }
        return tasks;
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
     * <li>Creates and submits all tasks to the executor</li>
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
     * @param list the batch iterable to process in parallel
     * @throws RuntimeException if any task fails during execution
     */
    public void executeAndCombine(ForkJoinPool executor, ProcedureFactory<PT> procedureFactory, BatchIterable<T> list)
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
