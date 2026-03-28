/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.bimap.mutable;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.bag.primitive.MutableBooleanBag;
import org.eclipse.collections.api.bag.primitive.MutableByteBag;
import org.eclipse.collections.api.bag.primitive.MutableCharBag;
import org.eclipse.collections.api.bag.primitive.MutableDoubleBag;
import org.eclipse.collections.api.bag.primitive.MutableFloatBag;
import org.eclipse.collections.api.bag.primitive.MutableIntBag;
import org.eclipse.collections.api.bag.primitive.MutableLongBag;
import org.eclipse.collections.api.bag.primitive.MutableShortBag;
import org.eclipse.collections.api.bimap.ImmutableBiMap;
import org.eclipse.collections.api.bimap.MutableBiMap;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function0;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.function.primitive.BooleanFunction;
import org.eclipse.collections.api.block.function.primitive.ByteFunction;
import org.eclipse.collections.api.block.function.primitive.CharFunction;
import org.eclipse.collections.api.block.function.primitive.DoubleFunction;
import org.eclipse.collections.api.block.function.primitive.FloatFunction;
import org.eclipse.collections.api.block.function.primitive.IntFunction;
import org.eclipse.collections.api.block.function.primitive.LongFunction;
import org.eclipse.collections.api.block.function.primitive.ShortFunction;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.factory.BiMaps;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.map.primitive.MutableObjectDoubleMap;
import org.eclipse.collections.api.map.primitive.MutableObjectLongMap;
import org.eclipse.collections.api.multimap.set.MutableSetMultimap;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.partition.set.PartitionMutableSet;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.bimap.AbstractBiMap;
import org.eclipse.collections.impl.block.factory.Predicates;
import org.eclipse.collections.impl.block.procedure.CollectionAddProcedure;
import org.eclipse.collections.impl.block.procedure.PartitionProcedure;
import org.eclipse.collections.impl.block.procedure.SelectInstancesOfProcedure;
import org.eclipse.collections.impl.list.fixed.ArrayAdapter;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.multimap.set.UnifiedSetMultimap;
import org.eclipse.collections.impl.partition.set.PartitionUnifiedSet;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.eclipse.collections.impl.utility.Iterate;
import org.eclipse.collections.impl.utility.MapIterate;

abstract class AbstractMutableBiMap<K, V> extends AbstractBiMap<K, V> implements MutableBiMap<K, V>
{
    private UnifiedMap<K, V> delegate;
    private AbstractMutableBiMap<V, K> inverse;

    AbstractMutableBiMap(Map<K, V> map)
    {
        this.delegate = UnifiedMap.newMap();
        this.inverse = new Inverse<>(UnifiedMap.newMap(), this);
        this.putAll(map);
    }

    AbstractMutableBiMap(Map<K, V> delegate, Map<V, K> inverse)
    {
        this.checkNull(delegate, inverse);
        this.checkSame(delegate, inverse);
        this.delegate = UnifiedMap.newMap(delegate);
        this.inverse = new Inverse<>(inverse, this);
    }

    private AbstractMutableBiMap(Map<K, V> delegate, AbstractMutableBiMap<V, K> valuesToKeys)
    {
        this.delegate = UnifiedMap.newMap(delegate);
        this.inverse = valuesToKeys;
    }

    private void checkNull(Map<K, V> delegate, Map<V, K> inverse)
    {
        if (delegate == null || inverse == null)
        {
            throw new IllegalArgumentException("The delegate maps cannot be null.");
        }
    }

    private void checkSame(Map<K, V> keysToValues, Map<V, K> valuesToKeys)
    {
        if (keysToValues == valuesToKeys)
        {
            throw new IllegalArgumentException("The delegate maps cannot be same.");
        }
    }

    private static boolean nullSafeEquals(Object value, Object other)
    {
        if (value == null)
        {
            return other == null;
        }
        return other == value || value.equals(other);
    }

    @Override
    protected UnifiedMap<K, V> getDelegate()
    {
        return this.delegate;
    }

