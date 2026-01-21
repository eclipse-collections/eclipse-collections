/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl;

import org.eclipse.collections.impl.block.function.primitive.IntegerFunctionImpl;
import org.eclipse.collections.impl.test.Verify;
import org.junit.jupiter.api.Test;

public class IntegerFunctionImplSerializationTest
{
    private static final IntegerFunctionImpl<Object> FUNCTION = new IntegerFunctionImpl<Object>()
    {
        private static final long serialVersionUID = 1L;

        @Override
        public int intValueOf(Object o)
        {
            return 0;
        }
    };

    @Test
    public void serializedForm()
    {
        Verify.assertSerializedForm(
                1L,
                "rO0ABXNyAENvcmcuZWNsaXBzZS5jb2xsZWN0aW9ucy5pbXBsLkludGVnZXJGdW5jdGlvbkltcGxT\n"
                        + "ZXJpYWxpemF0aW9uVGVzdCQxAAAAAAAAAAECAAB4cgBJb3JnLmVjbGlwc2UuY29sbGVjdGlvbnMu\n"
                        + "aW1wbC5ibG9jay5mdW5jdGlvbi5wcmltaXRpdmUuSW50ZWdlckZ1bmN0aW9uSW1wbAAAAAAAAAAB\n"
                        + "AgAAeHA=",
                FUNCTION);
    }
}
