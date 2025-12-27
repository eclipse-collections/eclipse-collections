/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.lazy.parallel.bag;

import org.eclipse.collections.api.annotation.Beta;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.impl.lazy.parallel.AbstractBatch;
import org.eclipse.collections.impl.lazy.parallel.Batch;
import org.eclipse.collections.impl.utility.Iterate;

@Beta
public class FlatCollectUnsortedBagBatch<T, V> extends AbstractBatch<V> implements UnsortedBagBatch<V>
{
    private final Batch<T> unsortedBagBatch;
    private final Function<? super T, ? extends Iterable<V>> function;

    public FlatCollectUnsortedBagBatch(Batch<T> unsortedBagBatch, Function<? super T, ? extends Iterable<V>> function)
    {
        this.unsortedBagBatch = unsortedBagBatch;
        this.function = function;
    }

    @Override
    public void forEach(Procedure<? super V> procedure)
    {
        this.unsortedBagBatch.forEach(each -> Iterate.forEach(this.function.valueOf(each), procedure));
    }

    @Override
    public void forEachWithOccurrences(ObjectIntProcedure<? super V> procedure)
    {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public UnsortedBagBatch<V> select(Predicate<? super V> predicate)
    {
        return new SelectUnsortedBagBatch<>(this, predicate);
    }

    @Override
    public <VV> UnsortedBagBatch<VV> collect(Function<? super V, ? extends VV> function)
    {
        return new CollectUnsortedBagBatch<>(this, function);
    }

    @Override
    public <V1> UnsortedBagBatch<V1> flatCollect(Function<? super V, ? extends Iterable<V1>> function)
    {
        return new FlatCollectUnsortedBagBatch<>(this, function);
    }
}
