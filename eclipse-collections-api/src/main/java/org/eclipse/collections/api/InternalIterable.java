/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api;

import java.util.function.Consumer;

import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.api.ordered.OrderedIterable;

/**
 * InternalIterable is the base interface for all Eclipse Collections. It provides the fundamental iteration
 * capabilities that all Eclipse Collections support through internal iteration patterns.
 * <p>
 * Unlike external iteration (where the caller controls the iteration using {@link java.util.Iterator}),
 * internal iteration (using methods like {@link #forEach(Procedure)}) allows the collection to control
 * how elements are traversed. This enables optimizations and simplifies concurrent programming.
 * <p>
 * All Eclipse Collections are internally iterable, and this interface provides the base set of internal
 * iteration methods that every Eclipse collection should implement.
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * MutableList<String> names = Lists.mutable.with("Alice", "Bob", "Charlie");
 *
 * // Internal iteration with forEach
 * names.forEach(System.out::println);
 *
 * // Iteration with index
 * names.forEachWithIndex((name, index) ->
 *     System.out.println(index + ": " + name));
 *
 * // Iteration with parameter
 * names.forEachWith((name, prefix) ->
 *     System.out.println(prefix + name), "Name: ");
 * }</pre>
 *
 * @param <T> the type of elements in the iterable
 */
public interface InternalIterable<T>
        extends Iterable<T>
{
    /**
     * Executes the specified procedure for each element in the iterable.
     * This is an internal iteration method where the collection controls the iteration.
     * <p>
     * <b>Note:</b> This method started to conflict with {@link Iterable#forEach(java.util.function.Consumer)}
     * since Java 8. It is recommended to use {@link RichIterable#each(Procedure)} instead to avoid
     * the need for casting to Procedure when using lambda expressions.
     * <p>
     * Example using a Java 8 lambda (requires casting):
     * <pre>
     * people.forEach(Procedures.cast(person -&gt; LOGGER.info(person.getName())));
     * </pre>
     * <p>
     * Example using an anonymous inner class:
     * <pre>
     * people.forEach(new Procedure&lt;Person&gt;()
     * {
     *     public void value(Person person)
     *     {
     *         LOGGER.info(person.getName());
     *     }
     * });
     * </pre>
     *
     * @param procedure the procedure to execute for each element
     * @see RichIterable#each(Procedure)
     * @see Iterable#forEach(java.util.function.Consumer)
     */
    @SuppressWarnings("UnnecessaryFullyQualifiedName")
    void forEach(Procedure<? super T> procedure);

    @Override
    default void forEach(Consumer<? super T> consumer)
    {
        Procedure<? super T> procedure = consumer::accept;
        this.forEach(procedure);
    }

    /**
     * Iterates over the iterable passing each element and its zero-based index to the specified procedure.
     * This is useful when you need to track the position of elements during iteration.
     * <p>
     * Example using a Java 8 lambda:
     * <pre>
     * people.forEachWithIndex((Person person, int index) -&gt;
     *     LOGGER.info("Index: " + index + " person: " + person.getName()));
     * </pre>
     * <p>
     * Example using an anonymous inner class:
     * <pre>
     * people.forEachWithIndex(new ObjectIntProcedure&lt;Person&gt;()
     * {
     *     public void value(Person person, int index)
     *     {
     *         LOGGER.info("Index: " + index + " person: " + person.getName());
     *     }
     * });
     * </pre>
     *
     * @param objectIntProcedure the procedure to execute for each element and its index
     * @deprecated in 6.0. Use {@link OrderedIterable#forEachWithIndex(ObjectIntProcedure)} instead.
     */
    @Deprecated
    void forEachWithIndex(ObjectIntProcedure<? super T> objectIntProcedure);

    /**
     * Iterates over the iterable executing the procedure for each element with the specified parameter
     * provided as the second argument. This is useful when you need to pass additional context to the
     * iteration procedure without capturing variables in a closure.
     * <p>
     * Example using a Java 8 lambda:
     * <pre>
     * people.forEachWith((Person person, Person other) -&gt;
     *     {
     *         if (person.isRelatedTo(other))
     *         {
     *              LOGGER.info(person.getName());
     *         }
     *     }, fred);
     * </pre>
     * <p>
     * Example using an anonymous inner class:
     * <pre>
     * people.forEachWith(new Procedure2&lt;Person, Person&gt;()
     * {
     *     public void value(Person person, Person other)
     *     {
     *         if (person.isRelatedTo(other))
     *         {
     *              LOGGER.info(person.getName());
     *         }
     *     }
     * }, fred);
     * </pre>
     *
     * @param procedure the procedure to execute for each element and the parameter
     * @param parameter the parameter to pass to the procedure along with each element
     * @param <P> the type of the parameter
     */
    <P> void forEachWith(Procedure2<? super T, ? super P> procedure, P parameter);
}