    @Override
    protected UnifiedMap<V, K> getInverse()
    {
        return this.inverse.delegate;
    }

    @Override
    public HashBiMap<K, V> newEmpty()
    {
        return new HashBiMap<>();
    }

    @Override
    public MutableBiMap<K, V> withKeyValue(K key, V value)
    {
        this.put(key, value);
        return this;
    }

    @Override
    public MutableBiMap<K, V> withAllKeyValues(Iterable<? extends Pair<? extends K, ? extends V>> keyValues)
    {
        for (Pair<? extends K, ? extends V> keyVal : keyValues)
        {
            this.put(keyVal.getOne(), keyVal.getTwo());
        }
        return this;
    }

    @Override
    public MutableBiMap<K, V> withAllKeyValueArguments(Pair<? extends K, ? extends V>... keyValuePairs)
    {
        return this.withAllKeyValues(ArrayAdapter.adapt(keyValuePairs));
    }

    @Override
    public MutableBiMap<K, V> withoutKey(K key)
    {
        this.removeKey(key);
        return this;
    }

    @Override
    public MutableBiMap<K, V> withoutAllKeys(Iterable<? extends K> keys)
    {
        for (K key : keys)
        {
            this.removeKey(key);
        }
        return this;
    }

    @Override
    public MutableBiMap<V, K> inverse()
    {
        return this.inverse;
    }

    @Override
    public MutableSetMultimap<V, K> flip()
    {
        // TODO: We could optimize this since we know the values are unique
        return MapIterate.flip(this);
    }

    @Override
    public MutableBiMap<V, K> flipUniqueValues()
    {
        return new HashBiMap<>(this.inverse());
    }

    @Override
    public V put(K key, V value)
    {
        if (this.inverse.delegate.containsKey(value))
        {
            if (AbstractMutableBiMap.nullSafeEquals(key, this.inverse.delegate.get(value)))
            {
                return value;
            }
            throw new IllegalArgumentException("Value " + value + " already exists in map!");
        }

        boolean containsKey = this.delegate.containsKey(key);

        if (containsKey)
        {
            V oldValue = this.delegate.put(key, value);
            K oldKeyToPreserve = this.inverse.delegate.removeKey(oldValue);
            this.inverse.delegate.put(value, oldKeyToPreserve);
            return oldValue;
        }

        V put = this.delegate.put(key, value);
        this.inverse.delegate.put(value, key);
        return put;
    }

    @Override
    public V forcePut(K key, V value)
    {
        boolean containsValue = this.inverse.delegate.containsKey(value);
        if (containsValue)
        {
            if (AbstractMutableBiMap.nullSafeEquals(key, this.inverse.delegate.get(value)))
            {
                return value;
            }
        }

        boolean containsKey = this.delegate.containsKey(key);
        if (containsValue)
        {
            V oldValueToPreserve = this.delegate.get(this.inverse.delegate.get(value));
            V put = this.delegate.put(key, oldValueToPreserve);

            if (containsKey)
            {
                K oldKeyToPreserve = this.inverse.delegate.removeKey(put);
                K oldKey = this.inverse.delegate.put(value, oldKeyToPreserve);
                this.delegate.removeKey(oldKey);
                return put;
            }
            K oldKey = this.inverse.delegate.put(value, key);
            this.delegate.removeKey(oldKey);
            return put;
        }

        V put = this.delegate.put(key, value);

        if (containsKey)
        {
            this.inverse.delegate.removeKey(put);
        }

        this.inverse.delegate.put(value, key);
        return put;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map)
    {
        map.forEach(this::put);
    }

    @Override
    public V remove(Object key)
    {
        if (!this.delegate.containsKey(key))
        {
            return null;
        }
        V oldValue = this.delegate.remove(key);
        this.inverse.delegate.remove(oldValue);
        return oldValue;
    }

    @Override
    public V removeKey(K key)
    {
        return this.remove(key);
    }

    @Override
    public void clear()
    {
        this.delegate.clear();
        this.inverse.delegate.clear();
    }

