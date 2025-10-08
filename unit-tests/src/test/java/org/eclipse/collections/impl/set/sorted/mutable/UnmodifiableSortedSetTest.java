/*
 * Copyright (c) 2021 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.set.sorted.mutable;

import java.util.Comparator;
import java.util.NoSuchElementException;

import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.impl.block.factory.Functions;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.test.SerializeTestHelper;
import org.eclipse.collections.impl.test.Verify;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * JUnit test for {@link UnmodifiableSortedSet}.
 */
public class UnmodifiableSortedSetTest extends AbstractSortedSetTestCase
{
    private static final String LED_ZEPPELIN = "Led Zeppelin";
    private static final String METALLICA = "Metallica";

    private MutableSortedSet<String> mutableSet;
    private MutableSortedSet<String> unmodifiableSet;

    @BeforeEach
    public void setUp()
    {
        this.mutableSet = TreeSortedSet.newSetWith(METALLICA, "Bon Jovi", "Europe", "Scorpions");
        this.unmodifiableSet = this.mutableSet.asUnmodifiable();
    }

    @Override
    protected <T> MutableSortedSet<T> newWith(T... elements)
    {
        return TreeSortedSet.newSetWith(elements).asUnmodifiable();
    }

    @Override
    protected <T> MutableSortedSet<T> newWith(Comparator<? super T> comparator, T... elements)
    {
        return TreeSortedSet.newSetWith(comparator, elements).asUnmodifiable();
    }

    @Override
    @Test
    public void asSynchronized()
    {
        Verify.assertInstanceOf(SynchronizedSortedSet.class, this.newWith().asSynchronized());
    }

    @Override
    @Test
    public void asUnmodifiable()
    {
        Verify.assertInstanceOf(UnmodifiableSortedSet.class, this.newWith());
    }

    @Test
    public void testAsUnmodifiable()
    {
        Verify.assertInstanceOf(UnmodifiableSortedSet.class, this.newWith().asUnmodifiable());
        MutableSortedSet<Object> set = this.newWith();
        assertSame(set, set.asUnmodifiable());
    }

    @Test
    public void testEqualsAndHashCode()
    {
        Verify.assertEqualsAndHashCode(this.mutableSet, this.unmodifiableSet);
        Verify.assertPostSerializedEqualsAndHashCode(this.unmodifiableSet);
        Verify.assertInstanceOf(UnmodifiableSortedSet.class, SerializeTestHelper.serializeDeserialize(this.unmodifiableSet));
    }

    @Test
    public void testNewEmpty()
    {
        MutableSortedSet<String> set = this.unmodifiableSet.newEmpty();
        set.add(LED_ZEPPELIN);
        Verify.assertContains(LED_ZEPPELIN, set);
    }

    @Override
    @Test
    public void testClone()
    {
        MutableSortedSet<String> set = this.newWith();
        MutableSortedSet<String> clone = set.clone();
        assertSame(clone, set);
    }

    @Override
    @Test
    public void min()
    {
        super.min();
        assertEquals("1", this.newWith("1", "3", "2").min(String::compareTo));
    }

    @Override
    @Test
    public void max()
    {
        super.max();
        assertEquals("3", this.newWith("1", "3", "2").max(String::compareTo));
    }

    @Test
    public void min_empty_throws_without_comparator()
    {
        assertThrows(NoSuchElementException.class, () -> this.newWith().min());
    }

    @Test
    public void max_empty_throws_without_comparator()
    {
        assertThrows(NoSuchElementException.class, () -> this.newWith().max());
    }

    @Override
    @Test
    public void min_null_throws_without_comparator()
    {
        super.min_null_throws_without_comparator();
        assertThrows(NullPointerException.class, () -> this.newWith("1", null, "2").min());
    }

    @Override
    @Test
    public void max_null_throws_without_comparator()
    {
        super.max_null_throws_without_comparator();
        assertThrows(NullPointerException.class, () -> this.newWith("1", null, "2").max());
    }

    @Override
    @Test
    public void min_without_comparator()
    {
        super.min_without_comparator();
        assertEquals("1", this.newWith("1", "3", "2").min());
    }

    @Override
    @Test
    public void max_without_comparator()
    {
        super.max_without_comparator();
        assertEquals("3", this.newWith("1", "3", "2").max());
    }

    @Override
    @Test
    public void minBy()
    {
        super.minBy();
        assertEquals("1", this.newWith("1", "3", "2").minBy(Functions.getStringToInteger()));
    }

    @Override
    @Test
    public void maxBy()
    {
        super.maxBy();
        assertEquals("3", this.newWith("1", "3", "2").maxBy(Functions.getStringToInteger()));
    }

    @Override
    @Test
    public void removeObject()
    {
        assertThrows(UnsupportedOperationException.class, () -> super.removeObject());
    }

