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

import java.util.concurrent.ForkJoinTask;

import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.impl.parallel.BatchIterable;
import org.eclipse.collections.impl.parallel.ProcedureFactory;

/**
 * A ForkJoinTask implementation that executes a procedure on a batch of elements from a BatchIterable.
 * This task divides the batch processing work across multiple sections that can be executed in parallel
 * using the ForkJoin framework.
 * <p>
 * This class is designed to work with {@link FJBatchIterableProcedureRunner} to coordinate parallel
 * execution of procedures across multiple tasks. Each task operates on a specific section of the
 * BatchIterable, determined by the section index and count.
 * </p>
 *
 * @param <T> the type of elements in the iterable
 * @param <PT> the type of procedure that processes elements
 * @see FJBatchIterableProcedureRunner
 * @see BatchIterable
 */
public class FJBatchIterableProcedureTask<T, PT extends Procedure<? super T>> extends ForkJoinTask<PT>
{
    private static final long serialVersionUID = 1L;

    private final ProcedureFactory<PT> procedureFactory;
    private PT procedure;
    private final BatchIterable<T> iterable;
    private final int sectionIndex;
    private final int sectionCount;
    private final FJBatchIterableProcedureRunner<T, PT> taskRunner;

    /**
     * Creates a ForkJoinTask for executing a procedure on a specific section of a BatchIterable.
     * <p>
     * The task will process one section of the iterable's batches, allowing for parallel processing
     * across multiple sections. The procedure is created by the factory when the task executes,
     * enabling stateful procedures that are combined later.
     * </p>
     *
     * @param newFJTaskRunner the runner that coordinates this task with other parallel tasks
     * @param newProcedureFactory the factory that creates the procedure instance for this task
     * @param iterable the batch iterable containing elements to process
     * @param index the section index this task will process (0-based)
     * @param count the total number of sections the iterable is divided into
     */
    public FJBatchIterableProcedureTask(
            FJBatchIterableProcedureRunner<T, PT> newFJTaskRunner,
            ProcedureFactory<PT> newProcedureFactory, BatchIterable<T> iterable, int index, int count)
    {
        this.taskRunner = newFJTaskRunner;
        this.procedureFactory = newProcedureFactory;
        this.iterable = iterable;
        this.sectionIndex = index;
        this.sectionCount = count;
    }

    /**
     * Executes the task by creating a procedure instance and applying it to the assigned section
     * of the batch iterable. This method is called by the ForkJoin framework to perform the actual work.
     * <p>
     * The execution performs the following steps:
     * <ol>
     * <li>Creates a new procedure instance using the procedure factory</li>
     * <li>Processes the assigned section by calling batchForEach on the iterable</li>
     * <li>Notifies the task runner upon completion or failure</li>
     * </ol>
     * </p>
     * <p>
     * Any exceptions thrown during execution are caught and reported to the task runner,
     * which will propagate them appropriately.
     * </p>
     *
     * @return true to indicate the task has completed
     */
    @Override
    protected boolean exec()
    {
        try
        {
            this.procedure = this.procedureFactory.create();
            this.iterable.batchForEach(this.procedure, this.sectionIndex, this.sectionCount);
        }
        catch (Throwable newError)
        {
            this.taskRunner.setFailed(newError);
        }
        finally
        {
            this.taskRunner.taskCompleted(this);
        }
        return true;
    }

    /**
     * Returns the procedure instance created and executed by this task.
     * <p>
     * This method is called after the task completes to retrieve the procedure,
     * which may contain accumulated state from processing its section of elements.
     * The procedure can then be combined with results from other tasks.
     * </p>
     *
     * @return the procedure instance, or null if the task has not yet executed
     */
    @Override
    public PT getRawResult()
    {
        return this.procedure;
    }

    /**
     * This operation is not supported for this task type.
     * <p>
     * The result (procedure) is created internally during task execution and cannot be
     * set externally.
     * </p>
     *
     * @param value the value to set (ignored)
     * @throws UnsupportedOperationException always thrown as this operation is not supported
     */
    @Override
    protected void setRawResult(PT value)
    {
        throw new UnsupportedOperationException();
    }
}
