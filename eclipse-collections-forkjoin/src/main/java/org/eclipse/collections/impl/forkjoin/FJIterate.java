/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.forkjoin;

import java.util.Collection;
import java.util.List;
import java.util.RandomAccess;
import java.util.concurrent.ForkJoinPool;

import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function0;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.api.list.ListIterable;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.map.MutableMapIterable;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.impl.block.procedure.MultimapPutProcedure;
import org.eclipse.collections.impl.block.procedure.MutatingAggregationProcedure;
import org.eclipse.collections.impl.block.procedure.NonMutatingAggregationProcedure;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.list.fixed.ArrayAdapter;
import org.eclipse.collections.impl.map.mutable.ConcurrentHashMap;
import org.eclipse.collections.impl.multimap.list.SynchronizedPutFastListMultimap;
import org.eclipse.collections.impl.parallel.BatchIterable;
import org.eclipse.collections.impl.parallel.CollectIfProcedureCombiner;
import org.eclipse.collections.impl.parallel.CollectIfProcedureFactory;
import org.eclipse.collections.impl.parallel.Combiner;
import org.eclipse.collections.impl.parallel.Combiners;
import org.eclipse.collections.impl.parallel.CountCombiner;
import org.eclipse.collections.impl.parallel.CountProcedureFactory;
import org.eclipse.collections.impl.parallel.FastListCollectProcedureCombiner;
import org.eclipse.collections.impl.parallel.FastListCollectProcedureFactory;
import org.eclipse.collections.impl.parallel.FlatCollectProcedureCombiner;
import org.eclipse.collections.impl.parallel.FlatCollectProcedureFactory;
import org.eclipse.collections.impl.parallel.ObjectIntProcedureFactory;
import org.eclipse.collections.impl.parallel.ParallelArrayIterate;
import org.eclipse.collections.impl.parallel.ParallelIterate;
import org.eclipse.collections.impl.parallel.PassThruCombiner;
import org.eclipse.collections.impl.parallel.PassThruObjectIntProcedureFactory;
import org.eclipse.collections.impl.parallel.PassThruProcedureFactory;
import org.eclipse.collections.impl.parallel.ProcedureFactory;
import org.eclipse.collections.impl.parallel.RejectProcedureCombiner;
import org.eclipse.collections.impl.parallel.RejectProcedureFactory;
import org.eclipse.collections.impl.parallel.SelectProcedureCombiner;
import org.eclipse.collections.impl.parallel.SelectProcedureFactory;
import org.eclipse.collections.impl.utility.Iterate;

/**
 * The FJIterate class contains several parallel algorithms that work with Collections and make use of Java's fork-join
 * framework. All the higher level parallel algorithms depend on the basic parallel algorithm named {@code forEach}.
 * The forEach algorithm employs a batching fork and join approach.
 * <p>
 * All Collections that are not either a {@link RandomAccess} or {@link List} are first converted to a Java array
 * using {@link Iterate#toArray(Iterable)}, and then run with one of the {@code FJIterate.forEach} methods.
 *
 * @see ParallelIterate
 */
public final class FJIterate
{
    public static final int DEFAULT_MIN_FORK_SIZE = 5000;
    private static final int DEFAULT_PARALLEL_TASK_COUNT = ParallelIterate.getDefaultTaskCount() * 4;

    private static final ForkJoinPool FORK_JOIN_POOL = new ForkJoinPool(ParallelIterate.getDefaultMaxThreadPoolSize());

    private FJIterate()
    {
        // utility class only
    }

    /**
     * Iterates over the collection in parallel batches, passing each element and its index to the procedure.
     * <p>
     * This method divides the collection into batches and processes them in parallel using the default
     * ForkJoin pool. The {@code ObjectIntProcedure} receives both the element and its index position.
     * </p>
     * <p>
     * The procedure must be either stateless or use thread-safe data structures if state is shared
     * across parallel executions.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * // Example 1: Build an index-to-element map in parallel
     * MutableList<String> names = Lists.mutable.with("Alice", "Bob", "Charlie", "David");
     * ConcurrentHashMap<Integer, String> indexMap = new ConcurrentHashMap<>();
     * FJIterate.forEachWithIndex(names, (name, index) -> indexMap.put(index, name));
     *
     * // Example 2: Perform indexed updates to a thread-safe collection
     * ConcurrentHashMap<Integer, Integer> squaresMap = new ConcurrentHashMap<>();
     * FJIterate.forEachWithIndex(
     *     IntInterval.oneTo(1000).toList(),
     *     (num, index) -> squaresMap.put(index, num * num)
     * );
     * }</pre>
     *
     * @param <T> the type of elements in the iterable
     * @param iterable the collection to iterate over
     * @param procedure the procedure to apply to each element with its index
     * @see #forEachWithIndex(Iterable, ObjectIntProcedure, ForkJoinPool)
     */
    public static <T> void forEachWithIndex(Iterable<T> iterable, ObjectIntProcedure<? super T> procedure)
    {
        FJIterate.forEachWithIndex(iterable, procedure, FJIterate.FORK_JOIN_POOL);
    }

    /**
     * Iterate over the collection specified in parallel batches using the default runtime parameters. The
     * ObjectIntProcedure used must be stateless, or use concurrent aware objects if they are to be shared. The code
     * is executed against the specified executor.
     *
     * <pre>e.g.
     * Map&lt;Integer, Object&gt; chm = new ConcurrentHashMap&lt;Integer, Object&gt;();
     * FJIterate.<b>forEachWithIndex</b>(collection, (each, index) -&gt; chm.put(index, each), executor);
     * </pre>
     *
     * @param executor Use this executor for all execution.
     */
    public static <T, PT extends ObjectIntProcedure<? super T>> void forEachWithIndex(
            Iterable<T> iterable,
            PT procedure,
            ForkJoinPool executor)
    {
        ObjectIntProcedureFactory<PT> procedureFactory = new PassThruObjectIntProcedureFactory<>(procedure);
        Combiner<PT> combiner = new PassThruCombiner<>();
        FJIterate.forEachWithIndex(iterable, procedureFactory, combiner, executor);
    }

