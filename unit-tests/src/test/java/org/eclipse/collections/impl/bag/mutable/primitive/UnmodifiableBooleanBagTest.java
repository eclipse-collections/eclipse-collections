/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.bag.mutable.primitive;

import org.eclipse.collections.api.bag.primitive.MutableBooleanBag;
import org.eclipse.collections.api.iterator.MutableBooleanIterator;
import org.eclipse.collections.api.set.primitive.MutableBooleanSet;
import org.eclipse.collections.impl.factory.primitive.BooleanSets;
import org.eclipse.collections.impl.list.mutable.primitive.BooleanArrayList;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JUnit test for {@link UnmodifiableBooleanBag}.
 */
public class UnmodifiableBooleanBagTest extends AbstractMutableBooleanBagTestCase
{
    private final MutableBooleanBag bag = this.classUnderTest();

    @Override
    protected final UnmodifiableBooleanBag classUnderTest()
    {
        return new UnmodifiableBooleanBag(BooleanHashBag.newBagWith(true, false, true));
    }

    @Override
    protected UnmodifiableBooleanBag newWith(boolean... elements)
    {
        return new UnmodifiableBooleanBag(BooleanHashBag.newBagWith(elements));
    }

    @Override
    @Test
    public void addOccurrences()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.bag.addOccurrences(false, 3));
    }

    @Override
    @Test
    public void addOccurrences_throws()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.newWith().addOccurrences(true, -1));
    }

    @Override
    @Test
    public void removeOccurrences()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.bag.removeOccurrences(true, 1));
    }

    @Override
    @Test
    public void removeOccurrences_throws()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.newWith().removeOccurrences(true, -1));
    }

    @Override
    @Test
    public void clear()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.classUnderTest().clear());
    }

    @Override
    @Test
    public void add()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.newWith().add(true));
    }

    @Override
    @Test
    public void addAllArray()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.classUnderTest().addAll(true, false, true));
    }

    @Override
    @Test
    public void addAllIterable()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.classUnderTest().addAll(this.newMutableCollectionWith()));
    }

    @Override
    @Test
    public void remove()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.classUnderTest().remove(false));
    }

    @Override
    @Test
    public void removeAll()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.classUnderTest().removeAll(true, false));
    }

    @Override
    @Test
    public void removeAll_iterable()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.classUnderTest().removeAll(this.newMutableCollectionWith()));
    }

    @Override
    @Test
    public void retainAll()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.classUnderTest().retainAll(true, false));
    }

    @Override
    @Test
    public void retainAll_iterable()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.classUnderTest().retainAll(this.newMutableCollectionWith()));
    }

    @Override
    @Test
    public void with()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.newWith().with(true));
    }

    @Override
    @Test
    public void withAll()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.newWith().withAll(this.newMutableCollectionWith(true)));
    }

    @Override
    @Test
    public void without()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.newWith(true, false, true, false, true).without(true));
    }

    @Override
    @Test
    public void withoutAll()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.newWith(true, false, true, false, true).withoutAll(this.newMutableCollectionWith(false, false)));
    }

    @Override
    @Test
    public void containsAllArray()
    {
        UnmodifiableBooleanBag collection = this.classUnderTest();
        assertTrue(collection.containsAll(true));
        assertTrue(collection.containsAll(true, false, true));
        assertTrue(collection.containsAll(true, false));
        assertTrue(collection.containsAll(true, true));
        assertTrue(collection.containsAll(false, false));
        UnmodifiableBooleanBag emptyCollection = this.newWith();
        assertFalse(emptyCollection.containsAll(true));
        assertFalse(emptyCollection.containsAll(false));
        assertFalse(emptyCollection.containsAll(false, true, false));
        assertFalse(this.newWith(true, true).containsAll(false, true, false));

        UnmodifiableBooleanBag trueCollection = this.newWith(true, true, true, true);
        assertFalse(trueCollection.containsAll(true, false));
        UnmodifiableBooleanBag falseCollection = this.newWith(false, false, false, false);
        assertFalse(falseCollection.containsAll(true, false));
    }

    @Override
    @Test
    public void containsAllIterable()
    {
        UnmodifiableBooleanBag emptyCollection = this.newWith();
        assertTrue(emptyCollection.containsAll(new BooleanArrayList()));
        assertFalse(emptyCollection.containsAll(BooleanArrayList.newListWith(true)));
        assertFalse(emptyCollection.containsAll(BooleanArrayList.newListWith(false)));
        UnmodifiableBooleanBag collection = this.newWith(true, true, false, false, false);
        assertTrue(collection.containsAll(BooleanArrayList.newListWith(true)));
        assertTrue(collection.containsAll(BooleanArrayList.newListWith(false)));
        assertTrue(collection.containsAll(BooleanArrayList.newListWith(true, false)));
        assertTrue(collection.containsAll(BooleanArrayList.newListWith(true, true)));
        assertTrue(collection.containsAll(BooleanArrayList.newListWith(false, false)));
        assertTrue(collection.containsAll(BooleanArrayList.newListWith(true, false, true)));
        assertFalse(this.newWith(true, true).containsAll(BooleanArrayList.newListWith(false, true, false)));

        UnmodifiableBooleanBag trueCollection = this.newWith(true, true, true, true);
        assertFalse(trueCollection.containsAll(BooleanArrayList.newListWith(true, false)));
        UnmodifiableBooleanBag falseCollection = this.newWith(false, false, false, false);
        assertFalse(falseCollection.containsAll(BooleanArrayList.newListWith(true, false)));
    }

    @Override
    @Test
    public void asUnmodifiable()
    {
        super.asUnmodifiable();
        assertSame(this.bag, this.bag.asUnmodifiable());
        assertEquals(this.bag, this.bag.asUnmodifiable());
    }

    @Override
    @Test
    public void booleanIterator_with_remove()
    {
        MutableBooleanIterator booleanIterator = this.classUnderTest().booleanIterator();
        assertTrue(booleanIterator.hasNext());
        booleanIterator.next();
        assertThrows(UnsupportedOperationException.class, booleanIterator::remove);
    }

    @Override
    @Test
    public void iterator_throws_on_invocation_of_remove_before_next()
    {
        MutableBooleanIterator booleanIterator = this.classUnderTest().booleanIterator();
        assertTrue(booleanIterator.hasNext());
        assertThrows(UnsupportedOperationException.class, booleanIterator::remove);
    }

    @Override
    @Test
    public void iterator_throws_on_consecutive_invocation_of_remove()
    {
        // Not applicable for Unmodifiable*
    }

    @Override
    @Test
    public void selectUnique()
    {
        super.selectUnique();

        MutableBooleanBag bag = this.classUnderTest();
        MutableBooleanSet expected = BooleanSets.mutable.with(false);
        MutableBooleanSet actual = bag.selectUnique();
        assertEquals(expected, actual);
    }
}
