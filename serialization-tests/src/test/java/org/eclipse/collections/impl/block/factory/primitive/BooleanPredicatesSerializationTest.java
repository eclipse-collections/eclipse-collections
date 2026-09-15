/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.block.factory.primitive;

import org.eclipse.collections.api.block.predicate.primitive.BooleanPredicate;
import org.eclipse.collections.impl.test.Verify;
import org.junit.jupiter.api.Test;

public class BooleanPredicatesSerializationTest
{
    private static final BooleanPredicate PREDICATE = new BooleanPredicate()
    {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean accept(boolean value)
        {
            return false;
        }
    };

    @Test
    public void alwaysFalse()
    {
        Verify.assertSerializedForm(
                1L,
                "rO0ABXNyAGJvcmcuZWNsaXBzZS5jb2xsZWN0aW9ucy5pbXBsLmJsb2NrLmZhY3RvcnkucHJpbWl0\n"
                        + "aXZlLkJvb2xlYW5QcmVkaWNhdGVzJEFsd2F5c0ZhbHNlQm9vbGVhblByZWRpY2F0ZQAAAAAAAAAB\n"
                        + "AgAAeHA=",
                BooleanPredicates.alwaysFalse());
    }

    @Test
    public void alwaysTrue()
    {
        Verify.assertSerializedForm(
                1L,
                "rO0ABXNyAGFvcmcuZWNsaXBzZS5jb2xsZWN0aW9ucy5pbXBsLmJsb2NrLmZhY3RvcnkucHJpbWl0\n"
                        + "aXZlLkJvb2xlYW5QcmVkaWNhdGVzJEFsd2F5c1RydWVCb29sZWFuUHJlZGljYXRlAAAAAAAAAAEC\n"
                        + "AAB4cA==",
                BooleanPredicates.alwaysTrue());
    }

    @Test
    public void isTrue()
    {
        Verify.assertSerializedForm(
                1L,
                "rO0ABXNyAF1vcmcuZWNsaXBzZS5jb2xsZWN0aW9ucy5pbXBsLmJsb2NrLmZhY3RvcnkucHJpbWl0\n"
                        + "aXZlLkJvb2xlYW5QcmVkaWNhdGVzJElzVHJ1ZUJvb2xlYW5QcmVkaWNhdGUAAAAAAAAAAQIAAHhw\n",
                BooleanPredicates.isTrue());
    }

    @Test
    public void isFalse()
    {
        Verify.assertSerializedForm(
                1L,
                "rO0ABXNyAF5vcmcuZWNsaXBzZS5jb2xsZWN0aW9ucy5pbXBsLmJsb2NrLmZhY3RvcnkucHJpbWl0\n"
                        + "aXZlLkJvb2xlYW5QcmVkaWNhdGVzJElzRmFsc2VCb29sZWFuUHJlZGljYXRlAAAAAAAAAAECAAB4\n"
                        + "cA==",
                BooleanPredicates.isFalse());
    }

    @Test
    public void not()
    {
        Verify.assertSerializedForm(
                1L,
                "rO0ABXNyAF5vcmcuZWNsaXBzZS5jb2xsZWN0aW9ucy5pbXBsLmJsb2NrLmZhY3RvcnkucHJpbWl0\n"
                        + "aXZlLkJvb2xlYW5QcmVkaWNhdGVzJElzRmFsc2VCb29sZWFuUHJlZGljYXRlAAAAAAAAAAECAAB4\n"
                        + "cA==",
                BooleanPredicates.not(BooleanPredicates.isTrue()));
    }

    @Test
    public void and()
    {
        Verify.assertSerializedForm(
                1L,
                "rO0ABXNyAFVvcmcuZWNsaXBzZS5jb2xsZWN0aW9ucy5pbXBsLmJsb2NrLmZhY3RvcnkucHJpbWl0\n"
                        + "aXZlLkJvb2xlYW5QcmVkaWNhdGVzJEZhbHNlUHJlZGljYXRlAAAAAAAAAAECAAB4cA==",
                BooleanPredicates.and(BooleanPredicates.isTrue(), BooleanPredicates.isFalse()));
    }

