/*
 * Copyright (c) 2026 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.set.mutable.sorted;

import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

public class TreeSetNoIterator<T> extends TreeSet<T>
{
    public TreeSetNoIterator()
    {
    }

    public TreeSetNoIterator(Comparator<? super T> comparator)
    {
        super(comparator);
    }

    @Override
    public Iterator<T> iterator()
    {
        throw new AssertionError("No iteration patterns should delegate to iterator()");
    }

    @Override
    public Iterator<T> descendingIterator()
    {
        throw new AssertionError("No iteration patterns should delegate to descendingIterator()");
    }
}
