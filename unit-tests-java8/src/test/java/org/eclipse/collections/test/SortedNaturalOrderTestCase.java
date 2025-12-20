/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.api.collection.primitive.MutableBooleanCollection;
import org.eclipse.collections.api.collection.primitive.MutableByteCollection;
import org.eclipse.collections.api.collection.primitive.MutableCharCollection;
import org.eclipse.collections.api.collection.primitive.MutableDoubleCollection;
import org.eclipse.collections.api.collection.primitive.MutableFloatCollection;
import org.eclipse.collections.api.collection.primitive.MutableIntCollection;
import org.eclipse.collections.api.collection.primitive.MutableLongCollection;
import org.eclipse.collections.api.collection.primitive.MutableShortCollection;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.primitive.BooleanLists;
import org.eclipse.collections.api.factory.primitive.ByteLists;
import org.eclipse.collections.api.factory.primitive.CharLists;
import org.eclipse.collections.api.factory.primitive.DoubleLists;
import org.eclipse.collections.api.factory.primitive.FloatLists;
import org.eclipse.collections.api.factory.primitive.IntLists;
import org.eclipse.collections.api.factory.primitive.LongLists;
import org.eclipse.collections.api.factory.primitive.ShortLists;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.ordered.SortedIterable;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.block.factory.Predicates;
import org.eclipse.collections.impl.block.factory.Predicates2;
import org.eclipse.collections.impl.list.Interval;
import org.eclipse.collections.impl.tuple.Tuples;
import org.eclipse.collections.impl.tuple.primitive.PrimitiveTuples;
import org.junit.jupiter.api.Test;

import static org.eclipse.collections.test.IterableTestCase.assertIterablesEqual;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("UnnecessaryCodeBlock")
public interface SortedNaturalOrderTestCase extends OrderedIterableTestCase
{
    @Override
    @Test
    default void Iterable_toString()
    {
        RichIterable<Integer> iterable = this.newWith(4, 4, 4, 4, 3, 3, 3, 2, 2, 1);
        assertEquals(
                "[1, 2, 2, 3, 3, 3, 4, 4, 4, 4]",
                iterable.toString());
        assertEquals(
                "[1, 2, 2, 3, 3, 3, 4, 4, 4, 4]",
                iterable.asLazy().toString());
    }

    @Override
    @Test
    default void RichIterable_collect()
    {
        RichIterable<Integer> iterable = this.newWith(1, 1, 2, 2, 3, 3, 11, 11, 12, 12, 13, 13);

        assertIterablesEqual(
                this.getExpectedTransformed(1, 1, 2, 2, 3, 3, 1, 1, 2, 2, 3, 3),
                iterable.collect(i -> i % 10));

        {
            MutableCollection<Integer> target = this.newMutableForTransform();
            MutableCollection<Integer> result = iterable.collect(i -> i % 10, target);
            assertIterablesEqual(this.newMutableForTransform(1, 1, 2, 2, 3, 3, 1, 1, 2, 2, 3, 3), result);
            assertSame(target, result);
        }

        assertIterablesEqual(
                this.getExpectedTransformed(1, 1, 2, 2, 3, 3, 1, 1, 2, 2, 3, 3),
                iterable.collectWith((i, mod) -> i % mod, 10));

        MutableCollection<Integer> target = this.newMutableForTransform();
        MutableCollection<Integer> result = iterable.collectWith((i, mod) -> i % mod, 10, target);
        assertIterablesEqual(this.newMutableForTransform(1, 1, 2, 2, 3, 3, 1, 1, 2, 2, 3, 3), result);
        assertSame(target, result);
    }

    @Override
    @Test
    default void RichIterable_collectIf()
    {
        RichIterable<Integer> iterable = this.newWith(1, 1, 2, 2, 3, 3, 11, 11, 12, 12, 13, 13);

        assertIterablesEqual(
                this.getExpectedTransformed(1, 1, 3, 3, 1, 1, 3, 3),
                iterable.collectIf(i -> i % 2 != 0, i -> i % 10));

        MutableCollection<Integer> target = this.newMutableForTransform();
        MutableCollection<Integer> result = iterable.collectIf(i -> i % 2 != 0, i -> i % 10, target);
        assertIterablesEqual(this.newMutableForTransform(1, 1, 3, 3, 1, 1, 3, 3), result);
        assertSame(target, result);
    }

