/*
 * Copyright (c) 2021 Goldman Sachs.
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
import org.eclipse.collections.api.block.function.primitive.BooleanToObjectFunction;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.block.procedure.primitive.BooleanProcedure;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.api.iterator.BooleanIterator;
import org.eclipse.collections.impl.lazy.AbstractLazyIterable;

public class CollectBooleanToObjectIterable<V> extends AbstractLazyIterable<V>
{
    private final BooleanIterable iterable;
    private final BooleanToObjectFunction<? extends V> function;

    public CollectBooleanToObjectIterable(BooleanIterable iterable, BooleanToObjectFunction<? extends V> function)
    {
        this.iterable = iterable;
        this.function = function;
    }

    @Override
    public void each(Procedure<? super V> procedure)
    {
        this.iterable.forEach(each -> procedure.value(this.function.valueOf(each)));
    }

    @Override
    public void forEachWithIndex(ObjectIntProcedure<? super V> objectIntProcedure)
    {
        this.iterable.forEach(new BooleanProcedure()
        {
            private int index;

            @Override
            public void value(boolean each)
            {
                objectIntProcedure.value(CollectBooleanToObjectIterable.this.function.valueOf(each), this.index++);
            }
        });
    }

    @Override
    public <P> void forEachWith(Procedure2<? super V, ? super P> procedure, P parameter)
    {
        this.iterable.forEach(each -> procedure.value(this.function.valueOf(each), parameter));
    }

    @Override
    public Iterator<V> iterator()
    {
        return new Iterator<V>()
        {
            private final BooleanIterator iterator = CollectBooleanToObjectIterable.this.iterable.booleanIterator();

            @Override
            public boolean hasNext()
            {
                return this.iterator.hasNext();
            }

            @Override
            public V next()
            {
                return CollectBooleanToObjectIterable.this.function.valueOf(this.iterator.next());
            }

            @Override
            public void remove()
            {
                throw new UnsupportedOperationException("Cannot call remove() on " + this.getClass().getSimpleName());
            }
        };
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
}