    /**
     * Iterate over the collection specified in parallel batches. The
     * ObjectIntProcedure used must be stateless, or use concurrent aware objects if they are to be shared. The
     * specified minimum fork size and task count are used instead of the default values.
     *
     * @param minForkSize Only run in parallel if input collection is longer than this.
     * @param taskCount   The number of parallel tasks to submit to the executor.
     * @see #forEachWithIndex(Iterable, ObjectIntProcedure)
     */
    public static <T, PT extends ObjectIntProcedure<? super T>> void forEachWithIndex(
            Iterable<T> iterable,
            PT procedure,
            int minForkSize,
            int taskCount)
    {
        ObjectIntProcedureFactory<PT> procedureFactory = new PassThruObjectIntProcedureFactory<>(procedure);
        Combiner<PT> combiner = new PassThruCombiner<>();
        FJIterate.forEachWithIndex(iterable, procedureFactory, combiner, minForkSize, taskCount);
    }

    public static <T, PT extends ObjectIntProcedure<? super T>> void forEachWithIndex(
            Iterable<T> iterable,
            ObjectIntProcedureFactory<PT> procedureFactory,
            Combiner<PT> combiner,
            ForkJoinPool executor)
    {
        int taskCount = Math.max(
                FJIterate.DEFAULT_PARALLEL_TASK_COUNT,
                Iterate.sizeOf(iterable) / DEFAULT_MIN_FORK_SIZE);
        FJIterate.forEachWithIndex(iterable, procedureFactory, combiner, DEFAULT_MIN_FORK_SIZE, taskCount, executor);
    }

    public static <T, PT extends ObjectIntProcedure<? super T>> void forEachWithIndex(
            Iterable<T> iterable,
            ObjectIntProcedureFactory<PT> procedureFactory,
            Combiner<PT> combiner,
            int minForkSize,
            int taskCount)
    {
        FJIterate.forEachWithIndex(iterable, procedureFactory, combiner, minForkSize, taskCount, FJIterate.FORK_JOIN_POOL);
    }

    public static <T, PT extends ObjectIntProcedure<? super T>> void forEachWithIndex(
            Iterable<T> iterable,
            ObjectIntProcedureFactory<PT> procedureFactory,
            Combiner<PT> combiner,
            int minForkSize,
            int taskCount,
            ForkJoinPool executor)
    {
        if (Iterate.notEmpty(iterable))
        {
            if ((iterable instanceof RandomAccess || iterable instanceof ListIterable)
                    && iterable instanceof List)
            {
                FJIterate.forEachWithIndexInListOnExecutor(
                        (List<T>) iterable,
                        procedureFactory,
                        combiner,
                        minForkSize,
                        taskCount,
                        executor);
            }
            else
            {
                FJIterate.forEachWithIndexInListOnExecutor(
                        ArrayAdapter.adapt((T[]) Iterate.toArray(iterable)),
                        procedureFactory,
                        combiner,
                        minForkSize,
                        taskCount,
                        executor);
            }
        }
    }

    public static <T, PT extends ObjectIntProcedure<? super T>> void forEachWithIndexInListOnExecutor(
            List<T> list,
            ObjectIntProcedureFactory<PT> procedureFactory,
            Combiner<PT> combiner,
            int minForkSize,
            int taskCount,
            ForkJoinPool executor)
    {
        int size = list.size();
        if (size < minForkSize || FJIterate.executedInsideOfForEach())
        {
            PT procedure = procedureFactory.create();
            Iterate.forEachWithIndex(list, procedure);
            if (combiner.useCombineOne())
            {
                combiner.combineOne(procedure);
            }
            else
            {
                combiner.combineAll(Lists.immutable.of(procedure));
            }
        }
        else
        {
            int threadCount = Math.min(size, taskCount);
            new FJListObjectIntProcedureRunner<>(combiner, threadCount).executeAndCombine(executor, procedureFactory, list);
        }
    }

    /**
     * Iterates over the collection in parallel batches, applying the procedure to each element.
     * <p>
     * This method divides the collection into batches and processes them in parallel using the default
     * ForkJoin pool with default batch size (5000 elements) and task count. This is the primary method
     * for parallel iteration and provides good performance for most use cases.
     * </p>
     * <p>
     * The procedure must be either stateless or use thread-safe data structures if state is shared
     * across parallel executions. For stateful operations, consider using thread-safe collections
     * like ConcurrentHashMap or atomic variables.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * // Example 1: Populate a concurrent map in parallel
     * MutableList<Person> people = getLargeListOfPeople(); // 10,000+ people
     * ConcurrentHashMap<String, Person> nameMap = new ConcurrentHashMap<>();
     * FJIterate.forEach(people, person -> nameMap.put(person.getName(), person));
     *
     * // Example 2: Parallel processing with side effects to a thread-safe counter
     * AtomicLong totalAge = new AtomicLong(0);
     * FJIterate.forEach(people, person -> totalAge.addAndGet(person.getAge()));
     *
     * // Example 3: Send notifications in parallel
     * MutableList<Customer> customers = getCustomers();
     * FJIterate.forEach(customers, customer -> emailService.send(customer));
     * }</pre>
     *
     * @param <T> the type of elements in the iterable
     * @param iterable the collection to iterate over
     * @param procedure the procedure to apply to each element
     * @see #forEach(Iterable, Procedure, int, int)
     * @see #forEach(Iterable, Procedure, ForkJoinPool)
     */
    public static <T> void forEach(Iterable<T> iterable, Procedure<? super T> procedure)
    {
        FJIterate.forEach(iterable, procedure, FJIterate.FORK_JOIN_POOL);
    }

    /**
     * Iterate over the collection specified in parallel batches using default runtime parameter values. The
     * {@code Procedure} used must be stateless, or use concurrent aware objects if they are to be shared.
     * <p>
     * e.g.
     * <pre>
     * Map&lt;Object, Boolean&gt; chm = new ConcurrentHashMap&lt;Object, Boolean&gt;();
     * FJIterate.<b>forEachBatchSize</b>(collection, each -&gt; chm.put(each, Boolean.TRUE), 100);
     * </pre>
     */
    public static <T> void forEach(Iterable<T> iterable, Procedure<? super T> procedure, int batchSize)
    {
        FJIterate.forEach(iterable, procedure, batchSize, FJIterate.FORK_JOIN_POOL);
    }

    public static <T> void forEach(Iterable<T> iterable, Procedure<? super T> procedure, int batchSize, ForkJoinPool executor)
    {
        FJIterate.forEach(iterable, procedure, batchSize, FJIterate.calculateTaskCount(iterable, batchSize), executor);
    }