    @Override
    @Test
    default void RichIterable_collectPrimitive()
    {
        assertIterablesEqual(
                this.getExpectedBoolean(false, false, true, true, false, false),
                this.newWith(1, 1, 2, 2, 3, 3).collectBoolean(each -> each % 2 == 0));

        {
            MutableBooleanCollection target = this.newBooleanForTransform();
            MutableBooleanCollection result = this.newWith(1, 1, 2, 2, 3, 3).collectBoolean(each -> each % 2 == 0, target);
            assertIterablesEqual(this.newBooleanForTransform(false, false, true, true, false, false), result);
            assertSame(target, result);
        }

        RichIterable<Integer> iterable = this.newWith(1, 1, 2, 2, 3, 3, 11, 11, 12, 12, 13, 13);

        assertIterablesEqual(
                this.getExpectedByte((byte) 1, (byte) 1, (byte) 2, (byte) 2, (byte) 3, (byte) 3, (byte) 1, (byte) 1, (byte) 2, (byte) 2, (byte) 3, (byte) 3),
                iterable.collectByte(each -> (byte) (each % 10)));

        {
            MutableByteCollection target = this.newByteForTransform();
            MutableByteCollection result = iterable.collectByte(each -> (byte) (each % 10), target);
            assertIterablesEqual(this.newByteForTransform((byte) 1, (byte) 1, (byte) 2, (byte) 2, (byte) 3, (byte) 3, (byte) 1, (byte) 1, (byte) 2, (byte) 2, (byte) 3, (byte) 3), result);
            assertSame(target, result);
        }

        assertIterablesEqual(
                this.getExpectedChar((char) 1, (char) 1, (char) 2, (char) 2, (char) 3, (char) 3, (char) 1, (char) 1, (char) 2, (char) 2, (char) 3, (char) 3),
                iterable.collectChar(each -> (char) (each % 10)));

        {
            MutableCharCollection target = this.newCharForTransform();
            MutableCharCollection result = iterable.collectChar(each -> (char) (each % 10), target);
            assertIterablesEqual(this.newCharForTransform((char) 1, (char) 1, (char) 2, (char) 2, (char) 3, (char) 3, (char) 1, (char) 1, (char) 2, (char) 2, (char) 3, (char) 3), result);
            assertSame(target, result);
        }

        assertIterablesEqual(
                this.getExpectedDouble(1.0, 1.0, 2.0, 2.0, 3.0, 3.0, 1.0, 1.0, 2.0, 2.0, 3.0, 3.0),
                iterable.collectDouble(each -> (double) (each % 10)));

        {
            MutableDoubleCollection target = this.newDoubleForTransform();
            MutableDoubleCollection result = iterable.collectDouble(each -> (double) (each % 10), target);
            assertIterablesEqual(this.newDoubleForTransform(1.0, 1.0, 2.0, 2.0, 3.0, 3.0, 1.0, 1.0, 2.0, 2.0, 3.0, 3.0), result);
            assertSame(target, result);
        }

        assertIterablesEqual(
                this.getExpectedFloat(1.0f, 1.0f, 2.0f, 2.0f, 3.0f, 3.0f, 1.0f, 1.0f, 2.0f, 2.0f, 3.0f, 3.0f),
                iterable.collectFloat(each -> (float) (each % 10)));

        {
            MutableFloatCollection target = this.newFloatForTransform();
            MutableFloatCollection result = iterable.collectFloat(each -> (float) (each % 10), target);
            assertIterablesEqual(this.newFloatForTransform(1.0f, 1.0f, 2.0f, 2.0f, 3.0f, 3.0f, 1.0f, 1.0f, 2.0f, 2.0f, 3.0f, 3.0f), result);
            assertSame(target, result);
        }

        assertIterablesEqual(
                this.getExpectedInt(1, 1, 2, 2, 3, 3, 1, 1, 2, 2, 3, 3),
                iterable.collectInt(each -> each % 10));

        {
            MutableIntCollection target = this.newIntForTransform();
            MutableIntCollection result = iterable.collectInt(each -> each % 10, target);
            assertIterablesEqual(this.newIntForTransform(1, 1, 2, 2, 3, 3, 1, 1, 2, 2, 3, 3), result);
            assertSame(target, result);
        }

        assertIterablesEqual(
                this.getExpectedLong(1, 1, 2, 2, 3, 3, 1, 1, 2, 2, 3, 3),
                iterable.collectLong(each -> each % 10));

        {
            MutableLongCollection target = this.newLongForTransform();
            MutableLongCollection result = iterable.collectLong(each -> each % 10, target);
            assertIterablesEqual(this.newLongForTransform(1, 1, 2, 2, 3, 3, 1, 1, 2, 2, 3, 3), result);
            assertSame(target, result);
        }

        assertIterablesEqual(
                this.getExpectedShort((short) 1, (short) 1, (short) 2, (short) 2, (short) 3, (short) 3, (short) 1, (short) 1, (short) 2, (short) 2, (short) 3, (short) 3),
                iterable.collectShort(each -> (short) (each % 10)));

        MutableShortCollection target = this.newShortForTransform();
        MutableShortCollection result = iterable.collectShort(each -> (short) (each % 10), target);
        assertIterablesEqual(this.newShortForTransform((short) 1, (short) 1, (short) 2, (short) 2, (short) 3, (short) 3, (short) 1, (short) 1, (short) 2, (short) 2, (short) 3, (short) 3), result);
        assertSame(target, result);
    }

