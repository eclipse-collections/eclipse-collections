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
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.multimap.AbstractMutableMultimap;
import org.eclipse.collections.impl.multimap.bag.HashBagMultimap;
import org.eclipse.collections.impl.multimap.list.FastListMultimap;
import org.eclipse.collections.impl.set.sorted.mutable.TreeSortedSet;
import org.eclipse.collections.impl.utility.Iterate;

public final class TreeSortedSetMultimap<K, V>
        extends AbstractMutableMultimap<K, V, MutableSortedSet<V>>
        implements MutableSortedSetMultimap<K, V>, Externalizable
{
    private static final long serialVersionUID = 1L;
    private Comparator<? super V> comparator;

    public TreeSortedSetMultimap()
    {
        this.comparator = null;
    }

    public TreeSortedSetMultimap(Comparator<? super V> comparator)
    {
        this.comparator = comparator;
    }

    public TreeSortedSetMultimap(Multimap<? extends K, ? extends V> multimap)
    {
        super(Math.max(multimap.sizeDistinct() * 2, 16));
        this.comparator = multimap instanceof SortedSetMultimap<?, ?>
                ? ((SortedSetMultimap<K, V>) multimap).comparator()
                : null;
        this.putAll(multimap);
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    public TreeSortedSetMultimap(Pair<K, V>... pairs)
    {
        super(pairs);
        this.comparator = null;
    }

    public TreeSortedSetMultimap(Iterable<Pair<K, V>> inputIterable)
    {
        super(inputIterable);
        this.comparator = null;
    }

    public static <K, V> TreeSortedSetMultimap<K, V> newMultimap()
    {
        return new TreeSortedSetMultimap<>();
    }

    public static <K, V> TreeSortedSetMultimap<K, V> newMultimap(Multimap<? extends K, ? extends V> multimap)
    {
        return new TreeSortedSetMultimap<>(multimap);
    }

    public static <K, V> TreeSortedSetMultimap<K, V> newMultimap(Comparator<? super V> comparator)
    {
        return new TreeSortedSetMultimap<>(comparator);
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <K, V> TreeSortedSetMultimap<K, V> newMultimap(Pair<K, V>... pairs)
    {
        return new TreeSortedSetMultimap<>(pairs);
    }

    public static <K, V> TreeSortedSetMultimap<K, V> newMultimap(Iterable<Pair<K, V>> inputIterable)
    {
        return new TreeSortedSetMultimap<>(inputIterable);
    }

    @Override
    protected MutableMap<K, MutableSortedSet<V>> createMap()
    {
        return Maps.mutable.empty();
    }

    @Override
    protected MutableMap<K, MutableSortedSet<V>> createMapWithKeyCount(int keyCount)
    {
        return UnifiedMap.newMap(keyCount);
    }

    @Override
    protected MutableSortedSet<V> createCollection()
    {
        return new TreeSortedSet<>(this.comparator);
    }

    @Override
    public TreeSortedSetMultimap<K, V> newEmpty()
    {
        return new TreeSortedSetMultimap<>(this.comparator());
    }

    @Override
    public Comparator<? super V> comparator()
    {
        return this.comparator;
    }

    @Override
    public MutableSortedSetMultimap<K, V> toMutable()
    {
        return new TreeSortedSetMultimap<>(this);
    }

    @Override
    public ImmutableSortedSetMultimap<K, V> toImmutable()
    {
        MutableMap<K, ImmutableSortedSet<V>> map = Maps.mutable.empty();

        this.map.forEachKeyValue((key, set) -> map.put(key, set.toImmutable()));
        return new ImmutableSortedSetMultimapImpl<>(map, this.comparator());
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException
    {
        out.writeObject(this.comparator());
        super.writeExternal(out);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
    {
        this.comparator = (Comparator<? super V>) in.readObject();
        super.readExternal(in);
    }

    @Override
    public MutableSetMultimap<V, K> flip()
    {
        return Iterate.flip(this);
    }

    @Override
    public TreeSortedSetMultimap<K, V> selectKeysValues(Predicate2<? super K, ? super V> predicate)
    {
        return this.selectKeysValues(predicate, this.newEmpty());
    }

    @Override
    public TreeSortedSetMultimap<K, V> rejectKeysValues(Predicate2<? super K, ? super V> predicate)
    {
        return this.rejectKeysValues(predicate, this.newEmpty());
    }

    @Override
    public TreeSortedSetMultimap<K, V> selectKeysMultiValues(Predicate2<? super K, ? super RichIterable<V>> predicate)
    {
        return this.selectKeysMultiValues(predicate, this.newEmpty());
    }

    @Override
    public TreeSortedSetMultimap<K, V> rejectKeysMultiValues(Predicate2<? super K, ? super RichIterable<V>> predicate)
    {
        return this.rejectKeysMultiValues(predicate, this.newEmpty());
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
    public MutableSortedSetMultimap<K, V> asSynchronized()
    {
        return SynchronizedSortedSetMultimap.of(this);
    }
}
