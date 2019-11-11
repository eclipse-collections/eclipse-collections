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
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.parallel.BatchIterable;
import org.eclipse.collections.impl.parallel.Combiner;
import org.eclipse.collections.impl.parallel.ProcedureFactory;

public class FJBatchIterableProcedureRunner<T, PT extends Procedure<? super T>> implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Throwable error;
    private final Combiner<PT> combiner;
    private final int taskCount;
    private final BlockingQueue<PT> outputQueue;

    public FJBatchIterableProcedureRunner(Combiner<PT> newCombiner, int taskCount)
    {
        this.combiner = newCombiner;
        this.taskCount = taskCount;
        this.outputQueue = this.combiner.useCombineOne() ? new ArrayBlockingQueue<>(taskCount) : null;
    }

    private MutableList<ForkJoinTask<PT>> createAndExecuteTasks(ForkJoinPool executor, ProcedureFactory<PT> procedureFactory, BatchIterable<T> iterable)
    {
        MutableList<ForkJoinTask<PT>> tasks = FastList.newList(this.taskCount);
        for (int index = 0; index < this.taskCount; index++)
        {
            ForkJoinTask<PT> voidBlockFJTask = new FJBatchIterableProcedureTask<>(this, procedureFactory, iterable, index, this.taskCount);
            tasks.add(voidBlockFJTask);
            executor.execute(voidBlockFJTask);
        }
        return tasks;
    }

    public void setFailed(Throwable newError)
    {
        this.error = newError;
    }

    public void taskCompleted(ForkJoinTask<PT> task)
    {
        if (this.combiner.useCombineOne())
        {
            this.outputQueue.add(task.getRawResult());
        }
    }

    public void executeAndCombine(ForkJoinPool executor, ProcedureFactory<PT> procedureFactory, BatchIterable<T> list)
    {
        MutableList<ForkJoinTask<PT>> tasks = this.createAndExecuteTasks(executor, procedureFactory, list);
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

    private final class ProcedureExtractor implements Function<ForkJoinTask<PT>, PT>
    {
        private static final long serialVersionUID = 1L;

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
