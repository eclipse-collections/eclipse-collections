/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.set.mutable;

import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.impl.collection.mutable.UnmodifiableMutableCollectionTestCase;
import org.junit.jupiter.api.Test;

public class MultiReaderUnifiedSetAsUnmodifiableTest extends UnmodifiableMutableCollectionTestCase<Integer>
{
    @Override
    protected MutableCollection<Integer> getCollection()
    {
        return MultiReaderUnifiedSet.newSetWith(1).asUnmodifiable();
    }

    @Override
    @Test
    public void iteratorRemove()
    {
        // Not applicable
    }
}
