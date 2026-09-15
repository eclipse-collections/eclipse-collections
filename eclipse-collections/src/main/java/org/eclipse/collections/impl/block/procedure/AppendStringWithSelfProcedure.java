/*
 * Copyright (c) 2026 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.block.procedure;

import java.io.IOException;
import java.util.Objects;

import org.eclipse.collections.api.block.procedure.Procedure;

public class AppendStringWithSelfProcedure<T> implements Procedure<T>
{
    private static final long serialVersionUID = 1L;
    private final Iterable<?> iterable;
    private final String selfReference;
    private final Appendable appendable;
    private final String separator;
    private boolean first = true;

    public AppendStringWithSelfProcedure(
            Appendable appendable,
            String separator,
            Iterable<?> iterable,
            String selfReference)
    {
        this.iterable = Objects.requireNonNull(iterable);
        this.selfReference = Objects.requireNonNull(selfReference);
        this.appendable = Objects.requireNonNull(appendable);
        this.separator = Objects.requireNonNull(separator);
    }

    @Override
    public void value(T each)
    {
        try
        {
            if (this.first)
            {
                this.first = false;
            }
            else
            {
                this.appendable.append(this.separator);
            }
            this.appendable.append(each == this.iterable ? this.selfReference : String.valueOf(each));
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