    @Override
    @Test
    default void RichIterable_flatCollect()
    {
        assertIterablesEqual(
                this.getExpectedTransformed(1, 1, 2, 1, 2, 1, 2, 3),
                this.newWith(1, 2, 2, 3).flatCollect(Interval::oneTo));

        assertIterablesEqual(
                this.newMutableForTransform(1, 1, 2, 1, 2, 1, 2, 3),
                this.newWith(1, 2, 2, 3).flatCollect(Interval::oneTo, this.newMutableForTransform()));

        assertIterablesEqual(
                this.getExpectedTransformed(1, 2, 3, 4, 5, 2, 3, 4, 5, 2, 3, 4, 5, 3, 4, 5),
                this.newWith(1, 2, 2, 3).flatCollectWith(Interval::fromTo, 5));

        assertIterablesEqual(
                this.newMutableForTransform(1, 2, 1, 2, 1, 3, 2, 1),
                this.newWith(1, 2, 2, 3).flatCollectWith(Interval::fromTo, 1, this.newMutableForTransform()));
    }

    @Override
    @Test
    default void RichIterable_flatCollect_primitive()
    {
        {
            MutableBooleanCollection target = this.newBooleanForTransform();
            MutableBooleanCollection result = this.newWith(1, 1, 2, 2, 3, 3).flatCollectBoolean(
                    each -> BooleanLists.immutable.with(each % 2 == 0, each % 2 == 0),
                    target);
            assertIterablesEqual(
                    this.newBooleanForTransform(false, false, false, false, true, true, true, true, false, false, false, false),
                    result);
            assertSame(target, result);
        }

        RichIterable<Integer> iterable = this.newWith(1, 1, 2, 2, 3, 3, 11, 11, 12, 12, 13, 13);

        {
            MutableByteCollection target = this.newByteForTransform();
            MutableByteCollection result = iterable.flatCollectByte(
                    each -> ByteLists.immutable.with((byte) (each % 10), (byte) (each % 10)),
                    target);
            assertIterablesEqual(
                    this.newByteForTransform((byte) 1, (byte) 1, (byte) 1, (byte) 1, (byte) 2, (byte) 2, (byte) 2, (byte) 2, (byte) 3, (byte) 3, (byte) 3, (byte) 3, (byte) 1, (byte) 1, (byte) 1, (byte) 1, (byte) 2, (byte) 2, (byte) 2, (byte) 2, (byte) 3, (byte) 3, (byte) 3, (byte) 3),
                    result);
            assertSame(target, result);
        }

        {
            MutableCharCollection target = this.newCharForTransform();
            MutableCharCollection result = iterable.flatCollectChar(
                    each -> CharLists.immutable.with((char) (each % 10), (char) (each % 10)),
                    target);
            assertIterablesEqual(
                    this.newCharForTransform((char) 1, (char) 1, (char) 1, (char) 1, (char) 2, (char) 2, (char) 2, (char) 2, (char) 3, (char) 3, (char) 3, (char) 3, (char) 1, (char) 1, (char) 1, (char) 1, (char) 2, (char) 2, (char) 2, (char) 2, (char) 3, (char) 3, (char) 3, (char) 3),
                    result);
            assertSame(target, result);
        }

        {
            MutableDoubleCollection target = this.newDoubleForTransform();
            MutableDoubleCollection result = iterable.flatCollectDouble(each -> DoubleLists.immutable.with(
                    (double) (each % 10),
                    (double) (each % 10)), target);
            assertIterablesEqual(
                    this.newDoubleForTransform(1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0, 2.0, 3.0, 3.0, 3.0, 3.0, 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0, 2.0, 3.0, 3.0, 3.0, 3.0),
                    result);
            assertSame(target, result);
        }

        {
            MutableFloatCollection target = this.newFloatForTransform();
            MutableFloatCollection result = iterable.flatCollectFloat(each -> FloatLists.immutable.with(
                    (float) (each % 10),
                    (float) (each % 10)), target);
            assertIterablesEqual(
                    this.newFloatForTransform(
                            1.0f, 1.0f, 1.0f, 1.0f, 2.0f, 2.0f, 2.0f, 2.0f, 3.0f, 3.0f, 3.0f, 3.0f, 1.0f, 1.0f, 1.0f, 1.0f, 2.0f, 2.0f, 2.0f, 2.0f, 3.0f, 3.0f, 3.0f, 3.0f),
                    result);
            assertSame(target, result);
        }

        {
            MutableIntCollection target = this.newIntForTransform();
            MutableIntCollection result =
                    iterable.flatCollectInt(each -> IntLists.immutable.with(each % 10, each % 10), target);
            assertIterablesEqual(
                    this.newIntForTransform(1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3),
                    result);
            assertSame(target, result);
        }

        {
            MutableLongCollection target = this.newLongForTransform();
            MutableLongCollection result =
                    iterable.flatCollectLong(each -> LongLists.immutable.with(each % 10, each % 10), target);
            assertIterablesEqual(
                    this.newLongForTransform(1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3),
                    result);
            assertSame(target, result);
        }

        {
            MutableShortCollection target = this.newShortForTransform();
            MutableShortCollection result = iterable.flatCollectShort(each -> ShortLists.immutable.with(
                    (short) (each % 10),
                    (short) (each % 10)), target);
            assertIterablesEqual(
                    this.newShortForTransform((short) 1, (short) 1, (short) 1, (short) 1, (short) 2, (short) 2, (short) 2, (short) 2, (short) 3, (short) 3, (short) 3, (short) 3, (short) 1, (short) 1, (short) 1, (short) 1, (short) 2, (short) 2, (short) 2, (short) 2, (short) 3, (short) 3, (short) 3, (short) 3),
                    result);
            assertSame(target, result);
        }
    }

