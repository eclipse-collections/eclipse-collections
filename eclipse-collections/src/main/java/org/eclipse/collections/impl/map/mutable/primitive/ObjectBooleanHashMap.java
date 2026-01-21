/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.map.mutable.primitive;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import org.eclipse.collections.api.BooleanIterable;
import org.eclipse.collections.api.LazyBooleanIterable;
import org.eclipse.collections.api.LazyIterable;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.bag.primitive.MutableBooleanBag;
import org.eclipse.collections.api.block.function.primitive.BooleanFunction;
import org.eclipse.collections.api.block.function.primitive.BooleanFunction0;
import org.eclipse.collections.api.block.function.primitive.BooleanToBooleanFunction;
import org.eclipse.collections.api.block.function.primitive.BooleanToObjectFunction;
import org.eclipse.collections.api.block.function.primitive.ObjectBooleanToBooleanFunction;
import org.eclipse.collections.api.block.function.primitive.ObjectBooleanToObjectFunction;
import org.eclipse.collections.api.block.predicate.primitive.BooleanPredicate;
import org.eclipse.collections.api.block.predicate.primitive.ObjectBooleanPredicate;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.block.procedure.primitive.BooleanProcedure;
import org.eclipse.collections.api.block.procedure.primitive.ObjectBooleanProcedure;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.api.collection.primitive.ImmutableBooleanCollection;
import org.eclipse.collections.api.collection.primitive.MutableBooleanCollection;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.primitive.BooleanBags;
import org.eclipse.collections.api.factory.primitive.BooleanLists;
import org.eclipse.collections.api.factory.primitive.ObjectBooleanMaps;
import org.eclipse.collections.api.iterator.BooleanIterator;
import org.eclipse.collections.api.iterator.MutableBooleanIterator;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.MutableBooleanList;
import org.eclipse.collections.api.map.primitive.ImmutableObjectBooleanMap;
import org.eclipse.collections.api.map.primitive.MutableObjectBooleanMap;
import org.eclipse.collections.api.map.primitive.ObjectBooleanMap;
import org.eclipse.collections.api.set.primitive.BooleanSet;
import org.eclipse.collections.api.set.primitive.MutableBooleanSet;
import org.eclipse.collections.api.tuple.primitive.ObjectBooleanPair;
import org.eclipse.collections.impl.bag.mutable.primitive.BooleanHashBag;
import org.eclipse.collections.impl.collection.mutable.primitive.SynchronizedBooleanCollection;
import org.eclipse.collections.impl.collection.mutable.primitive.UnmodifiableBooleanCollection;
import org.eclipse.collections.impl.lazy.AbstractLazyIterable;
import org.eclipse.collections.impl.lazy.primitive.LazyBooleanIterableAdapter;
import org.eclipse.collections.impl.list.mutable.primitive.BooleanArrayList;
import org.eclipse.collections.impl.set.mutable.primitive.BooleanHashSet;
import org.eclipse.collections.impl.tuple.primitive.PrimitiveTuples;

/**
 * @since 3.0.
 */
public class ObjectBooleanHashMap<K> implements MutableObjectBooleanMap<K>, Externalizable
{
    public static final boolean EMPTY_VALUE = false;

    private static final long serialVersionUID = 1L;

    /**
     * @deprecated in 5.1.0.
     */
    @Deprecated
    private static final float DEFAULT_LOAD_FACTOR = 0.5f;
    private static final int OCCUPIED_DATA_RATIO = 2;
    private static final int DEFAULT_INITIAL_CAPACITY = 8;

    private static final Object NULL_KEY = new Object()
    {
        @Override
        public boolean equals(Object obj)
        {
            throw new RuntimeException("Possible corruption through unsynchronized concurrent modification.");
        }

        @Override
        public int hashCode()
        {
            throw new RuntimeException("Possible corruption through unsynchronized concurrent modification.");
        }

        @Override
        public String toString()
        {
            return "ObjectBooleanHashMap.NULL_KEY";
        }
    };

    private static final Object REMOVED_KEY = new Object()
    {
        @Override
        public boolean equals(Object obj)
        {
            throw new RuntimeException("Possible corruption through unsynchronized concurrent modification.");
        }

        @Override
        public int hashCode()
        {
            throw new RuntimeException("Possible corruption through unsynchronized concurrent modification.");
        }

        @Override
        public String toString()
        {
            return "ObjectBooleanHashMap.REMOVED_KEY";
        }
    };

    private Object[] keys;
    private BitSet values;

    private int occupiedWithData;
    private int occupiedWithSentinels;

    public ObjectBooleanHashMap()
    {
        this.allocateTable(DEFAULT_INITIAL_CAPACITY << 1);
    }

    public ObjectBooleanHashMap(int initialCapacity)
    {
        if (initialCapacity < 0)
        {
            throw new IllegalArgumentException("initial capacity cannot be less than 0");
        }
        int capacity = this.smallestPowerOfTwoGreaterThan(this.fastCeil(initialCapacity * OCCUPIED_DATA_RATIO));
        this.allocateTable(capacity);
    }

    public ObjectBooleanHashMap(ObjectBooleanMap<? extends K> map)
    {
        this(Math.max(map.size(), DEFAULT_INITIAL_CAPACITY));
        this.putAll(map);
    }

    /**
     * @deprecated in 5.1.0.
     */
    @Deprecated
    public ObjectBooleanHashMap(int initialCapacity, float loadFactor)
    {
        this(initialCapacity);
    }

    private int smallestPowerOfTwoGreaterThan(int n)
    {
        return n > 1 ? Integer.highestOneBit(n - 1) << 1 : 1;
    }

    private int fastCeil(float v)
    {
        int possibleResult = (int) v;
        if (v - possibleResult > 0.0F)
        {
            possibleResult++;
        }
        return possibleResult;
    }

    public static <K> ObjectBooleanHashMap<K> newMap()
    {
        return new ObjectBooleanHashMap<>();
    }

    public static <K> ObjectBooleanHashMap<K> newWithKeysValues(K key1, boolean value1)
    {
        return new ObjectBooleanHashMap<K>(1).withKeyValue(key1, value1);
    }

    public static <K> ObjectBooleanHashMap<K> newWithKeysValues(K key1, boolean value1, K key2, boolean value2)
    {
        return new ObjectBooleanHashMap<K>(2).withKeysValues(key1, value1, key2, value2);
    }