    @Override
    public V getIfAbsentPut(K key, V value)
    {
        V oldValue = this.delegate.get(key);

        if (oldValue != null || this.delegate.containsKey(key))
        {
            return oldValue;
        }

        this.put(key, value);
        return value;
    }

    @Override
    public V getIfAbsentPut(K key, Function0<? extends V> function)
    {
        V value = this.delegate.get(key);

        if (value != null || this.delegate.containsKey(key))
        {
            return value;
        }

        V newValue = function.value();
        this.put(key, newValue);
        return newValue;
    }

    @Override
    public <P> V getIfAbsentPutWith(K key, Function<? super P, ? extends V> function, P parameter)
    {
        V value = this.delegate.get(key);

        if (value != null || this.delegate.containsKey(key))
        {
            return value;
        }

        V newValue = function.valueOf(parameter);
        this.put(key, newValue);
        return newValue;
    }

    @Override
    public V getIfAbsentPutWithKey(K key, Function<? super K, ? extends V> function)
    {
        V value = this.delegate.get(key);

        if (value != null || this.delegate.containsKey(key))
        {
            return value;
        }

        V newValue = function.valueOf(key);
        this.put(key, newValue);
        return newValue;
    }

    @Override
    public V updateValue(K key, Function0<? extends V> factory, Function<? super V, ? extends V> function)
    {
        if (this.delegate.containsKey(key))
        {
            V newValue = function.valueOf(this.delegate.get(key));
            this.put(key, newValue);
            return newValue;
        }

        V newValue = function.valueOf(factory.value());
        this.put(key, newValue);
        return newValue;
    }

    @Override
    public <P> V updateValueWith(K key, Function0<? extends V> factory, Function2<? super V, ? super P, ? extends V> function, P parameter)
    {
        if (this.delegate.containsKey(key))
        {
            V newValue = function.value(this.delegate.get(key), parameter);
            this.put(key, newValue);
            return newValue;
        }

        V newValue = function.value(factory.value(), parameter);
        this.put(key, newValue);
        return newValue;
    }

    @Override
    public Set<K> keySet()
    {
        return new KeySet();
    }

