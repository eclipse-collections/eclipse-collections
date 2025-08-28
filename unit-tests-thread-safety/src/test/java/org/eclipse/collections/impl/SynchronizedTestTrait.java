/*
 * Copyright (c) 2026 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public interface SynchronizedTestTrait
{
    Object getClassUnderTest();

    class Gate
    {
        private final CountDownLatch latch = new CountDownLatch(1);

        public void open()
        {
            this.latch.countDown();
        }

        public void await()
                throws InterruptedException
        {
            this.latch.await();
        }
    }

    default long time(Runnable code)
    {
        long before = System.currentTimeMillis();
        code.run();
        long after = System.currentTimeMillis();
        return after - before;
    }

    default void assertSynchronized(Runnable code)
    {
        this.assertThreadSafety(true, 10L, TimeUnit.MILLISECONDS, code);
    }

    default void assertNotSynchronized(Runnable code)
    {
        this.assertThreadSafety(false, 60L, TimeUnit.SECONDS, code);
    }

    default void assertThreadSafety(
            boolean threadSafe,
            long timeout,
            TimeUnit timeUnit,
            Runnable code)
    {
        Gate gate = new Gate();

        Thread lockHolderThread = this.spawn(() ->
        {
            synchronized (this.getClassUnderTest())
            {
                gate.open();

                try
                {
                    Thread.sleep(Long.MAX_VALUE);
                }
                catch (InterruptedException ignore)
                {
                    Thread.currentThread().interrupt();
                }
            }
        });

        long millisTimeout = TimeUnit.MILLISECONDS.convert(timeout, timeUnit);
        long measuredTime = this.time(() ->
        {
            try
            {
                gate.await();
                Thread codeThread = this.spawn(code);
                codeThread.join(millisTimeout, 0);
            }
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        });

        assertEquals(
                threadSafe,
                measuredTime >= millisTimeout,
                () -> "Measured %d ms but timeout was %d ms.".formatted(measuredTime, millisTimeout));

        lockHolderThread.interrupt();
        try
        {
            lockHolderThread.join();
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    default Thread spawn(Runnable code)
    {
        Thread result = new Thread(code);
        result.start();
        return result;
    }
}