    /**
     * Iterate over the collection specified in parallel batches using default runtime parameter values
     * and the specified executor.
     * The {@code Procedure} used must be stateless, or use concurrent aware objects if they are to be shared.
     *
     * @param executor Use this executor for all execution.
     * @see #forEach(Iterable, Procedure)
     */
    public static <T, PT extends Procedure<? super T>> void forEach(
            Iterable<T> iterable,
            PT procedure,
            ForkJoinPool executor)
    {
        ProcedureFactory<PT> procedureFactory = new PassThruProcedureFactory<>(procedure);
        Combiner<PT> combiner = new PassThruCombiner<>();
        FJIterate.forEach(iterable, procedureFactory, combiner, executor);
    }

    /**
     * Iterate over the collection specified in parallel batches using the specified minimum fork and task count sizes.
     * The {@code Procedure} used must be stateless, or use concurrent aware objects if they are to be shared.
     *
     * @param minForkSize Only run in parallel if input collection is longer than this.
     * @param taskCount   The number of parallel tasks to submit to the executor.
     * @see #forEach(Iterable, Procedure)
     */
    public static <T, PT extends Procedure<? super T>> void forEach(
            Iterable<T> iterable,
            PT procedure,
            int minForkSize,
            int taskCount)
    {
        FJIterate.forEach(iterable, procedure, minForkSize, taskCount, FJIterate.FORK_JOIN_POOL);
    }

    public static <T, PT extends Procedure<? super T>> void forEach(
            Iterable<T> iterable,
            PT procedure,
            int minForkSize,
            int taskCount,
            ForkJoinPool executor)
    {
        ProcedureFactory<PT> procedureFactory = new PassThruProcedureFactory<>(procedure);
        Combiner<PT> combiner = new PassThruCombiner<>();
        FJIterate.forEach(iterable, procedureFactory, combiner, minForkSize, taskCount, executor);
    }

    public static <T, PT extends Procedure<? super T>> void forEach(
            Iterable<T> iterable,
            ProcedureFactory<PT> procedureFactory,
            Combiner<PT> combiner,
            ForkJoinPool executor)
    {
        FJIterate.forEach(iterable, procedureFactory, combiner, FJIterate.DEFAULT_MIN_FORK_SIZE, executor);
    }

    public static <T, PT extends Procedure<? super T>> void forEach(
            Iterable<T> iterable,
            ProcedureFactory<PT> procedureFactory,
            Combiner<PT> combiner)
    {
        FJIterate.forEach(iterable, procedureFactory, combiner, FJIterate.FORK_JOIN_POOL);
    }

    /**
     * Iterate over the collection specified in parallel batches using the default values for the task size. The
     * ProcedureFactory can create stateful closures that will be collected and combined using the specified Combiner.
     */
    public static <T, PT extends Procedure<? super T>> void forEach(
            Iterable<T> iterable,
            ProcedureFactory<PT> procedureFactory,
            Combiner<PT> combiner,
            int batchSize)
    {
        FJIterate.forEach(iterable, procedureFactory, combiner, batchSize, FJIterate.FORK_JOIN_POOL);
    }

    public static <T, PT extends Procedure<? super T>> void forEach(
            Iterable<T> iterable,
            ProcedureFactory<PT> blockFactory,
            Combiner<PT> combiner,
            int batchSize,
            ForkJoinPool executor)
    {
        FJIterate.forEach(iterable, blockFactory, combiner, batchSize, FJIterate.calculateTaskCount(iterable, batchSize), executor);
    }

    /**
     * Iterate over the collection specified in parallel batches using the default values for the task size. The
     * ProcedureFactory can create stateful closures that will be collected and combined using the specified Combiner.
     */
    public static <T, PT extends Procedure<? super T>> void forEach(
            Iterable<T> iterable,
            ProcedureFactory<PT> procedureFactory,
            Combiner<PT> combiner,
            int minForkSize,
            int taskCount)
    {
        FJIterate.forEach(iterable, procedureFactory, combiner, minForkSize, taskCount, FJIterate.FORK_JOIN_POOL);
    }

    public static <T, PT extends Procedure<? super T>> void forEach(
            Iterable<T> iterable,
            ProcedureFactory<PT> procedureFactory,
            Combiner<PT> combiner,
            int minForkSize,
            int taskCount,
            ForkJoinPool executor)
    {
        if (Iterate.notEmpty(iterable))
        {
            if ((iterable instanceof RandomAccess || iterable instanceof ListIterable)
                    && iterable instanceof List)
            {
                FJIterate.forEachInListOnExecutor(
                        (List<T>) iterable,
                        procedureFactory,
                        combiner,
                        minForkSize,
                        taskCount,
                        executor);
            }
            else if (iterable instanceof BatchIterable)
            {
                FJIterate.forEachInBatchWithExecutor(
                        (BatchIterable<T>) iterable,
                        procedureFactory,
                        combiner,
                        minForkSize,
                        taskCount,
                        executor);
            }
            else
            {
                ParallelArrayIterate.forEachOn(
                        (T[]) Iterate.toArray(iterable),
                        procedureFactory,
                        combiner,
                        minForkSize,
                        taskCount,
                        executor);
            }
        }
    }

    public static <T, PT extends Procedure<? super T>> void forEachInListOnExecutor(
            List<T> list,
            ProcedureFactory<PT> procedureFactory,
            Combiner<PT> combiner,
            int minForkSize,
            int taskCount,
            ForkJoinPool executor)
    {
        int size = list.size();
        if (size < minForkSize || FJIterate.executedInsideOfForEach())
        {
            PT procedure = procedureFactory.create();
            Iterate.forEach(list, procedure);
            if (combiner.useCombineOne())
            {
                combiner.combineOne(procedure);
            }
            else
            {
                combiner.combineAll(Lists.immutable.of(procedure));
            }
        }
        else
        {
            int newTaskCount = Math.min(size, taskCount);
            new FJListProcedureRunner<>(combiner, newTaskCount).executeAndCombine(executor, procedureFactory, list);
        }
    }

    public static <T, PT extends Procedure<? super T>> void forEachInBatchWithExecutor(
            BatchIterable<T> batchIterable,
            ProcedureFactory<PT> procedureFactory, Combiner<PT> combiner, int minForkSize, int taskCount,
            ForkJoinPool executor)
    {
        int size = batchIterable.size();
        if (size < minForkSize || FJIterate.executedInsideOfForEach())
        {
            PT procedure = procedureFactory.create();
            batchIterable.forEach(procedure);
            if (combiner.useCombineOne())
            {
                combiner.combineOne(procedure);
            }
            else
            {
                combiner.combineAll(Lists.immutable.of(procedure));
            }
        }
        else
        {
            int newTaskCount = Math.min(size, Math.min(taskCount, batchIterable.getBatchCount((int) Math.ceil((double) size / (double) taskCount))));
            new FJBatchIterableProcedureRunner<>(combiner, newTaskCount).executeAndCombine(executor, procedureFactory, batchIterable);
        }
    }

