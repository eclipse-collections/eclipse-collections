/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.bag.mutable.primitive;

import org.eclipse.collections.api.bag.primitive.MutableBooleanBag;
import org.eclipse.collections.api.set.primitive.MutableBooleanSet;
import org.eclipse.collections.impl.factory.primitive.BooleanSets;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * JUnit test for {@link SynchronizedBooleanBag}.
 */
public class SynchronizedBooleanBagTest extends AbstractMutableBooleanBagTestCase
{
    @Override
    protected final SynchronizedBooleanBag classUnderTest()
    {
        return new SynchronizedBooleanBag(BooleanHashBag.newBagWith(true, false, true));
    }

    @Override
    protected SynchronizedBooleanBag newWith(boolean... elements)
    {
        return new SynchronizedBooleanBag(BooleanHashBag.newBagWith(elements));
    }

    @Override
    @Test
    public void asSynchronized()
    {
        super.asSynchronized();
        MutableBooleanBag bagWithLockObject = new SynchronizedBooleanBag(BooleanHashBag.newBagWith(true, false, true), new Object());
        assertSame(bagWithLockObject, bagWithLockObject.asSynchronized());
        assertEquals(bagWithLockObject, bagWithLockObject.asSynchronized());
        MutableBooleanBag bag = this.classUnderTest();
        assertSame(bag, bag.asSynchronized());
        assertEquals(bag, bag.asSynchronized());
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
