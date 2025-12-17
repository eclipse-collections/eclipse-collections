/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.block.function.primitive;

import org.eclipse.collections.impl.test.Verify;
import org.junit.jupiter.api.Test;

public final class DoubleFunctionImplSerializationTest
{
    private static final DoubleFunctionImpl<?> DOUBLE_FUNCTION = new DoubleFunctionImpl<Object>()
    {
        private static final long serialVersionUID = 1L;

        @Override
        public double doubleValueOf(Object anObject)
        {
            return 0;
        }
    };

    @Test
    public void serializedForm()
    {
        Verify.assertSerializedForm(
                1L,
                "rO0ABXNyAFtvcmcuZWNsaXBzZS5jb2xsZWN0aW9ucy5pbXBsLmJsb2NrLmZ1bmN0aW9uLnByaW1p\n"
                        + "dGl2ZS5Eb3VibGVGdW5jdGlvbkltcGxTZXJpYWxpemF0aW9uVGVzdCQxAAAAAAAAAAECAAB4cgBI\n"
                        + "b3JnLmVjbGlwc2UuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mdW5jdGlvbi5wcmltaXRpdmUuRG91\n"
                        + "YmxlRnVuY3Rpb25JbXBsAAAAAAAAAAECAAB4cA==",
                DOUBLE_FUNCTION);
    }
}