    // TODO find a better way to guarantee nested parallelism will not result in deadlocks with ForkJoin
    static boolean executedInsideOfForEach()
    {
        return Thread.currentThread().getName().startsWith("ForkJoinPool");
    }

    /**
     * Filters elements from the collection in parallel, returning those that satisfy the predicate.
     * <p>
     * This method processes the collection in parallel batches, testing each element against the predicate.
     * Elements that satisfy the predicate are collected into a result collection of the same type as the
     * input (List or Set). The order is preserved for ordered collections.
     * </p>
     * <p>
     * This variant maintains ordering by default. For potentially better performance when order doesn't
     * matter, use {@link #select(Iterable, Predicate, boolean)} with allowReorderedResult=true.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * // Example 1: Filter a large list of products by price
     * MutableList<Product> products = getProducts(); // 50,000+ products
     * Collection<Product> expensiveProducts = FJIterate.select(
     *     products,
     *     product -> product.getPrice() > 100.0
     * );
     *
     * // Example 2: Find active users in a large dataset
     * MutableList<User> users = getAllUsers();
     * Collection<User> activeUsers = FJIterate.select(
     *     users,
     *     user -> user.isActive() && user.getLastLoginDate().isAfter(cutoffDate)
     * );
     *
     * // Example 3: Filter with complex predicate
     * Collection<Order> largeOrders = FJIterate.select(
     *     orders,
     *     order -> order.getTotalAmount() > 1000 && order.getItemCount() > 5
     * );
     * }</pre>
     *
     * @param <T> the type of elements in the iterable
     * @param iterable the collection to filter
     * @param predicate the condition elements must satisfy to be selected
     * @return the selected elements in a collection of the same type as the input
     * @see Iterate#select(Iterable, Predicate)
     * @see #select(Iterable, Predicate, boolean)
     */
    public static <T> Collection<T> select(
            Iterable<T> iterable,
            Predicate<? super T> predicate)
    {
        return FJIterate.select(iterable, predicate, false);
    }

    /**
     * Same effect as {@link Iterate#select(Iterable, Predicate)}, but executed in parallel batches,
     * and with a potentially reordered result.
     *
     * @param allowReorderedResult If the result can be in a different order.
     *                             Allowing reordering may yield faster execution.
     * @return The selected elements. The Collection will be of the same type (List or Set) as the input.
     */
    public static <T> Collection<T> select(
            Iterable<T> iterable,
            Predicate<? super T> predicate,
            boolean allowReorderedResult)
    {
        return FJIterate.select(iterable, predicate, null, allowReorderedResult);
    }

    /**
     * Same effect as {@link Iterate#select(Iterable, Predicate)}, but executed in parallel batches,
     * and writing output into the specified collection.
     *
     * @param target               Where to write the output.
     * @param allowReorderedResult If the result can be in a different order.
     *                             Allowing reordering may yield faster execution.
     * @return The 'target' collection, with the selected elements added.
     */
    public static <T, R extends Collection<T>> R select(
            Iterable<T> iterable,
            Predicate<? super T> predicate,
            R target,
            boolean allowReorderedResult)
    {
        return FJIterate.select(iterable, predicate, target, FJIterate.DEFAULT_MIN_FORK_SIZE, FJIterate.FORK_JOIN_POOL, allowReorderedResult);
    }

    /**
     * Same effect as {@link Iterate#select(Iterable, Predicate)}, but executed in parallel batches,
     * and writing output into the specified collection.
     *
     * @param target               Where to write the output.
     * @param allowReorderedResult If the result can be in a different order.
     *                             Allowing reordering may yield faster execution.
     * @return The 'target' collection, with the selected elements added.
     */
    public static <T, R extends Collection<T>> R select(
            Iterable<T> iterable,
            Predicate<? super T> predicate,
            R target,
            int batchSize,
            ForkJoinPool executor,
            boolean allowReorderedResult)
    {
        SelectProcedureCombiner<T> combiner = new SelectProcedureCombiner<>(iterable, target, 10, allowReorderedResult);
        SelectProcedureFactory<T> procedureFactory = new SelectProcedureFactory<>(predicate, batchSize);
        int taskCount = FJIterate.calculateTaskCount(iterable, batchSize);
        FJIterate.forEach(iterable, procedureFactory, combiner, batchSize, taskCount, executor);
        return (R) combiner.getResult();
    }

    private static <T> int calculateTaskCount(Iterable<T> iterable, int batchSize)
    {
        if (iterable instanceof BatchIterable<?>)
        {
            return FJIterate.calculateTaskCount((BatchIterable<?>) iterable, batchSize);
        }
        return FJIterate.calculateTaskCount(Iterate.sizeOf(iterable), batchSize);
    }

    private static <T> int calculateTaskCount(BatchIterable<T> batchIterable, int batchSize)
    {
        return Math.max(2, batchIterable.getBatchCount(batchSize));
    }

    private static int calculateTaskCount(int size, int batchSize)
    {
        return Math.max(2, size / batchSize);
    }

    /**
     * Filters elements from the collection in parallel, returning those that do NOT satisfy the predicate.
     * <p>
     * This is the inverse of {@link #select(Iterable, Predicate)}. Elements that fail the predicate test
     * are collected into a result collection of the same type as the input. The order is preserved for
     * ordered collections.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * // Example 1: Remove invalid entries from a large dataset
     * MutableList<Record> records = getRecords(); // 100,000+ records
     * Collection<Record> validRecords = FJIterate.reject(
     *     records,
     *     record -> record.hasErrors() || record.isExpired()
     * );
     *
     * // Example 2: Filter out inactive accounts
     * Collection<Account> activeAccounts = FJIterate.reject(
     *     allAccounts,
     *     account -> account.getStatus() == Status.INACTIVE
     * );
     * }</pre>
     *
     * @param <T> the type of elements in the iterable
     * @param iterable the collection to filter
     * @param predicate the condition elements must NOT satisfy to be included
     * @return the rejected elements in a collection of the same type as the input
     * @see Iterate#reject(Iterable, Predicate)
     * @see #reject(Iterable, Predicate, boolean)
     */
    public static <T> Collection<T> reject(
            Iterable<T> iterable,
            Predicate<? super T> predicate)
    {
        return FJIterate.reject(iterable, predicate, false);
    }

