/*
 * Copyright (c) 2021 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.lazy;

import java.util.Iterator;
import java.util.Optional;

import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.function.primitive.DoubleObjectToDoubleFunction;
import org.eclipse.collections.api.block.function.primitive.FloatObjectToFloatFunction;
import org.eclipse.collections.api.block.function.primitive.IntObjectToIntFunction;
import org.eclipse.collections.api.block.function.primitive.LongObjectToLongFunction;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.impl.block.factory.Functions;
import org.eclipse.collections.impl.block.factory.Predicates;
import org.eclipse.collections.impl.lazy.iterator.CollectIterator;
import org.eclipse.collections.impl.utility.Iterate;

/**
 * A CollectIterable is an iterable that transforms a source iterable using a function as it iterates.
 */
public class CollectIterable<T, V>
        extends AbstractLazyIterable<V>
{
    private final Iterable<T> adapted;
    private final Function<? super T, ? extends V> function;

    public CollectIterable(Iterable<T> newAdapted, Function<? super T, ? extends V> function)
    {
        this.adapted = newAdapted;
        this.function = function;
    }

    @Override
    public void each(Procedure<? super V> procedure)
    {
        Iterate.forEach(this.adapted, Functions.bind(procedure, this.function));
    }

    @Override
    public void forEachWithIndex(ObjectIntProcedure<? super V> objectIntProcedure)
    {
        Iterate.forEachWithIndex(this.adapted, Functions.bind(objectIntProcedure, this.function));
    }

    @Override
    public <P> void forEachWith(Procedure2<? super V, ? super P> procedure, P parameter)
    {
        Iterate.forEachWith(this.adapted, Functions.bind(procedure, this.function), parameter);
    }

    @Override
    public Iterator<V> iterator()
    {
        return new CollectIterator<>(this.adapted, this.function);
    }

    @Override
    public int size()
    {
        return Iterate.sizeOf(this.adapted);
    }

    @Override
    public boolean isEmpty()
    {
        return Iterate.isEmpty(this.adapted);
    }

    @Override
    public boolean notEmpty()
    {
        return !this.isEmpty();
    }

    @Override
    public boolean anySatisfy(Predicate<? super V> predicate)
    {
        return Iterate.anySatisfy(this.adapted, Predicates.attributePredicate(this.function, predicate));
    }

    @Override
    public boolean allSatisfy(Predicate<? super V> predicate)
    {
        return Iterate.allSatisfy(this.adapted, Predicates.attributePredicate(this.function, predicate));
    }

    @Override
    public boolean noneSatisfy(Predicate<? super V> predicate)
    {
        return Iterate.noneSatisfy(this.adapted, Predicates.attributePredicate(this.function, predicate));
    }

    @Override
    public V detect(Predicate<? super V> predicate)
    {
        AttributePredicate<T, V> attributePredicate = new AttributePredicate<>(this.function, predicate);
        T resultItem = Iterate.detect(this.adapted, attributePredicate);
        return resultItem == null ? null : attributePredicate.functionResult();
    }

    @Override
    public Optional<V> detectOptional(Predicate<? super V> predicate)
    {
        AttributePredicate<T, V> attributePredicate = new AttributePredicate<>(this.function, predicate);
        Optional<T> resultItem = Iterate.detectOptional(this.adapted, attributePredicate);
        return resultItem.isPresent() ? Optional.of(attributePredicate.functionResult()) : Optional.empty();
    }

    @Override
    public <IV> IV injectInto(IV injectedValue, Function2<? super IV, ? super V, ? extends IV> f)
    {
        return Iterate.injectInto(injectedValue, this.adapted, (argument1, argument2) -> f.value(argument1, this.function.valueOf(argument2)));
    }

    @Override
    public int injectInto(int injectedValue, IntObjectToIntFunction<? super V> f)
    {
        return Iterate.injectInto(injectedValue, this.adapted, (IntObjectToIntFunction<T>) (intParameter, objectParameter) -> f.intValueOf(intParameter, this.function.valueOf(objectParameter)));
    }

    @Override
    public long injectInto(long injectedValue, LongObjectToLongFunction<? super V> f)
    {
        return Iterate.injectInto(injectedValue, this.adapted, (LongObjectToLongFunction<T>) (intParameter, objectParameter) -> f.longValueOf(intParameter, this.function.valueOf(objectParameter)));
    }

    @Override
    public double injectInto(double injectedValue, DoubleObjectToDoubleFunction<? super V> f)
    {
        return Iterate.injectInto(injectedValue, this.adapted, (DoubleObjectToDoubleFunction<T>) (intParameter, objectParameter) -> f.doubleValueOf(intParameter, this.function.valueOf(objectParameter)));
    }

    @Override
    public float injectInto(float injectedValue, FloatObjectToFloatFunction<? super V> f)
    {
        return Iterate.injectInto(injectedValue, this.adapted, (FloatObjectToFloatFunction<T>) (intParameter, objectParameter) -> f.floatValueOf(intParameter, this.function.valueOf(objectParameter)));
    }

    @Override
    public V getFirst()
    {
        if (this.isEmpty())
        {
            return null;
        }
        return this.function.valueOf(Iterate.getFirst(this.adapted));
    }

    @Override
    public V getLast()
    {
        if (this.isEmpty())
        {
            return null;
        }
        return this.function.valueOf(Iterate.getLast(this.adapted));
    }

    private static final class AttributePredicate<T, V> implements Predicate<T>
    {
        private static final long serialVersionUID = 1L;
        private final Function<? super T, ? extends V> function;
        private final Predicate<? super V> predicate;
        private transient V functionResult;

        private AttributePredicate(
                Function<? super T, ? extends V> newFunction,
                Predicate<? super V> newPredicate)
        {
            this.function = newFunction;
            this.predicate = newPredicate;
        }

        @Override
        public boolean accept(T anObject)
        {
            V functionValue = this.function.valueOf(anObject);
            boolean result = this.predicate.accept(functionValue);
            if (result)
            {
                this.functionResult = functionValue;
            }
            return result;
        }

        public V functionResult()
        {
            return this.functionResult;
        }

        @Override
        public String toString()
        {
            return "AttributePredicate("
                    + this.function
                    + ", "
                    + this.predicate
                    + ')';
        }
    }
}
