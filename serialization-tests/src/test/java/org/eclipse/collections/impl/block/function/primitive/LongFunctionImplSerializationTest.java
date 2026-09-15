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

public final class LongFunctionImplSerializationTest
{
    private static final LongFunctionImpl<?> LONG_FUNCTION = new LongFunctionImpl<Object>()
    {
        private static final long serialVersionUID = 1L;

        @Override
        public long longValueOf(Object anObject)
        {
            return 0;
        }
    };

    @Test
    public void serializedForm()
    {
        Verify.assertSerializedForm(
                1L,
                "rO0ABXNyAFlvcmcuZWNsaXBzZS5jb2xsZWN0aW9ucy5pbXBsLmJsb2NrLmZ1bmN0aW9uLnByaW1p\n"
                        + "dGl2ZS5Mb25nRnVuY3Rpb25JbXBsU2VyaWFsaXphdGlvblRlc3QkMQAAAAAAAAABAgAAeHIARm9y\n"
                        + "Zy5lY2xpcHNlLmNvbGxlY3Rpb25zLmltcGwuYmxvY2suZnVuY3Rpb24ucHJpbWl0aXZlLkxvbmdG\n"
                        + "dW5jdGlvbkltcGwAAAAAAAAAAQIAAHhw",
                LONG_FUNCTION);
    }
}