    public static <K> ObjectBooleanHashMap<K> newWithKeysValues(K key1, boolean value1, K key2, boolean value2, K key3, boolean value3)
    {
        return new ObjectBooleanHashMap<K>(3).withKeysValues(key1, value1, key2, value2, key3, value3);
    }

    public static <K> ObjectBooleanHashMap<K> newWithKeysValues(K key1, boolean value1, K key2, boolean value2, K key3, boolean value3, K key4, boolean value4)
    {
        return new ObjectBooleanHashMap<K>(4).withKeysValues(key1, value1, key2, value2, key3, value3, key4, value4);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (!(obj instanceof ObjectBooleanMap))
        {
            return false;
        }

        ObjectBooleanMap<K> other = (ObjectBooleanMap<K>) obj;

        if (this.size() != other.size())
        {
            return false;
        }

        for (int i = 0; i < this.keys.length; i++)
        {
            if (ObjectBooleanHashMap.isNonSentinel(this.keys[i]) && (!other.containsKey(this.toNonSentinel(this.keys[i])) || this.values.get(i) != other.getOrThrow(this.toNonSentinel(this.keys[i]))))
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int result = 0;

        for (int i = 0; i < this.keys.length; i++)
        {
            if (ObjectBooleanHashMap.isNonSentinel(this.keys[i]))
            {
                result += (this.toNonSentinel(this.keys[i]) == null ? 0 : this.keys[i].hashCode()) ^ (this.values.get(i) ? 1231 : 1237);
            }
        }
        return result;
    }

    @Override
    public String toString()
    {
        StringBuilder appendable = new StringBuilder();

        appendable.append("{");

        boolean first = true;

        for (int i = 0; i < this.keys.length; i++)
        {
            Object key = this.keys[i];
            if (ObjectBooleanHashMap.isNonSentinel(key))
            {
                if (!first)
                {
                    appendable.append(", ");
                }
                appendable.append(this.toNonSentinel(key)).append("=").append(this.values.get(i));
                first = false;
            }
        }
        appendable.append("}");

        return appendable.toString();
    }

    @Override
    public int size()
    {
        return this.occupiedWithData;
    }

    @Override
    public boolean isEmpty()
    {
        return this.size() == 0;
    }

    @Override
    public boolean notEmpty()
    {
        return this.size() != 0;
    }

    @Override
    public String makeString()
    {
        return this.makeString(", ");
    }

    @Override
    public String makeString(String separator)
    {
        return this.makeString("", separator, "");
    }

    @Override
    public String makeString(String start, String separator, String end)
    {
        Appendable stringBuilder = new StringBuilder();
        this.appendString(stringBuilder, start, separator, end);
        return stringBuilder.toString();
    }

    @Override
    public void appendString(Appendable appendable)
    {
        this.appendString(appendable, ", ");
    }

    @Override
    public void appendString(Appendable appendable, String separator)
    {
        this.appendString(appendable, "", separator, "");
    }

    @Override
    public void appendString(Appendable appendable, String start, String separator, String end)
    {
        try
        {
            appendable.append(start);

            boolean first = true;

            for (int i = 0; i < this.keys.length; i++)
            {
                Object key = this.keys[i];
                if (ObjectBooleanHashMap.isNonSentinel(key))
                {
                    if (!first)
                    {
                        appendable.append(separator);
                    }
                    appendable.append(String.valueOf(this.values.get(i)));
                    first = false;
                }
            }
            appendable.append(end);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public MutableBooleanIterator booleanIterator()
    {
        return new InternalBooleanIterator();
    }

    @Override
    public boolean[] toArray()
    {
        boolean[] result = new boolean[this.size()];
        int index = 0;
        for (int i = 0; i < this.keys.length; i++)
        {
            if (ObjectBooleanHashMap.isNonSentinel(this.keys[i]))
            {
                result[index] = this.values.get(i);
                index++;
            }
        }
        return result;
    }

    @Override
    public boolean[] toArray(boolean[] result)
    {
        if (result.length < this.size())
        {
            result = new boolean[this.size()];
        }
        int index = 0;
        for (int i = 0; i < this.keys.length; i++)
        {
            if (ObjectBooleanHashMap.isNonSentinel(this.keys[i]))
            {
                result[index] = this.values.get(i);
                index++;
            }
        }
        return result;
    }

    @Override
    public boolean contains(boolean value)
    {
        return this.containsValue(value);
    }

    @Override
    public boolean containsAll(boolean... source)
    {
        for (boolean item : source)
        {
            if (!this.containsValue(item))
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean containsAll(BooleanIterable source)
    {
        return this.containsAll(source.toArray());
    }

    /**
     * @since 7.0.
     */
    @Override
    public void each(BooleanProcedure procedure)
    {
        this.forEachValue(procedure);
    }

    @Override
    public boolean detectIfNone(BooleanPredicate predicate, boolean ifNone)
    {
        for (int i = 0; i < this.keys.length; i++)
        {
            if (ObjectBooleanHashMap.isNonSentinel(this.keys[i]) && predicate.accept(this.values.get(i)))
            {
                return this.values.get(i);
            }
        }
        return ifNone;
    }

    @Override
    public MutableBooleanCollection select(BooleanPredicate predicate)
    {
        MutableBooleanList result = BooleanLists.mutable.empty();
        for (int i = 0; i < this.keys.length; i++)
        {
            if (ObjectBooleanHashMap.isNonSentinel(this.keys[i]) && predicate.accept(this.values.get(i)))
            {
                result.add(this.values.get(i));
            }
        }
        return result;
    }

    @Override
    public int count(BooleanPredicate predicate)
    {
        int count = 0;
        for (int i = 0; i < this.keys.length; i++)
        {
            if (ObjectBooleanHashMap.isNonSentinel(this.keys[i]) && predicate.accept(this.values.get(i)))
            {
                count++;
            }
        }
        return count;
    }

    @Override
    public MutableBooleanCollection reject(BooleanPredicate predicate)
    {
        MutableBooleanList result = BooleanLists.mutable.empty();
        for (int i = 0; i < this.keys.length; i++)
        {
            if (ObjectBooleanHashMap.isNonSentinel(this.keys[i]) && !predicate.accept(this.values.get(i)))
            {
                result.add(this.values.get(i));
            }
        }
        return result;
    }

    @Override
    public boolean anySatisfy(BooleanPredicate predicate)
    {
        for (int i = 0; i < this.keys.length; i++)
        {
            if (ObjectBooleanHashMap.isNonSentinel(this.keys[i]) && predicate.accept(this.values.get(i)))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean allSatisfy(BooleanPredicate predicate)
    {
        for (int i = 0; i < this.keys.length; i++)
        {
            if (ObjectBooleanHashMap.isNonSentinel(this.keys[i]) && !predicate.accept(this.values.get(i)))
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public <V> MutableCollection<V> collect(BooleanToObjectFunction<? extends V> function)
    {
        MutableList<V> result = Lists.mutable.withInitialCapacity(this.size());
        for (int i = 0; i < this.keys.length; i++)
        {
            if (ObjectBooleanHashMap.isNonSentinel(this.keys[i]))
            {
                result.add(function.valueOf(this.values.get(i)));
            }
        }
        return result;
    }

    @Override
    public boolean noneSatisfy(BooleanPredicate predicate)
    {
        for (int i = 0; i < this.keys.length; i++)
        {
            if (ObjectBooleanHashMap.isNonSentinel(this.keys[i]) && predicate.accept(this.values.get(i)))
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public MutableBooleanList toList()
    {
        MutableBooleanList result = new BooleanArrayList(this.size());
        for (int i = 0; i < this.keys.length; i++)
        {
            if (ObjectBooleanHashMap.isNonSentinel(this.keys[i]))
            {
                result.add(this.values.get(i));
            }
        }
        return result;
    }

    @Override
    public MutableBooleanSet toSet()
    {
        MutableBooleanSet result = new BooleanHashSet();
        for (int i = 0; result.size() < 2 && i < this.keys.length; i++)
        {
            if (ObjectBooleanHashMap.isNonSentinel(this.keys[i]))
            {
                result.add(this.values.get(i));
            }
        }
        return result;
    }

    @Override
    public MutableBooleanBag toBag()
    {
        MutableBooleanBag result = new BooleanHashBag();
        for (int i = 0; i < this.keys.length; i++)
        {
            if (ObjectBooleanHashMap.isNonSentinel(this.keys[i]))
            {
                result.add(this.values.get(i));
            }
        }
        return result;
    }

    @Override
    public LazyBooleanIterable asLazy()
    {
        return new LazyBooleanIterableAdapter(this);
    }

    @Override
    public <V> V injectInto(V injectedValue, ObjectBooleanToObjectFunction<? super V, ? extends V> function)
    {
        V result = injectedValue;

        for (int i = 0; i < this.keys.length; i++)
        {
            if (ObjectBooleanHashMap.isNonSentinel(this.keys[i]))
            {
                result = function.valueOf(result, this.values.get(i));
            }
        }

        return result;
    }

    @Override
    public RichIterable<BooleanIterable> chunk(int size)
    {
        if (size <= 0)
        {
            throw new IllegalArgumentException("Size for groups must be positive but was: " + size);
        }
        MutableList<BooleanIterable> result = Lists.mutable.empty();
        if (this.notEmpty())
        {
            BooleanIterator iterator = this.booleanIterator();
            while (iterator.hasNext())
            {
                MutableBooleanBag batch = BooleanBags.mutable.empty();
                for (int i = 0; i < size && iterator.hasNext(); i++)
                {
                    batch.add(iterator.next());
                }
                result.add(batch);
            }
        }
        return result;
    }

    @Override
    public void clear()
    {
        this.occupiedWithData = 0;
        this.occupiedWithSentinels = 0;
        Arrays.fill(this.keys, null);
        this.values.clear();
    }

    @Override
    public void put(K key, boolean value)
    {
        int index = this.probe(key);

        if (ObjectBooleanHashMap.isNonSentinel(this.keys[index]) && ObjectBooleanHashMap.nullSafeEquals(this.toNonSentinel(this.keys[index]), key))
        {
            // key already present in map
            this.values.set(index, value);
            return;
        }

        this.addKeyValueAtIndex(key, value, index);
    }

    @Override
    public void putAll(ObjectBooleanMap<? extends K> map)
    {
        map.forEachKeyValue(this::put);
    }

    @Override
    public void updateValues(ObjectBooleanToBooleanFunction<? super K> function)
    {
        for (int i = 0; i < this.keys.length; i++)
        {
            if (ObjectBooleanHashMap.isNonSentinel(this.keys[i]))
            {
                this.values.set(i, function.valueOf(this.toNonSentinel(this.keys[i]), this.values.get(i)));
            }
        }
    }

    @Override
    public void removeKey(K key)
    {
        int index = this.probe(key);
        this.removeKeyAtIndex(key, index);
    }

    private void removeKeyAtIndex(K key, int index)
    {
        if (isNonSentinel(this.keys[index]) && nullSafeEquals(this.toNonSentinel(this.keys[index]), key))
        {
            this.keys[index] = REMOVED_KEY;
            this.values.set(index, EMPTY_VALUE);
            this.occupiedWithData--;
            this.occupiedWithSentinels++;
        }
    }

    @Override
    public void remove(Object key)
    {
        this.removeKey((K) key);
    }

    @Override
    public boolean removeKeyIfAbsent(K key, boolean value)
    {
        int index = this.probe(key);
        if (ObjectBooleanHashMap.isNonSentinel(this.keys[index]) && ObjectBooleanHashMap.nullSafeEquals(this.toNonSentinel(this.keys[index]), key))
        {
            this.keys[index] = REMOVED_KEY;
            boolean oldValue = this.values.get(index);
            this.values.set(index, EMPTY_VALUE);
            this.occupiedWithData--;
            this.occupiedWithSentinels++;

            return oldValue;
        }
        return value;
    }

    @Override
    public boolean getIfAbsentPut(K key, boolean value)
    {
        int index = this.probe(key);
        if (ObjectBooleanHashMap.isNonSentinel(this.keys[index]) && ObjectBooleanHashMap.nullSafeEquals(this.toNonSentinel(this.keys[index]), key))
        {
            return this.values.get(index);
        }
        this.addKeyValueAtIndex(key, value, index);
        return value;
    }

    @Override
    public boolean getAndPut(K key, boolean putValue, boolean defaultValue)
    {
        int index = this.probe(key);
        if (ObjectBooleanHashMap.isNonSentinel(this.keys[index]) && ObjectBooleanHashMap.nullSafeEquals(this.toNonSentinel(this.keys[index]), key))
        {
            boolean existingValue = this.values.get(index);
            this.values.set(index, putValue);
            return existingValue;
        }
        this.addKeyValueAtIndex(key, putValue, index);
        return defaultValue;
    }

    @Override
    public boolean getIfAbsentPut(K key, BooleanFunction0 function)
    {
        int index = this.probe(key);
        if (ObjectBooleanHashMap.isNonSentinel(this.keys[index]) && ObjectBooleanHashMap.nullSafeEquals(this.toNonSentinel(this.keys[index]), key))
        {
            return this.values.get(index);
        }
        boolean value = function.value();
        this.addKeyValueAtIndex(key, value, index);
        return value;
    }

    @Override
    public boolean getIfAbsentPutWithKey(K key, BooleanFunction<? super K> function)
    {
        int index = this.probe(key);
        if (ObjectBooleanHashMap.isNonSentinel(this.keys[index]) && ObjectBooleanHashMap.nullSafeEquals(this.toNonSentinel(this.keys[index]), key))
        {
            return this.values.get(index);
        }
        boolean value = function.booleanValueOf(key);
        this.addKeyValueAtIndex(key, value, index);
        return value;
    }

    @Override
    public <P> boolean getIfAbsentPutWith(K key, BooleanFunction<? super P> function, P parameter)
    {
        int index = this.probe(key);
        if (ObjectBooleanHashMap.isNonSentinel(this.keys[index]) && ObjectBooleanHashMap.nullSafeEquals(this.toNonSentinel(this.keys[index]), key))
        {
            return this.values.get(index);
        }
        boolean value = function.booleanValueOf(parameter);
        this.addKeyValueAtIndex(key, value, index);
        return value;
    }

    @Override
    public boolean updateValue(K key, boolean initialValueIfAbsent, BooleanToBooleanFunction function)
    {
        int index = this.probe(key);
        if (ObjectBooleanHashMap.isNonSentinel(this.keys[index]) && ObjectBooleanHashMap.nullSafeEquals(this.toNonSentinel(this.keys[index]), key))
        {
            this.values.set(index, function.valueOf(this.values.get(index)));
            return this.values.get(index);
        }
        boolean value = function.valueOf(initialValueIfAbsent);
        this.addKeyValueAtIndex(key, value, index);
        return value;
    }

    @Override
    public ObjectBooleanHashMap<K> withKeyValue(K key1, boolean value1)
    {
        this.put(key1, value1);
        return this;
    }

    @Override
    public ObjectBooleanHashMap<K> withoutKey(K key)
    {
        this.removeKey(key);
        return this;
    }

    @Override
    public ObjectBooleanHashMap<K> withoutAllKeys(Iterable<? extends K> keys)
    {
        for (K key : keys)
        {
            this.removeKey(key);
        }
        return this;
    }

    @Override
    public MutableObjectBooleanMap<K> asUnmodifiable()
    {
        return new UnmodifiableObjectBooleanMap<>(this);
    }

    @Override
    public MutableObjectBooleanMap<K> asSynchronized()
    {
        return new SynchronizedObjectBooleanMap<>(this);
    }

    @Override
    public boolean get(Object key)
    {
        return this.getIfAbsent(key, EMPTY_VALUE);
    }

    @Override
    public boolean getOrThrow(Object key)
    {
        int index = this.probe(key);
        if (ObjectBooleanHashMap.isNonSentinel(this.keys[index]))
        {
            return this.values.get(index);
        }
        throw new IllegalStateException("Key " + key + " not present.");
    }

    @Override
    public boolean getIfAbsent(Object key, boolean ifAbsent)
    {
        int index = this.probe(key);
        if (ObjectBooleanHashMap.isNonSentinel(this.keys[index]) && ObjectBooleanHashMap.nullSafeEquals(this.toNonSentinel(this.keys[index]), key))
        {
            return this.values.get(index);
        }
        return ifAbsent;
    }

    @Override
    public boolean containsKey(Object key)
    {
        int index = this.probe(key);
        return ObjectBooleanHashMap.isNonSentinel(this.keys[index]) && ObjectBooleanHashMap.nullSafeEquals(this.toNonSentinel(this.keys[index]), key);
    }

    @Override
    public boolean containsValue(boolean value)
    {
        for (int i = 0; i < this.keys.length; i++)
        {
            if (ObjectBooleanHashMap.isNonSentinel(this.keys[i]) && this.values.get(i) == value)
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public void forEachValue(BooleanProcedure procedure)
    {
        for (int i = 0; i < this.keys.length; i++)
        {
            if (ObjectBooleanHashMap.isNonSentinel(this.keys[i]))
            {
                procedure.value(this.values.get(i));
            }
        }
    }

    @Override
    public void forEachKey(Procedure<? super K> procedure)
    {
        for (int i = 0; i < this.keys.length; i++)
        {
            if (ObjectBooleanHashMap.isNonSentinel(this.keys[i]))
            {
                procedure.value(this.toNonSentinel(this.keys[i]));
            }
        }
    }

    @Override
    public void forEachKeyValue(ObjectBooleanProcedure<? super K> procedure)
    {
        for (int i = 0; i < this.keys.length; i++)
        {
            if (ObjectBooleanHashMap.isNonSentinel(this.keys[i]))
            {
                procedure.value(this.toNonSentinel(this.keys[i]), this.values.get(i));
            }
        }
    }

    @Override
    public ObjectBooleanHashMap<K> select(ObjectBooleanPredicate<? super K> predicate)
    {
        ObjectBooleanHashMap<K> result = ObjectBooleanHashMap.newMap();
        for (int i = 0; i < this.keys.length; i++)
        {
            if (ObjectBooleanHashMap.isNonSentinel(this.keys[i]) && predicate.accept(this.toNonSentinel(this.keys[i]), this.values.get(i)))
            {
                result.put(this.toNonSentinel(this.keys[i]), this.values.get(i));
            }
        }
        return result;
    }

    @Override
    public ObjectBooleanHashMap<K> reject(ObjectBooleanPredicate<? super K> predicate)
    {
        ObjectBooleanHashMap<K> result = ObjectBooleanHashMap.newMap();
        for (int i = 0; i < this.keys.length; i++)
        {
            if (ObjectBooleanHashMap.isNonSentinel(this.keys[i]) && !predicate.accept(this.toNonSentinel(this.keys[i]), this.values.get(i)))
            {
                result.put(this.toNonSentinel(this.keys[i]), this.values.get(i));
            }
        }
        return result;
    }

    @Override
    public ImmutableObjectBooleanMap<K> toImmutable()
    {
        return ObjectBooleanMaps.immutable.withAll(this);
    }

    @Override
    public Set<K> keySet()
    {
        return new KeySet();
    }

    @Override
    public MutableBooleanCollection values()
    {
        return new ValuesCollection();
    }

    @Override
    public LazyIterable<K> keysView()
    {
        return new KeysView();
    }

    @Override
    public RichIterable<ObjectBooleanPair<K>> keyValuesView()
    {
        return new KeyValuesView();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException
    {
        out.writeInt(this.size());
        /*
         * @deprecated in 5.1.0.
         */
        out.writeFloat(DEFAULT_LOAD_FACTOR);
        for (int i = 0; i < this.keys.length; i++)
        {
            if (ObjectBooleanHashMap.isNonSentinel(this.keys[i]))
            {
                out.writeObject(this.toNonSentinel(this.keys[i]));
                out.writeBoolean(this.values.get(i));
            }
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
    {
        int size = in.readInt();
        /*
         * @deprecated in 5.1.0.
         */
        in.readFloat();
        int capacity = this.smallestPowerOfTwoGreaterThan(this.fastCeil(size * OCCUPIED_DATA_RATIO));
        this.allocateTable(capacity);
        for (int i = 0; i < size; i++)
        {
            this.put((K) in.readObject(), in.readBoolean());
        }
    }

    // exposed for testing
    int probe(Object element)
    {
        int index = this.spread(element);

        int removedIndex = -1;
        if (ObjectBooleanHashMap.isRemovedKey(this.keys[index]))
        {
            removedIndex = index;
        }

        else if (this.keys[index] == null || ObjectBooleanHashMap.nullSafeEquals(this.toNonSentinel(this.keys[index]), element))
        {
            return index;
        }

        int nextIndex = index;
        int probe = 17;

        // loop until an empty slot is reached
        while (true)
        {
            // Probe algorithm: 17*n*(n+1)/2 where n = no. of collisions
            nextIndex += probe;
            probe += 17;
            nextIndex &= this.keys.length - 1;

            if (ObjectBooleanHashMap.isRemovedKey(this.keys[nextIndex]))
            {
                if (removedIndex == -1)
                {
                    removedIndex = nextIndex;
                }
            }
            else if (ObjectBooleanHashMap.nullSafeEquals(this.toNonSentinel(this.keys[nextIndex]), element))
            {
                return nextIndex;
            }
            else if (this.keys[nextIndex] == null)
            {
                return removedIndex == -1 ? nextIndex : removedIndex;
            }
        }
    }

    private static boolean isRemovedKey(Object key)
    {
        return key == REMOVED_KEY;
    }

    private static boolean nullSafeEquals(Object value, Object other)
    {
        if (value == null)
        {
            return other == null;
        }
        return other == value || value.equals(other);
    }

    private K toNonSentinel(Object key)
    {
        return key == NULL_KEY ? null : (K) key;
    }

    // exposed for testing
    int spread(Object element)
    {
        // This function ensures that hashCodes that differ only by
        // constant multiples at each bit position have a bounded
        // number of collisions (approximately 8 at default load factor).
        int h = element == null ? 0 : element.hashCode();
        h ^= h >>> 20 ^ h >>> 12;
        h ^= h >>> 7 ^ h >>> 4;
        return h & (this.keys.length - 1);
    }

    private void addKeyValueAtIndex(K key, boolean value, int index)
    {
        if (this.keys[index] == REMOVED_KEY)
        {
            --this.occupiedWithSentinels;
        }
        this.keys[index] = ObjectBooleanHashMap.toSentinelIfNull(key);
        this.values.set(index, value);
        ++this.occupiedWithData;
        if (this.occupiedWithData + this.occupiedWithSentinels > this.maxOccupiedWithData())
        {
            this.rehashAndGrow();
        }
    }

    private static Object toSentinelIfNull(Object key)
    {
        return key == null ? NULL_KEY : key;
    }

    private int maxOccupiedWithData()
    {
        int capacity = this.keys.length;
        // need at least one free slot for open addressing
        return Math.min(capacity - 1, capacity / OCCUPIED_DATA_RATIO);
    }

    /**
    * @since 12.0
    */
    public boolean trimToSize()
    {
        int newCapacity = this.smallestPowerOfTwoGreaterThan(this.size());
        if (this.keys.length > newCapacity)
        {
            this.rehash(newCapacity);
            return true;
        }
        return false;
    }

    private void rehashAndGrow()
    {
        this.rehash(this.keys.length << 1);
    }

    private void rehash(int newCapacity)
    {
        int oldLength = this.keys.length;
        Object[] old = this.keys;
        BitSet oldValues = this.values;
        this.allocateTable(newCapacity);
        this.occupiedWithData = 0;
        this.occupiedWithSentinels = 0;

        for (int i = 0; i < oldLength; i++)
        {
            if (ObjectBooleanHashMap.isNonSentinel(old[i]))
            {
                this.put(this.toNonSentinel(old[i]), oldValues.get(i));
            }
        }
    }

    protected void allocateTable(int sizeToAllocate)
    {
        this.keys = new Object[sizeToAllocate];
        this.values = new BitSet(sizeToAllocate);
    }

    private static <K> boolean isNonSentinel(K key)
    {
        return key != null && !ObjectBooleanHashMap.isRemovedKey(key);
    }

    public ObjectBooleanHashMap<K> withKeysValues(K key1, boolean value1, K key2, boolean value2)
    {
        this.put(key1, value1);
        this.put(key2, value2);
        return this;
    }

    public ObjectBooleanHashMap<K> withKeysValues(K key1, boolean value1, K key2, boolean value2, K key3, boolean value3)
    {
        this.put(key1, value1);
        this.put(key2, value2);
        this.put(key3, value3);
        return this;
    }

    public ObjectBooleanHashMap<K> withKeysValues(K key1, boolean value1, K key2, boolean value2, K key3, boolean value3, K key4, boolean value4)
    {
        this.put(key1, value1);
        this.put(key2, value2);
        this.put(key3, value3);
        this.put(key4, value4);
        return this;
    }

    private class KeySet implements Set<K>
    {
        @Override
        public boolean equals(Object obj)
        {
            if (obj instanceof Set)
            {
                Set<?> other = (Set<?>) obj;
                if (other.size() == this.size())
                {
                    return this.containsAll(other);
                }
            }
            return false;
        }

        @Override
        public int hashCode()
        {
            int hashCode = 0;
            Object[] table = ObjectBooleanHashMap.this.keys;
            for (int i = 0; i < table.length; i++)
            {
                Object key = table[i];
                if (ObjectBooleanHashMap.isNonSentinel(key))
                {
                    K nonSentinelKey = ObjectBooleanHashMap.this.toNonSentinel(key);
                    hashCode += nonSentinelKey == null ? 0 : nonSentinelKey.hashCode();
                }
            }
            return hashCode;
        }

        @Override
        public int size()
        {
            return ObjectBooleanHashMap.this.size();
        }

        @Override
        public boolean isEmpty()
        {
            return ObjectBooleanHashMap.this.isEmpty();
        }

        @Override
        public boolean contains(Object o)
        {
            return ObjectBooleanHashMap.this.containsKey(o);
        }

        @Override
        public Object[] toArray()
        {
            int size = ObjectBooleanHashMap.this.size();
            Object[] result = new Object[size];
            this.copyKeys(result);
            return result;
        }

        @Override
        public <T> T[] toArray(T[] result)
        {
            int size = ObjectBooleanHashMap.this.size();
            if (result.length < size)
            {
                result = (T[]) Array.newInstance(result.getClass().getComponentType(), size);
            }
            this.copyKeys(result);
            if (size < result.length)
            {
                result[size] = null;
            }
            return result;
        }

        @Override
        public boolean add(K key)
        {
            throw new UnsupportedOperationException("Cannot call add() on " + this.getClass().getSimpleName());
        }

        @Override
        public boolean remove(Object key)
        {
            int oldSize = ObjectBooleanHashMap.this.size();
            ObjectBooleanHashMap.this.removeKey((K) key);
            return oldSize != ObjectBooleanHashMap.this.size();
        }

        @Override
        public boolean containsAll(Collection<?> collection)
        {
            for (Object aCollection : collection)
            {
                if (!ObjectBooleanHashMap.this.containsKey(aCollection))
                {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean addAll(Collection<? extends K> collection)
        {
            throw new UnsupportedOperationException("Cannot call addAll() on " + this.getClass().getSimpleName());
        }

        @Override
        public boolean retainAll(Collection<?> collection)
        {
            int oldSize = ObjectBooleanHashMap.this.size();
            Iterator<K> iterator = this.iterator();
            while (iterator.hasNext())
            {
                K next = iterator.next();
                if (!collection.contains(next))
                {
                    iterator.remove();
                }
            }
            return oldSize != ObjectBooleanHashMap.this.size();
        }

        @Override
        public boolean removeAll(Collection<?> collection)
        {
            int oldSize = ObjectBooleanHashMap.this.size();
            for (Object object : collection)
            {
                ObjectBooleanHashMap.this.removeKey((K) object);
            }
            return oldSize != ObjectBooleanHashMap.this.size();
        }

        @Override
        public void clear()
        {
            ObjectBooleanHashMap.this.clear();
        }

        @Override
        public Iterator<K> iterator()
        {
            return new KeySetIterator();
        }

        private void copyKeys(Object[] result)
        {
            int count = 0;
            for (int i = 0; i < ObjectBooleanHashMap.this.keys.length; i++)
            {
                Object key = ObjectBooleanHashMap.this.keys[i];
                if (ObjectBooleanHashMap.isNonSentinel(key))
                {
                    result[count++] = ObjectBooleanHashMap.this.keys[i];
                }
            }
        }
    }

    private class KeySetIterator implements Iterator<K>
    {
        private int count;
        private int position;
        private K currentKey;
        private boolean isCurrentKeySet;

        @Override
        public boolean hasNext()
        {
            return this.count < ObjectBooleanHashMap.this.size();
        }

        @Override
        public K next()
        {
            if (!this.hasNext())
            {
                throw new NoSuchElementException();
            }
            this.count++;
            Object[] keys = ObjectBooleanHashMap.this.keys;
            while (!ObjectBooleanHashMap.isNonSentinel(keys[this.position]))
            {
                this.position++;
            }
            this.currentKey = (K) ObjectBooleanHashMap.this.keys[this.position];
            this.isCurrentKeySet = true;
            this.position++;
            return ObjectBooleanHashMap.this.toNonSentinel(this.currentKey);
        }

        @Override
        public void remove()
        {
            if (!this.isCurrentKeySet)
            {
                throw new IllegalStateException();
            }

            this.isCurrentKeySet = false;
            this.count--;

            if (ObjectBooleanHashMap.isNonSentinel(this.currentKey))
            {
                int index = this.position - 1;
                ObjectBooleanHashMap.this.removeKeyAtIndex(ObjectBooleanHashMap.this.toNonSentinel(this.currentKey), index);
            }
            else
            {
                ObjectBooleanHashMap.this.removeKey(this.currentKey);
            }
        }
    }

    private class ValuesCollection implements MutableBooleanCollection
    {
        @Override
        public boolean add(boolean element)
        {
            throw new UnsupportedOperationException("Cannot call add() on " + this.getClass().getSimpleName());
        }

        @Override
        public boolean addAll(boolean... source)
        {
            throw new UnsupportedOperationException("Cannot call addAll() on " + this.getClass().getSimpleName());
        }

        @Override
        public boolean addAll(BooleanIterable source)
        {
            throw new UnsupportedOperationException("Cannot call addAll() on " + this.getClass().getSimpleName());
        }

        @Override
        public boolean remove(boolean element)
        {
            for (int i = 0; i < ObjectBooleanHashMap.this.values.size(); i++)
            {
                if (ObjectBooleanHashMap.this.values.get(i) == element && ObjectBooleanHashMap.isNonSentinel(ObjectBooleanHashMap.this.keys[i]))
                {
                    ObjectBooleanHashMap.this.removeKey(ObjectBooleanHashMap.this.toNonSentinel(ObjectBooleanHashMap.this.keys[i]));
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean removeAll(BooleanIterable source)
        {
            int oldSize = ObjectBooleanHashMap.this.size();

            BooleanIterator iterator = source.booleanIterator();
            while (iterator.hasNext())
            {
                this.remove(iterator.next());
            }
            return oldSize != ObjectBooleanHashMap.this.size();
        }

        @Override
        public boolean removeAll(boolean... source)
        {
            int oldSize = ObjectBooleanHashMap.this.size();

            for (boolean item : source)
            {
                this.remove(item);
            }
            return oldSize != ObjectBooleanHashMap.this.size();
        }

        @Override
        public boolean retainAll(BooleanIterable source)
        {
            int oldSize = ObjectBooleanHashMap.this.size();
            BooleanSet sourceSet = source instanceof BooleanSet ? (BooleanSet) source : source.toSet();
            ObjectBooleanHashMap<K> retained = ObjectBooleanHashMap.this.select((object, value) -> sourceSet.contains(value));
            if (retained.size() != oldSize)
            {
                ObjectBooleanHashMap.this.keys = retained.keys;
                ObjectBooleanHashMap.this.values = retained.values;
                ObjectBooleanHashMap.this.occupiedWithData = retained.occupiedWithData;
                ObjectBooleanHashMap.this.occupiedWithSentinels = retained.occupiedWithSentinels;
                return true;
            }
            return false;
        }

        @Override
        public boolean retainAll(boolean... source)
        {
            return this.retainAll(BooleanHashSet.newSetWith(source));
        }

        @Override
        public void clear()
        {
            ObjectBooleanHashMap.this.clear();
        }

        @Override
        public MutableBooleanCollection with(boolean element)
        {
            throw new UnsupportedOperationException("Cannot call with() on " + this.getClass().getSimpleName());
        }

        @Override
        public MutableBooleanCollection without(boolean element)
        {
            throw new UnsupportedOperationException("Cannot call without() on " + this.getClass().getSimpleName());
        }

        @Override
        public MutableBooleanCollection withAll(BooleanIterable elements)
        {
            throw new UnsupportedOperationException("Cannot call withAll() on " + this.getClass().getSimpleName());
        }

        @Override
        public MutableBooleanCollection withoutAll(BooleanIterable elements)
        {
            throw new UnsupportedOperationException("Cannot call withoutAll() on " + this.getClass().getSimpleName());
        }

        @Override
        public MutableBooleanCollection asUnmodifiable()
        {
            return UnmodifiableBooleanCollection.of(this);
        }

        @Override
        public MutableBooleanCollection asSynchronized()
        {
            return SynchronizedBooleanCollection.of(this);
        }

        @Override
        public ImmutableBooleanCollection toImmutable()
        {
            return BooleanLists.immutable.withAll(this);
        }

        @Override
        public MutableBooleanCollection select(BooleanPredicate predicate)
        {
            return ObjectBooleanHashMap.this.select(predicate);
        }

        @Override
        public MutableBooleanCollection reject(BooleanPredicate predicate)
        {
            return ObjectBooleanHashMap.this.reject(predicate);
        }

        @Override
        public MutableBooleanIterator booleanIterator()
        {
            return ObjectBooleanHashMap.this.booleanIterator();
        }

        @Override
        public boolean[] toArray()
        {
            return ObjectBooleanHashMap.this.toArray();
        }

        @Override
        public boolean[] toArray(boolean[] target)
        {
            return ObjectBooleanHashMap.this.toArray(target);
        }

        @Override
        public <V> MutableCollection<V> collect(BooleanToObjectFunction<? extends V> function)
        {
            return ObjectBooleanHashMap.this.collect(function);
        }

        @Override
        public boolean contains(boolean value)
        {
            return ObjectBooleanHashMap.this.containsValue(value);
        }

        @Override
        public boolean containsAll(boolean... source)
        {
            return ObjectBooleanHashMap.this.containsAll(source);
        }

        @Override
        public boolean containsAll(BooleanIterable source)
        {
            return ObjectBooleanHashMap.this.containsAll(source);
        }

        @Override
        public void forEach(BooleanProcedure procedure)
        {
            this.each(procedure);
        }

        @Override
        public void each(BooleanProcedure procedure)
        {
            ObjectBooleanHashMap.this.forEach(procedure);
        }

        @Override
        public boolean detectIfNone(BooleanPredicate predicate, boolean ifNone)
        {
            return ObjectBooleanHashMap.this.detectIfNone(predicate, ifNone);
        }

        @Override
        public int count(BooleanPredicate predicate)
        {
            return ObjectBooleanHashMap.this.count(predicate);
        }

        @Override
        public boolean anySatisfy(BooleanPredicate predicate)
        {
            return ObjectBooleanHashMap.this.anySatisfy(predicate);
        }

        @Override
        public boolean allSatisfy(BooleanPredicate predicate)
        {
            return ObjectBooleanHashMap.this.allSatisfy(predicate);
        }

        @Override
        public boolean noneSatisfy(BooleanPredicate predicate)
        {
            return ObjectBooleanHashMap.this.noneSatisfy(predicate);
        }

        @Override
        public MutableBooleanList toList()
        {
            return ObjectBooleanHashMap.this.toList();
        }

        @Override
        public MutableBooleanSet toSet()
        {
            return ObjectBooleanHashMap.this.toSet();
        }

        @Override
        public MutableBooleanBag toBag()
        {
            return ObjectBooleanHashMap.this.toBag();
        }

        @Override
        public LazyBooleanIterable asLazy()
        {
            return new LazyBooleanIterableAdapter(this);
        }

        @Override
        public <T> T injectInto(T injectedValue, ObjectBooleanToObjectFunction<? super T, ? extends T> function)
        {
            return ObjectBooleanHashMap.this.injectInto(injectedValue, function);
        }

        @Override
        public RichIterable<BooleanIterable> chunk(int size)
        {
            return ObjectBooleanHashMap.this.chunk(size);
        }

        @Override
        public int size()
        {
            return ObjectBooleanHashMap.this.size();
        }

        @Override
        public boolean isEmpty()
        {
            return ObjectBooleanHashMap.this.isEmpty();
        }

        @Override
        public boolean notEmpty()
        {
            return ObjectBooleanHashMap.this.notEmpty();
        }

        @Override
        public String makeString()
        {
            return this.makeString(", ");
        }

        @Override
        public String makeString(String separator)
        {
            return this.makeString("", separator, "");
        }

        @Override
        public String makeString(String start, String separator, String end)
        {
            Appendable stringBuilder = new StringBuilder();
            this.appendString(stringBuilder, start, separator, end);
            return stringBuilder.toString();
        }

        @Override
        public void appendString(Appendable appendable)
        {
            this.appendString(appendable, ", ");
        }

        @Override
        public void appendString(Appendable appendable, String separator)
        {
            this.appendString(appendable, "", separator, "");
        }

        @Override
        public void appendString(Appendable appendable, String start, String separator, String end)
        {
            try
            {
                appendable.append(start);

                boolean first = true;

                for (int i = 0; i < ObjectBooleanHashMap.this.keys.length; i++)
                {
                    Object key = ObjectBooleanHashMap.this.keys[i];
                    if (ObjectBooleanHashMap.isNonSentinel(key))
                    {
                        if (!first)
                        {
                            appendable.append(separator);
                        }
                        appendable.append(String.valueOf(ObjectBooleanHashMap.this.values.get(i)));
                        first = false;
                    }
                }
                appendable.append(end);
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    private class InternalBooleanIterator implements MutableBooleanIterator
    {
        private int count;
        private int position;

        @Override
        public boolean hasNext()
        {
            return this.count != ObjectBooleanHashMap.this.size();
        }

        @Override
        public boolean next()
        {
            if (!this.hasNext())
            {
                throw new NoSuchElementException();
            }

            Object[] keys = ObjectBooleanHashMap.this.keys;
            while (!ObjectBooleanHashMap.isNonSentinel(keys[this.position]))
            {
                this.position++;
            }
            boolean result = ObjectBooleanHashMap.this.values.get(this.position);
            this.count++;
            this.position++;
            return result;
        }

        @Override
        public void remove()
        {
            if (this.position == 0 || !ObjectBooleanHashMap.isNonSentinel(ObjectBooleanHashMap.this.keys[this.position - 1]))
            {
                throw new IllegalStateException();
            }
            ObjectBooleanHashMap.this.remove(ObjectBooleanHashMap.this.keys[this.position - 1]);
            this.count--;
        }
    }

    private class KeysView extends AbstractLazyIterable<K>
    {
        @Override
        public void each(Procedure<? super K> procedure)
        {
            ObjectBooleanHashMap.this.forEachKey(procedure);
        }

        @Override
        public void forEachWithIndex(ObjectIntProcedure<? super K> objectIntProcedure)
        {
            int index = 0;
            for (int i = 0; i < ObjectBooleanHashMap.this.keys.length; i++)
            {
                if (ObjectBooleanHashMap.isNonSentinel(ObjectBooleanHashMap.this.keys[i]))
                {
                    objectIntProcedure.value(ObjectBooleanHashMap.this.toNonSentinel(ObjectBooleanHashMap.this.keys[i]), index);
                    index++;
                }
            }
        }

        @Override
        public <P> void forEachWith(Procedure2<? super K, ? super P> procedure, P parameter)
        {
            for (int i = 0; i < ObjectBooleanHashMap.this.keys.length; i++)
            {
                if (ObjectBooleanHashMap.isNonSentinel(ObjectBooleanHashMap.this.keys[i]))
                {
                    procedure.value(ObjectBooleanHashMap.this.toNonSentinel(ObjectBooleanHashMap.this.keys[i]), parameter);
                }
            }
        }

        @Override
        public Iterator<K> iterator()
        {
            return new InternalKeysViewIterator();
        }

        public class InternalKeysViewIterator implements Iterator<K>
        {
            private int count;
            private int position;

            @Override
            public boolean hasNext()
            {
                return this.count != ObjectBooleanHashMap.this.size();
            }

            @Override
            public K next()
            {
                if (!this.hasNext())
                {
                    throw new NoSuchElementException();
                }

                Object[] keys = ObjectBooleanHashMap.this.keys;
                while (!ObjectBooleanHashMap.isNonSentinel(keys[this.position]))
                {
                    this.position++;
                }
                K result = (K) ObjectBooleanHashMap.this.keys[this.position];
                this.count++;
                this.position++;
                return result;
            }

            @Override
            public void remove()
            {
                throw new UnsupportedOperationException("Cannot call remove() on " + this.getClass().getSimpleName());
            }
        }
    }

    private class KeyValuesView extends AbstractLazyIterable<ObjectBooleanPair<K>>
    {
        @Override
        public void each(Procedure<? super ObjectBooleanPair<K>> procedure)
        {
            for (int i = 0; i < ObjectBooleanHashMap.this.keys.length; i++)
            {
                if (ObjectBooleanHashMap.isNonSentinel(ObjectBooleanHashMap.this.keys[i]))
                {
                    procedure.value(PrimitiveTuples.pair(ObjectBooleanHashMap.this.toNonSentinel(ObjectBooleanHashMap.this.keys[i]), ObjectBooleanHashMap.this.values.get(i)));
                }
            }
        }

        @Override
        public void forEachWithIndex(ObjectIntProcedure<? super ObjectBooleanPair<K>> objectIntProcedure)
        {
            int index = 0;
            for (int i = 0; i < ObjectBooleanHashMap.this.keys.length; i++)
            {
                if (ObjectBooleanHashMap.isNonSentinel(ObjectBooleanHashMap.this.keys[i]))
                {
                    objectIntProcedure.value(PrimitiveTuples.pair(ObjectBooleanHashMap.this.toNonSentinel(ObjectBooleanHashMap.this.keys[i]), ObjectBooleanHashMap.this.values.get(i)), index);
                    index++;
                }
            }
        }

        @Override
        public <P> void forEachWith(Procedure2<? super ObjectBooleanPair<K>, ? super P> procedure, P parameter)
        {
            for (int i = 0; i < ObjectBooleanHashMap.this.keys.length; i++)
            {
                if (ObjectBooleanHashMap.isNonSentinel(ObjectBooleanHashMap.this.keys[i]))
                {
                    procedure.value(PrimitiveTuples.pair(ObjectBooleanHashMap.this.toNonSentinel(ObjectBooleanHashMap.this.keys[i]), ObjectBooleanHashMap.this.values.get(i)), parameter);
                }
            }
        }

        @Override
        public Iterator<ObjectBooleanPair<K>> iterator()
        {
            return new InternalKeyValuesIterator();
        }

        public class InternalKeyValuesIterator implements Iterator<ObjectBooleanPair<K>>
        {
            private int count;
            private int position;

            @Override
            public boolean hasNext()
            {
                return this.count != ObjectBooleanHashMap.this.size();
            }

            @Override
            public ObjectBooleanPair<K> next()
            {
                if (!this.hasNext())
                {
                    throw new NoSuchElementException();
                }

                Object[] keys = ObjectBooleanHashMap.this.keys;
                while (!ObjectBooleanHashMap.isNonSentinel(keys[this.position]))
                {
                    this.position++;
                }
                ObjectBooleanPair<K> result = PrimitiveTuples.pair(ObjectBooleanHashMap.this.toNonSentinel(ObjectBooleanHashMap.this.keys[this.position]), ObjectBooleanHashMap.this.values.get(this.position));
                this.count++;
                this.position++;
                return result;
            }

            @Override
            public void remove()
            {
                throw new UnsupportedOperationException("Cannot call remove() on " + this.getClass().getSimpleName());
            }
        }
    }
}
