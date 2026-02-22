/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.block.function;

import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.tuple.Tuples;

/**
 * Represents a case function for creating and evaluating cases in order, returning the result of the first matching case. Multiple cases not supported.
 */
public class CaseFunction<T extends Comparable<? super T>, V> implements Function<T, V>
{
    private static final long serialVersionUID = 1L;

    private final MutableList<Pair<Predicate<? super T>, Function<? super T, ? extends V>>> predicateFunctions = Lists.mutable.empty();
    private Function<? super T, ? extends V> defaultFunction;

    public CaseFunction()
    {
    }

    public CaseFunction(Function<? super T, ? extends V> newDefaultFunction)
    {
        this.defaultFunction = newDefaultFunction;
    }

    public CaseFunction<T, V> addCase(
            Predicate<? super T> predicate,
            Function<? super T, ? extends V> function)
    {
        this.predicateFunctions.add(Tuples.pair(predicate, function));
        return this;
    }

    public CaseFunction<T, V> setDefault(Function<? super T, ? extends V> function)
    {
        this.defaultFunction = function;
        return this;
    }

    @Override
    public V valueOf(T argument)
    {
        int localSize = this.predicateFunctions.size();
        for (int i = 0; i < localSize; i++)
        {
            Pair<Predicate<? super T>, Function<? super T, ? extends V>> pair = this.predicateFunctions.get(i);
            if (pair.getOne().accept(argument))
            {
                return pair.getTwo().valueOf(argument);
            }
        }

        if (this.defaultFunction != null)
        {
            return this.defaultFunction.valueOf(argument);
        }

        return null;
    }

    @Override
    public String toString()
    {
        return "new CaseFunction(" + this.predicateFunctions + ')';
    }
}
