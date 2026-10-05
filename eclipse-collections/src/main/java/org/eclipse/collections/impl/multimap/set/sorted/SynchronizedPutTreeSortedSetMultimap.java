/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.multimap.set.sorted;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Comparator;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.multimap.Multimap;
import org.eclipse.collections.api.multimap.set.MutableSetMultimap;
import org.eclipse.collections.api.multimap.sortedset.ImmutableSortedSetMultimap;
import org.eclipse.collections.api.multimap.sortedset.MutableSortedSetMultimap;
import org.eclipse.collections.api.multimap.sortedset.SortedSetMultimap;
import org.eclipse.collections.api.set.sorted.ImmutableSortedSet;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.multimap.AbstractSynchronizedPutMultimap;
import org.eclipse.collections.impl.multimap.bag.HashBagMultimap;
import org.eclipse.collections.impl.multimap.list.FastListMultimap;
import org.eclipse.collections.impl.set.sorted.mutable.TreeSortedSet;
import org.eclipse.collections.impl.utility.ArrayIterate;
import org.eclipse.collections.impl.utility.Iterate;

/**
 * A Multimap that is optimized for parallel writes, but is not protected for concurrent reads.
 */
public class SynchronizedPutTreeSortedSetMultimap<K, V>
        extends AbstractSynchronizedPutMultimap<K, V, MutableSortedSet<V>> implements MutableSortedSetMultimap<K, V>, Externalizable
{
    private static final long serialVersionUID = 2L;
    private Comparator<? super V> comparator;

    public SynchronizedPutTreeSortedSetMultimap()
    {
        this.comparator = null;
    }

    public SynchronizedPutTreeSortedSetMultimap(Comparator<? super V> comparator)
    {
        this.comparator = comparator;
    }

    public SynchronizedPutTreeSortedSetMultimap(Multimap<? extends K, ? extends V> multimap)
    {
        this.comparator = multimap instanceof SortedSetMultimap<?, ?>
                ? ((SortedSetMultimap<K, V>) multimap).comparator()
                : null;
        this.putAll(multimap);
    }

    public SynchronizedPutTreeSortedSetMultimap(Pair<K, V>... pairs)
    {
        this();
        ArrayIterate.forEach(pairs, pair -> this.put(pair.getOne(), pair.getTwo()));
    }

    public SynchronizedPutTreeSortedSetMultimap(Iterable<Pair<K, V>> inputIterable)
    {
        this();
        Iterate.forEach(inputIterable, this::add);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException
    {
        out.writeObject(this.comparator);
        super.writeExternal(out);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
    {
        this.comparator = (Comparator<? super V>) in.readObject();
        super.readExternal(in);
    }

    @Override
    public SynchronizedPutTreeSortedSetMultimap<K, V> withKeyMultiValues(K key, V... values)
    {
        return (SynchronizedPutTreeSortedSetMultimap<K, V>) super.withKeyMultiValues(key, values);
    }

    public static <K, V> SynchronizedPutTreeSortedSetMultimap<K, V> newMultimap(Comparator<? super V> comparator)
    {
        return new SynchronizedPutTreeSortedSetMultimap<>(comparator);
    }

    public static <K, V> SynchronizedPutTreeSortedSetMultimap<K, V> newMultimap()
    {
        return new SynchronizedPutTreeSortedSetMultimap<>();
    }

    public static <K, V> SynchronizedPutTreeSortedSetMultimap<K, V> newMultimap(Multimap<? extends K, ? extends V> multimap)
    {
        return new SynchronizedPutTreeSortedSetMultimap<>(multimap);
    }

    public static <K, V> SynchronizedPutTreeSortedSetMultimap<K, V> newMultimap(Pair<K, V>... pairs)
    {
        return new SynchronizedPutTreeSortedSetMultimap<>(pairs);
    }

    public static <K, V> SynchronizedPutTreeSortedSetMultimap<K, V> newMultimap(Iterable<Pair<K, V>> inputIterable)
    {
        return new SynchronizedPutTreeSortedSetMultimap<>(inputIterable);
    }

    @Override
    protected MutableSortedSet<V> createCollection()
    {
        return TreeSortedSet.newSet(this.comparator);
    }

    @Override
    public SynchronizedPutTreeSortedSetMultimap<K, V> newEmpty()
    {
        return new SynchronizedPutTreeSortedSetMultimap<>();
    }

    @Override
    public Comparator<? super V> comparator()
    {
        return this.comparator;
    }

    @Override
    public MutableSortedSetMultimap<K, V> toMutable()
    {
        return new SynchronizedPutTreeSortedSetMultimap<>(this);
    }

    @Override
    public ImmutableSortedSetMultimap<K, V> toImmutable()
    {
        MutableMap<K, ImmutableSortedSet<V>> map = Maps.mutable.empty();

        this.map.forEachKeyValue((key, set) -> map.put(key, set.toImmutable()));
        return new ImmutableSortedSetMultimapImpl<>(map, this.comparator());
    }

    @Override
    public TreeSortedSetMultimap<K, V> selectKeysValues(Predicate2<? super K, ? super V> predicate)
    {
        return this.selectKeysValues(predicate, TreeSortedSetMultimap.newMultimap(this.comparator));
    }

    @Override
    public TreeSortedSetMultimap<K, V> rejectKeysValues(Predicate2<? super K, ? super V> predicate)
    {
        return this.rejectKeysValues(predicate, TreeSortedSetMultimap.newMultimap(this.comparator));
    }

    @Override
    public TreeSortedSetMultimap<K, V> selectKeysMultiValues(Predicate2<? super K, ? super RichIterable<V>> predicate)
    {
        return this.selectKeysMultiValues(predicate, TreeSortedSetMultimap.newMultimap(this.comparator));
    }

    @Override
    public TreeSortedSetMultimap<K, V> rejectKeysMultiValues(Predicate2<? super K, ? super RichIterable<V>> predicate)
    {
        return this.rejectKeysMultiValues(predicate, TreeSortedSetMultimap.newMultimap(this.comparator));
    }

    @Override
    public <K2, V2> HashBagMultimap<K2, V2> collectKeysValues(Function2<? super K, ? super V, Pair<K2, V2>> function)
    {
        return this.collectKeysValues(function, HashBagMultimap.newMultimap());
    }

    @Override
    public <K2, V2> HashBagMultimap<K2, V2> collectKeyMultiValues(Function<? super K, ? extends K2> keyFunction, Function<? super V, ? extends V2> valueFunction)
    {
        return this.collectKeyMultiValues(keyFunction, valueFunction, HashBagMultimap.newMultimap());
    }

    @Override
    public <V2> FastListMultimap<K, V2> collectValues(Function<? super V, ? extends V2> function)
    {
        return this.collectValues(function, FastListMultimap.newMultimap());
    }

    @Override
    public MutableSetMultimap<V, K> flip()
    {
        return Iterate.flip(this);
    }

    @Override
    public MutableSortedSetMultimap<K, V> asSynchronized()
    {
        throw new UnsupportedOperationException("Cannot call asSynchronized() on " + this.getClass().getSimpleName());
    }
}