    @Override
    @Test
    default void RichIterable_detect()
    {
        RichIterable<Integer> iterable = this.newWith(1, 2, 3);

        assertThat(iterable.detect(Predicates.greaterThan(0)), is(1));
        assertThat(iterable.detect(Predicates.greaterThan(1)), is(2));
        assertThat(iterable.detect(Predicates.greaterThan(2)), is(3));
        assertThat(iterable.detect(Predicates.greaterThan(3)), nullValue());

        assertThat(iterable.detect(Predicates.lessThan(1)), nullValue());
        assertThat(iterable.detect(Predicates.lessThan(2)), is(1));
        assertThat(iterable.detect(Predicates.lessThan(3)), is(1));
        assertThat(iterable.detect(Predicates.lessThan(4)), is(1));

        assertThat(iterable.detectWith(Predicates2.greaterThan(), 0), is(1));
        assertThat(iterable.detectWith(Predicates2.greaterThan(), 1), is(2));
        assertThat(iterable.detectWith(Predicates2.greaterThan(), 2), is(3));
        assertThat(iterable.detectWith(Predicates2.greaterThan(), 3), nullValue());

        assertThat(iterable.detectWith(Predicates2.lessThan(), 1), nullValue());
        assertThat(iterable.detectWith(Predicates2.lessThan(), 2), is(1));
        assertThat(iterable.detectWith(Predicates2.lessThan(), 3), is(1));
        assertThat(iterable.detectWith(Predicates2.lessThan(), 4), is(1));

        assertThat(iterable.detectIfNone(Predicates.greaterThan(0), () -> 4), is(1));
        assertThat(iterable.detectIfNone(Predicates.greaterThan(1), () -> 4), is(2));
        assertThat(iterable.detectIfNone(Predicates.greaterThan(2), () -> 4), is(3));
        assertThat(iterable.detectIfNone(Predicates.greaterThan(3), () -> 4), is(4));

        assertThat(iterable.detectIfNone(Predicates.lessThan(1), () -> 4), is(4));
        assertThat(iterable.detectIfNone(Predicates.lessThan(2), () -> 4), is(1));
        assertThat(iterable.detectIfNone(Predicates.lessThan(3), () -> 4), is(1));
        assertThat(iterable.detectIfNone(Predicates.lessThan(4), () -> 4), is(1));

        assertThat(iterable.detectWithIfNone(Predicates2.greaterThan(), 0, () -> 4), is(1));
        assertThat(iterable.detectWithIfNone(Predicates2.greaterThan(), 1, () -> 4), is(2));
        assertThat(iterable.detectWithIfNone(Predicates2.greaterThan(), 2, () -> 4), is(3));
        assertThat(iterable.detectWithIfNone(Predicates2.greaterThan(), 3, () -> 4), is(4));

        assertThat(iterable.detectWithIfNone(Predicates2.lessThan(), 1, () -> 4), is(4));
        assertThat(iterable.detectWithIfNone(Predicates2.lessThan(), 2, () -> 4), is(1));
        assertThat(iterable.detectWithIfNone(Predicates2.lessThan(), 3, () -> 4), is(1));
        assertThat(iterable.detectWithIfNone(Predicates2.lessThan(), 4, () -> 4), is(1));

        assertThat(iterable.detectOptional(Predicates.greaterThan(0)), is(Optional.of(1)));
        assertThat(iterable.detectOptional(Predicates.greaterThan(1)), is(Optional.of(2)));
        assertThat(iterable.detectOptional(Predicates.greaterThan(2)), is(Optional.of(3)));
        assertThat(iterable.detectOptional(Predicates.greaterThan(3)), is(Optional.empty()));

        assertThat(iterable.detectOptional(Predicates.lessThan(1)), is(Optional.empty()));
        assertThat(iterable.detectOptional(Predicates.lessThan(2)), is(Optional.of(1)));
        assertThat(iterable.detectOptional(Predicates.lessThan(3)), is(Optional.of(1)));
        assertThat(iterable.detectOptional(Predicates.lessThan(4)), is(Optional.of(1)));

        assertThat(iterable.detectWithOptional(Predicates2.greaterThan(), 0), is(Optional.of(1)));
        assertThat(iterable.detectWithOptional(Predicates2.greaterThan(), 1), is(Optional.of(2)));
        assertThat(iterable.detectWithOptional(Predicates2.greaterThan(), 2), is(Optional.of(3)));
        assertThat(iterable.detectWithOptional(Predicates2.greaterThan(), 3), is(Optional.empty()));

        assertThat(iterable.detectWithOptional(Predicates2.lessThan(), 1), is(Optional.empty()));
        assertThat(iterable.detectWithOptional(Predicates2.lessThan(), 2), is(Optional.of(1)));
        assertThat(iterable.detectWithOptional(Predicates2.lessThan(), 3), is(Optional.of(1)));
        assertThat(iterable.detectWithOptional(Predicates2.lessThan(), 4), is(Optional.of(1)));
    }

