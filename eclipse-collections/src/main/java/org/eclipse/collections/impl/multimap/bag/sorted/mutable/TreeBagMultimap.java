/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.multimap.bag.sorted.mutable;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Comparator;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.bag.sorted.ImmutableSortedBag;
import org.eclipse.collections.api.bag.sorted.MutableSortedBag;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.multimap.Multimap;
import org.eclipse.collections.api.multimap.bag.MutableBagMultimap;
import org.eclipse.collections.api.multimap.sortedbag.ImmutableSortedBagMultimap;
import org.eclipse.collections.api.multimap.sortedbag.MutableSortedBagMultimap;
import org.eclipse.collections.api.multimap.sortedbag.SortedBagMultimap;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.bag.sorted.mutable.TreeBag;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.multimap.AbstractMutableMultimap;
import org.eclipse.collections.impl.multimap.bag.HashBagMultimap;
import org.eclipse.collections.impl.multimap.bag.sorted.immutable.ImmutableSortedBagMultimapImpl;
import org.eclipse.collections.impl.multimap.list.FastListMultimap;
import org.eclipse.collections.impl.utility.Iterate;

public final class TreeBagMultimap<K, V>
        extends AbstractMutableMultimap<K, V, MutableSortedBag<V>>
        implements MutableSortedBagMultimap<K, V>, Externalizable
{
    private static final long serialVersionUID = 1L;
    private Comparator<? super V> comparator;

    public TreeBagMultimap()
    {
        this.comparator = null;
    }

    public TreeBagMultimap(Comparator<? super V> comparator)
    {
        this.comparator = comparator;
    }

    public TreeBagMultimap(Multimap<? extends K, ? extends V> multimap)
    {
        super(Math.max(multimap.keysView().size() * 2, 16));
        this.comparator = multimap instanceof SortedBagMultimap<?, ?>
                ? ((SortedBagMultimap<K, V>) multimap).comparator()
                : null;
        this.putAll(multimap);
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    public TreeBagMultimap(Pair<K, V>... pairs)
    {
        super(pairs);
        this.comparator = null;
    }

    public TreeBagMultimap(Iterable<Pair<K, V>> inputIterable)
    {
        super(inputIterable);
        this.comparator = null;
    }

    public static <K, V> TreeBagMultimap<K, V> newMultimap()
    {
        return new TreeBagMultimap<>();
    }

    public static <K, V> TreeBagMultimap<K, V> newMultimap(Multimap<? extends K, ? extends V> multimap)
    {
        return new TreeBagMultimap<>(multimap);
    }

    public static <K, V> TreeBagMultimap<K, V> newMultimap(Comparator<? super V> comparator)
    {
        return new TreeBagMultimap<>(comparator);
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <K, V> TreeBagMultimap<K, V> newMultimap(Pair<K, V>... pairs)
    {
        return new TreeBagMultimap<>(pairs);
    }

    public static <K, V> TreeBagMultimap<K, V> newMultimap(Iterable<Pair<K, V>> inputIterable)
    {
        return new TreeBagMultimap<>(inputIterable);
    }

    @Override
    protected MutableMap<K, MutableSortedBag<V>> createMap()
    {
        return Maps.mutable.empty();
    }

    @Override
    protected MutableMap<K, MutableSortedBag<V>> createMapWithKeyCount(int keyCount)
    {
        return UnifiedMap.newMap(keyCount);
    }

    @Override
    protected MutableSortedBag<V> createCollection()
    {
        return TreeBag.newBag(this.comparator);
    }

    @Override
    public TreeBagMultimap<K, V> newEmpty()
    {
        return new TreeBagMultimap<>(this.comparator);
    }

    @Override
    public Comparator<? super V> comparator()
    {
        return this.comparator;
    }

    @Override
    public MutableSortedBagMultimap<K, V> toMutable()
    {
        return new TreeBagMultimap<>(this);
    }

    @Override
    public ImmutableSortedBagMultimap<K, V> toImmutable()
    {
        MutableMap<K, ImmutableSortedBag<V>> map = Maps.mutable.empty();

        this.map.forEachKeyValue((key, bag) -> map.put(key, bag.toImmutable()));
        return new ImmutableSortedBagMultimapImpl<>(map, this.comparator());
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
    public MutableBagMultimap<V, K> flip()
    {
        return Iterate.flip(this);
    }

    @Override
    public TreeBagMultimap<K, V> selectKeysValues(Predicate2<? super K, ? super V> predicate)
    {
        return this.selectKeysValues(predicate, this.newEmpty());
    }

    @Override
    public TreeBagMultimap<K, V> rejectKeysValues(Predicate2<? super K, ? super V> predicate)
    {
        return this.rejectKeysValues(predicate, this.newEmpty());
    }

    @Override
    public TreeBagMultimap<K, V> selectKeysMultiValues(Predicate2<? super K, ? super RichIterable<V>> predicate)
    {
        return this.selectKeysMultiValues(predicate, this.newEmpty());
    }

    @Override
    public TreeBagMultimap<K, V> rejectKeysMultiValues(Predicate2<? super K, ? super RichIterable<V>> predicate)
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
    public MutableSortedBagMultimap<K, V> asSynchronized()
    {
        return SynchronizedSortedBagMultimap.of(this);
    }
}