    /**
     * Same effect as {@link Iterate#reject(Iterable, Predicate)}, but executed in parallel batches,
     * and with a potentially reordered result.
     *
     * @param allowReorderedResult If the result can be in a different order.
     *                             Allowing reordering may yield faster execution.
     * @return The rejected elements. The Collection will be of the same type (List or Set) as the input.
     */
    public static <T> Collection<T> reject(
            Iterable<T> iterable,
            Predicate<? super T> predicate,
            boolean allowReorderedResult)
    {
        return FJIterate.reject(iterable, predicate, null, allowReorderedResult);
    }

    /**
     * Same effect as {@link Iterate#reject(Iterable, Predicate)}, but executed in parallel batches,
     * and writing output into the specified collection.
     *
     * @param target               Where to write the output.
     * @param allowReorderedResult If the result can be in a different order.
     *                             Allowing reordering may yield faster execution.
     * @return The 'target' collection, with the rejected elements added.
     */
    public static <T, R extends Collection<T>> R reject(
            Iterable<T> iterable,
            Predicate<? super T> predicate,
            R target,
            boolean allowReorderedResult)
    {
        return FJIterate.reject(iterable, predicate, target, FJIterate.DEFAULT_MIN_FORK_SIZE, FJIterate.FORK_JOIN_POOL, allowReorderedResult);
    }

    public static <T, R extends Collection<T>> R reject(
            Iterable<T> iterable,
            Predicate<? super T> predicate,
            R target,
            int batchSize,
            ForkJoinPool executor,
            boolean allowReorderedResult)
    {
        RejectProcedureCombiner<T> combiner = new RejectProcedureCombiner<>(iterable, target, 10, allowReorderedResult);
        RejectProcedureFactory<T> procedureFactory = new RejectProcedureFactory<>(predicate, batchSize);
        int taskCount = FJIterate.calculateTaskCount(iterable, batchSize);
        FJIterate.forEach(iterable, procedureFactory, combiner, batchSize, taskCount, executor);
        return (R) combiner.getResult();
    }

    /**
     * Counts the number of elements in the collection that satisfy the predicate, processing in parallel.
     * <p>
     * This method evaluates the predicate for each element in parallel batches and returns the total
     * count of elements that satisfy the condition. The counting is performed concurrently across
     * multiple threads and the results are combined efficiently.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * // Example 1: Count premium customers in a large dataset
     * MutableList<Customer> customers = getAllCustomers(); // 500,000+ customers
     * int premiumCount = FJIterate.count(
     *     customers,
     *     customer -> customer.getMembershipLevel() == Level.PREMIUM
     * );
     *
     * // Example 2: Count orders above threshold
     * int largeOrderCount = FJIterate.count(
     *     orders,
     *     order -> order.getTotalAmount() > 1000.0
     * );
     *
     * // Example 3: Count valid entries with complex condition
     * int validCount = FJIterate.count(
     *     records,
     *     record -> record.isValid() && record.getTimestamp().isAfter(cutoff)
     * );
     * }</pre>
     *
     * @param <T> the type of elements in the iterable
     * @param iterable the collection to count elements from
     * @param predicate the condition elements must satisfy to be counted
     * @return the number of elements that satisfy the predicate
     * @see Iterate#count(Iterable, Predicate)
     */
    public static <T> int count(Iterable<T> iterable, Predicate<? super T> predicate)
    {
        return count(iterable, predicate, FJIterate.DEFAULT_MIN_FORK_SIZE, FJIterate.FORK_JOIN_POOL);
    }

    /**
     * Same effect as {@link Iterate#count(Iterable, Predicate)}, but executed in parallel batches.
     *
     * @return The number of elements which satisfy the Predicate.
     */
    public static <T> int count(Iterable<T> iterable, Predicate<? super T> predicate, int batchSize, ForkJoinPool executor)
    {
        CountCombiner<T> combiner = new CountCombiner<>();
        CountProcedureFactory<T> procedureFactory = new CountProcedureFactory<>(predicate);
        FJIterate.forEach(iterable, procedureFactory, combiner, batchSize, executor);
        return combiner.getCount();
    }

    /**
     * Transforms each element in the collection using the function, processing in parallel.
     * <p>
     * This method applies the transformation function to each element in parallel batches and collects
     * the results into a new collection of the same type as the input (List or Set). The order is
     * preserved for ordered collections.
     * </p>
     * <p>
     * This is the parallel equivalent of {@link Iterate#collect(Iterable, Function)} and is highly
     * effective for CPU-intensive transformations on large datasets.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * // Example 1: Extract names from a large list of people
     * MutableList<Person> people = getPeople(); // 100,000+ people
     * Collection<String> names = FJIterate.collect(
     *     people,
     *     Person::getName
     * );
     *
     * // Example 2: Transform objects with computation
     * Collection<AnalysisResult> results = FJIterate.collect(
     *     dataPoints,
     *     point -> performComplexAnalysis(point)
     * );
     *
     * // Example 3: Extract and transform nested properties
     * Collection<BigDecimal> totalSales = FJIterate.collect(
     *     customers,
     *     customer -> customer.getOrders()
     *         .collect(Order::getAmount)
     *         .reduce(BigDecimal.ZERO, BigDecimal::add)
     * );
     * }</pre>
     *
     * @param <T> the type of elements in the input iterable
     * @param <V> the type of elements in the result collection
     * @param iterable the collection to transform
     * @param function the transformation function to apply to each element
     * @return a collection of transformed elements, same type as input
     * @see Iterate#collect(Iterable, Function)
     * @see #collect(Iterable, Function, boolean)
     */
    public static <T, V> Collection<V> collect(
            Iterable<T> iterable,
            Function<? super T, V> function)
    {
        return FJIterate.collect(iterable, function, false);
    }

    /**
     * Same effect as {@link Iterate#collect(Iterable, Function)}, but executed in parallel batches,
     * and with potentially reordered result.
     *
     * @param allowReorderedResult If the result can be in a different order.
     *                             Allowing reordering may yield faster execution.
     * @return The collected elements. The Collection will be of the same type
     * (List or Set) as the input.
     */
    public static <T, V> Collection<V> collect(
            Iterable<T> iterable,
            Function<? super T, V> function,
            boolean allowReorderedResult)
    {
        return FJIterate.collect(iterable, function, null, allowReorderedResult);
    }

