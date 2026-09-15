/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.block.procedure;

import org.eclipse.collections.impl.test.Verify;
import org.junit.jupiter.api.Test;

public class ComparatorProcedureSerializationTest
{
    private static final ComparatorProcedure<Object> COMPARATOR_PROCEDURE = new ComparatorProcedure<Object>(null)
    {
        private static final long serialVersionUID = 1L;

        @Override
        public void value(Object each)
        {
        }
    };

    @Test
    public void serializedForm()
    {
        Verify.assertSerializedForm(
                1L,
                "rO0ABXNyAFNvcmcuZWNsaXBzZS5jb2xsZWN0aW9ucy5pbXBsLmJsb2NrLnByb2NlZHVyZS5Db21w\n"
                        + "YXJhdG9yUHJvY2VkdXJlU2VyaWFsaXphdGlvblRlc3QkMQAAAAAAAAABAgAAeHIAQG9yZy5lY2xp\n"
                        + "cHNlLmNvbGxlY3Rpb25zLmltcGwuYmxvY2sucHJvY2VkdXJlLkNvbXBhcmF0b3JQcm9jZWR1cmUA\n"
                        + "AAAAAAAAAQIAA1oAEnZpc2l0ZWRBdExlYXN0T25jZUwACmNvbXBhcmF0b3J0ABZMamF2YS91dGls\n"
                        + "L0NvbXBhcmF0b3I7TAAGcmVzdWx0dAASTGphdmEvbGFuZy9PYmplY3Q7eHAAcHA=",
                COMPARATOR_PROCEDURE);
    }
}
