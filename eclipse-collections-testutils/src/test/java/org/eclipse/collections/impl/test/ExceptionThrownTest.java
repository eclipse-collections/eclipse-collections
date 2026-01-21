/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.test;

import java.util.concurrent.Callable;

import org.junit.Assert;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;

/**
 * JUnit test to make sure that methods like {@link Assert#assertThrows(Class, ThrowingRunnable)} really throw when
 * they ought to.
 */
@SuppressWarnings("AssertionFailureIgnored")
public class ExceptionThrownTest
{
    @Test
    public void specificRuntimeException()
    {
        try
        {
            assertThrows(NullPointerException.class, new EmptyRunnable());
            fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains("org.junit.Assert.assertThrows", e.getStackTrace()[0].toString());
        }
    }

    @Test
    public void callableException()
    {
        try
        {
            Verify.assertThrows(NullPointerException.class, new EmptyCallable());
            fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(ExceptionThrownTest.class.getName(), e.getStackTrace()[2].toString());
        }
    }

    @Test
    public void nullCause()
    {
        try
        {
            Verify.assertThrowsWithCause(
                    IllegalStateException.class,
                    IllegalArgumentException.class,
                    () ->
                    {
                        throw new IllegalStateException();
                    });
            fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(ExceptionThrownTest.class.getName(), e.getStackTrace()[4].toString());
        }
    }

    private static final class EmptyRunnable implements Runnable, ThrowingRunnable
    {
        @Override
        public void run()
        {
        }
    }

    private static final class EmptyCallable implements Callable<Void>
    {
        @Override
        public Void call()
        {
            return null;
        }
    }
}