    @Override
    @Test
    default void RichIterable_minBy_maxBy()
    {
        assertEquals("ca", this.newWith("ab", "bc", "ca", "da", "ed").minBy(string -> string.charAt(string.length() - 1)));
        assertThrows(NoSuchElementException.class, () -> this.<String>newWith().minBy(string -> string.charAt(string.length() - 1)));

        assertEquals("cz", this.newWith("ew", "dz", "cz", "bx", "ay").maxBy(string -> string.charAt(string.length() - 1)));
        assertThrows(NoSuchElementException.class, () -> this.<String>newWith().maxBy(string -> string.charAt(string.length() - 1)));
    }

    @Override
    @Test
    default void RichIterable_makeString_appendString()
    {
        RichIterable<Integer> iterable = this.newWith(1, 2, 2, 3, 3, 3, 4, 4, 4, 4);
        assertEquals(
                "1, 2, 2, 3, 3, 3, 4, 4, 4, 4",
                iterable.makeString());

        assertEquals(
                "1/2/2/3/3/3/4/4/4/4",
                iterable.makeString("/"));

        assertEquals(
                "[1/2/2/3/3/3/4/4/4/4]",
                iterable.makeString("[", "/", "]"));

        StringBuilder builder1 = new StringBuilder();
        iterable.appendString(builder1);
        assertEquals(
                "1, 2, 2, 3, 3, 3, 4, 4, 4, 4",
                builder1.toString());

        StringBuilder builder2 = new StringBuilder();
        iterable.appendString(builder2, "/");
        assertEquals(
                "1/2/2/3/3/3/4/4/4/4",
                builder2.toString());

        StringBuilder builder3 = new StringBuilder();
        iterable.appendString(builder3, "[", "/", "]");
        assertEquals(
                "[1/2/2/3/3/3/4/4/4/4]",
                builder3.toString());
    }