    /**
     * Same effect as {@link Iterate#collect(Iterable, Function)}, but executed in parallel batches,
     * and writing output into the specified collection.
     *
     * @param target               Where to write the output.
     * @param allowReorderedResult If the result can be in a different order.
     *                             Allowing reordering may yield faster execution.
     * @return The 'target' collection, with the collected elements added.
     */
    public static <T, V, R extends Collection<V>> R collect(
            Iterable<T> iterable,
            Function<? super T, V> function,
            R target,
            boolean allowReorderedResult)
    {
        return FJIterate.collect(
                iterable,
                function,
                target,
                FJIterate.DEFAULT_MIN_FORK_SIZE,
                FJIterate.FORK_JOIN_POOL,
                allowReorderedResult);
    }

    public static <T, V, R extends Collection<V>> R collect(
            Iterable<T> iterable,
            Function<? super T, V> function,
            R target,
            int batchSize,
            ForkJoinPool executor,
            boolean allowReorderedResult)
    {
        int size = Iterate.sizeOf(iterable);
        FastListCollectProcedureCombiner<T, V> combiner = new FastListCollectProcedureCombiner<>(iterable, target, size, allowReorderedResult);
        int taskCount = FJIterate.calculateTaskCount(size, batchSize);
        FastListCollectProcedureFactory<T, V> procedureFactory = new FastListCollectProcedureFactory<>(function, size / taskCount);
        FJIterate.forEach(
                iterable,
                procedureFactory,
                combiner,
                batchSize,
                taskCount,
                executor);
        return (R) combiner.getResult();
    }

    public static <T, V> Collection<V> flatCollect(
            Iterable<T> iterable,
            Function<? super T, ? extends Iterable<V>> function)
    {
        return FJIterate.flatCollect(iterable, function, false);
    }

    public static <T, V> Collection<V> flatCollect(
            Iterable<T> iterable,
            Function<? super T, ? extends Iterable<V>> function,
            boolean allowReorderedResult)
    {
        return FJIterate.flatCollect(iterable, function, null, allowReorderedResult);
    }

    public static <T, V, R extends Collection<V>> R flatCollect(
            Iterable<T> iterable,
            Function<? super T, ? extends Iterable<V>> function,
            R target,
            boolean allowReorderedResult)
    {
        return FJIterate.flatCollect(
                iterable,
                function,
                target,
                FJIterate.DEFAULT_MIN_FORK_SIZE,
                FJIterate.FORK_JOIN_POOL,
                allowReorderedResult);
    }

    public static <T, V, R extends Collection<V>> R flatCollect(
            Iterable<T> iterable,
            Function<? super T, ? extends Iterable<V>> function,
            R target,
            int batchSize,
            ForkJoinPool executor,
            boolean allowReorderedResult)
    {
        int size = Iterate.sizeOf(iterable);
        int taskSize = size / FJIterate.DEFAULT_PARALLEL_TASK_COUNT;
        FlatCollectProcedureCombiner<T, V> combiner =
                new FlatCollectProcedureCombiner<>(iterable, target, size, allowReorderedResult);
        FlatCollectProcedureFactory<T, V> procedureFactory = new FlatCollectProcedureFactory<>(function, taskSize);
        int taskCount = FJIterate.calculateTaskCount(size, batchSize);
        FJIterate.forEach(iterable, procedureFactory, combiner, batchSize, taskCount, executor);
        return (R) combiner.getResult();
    }

    /**
     * Same effect as {@link Iterate#collectIf(Iterable, Predicate, Function)},
     * but executed in parallel batches.
     *
     * @return The collected elements. The Collection will be of the same type as the input (List or Set)
     * and will be in the same order as the input (if it is an ordered collection).
     * @see FJIterate#collectIf(Iterable, Predicate, Function, boolean)
     */
    public static <T, V> Collection<V> collectIf(
            Iterable<T> iterable,
            Predicate<? super T> predicate,
            Function<? super T, V> function)
    {
        return FJIterate.collectIf(iterable, predicate, function, false);
    }

    /**
     * Same effect as {@link Iterate#collectIf(Iterable, Predicate, Function)},
     * but executed in parallel batches, and with potentially reordered results.
     *
     * @param allowReorderedResult If the result can be in a different order.
     *                             Allowing reordering may yield faster execution.
     * @return The collected elements. The Collection will be of the same type
     * as the input (List or Set)
     */
    public static <T, V> Collection<V> collectIf(
            Iterable<T> iterable,
            Predicate<? super T> predicate,
            Function<? super T, V> function,
            boolean allowReorderedResult)
    {
        return FJIterate.collectIf(iterable, predicate, function, null, allowReorderedResult);
    }

    /**
     * Same effect as {@link Iterate#collectIf(Iterable, Predicate, Function)},
     * but executed in parallel batches, and writing output into the specified collection.
     *
     * @param target               Where to write the output.
     * @param allowReorderedResult If the result can be in a different order.
     *                             Allowing reordering may yield faster execution.
     * @return The 'target' collection, with the collected elements added.
     */
    public static <T, V, R extends Collection<V>> R collectIf(
            Iterable<T> iterable,
            Predicate<? super T> predicate,
            Function<? super T, V> function,
            R target,
            boolean allowReorderedResult)
    {
        return FJIterate.collectIf(
                iterable,
                predicate,
                function,
                target,
                FJIterate.DEFAULT_MIN_FORK_SIZE,
                FJIterate.FORK_JOIN_POOL,
                allowReorderedResult);
    }

    public static <T, V, R extends Collection<V>> R collectIf(
            Iterable<T> iterable,
            Predicate<? super T> predicate,
            Function<? super T, V> function,
            R target,
            int batchSize,
            ForkJoinPool executor,
            boolean allowReorderedResult)
    {
        CollectIfProcedureCombiner<T, V> combiner = new CollectIfProcedureCombiner<>(iterable, target, 10, allowReorderedResult);
        CollectIfProcedureFactory<T, V> procedureFactory = new CollectIfProcedureFactory<>(function, predicate, batchSize);
        FJIterate.forEach(
                iterable,
                procedureFactory,
                combiner,
                batchSize,
                FJIterate.calculateTaskCount(iterable, batchSize),
                executor);
        return (R) combiner.getResult();
    }