    @Override
    public Collection<V> values()
    {
        return new ValuesCollection();
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet()
    {
        return new EntrySet();
    }

    @Override
    public Iterator<V> iterator()
    {
        return new InternalIterator();
    }

    @Override
    public ImmutableBiMap<K, V> toImmutable()
    {
        return BiMaps.immutable.withAll(this.delegate);
    }

    @Override
    public MutableBiMap<K, V> asSynchronized()
    {
        return SynchronizedBiMap.of(this);
    }

    @Override
    public MutableBiMap<K, V> asUnmodifiable()
    {
        return UnmodifiableBiMap.of(this);
    }

    @Override
    public MutableBiMap<K, V> clone()
    {
        return new HashBiMap<>(this);
    }

    @Override
    public MutableBiMap<K, V> tap(Procedure<? super V> procedure)
    {
        this.forEach(procedure);
        return this;
    }

    @Override
    public HashBiMap<K, V> select(Predicate2<? super K, ? super V> predicate)
    {
        HashBiMap<K, V> result = HashBiMap.newMap();
        this.delegate.forEachKeyValue((key, value) -> {
            if (predicate.accept(key, value))
            {
                result.put(key, value);
            }
        });
        return result;
    }

    @Override
    public HashBiMap<K, V> reject(Predicate2<? super K, ? super V> predicate)
    {
        HashBiMap<K, V> result = HashBiMap.newMap();
        this.delegate.forEachKeyValue((key, value) -> {
            if (!predicate.accept(key, value))
            {
                result.put(key, value);
            }
        });
        return result;
    }

    @Override
    public <K2, V2> HashBiMap<K2, V2> collect(Function2<? super K, ? super V, Pair<K2, V2>> function)
    {
        HashBiMap<K2, V2> result = HashBiMap.newMap();
        this.delegate.forEachKeyValue((key, value) -> {
            Pair<K2, V2> pair = function.value(key, value);
            result.put(pair.getOne(), pair.getTwo());
        });
        return result;
    }

    @Override
    public <R> HashBiMap<K, R> collectValues(Function2<? super K, ? super V, ? extends R> function)
    {
        HashBiMap<K, R> result = HashBiMap.newMap();
        this.delegate.forEachKeyValue((key, value) -> result.put(key, function.value(key, value)));
        return result;
    }

    @Override
    public <R> MutableBiMap<R, V> collectKeysUnique(Function2<? super K, ? super V, ? extends R> function)
    {
        return MapIterate.collectKeysUnique(this.delegate, function, BiMaps.mutable.empty());
    }

    @Override
    public <VV> MutableBag<VV> collect(Function<? super V, ? extends VV> function)
    {
        return this.delegate.collect(function);
    }

    @Override
    public <P, VV> MutableBag<VV> collectWith(Function2<? super V, ? super P, ? extends VV> function, P parameter)
    {
        return this.delegate.collectWith(function, parameter);
    }

    @Override
    public <VV> MutableBag<VV> flatCollect(Function<? super V, ? extends Iterable<VV>> function)
    {
        return this.delegate.flatCollect(function);
    }

    @Override
    public <P, VV> MutableBag<VV> flatCollectWith(Function2<? super V, ? super P, ? extends Iterable<VV>> function, P parameter)
    {
        return this.delegate.flatCollectWith(function, parameter);
    }

    @Override
    public MutableBooleanBag collectBoolean(BooleanFunction<? super V> booleanFunction)
    {
        return this.delegate.collectBoolean(booleanFunction);
    }

    @Override
    public MutableByteBag collectByte(ByteFunction<? super V> byteFunction)
    {
        return this.delegate.collectByte(byteFunction);
    }

    @Override
    public MutableCharBag collectChar(CharFunction<? super V> charFunction)
    {
        return this.delegate.collectChar(charFunction);
    }

    @Override
    public MutableDoubleBag collectDouble(DoubleFunction<? super V> doubleFunction)
    {
        return this.delegate.collectDouble(doubleFunction);
    }

    @Override
    public MutableFloatBag collectFloat(FloatFunction<? super V> floatFunction)
    {
        return this.delegate.collectFloat(floatFunction);
    }

    @Override
    public MutableIntBag collectInt(IntFunction<? super V> intFunction)
    {
        return this.delegate.collectInt(intFunction);
    }

    @Override
    public MutableLongBag collectLong(LongFunction<? super V> longFunction)
    {
        return this.delegate.collectLong(longFunction);
    }

    @Override
    public MutableShortBag collectShort(ShortFunction<? super V> shortFunction)
    {
        return this.delegate.collectShort(shortFunction);
    }

    @Override
    public <VV> MutableBag<VV> collectIf(Predicate<? super V> predicate, Function<? super V, ? extends VV> function)
    {
        return this.delegate.collectIf(predicate, function);
    }

    /**
     * @deprecated in 8.0. Use {@link OrderedIterable#zipWithIndex()} instead.
     */
    @Override
    @Deprecated
    public MutableSet<Pair<V, Integer>> zipWithIndex()
    {
        return this.delegate.zipWithIndex(Sets.mutable.withInitialCapacity(this.size()));
    }

    @Override
    public <VV> MutableSetMultimap<VV, V> groupBy(Function<? super V, ? extends VV> function)
    {
        return this.delegate.groupBy(function, new UnifiedSetMultimap<>());
    }

    @Override
    public <VV> MutableSetMultimap<VV, V> groupByEach(Function<? super V, ? extends Iterable<VV>> function)
    {
        return this.delegate.groupByEach(function, new UnifiedSetMultimap<>());
    }

    /**
     * @deprecated in 8.0. Use {@link OrderedIterable#zip(Iterable)} instead.
     */
    @Override
    @Deprecated
    public <S> MutableSet<Pair<V, S>> zip(Iterable<S> that)
    {
        if (that instanceof Collection || that instanceof RichIterable)
        {
            int thatSize = Iterate.sizeOf(that);
            MutableSet<Pair<V, S>> target = UnifiedSet.newSet(Math.min(this.size(), thatSize));
            return this.delegate.zip(that, target);
        }
        return this.delegate.zip(that, Sets.mutable.empty());
    }

    @Override
    public MutableSet<V> select(Predicate<? super V> predicate)
    {
        return this.delegate.select(predicate, new UnifiedSet<>());
    }

    @Override
    public <P> MutableSet<V> selectWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.delegate.selectWith(predicate, parameter, new UnifiedSet<>());
    }

