/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.block.predicate;

import org.eclipse.collections.impl.test.Verify;
import org.junit.jupiter.api.Test;

public class PairPredicateSerializationTest
{
    private static final PairPredicate<Object, Object> PAIR_PREDICATE = new PairPredicate<Object, Object>()
    {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean accept(Object argument1, Object argument2)
        {
            return false;
        }
    };

    @Test
    public void serializedForm()
    {
        Verify.assertSerializedForm(
                1L,
                "rO0ABXNyAE1vcmcuZWNsaXBzZS5jb2xsZWN0aW9ucy5pbXBsLmJsb2NrLnByZWRpY2F0ZS5QYWly\n"
                        + "UHJlZGljYXRlU2VyaWFsaXphdGlvblRlc3QkMQAAAAAAAAABAgAAeHIAOm9yZy5lY2xpcHNlLmNv\n"
                        + "bGxlY3Rpb25zLmltcGwuYmxvY2sucHJlZGljYXRlLlBhaXJQcmVkaWNhdGUAAAAAAAAAAQIAAHhw\n",
                PAIR_PREDICATE);
    }
}