    @Test
    public void or()
    {
        Verify.assertSerializedForm(
                1L,
                "rO0ABXNyAFRvcmcuZWNsaXBzZS5jb2xsZWN0aW9ucy5pbXBsLmJsb2NrLmZhY3RvcnkucHJpbWl0\n"
                        + "aXZlLkJvb2xlYW5QcmVkaWNhdGVzJFRydWVQcmVkaWNhdGUAAAAAAAAAAQIAAHhw",
                BooleanPredicates.or(BooleanPredicates.isTrue(), BooleanPredicates.isFalse()));
    }

    @Test
    public void not_custom()
    {
        Verify.assertSerializedForm(
                1L,
                "rO0ABXNyAFpvcmcuZWNsaXBzZS5jb2xsZWN0aW9ucy5pbXBsLmJsb2NrLmZhY3RvcnkucHJpbWl0\n"
                        + "aXZlLkJvb2xlYW5QcmVkaWNhdGVzJE5vdEJvb2xlYW5QcmVkaWNhdGUAAAAAAAAAAQIAAUwABm5l\n"
                        + "Z2F0ZXQASExvcmcvZWNsaXBzZS9jb2xsZWN0aW9ucy9hcGkvYmxvY2svcHJlZGljYXRlL3ByaW1p\n"
                        + "dGl2ZS9Cb29sZWFuUHJlZGljYXRlO3hwc3IAWW9yZy5lY2xpcHNlLmNvbGxlY3Rpb25zLmltcGwu\n"
                        + "YmxvY2suZmFjdG9yeS5wcmltaXRpdmUuQm9vbGVhblByZWRpY2F0ZXNTZXJpYWxpemF0aW9uVGVz\n"
                        + "dCQxAAAAAAAAAAECAAB4cA==",
                BooleanPredicates.not(PREDICATE));
    }

    @Test
    public void and_custom()
    {
        Verify.assertSerializedForm(
                1L,
                "rO0ABXNyAFpvcmcuZWNsaXBzZS5jb2xsZWN0aW9ucy5pbXBsLmJsb2NrLmZhY3RvcnkucHJpbWl0\n"
                        + "aXZlLkJvb2xlYW5QcmVkaWNhdGVzJEFuZEJvb2xlYW5QcmVkaWNhdGUAAAAAAAAAAQIAAkwAA29u\n"
                        + "ZXQASExvcmcvZWNsaXBzZS9jb2xsZWN0aW9ucy9hcGkvYmxvY2svcHJlZGljYXRlL3ByaW1pdGl2\n"
                        + "ZS9Cb29sZWFuUHJlZGljYXRlO0wAA3R3b3EAfgABeHBzcgBZb3JnLmVjbGlwc2UuY29sbGVjdGlv\n"
                        + "bnMuaW1wbC5ibG9jay5mYWN0b3J5LnByaW1pdGl2ZS5Cb29sZWFuUHJlZGljYXRlc1NlcmlhbGl6\n"
                        + "YXRpb25UZXN0JDEAAAAAAAAAAQIAAHhwcQB+AAQ=",
                BooleanPredicates.and(PREDICATE, PREDICATE));
    }

    @Test
    public void or_custom()
    {
        Verify.assertSerializedForm(
                1L,
                "rO0ABXNyAFlvcmcuZWNsaXBzZS5jb2xsZWN0aW9ucy5pbXBsLmJsb2NrLmZhY3RvcnkucHJpbWl0\n"
                        + "aXZlLkJvb2xlYW5QcmVkaWNhdGVzJE9yQm9vbGVhblByZWRpY2F0ZQAAAAAAAAABAgACTAADb25l\n"
                        + "dABITG9yZy9lY2xpcHNlL2NvbGxlY3Rpb25zL2FwaS9ibG9jay9wcmVkaWNhdGUvcHJpbWl0aXZl\n"
                        + "L0Jvb2xlYW5QcmVkaWNhdGU7TAADdHdvcQB+AAF4cHNyAFlvcmcuZWNsaXBzZS5jb2xsZWN0aW9u\n"
                        + "cy5pbXBsLmJsb2NrLmZhY3RvcnkucHJpbWl0aXZlLkJvb2xlYW5QcmVkaWNhdGVzU2VyaWFsaXph\n"
                        + "dGlvblRlc3QkMQAAAAAAAAABAgAAeHBxAH4ABA==",
                BooleanPredicates.or(PREDICATE, PREDICATE));
    }
}