    /**
     * Aggregates elements by key in parallel, applying a non-mutating aggregation function.
     * <p>
     * This method groups elements by the result of the groupBy function and aggregates values for each
     * group using the provided aggregation function. The aggregator is non-mutating, meaning it returns
     * a new value rather than modifying the existing one. A {@link ConcurrentHashMap} is used to safely
     * handle concurrent updates during parallel processing.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * // Example 1: Sum order totals by customer ID
     * MutableList<Order> orders = getOrders(); // 1,000,000+ orders
     * MutableMap<String, BigDecimal> salesByCustomer = FJIterate.aggregateBy(
     *     orders,
     *     Order::getCustomerId,
     *     () -> BigDecimal.ZERO,
     *     (sum, order) -> sum.add(order.getAmount())
     * );
     *
     * // Example 2: Count items by category
     * MutableMap<String, Integer> countsByCategory = FJIterate.aggregateBy(
     *     products,
     *     Product::getCategory,
     *     () -> 0,
     *     (count, product) -> count + 1
     * );
     *
     * // Example 3: Collect maximum values by group
     * MutableMap<Department, Integer> maxSalaries = FJIterate.aggregateBy(
     *     employees,
     *     Employee::getDepartment,
     *     () -> 0,
     *     (max, emp) -> Math.max(max, emp.getSalary())
     * );
     * }</pre>
     *
     * @param <T> the type of elements in the input iterable
     * @param <K> the type of keys for grouping
     * @param <V> the type of aggregated values
     * @param iterable the collection to aggregate
     * @param groupBy the function to determine the grouping key for each element
     * @param zeroValueFactory factory to create the initial value for each key
     * @param nonMutatingAggregator function that combines the current aggregated value with a new element
     * @return a map with aggregated values for each key
     */
    public static <T, K, V> MutableMap<K, V> aggregateBy(
            Iterable<T> iterable,
            Function<? super T, ? extends K> groupBy,
            Function0<? extends V> zeroValueFactory,
            Function2<? super V, ? super T, ? extends V> nonMutatingAggregator)
    {
        return FJIterate.aggregateBy(
                iterable,
                groupBy,
                zeroValueFactory,
                nonMutatingAggregator,
                FJIterate.DEFAULT_MIN_FORK_SIZE);
    }

    public static <T, K, V, R extends MutableMapIterable<K, V>> R aggregateBy(
            Iterable<T> iterable,
            Function<? super T, ? extends K> groupBy,
            Function0<? extends V> zeroValueFactory,
            Function2<? super V, ? super T, ? extends V> nonMutatingAggregator,
            R mutableMap)
    {
        return FJIterate.aggregateBy(
                iterable,
                groupBy,
                zeroValueFactory,
                nonMutatingAggregator,
                mutableMap,
                FJIterate.DEFAULT_MIN_FORK_SIZE);
    }

    public static <T, K, V> MutableMap<K, V> aggregateBy(
            Iterable<T> iterable,
            Function<? super T, ? extends K> groupBy,
            Function0<? extends V> zeroValueFactory,
            Function2<? super V, ? super T, ? extends V> nonMutatingAggregator,
            int batchSize)
    {
        return FJIterate.aggregateBy(
                iterable,
                groupBy,
                zeroValueFactory,
                nonMutatingAggregator,
                batchSize,
                FJIterate.FORK_JOIN_POOL);
    }

    public static <T, K, V, R extends MutableMapIterable<K, V>> R aggregateBy(
            Iterable<T> iterable,
            Function<? super T, ? extends K> groupBy,
            Function0<? extends V> zeroValueFactory,
            Function2<? super V, ? super T, ? extends V> nonMutatingAggregator,
            R mutableMap,
            int batchSize)
    {
        return FJIterate.aggregateBy(
                iterable,
                groupBy,
                zeroValueFactory,
                nonMutatingAggregator,
                mutableMap,
                batchSize,
                FJIterate.FORK_JOIN_POOL);
    }

    public static <T, K, V> MutableMap<K, V> aggregateBy(
            Iterable<T> iterable,
            Function<? super T, ? extends K> groupBy,
            Function0<? extends V> zeroValueFactory,
            Function2<? super V, ? super T, ? extends V> nonMutatingAggregator,
            int batchSize,
            ForkJoinPool executor)
    {
        return FJIterate.aggregateBy(
                iterable,
                groupBy,
                zeroValueFactory,
                nonMutatingAggregator,
                ConcurrentHashMap.newMap(),
                batchSize,
                executor);
    }

    public static <T, K, V, R extends MutableMapIterable<K, V>> R aggregateBy(
            Iterable<T> iterable,
            Function<? super T, ? extends K> groupBy,
            Function0<? extends V> zeroValueFactory,
            Function2<? super V, ? super T, ? extends V> nonMutatingAggregator,
            R mutableMap,
            int batchSize,
            ForkJoinPool executor)
    {
        NonMutatingAggregationProcedure<T, K, V> nonMutatingAggregationProcedure =
                new NonMutatingAggregationProcedure<>(mutableMap, groupBy, zeroValueFactory, nonMutatingAggregator);
        FJIterate.forEach(
                iterable,
                new PassThruProcedureFactory<>(nonMutatingAggregationProcedure),
                Combiners.<Procedure<T>>passThru(),
                batchSize,
                executor);
        return mutableMap;
    }

    public static <T, K, V> MutableMap<K, V> aggregateInPlaceBy(
            Iterable<T> iterable,
            Function<? super T, ? extends K> groupBy,
            Function0<? extends V> zeroValueFactory,
            Procedure2<? super V, ? super T> mutatingAggregator)
    {
        return FJIterate.aggregateInPlaceBy(
                iterable,
                groupBy,
                zeroValueFactory,
                mutatingAggregator,
                FJIterate.DEFAULT_MIN_FORK_SIZE);
    }

    public static <T, K, V, R extends MutableMapIterable<K, V>> R aggregateInPlaceBy(
            Iterable<T> iterable,
            Function<? super T, ? extends K> groupBy,
            Function0<? extends V> zeroValueFactory,
            Procedure2<? super V, ? super T> mutatingAggregator,
            R mutableMap)
    {
        return FJIterate.aggregateInPlaceBy(
                iterable,
                groupBy,
                zeroValueFactory,
                mutatingAggregator,
                mutableMap,
                FJIterate.DEFAULT_MIN_FORK_SIZE);
    }

    public static <T, K, V> MutableMap<K, V> aggregateInPlaceBy(
            Iterable<T> iterable,
            Function<? super T, ? extends K> groupBy,
            Function0<? extends V> zeroValueFactory,
            Procedure2<? super V, ? super T> mutatingAggregator,
            int batchSize)
    {
        return FJIterate.aggregateInPlaceBy(
                iterable,
                groupBy,
                zeroValueFactory,
                mutatingAggregator,
                batchSize,
                FJIterate.FORK_JOIN_POOL);
    }