    @Override
    @Test
    public void removeIfWith()
    {
        assertThrows(UnsupportedOperationException.class, () -> super.removeIfWith());
    }

    @Override
    @Test
    public void clear()
    {
        assertThrows(UnsupportedOperationException.class, () -> super.clear());
    }

    @Override
    @Test
    public void addAll()
    {
        assertThrows(UnsupportedOperationException.class, () -> super.addAll());
    }

    @Override
    @Test
    public void addAllIterable()
    {
        assertThrows(UnsupportedOperationException.class, () -> super.addAllIterable());
    }

    @Override
    @Test
    public void removeIf()
    {
        assertThrows(UnsupportedOperationException.class, () -> super.removeIf());
    }

    @Override
    @Test
    public void removeAll()
    {
        assertThrows(UnsupportedOperationException.class, () -> super.removeAll());
    }

    @Override
    @Test
    public void removeAllIterable()
    {
        assertThrows(UnsupportedOperationException.class, () -> super.removeAllIterable());
    }

    @Override
    @Test
    public void retainAll()
    {
        assertThrows(UnsupportedOperationException.class, () -> super.retainAll());
    }

    @Override
    @Test
    public void retainAllIterable()
    {
        assertThrows(UnsupportedOperationException.class, () -> super.retainAllIterable());
    }

    @Override
    @Test
    public void testToString()
    {
        super.testToString();
        MutableCollection<Object> collection = this.newWith(1, 2);
        String toString = collection.toString();
        assertTrue("[1, 2]".equals(toString) || "[2, 1]".equals(toString));
    }

    @Override
    @Test
    public void makeString()
    {
        super.makeString();
        MutableCollection<Object> collection = this.newWith(1, 2, 3);
        assertEquals(collection.toString(), '[' + collection.makeString() + ']');
    }

    @Override
    @Test
    public void appendString()
    {
        super.appendString();
        MutableCollection<Object> collection = this.newWith(1, 2, 3);
        Appendable builder = new StringBuilder();
        collection.appendString(builder);
        assertEquals(collection.toString(), '[' + builder.toString() + ']');
    }

    @Override
    @Test
    public void getFirst()
    {
        super.getFirst();
        assertNotNull(this.newWith(1, 2, 3).getFirst());
        assertThrows(NoSuchElementException.class, () -> assertNull(this.newWith().getFirst()));
    }

    @Override
    @Test
    public void getLast()
    {
        super.getLast();
        assertNotNull(this.newWith(1, 2, 3).getLast());
        assertThrows(NoSuchElementException.class, () -> assertNull(this.newWith().getLast()));
    }

    @Override
    @Test
    public void subSet()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.newWith(1, 2, 3).subSet(1, 3).clear());
    }

    @Override
    @Test
    public void headSet()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.newWith(1, 2, 3, 4).headSet(3).add(4));
    }

    @Override
    @Test
    public void tailSet()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.newWith(1, 2, 3, 4).tailSet(3).remove(1));
    }

    @Test
    public void serialization()
    {
        Verify.assertPostSerializedEqualsAndHashCode(this.newWith(1, 2, 3, 4, 5));
    }

    @Override
    @Test
    public void with()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.newWith().with(1));
    }

    @Override
    @Test
    public void withAll()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.newWith().withAll(FastList.newListWith(1, 2)));
    }

    @Override
    @Test
    public void without()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.newWith().without(2));
    }

    @Override
    @Test
    public void withoutAll()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.newWith().withoutAll(FastList.newListWith(1, 2)));
    }

    @Override
    @Test
    public void detectLastIndex()
    {
        assertEquals(1, this.newWith(1, 2, 3).detectLastIndex(each -> each % 2 == 0));
        assertEquals(3, this.newWith(1, 2, 3, 4).detectLastIndex(each -> each % 2 == 0));
        assertEquals(-1, this.newWith(1, 3, 5).detectLastIndex(each -> each % 2 == 0));
    }

    @Override
    @Test
    public void reverseForEach()
    {
        MutableList<Integer> result = FastList.newList();
        this.newWith(1, 2, 3).reverseForEach(result::add);
        assertEquals(FastList.newListWith(3, 2, 1), result);
    }

    @Override
    @Test
    public void reverseForEachWithIndex()
    {
        MutableList<String> result = FastList.newList();
        this.newWith(1, 2, 3).reverseForEachWithIndex((each, index) -> result.add(each + ":" + index));
        assertEquals(FastList.newListWith("3:0", "2:1", "1:2"), result);
    }

    @Override
    @Test
    public void toReversed()
    {
        MutableSortedSet<Integer> set = this.newWith(1, 2, 3, 4, 5);
        MutableSortedSet<Integer> reversed = set.toReversed();
        assertEquals(FastList.newListWith(5, 4, 3, 2, 1), reversed.toList());
    }
}
