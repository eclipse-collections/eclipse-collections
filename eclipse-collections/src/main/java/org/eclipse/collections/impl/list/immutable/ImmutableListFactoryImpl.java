/*
 * Copyright (c) 2021 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.list.immutable;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.RandomAccess;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.factory.list.ImmutableListFactory;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.impl.utility.Iterate;

@aQute.bnd.annotation.spi.ServiceProvider(ImmutableListFactory.class)
public class ImmutableListFactoryImpl implements ImmutableListFactory
{
    public static final ImmutableListFactory INSTANCE = new ImmutableListFactoryImpl();

    @Override
    public <T> ImmutableList<T> empty()
    {
        return (ImmutableList<T>) ImmutableEmptyList.INSTANCE;
    }

    @Override
    public <T> ImmutableList<T> of()
    {
        return this.empty();
    }

    @Override
    public <T> ImmutableList<T> with()
    {
        return this.empty();
    }

    @Override
    public <T> ImmutableList<T> of(T one)
    {
        return this.with(one);
    }

    @Override
    public <T> ImmutableList<T> with(T one)
    {
        return new ImmutableSingletonList<>(one);
    }

    @Override
    public <T> ImmutableList<T> of(T one, T two)
    {
        return this.with(one, two);
    }

    @Override
    public <T> ImmutableList<T> with(T one, T two)
    {
        return new ImmutableDoubletonList<>(one, two);
    }

    @Override
    public <T> ImmutableList<T> of(T one, T two, T three)
    {
        return this.with(one, two, three);
    }

    @Override
    public <T> ImmutableList<T> with(T one, T two, T three)
    {
        return new ImmutableTripletonList<>(one, two, three);
    }

    @Override
    public <T> ImmutableList<T> of(T one, T two, T three, T four)
    {
        return this.with(one, two, three, four);
    }

    @Override
    public <T> ImmutableList<T> with(T one, T two, T three, T four)
    {
        return new ImmutableQuadrupletonList<>(one, two, three, four);
    }

    @Override
    public <T> ImmutableList<T> of(T one, T two, T three, T four, T five)
    {
        return this.with(one, two, three, four, five);
    }

    @Override
    public <T> ImmutableList<T> with(T one, T two, T three, T four, T five)
    {
        return new ImmutableQuintupletonList<>(one, two, three, four, five);
    }

    @Override
    public <T> ImmutableList<T> of(T one, T two, T three, T four, T five, T six)
    {
        return this.with(one, two, three, four, five, six);
    }

    @Override
    public <T> ImmutableList<T> with(T one, T two, T three, T four, T five, T six)
    {
        return new ImmutableSextupletonList<>(one, two, three, four, five, six);
    }

    @Override
    public <T> ImmutableList<T> of(T one, T two, T three, T four, T five, T six, T seven)
    {
        return this.with(one, two, three, four, five, six, seven);
    }

    @Override
    public <T> ImmutableList<T> with(T one, T two, T three, T four, T five, T six, T seven)
    {
        return new ImmutableSeptupletonList<>(one, two, three, four, five, six, seven);
    }

    @Override
    public <T> ImmutableList<T> of(T one, T two, T three, T four, T five, T six, T seven, T eight)
    {
        return this.with(one, two, three, four, five, six, seven, eight);
    }

    @Override
    public <T> ImmutableList<T> with(T one, T two, T three, T four, T five, T six, T seven, T eight)
    {
        return new ImmutableOctupletonList<>(one, two, three, four, five, six, seven, eight);
    }

    @Override
    public <T> ImmutableList<T> of(T one, T two, T three, T four, T five, T six, T seven, T eight, T nine)
    {
        return this.with(one, two, three, four, five, six, seven, eight, nine);
    }

    @Override
    public <T> ImmutableList<T> with(T one, T two, T three, T four, T five, T six, T seven, T eight, T nine)
    {
        return new ImmutableNonupletonList<>(one, two, three, four, five, six, seven, eight, nine);
    }

    @Override
    public <T> ImmutableList<T> of(T one, T two, T three, T four, T five, T six, T seven, T eight, T nine, T ten)
    {
        return this.with(one, two, three, four, five, six, seven, eight, nine, ten);
    }

    @Override
    public <T> ImmutableList<T> with(T one, T two, T three, T four, T five, T six, T seven, T eight, T nine, T ten)
    {
        return new ImmutableDecapletonList<>(one, two, three, four, five, six, seven, eight, nine, ten);
    }

    @Override
    public <T> ImmutableList<T> of(T... items)
    {
        return this.with(items);
    }

    @Override
    public <T> ImmutableList<T> with(T... items)
    {
        if (items == null || items.length == 0)
        {
            return this.empty();
        }

        switch (items.length)
        {
            case 1:
                return this.of(items[0]);
            case 2:
                return this.of(items[0], items[1]);
            case 3:
                return this.of(items[0], items[1], items[2]);
            case 4:
                return this.of(items[0], items[1], items[2], items[3]);
            case 5:
                return this.of(items[0], items[1], items[2], items[3], items[4]);
            case 6:
                return this.of(items[0], items[1], items[2], items[3], items[4], items[5]);
            case 7:
                return this.of(items[0], items[1], items[2], items[3], items[4], items[5], items[6]);
            case 8:
                return this.of(items[0], items[1], items[2], items[3], items[4], items[5], items[6], items[7]);
            case 9:
                return this.of(items[0], items[1], items[2], items[3], items[4], items[5], items[6], items[7], items[8]);
            case 10:
                return this.of(items[0], items[1], items[2], items[3], items[4], items[5], items[6], items[7], items[8], items[9]);

            default:
                return ImmutableArrayList.newListWith(items);
        }
    }

    private <T> ImmutableList<T> withList(List<T> items)
    {
        switch (items.size())
        {
            case 0:
                return this.empty();
            case 1:
                return this.of(items.get(0));
            case 2:
                return this.of(items.get(0), items.get(1));
            case 3:
                return this.of(items.get(0), items.get(1), items.get(2));
            case 4:
                return this.of(items.get(0), items.get(1), items.get(2), items.get(3));
            case 5:
                return this.of(items.get(0), items.get(1), items.get(2), items.get(3), items.get(4));
            case 6:
                return this.of(items.get(0), items.get(1), items.get(2), items.get(3), items.get(4), items.get(5));
            case 7:
                return this.of(items.get(0), items.get(1), items.get(2), items.get(3), items.get(4), items.get(5), items.get(6));
            case 8:
                return this.of(items.get(0), items.get(1), items.get(2), items.get(3), items.get(4), items.get(5), items.get(6), items.get(7));
            case 9:
                return this.of(items.get(0), items.get(1), items.get(2), items.get(3), items.get(4), items.get(5), items.get(6), items.get(7), items.get(8));
            case 10:
                return this.of(items.get(0), items.get(1), items.get(2), items.get(3), items.get(4), items.get(5), items.get(6), items.get(7), items.get(8), items.get(9));

            default:
                return ImmutableArrayList.newList(items);
        }
    }

    @Override
    public <T> ImmutableList<T> ofAll(Iterable<? extends T> items)
    {
        return this.withAll(items);
    }

    @Override
    public <T> ImmutableList<T> withAll(Iterable<? extends T> items)
    {
        if (Iterate.isEmpty(items))
        {
            return this.empty();
        }
        if (items instanceof ImmutableList<?>)
        {
            return (ImmutableList<T>) items;
        }
        if (items instanceof List && items instanceof RandomAccess)
        {
            return this.withList((List<T>) items);
        }
        return new ImmutableArrayList<>((T[]) Iterate.toArray(items));
    }

    @Override
    public <T> ImmutableList<T> withAllSorted(RichIterable<? extends T> items)
    {
        if (items.isEmpty())
        {
            return this.empty();
        }
        if (items.size() == 1)
        {
            return this.with(items.getOnly());
        }
        T[] array = (T[]) items.toArray();
        Arrays.sort(array);
        return new ImmutableArrayList<>(array);
    }

    @Override
    public <T> ImmutableList<T> withAllSorted(Comparator<? super T> comparator, RichIterable<? extends T> items)
    {
        if (items.isEmpty())
        {
            return this.empty();
        }
        if (items.size() == 1)
        {
            return this.with(items.getOnly());
        }
        T[] array = (T[]) items.toArray();
        Arrays.sort(array, comparator);
        return new ImmutableArrayList<>(array);
    }

    @Override
    public <T> ImmutableList<T> ofNCopiesOf(int copies, T item)
    {
        if (copies < 0)
        {
            throw new IllegalArgumentException("Copies must be > 0 but was " + copies);
        }
        if (copies == 0)
        {
            return this.empty();
        }
        if (copies == 1)
        {
            return this.of(item);
        }
        return new ImmutableNCopiesList<T>(item, copies);
    }
}
