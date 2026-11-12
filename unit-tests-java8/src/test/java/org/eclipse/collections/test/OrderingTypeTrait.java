/*
 * Copyright (c) 2026 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test;

import org.eclipse.collections.test.IterableTestCase.OrderingType;

/**
 * The iteration order of the instances under test. Shared by {@link IterableTestCase} and the java.util.Map contract tests, so a map test case that is both inherits one {@code getOrderingType()}.
 */
public interface OrderingTypeTrait
{
    default OrderingType getOrderingType()
    {
        return OrderingType.INSERTION_ORDER;
    }
}
