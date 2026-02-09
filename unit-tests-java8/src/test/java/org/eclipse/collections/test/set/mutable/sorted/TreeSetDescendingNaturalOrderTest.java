/*
 * Copyright (c) 2025 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.set.mutable.sorted;

import java.util.Collections;
import java.util.NavigableSet;
import java.util.TreeSet;

import org.eclipse.collections.test.set.sorted.NavigableSetTestCase;

public class TreeSetDescendingNaturalOrderTest
        implements NavigableSetTestCase
{
    @SafeVarargs
    @Override
    public final <T> NavigableSet<T> newWith(T... elements)
    {
        NavigableSet<T> treeSet = new TreeSet<>();
        Collections.addAll(treeSet, elements);
        return treeSet.descendingSet();
    }

    @Override
    public boolean allowsDuplicates()
    {
        return false;
    }
}
