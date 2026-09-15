/*
 * Copyright (c) 2022 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.map;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import org.eclipse.collections.api.LazyIterable;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function0;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.api.map.MapIterable;
import org.eclipse.collections.impl.AbstractRichIterable;
import org.eclipse.collections.impl.block.procedure.AppendStringWithSelfProcedure;
import org.eclipse.collections.impl.block.procedure.MapIterableToStringProcedure;

public abstract class AbstractMapIterable<K, V> extends AbstractRichIterable<V> implements MapIterable<K, V>
{
    protected int keyAndValueHashCode(K key, V value)
    {
        return (key == null ? 0 : key.hashCode()) ^ (value == null ? 0 : value.hashCode());
    }

    protected boolean keyAndValueEquals(K key, V value, Map<K, V> map)
    {
        if (value == null && !map.containsKey(key))
        {
            return false;
        }

        V oValue = map.get(key);
        return oValue == value || oValue != null && oValue.equals(value);
    }

    @Override
    public String toString()
    {
        MapIterableToStringProcedure<K, V> procedure = new MapIterableToStringProcedure<>(this);
        this.forEachKeyValue(procedure);
        return procedure.getString();
    }

    @Override
    public void appendString(Appendable appendable, String start, String separator, String end)
    {
        Procedure<V> procedure = new AppendStringWithSelfProcedure<>(appendable, separator, this, "(this Map)");
        try
        {
            appendable.append(start);
            this.forEachValue(procedure);
            appendable.append(end);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <A> A ifPresentApply(K key, Function<? super V, ? extends A> function)
    {
        V result = this.get(key);
        return this.isAbsent(result, key) ? null : function.valueOf(result);
    }

    protected boolean isAbsent(V result, K key)
    {
        return result == null && !this.containsKey(key);
    }

    @Override
    public V getOrDefault(Object key, V defaultValue)
    {
        return this.getIfAbsentValue((K) key, defaultValue);
    }

    @Override
    public V getIfAbsent(K key, Function0<? extends V> function)
    {
        V result = this.get(key);
        if (!this.isAbsent(result, key))
        {
            return result;
        }
        return function.value();
    }

    @Override
    public V getIfAbsentValue(K key, V value)
    {
        V result = this.get(key);
        if (!this.isAbsent(result, key))
        {
            return result;
        }
        return value;
    }

    @Override
    public <P> V getIfAbsentWith(
            K key,
            Function<? super P, ? extends V> function,
            P parameter)
    {
        V result = this.get(key);
        if (this.isAbsent(result, key))
        {
            result = function.valueOf(parameter);
        }
        return result;
    }

    @Override
    public boolean anySatisfy(Predicate<? super V> predicate)
    {
        return this.valuesView().anySatisfy(predicate);
    }

    @Override
    public <P> boolean anySatisfyWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.valuesView().anySatisfyWith(predicate, parameter);
    }

    @Override
    public boolean allSatisfy(Predicate<? super V> predicate)
    {
        return this.valuesView().allSatisfy(predicate);
    }

    @Override
    public <P> boolean allSatisfyWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.valuesView().allSatisfyWith(predicate, parameter);
    }

    @Override
    public boolean noneSatisfy(Predicate<? super V> predicate)
    {
        return this.valuesView().noneSatisfy(predicate);
    }

    @Override
    public <P> boolean noneSatisfyWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.valuesView().noneSatisfyWith(predicate, parameter);
    }

    @Override
    public LazyIterable<V> asLazy()
    {
        return this.valuesView().asLazy();
    }

    @Override
    public RichIterable<RichIterable<V>> chunk(int size)
    {
        return this.valuesView().chunk(size);
    }

    @Override
    public void each(Procedure<? super V> procedure)
    {
        this.forEachValue(procedure);
    }

    @Override
    public <P> void forEachWith(Procedure2<? super V, ? super P> procedure2, P parameter)
    {
        this.valuesView().forEachWith(procedure2, parameter);
    }

    @Override
    public void forEachWithIndex(ObjectIntProcedure<? super V> objectIntProcedure)
    {
        this.valuesView().forEachWithIndex(objectIntProcedure);
    }

    @Override
    public void forEachKey(Procedure<? super K> procedure)
    {
        this.keysView().forEach(procedure);
    }

    @Override
    public void forEachValue(Procedure<? super V> procedure)
    {
        this.valuesView().forEach(procedure);
    }

    @Override
    public boolean contains(Object object)
    {
        return this.containsValue(object);
    }

    @Override
    public V detect(Predicate<? super V> predicate)
    {
        return this.valuesView().detect(predicate);
    }

    @Override
    public <P> V detectWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.valuesView().detectWith(predicate, parameter);
    }

    @Override
    public Optional<V> detectOptional(Predicate<? super V> predicate)
    {
        return this.valuesView().detectOptional(predicate);
    }

    @Override
    public <P> Optional<V> detectWithOptional(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.valuesView().detectWithOptional(predicate, parameter);
    }

    @Override
    public V detectIfNone(Predicate<? super V> predicate, Function0<? extends V> function)
    {
        return this.valuesView().detectIfNone(predicate, function);
    }

    @Override
    public <P> V detectWithIfNone(Predicate2<? super V, ? super P> predicate, P parameter, Function0<? extends V> function)
    {
        return this.valuesView().detectWithIfNone(predicate, parameter, function);
    }

    @Override
    public V getFirst()
    {
        return this.valuesView().getFirst();
    }

    @Override
    public V getLast()
    {
        return this.valuesView().getLast();
    }

    @Override
    public V getOnly()
    {
        return this.valuesView().getOnly();
    }

    @Override
    public Object[] toArray()
    {
        return this.valuesView().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a)
    {
        return this.valuesView().toArray(a);
    }
}
