/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.bag.mutable;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;

import org.eclipse.collections.api.bag.Bag;
import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.block.predicate.primitive.IntPredicate;
import org.eclipse.collections.api.map.primitive.MutableObjectIntMap;
import org.eclipse.collections.impl.map.mutable.primitive.ObjectIntHashMap;
import org.eclipse.collections.impl.utility.ArrayIterate;
import org.eclipse.collections.impl.utility.Iterate;

/**
 * A HashBag is a MutableBag which uses a Map as its underlying data store. Each key in the Map represents some item,
 * and the value in the map represents the current number of occurrences of that item.
 *
 * @since 1.0
 */
public class HashBag<T>
        extends AbstractHashBag<T>
        implements Externalizable
{
    private static final long serialVersionUID = 1L;

    public HashBag()
    {
        this.items = new ObjectIntHashMap<>();
    }

    public HashBag(int size)
    {
        this.items = new ObjectIntHashMap<>(size);
    }

    private HashBag(MutableObjectIntMap<T> map)
    {
        this.items = map;
        this.size = (int) map.sum();
    }

    public static <E> HashBag<E> newBag()
    {
        return new HashBag<>();
    }

    public static <E> HashBag<E> newBag(int size)
    {
        return new HashBag<>(size);
    }

    public static <E> HashBag<E> newBag(Bag<? extends E> source)
    {
        //noinspection SSBasedInspection
        HashBag<E> result = HashBag.newBag(source.sizeDistinct());
        result.addAllBag(source);
        return result;
    }

    public static <E> HashBag<E> newBag(Iterable<? extends E> source)
    {
        if (source instanceof Bag)
        {
            return HashBag.newBag((Bag<E>) source);
        }
        return HashBag.newBagWith((E[]) Iterate.toArray(source));
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <E> HashBag<E> newBagWith(E... elements)
    {
        HashBag<E> result = HashBag.newBag();
        ArrayIterate.addAllTo(elements, result);
        return result;
    }

    /**
     * Rehashes every element in the set into a new backing table of the smallest possible size and eliminating removed sentinels.
     *
     * @since 12.0
     */
    public void trimToSize()
    {
        ((ObjectIntHashMap<T>) this.items).trimToSize();
    }

    @Override
    protected int computeHashCode(T item)
    {
        return item.hashCode();
    }

    @Override
    public MutableBag<T> selectByOccurrences(IntPredicate predicate)
    {
        MutableObjectIntMap<T> map = this.items.select((each, occurrences) -> predicate.accept(occurrences));
        return new HashBag<>(map);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException
    {
        ((Externalizable) this.items).writeExternal(out);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
    {
        this.items = new ObjectIntHashMap<>();
        ((Externalizable) this.items).readExternal(in);
        this.size = (int) this.items.sum();
    }

    @Override
    public HashBag<T> without(T element)
    {
        this.remove(element);
        return this;
    }

    @Override
    public MutableBag<T> newEmpty()
    {
        return HashBag.newBag();
    }

    @Override
    public HashBag<T> with(T element)
    {
        this.add(element);
        return this;
    }

    @Override
    public HashBag<T> withAll(Iterable<? extends T> iterable)
    {
        this.addAllIterable(iterable);
        return this;
    }

    @Override
    public HashBag<T> withoutAll(Iterable<? extends T> iterable)
    {
        this.removeAllIterable(iterable);
        return this;
    }

    public HashBag<T> with(T... elements)
    {
        this.addAll(Arrays.asList(elements));
        return this;
    }

    public HashBag<T> with(T element1, T element2)
    {
        this.add(element1);
        this.add(element2);
        return this;
    }

    public HashBag<T> with(T element1, T element2, T element3)
    {
        this.add(element1);
        this.add(element2);
        this.add(element3);
        return this;
    }
}
