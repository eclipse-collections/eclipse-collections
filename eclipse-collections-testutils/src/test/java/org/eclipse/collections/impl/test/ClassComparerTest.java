/*
 * Copyright (c) 2021 The Bank of New York Mellon.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.test;

import java.util.Stack;
import java.util.Vector;
import java.util.concurrent.Callable;

import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.SortedSets;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.api.tuple.Triplet;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ClassComparerTest
{
    @Test
    public void getMethodNames()
    {
        ClassComparer comparer = new ClassComparer();
        MutableSortedSet<String> methods1 = comparer.getMethodNames(Runnable.class);
        MutableSortedSet<String> methods2 = comparer.getMethodNames(Callable.class);
        assertEquals(SortedSets.mutable.with("run"), methods1);
        assertEquals(SortedSets.mutable.with("call"), methods2);
        MutableSortedSet<String> methods3 = comparer.getMethodNames(java.util.function.Function.class);
        assertEquals(SortedSets.mutable.with("identity", "apply", "compose", "andThen"), methods3);
        MutableSortedSet<String> methods4 = comparer.getMethodNames(Function.class);
        assertEquals(SortedSets.mutable.with("valueOf", "apply", "compose", "andThen"), methods4);
    }

    @Test
    public void printClass()
    {
        CollectingAppendable out1 = new CollectingAppendable();
        ClassComparer comparer1 = new ClassComparer(out1);
        MutableSortedSet<String> methods1 = comparer1.printClass(Runnable.class);
        assertEquals(SortedSets.mutable.with("run"), methods1);
        MutableList<Object> expectedOutput1 = Lists.mutable.with(
                "Class: Runnable",
                System.lineSeparator(),
                "---------------",
                System.lineSeparator(),
                "r:[run]",
                System.lineSeparator(),
                System.lineSeparator());
        assertEquals(expectedOutput1, out1.getContents());

        CollectingAppendable out2 = new CollectingAppendable();
        ClassComparer comparer2 = new ClassComparer(out2);
        MutableSortedSet<String> methods2 = comparer2.printClass(java.util.function.Function.class);
        assertEquals(SortedSets.mutable.with("identity", "apply", "compose", "andThen"), methods2);
        MutableList<Object> expectedOutput2 = Lists.mutable.with(
                "Class: Function",
                System.lineSeparator(),
                "---------------",
                System.lineSeparator(),
                "a:[andThen, apply]"
                + System.lineSeparator()
                + "c:[compose]"
                + System.lineSeparator()
                + "i:[identity]",
                System.lineSeparator(),
                System.lineSeparator());
        assertEquals(expectedOutput2, out2.getContents());
    }

    @Test
    public void printClassWithPackage()
    {
        CollectingAppendable out1 = new CollectingAppendable();
        ClassComparer comparer1 = new ClassComparer(out1, false, false, true);
        MutableSortedSet<String> methods1 = comparer1.printClass(Runnable.class);
        assertEquals(SortedSets.mutable.with("run"), methods1);
        MutableList<Object> expectedOutput1 = Lists.mutable.with(
                "Class: java.lang.Runnable",
                System.lineSeparator(),
                "-------------------------",
                System.lineSeparator(),
                "r:[run]",
                System.lineSeparator(),
                System.lineSeparator());
        assertEquals(expectedOutput1, out1.getContents());
    }

    @Test
    public void difference()
    {
        ClassComparer comparer = new ClassComparer();
        MutableSortedSet<String> difference1 = comparer.difference(Runnable.class, Callable.class);
        MutableSortedSet<String> difference2 = comparer.difference(Callable.class, Runnable.class);
        assertEquals(SortedSets.mutable.with("run"), difference1);
        assertEquals(SortedSets.mutable.with("call"), difference2);
        MutableSortedSet<String> difference3 = comparer.difference(java.util.function.Function.class, Function.class);
        assertEquals(SortedSets.mutable.with("identity"), difference3);
        MutableSortedSet<String> difference4 = comparer.difference(Function.class, java.util.function.Function.class);
        assertEquals(SortedSets.mutable.with("valueOf"), difference4);
    }

    @Test
    public void printDifference()
    {
        CollectingAppendable out = new CollectingAppendable();
        ClassComparer comparer = new ClassComparer(out);
        MutableSortedSet<String> difference1 = comparer.printDifference(Runnable.class, Callable.class);
        assertEquals(SortedSets.mutable.with("run"), difference1);
        MutableList<Object> expectedOutput = Lists.mutable.with(
                "Difference (Runnable, Callable)",
                System.lineSeparator(),
                "-------------------------------",
                System.lineSeparator(),
                "r:[run]",
                System.lineSeparator(),
                System.lineSeparator());
        assertEquals(expectedOutput, out.getContents());
    }

    @Test
    public void printDifferenceWithPackages()
    {
        CollectingAppendable out = new CollectingAppendable();
        ClassComparer comparer = new ClassComparer(out, false, false, true);
        MutableSortedSet<String> difference1 = comparer.printDifference(Runnable.class, Callable.class);
        assertEquals(SortedSets.mutable.with("run"), difference1);
        MutableList<Object> expectedOutput = Lists.mutable.with(
                "Difference (java.lang.Runnable, java.util.concurrent.Callable)",
                System.lineSeparator(),
                "--------------------------------------------------------------",
                System.lineSeparator(),
                "r:[run]",
                System.lineSeparator(),
                System.lineSeparator());
        assertEquals(expectedOutput, out.getContents());
    }

    @Test
    public void differenceParams()
    {
        ClassComparer comparer = new ClassComparer(true, false, false);
        MutableSortedSet<String> difference1 = comparer.difference(Runnable.class, Callable.class);
        MutableSortedSet<String> difference2 = comparer.difference(Callable.class, Runnable.class);
        assertEquals(SortedSets.mutable.with("run()"), difference1);
        assertEquals(SortedSets.mutable.with("call()"), difference2);
        MutableSortedSet<String> difference3 = comparer.difference(java.util.function.Function.class, Function.class);
        assertEquals(SortedSets.mutable.with("identity()"), difference3);
        MutableSortedSet<String> difference4 = comparer.difference(Function.class, java.util.function.Function.class);
        assertEquals(SortedSets.mutable.with("valueOf(Object)"), difference4);
    }

    @Test
    public void differenceParamsAndReturnTypes()
    {
        ClassComparer comparer = new ClassComparer(true, true, false);
        MutableSortedSet<String> difference1 = comparer.difference(Runnable.class, Callable.class);
        MutableSortedSet<String> difference2 = comparer.difference(Callable.class, Runnable.class);
        assertEquals(SortedSets.mutable.with("run():void"), difference1);
        assertEquals(SortedSets.mutable.with("call():Object"), difference2);
        MutableSortedSet<String> difference3 = comparer.difference(java.util.function.Function.class, Function.class);
        assertEquals(SortedSets.mutable.with("identity():Function"), difference3);
        MutableSortedSet<String> difference4 = comparer.difference(Function.class, java.util.function.Function.class);
        assertEquals(SortedSets.mutable.with("valueOf(Object):Object"), difference4);
    }

    @Test
    public void symmetricDifference()
    {
        ClassComparer comparer = new ClassComparer();
        MutableSortedSet<String> symmetricDifference = comparer.symmetricDifference(Runnable.class, Callable.class);
        assertEquals(SortedSets.mutable.with("run", "call"), symmetricDifference);
    }

    @Test
    public void printSymmetricDifference()
    {
        CollectingAppendable out = new CollectingAppendable();
        ClassComparer comparer = new ClassComparer(out);
        MutableSortedSet<String> symmetricDifference = comparer.printSymmetricDifference(Runnable.class, Callable.class);
        assertEquals(SortedSets.mutable.with("run", "call"), symmetricDifference);
        MutableList<Object> expectedOutput = Lists.mutable.with(
                "Symmetric Difference (Runnable, Callable)",
                System.lineSeparator(),
                "-----------------------------------------",
                System.lineSeparator(),
                "c:[call]" + System.lineSeparator() + "r:[run]",
                System.lineSeparator(),
                System.lineSeparator());
        assertEquals(expectedOutput, out.getContents());
    }

    @Test
    public void symmetricDifferenceParams()
    {
        ClassComparer comparer = new ClassComparer(true, false, false);
        MutableSortedSet<String> symmetricDifference = comparer.symmetricDifference(Runnable.class, Callable.class);
        assertEquals(SortedSets.mutable.with("run()", "call()"), symmetricDifference);
    }

    @Test
    public void symmetricDifferenceParamsAndReturnTypes()
    {
        ClassComparer comparer = new ClassComparer(true, true, false);
        MutableSortedSet<String> symmetricDifference = comparer.symmetricDifference(Runnable.class, Callable.class);
        assertEquals(SortedSets.mutable.with("run():void", "call():Object"), symmetricDifference);
        MutableSortedSet<String> symmetricDifference2 =
                comparer.symmetricDifference(java.util.function.Function.class, Function.class);
        assertEquals(
                SortedSets.mutable.with("identity():Function", "valueOf(Object):Object"),
                symmetricDifference2);
    }

    @Test
    public void intersection()
    {
        ClassComparer comparer = new ClassComparer();
        MutableSortedSet<String> intersection1 = comparer.intersect(Runnable.class, Callable.class);
        assertEquals(SortedSets.mutable.empty(), intersection1);
        MutableSortedSet<String> intersection2 = comparer.intersect(java.util.function.Function.class, Function.class);
        assertEquals(SortedSets.mutable.with("apply", "compose", "andThen"), intersection2);
    }

    @Test
    public void printIntersection()
    {
        CollectingAppendable out = new CollectingAppendable();
        ClassComparer comparer = new ClassComparer(out);
        MutableSortedSet<String> intersection = comparer.printIntersection(Runnable.class, Callable.class);
        assertEquals(SortedSets.mutable.empty(), intersection);
        MutableList<Object> expectedOutput = Lists.mutable.with(
                "Intersection (Runnable, Callable)",
                System.lineSeparator(),
                "---------------------------------",
                System.lineSeparator(),
                "",
                System.lineSeparator(),
                System.lineSeparator());
        assertEquals(expectedOutput, out.getContents());
    }

    @Test
    public void intersectionParams()
    {
        ClassComparer comparer = new ClassComparer(true, false, false);
        MutableSortedSet<String> intersection1 = comparer.intersect(Runnable.class, Callable.class);
        assertEquals(SortedSets.mutable.empty(), intersection1);
        MutableSortedSet<String> intersection2 = comparer.intersect(java.util.function.Function.class, Function.class);
        assertEquals(
                SortedSets.mutable.with("apply(Object)", "compose(Function)", "andThen(Function)"),
                intersection2);
    }

    @Test
    public void intersectionParamsAndReturnTypes()
    {
        ClassComparer comparer = new ClassComparer(true, true, false);
        MutableSortedSet<String> intersection1 = comparer.intersect(Runnable.class, Callable.class);
        assertEquals(SortedSets.mutable.empty(), intersection1);
        MutableSortedSet<String> intersection2 = comparer.intersect(java.util.function.Function.class, Function.class);
        assertEquals(
                SortedSets.mutable.with(
                        "apply(Object):Object",
                        "compose(Function):Function",
                        "andThen(Function):Function"),
                intersection2);
    }

    @Test
    public void compare()
    {
        ClassComparer comparer = new ClassComparer();
        Triplet<MutableSortedSet<String>> compare = comparer.compare(java.util.function.Function.class, Function.class);
        assertEquals(comparer.intersect(java.util.function.Function.class, Function.class), compare.getOne());
        assertEquals(comparer.difference(java.util.function.Function.class, Function.class), compare.getTwo());
        assertEquals(comparer.difference(Function.class, java.util.function.Function.class), compare.getThree());
    }

    @Test
    public void compareAndPrint()
    {
        CollectingAppendable expectedOut = new CollectingAppendable();
        CollectingAppendable actualOut = new CollectingAppendable();
        ClassComparer expected = new ClassComparer(expectedOut, true, true, false);
        ClassComparer actual = new ClassComparer(actualOut, true, true, false);
        Triplet<MutableSortedSet<String>> compare = actual.compareAndPrint(java.util.function.Function.class, Function.class);
        assertEquals(expected.printIntersection(java.util.function.Function.class, Function.class), compare.getOne());
        assertEquals(expected.printDifference(java.util.function.Function.class, Function.class), compare.getTwo());
        assertEquals(expected.printDifference(Function.class, java.util.function.Function.class), compare.getThree());
        assertEquals(expectedOut.getContents(), actualOut.getContents());
    }

    @Test
    public void isProperSupersetOf()
    {
        assertTrue(ClassComparer.isProperSupersetOf(Stack.class, Vector.class));
        assertFalse(ClassComparer.isProperSupersetOf(Vector.class, Stack.class));
    }

    @Test
    public void isProperSubsetOf()
    {
        assertTrue(ClassComparer.isProperSubsetOf(Vector.class, Stack.class));
        assertFalse(ClassComparer.isProperSubsetOf(Stack.class, Vector.class));
    }

    private static class CollectingAppendable implements Appendable
    {
        private final MutableList<CharSequence> contents = Lists.mutable.empty();

        @Override
        public Appendable append(CharSequence csq)
        {
            this.contents.add(csq);
            return this;
        }

        @Override
        public Appendable append(CharSequence csq, int start, int end)
        {
            this.contents.add(csq);
            return this;
        }

        @Override
        public Appendable append(char c)
        {
            this.contents.add(String.valueOf(c));
            return this;
        }

        public MutableList<CharSequence> getContents()
        {
            return this.contents;
        }
    }
}
