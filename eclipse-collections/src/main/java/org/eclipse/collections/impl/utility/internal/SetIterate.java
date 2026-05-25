/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.utility.internal;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * @since 1.0
 */
public final class SetIterate
{
    private SetIterate()
    {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }

    /**
     * @see Collection#removeAll(Collection)
     * @since 1.0
     */
    public static boolean removeAllIterable(Set<?> collection, Iterable<?> iterable)
    {
        if (iterable instanceof Set<?> set && set.size() > collection.size())
        {
            boolean modified = false;
            Iterator<?> e = collection.iterator();
            while (e.hasNext())
            {
                if (set.contains(e.next()))
                {
                    e.remove();
                    modified = true;
                }
            }
            return modified;
        }

        boolean modified = false;
        for (Object each : iterable)
        {
            if (collection.remove(each))
            {
                modified = true;
            }
        }
        return modified;
    }
}