    public static <T, K, V, R extends MutableMapIterable<K, V>> R aggregateInPlaceBy(
            Iterable<T> iterable,
            Function<? super T, ? extends K> groupBy,
            Function0<? extends V> zeroValueFactory,
            Procedure2<? super V, ? super T> mutatingAggregator,
            R mutableMap,
            int batchSize)
    {
        return FJIterate.aggregateInPlaceBy(
                iterable,
                groupBy,
                zeroValueFactory,
                mutatingAggregator,
                mutableMap,
                batchSize,
                FJIterate.FORK_JOIN_POOL);
    }

    public static <T, K, V> MutableMap<K, V> aggregateInPlaceBy(
            Iterable<T> iterable,
            Function<? super T, ? extends K> groupBy,
            Function0<? extends V> zeroValueFactory,
            Procedure2<? super V, ? super T> mutatingAggregator,
            int batchSize,
            ForkJoinPool executor)
    {
        MutableMap<K, V> map = ConcurrentHashMap.newMap();
        MutatingAggregationProcedure<T, K, V> mutatingAggregationProcedure =
                new MutatingAggregationProcedure<>(map, groupBy, zeroValueFactory, mutatingAggregator);
        FJIterate.forEach(
                iterable,
                new PassThruProcedureFactory<>(mutatingAggregationProcedure),
                Combiners.<Procedure<T>>passThru(),
                batchSize,
                executor);
        return map;
    }

    public static <T, K, V, R extends MutableMapIterable<K, V>> R aggregateInPlaceBy(
            Iterable<T> iterable,
            Function<? super T, ? extends K> groupBy,
            Function0<? extends V> zeroValueFactory,
            Procedure2<? super V, ? super T> mutatingAggregator,
            R mutableMap,
            int batchSize,
            ForkJoinPool executor)
    {
        MutatingAggregationProcedure<T, K, V> mutatingAggregationProcedure =
                new MutatingAggregationProcedure<>(mutableMap, groupBy, zeroValueFactory, mutatingAggregator);
        FJIterate.forEach(
                iterable,
                new PassThruProcedureFactory<>(mutatingAggregationProcedure),
                Combiners.<Procedure<T>>passThru(),
                batchSize,
                executor);
        return mutableMap;
    }

    /**
     * Groups elements from the collection by the result of applying the function, processing in parallel.
     * <p>
     * This method applies the grouping function to each element in parallel and collects them into a
     * {@link SynchronizedPutFastListMultimap}. Elements with the same key are grouped together in lists.
     * The multimap is thread-safe for concurrent writes during parallel processing.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * // Example 1: Group customers by country
     * MutableList<Customer> customers = getCustomers(); // 200,000+ customers
     * MutableMultimap<String, Customer> byCountry = FJIterate.groupBy(
     *     customers,
     *     Customer::getCountry
     * );
     *
     * // Example 2: Group orders by status
     * MutableMultimap<OrderStatus, Order> ordersByStatus = FJIterate.groupBy(
     *     orders,
     *     Order::getStatus
     * );
     *
     * // Example 3: Group products by price range
     * MutableMultimap<String, Product> byPriceRange = FJIterate.groupBy(
     *     products,
     *     product -> {
     *         double price = product.getPrice();
     *         if (price < 50) return "Budget";
     *         if (price < 200) return "Mid-Range";
     *         return "Premium";
     *     }
     * );
     * }</pre>
     *
     * @param <K> the type of keys in the resulting multimap
     * @param <V> the type of values in the input and resulting multimap
     * @param iterable the collection to group
     * @param function the function to determine the grouping key for each element
     * @return a multimap with elements grouped by their keys
     * @see Iterate#groupBy(Iterable, Function)
     */
    public static <K, V> MutableMultimap<K, V> groupBy(
            Iterable<V> iterable,
            Function<? super V, ? extends K> function)
    {
        return FJIterate.groupBy(iterable, function, FJIterate.DEFAULT_MIN_FORK_SIZE, FJIterate.FORK_JOIN_POOL);
    }

    /**
     * Same effect as {@link Iterate#groupBy(Iterable, Function)},
     * but executed in parallel batches, and writing output into a SynchronizedPutFastListMultimap.
     */
    public static <K, V, R extends MutableMultimap<K, V>> MutableMultimap<K, V> groupBy(
            Iterable<V> iterable,
            Function<? super V, ? extends K> function,
            R concurrentMultimap)
    {
        return FJIterate.groupBy(iterable, function, concurrentMultimap, FJIterate.DEFAULT_MIN_FORK_SIZE);
    }

    /**
     * Same effect as {@link Iterate#groupBy(Iterable, Function)},
     * but executed in parallel batches, and writing output into a SynchronizedPutFastListMultimap.
     */
    public static <K, V, R extends MutableMultimap<K, V>> MutableMultimap<K, V> groupBy(
            Iterable<V> iterable,
            Function<? super V, ? extends K> function,
            R concurrentMultimap,
            int batchSize)
    {
        return FJIterate.groupBy(iterable, function, concurrentMultimap, batchSize, FJIterate.FORK_JOIN_POOL);
    }

    /**
     * Same effect as {@link Iterate#groupBy(Iterable, Function)},
     * but executed in parallel batches, and writing output into a SynchronizedPutFastListMultimap.
     */
    public static <K, V> MutableMultimap<K, V> groupBy(
            Iterable<V> iterable,
            Function<? super V, ? extends K> function,
            int batchSize)
    {
        return FJIterate.groupBy(iterable, function, batchSize, FJIterate.FORK_JOIN_POOL);
    }

    /**
     * Same effect as {@link Iterate#groupBy(Iterable, Function)},
     * but executed in parallel batches, and writing output into a SynchronizedPutFastListMultimap.
     */
    public static <K, V> MutableMultimap<K, V> groupBy(
            Iterable<V> iterable,
            Function<? super V, ? extends K> function,
            int batchSize,
            ForkJoinPool executor)
    {
        return FJIterate.groupBy(iterable, function, SynchronizedPutFastListMultimap.newMultimap(), batchSize, executor);
    }

    /**
     * Same effect as {@link Iterate#groupBy(Iterable, Function)},
     * but executed in parallel batches, and writing output into a SynchronizedPutFastListMultimap.
     */
    public static <K, V, R extends MutableMultimap<K, V>> MutableMultimap<K, V> groupBy(
            Iterable<V> iterable,
            Function<? super V, ? extends K> function,
            R concurrentMultimap,
            int batchSize,
            ForkJoinPool executor)
    {
        FJIterate.forEach(
                iterable,
                new PassThruProcedureFactory<>(new MultimapPutProcedure<>(concurrentMultimap, function)),
                Combiners.<Procedure<V>>passThru(),
                batchSize,
                executor);
        return concurrentMultimap;
    }
}
