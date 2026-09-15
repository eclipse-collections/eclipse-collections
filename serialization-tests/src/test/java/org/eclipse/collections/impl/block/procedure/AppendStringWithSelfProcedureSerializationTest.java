/*
 * Copyright (c) 2026 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.block.procedure;

import java.util.Collections;

import org.eclipse.collections.impl.test.Verify;
import org.junit.jupiter.api.Test;

public class AppendStringWithSelfProcedureSerializationTest
{
    @Test
    public void serializedForm()
    {
        Verify.assertSerializedForm(
                1L,
                "rO0ABXNyAEpvcmcuZWNsaXBzZS5jb2xsZWN0aW9ucy5pbXBsLmJsb2NrLnByb2NlZHVyZS5BcHBl\n"
                        + "bmRTdHJpbmdXaXRoU2VsZlByb2NlZHVyZQAAAAAAAAABAgAFWgAFZmlyc3RMAAphcHBlbmRhYmxl\n"
                        + "dAAWTGphdmEvbGFuZy9BcHBlbmRhYmxlO0wACGl0ZXJhYmxldAAUTGphdmEvbGFuZy9JdGVyYWJs\n"
                        + "ZTtMAA1zZWxmUmVmZXJlbmNldAASTGphdmEvbGFuZy9TdHJpbmc7TAAJc2VwYXJhdG9ycQB+AAN4\n"
                        + "cAFzcgAXamF2YS5sYW5nLlN0cmluZ0J1aWxkZXI81fsUWkxqywMAAHhwdwQAAAAAdXIAAltDsCZm\n"
                        + "sOJdhKwCAAB4cAAAABAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAHhzcgAfamF2YS51\n"
                        + "dGlsLkNvbGxlY3Rpb25zJEVtcHR5TGlzdHq4F7Q8p57eAgAAeHB0ABEodGhpcyBDb2xsZWN0aW9u\n"
                        + "KXQAAiwg",
                new AppendStringWithSelfProcedure<>(
                        new StringBuilder(),
                        ", ",
                        Collections.emptyList(),
                        "(this Collection)"));
    }
}
