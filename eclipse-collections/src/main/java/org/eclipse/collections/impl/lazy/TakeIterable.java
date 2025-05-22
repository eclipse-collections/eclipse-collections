/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.lazy;

import java.util.Iterator;

import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.impl.lazy.iterator.TakeIterator;

/**
 * Iterates over the first count elements of the adapted Iterable or the full size of the adapted
 * iterable if the count is greater than the length of the receiver.
 */
public class TakeIterable<T> extends AbstractLazyIterable<T>
{
    private final Iterable<T> adapted;
    private final int count;

    public TakeIterable(Iterable<T> newAdapted, int count)
    {
        if (count < 0)
        {
            throw new IllegalArgumentException("Count must be greater than zero, but was: " + count);
        }
        this.adapted = newAdapted;
        this.count = count;
    }

    // TODO: implement in terms of LazyIterate.whileDo() when it is added.

    @Override
    public void each(Procedure<? super T> procedure)
    {
        int i = 0;
        Iterator<T> iterator = this.adapted.iterator();
        while (i < this.count && iterator.hasNext())
        {
            procedure.value(iterator.next());
            i++;
        }
    }

    @Override
    public void forEachWithIndex(ObjectIntProcedure<? super T> procedure)
    {
        int i = 0;
        Iterator<T> iterator = this.adapted.iterator();
        while (i < this.count && iterator.hasNext())
        {
            procedure.value(iterator.next(), i);
            i++;
        }
    }

    @Override
    public <P> void forEachWith(Procedure2<? super T, ? super P> procedure, P parameter)
    {
        int i = 0;
        Iterator<T> iterator = this.adapted.iterator();
        while (i < this.count && iterator.hasNext())
        {
            procedure.value(iterator.next(), parameter);
            i++;
        }
    }

    @Override
    public Iterator<T> iterator()
    {
        return new TakeIterator<>(this.adapted, this.count);
    }
}
