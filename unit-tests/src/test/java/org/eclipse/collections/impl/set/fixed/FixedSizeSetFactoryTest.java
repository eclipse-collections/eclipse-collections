/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.set.fixed;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.factory.set.FixedSizeSetFactory;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.set.FixedSizeSet;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.block.factory.Procedures2;
import org.eclipse.collections.impl.block.procedure.CollectionAddProcedure;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.eclipse.collections.impl.test.Verify;
import org.eclipse.collections.impl.test.domain.Holder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public class FixedSizeSetFactoryTest
{
    private FixedSizeSetFactory setFactory;

    @BeforeEach
    public void setUp()
    {
        this.setFactory = FixedSizeSetFactoryImpl.INSTANCE;
    }

    @Test
    public void testCreateWith3Args()
    {
        this.assertCreateSet(this.setFactory.of("a", "a"), "a");
        this.assertCreateSet(this.setFactory.of("a", "a", "c"), "a", "c");
        this.assertCreateSet(this.setFactory.of("a", "b", "a"), "a", "b");
        this.assertCreateSet(this.setFactory.of("a", "b", "b"), "a", "b");
    }

    @Test
    public void testCreateWith4Args()
    {
        this.assertCreateSet(this.setFactory.of("a", "a", "c", "d"), "a", "c", "d");
        this.assertCreateSet(this.setFactory.of("a", "b", "a", "d"), "a", "b", "d");
        this.assertCreateSet(this.setFactory.of("a", "b", "c", "a"), "a", "b", "c");
        this.assertCreateSet(this.setFactory.of("a", "b", "b", "d"), "a", "b", "d");
        this.assertCreateSet(this.setFactory.of("a", "b", "c", "b"), "a", "b", "c");
        this.assertCreateSet(this.setFactory.of("a", "b", "c", "c"), "a", "b", "c");
    }

    private void assertCreateSet(FixedSizeSet<String> undertest, String... expected)
    {
        assertEquals(UnifiedSet.newSetWith(expected), undertest);
        Verify.assertInstanceOf(FixedSizeSet.class, undertest);
    }

    @Test
    public void keyPreservation()
    {
        Holder<String> key = new Holder<>("key");

        Holder<String> duplicateKey1 = new Holder<>("key");
        MutableSet<Holder<String>> set1 = this.setFactory.of(key, duplicateKey1);
        Verify.assertSize(1, set1);
        Verify.assertContains(key, set1);
        assertSame(key, set1.getFirst());

        Holder<String> duplicateKey2 = new Holder<>("key");
        MutableSet<Holder<String>> set2 = this.setFactory.of(key, duplicateKey1, duplicateKey2);
        Verify.assertSize(1, set2);
        Verify.assertContains(key, set2);
        assertSame(key, set1.getFirst());

        Holder<String> duplicateKey3 = new Holder<>("key");
        MutableSet<Holder<String>> set3 = this.setFactory.of(key, new Holder<>("not a dupe"), duplicateKey3);
        Verify.assertSize(2, set3);
        Verify.assertContainsAll(set3, key, new Holder<>("not a dupe"));
        assertSame(key, set3.detect(key::equals));

        Holder<String> duplicateKey4 = new Holder<>("key");
        MutableSet<Holder<String>> set4 = this.setFactory.of(key, new Holder<>("not a dupe"), duplicateKey3, duplicateKey4);
        Verify.assertSize(2, set4);
        Verify.assertContainsAll(set4, key, new Holder<>("not a dupe"));
        assertSame(key, set4.detect(key::equals));

        MutableSet<Holder<String>> set5 = this.setFactory.of(key, new Holder<>("not a dupe"), new Holder<>("me neither"), duplicateKey4);
        Verify.assertSize(3, set5);
        Verify.assertContainsAll(set5, key, new Holder<>("not a dupe"), new Holder<>("me neither"));
        assertSame(key, set5.detect(key::equals));

        MutableSet<Holder<String>> set6 = this.setFactory.of(key, duplicateKey2, duplicateKey3, duplicateKey4);
        Verify.assertSize(1, set6);
        Verify.assertContains(key, set6);
        assertSame(key, set6.detect(key::equals));
    }

    @Test
    public void create1()
    {
        FixedSizeSet<String> set = Sets.fixedSize.of("1");
        Verify.assertSize(1, set);
        Verify.assertContains("1", set);
    }

    @Test
    public void create2()
    {
        FixedSizeSet<String> set = Sets.fixedSize.of("1", "2");
        assertEquals(UnifiedSet.newSetWith("1", "2"), set);
    }

    @Test
    public void create3()
    {
        FixedSizeSet<String> set = Sets.fixedSize.of("1", "2", "3");
        assertEquals(UnifiedSet.newSetWith("1", "2", "3"), set);
    }

    @Test
    public void create4()
    {
        FixedSizeSet<String> set = Sets.fixedSize.of("1", "2", "3", "4");
        assertEquals(UnifiedSet.newSetWith("1", "2", "3", "4"), set);
    }

    @Test
    public void createWithDuplicates()
    {
        FixedSizeSet<String> set1 = Sets.fixedSize.of("1", "1");
        assertEquals(UnifiedSet.newSetWith("1"), set1);

        FixedSizeSet<String> set2 = Sets.fixedSize.of("1", "1", "1");
        assertEquals(UnifiedSet.newSetWith("1"), set2);

        FixedSizeSet<String> set3 = Sets.fixedSize.of("2", "3", "2");
        assertEquals(UnifiedSet.newSetWith("2", "3"), set3);

        FixedSizeSet<String> set4 = Sets.fixedSize.of("3", "4", "4");
        assertEquals(UnifiedSet.newSetWith("3", "4"), set4);

        FixedSizeSet<String> set5 = Sets.fixedSize.of("4", "4", "4", "4");
        assertEquals(UnifiedSet.newSetWith("4"), set5);

        FixedSizeSet<String> set6 = Sets.fixedSize.of("4", "3", "4", "4");
        assertEquals(UnifiedSet.newSetWith("4", "3"), set6);

        FixedSizeSet<String> set7 = Sets.fixedSize.of("4", "2", "3", "4");
        assertEquals(UnifiedSet.newSetWith("4", "3", "2"), set7);

        FixedSizeSet<String> set8 = Sets.fixedSize.of("2", "3", "4", "4");
        assertEquals(UnifiedSet.newSetWith("4", "3", "2"), set8);

        FixedSizeSet<String> set9 = Sets.fixedSize.of("2", "4", "3", "4");
        assertEquals(UnifiedSet.newSetWith("4", "3", "2"), set9);

        FixedSizeSet<String> set10 = Sets.fixedSize.of("2", "4", "3", "4");
        assertEquals(UnifiedSet.newSetWith("4", "3", "2"), set10);

        FixedSizeSet<String> set11 = Sets.fixedSize.of("4", "3", "4", "2");
        assertEquals(UnifiedSet.newSetWith("4", "3", "2"), set11);

        FixedSizeSet<String> set12 = Sets.fixedSize.of("3", "4", "4", "2");
        assertEquals(UnifiedSet.newSetWith("4", "3", "2"), set12);
    }

    @Test
    public void createSet()
    {
        MutableSet<String> set1 = Sets.fixedSize.of();
        Verify.assertEmpty(set1);

        MutableSet<String> set2 = Sets.fixedSize.of();
        Verify.assertEmpty(set2);

        assertSame(Sets.fixedSize.of(), Sets.fixedSize.of());
    }

    @Test
    public void forEach()
    {
        MutableList<String> result = Lists.mutable.of();
        MutableSet<String> source = Sets.fixedSize.of("1", "2", "3", "4");
        source.forEach(CollectionAddProcedure.on(result));
        assertEquals(FastList.newListWith("1", "2", "3", "4"), result);
    }

    @Test
    public void forEachWithIndex()
    {
        int[] indexSum = new int[1];
        MutableList<String> result = Lists.mutable.of();
        MutableSet<String> source = Sets.fixedSize.of("1", "2", "3", "4");
        source.forEachWithIndex((each, index) -> {
            result.add(each);
            indexSum[0] += index;
        });
        assertEquals(6, indexSum[0]);
        assertEquals(FastList.newListWith("1", "2", "3", "4"), result);
    }

    @Test
    public void forEachWith()
    {
        MutableList<String> result = Lists.mutable.of();
        MutableSet<String> source = Sets.fixedSize.of("1", "2", "3", "4");
        source.forEachWith(Procedures2.fromProcedure(CollectionAddProcedure.on(result)), null);
        assertEquals(FastList.newListWith("1", "2", "3", "4"), result);
    }

    @Test
    public void ofAllSizeZero()
    {
        MutableSet<Integer> set = Sets.fixedSize.ofAll(FastList.newList());
        assertEquals(UnifiedSet.<Integer>newSetWith(), set);
        Verify.assertInstanceOf(FixedSizeSet.class, set);
    }

    @Test
    public void ofAllSizeOne()
    {
        MutableSet<Integer> set = Sets.fixedSize.ofAll(FastList.newListWith(1));
        assertEquals(UnifiedSet.newSetWith(1), set);
        Verify.assertInstanceOf(FixedSizeSet.class, set);
    }

    @Test
    public void ofAllSizeTwo()
    {
        MutableSet<Integer> set = Sets.fixedSize.ofAll(FastList.newListWith(1, 2));
        assertEquals(UnifiedSet.newSetWith(1, 2), set);
        Verify.assertInstanceOf(FixedSizeSet.class, set);
    }

    @Test
    public void ofAllSizeThree()
    {
        MutableSet<Integer> set = Sets.fixedSize.ofAll(FastList.newListWith(1, 2, 3));
        assertEquals(UnifiedSet.newSetWith(1, 2, 3), set);
        Verify.assertInstanceOf(FixedSizeSet.class, set);
    }

    @Test
    public void ofAllSizeFour()
    {
        MutableSet<Integer> set = Sets.fixedSize.ofAll(FastList.newListWith(1, 2, 3, 4));
        assertEquals(UnifiedSet.newSetWith(1, 2, 3, 4), set);
        Verify.assertInstanceOf(FixedSizeSet.class, set);
    }

    @Test
    public void ofAllSizeFive()
    {
        MutableSet<Integer> set = Sets.fixedSize.ofAll(FastList.newListWith(1, 2, 3, 4, 5));
        assertEquals(UnifiedSet.newSetWith(1, 2, 3, 4, 5), set);
        Verify.assertInstanceOf(UnifiedSet.class, set);
    }
}
