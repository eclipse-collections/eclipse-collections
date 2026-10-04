/*
 * Copyright (c) 2026 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.test.category;

import java.lang.reflect.Method;

import org.eclipse.collections.impl.utility.ArrayIterate;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JavadocMethodLinkTest
{
    @Test
    public void signature()
    {
        assertEquals("noArgs()", JavadocMethodLink.of(method("noArgs")).signature());
        assertEquals(
                "primitives(int, long, boolean, char, byte, short, float, double)",
                JavadocMethodLink.of(method("primitives")).signature());
        assertEquals("array(int[])", JavadocMethodLink.of(method("array")).signature());
        assertEquals("varargs(String, Object...)", JavadocMethodLink.of(method("varargs")).signature());
        assertEquals("arrayThenVarargs(int[], Object...)", JavadocMethodLink.of(method("arrayThenVarargs")).signature());
        assertEquals("nested(JavadocMethodLinkTest.Outer.Inner)", JavadocMethodLink.of(method("nested")).signature());
        assertEquals("nestedArray(JavadocMethodLinkTest.Outer.Inner[])", JavadocMethodLink.of(method("nestedArray")).signature());
    }

    @Test
    public void link()
    {
        assertEquals("{@link #noArgs()}", JavadocMethodLink.of(method("noArgs")).link());
        assertEquals(
                "{@link #primitives(int, long, boolean, char, byte, short, float, double)}",
                JavadocMethodLink.of(method("primitives")).link());
        assertEquals("{@link #array(int[])}", JavadocMethodLink.of(method("array")).link());
        assertEquals("{@link #varargs(String, Object...)}", JavadocMethodLink.of(method("varargs")).link());
        assertEquals("{@link #arrayThenVarargs(int[], Object...)}", JavadocMethodLink.of(method("arrayThenVarargs")).link());
        assertEquals("{@link #nested(JavadocMethodLinkTest.Outer.Inner)}", JavadocMethodLink.of(method("nested")).link());
        assertEquals("{@link #nestedArray(JavadocMethodLinkTest.Outer.Inner[])}", JavadocMethodLink.of(method("nestedArray")).link());
    }

    private Method method(String name)
    {
        return ArrayIterate.detect(Sample.class.getDeclaredMethods(), candidate -> candidate.getName().equals(name));
    }

    private static final class Sample
    {
        void noArgs()
        {
        }

        void primitives(int intValue, long longValue, boolean booleanValue, char charValue, byte byteValue, short shortValue, float floatValue, double doubleValue)
        {
        }

        void array(int[] values)
        {
        }

        void varargs(String first, Object... rest)
        {
        }

        void arrayThenVarargs(int[] values, Object... rest)
        {
        }

        void nested(Outer.Inner value)
        {
        }

        void nestedArray(Outer.Inner[] values)
        {
        }
    }

    private static final class Outer
    {
        private static final class Inner
        {
        }
    }
}
