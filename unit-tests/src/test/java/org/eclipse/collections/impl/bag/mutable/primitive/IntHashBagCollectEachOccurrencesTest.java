/*
 * Copyright (c) 2026 and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.bag.mutable.primitive;

import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.bag.primitive.MutableIntBag;
import org.eclipse.collections.api.factory.Bags;
import org.eclipse.collections.api.factory.primitive.IntBags;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IntHashBagCollectEachOccurrencesTest
{
    @Test
    public void collectEachOccurrences_toObjectBag()
    {
        MutableIntBag bag = IntBags.mutable.with(1, 1, 1, 2, 2);
        int[] calls = {0};
        MutableBag<String> target = Bags.mutable.empty();
        bag.collectEachOccurrences(each ->
        {
            calls[0]++;
            return String.valueOf(each);
        }, target);
        assertEquals(2, calls[0]);
        assertEquals(3, target.occurrencesOf("1"));
        assertEquals(2, target.occurrencesOf("2"));
    }

    @Test
    public void collectEachOccurrencesInt_toIntBag()
    {
        MutableIntBag bag = IntBags.mutable.with(1, 1, 1, 2, 2);
        int[] calls = {0};
        MutableIntBag target = IntBags.mutable.empty();
        bag.collectEachOccurrencesInt(each ->
        {
            calls[0]++;
            return each * 10;
        }, target);
        assertEquals(2, calls[0]);
        assertEquals(3, target.occurrencesOf(10));
        assertEquals(2, target.occurrencesOf(20));
    }

    @Test
    public void collect_appliesFunctionOncePerOccurrence()
    {
        MutableIntBag bag = IntBags.mutable.with(1, 1, 1);
        int[] calls = {0};
        MutableBag<String> target = Bags.mutable.empty();
        bag.collect(each ->
        {
            calls[0]++;
            return each + "-" + calls[0];
        }, target);
        assertEquals(3, calls[0]);
        assertEquals(1, target.occurrencesOf("1-1"));
        assertEquals(1, target.occurrencesOf("1-2"));
        assertEquals(1, target.occurrencesOf("1-3"));
    }

    @Test
    public void collectInt_appliesFunctionOncePerOccurrence()
    {
        MutableIntBag bag = IntBags.mutable.with(1, 1, 1);
        int[] calls = {0};
        MutableIntBag target = IntBags.mutable.empty();
        bag.collectInt(each ->
        {
            calls[0]++;
            return each + calls[0];
        }, target);
        assertEquals(3, calls[0]);
        assertEquals(3, target.size());
        assertEquals(1, target.occurrencesOf(2));
        assertEquals(1, target.occurrencesOf(3));
        assertEquals(1, target.occurrencesOf(4));
    }
}

