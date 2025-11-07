/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.codegenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class BasicErrorListener implements ErrorListener
{
    private final List<String> errors = Collections.synchronizedList(new ArrayList<String>());

    @Override
    public void error(String errorMessage)
    {
        errors.add(errorMessage);
    }

    public boolean hasErrors()
    {
        return errors.size() > 0;
    }

    @Override
    public String toString()
    {
        return String.valueOf(errors);
    }
}
