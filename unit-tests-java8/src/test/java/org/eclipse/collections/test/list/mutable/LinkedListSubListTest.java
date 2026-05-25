/*
 * Copyright (c) 2026 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.list.mutable;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.collections.test.list.ListTestCase;

public class LinkedListSubListTest implements ListTestCase
{
    @SafeVarargs
    @Override
    public final <T> List<T> newWith(T... elements)
    {
        List<T> list = new LinkedList<>();
        list.add(null);
        Collections.addAll(list, elements);
        list.add(null);
        return list.subList(1, 1 + elements.length);
    }

    @Override
    public boolean allowsSerialization()
    {
        return false;
    }
}
