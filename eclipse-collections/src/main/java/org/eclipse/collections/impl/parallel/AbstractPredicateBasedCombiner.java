/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.parallel;

import java.util.Collection;

import org.eclipse.collections.api.bag.Bag;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.list.ListIterable;
import org.eclipse.collections.api.map.MapIterable;
import org.eclipse.collections.api.set.SetIterable;
import org.eclipse.collections.api.set.sorted.SortedSetIterable;
import org.eclipse.collections.impl.bag.mutable.HashBag;
import org.eclipse.collections.impl.list.mutable.CompositeFastList;
import org.eclipse.collections.impl.set.sorted.mutable.TreeSortedSet;
import org.eclipse.collections.impl.utility.internal.DefaultSpeciesNewStrategy;

public abstract class AbstractPredicateBasedCombiner<T, BT extends Procedure<T>>
        extends AbstractProcedureCombiner<BT>
{
    private static final long serialVersionUID = 1L;

    protected final Collection<T> result;

    protected AbstractPredicateBasedCombiner(
            boolean useCombineOne,
            Iterable<?> sourceCollection,
            int initialCapacity,
            Collection<T> targetCollection)
    {
        super(useCombineOne);
        this.result = this.initializeResult(sourceCollection, targetCollection, initialCapacity);
    }

    protected final Collection<T> initializeResult(
            Iterable<?> sourceCollection,
            Collection<T> targetCollection,
            int initialCapacity)
    {
        if (targetCollection != null)
        {
            return targetCollection;
        }
        if (sourceCollection instanceof ListIterable)
        {
            return new CompositeFastList<>();
        }
        if (sourceCollection instanceof SortedSetIterable)
        {
            return TreeSortedSet.newSet(((SortedSetIterable<T>) sourceCollection).comparator());
        }
        if (sourceCollection instanceof SetIterable)
        {
            this.setCombineOne(true);
            return Sets.mutable.withInitialCapacity(initialCapacity);
        }
        if (sourceCollection instanceof Bag || sourceCollection instanceof MapIterable)
        {
            return HashBag.newBag();
        }
        return this.createResultForCollection(sourceCollection, initialCapacity);
    }

    private Collection<T> createResultForCollection(Iterable<?> sourceCollection, int initialCapacity)
    {
        if (sourceCollection instanceof Collection<?> collection)
        {
            return DefaultSpeciesNewStrategy.INSTANCE.speciesNew(collection, initialCapacity);
        }
        return Lists.mutable.withInitialCapacity(initialCapacity);
    }

    public Collection<T> getResult()
    {
        return this.result;
    }
}