    @Override
    @Test
    default void RichIterable_toList()
    {
        RichIterable<Integer> iterable = this.newWith(1, 2, 2, 3, 3, 3, 4, 4, 4, 4);
        assertIterablesEqual(
                Lists.immutable.with(1, 2, 2, 3, 3, 3, 4, 4, 4, 4),
                iterable.toList());

        MutableList<Integer> target = Lists.mutable.empty();
        iterable.each(target::add);
        assertIterablesEqual(
                target,
                iterable.toList());
    }

    @Override
    @Test
    default void RichIterable_into()
    {
        assertIterablesEqual(
                Lists.immutable.with(1, 2, 2, 3, 3, 3, 4, 4, 4, 4),
                this.newWith(1, 2, 2, 3, 3, 3, 4, 4, 4, 4).into(Lists.mutable.empty()));
    }

    @Override
    @Test
    default void OrderedIterable_getFirst()
    {
        assertEquals(Integer.valueOf(1), this.newWith(1, 2, 2, 3, 3, 3).getFirst());
    }

    @Override
    @Test
    default void OrderedIterable_getLast()
    {
        assertEquals(Integer.valueOf(3), this.newWith(1, 2, 2, 3, 3, 3).getLast());
    }

    @Override
    @Test
    default void OrderedIterable_next()
    {
        Iterator<Integer> iterator = this.newWith(1, 2, 3).iterator();
        assertEquals(Integer.valueOf(1), iterator.next());
        assertEquals(Integer.valueOf(2), iterator.next());
        assertEquals(Integer.valueOf(3), iterator.next());
    }

    /**
     * @since 9.1.
     */
    @Override
    @Test
    default void OrderedIterable_collectWithIndex()
    {
        OrderedIterable<Integer> iterable = (OrderedIterable<Integer>) this.newWith(1, 2, 2, 3, 3, 3, 4, 4, 4, 4);
        assertEquals(
                Lists.immutable.with(
                        PrimitiveTuples.pair(Integer.valueOf(1), 0),
                        PrimitiveTuples.pair(Integer.valueOf(2), 1),
                        PrimitiveTuples.pair(Integer.valueOf(2), 2),
                        PrimitiveTuples.pair(Integer.valueOf(3), 3),
                        PrimitiveTuples.pair(Integer.valueOf(3), 4),
                        PrimitiveTuples.pair(Integer.valueOf(3), 5),
                        PrimitiveTuples.pair(Integer.valueOf(4), 6),
                        PrimitiveTuples.pair(Integer.valueOf(4), 7),
                        PrimitiveTuples.pair(Integer.valueOf(4), 8),
                        PrimitiveTuples.pair(Integer.valueOf(4), 9)),
                iterable.collectWithIndex(PrimitiveTuples::pair).toList());
    }

