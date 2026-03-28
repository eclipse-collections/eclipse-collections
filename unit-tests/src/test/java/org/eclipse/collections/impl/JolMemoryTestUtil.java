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

import java.lang.management.ManagementFactory;

import com.sun.management.HotSpotDiagnosticMXBean;
import com.sun.management.VMOption;
import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.info.GraphLayout;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JolMemoryTestUtil
{
    private static final boolean IS_COH_ENABLED = JolMemoryTestUtil.checkIfCompactObjectHeadersEnabled();

    private JolMemoryTestUtil()
    {
    }

    private static boolean checkIfCompactObjectHeadersEnabled()
    {
        int majorVersion = Runtime.version().major();

        if (majorVersion >= 24)
        {
            HotSpotDiagnosticMXBean bean = ManagementFactory.getPlatformMXBean(HotSpotDiagnosticMXBean.class);
            VMOption option = bean.getVMOption("UseCompactObjectHeaders");
            return "true".equals(option.getValue());
        }
        return false;
    }

    public static void assertClassMemoryEquals(int expected, int expectedCOH, Object instance)
    {
        int memoryRequired = IS_COH_ENABLED ? expectedCOH : expected;
        assertEquals(memoryRequired, ClassLayout.parseInstance(instance).instanceSize());
    }

    public static void assertGraphMemoryEquals(int expected, int expectedCOH, Object instance)
    {
        int memoryRequired = IS_COH_ENABLED ? expectedCOH : expected;
        assertEquals(memoryRequired, GraphLayout.parseInstance(instance).totalSize());
    }
}
