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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinTask;

import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.list.ListIterable;
import org.eclipse.collections.impl.parallel.ProcedureFactory;
import org.eclipse.collections.impl.utility.ArrayListIterate;
import org.eclipse.collections.impl.utility.ListIterate;

/**
 * A ForkJoinTask implementation that executes a Procedure on a section of a List.
 * <p>
 * This task processes a contiguous range of list elements, applying a {@link Procedure}
 * to each element. It is the workhorse for parallel list processing in the ForkJoin framework,
 * optimized for different list types to achieve maximum performance.
 * </p>
 * <p>
 * The task optimizes execution based on the list type:
 * <ul>
 * <li>For {@link ListIterable}: Uses the optimized forEach method</li>
 * <li>For {@link ArrayList}: Uses ArrayListIterate.forEach for efficient random access</li>
 * <li>For other List types: Uses ListIterate.forEach</li>
 * </ul>
 * </p>
 * <p>
 * Performance Characteristics: By dividing the list into sections and processing them in parallel,
 * this task enables efficient utilization of multi-core processors while maintaining cache locality
 * within each section.
 * </p>
 *
 * @param <T> the type of elements in the list
 * @param <PT> the type of procedure that processes elements
 * @see FJListProcedureRunner
 * @see Procedure
 */
public class FJListProcedureTask<T, PT extends Procedure<? super T>> extends ForkJoinTask<PT>
{
    private static final long serialVersionUID = 1L;

    private final ProcedureFactory<PT> procedureFactory;
    private PT procedure;
    private final List<T> list;
    private final int start;
    private final int end;
    private final FJListProcedureRunner<T, PT> taskRunner;

    /**
     * Creates a ForkJoinTask for executing a Procedure on a specific section of a List.
     * <p>
     * The task processes elements from the calculated start index to the end index.
     * If this is the last task (isLast = true), it processes all remaining elements
     * to ensure complete coverage of the list.
     * </p>
     *
     * @param newFJTaskRunner the runner that coordinates this task with other parallel tasks
     * @param newProcedureFactory the factory that creates the Procedure instance for this task
     * @param list the list containing elements to process
     * @param index the section index this task will process (0-based)
     * @param sectionSize the number of elements per section
     * @param isLast true if this is the last task, which should process all remaining elements
     */
    public FJListProcedureTask(
            FJListProcedureRunner<T, PT> newFJTaskRunner, ProcedureFactory<PT> newProcedureFactory,
            List<T> list, int index, int sectionSize, boolean isLast)
    {
        this.taskRunner = newFJTaskRunner;
        this.procedureFactory = newProcedureFactory;
        this.list = list;
        this.start = index * sectionSize;
        this.end = isLast ? this.list.size() - 1 : this.start + sectionSize - 1;
    }

    /**
     * Executes the task by creating a Procedure instance and applying it to the assigned
     * section of the list. This method is called by the ForkJoin framework to perform the actual work.
     * <p>
     * The execution performs the following steps:
     * <ol>
     * <li>Creates a new Procedure instance using the procedure factory</li>
     * <li>Processes the assigned range of elements using the most efficient iteration method for the list type</li>
     * <li>Notifies the task runner upon completion or failure</li>
     * </ol>
     * </p>
     * <p>
     * The procedure is applied to each element in the range [start, end] inclusive.
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
            if (this.list instanceof ListIterable)
            {
                ((ListIterable<T>) this.list).forEach(this.start, this.end, this.procedure);
            }
            else if (this.list instanceof ArrayList)
            {
                ArrayListIterate.forEach((ArrayList<T>) this.list, this.start, this.end, this.procedure);
            }
            else
            {
                ListIterate.forEach(this.list, this.start, this.end, this.procedure);
            }
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
     * Returns the Procedure instance created and executed by this task.
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
