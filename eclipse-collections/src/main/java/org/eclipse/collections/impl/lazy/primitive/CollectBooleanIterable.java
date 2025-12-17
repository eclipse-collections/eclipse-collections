/*
 * Copyright (c) 2022 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.lazy.primitive;

import java.util.Iterator;

import org.eclipse.collections.api.BooleanIterable;
import org.eclipse.collections.api.LazyIterable;
import org.eclipse.collections.api.bag.primitive.MutableBooleanBag;
import org.eclipse.collections.api.block.function.primitive.BooleanFunction;
import org.eclipse.collections.api.block.predicate.primitive.BooleanPredicate;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.block.procedure.primitive.BooleanProcedure;
import org.eclipse.collections.api.factory.primitive.BooleanBags;
import org.eclipse.collections.api.factory.primitive.BooleanLists;
import org.eclipse.collections.api.factory.primitive.BooleanSets;
import org.eclipse.collections.api.iterator.BooleanIterator;
import org.eclipse.collections.api.list.primitive.MutableBooleanList;
import org.eclipse.collections.api.set.primitive.MutableBooleanSet;

/**
 * A CollectIntIterable is an iterable that transforms a source iterable using an IntFunction as it iterates.
 */
public class CollectBooleanIterable<T>
        extends AbstractLazyBooleanIterable
{
    private final LazyIterable<T> iterable;
    private final BooleanFunction<? super T> function;
    private final BooleanFunctionToProcedure<T> booleanFunctionToProcedure;

    public CollectBooleanIterable(LazyIterable<T> adapted, BooleanFunction<? super T> function)
    {
        this.iterable = adapted;
        this.function = function;
        this.booleanFunctionToProcedure = new BooleanFunctionToProcedure<>(function);
    }

    @Override
    public BooleanIterator booleanIterator()
    {
        return new BooleanIterator()
        {
            private final Iterator<T> iterator = CollectBooleanIterable.this.iterable.iterator();

            @Override
            public boolean next()
            {
                return CollectBooleanIterable.this.function.booleanValueOf(this.iterator.next());
            }

            @Override
            public boolean hasNext()
            {
                return this.iterator.hasNext();
            }
        };
    }

    @Override
    public void forEach(BooleanProcedure procedure)
    {
        this.each(procedure);
    }

    /**
     * @since 7.0.
     */
    @Override
    public void each(BooleanProcedure procedure)
    {
        this.iterable.forEachWith(this.booleanFunctionToProcedure, procedure);
    }

    @Override
    public int size()
    {
        return this.iterable.size();
    }

    @Override
    public boolean isEmpty()
    {
        return this.iterable.isEmpty();
    }

    @Override
    public boolean notEmpty()
    {
        return this.iterable.notEmpty();
    }

    @Override
    public int count(BooleanPredicate predicate)
    {
        return this.iterable.count(each -> predicate.accept(this.function.booleanValueOf(each)));
    }

    @Override
    public boolean anySatisfy(BooleanPredicate predicate)
    {
        return this.iterable.anySatisfy(each -> predicate.accept(this.function.booleanValueOf(each)));
    }

    @Override
    public boolean allSatisfy(BooleanPredicate predicate)
    {
        return this.iterable.allSatisfy(each -> predicate.accept(this.function.booleanValueOf(each)));
    }

    @Override
    public boolean noneSatisfy(BooleanPredicate predicate)
    {
        return this.iterable.allSatisfy(each -> !predicate.accept(this.function.booleanValueOf(each)));
    }

    @Override
    public boolean[] toArray()
    {
        boolean[] array = new boolean[this.size()];
        this.iterable.forEachWithIndex((each, index) -> array[index] = this.function.booleanValueOf(each));
        return array;
    }

    @Override
    public MutableBooleanList toList()
    {
        return BooleanLists.mutable.withAll(this);
    }

    @Override
    public MutableBooleanSet toSet()
    {
        return BooleanSets.mutable.withAll(this);
    }

    @Override
    public MutableBooleanBag toBag()
    {
        return BooleanBags.mutable.withAll(this);
    }

    @Override
    public boolean containsAll(boolean... source)
    {
        for (boolean value : source)
        {
            if (!this.contains(value))
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean containsAll(BooleanIterable source)
    {
        for (BooleanIterator iterator = source.booleanIterator(); iterator.hasNext(); )
        {
            if (!this.contains(iterator.next()))
            {
                return false;
            }
        }
        return true;
    }

    private static final class BooleanFunctionToProcedure<T> implements Procedure2<T, BooleanProcedure>
    {
        private static final long serialVersionUID = 1L;
        private final BooleanFunction<? super T> function;

        private BooleanFunctionToProcedure(BooleanFunction<? super T> function)
        {
            this.function = function;
        }

        @Override
        public void value(T each, BooleanProcedure procedure)
        {
            procedure.value(this.function.booleanValueOf(each));
        }
    }
}
