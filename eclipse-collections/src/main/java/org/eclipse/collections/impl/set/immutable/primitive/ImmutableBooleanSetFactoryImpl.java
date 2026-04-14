/*
 * Copyright (c) 2022 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.set.immutable.primitive;

import org.eclipse.collections.api.BooleanIterable;
import org.eclipse.collections.api.factory.primitive.BooleanSets;
import org.eclipse.collections.api.factory.set.primitive.ImmutableBooleanSetFactory;
import org.eclipse.collections.api.set.primitive.ImmutableBooleanSet;

/**
 * ImmutableBooleanSetFactoryImpl is a factory implementation which creates instances of type {@link ImmutableBooleanSet}.
 *
 * @since 4.0.
 */
@aQute.bnd.annotation.spi.ServiceProvider(ImmutableBooleanSetFactory.class)
public class ImmutableBooleanSetFactoryImpl implements ImmutableBooleanSetFactory
{
    public static final ImmutableBooleanSetFactory INSTANCE = new ImmutableBooleanSetFactoryImpl();

    @Override
    public ImmutableBooleanSet empty()
    {
        return ImmutableBooleanEmptySet.INSTANCE;
    }

    @Override
    public ImmutableBooleanSet of()
    {
        return this.empty();
    }

    @Override
    public ImmutableBooleanSet with()
    {
        return this.empty();
    }

    @Override
    public ImmutableBooleanSet of(boolean one)
    {
        return this.with(one);
    }

    @Override
    public ImmutableBooleanSet with(boolean one)
    {
        return one ? ImmutableTrueSet.INSTANCE : ImmutableFalseSet.INSTANCE;
    }

    @Override
    public ImmutableBooleanSet of(boolean... items)
    {
        return this.with(items);
    }

    @Override
    public ImmutableBooleanSet with(boolean... items)
    {
        if (items == null || items.length == 0)
        {
            return this.with();
        }
        if (items.length == 1)
        {
            return this.with(items[0]);
        }
        ImmutableBooleanSet result = ImmutableBooleanEmptySet.INSTANCE;
        for (boolean item : items)
        {
            result = result.newWith(item);
        }
        return result;
    }

    @Override
    public ImmutableBooleanSet ofAll(BooleanIterable items)
    {
        return this.withAll(items);
    }

    @Override
    public ImmutableBooleanSet withAll(BooleanIterable items)
    {
        if (items instanceof ImmutableBooleanSet immutableBooleanSet)
        {
            return immutableBooleanSet;
        }
        return this.with(items.toArray());
    }

    /**
     * @since 10.0
     */
    @Override
    public ImmutableBooleanSet ofAll(Iterable<Boolean> iterable)
    {
        return this.withAll(iterable);
    }

    /**
     * @since 10.0
     */
    @Override
    public ImmutableBooleanSet withAll(Iterable<Boolean> iterable)
    {
        return BooleanSets.mutable.withAll(iterable).toImmutable();
    }
}