    @Override
    public MutableSet<V> reject(Predicate<? super V> predicate)
    {
        return this.delegate.reject(predicate, new UnifiedSet<>());
    }

    @Override
    public <P> MutableSet<V> rejectWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.delegate.rejectWith(predicate, parameter, new UnifiedSet<>());
    }

    @Override
    public PartitionMutableSet<V> partition(Predicate<? super V> predicate)
    {
        PartitionMutableSet<V> result = new PartitionUnifiedSet<>();
        this.inverse.forEachKey(new PartitionProcedure<>(predicate, result));
        return result;
    }

    @Override
    public <P> PartitionMutableSet<V> partitionWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.partition(Predicates.bind(predicate, parameter));
    }

    @Override
    public void forEachValue(Procedure<? super V> procedure)
    {
        this.inverse.delegate.forEachKey(procedure);
    }

    @Override
    public <VV> MutableBiMap<VV, V> groupByUniqueKey(Function<? super V, ? extends VV> function)
    {
        return new HashBiMap<>(this.delegate.groupByUniqueKey(function));
    }

    @Override
    public <V1> MutableObjectLongMap<V1> sumByInt(Function<? super V, ? extends V1> groupBy, IntFunction<? super V> function)
    {
        return this.delegate.sumByInt(groupBy, function);
    }

    @Override
    public <V1> MutableObjectDoubleMap<V1> sumByFloat(Function<? super V, ? extends V1> groupBy, FloatFunction<? super V> function)
    {
        return this.delegate.sumByFloat(groupBy, function);
    }

    @Override
    public <V1> MutableObjectLongMap<V1> sumByLong(Function<? super V, ? extends V1> groupBy, LongFunction<? super V> function)
    {
        return this.delegate.sumByLong(groupBy, function);
    }

    @Override
    public <V1> MutableObjectDoubleMap<V1> sumByDouble(Function<? super V, ? extends V1> groupBy, DoubleFunction<? super V> function)
    {
        return this.delegate.sumByDouble(groupBy, function);
    }

    @Override
    public <S> MutableSet<S> selectInstancesOf(Class<S> clazz)
    {
        MutableSet<S> result = new UnifiedSet<>();
        this.inverse.forEachKey(new SelectInstancesOfProcedure<>(clazz, result));
        return result;
    }

    public void writeExternal(ObjectOutput out) throws IOException
    {
        this.delegate.writeExternal(out);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
    {
        this.delegate = UnifiedMap.newMap();
        this.delegate.readExternal(in);
        UnifiedMap<V, K> inverseDelegate = UnifiedMap.newMap();
        this.delegate.forEachKeyValue((key, value) -> inverseDelegate.put(value, key));
        this.inverse = new Inverse<>(inverseDelegate, this);
    }

    private class InternalIterator implements Iterator<V>
    {
        private final Iterator<V> iterator = AbstractMutableBiMap.this.delegate.iterator();
        private V currentValue;

        @Override
        public boolean hasNext()
        {
            return this.iterator.hasNext();
        }

        @Override
        public V next()
        {
            V next = this.iterator.next();
            this.currentValue = next;
            return next;
        }

        @Override
        public void remove()
        {
            this.iterator.remove();
            AbstractMutableBiMap.this.inverse.delegate.remove(this.currentValue);
        }
    }

    protected class KeySet implements Set<K>, Serializable
    {
        @Override
        public boolean equals(Object obj)
        {
            return AbstractMutableBiMap.this.delegate.keySet().equals(obj);
        }

        @Override
        public int hashCode()
        {
            return AbstractMutableBiMap.this.delegate.keySet().hashCode();
        }

        @Override
        public int size()
        {
            return AbstractMutableBiMap.this.size();
        }

        @Override
        public boolean isEmpty()
        {
            return AbstractMutableBiMap.this.isEmpty();
        }

        @Override
        public boolean contains(Object key)
        {
            return AbstractMutableBiMap.this.delegate.containsKey(key);
        }

        @Override
        public Object[] toArray()
        {
            return AbstractMutableBiMap.this.delegate.keySet().toArray();
        }

        @Override
        public <T> T[] toArray(T[] a)
        {
            return AbstractMutableBiMap.this.delegate.keySet().toArray(a);
        }

        @Override
        public boolean add(K key)
        {
            throw new UnsupportedOperationException("Cannot call add() on " + this.getClass().getSimpleName());
        }

        @Override
        public boolean remove(Object key)
        {
            int oldSize = AbstractMutableBiMap.this.size();
            AbstractMutableBiMap.this.remove(key);
            return AbstractMutableBiMap.this.size() != oldSize;
        }

        @Override
        public boolean containsAll(Collection<?> source)
        {
            for (Object key : source)
            {
                if (!AbstractMutableBiMap.this.delegate.containsKey(key))
                {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean addAll(Collection<? extends K> source)
        {
            throw new UnsupportedOperationException("Cannot call addAll() on " + this.getClass().getSimpleName());
        }

        @Override
        public boolean retainAll(Collection<?> collection)
        {
            int oldSize = AbstractMutableBiMap.this.size();
            Iterator<K> iterator = this.iterator();
            while (iterator.hasNext())
            {
                K next = iterator.next();
                if (!collection.contains(next))
                {
                    this.remove(next);
                }
            }
            return oldSize != AbstractMutableBiMap.this.size();
        }

        @Override
        public boolean removeAll(Collection<?> collection)
        {
            int oldSize = AbstractMutableBiMap.this.size();
            for (Object object : collection)
            {
                AbstractMutableBiMap.this.remove(object);
            }
            return oldSize != AbstractMutableBiMap.this.size();
        }

        @Override
        public void clear()
        {
            AbstractMutableBiMap.this.clear();
        }

        @Override
        public void forEach(Consumer<? super K> action)
        {
            AbstractMutableBiMap.this.forEachKey(action::accept);
        }

        @Override
        public Iterator<K> iterator()
        {
            return AbstractMutableBiMap.this.inverse().iterator();
        }

        @Override
        public String toString()
        {
            return AbstractMutableBiMap.this.delegate.keySet().toString();
        }

        protected Object writeReplace()
        {
            MutableSet<K> replace = Sets.mutable.withInitialCapacity(AbstractMutableBiMap.this.size());
            AbstractMutableBiMap.this.forEachKey(CollectionAddProcedure.on(replace));
            return replace;
        }
    }

    protected class ValuesCollection implements Collection<V>
    {
        @Override
        public int size()
        {
            return AbstractMutableBiMap.this.size();
        }

        @Override
        public boolean isEmpty()
        {
            return AbstractMutableBiMap.this.isEmpty();
        }

        @Override
        public boolean contains(Object key)
        {
            return AbstractMutableBiMap.this.inverse.delegate.containsKey(key);
        }

        @Override
        public Object[] toArray()
        {
            return AbstractMutableBiMap.this.delegate.values().toArray();
        }

        @Override
        public <T> T[] toArray(T[] a)
        {
            return AbstractMutableBiMap.this.delegate.values().toArray(a);
        }

        @Override
        public boolean add(V v)
        {
            throw new UnsupportedOperationException("Cannot call add() on " + this.getClass().getSimpleName());
        }

        @Override
        public boolean remove(Object value)
        {
            int oldSize = AbstractMutableBiMap.this.size();
            AbstractMutableBiMap.this.inverse().remove(value);
            return oldSize != AbstractMutableBiMap.this.size();
        }

        @Override
        public boolean containsAll(Collection<?> collection)
        {
            for (Object key : collection)
            {
                if (!AbstractMutableBiMap.this.inverse.delegate.containsKey(key))
                {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean addAll(Collection<? extends V> collection)
        {
            throw new UnsupportedOperationException("Cannot call addAll() on " + this.getClass().getSimpleName());
        }

        @Override
        public boolean removeAll(Collection<?> collection)
        {
            int oldSize = AbstractMutableBiMap.this.size();
            for (Object object : collection)
            {
                this.remove(object);
            }
            return oldSize != AbstractMutableBiMap.this.size();
        }

        @Override
        public boolean retainAll(Collection<?> collection)
        {
            int oldSize = AbstractMutableBiMap.this.size();
            Iterator<V> iterator = this.iterator();
            while (iterator.hasNext())
            {
                V next = iterator.next();
                if (!collection.contains(next))
                {
                    this.remove(next);
                }
            }
            return oldSize != AbstractMutableBiMap.this.size();
        }

        @Override
        public void clear()
        {
            AbstractMutableBiMap.this.clear();
        }

        @Override
        public Iterator<V> iterator()
        {
            return AbstractMutableBiMap.this.iterator();
        }

        @Override
        public String toString()
        {
            return AbstractMutableBiMap.this.delegate.values().toString();
        }
    }

    protected class EntrySet implements Set<Map.Entry<K, V>>
    {
        @Override
        public void forEach(Consumer<? super Entry<K, V>> action)
        {
            AbstractMutableBiMap.this.delegate.forEachKeyValue((key, value) -> action.accept(new InternalEntry(key, value)));
        }

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
            return AbstractMutableBiMap.this.hashCode();
        }

        @Override
        public String toString()
        {
            return AbstractMutableBiMap.this.delegate.entrySet().toString();
        }

        @Override
        public int size()
        {
            return AbstractMutableBiMap.this.size();
        }

        @Override
        public boolean isEmpty()
        {
            return AbstractMutableBiMap.this.isEmpty();
        }

        @Override
        public boolean contains(Object o)
        {
            if (!(o instanceof Map.Entry))
            {
                return false;
            }
            Map.Entry<K, V> entry = (Map.Entry<K, V>) o;
            K key = entry.getKey();
            V actualValue = AbstractMutableBiMap.this.get(key);
            if (actualValue != null)
            {
                return actualValue.equals(entry.getValue());
            }
            return entry.getValue() == null && AbstractMutableBiMap.this.containsKey(key);
        }

        @Override
        public Object[] toArray()
        {
            Object[] result = new Object[AbstractMutableBiMap.this.size()];
            return this.copyEntries(result);
        }

        @Override
        public <T> T[] toArray(T[] result)
        {
            int size = AbstractMutableBiMap.this.size();
            if (result.length < size)
            {
                result = (T[]) Array.newInstance(result.getClass().getComponentType(), size);
            }
            this.copyEntries(result);
            if (size < result.length)
            {
                result[size] = null;
            }
            return result;
        }

        @Override
        public boolean add(Map.Entry<K, V> entry)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(Object e)
        {
            if (!(e instanceof Map.Entry))
            {
                return false;
            }
            Map.Entry<?, ?> entry = (Map.Entry<?, ?>) e;
            K key = (K) entry.getKey();
            V value = (V) entry.getValue();

            V actualValue = AbstractMutableBiMap.this.delegate.get(key);
            if (actualValue != null)
            {
                if (actualValue.equals(value))
                {
                    AbstractMutableBiMap.this.remove(key);
                    return true;
                }
                return false;
            }
            if (value == null && AbstractMutableBiMap.this.delegate.containsKey(key))
            {
                AbstractMutableBiMap.this.remove(key);
                return true;
            }
            return false;
        }

        @Override
        public boolean containsAll(Collection<?> collection)
        {
            for (Object obj : collection)
            {
                if (!this.contains(obj))
                {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean addAll(Collection<? extends Map.Entry<K, V>> collection)
        {
            throw new UnsupportedOperationException("Cannot call addAll() on " + this.getClass().getSimpleName());
        }

        @Override
        public boolean retainAll(Collection<?> collection)
        {
            int oldSize = AbstractMutableBiMap.this.size();
            Iterator<Map.Entry<K, V>> iterator = this.iterator();
            while (iterator.hasNext())
            {
                Map.Entry<K, V> next = iterator.next();
                if (!collection.contains(next))
                {
                    iterator.remove();
                }
            }
            return oldSize != AbstractMutableBiMap.this.size();
        }

        @Override
        public boolean removeAll(Collection<?> collection)
        {
            boolean changed = false;
            for (Object obj : collection)
            {
                if (this.remove(obj))
                {
                    changed = true;
                }
            }
            return changed;
        }

        @Override
        public void clear()
        {
            AbstractMutableBiMap.this.clear();
        }

        @Override
        public Iterator<Map.Entry<K, V>> iterator()
        {
            return new InternalEntrySetIterator();
        }

        private Object[] copyEntries(Object[] result)
        {
            int count = 0;
            for (Pair<K, V> pair : AbstractMutableBiMap.this.keyValuesView())
            {
                result[count] = new InternalEntry(pair.getOne(), pair.getTwo());
                count++;
            }
            return result;
        }

        private class InternalEntrySetIterator implements Iterator<Entry<K, V>>
        {
            private final Iterator<Entry<K, V>> iterator = AbstractMutableBiMap.this.delegate.entrySet().iterator();
            private V currentValue;

            @Override
            public boolean hasNext()
            {
                return this.iterator.hasNext();
            }

            @Override
            public Entry<K, V> next()
            {
                Entry<K, V> next = this.iterator.next();
                Entry<K, V> result = new InternalEntry(next.getKey(), next.getValue());
                this.currentValue = result.getValue();
                return result;
            }

            @Override
            public void remove()
            {
                this.iterator.remove();
                AbstractMutableBiMap.this.inverse.delegate.removeKey(this.currentValue);
            }
        }

        private final class InternalEntry implements Entry<K, V>
        {
            private final K key;
            private V value;

            private InternalEntry(K key, V value)
            {
                this.key = key;
                this.value = value;
            }

            @Override
            public K getKey()
            {
                return this.key;
            }

            @Override
            public V getValue()
            {
                return this.value;
            }

            @Override
            public boolean equals(Object obj)
            {
                if (obj instanceof Entry)
                {
                    Entry<?, ?> other = (Entry<?, ?>) obj;
                    Object otherKey = other.getKey();
                    Object otherValue = other.getValue();
                    return AbstractMutableBiMap.nullSafeEquals(this.key, otherKey)
                            && AbstractMutableBiMap.nullSafeEquals(this.value, otherValue);
                }
                return false;
            }

            @Override
            public int hashCode()
            {
                return (this.key == null ? 0 : this.key.hashCode())
                        ^ (this.value == null ? 0 : this.value.hashCode());
            }

            @Override
            public String toString()
            {
                return this.key + "=" + this.value;
            }

            @Override
            public V setValue(V value)
            {
                V result = AbstractMutableBiMap.this.put(this.key, value);
                this.value = value;
                return result;
            }
        }
    }

    private static class Inverse<K, V> extends AbstractMutableBiMap<K, V> implements Externalizable
    {
        private static final long serialVersionUID = 1L;

        public Inverse()
        {
            // Empty constructor for Externalizable class
            super(UnifiedMap.newMap(), UnifiedMap.newMap());
        }

        Inverse(Map<K, V> delegate, AbstractMutableBiMap<V, K> inverse)
        {
            super(delegate, inverse);
        }
    }
}