    /**
     * @since 9.1.
     */
    @Override
    @Test
    default void OrderedIterable_collectWithIndexWithTarget()
    {
        OrderedIterable<Integer> iterable = (OrderedIterable<Integer>) this.newWith(1, 2, 2, 3, 3, 3, 4, 4, 4, 4);
        assertEquals(
                Lists.immutable.with(
                        PrimitiveTuples.pair(Integer.valueOf(1), 0),
                        PrimitiveTuples.pair(Integer.valueOf(2), 1),
                        PrimitiveTuples.pair(Integer.valueOf(2), 2),
                        PrimitiveTuples.pair(Integer.valueOf(3), 3),
                        PrimitiveTuples.pair(Integer.valueOf(3), 4),
                        PrimitiveTuples.pair(Integer.valueOf(3), 5),
                        PrimitiveTuples.pair(Integer.valueOf(4), 6),
                        PrimitiveTuples.pair(Integer.valueOf(4), 7),
                        PrimitiveTuples.pair(Integer.valueOf(4), 8),
                        PrimitiveTuples.pair(Integer.valueOf(4), 9)),
                iterable.collectWithIndex(PrimitiveTuples::pair, Lists.mutable.empty()));
    }

    @Override
    @Test
    default void OrderedIterable_zipWithIndex()
    {
        RichIterable<Integer> iterable = this.newWith(1, 2, 2, 3, 3, 3, 4, 4, 4, 4);
        assertEquals(
                Lists.immutable.with(
                        Tuples.pair(1, 0),
                        Tuples.pair(2, 1),
                        Tuples.pair(2, 2),
                        Tuples.pair(3, 3),
                        Tuples.pair(3, 4),
                        Tuples.pair(3, 5),
                        Tuples.pair(4, 6),
                        Tuples.pair(4, 7),
                        Tuples.pair(4, 8),
                        Tuples.pair(4, 9)),
                iterable.zipWithIndex().toList());
    }

    @Override
    @Test
    default void OrderedIterable_zipWithIndex_target()
    {
        RichIterable<Integer> iterable = this.newWith(1, 2, 2, 3, 3, 3, 4, 4, 4, 4);
        MutableList<Pair<Integer, Integer>> target = Lists.mutable.empty();
        MutableList<Pair<Integer, Integer>> result = iterable.zipWithIndex(target);
        assertEquals(
                Lists.immutable.with(
                        Tuples.pair(1, 0),
                        Tuples.pair(2, 1),
                        Tuples.pair(2, 2),
                        Tuples.pair(3, 3),
                        Tuples.pair(3, 4),
                        Tuples.pair(3, 5),
                        Tuples.pair(4, 6),
                        Tuples.pair(4, 7),
                        Tuples.pair(4, 8),
                        Tuples.pair(4, 9)),
                result);
        assertSame(target, result);
    }

    @Test
    default void SortedIterable_comparator()
    {
        SortedIterable<?> iterable = (SortedIterable<?>) this.newWith();
        assertNull(iterable.comparator());
    }

    @Override
    @Test
    default void OrderedIterable_injectIntoWithIndex()
    {
        OrderedIterable<Integer> iterable = (OrderedIterable<Integer>)this.newWith(1, 2, 2, 3, 3, 3, 4, 4, 4, 4);
        MutableList<Pair<Integer, Integer>> injected = Lists.mutable.empty();
        MutableList<Pair<Integer, Integer>> result = iterable.injectIntoWithIndex(injected, (coll, each, index) -> {
            injected.add(Tuples.pair(each, index));
            return injected;
        });
        assertEquals(
                Lists.immutable.with(
                        Tuples.pair(1, 0),
                        Tuples.pair(2, 1),
                        Tuples.pair(2, 2),
                        Tuples.pair(3, 3),
                        Tuples.pair(3, 4),
                        Tuples.pair(3, 5),
                        Tuples.pair(4, 6),
                        Tuples.pair(4, 7),
                        Tuples.pair(4, 8),
                        Tuples.pair(4, 9)),
                result);
        assertSame(injected, result);
    }
}
