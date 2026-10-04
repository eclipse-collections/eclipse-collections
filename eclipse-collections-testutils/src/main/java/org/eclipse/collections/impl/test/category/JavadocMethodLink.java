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

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.utility.ArrayIterate;

/**
 * Formats a method and its types exactly as they appear in a Javadoc category index.
 * <p>
 * Used both to generate a method link and to match a parsed link back to its method.
 */
final class JavadocMethodLink
{
    private final Method method;

    private JavadocMethodLink(Method method)
    {
        this.method = method;
    }

    static JavadocMethodLink of(Method method)
    {
        return new JavadocMethodLink(method);
    }

    /**
     * Returns the Javadoc link for the method.
     */
    String link()
    {
        return "{@link #" + this.signature() + '}';
    }

    /**
     * Returns the method signature as used in a Javadoc link.
     */
    String signature()
    {
        Class<?>[] parameters = this.method.getParameterTypes();
        MutableList<String> parameterTypes = ArrayIterate.zipWithIndex(parameters)
                .collect(pair -> JavadocMethodLink.toType(
                        pair.getOne(),
                        this.isVarArgsParameter(pair.getTwo())));
        return this.method.getName() + '(' + parameterTypes.makeString(", ") + ')';
    }

    /**
     * Returns whether the parameter at {@code index} is the var-args parameter.
     * <p>
     * A var-args parameter is always the last declared parameter of the method, so this is only true when the method
     * is var-args and {@code index} refers to that final parameter. Any earlier array parameter of a var-args method
     * (for example the {@code int[]} in {@code foo(int[], Object...)}) is a regular array.
     */
    private boolean isVarArgsParameter(int index)
    {
        return this.method.isVarArgs() && index == this.method.getParameterCount() - 1;
    }

    /**
     * Returns the simple Javadoc type name, using {@code ...} for a var-args array and {@code []} otherwise.
     */
    private static String toType(Class<?> type, boolean varArgsParameter)
    {
        if (varArgsParameter)
        {
            return JavadocMethodLink.toType(type.getComponentType(), false) + "...";
        }
        if (type.isArray())
        {
            return JavadocMethodLink.toType(type.getComponentType(), false) + "[]";
        }
        if (type.isPrimitive())
        {
            return type.getName();
        }
        return JavadocMethodLink.simpleNestedTypeName(type);
    }

    private static String simpleNestedTypeName(Class<?> type)
    {
        Class<?> enclosingClass = type.getEnclosingClass();
        if (enclosingClass == null)
        {
            return type.getSimpleName();
        }
        return JavadocMethodLink.simpleNestedTypeName(enclosingClass) + '.' + type.getSimpleName();
    }
}
