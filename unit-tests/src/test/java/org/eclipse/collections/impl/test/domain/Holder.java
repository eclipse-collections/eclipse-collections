/*
 * Copyright (c) 2025 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.test.domain;

import java.io.Serializable;

public final class Holder<T extends Comparable<? super T>>
        implements Comparable<Holder<T>>, Serializable
{
    private final T value;

    public Holder(T value)
    {
        this.value = value;
    }

    public T getValue()
    {
        return this.value;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass())
        {
            return false;
        }

        Holder<?> that = (Holder<?>) obj;

        return this.value.equals(that.value);
    }

    @Override
    public int hashCode()
    {
        return this.value.hashCode();
    }

    @Override
    public String toString()
    {
        return "Holder{" + this.value + "}";
    }

    @Override
    public int compareTo(Holder<T> o)
    {
        return this.value.compareTo(o.value);
    }
}
