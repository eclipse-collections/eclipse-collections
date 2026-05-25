/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.block.factory;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.set.SetIterable;
import org.eclipse.collections.impl.block.predicate.checked.CheckedPredicate;
import org.eclipse.collections.impl.block.predicate.checked.ThrowingPredicate;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.eclipse.collections.impl.utility.Iterate;

/**
 * The Predicates class can be used to build common Predicates for use in methods such
 * as detect, select and reject which are found on Iterate, MapIterate, and ArrayIterate classes.
 * Predicates supports equals, not equals, less than, greater than, less than or equal to, greater than
 * or equal to, in, not in and, or, and several other Predicate type operations.
 */
public abstract class Predicates<T>
        implements Predicate<T>
{
    private static final long serialVersionUID = 1L;
    private static final Predicates<Object> ALWAYS_TRUE = new AlwaysTrue();
    private static final Predicates<Object> ALWAYS_FALSE = new AlwaysFalse();
    private static final Predicates<Object> IS_NULL = new IsNull();
    private static final Predicates<Object> NOT_NULL = new NotNull();
    private static final int SMALL_COLLECTION_THRESHOLD = 6;

    public static <T> Predicates<T> adapt(Predicate<T> predicate)
    {
        return new PredicateAdapter<>(predicate);
    }

    /**
     * Allows a Java 8 lambda or method reference to be used in a method taking a predicate without requiring an actual cast.
     * This method can be used in places where two or more method overloads could apply when used with a lambda or method
     * reference (e.g. removeIf).
     */
    public static <T> Predicate<T> cast(Predicate<T> predicate)
    {
        return predicate;
    }

    /**
     * Allows a lambda or anonymous inner class that needs to throw a checked exception to be safely wrapped as a
     * Predicate that will throw a RuntimeException, wrapping the checked exception that is the cause.
     */
    public static <T> Predicate<T> throwing(ThrowingPredicate<T> throwingPredicate)
    {
        return new ThrowingPredicateAdapter<>(throwingPredicate);
    }

    /**
     * Allows a lambda or anonymous inner class that needs to throw a checked exception to be safely wrapped as a
     * Predicate that will throw a user specified RuntimeException based on the provided function. The function
     * is passed the current element and the checked exception that was thrown as context arguments.
     */
    public static <T> Predicate<T> throwing(
            ThrowingPredicate<T> throwingPredicate,
            Function2<T, ? super Throwable, ? extends RuntimeException> rethrow)
    {
        return each ->
        {
            try
            {
                return throwingPredicate.safeAccept(each);
            }
            catch (RuntimeException e)
            {
                throw e;
            }
            catch (Throwable t)
            {
                throw rethrow.value(each, t);
            }
        };
    }

    public static <P, T> Predicate<T> bind(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return new BindPredicate2<>(predicate, parameter);
    }

    public static <T> Predicate<T> synchronizedEach(Predicate<T> predicate)
    {
        return new SynchronizedPredicate<>(predicate);
    }

    public static <T> Predicates<T> or(Iterable<? extends Predicate<? super T>> predicates)
    {
        return new OrIterablePredicate<>(predicates);
    }

    public static <T> Predicates<T> or(Predicate<? super T> predicate1, Predicate<? super T> predicate2)
    {
        return new OrPredicate<>(predicate1, predicate2);
    }

    public static <T> Predicates<T> or(Predicate<? super T>... predicates)
    {
        return new OrIterablePredicate<T>(Arrays.asList(predicates));
    }

    public static <T> Predicates<T> and(Iterable<? extends Predicate<? super T>> predicates)
    {
        return new AndIterablePredicate<>(predicates);
    }

    public static <T> Predicates<T> and(Predicate<? super T> predicate1, Predicate<? super T> predicate2)
    {
        return new AndPredicate<>(predicate1, predicate2);
    }

    public static <T> Predicates<T> and(Predicate<? super T>... predicates)
    {
        return new AndIterablePredicate<T>(Arrays.asList(predicates));
    }

    public static <T> Predicates<T> not(Predicate<T> predicate)
    {
        return new NotPredicate<>(predicate);
    }

    public Predicates<T> not()
    {
        return Predicates.not(this);
    }

    public static <T> Predicates<T> neither(Predicate<? super T> operation1, Predicate<? super T> operation2)
    {
        return new NeitherPredicate<>(operation1, operation2);
    }

    public static <T> Predicates<T> noneOf(Predicate<? super T>... operations)
    {
        return new NoneOfIterablePredicate<T>(Arrays.asList(operations));
    }

    public static <T> Predicates<T> noneOf(Iterable<? extends Predicate<? super T>> operations)
    {
        return new NoneOfIterablePredicate<>(operations);
    }

    /**
     * Tests for equality.
     */
    public static Predicates<Object> equal(Object object)
    {
        if (object == null)
        {
            return Predicates.isNull();
        }
        return new EqualPredicate(object);
    }

    /**
     * Creates a predicate which returns true if an object passed to accept method is within the range, inclusive
     * of the from and to values.
     */
    public static <T extends Comparable<? super T>> Predicates<T> betweenInclusive(T from, T to)
    {
        Predicates.failIfDifferentTypes(from, to);
        return new BetweenInclusive<>(from, to);
    }

    private static void failIfDifferentTypes(Object from, Object to)
    {
        if (!from.getClass().equals(to.getClass()))
        {
            throw new IllegalArgumentException("Trying to do a between comparison with two different types "
                    + from.getClass()
                    + ':'
                    + to.getClass());
        }
    }

    /**
     * Creates a predicate which returns true if an object passed to accept method is within the range, exclusive
     * of the from and to values.
     */
    public static <T extends Comparable<? super T>> Predicates<T> betweenExclusive(T from, T to)
    {
        Predicates.failIfDifferentTypes(from, to);
        return new BetweenExclusive<>(from, to);
    }

    /**
     * Creates a predicate which returns true if an object passed to accept method is within the range, inclusive
     * of the from and exclusive from the to value.
     */
    public static <T extends Comparable<? super T>> Predicates<T> betweenInclusiveFrom(T from, T to)
    {
        Predicates.failIfDifferentTypes(from, to);
        return new BetweenInclusiveFrom<>(from, to);
    }

    /**
     * Creates a predicate which returns true if an object passed to accept method is within the range, exclusive
     * of the from and inclusive of the to value.
     */
    public static <T extends Comparable<? super T>> Predicates<T> betweenInclusiveTo(T from, T to)
    {
        Predicates.failIfDifferentTypes(from, to);
        return new BetweenInclusiveTo<>(from, to);
    }

    /**
     * Creates a predicate which returns true if an object passed to accept method is contained in the iterable.
     */
    public static Predicates<Object> in(Iterable<?> iterable)
    {
        if (iterable instanceof SetIterable<?> setIterable)
        {
            return new InSetIterablePredicate(setIterable);
        }
        if (iterable instanceof Set<?> set)
        {
            return new InSetPredicate(set);
        }
        if (iterable instanceof Collection<?> collection && collection.size() <= SMALL_COLLECTION_THRESHOLD)
        {
            return new InCollectionPredicate(collection);
        }
        return new InSetIterablePredicate(Sets.mutable.withAll(iterable));
    }

    public static Predicates<Object> in(Object... array)
    {
        if (array.length <= SMALL_COLLECTION_THRESHOLD)
        {
            return new InCollectionPredicate(Arrays.asList(array));
        }
        return new InSetIterablePredicate(UnifiedSet.newSetWith(array));
    }

    /**
     * Creates a predicate which returns true if an attribute selected from an object passed to accept method
     * is contained in the iterable.
     */
    public static <T> Predicates<T> attributeIn(
            Function<? super T, ?> function,
            Iterable<?> iterable)
    {
        return new AttributePredicate<>(function, Predicates.in(iterable));
    }

    public static <T, V extends Comparable<? super V>> Predicates<T> attributeBetweenInclusive(
            Function<? super T, ? extends V> function,
            V from,
            V to)
    {
        return new AttributePredicate<>(function, Predicates.betweenInclusive(from, to));
    }

    public static <T, V extends Comparable<? super V>> Predicates<T> attributeBetweenExclusive(
            Function<? super T, ? extends V> function,
            V from,
            V to)
    {
        return new AttributePredicate<>(function, Predicates.betweenExclusive(from, to));
    }

    public static <T, V extends Comparable<? super V>> Predicates<T> attributeBetweenInclusiveFrom(
            Function<? super T, ? extends V> function,
            V from,
            V to)
    {
        return new AttributePredicate<>(function, Predicates.betweenInclusiveFrom(from, to));
    }

    public static <T, V extends Comparable<? super V>> Predicates<T> attributeBetweenInclusiveTo(
            Function<? super T, ? extends V> function,
            V from,
            V to)
    {
        return new AttributePredicate<>(function, Predicates.betweenInclusiveTo(from, to));
    }

    /**
     * Creates a predicate which returns true if an object passed to accept method is not contained in
     * the iterable.
     */
    public static Predicates<Object> notIn(Iterable<?> iterable)
    {
        if (iterable instanceof SetIterable<?> setIterable)
        {
            return new NotInSetIterablePredicate(setIterable);
        }
        if (iterable instanceof Set<?> set)
        {
            return new NotInSetPredicate(set);
        }
        if (iterable instanceof Collection<?> collection && collection.size() <= SMALL_COLLECTION_THRESHOLD)
        {
            return new NotInCollectionPredicate(collection);
        }
        return new NotInSetIterablePredicate(Sets.mutable.withAll(iterable));
    }

    public static Predicates<Object> notIn(Object... array)
    {
        if (array.length <= SMALL_COLLECTION_THRESHOLD)
        {
            return new NotInCollectionPredicate(Arrays.asList(array));
        }
        return new NotInSetIterablePredicate(UnifiedSet.newSetWith(array));
    }

    /**
     * Creates a predicate which returns true if an attribute selected from an object passed to accept method
     * is not contained in the iterable.
     */
    public static <T> Predicates<T> attributeNotIn(
            Function<? super T, ?> function,
            Iterable<?> iterable)
    {
        return new AttributePredicate<>(function, Predicates.notIn(iterable));
    }

    public static <T extends Comparable<? super T>> Predicates<T> lessThan(T object)
    {
        return new LessThanPredicate<>(object);
    }

    public static <T, V extends Comparable<? super V>> Predicates<T> attributeLessThan(
            Function<? super T, ? extends V> function,
            V object)
    {
        return new AttributePredicate<>(function, new LessThanPredicate<>(object));
    }

    public static <T extends Comparable<? super T>> Predicates<T> lessThanOrEqualTo(T object)
    {
        return new LessThanOrEqualPredicate<>(object);
    }

    public static <T, V extends Comparable<? super V>> Predicates<T> attributeLessThanOrEqualTo(
            Function<? super T, ? extends V> function,
            V object)
    {
        return new AttributePredicate<>(function, new LessThanOrEqualPredicate<>(object));
    }

    public static <T extends Comparable<? super T>> Predicates<T> greaterThan(T object)
    {
        return new GreaterThanPredicate<>(object);
    }

    public static <T, V extends Comparable<? super V>> Predicates<T> attributeGreaterThan(
            Function<? super T, ? extends V> function,
            V object)
    {
        return new AttributePredicate<>(function, new GreaterThanPredicate<>(object));
    }

    public static <T extends Comparable<? super T>> Predicates<T> greaterThanOrEqualTo(T object)
    {
        return new GreaterThanOrEqualPredicate<>(object);
    }

    public static <T, V extends Comparable<? super V>> Predicates<T> attributeGreaterThanOrEqualTo(
            Function<? super T, ? extends V> function,
            V object)
    {
        return new AttributePredicate<>(function, new GreaterThanOrEqualPredicate<>(object));
    }

    public static <T, V> Predicates<T> attributePredicate(
            Function<? super T, ? extends V> function,
            Predicate<? super V> predicate)
    {
        return new AttributePredicate<>(function, predicate);
    }

    public static <T> Predicates<T> attributeEqual(
            Function<? super T, ?> function,
            Object object)
    {
        return new AttributePredicate<>(function, Predicates.equal(object));
    }

    public static <T> Predicates<Iterable<T>> anySatisfy(Predicate<? super T> predicate)
    {
        return new AnySatisfy<>(predicate);
    }

    public static <T> Predicates<Iterable<T>> allSatisfy(Predicate<? super T> predicate)
    {
        return new AllSatisfy<>(predicate);
    }

    public static <T> Predicates<Iterable<T>> noneSatisfy(Predicate<? super T> predicate)
    {
        return new NoneSatisfy<>(predicate);
    }

    public static <T, V> Predicates<T> attributeAnySatisfy(
            Function<? super T, ? extends Iterable<V>> function,
            Predicate<? super V> predicate)
    {
        return Predicates.attributePredicate(function, Predicates.anySatisfy(predicate));
    }

    public static <T, V> Predicates<T> attributeAllSatisfy(
            Function<? super T, ? extends Iterable<V>> function,
            Predicate<? super V> predicate)
    {
        return Predicates.attributePredicate(function, Predicates.allSatisfy(predicate));
    }

    public static <T, V> Predicates<T> attributeNoneSatisfy(
            Function<? super T, ? extends Iterable<V>> function,
            Predicate<? super V> predicate)
    {
        return Predicates.attributePredicate(function, Predicates.noneSatisfy(predicate));
    }

    public static Predicates<Object> notEqual(Object object)
    {
        if (object == null)
        {
            return Predicates.notNull();
        }
        return new NotEqualPredicate(object);
    }

    public static <T> Predicates<T> ifTrue(Function<? super T, Boolean> function)
    {
        return new AttributeTrue<>(function);
    }

    public static <T> Predicates<T> ifFalse(Function<? super T, Boolean> function)
    {
        return new AttributeFalse<>(function);
    }

    public static <T> Predicates<T> attributeNotEqual(
            Function<? super T, ?> function,
            Object object)
    {
        return new AttributePredicate<>(function, Predicates.notEqual(object));
    }

    public static Predicates<Object> isNull()
    {
        return IS_NULL;
    }

    public static <T> Predicates<T> attributeIsNull(Function<? super T, ?> function)
    {
        return new AttributePredicate<>(function, Predicates.isNull());
    }

    public static Predicates<Object> notNull()
    {
        return NOT_NULL;
    }

    public static <T> Predicates<T> attributeNotNull(Function<? super T, ?> function)
    {
        return new AttributePredicate<>(function, Predicates.notNull());
    }

    public static Predicates<Object> sameAs(Object object)
    {
        return new IdentityPredicate(object);
    }

    public static Predicates<Object> notSameAs(Object object)
    {
        return new NotIdentityPredicate(object);
    }

    public static Predicates<Object> instanceOf(Class<?> clazz)
    {
        return new InstanceOfPredicate(clazz);
    }

    public static Predicates<Object> assignableFrom(Class<?> clazz)
    {
        return new AssignableFromPredicate(clazz);
    }

    public static Predicates<Object> notInstanceOf(Class<?> clazz)
    {
        return new NotInstanceOfPredicate(clazz);
    }

    public static Predicates<Object> alwaysTrue()
    {
        return ALWAYS_TRUE;
    }

    public static Predicates<Object> alwaysFalse()
    {
        return ALWAYS_FALSE;
    }

    public Predicates<T> and(Predicate<? super T> op)
    {
        return Predicates.and(this, op);
    }

    public Predicates<T> or(Predicate<? super T> op)
    {
        return Predicates.or(this, op);
    }

    public static Predicates<Class<?>> subClass(Class<?> aClass)
    {
        return new SubclassPredicate(aClass);
    }

    public static Predicates<Class<?>> superClass(Class<?> aClass)
    {
        return new SuperclassPredicate(aClass);
    }

    private static final class PredicateAdapter<T>
            extends Predicates<T>
    {
        private static final long serialVersionUID = 1L;
        private final Predicate<T> predicate;

        private PredicateAdapter(Predicate<T> newPredicate)
        {
            this.predicate = newPredicate;
        }

        @Override
        public boolean accept(T o)
        {
            return this.predicate.accept(o);
        }

        @Override
        public String toString()
        {
            return "Predicates.adapt(" + this.predicate + ')';
        }
    }

    protected static class AttributePredicate<T, V> extends Predicates<T>
    {
        private static final long serialVersionUID = 1L;
        protected final Function<? super T, ? extends V> function;
        protected final Predicate<? super V> predicate;

        protected AttributePredicate(
                Function<? super T, ? extends V> newFunction,
                Predicate<? super V> newPredicate)
        {
            this.function = newFunction;
            this.predicate = newPredicate;
        }

        @Override
        public boolean accept(T anObject)
        {
            return this.predicate.accept(this.function.valueOf(anObject));
        }

        @Override
        public String toString()
        {
            return "Predicates.attributePredicate("
                    + this.function
                    + ", "
                    + this.predicate
                    + ')';
        }
    }

    private static class FalseEquals implements Predicate<Boolean>
    {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean accept(Boolean anObject)
        {
            return Boolean.FALSE.equals(anObject);
        }
    }

    private static class TrueEquals implements Predicate<Boolean>
    {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean accept(Boolean anObject)
        {
            return Boolean.TRUE.equals(anObject);
        }
    }

    private static final class AttributeFalse<T> extends AttributePredicate<T, Boolean>
    {
        private static final long serialVersionUID = 1L;
        private static final FalseEquals FALSE_EQUALS = new FalseEquals();

        private AttributeFalse(Function<? super T, Boolean> newFunction)
        {
            super(newFunction, FALSE_EQUALS);
        }

        @Override
        public String toString()
        {
            return "Predicates.ifFalse(" + this.function + ')';
        }
    }

    private static final class AttributeTrue<T> extends AttributePredicate<T, Boolean>
    {
        private static final long serialVersionUID = 1L;
        private static final TrueEquals TRUE_EQUALS = new TrueEquals();

        private AttributeTrue(Function<? super T, Boolean> newFunction)
        {
            super(newFunction, TRUE_EQUALS);
        }

        @Override
        public String toString()
        {
            return "Predicates.ifTrue(" + this.function + ')';
        }
    }

    public static class AnySatisfy<T> extends Predicates<Iterable<T>>
    {
        private static final long serialVersionUID = 1L;
        private final Predicate<? super T> predicate;

        public AnySatisfy(Predicate<? super T> predicate)
        {
            this.predicate = predicate;
        }

        @Override
        public boolean accept(Iterable<T> iterable)
        {
            return Iterate.anySatisfy(iterable, this.predicate);
        }
    }

    public static class AllSatisfy<T> extends Predicates<Iterable<T>>
    {
        private static final long serialVersionUID = 1L;
        private final Predicate<? super T> predicate;

        public AllSatisfy(Predicate<? super T> predicate)
        {
            this.predicate = predicate;
        }

        @Override
        public boolean accept(Iterable<T> iterable)
        {
            return Iterate.allSatisfy(iterable, this.predicate);
        }
    }

    public static class NoneSatisfy<T> extends Predicates<Iterable<T>>
    {
        private static final long serialVersionUID = 1L;
        private final Predicate<? super T> predicate;

        public NoneSatisfy(Predicate<? super T> predicate)
        {
            this.predicate = predicate;
        }

        @Override
        public boolean accept(Iterable<T> iterable)
        {
            return Iterate.noneSatisfy(iterable, this.predicate);
        }
    }

    private abstract static class CompareToPredicate<T extends Comparable<? super T>>
            extends Predicates<T>
    {
        private static final long serialVersionUID = 1L;
        protected final T compareTo;

        private CompareToPredicate(T newCompareTo)
        {
            this.compareTo = newCompareTo;
        }
    }

    protected static class LessThanPredicate<T extends Comparable<? super T>>
            extends CompareToPredicate<T>
    {
        private static final long serialVersionUID = 1L;

        protected LessThanPredicate(T newCompareTo)
        {
            super(newCompareTo);
        }

        @Override
        public boolean accept(T o)
        {
            return o.compareTo(this.compareTo) < 0;
        }

        @Override
        public String toString()
        {
            return "Predicates.lessThan(" + this.compareTo + ')';
        }
    }

    protected abstract static class RangePredicate<T extends Comparable<? super T>>
            extends CompareToPredicate<T>
    {
        private static final long serialVersionUID = 1L;
        protected final T compareFrom;

        protected RangePredicate(T newCompareFrom, T newCompareTo)
        {
            super(newCompareTo);
            this.compareFrom = newCompareFrom;
        }
    }

    private static final class BetweenInclusive<T extends Comparable<? super T>>
            extends RangePredicate<T>
    {
        private static final long serialVersionUID = 1L;

        private BetweenInclusive(T newCompareFrom, T newCompareTo)
        {
            super(newCompareFrom, newCompareTo);
        }

        @Override
        public boolean accept(T o)
        {
            return o.compareTo(this.compareFrom) >= 0 && o.compareTo(this.compareTo) <= 0;
        }
    }

    private static final class BetweenInclusiveTo<T extends Comparable<? super T>>
            extends RangePredicate<T>
    {
        private static final long serialVersionUID = 1L;

        private BetweenInclusiveTo(T newCompareFrom, T newCompareTo)
        {
            super(newCompareFrom, newCompareTo);
        }

        @Override
        public boolean accept(T o)
        {
            return o.compareTo(this.compareFrom) > 0 && o.compareTo(this.compareTo) <= 0;
        }
    }

    private static final class BetweenInclusiveFrom<T extends Comparable<? super T>>
            extends RangePredicate<T>
    {
        private static final long serialVersionUID = 1L;

        private BetweenInclusiveFrom(T newCompareFrom, T newCompareTo)
        {
            super(newCompareFrom, newCompareTo);
        }

        @Override
        public boolean accept(T o)
        {
            return o.compareTo(this.compareFrom) >= 0 && o.compareTo(this.compareTo) < 0;
        }
    }

    private static final class BetweenExclusive<T extends Comparable<? super T>>
            extends RangePredicate<T>
    {
        private static final long serialVersionUID = 1L;

        private BetweenExclusive(T newCompareFrom, T newCompareTo)
        {
            super(newCompareFrom, newCompareTo);
        }

        @Override
        public boolean accept(T o)
        {
            return o.compareTo(this.compareFrom) > 0 && o.compareTo(this.compareTo) < 0;
        }
    }

    protected static class LessThanOrEqualPredicate<T extends Comparable<? super T>>
            extends CompareToPredicate<T>
    {
        private static final long serialVersionUID = 1L;

        protected LessThanOrEqualPredicate(T newCompareTo)
        {
            super(newCompareTo);
        }

        @Override
        public boolean accept(T o)
        {
            return o.compareTo(this.compareTo) <= 0;
        }

        @Override
        public String toString()
        {
            return "Predicates.lessThanOrEqualTo(" + this.compareTo + ')';
        }
    }

    protected static class GreaterThanPredicate<T extends Comparable<? super T>>
            extends CompareToPredicate<T>
    {
        private static final long serialVersionUID = 1L;

        protected GreaterThanPredicate(T newCompareTo)
        {
            super(newCompareTo);
        }

        @Override
        public boolean accept(T o)
        {
            return o.compareTo(this.compareTo) > 0;
        }

        @Override
        public String toString()
        {
            return "Predicates.greaterThan(" + this.compareTo + ')';
        }
    }

    protected static class GreaterThanOrEqualPredicate<T extends Comparable<? super T>>
            extends CompareToPredicate<T>
    {
        private static final long serialVersionUID = 1L;

        protected GreaterThanOrEqualPredicate(T newCompareTo)
        {
            super(newCompareTo);
        }

        @Override
        public boolean accept(T o)
        {
            return o.compareTo(this.compareTo) >= 0;
        }

        @Override
        public String toString()
        {
            return "Predicates.greaterThanOrEqualTo(" + this.compareTo + ')';
        }
    }

    private static final class AndIterablePredicate<T>
            extends AbstractIterablePredicate<T>
    {
        private static final long serialVersionUID = 1L;

        private AndIterablePredicate(Iterable<? extends Predicate<? super T>> predicates)
        {
            super(predicates);
        }

        @Override
        protected String getTypeName()
        {
            return "and";
        }

        @Override
        public boolean accept(T anObject)
        {
            Predicate<Predicate<? super T>> predicate = aPredicate -> aPredicate.accept(anObject);
            return Iterate.allSatisfy(this.predicates, predicate);
        }
    }

    private static final class OrIterablePredicate<T>
            extends AbstractIterablePredicate<T>
    {
        private static final long serialVersionUID = 1L;

        private OrIterablePredicate(Iterable<? extends Predicate<? super T>> predicates)
        {
            super(predicates);
        }

        @Override
        protected String getTypeName()
        {
            return "or";
        }

        @Override
        public boolean accept(T anObject)
        {
            Predicate<Predicate<? super T>> predicate = aPredicate -> aPredicate.accept(anObject);
            return Iterate.anySatisfy(this.predicates, predicate);
        }
    }

    private static final class NoneOfIterablePredicate<T>
            extends AbstractIterablePredicate<T>
    {
        private static final long serialVersionUID = 1L;

        private NoneOfIterablePredicate(Iterable<? extends Predicate<? super T>> predicates)
        {
            super(predicates);
        }

        @Override
        protected String getTypeName()
        {
            return "noneOf";
        }

        @Override
        public boolean accept(T anObject)
        {
            Predicate<Predicate<? super T>> predicate = aPredicate -> !aPredicate.accept(anObject);
            return Iterate.allSatisfy(this.predicates, predicate);
        }
    }

    private abstract static class AbstractIterablePredicate<T>
            extends Predicates<T>
    {
        private static final long serialVersionUID = 1L;
        protected final Iterable<? extends Predicate<? super T>> predicates;

        private AbstractIterablePredicate(Iterable<? extends Predicate<? super T>> predicates)
        {
            this.predicates = predicates;
        }

        protected abstract String getTypeName();

        @Override
        public String toString()
        {
            return "Predicates." + this.getTypeName() + '(' + this.predicates + ')';
        }
    }

    private static final class AndPredicate<T>
            extends Predicates<T>
    {
        private static final long serialVersionUID = 1L;
        private final Predicate<? super T> left;
        private final Predicate<? super T> right;

        private AndPredicate(Predicate<? super T> one, Predicate<? super T> two)
        {
            this.left = one;
            this.right = two;
        }

        @Override
        public boolean accept(T anObject)
        {
            return this.left.accept(anObject) && this.right.accept(anObject);
        }

        @Override
        public String toString()
        {
            return this.left + ".and(" + this.right + ')';
        }
    }

    private static final class NeitherPredicate<T>
            extends Predicates<T>
    {
        private static final long serialVersionUID = 1L;
        private final Predicate<? super T> left;
        private final Predicate<? super T> right;

        private NeitherPredicate(Predicate<? super T> one, Predicate<? super T> two)
        {
            this.left = one;
            this.right = two;
        }

        @Override
        public boolean accept(T anObject)
        {
            return !this.left.accept(anObject) && !this.right.accept(anObject);
        }

        @Override
        public String toString()
        {
            return "Predicates.neither(" + this.left + ", " + this.right + ')';
        }
    }

    private static final class OrPredicate<T>
            extends Predicates<T>
    {
        private static final long serialVersionUID = 1L;
        private final Predicate<? super T> left;
        private final Predicate<? super T> right;

        private OrPredicate(Predicate<? super T> one, Predicate<? super T> two)
        {
            this.left = one;
            this.right = two;
        }

        @Override
        public boolean accept(T anObject)
        {
            return this.left.accept(anObject) || this.right.accept(anObject);
        }

        @Override
        public String toString()
        {
            return this.left + ".or(" + this.right + ')';
        }
    }

    private static final class NotPredicate<T>
            extends Predicates<T>
    {
        private static final long serialVersionUID = 1L;
        private final Predicate<T> predicate;

        private NotPredicate(Predicate<T> newPredicate)
        {
            this.predicate = newPredicate;
        }

        @Override
        public boolean accept(T anObject)
        {
            return !this.predicate.accept(anObject);
        }

        @Override
        public String toString()
        {
            return "Predicates.not(" + this.predicate + ')';
        }
    }

    private static final class EqualPredicate
            extends Predicates<Object>
    {
        private static final long serialVersionUID = 1L;
        private final Object compareObject;

        private EqualPredicate(Object newCompareObject)
        {
            this.compareObject = newCompareObject;
        }

        @Override
        public boolean accept(Object anObject)
        {
            return this.compareObject.equals(anObject);
        }

        @Override
        public String toString()
        {
            return "Predicates.equal(" + this.compareObject + ')';
        }
    }

    private static final class InCollectionPredicate
            extends Predicates<Object>
    {
        private static final long serialVersionUID = 1L;
        private final Collection<?> collection;

        private InCollectionPredicate(Collection<?> collection)
        {
            this.collection = collection;
        }

        @Override
        public boolean accept(Object anObject)
        {
            return this.collection.contains(anObject);
        }

        @Override
        public String toString()
        {
            return "Predicates.in(" + this.collection + ')';
        }
    }

    private static final class NotInCollectionPredicate
            extends Predicates<Object>
    {
        private static final long serialVersionUID = 1L;
        private final Collection<?> collection;

        private NotInCollectionPredicate(Collection<?> collection)
        {
            this.collection = collection;
        }

        @Override
        public boolean accept(Object anObject)
        {
            return !this.collection.contains(anObject);
        }

        @Override
        public String toString()
        {
            return "Predicates.notIn(" + this.collection + ')';
        }
    }

    private static final class InSetIterablePredicate
            extends Predicates<Object>
    {
        private static final long serialVersionUID = 1L;
        private final SetIterable<?> setIterable;

        private InSetIterablePredicate(SetIterable<?> setIterable)
        {
            this.setIterable = setIterable;
        }

        @Override
        public boolean accept(Object anObject)
        {
            return this.setIterable.contains(anObject);
        }

        @Override
        public String toString()
        {
            return "Predicates.in(" + this.setIterable + ')';
        }
    }

    private static final class NotInSetIterablePredicate
            extends Predicates<Object>
    {
        private static final long serialVersionUID = 1L;
        private final SetIterable<?> setIterable;

        private NotInSetIterablePredicate(SetIterable<?> setIterable)
        {
            this.setIterable = setIterable;
        }

        @Override
        public boolean accept(Object anObject)
        {
            return !this.setIterable.contains(anObject);
        }

        @Override
        public String toString()
        {
            return "Predicates.notIn(" + this.setIterable + ')';
        }
    }

    private static final class InSetPredicate
            extends Predicates<Object>
    {
        private static final long serialVersionUID = 1L;
        private final Set<?> set;

        private InSetPredicate(Set<?> set)
        {
            this.set = set;
        }

        @Override
        public boolean accept(Object anObject)
        {
            return this.set.contains(anObject);
        }

        @Override
        public String toString()
        {
            return "Predicates.in(" + this.set + ')';
        }
    }

    private static final class NotInSetPredicate
            extends Predicates<Object>
    {
        private static final long serialVersionUID = 1L;
        private final Set<?> set;

        private NotInSetPredicate(Set<?> set)
        {
            this.set = set;
        }

        @Override
        public boolean accept(Object anObject)
        {
            return !this.set.contains(anObject);
        }

        @Override
        public String toString()
        {
            return "Predicates.notIn(" + this.set + ')';
        }
    }

    private static final class NotEqualPredicate
            extends Predicates<Object>
    {
        private static final long serialVersionUID = 1L;
        private final Object compareObject;

        private NotEqualPredicate(Object newCompareObject)
        {
            this.compareObject = newCompareObject;
        }

        @Override
        public boolean accept(Object anObject)
        {
            return !this.compareObject.equals(anObject);
        }

        @Override
        public String toString()
        {
            return "Predicates.notEqual(" + this.compareObject + ')';
        }
    }

    private static final class IsNull
            extends Predicates<Object>
    {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean accept(Object anObject)
        {
            return anObject == null;
        }

        @Override
        public String toString()
        {
            return "Predicates.isNull()";
        }
    }

    private static final class NotNull
            extends Predicates<Object>
    {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean accept(Object anObject)
        {
            return anObject != null;
        }

        @Override
        public String toString()
        {
            return "Predicates.notNull()";
        }
    }

    private static final class AssignableFromPredicate
            extends Predicates<Object>
    {
        private static final long serialVersionUID = 1L;
        private final Class<?> clazz;

        private AssignableFromPredicate(Class<?> newClass)
        {
            this.clazz = newClass;
        }

        @Override
        public boolean accept(Object anObject)
        {
            return this.clazz.isAssignableFrom(anObject.getClass());
        }

        @Override
        public String toString()
        {
            return "Predicates.assignableFrom(" + this.clazz.getName() + ".class)";
        }
    }

    private static final class InstanceOfPredicate
            extends Predicates<Object>
    {
        private static final long serialVersionUID = 1L;
        private final Class<?> clazz;

        private InstanceOfPredicate(Class<?> newClass)
        {
            this.clazz = newClass;
        }

        @Override
        public boolean accept(Object anObject)
        {
            return this.clazz.isInstance(anObject);
        }

        @Override
        public String toString()
        {
            return "Predicates.instanceOf(" + this.clazz.getName() + ".class)";
        }
    }

    private static final class NotInstanceOfPredicate
            extends Predicates<Object>
    {
        private static final long serialVersionUID = 1L;
        private final Class<?> clazz;

        private NotInstanceOfPredicate(Class<?> newClass)
        {
            this.clazz = newClass;
        }

        @Override
        public boolean accept(Object anObject)
        {
            return !this.clazz.isInstance(anObject);
        }

        @Override
        public String toString()
        {
            return "Predicates.notInstanceOf(" + this.clazz.getName() + ".class)";
        }
    }

    private static final class AlwaysTrue
            extends Predicates<Object>
    {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean accept(Object anObject)
        {
            return true;
        }

        @Override
        public String toString()
        {
            return "Predicates.alwaysTrue()";
        }
    }

    private static final class AlwaysFalse
            extends Predicates<Object>
    {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean accept(Object anObject)
        {
            return false;
        }

        @Override
        public String toString()
        {
            return "Predicates.alwaysFalse()";
        }
    }

    private static final class IdentityPredicate
            extends Predicates<Object>
    {
        private static final long serialVersionUID = 1L;
        private final Object twin;

        private IdentityPredicate(Object object)
        {
            this.twin = object;
        }

        @Override
        public boolean accept(Object anObject)
        {
            return this.twin == anObject;
        }

        @Override
        public String toString()
        {
            return "Predicates.sameAs(" + this.twin + ')';
        }
    }

    private static final class NotIdentityPredicate
            extends Predicates<Object>
    {
        private static final long serialVersionUID = 1L;
        private final Object twin;

        private NotIdentityPredicate(Object object)
        {
            this.twin = object;
        }

        @Override
        public boolean accept(Object anObject)
        {
            return this.twin != anObject;
        }

        @Override
        public String toString()
        {
            return "Predicates.notSameAs(" + this.twin + ')';
        }
    }

    private static final class SynchronizedPredicate<T> implements Predicate<T>
    {
        private static final long serialVersionUID = 1L;
        private final Predicate<T> predicate;

        private SynchronizedPredicate(Predicate<T> predicate)
        {
            this.predicate = predicate;
        }

        @Override
        public boolean accept(T each)
        {
            synchronized (each)
            {
                return this.predicate.accept(each);
            }
        }
    }

    private static final class SubclassPredicate extends Predicates<Class<?>>
    {
        private static final long serialVersionUID = 1L;

        private final Class<?> aClass;

        private SubclassPredicate(Class<?> aClass)
        {
            this.aClass = aClass;
        }

        @Override
        public boolean accept(Class<?> each)
        {
            return this.aClass.isAssignableFrom(each);
        }
    }

    private static final class SuperclassPredicate extends Predicates<Class<?>>
    {
        private static final long serialVersionUID = 1L;

        private final Class<?> aClass;

        private SuperclassPredicate(Class<?> aClass)
        {
            this.aClass = aClass;
        }

        @Override
        public boolean accept(Class<?> each)
        {
            return each.isAssignableFrom(this.aClass);
        }
    }

    private static final class BindPredicate2<T, P> implements Predicate<T>
    {
        private static final long serialVersionUID = 1L;

        private final Predicate2<? super T, ? super P> predicate;
        private final P parameter;

        private BindPredicate2(Predicate2<? super T, ? super P> predicate, P parameter)
        {
            this.predicate = predicate;
            this.parameter = parameter;
        }

        @Override
        public boolean accept(T each)
        {
            return this.predicate.accept(each, this.parameter);
        }

        @Override
        public String toString()
        {
            return "Predicates.bind(" + this.predicate + ", " + this.parameter + ")";
        }
    }

    private static final class ThrowingPredicateAdapter<T> extends CheckedPredicate<T>
    {
        private static final long serialVersionUID = 1L;
        private final ThrowingPredicate<T> throwingPredicate;

        private ThrowingPredicateAdapter(ThrowingPredicate<T> throwingPredicate)
        {
            this.throwingPredicate = throwingPredicate;
        }

        @Override
        public boolean safeAccept(T object) throws Exception
        {
            return this.throwingPredicate.safeAccept(object);
        }
    }
}
