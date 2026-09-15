/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.block.comparator;

import java.util.Comparator;

import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.block.factory.Comparators;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FunctionComparatorTest
{
    private static final Band VAN_HALEN = new Band("Van Halen");
    private static final Band BON_JOVI = new Band("Bon Jovi");
    private static final Band METALLICA = new Band("Metallica");
    private static final Band SCORPIONS = new Band("Scorpions");

    private static final Band ACDC = new Band("AC/DC");
    private static final Band ZZTOP = new Band("ZZ Top");

    @Test
    public void comparator()
    {
        Comparator<Band> comparator = new FunctionComparator<>(
                Band.TO_NAME,
                String::compareTo);

        assertEquals(comparator.compare(ACDC, ZZTOP), ACDC.getName().compareTo(ZZTOP.getName()));
        assertEquals(comparator.compare(ZZTOP, ACDC), ZZTOP.getName().compareTo(ACDC.getName()));
    }

    private MutableList<Band> createTestList()
    {
        return FastList.newListWith(VAN_HALEN, SCORPIONS, BON_JOVI, METALLICA);
    }

    @Test
    public void functionComparatorBuiltTheHardWay()
    {
        Comparator<Band> byName = (bandA, bandB) -> Band.TO_NAME.valueOf(bandA).compareTo(Band.TO_NAME.valueOf(bandB));
        MutableList<Band> sortedList = this.createTestList().sortThis(byName);
        assertEquals(FastList.newListWith(BON_JOVI, METALLICA, SCORPIONS, VAN_HALEN), sortedList);
    }

    @Test
    public void functionComparatorBuiltTheEasyWay()
    {
        Comparator<Band> byName = Comparators.byFunction(Band.TO_NAME, String::compareTo);
        MutableList<Band> sortedList = this.createTestList().sortThis(byName);
        assertEquals(FastList.newListWith(BON_JOVI, METALLICA, SCORPIONS, VAN_HALEN), sortedList);
    }

    private static final class Band
    {
        public static final Function<Band, String> TO_NAME = band -> band.name;

        private final String name;

        private Band(String name)
        {
            this.name = name;
        }

        public String getName()
        {
            return this.name;
        }

        @Override
        public String toString()
        {
            return this.name;
        }

        @Override
        public boolean equals(Object other)
        {
            return this == other || other instanceof Band band && this.name.equals(band.name);
        }

        @Override
        public int hashCode()
        {
            return this.name.hashCode();
        }
    }
}
