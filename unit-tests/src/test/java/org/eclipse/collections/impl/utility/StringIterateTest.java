/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.utility;

import java.util.Locale;

import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.procedure.primitive.CharProcedure;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.CharList;
import org.eclipse.collections.api.list.primitive.ImmutableCharList;
import org.eclipse.collections.api.list.primitive.IntList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.set.primitive.ImmutableCharSet;
import org.eclipse.collections.api.set.primitive.ImmutableIntSet;
import org.eclipse.collections.api.tuple.Twin;
import org.eclipse.collections.impl.block.factory.Functions;
import org.eclipse.collections.impl.block.factory.Procedures;
import org.eclipse.collections.impl.block.factory.primitive.CharPredicates;
import org.eclipse.collections.impl.block.factory.primitive.CharToCharFunctions;
import org.eclipse.collections.impl.block.function.AddFunction;
import org.eclipse.collections.impl.block.function.primitive.CodePointFunction;
import org.eclipse.collections.impl.block.predicate.CodePointPredicate;
import org.eclipse.collections.impl.block.procedure.primitive.CodePointProcedure;
import org.eclipse.collections.impl.factory.primitive.CharSets;
import org.eclipse.collections.impl.factory.primitive.IntSets;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.list.mutable.primitive.CharArrayList;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.eclipse.collections.impl.string.immutable.CharAdapter;
import org.eclipse.collections.impl.string.immutable.CodePointAdapter;
import org.eclipse.collections.impl.string.immutable.CodePointList;
import org.eclipse.collections.impl.test.Verify;
import org.eclipse.collections.impl.tuple.Tuples;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * JUnit test for {@link StringIterate}.
 */
public class StringIterateTest
{
    public static final String THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG = "The quick brown fox jumps over the lazy dog.";
    public static final String ALPHABET_LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    public static final Twin<String> HALF_ABET = StringIterate.splitAtIndex(ALPHABET_LOWERCASE, 13);
    public static final String TQBFJOTLD_MINUS_HALF_ABET_1 = "t qu rown ox ups ovr t zy o.";
    public static final String TQBFJOTLD_MINUS_HALF_ABET_2 = "he ick b f jm e he la dg.";

    @Test
    public void asCharAdapter()
    {
        CharAdapter answer =
                StringIterate.asCharAdapter("HelloHellow")
                        .collectChar(Character::toUpperCase)
                        .select(c -> c != 'W')
                        .distinct()
                        .toReversed()
                        .reject(CharAdapter.adapt("LE")::contains)
                        .newWith('!');

        assertEquals("OH!", answer.toString());
        assertEquals("OH!", answer.toStringBuilder().toString());
        assertEquals("OH!", answer.makeString(""));

        CharList charList = StringIterate.asCharAdapter("HelloHellow")
                .asLazy()
                .collectChar(Character::toUpperCase)
                .select(c -> c != 'W')
                .toList()
                .distinct()
                .toReversed()
                .reject(CharAdapter.adapt("LE")::contains)
                .with('!');

        assertEquals("OH!", CharAdapter.from(charList).toString());
        assertEquals("OH!", CharAdapter.from(CharAdapter.from(charList)).toString());

        String helloUppercase2 = StringIterate.asCharAdapter("Hello")
                .asLazy()
                .collectChar(Character::toUpperCase)
                .makeString("");
        assertEquals("HELLO", helloUppercase2);

        CharArrayList arraylist = new CharArrayList();
        StringIterate.asCharAdapter("Hello".toUpperCase(Locale.ROOT))
                .chars()
                .sorted()
                .forEach(e -> arraylist.add((char) e));
        assertEquals(StringIterate.asCharAdapter("EHLLO"), arraylist);

        ImmutableCharList arrayList2 =
                StringIterate.asCharAdapter("Hello".toUpperCase(Locale.ROOT))
                        .toSortedList()
                        .toImmutable();

        assertEquals(StringIterate.asCharAdapter("EHLLO"), arrayList2);

        assertEquals(StringIterate.asCharAdapter("HELLO"), CharAdapter.adapt("hello").collectChar(Character::toUpperCase));
    }

    @Test
    public void asCharAdapterExtra()
    {
        assertEquals(
                9,
                StringIterate.asCharAdapter(THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG)
                        .count(c -> !Character.isLetter(c)));

        assertTrue(
                StringIterate.asCharAdapter(THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG).anySatisfy(Character::isWhitespace));

        assertEquals(
                8,
                StringIterate.asCharAdapter(THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG)
                        .count(Character::isWhitespace));

        Verify.assertSize(
                26,
                StringIterate.asCharAdapter(THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG)
                        .asLazy()
                        .select(Character::isLetter)
                        .collectChar(Character::toLowerCase).toSet());

        ImmutableCharSet alphaCharAdapter =
                StringIterate.asCharAdapter(ALPHABET_LOWERCASE).toSet().toImmutable();
        assertTrue(
                StringIterate.asCharAdapter(THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG).containsAll(alphaCharAdapter));
        assertEquals(
                CharSets.immutable.empty(),
                alphaCharAdapter.newWithoutAll(StringIterate.asCharAdapter(THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG.toLowerCase(Locale.ROOT))));
        assertEquals(
                TQBFJOTLD_MINUS_HALF_ABET_1,
                StringIterate.asCharAdapter(THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG.toLowerCase(Locale.ROOT))
                        .newWithoutAll(StringIterate.asCharAdapter(HALF_ABET.getOne()))
                        .toString());
        assertEquals(
                TQBFJOTLD_MINUS_HALF_ABET_2,
                StringIterate.asCharAdapter(THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG.toLowerCase(Locale.ROOT))
                        .newWithoutAll(StringIterate.asCharAdapter(HALF_ABET.getTwo()))
                        .toString());
    }

    @Test
    public void buildTheAlphabetFromEmpty()
    {
        String alphabet = StringIterate.asCharAdapter("")
                .newWith('a')
                .newWithAll(StringIterate.asCharAdapter(HALF_ABET.getOne()))
                .newWithAll(StringIterate.asCharAdapter(HALF_ABET.getTwo()))
                .newWithout('a').toString();
        assertEquals(ALPHABET_LOWERCASE, alphabet);
    }

    @Test
    public void asCodePointAdapter()
    {
        CodePointAdapter answer =
                StringIterate.asCodePointAdapter("HelloHellow")
                        .collectInt(Character::toUpperCase)
                        .select(i -> i != 'W')
                        .distinct()
                        .toReversed()
                        .reject(CodePointAdapter.adapt("LE")::contains)
                        .newWith('!');

        assertEquals("OH!", answer.toString());
        assertEquals("OH!", answer.toStringBuilder().toString());
        assertEquals("OH!", answer.makeString(""));

        IntList intList = StringIterate.asCodePointAdapter("HelloHellow")
                .asLazy()
                .collectInt(Character::toUpperCase)
                .select(i -> i != 'W')
                .toList()
                .distinct()
                .toReversed()
                .reject(CodePointAdapter.adapt("LE")::contains)
                .with('!');

        assertEquals("OH!", CodePointAdapter.from(intList).toString());
        assertEquals("OH!", CodePointAdapter.from(CodePointAdapter.from(intList)).toString());
    }

    @Test
    public void asCodePointAdapterExtra()
    {
        assertEquals(
                9,
                StringIterate.asCodePointAdapter(THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG)
                        .count(i -> !Character.isLetter(i)));

        assertTrue(
                StringIterate.asCodePointAdapter(THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG).anySatisfy(Character::isWhitespace));

        assertEquals(
                8,
                StringIterate.asCodePointAdapter(THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG)
                        .count(Character::isWhitespace));

        Verify.assertSize(
                26,
                StringIterate.asCodePointAdapter(THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG)
                        .asLazy()
                        .select(Character::isLetter)
                        .collectInt(Character::toLowerCase).toSet());

        ImmutableIntSet alphaints =
                StringIterate.asCodePointAdapter(ALPHABET_LOWERCASE).toSet().toImmutable();
        assertTrue(
                StringIterate.asCodePointAdapter(THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG).containsAll(alphaints));
        assertEquals(
                IntSets.immutable.empty(),
                alphaints.newWithoutAll(StringIterate.asCodePointAdapter(THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG.toLowerCase(Locale.ROOT))));
        assertEquals(
                TQBFJOTLD_MINUS_HALF_ABET_1,
                StringIterate.asCodePointAdapter(THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG.toLowerCase(Locale.ROOT))
                        .newWithoutAll(StringIterate.asCodePointAdapter(HALF_ABET.getOne()))
                        .toString());
        assertEquals(
                TQBFJOTLD_MINUS_HALF_ABET_2,
                StringIterate.asCodePointAdapter(THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG.toLowerCase(Locale.ROOT))
                        .newWithoutAll(StringIterate.asCodePointAdapter(HALF_ABET.getTwo()))
                        .toString());
    }

    @Test
    public void toCodePointList()
    {
        CodePointList answer =
                StringIterate.toCodePointList("Hello")
                        .collectInt(Character::toUpperCase)
                        .select(i -> i != 'W')
                        .distinct()
                        .toReversed()
                        .reject(CodePointList.from("LE")::contains)
                        .newWith('!');

        assertEquals("OH!", answer.toString());
        assertEquals("OH!", answer.toStringBuilder().toString());
        assertEquals("OH!", answer.makeString(""));

        IntList intList = StringIterate.toCodePointList("HelloHellow")
                .asLazy()
                .collectInt(Character::toUpperCase)
                .select(i -> i != 'W')
                .toList()
                .distinct()
                .toReversed()
                .reject(CodePointList.from("LE")::contains)
                .with('!');

        assertEquals("OH!", CodePointList.from(intList).toString());
        assertEquals("OH!", CodePointList.from(CodePointList.from(intList)).toString());
    }

    @Test
    public void toCodePointListExtra()
    {
        assertEquals(
                9,
                StringIterate.toCodePointList(THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG)
                        .count(i -> !Character.isLetter(i)));

        assertTrue(
                StringIterate.toCodePointList(THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG).anySatisfy(Character::isWhitespace));

        assertEquals(
                8,
                StringIterate.toCodePointList(THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG)
                        .count(Character::isWhitespace));

        Verify.assertSize(
                26,
                StringIterate.toCodePointList(THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG)
                        .asLazy()
                        .select(Character::isLetter)
                        .collectInt(Character::toLowerCase).toSet());

        ImmutableIntSet alphaints =
                StringIterate.toCodePointList(ALPHABET_LOWERCASE).toSet().toImmutable();
        assertTrue(
                StringIterate.toCodePointList(THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG).containsAll(alphaints));
        assertEquals(
                IntSets.immutable.empty(),
                alphaints.newWithoutAll(StringIterate.toCodePointList(THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG.toLowerCase(Locale.ROOT))));
        assertTrue(
                StringIterate.toCodePointList(THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG)
                        .containsAll(StringIterate.toCodePointList(HALF_ABET.getOne())));
        assertEquals(
                TQBFJOTLD_MINUS_HALF_ABET_1,
                StringIterate.toCodePointList(THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG.toLowerCase(Locale.ROOT))
                        .newWithoutAll(StringIterate.toCodePointList(HALF_ABET.getOne()))
                        .toString());
        assertEquals(
                TQBFJOTLD_MINUS_HALF_ABET_2,
                StringIterate.toCodePointList(THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG.toLowerCase(Locale.ROOT))
                        .newWithoutAll(StringIterate.toCodePointList(HALF_ABET.getTwo()))
                        .toString());
    }

    @Test
    public void englishToUpperLowerCase()
    {
        assertEquals("ABC", StringIterate.englishToUpperCase("abc"));
        assertEquals("abc", StringIterate.englishToLowerCase("ABC"));
    }

    @Test
    public void collect()
    {
        assertEquals("ABC", StringIterate.collect("abc", CharToCharFunctions.toUpperCase()));
        assertEquals("abc", StringIterate.collect("abc", CharToCharFunctions.toLowerCase()));
    }

    @Test
    public void collectCodePoint()
    {
        assertEquals("ABC", StringIterate.collect("abc", CodePointFunction.TO_UPPERCASE));
        assertEquals("abc", StringIterate.collect("abc", CodePointFunction.TO_LOWERCASE));
    }

    @Test
    public void collectCodePointUnicode()
    {
        assertEquals("\u3042\uD840\uDC00\u3044\uD840\uDC03\u3046\uD83D\uDE09", StringIterate.collect("\u3042\uD840\uDC00\u3044\uD840\uDC03\u3046\uD83D\uDE09", CodePointFunction.PASS_THRU));
        assertEquals("\u3042\uD840\uDC00\u3044\uD840\uDC03\u3046\uD83D\uDE09", StringIterate.collect("\u3042\uD840\uDC00\u3044\uD840\uDC03\u3046\uD83D\uDE09", CodePointFunction.PASS_THRU));
    }

    @Test
    public void englishToUpperCase()
    {
        assertEquals("ABC", StringIterate.englishToUpperCase("abc"));
        assertEquals("A,B,C", StringIterate.englishToUpperCase("a,b,c"));
        assertSame("A,B,C", StringIterate.englishToUpperCase("A,B,C"));
    }

    @Test
    public void englishToLowerCase()
    {
        assertEquals("abc", StringIterate.englishToLowerCase("ABC"));
        assertEquals("a,b,c", StringIterate.englishToLowerCase("A,B,C"));
        assertSame("a,b,c", StringIterate.englishToLowerCase("a,b,c"));
    }

    @Test
    public void englishIsUpperLowerCase()
    {
        String allValues = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890~`!@#$%^&*()_-+=[]{};<>,.?/|";
        String jdkUpper = allValues.toUpperCase(Locale.ROOT);
        String upper = StringIterate.englishToUpperCase(allValues);
        assertEquals(jdkUpper.length(), upper.length());
        assertEquals(jdkUpper, upper);
        String jdkLower = allValues.toLowerCase(Locale.ROOT);
        String lower = StringIterate.englishToLowerCase(allValues);
        assertEquals(jdkLower.length(), lower.length());
        assertEquals(jdkLower, lower);
    }

    @Test
    public void select()
    {
        String string = StringIterate.select("1a2a3", CharPredicates.isDigit());
        assertEquals("123", string);
    }

    @Test
    public void selectCodePoint()
    {
        String string = StringIterate.select("1a2a3", CodePointPredicate.IS_DIGIT);
        assertEquals("123", string);
    }

    @Test
    public void selectCodePointUnicode()
    {
        String string = StringIterate.select("\u3042\uD840\uDC00\u3044\uD840\uDC03\u3046\uD83D\uDE09", CodePointPredicate.IS_BMP);
        assertEquals("\u3042\u3044\u3046", string);
    }

    @Test
    public void detect()
    {
        char character = StringIterate.detect("1a2a3", CharPredicates.isLetter());
        assertEquals('a', character);
    }

    @Test
    public void detectIfNone()
    {
        char character = StringIterate.detectIfNone("123", CharPredicates.isLetter(), "b".charAt(0));
        assertEquals('b', character);
    }

    @Test
    public void detectIfNoneWithString()
    {
        char character = StringIterate.detectIfNone("123", CharPredicates.isLetter(), "b");
        assertEquals('b', character);
    }

    @Test
    public void allSatisfy()
    {
        assertTrue(StringIterate.allSatisfy("MARY", CharPredicates.isUpperCase()));
        assertFalse(StringIterate.allSatisfy("Mary", CharPredicates.isUpperCase()));
    }

    @Test
    public void allSatisfyCodePoint()
    {
        assertTrue(StringIterate.allSatisfy("MARY", CodePointPredicate.IS_UPPERCASE));
        assertFalse(StringIterate.allSatisfy("Mary", CodePointPredicate.IS_UPPERCASE));
    }

    @Test
    public void allSatisfyCodePointUnicode()
    {
        assertTrue(StringIterate.allSatisfy("\u3042\u3044\u3046", CodePointPredicate.IS_BMP));
        assertFalse(StringIterate.allSatisfy("\uD840\uDC00\uD840\uDC03\uD83D\uDE09", CodePointPredicate.IS_BMP));
    }

    @Test
    public void anySatisfy()
    {
        assertTrue(StringIterate.anySatisfy("MARY", CharPredicates.isUpperCase()));
        assertFalse(StringIterate.anySatisfy("mary", CharPredicates.isUpperCase()));
    }

    @Test
    public void anySatisfyCodePoint()
    {
        assertTrue(StringIterate.anySatisfy("MARY", CodePointPredicate.IS_UPPERCASE));
        assertFalse(StringIterate.anySatisfy("mary", CodePointPredicate.IS_UPPERCASE));
    }

    @Test
    public void anySatisfyCodePointUnicode()
    {
        assertTrue(StringIterate.anySatisfy("\u3042\u3044\u3046", CodePointPredicate.IS_BMP));
        assertFalse(StringIterate.anySatisfy("\uD840\uDC00\uD840\uDC03\uD83D\uDE09", CodePointPredicate.IS_BMP));
    }

    @Test
    public void noneSatisfy()
    {
        assertFalse(StringIterate.noneSatisfy("MaRy", CharPredicates.isUpperCase()));
        assertTrue(StringIterate.noneSatisfy("mary", CharPredicates.isUpperCase()));
    }

    @Test
    public void noneSatisfyCodePoint()
    {
        assertFalse(StringIterate.noneSatisfy("MaRy", CodePointPredicate.IS_UPPERCASE));
        assertTrue(StringIterate.noneSatisfy("mary", CodePointPredicate.IS_UPPERCASE));
    }

    @Test
    public void noneSatisfyCodePointUnicode()
    {
        assertFalse(StringIterate.noneSatisfy("\u3042\u3044\u3046", CodePointPredicate.IS_BMP));
        assertTrue(StringIterate.noneSatisfy("\uD840\uDC00\uD840\uDC03\uD83D\uDE09", CodePointPredicate.IS_BMP));
    }

    @Test
    public void isNumber()
    {
        assertTrue(StringIterate.isNumber("123"));
        assertFalse(StringIterate.isNumber("abc"));
        assertFalse(StringIterate.isNumber(""));
    }

    @Test
    public void isAlphaNumeric()
    {
        assertTrue(StringIterate.isAlphaNumeric("123"));
        assertTrue(StringIterate.isAlphaNumeric("abc"));
        assertTrue(StringIterate.isAlphaNumeric("123abc"));
        assertFalse(StringIterate.isAlphaNumeric("!@#"));
        assertFalse(StringIterate.isAlphaNumeric(""));
    }

    @Test
    public void csvTokensToList()
    {
        String tokens = "Ted,Mary  ";
        MutableList<String> results = StringIterate.csvTokensToList(tokens);
        Verify.assertSize(2, results);
        Verify.assertStartsWith(results, "Ted", "Mary  ");
    }

    @Test
    public void csvTokensToSortedList()
    {
        String tokens = " Ted, Mary ";
        MutableList<String> results = StringIterate.csvTokensToSortedList(tokens);
        Verify.assertSize(2, results);
        Verify.assertStartsWith(results, " Mary ", " Ted");
    }

    @Test
    public void csvTrimmedTokensToSortedList()
    {
        String tokens = " Ted,Mary ";
        MutableList<String> results = StringIterate.csvTrimmedTokensToSortedList(tokens);
        Verify.assertSize(2, results);
        Verify.assertStartsWith(results, "Mary", "Ted");
    }

    @Test
    public void csvTokensToSet()
    {
        String tokens = "Ted,Mary";
        MutableSet<String> results = StringIterate.csvTokensToSet(tokens);
        Verify.assertSize(2, results);
        Verify.assertContainsAll(results, "Mary", "Ted");
    }

    @Test
    public void csvTokensToReverseSortedList()
    {
        String tokens = "Ted,Mary";
        MutableList<String> results = StringIterate.csvTokensToReverseSortedList(tokens);
        Verify.assertSize(2, results);
    }

    @Test
    public void tokensToMap()
    {
        String tokens = "1:Ted|2:Mary";
        MutableMap<String, String> results = StringIterate.tokensToMap(tokens);
        Verify.assertSize(2, results);
        Verify.assertContainsKeyValue("1", "Ted", results);
        Verify.assertContainsKeyValue("2", "Mary", results);
    }

    @Test
    public void tokensToMapWithFunctions()
    {
        String tokens = "1:Ted|2:Mary";
        Function<String, String> stringPassThruFunction = Functions.getPassThru();
        MutableMap<Integer, String> results = StringIterate.tokensToMap(tokens, "|", ":", Integer::valueOf, stringPassThruFunction);
        Verify.assertSize(2, results);
        Verify.assertContainsKeyValue(1, "Ted", results);
        Verify.assertContainsKeyValue(2, "Mary", results);
    }

    @Test
    public void reject()
    {
        String string = StringIterate.reject("1a2b3c", CharPredicates.isDigit());
        assertEquals("abc", string);
    }

    @Test
    public void rejectCodePoint()
    {
        String string = StringIterate.reject("1a2b3c", CodePointPredicate.IS_DIGIT);
        assertEquals("abc", string);
    }

    @Test
    public void count()
    {
        int count = StringIterate.count("1a2a3", CharPredicates.isDigit());
        assertEquals(3, count);
    }

    @Test
    public void countCodePoint()
    {
        int count = StringIterate.count("1a2a3", CodePointPredicate.IS_DIGIT);
        assertEquals(3, count);
    }

    @Test
    public void occurrencesOf()
    {
        int count = StringIterate.occurrencesOf("1a2a3", 'a');
        assertEquals(2, count);
    }

    @Test
    public void occurrencesOf_multiple_character_string_throws()
    {
        assertThrows(IllegalArgumentException.class, () -> StringIterate.occurrencesOf("1a2a3", "abc"));
    }

    @Test
    public void occurrencesOfCodePoint()
    {
        int count = StringIterate.occurrencesOf("1a2a3", "a".codePointAt(0));
        assertEquals(2, count);
    }

    @Test
    public void occurrencesOfString()
    {
        int count = StringIterate.occurrencesOf("1a2a3", "a");
        assertEquals(2, count);
    }

    @Test
    public void count2()
    {
        int count = StringIterate.count("1a2a3", CharPredicates.isUndefined());
        assertEquals(0, count);
    }

    @Test
    public void count2CodePoint()
    {
        int count = StringIterate.count("1a2a3", CodePointPredicate.IS_UNDEFINED);
        assertEquals(0, count);
    }

    @Test
    public void forEach()
    {
        StringBuilder builder = new StringBuilder();
        StringIterate.forEach("1a2b3c", (CharProcedure) builder::append);
        assertEquals("1a2b3c", builder.toString());
    }

    @Test
    public void forEachCodePoint()
    {
        StringBuilder builder = new StringBuilder();
        StringIterate.forEach("1a2b3c", (CodePointProcedure) builder::appendCodePoint);
        assertEquals("1a2b3c", builder.toString());
    }

    @Test
    public void forEachCodePointUnicode()
    {
        StringBuilder builder = new StringBuilder();
        StringIterate.forEach("\u3042\uD840\uDC00\u3044\uD840\uDC03\u3046\uD83D\uDE09", (CodePointProcedure) builder::appendCodePoint);
        assertEquals("\u3042\uD840\uDC00\u3044\uD840\uDC03\u3046\uD83D\uDE09", builder.toString());
    }

    @Test
    public void reverseForEach()
    {
        StringBuilder builder = new StringBuilder();
        StringIterate.reverseForEach("1a2b3c", (CharProcedure) builder::append);
        assertEquals("c3b2a1", builder.toString());

        StringIterate.reverseForEach("", (char character) -> fail());
    }

    @Test
    public void reverseForEachCodePoint()
    {
        StringBuilder builder = new StringBuilder();
        StringIterate.reverseForEach("1a2b3c", (CodePointProcedure) builder::appendCodePoint);
        assertEquals("c3b2a1", builder.toString());

        StringIterate.reverseForEach("", (int codePoint) -> fail());
    }

    @Test
    public void reverseForEachCodePointUnicode()
    {
        StringBuilder builder = new StringBuilder();
        StringIterate.reverseForEach("\u3042\uD840\uDC00\u3044\uD840\uDC03\u3046\uD83D\uDE09", (CodePointProcedure) builder::appendCodePoint);
        assertEquals("\uD83D\uDE09\u3046\uD840\uDC03\u3044\uD840\uDC00\u3042", builder.toString());
        StringIterate.reverseForEach("", (int codePoint) -> fail());
    }

    @Test
    public void reverseForEachCodePointInvalidUnicode()
    {
        StringBuilder builder = new StringBuilder();
        StringIterate.reverseForEach("\u3042\uDC00\uD840\u3044\uDC03\uD840\u3046\uDE09\uD83D", (CodePointProcedure) builder::appendCodePoint);
        assertEquals("\uD83D\uDE09\u3046\uD840\uDC03\u3044\uD840\uDC00\u3042", builder.toString());

        StringBuilder builder2 = new StringBuilder();
        StringIterate.reverseForEach("\u3042\uD840\u3044\uD840\u3046\uD840", (CodePointProcedure) builder2::appendCodePoint);
        assertEquals("\uD840\u3046\uD840\u3044\uD840\u3042", builder2.toString());

        StringBuilder builder3 = new StringBuilder();
        StringIterate.reverseForEach("\u3042\uDC00\u3044\uDC03\u3046\uDC06", (CodePointProcedure) builder3::appendCodePoint);
        assertEquals("\uDC06\u3046\uDC03\u3044\uDC00\u3042", builder3.toString());

        StringIterate.reverseForEach("", (int codePoint) -> fail());
    }

    @Test
    public void forEachToken()
    {
        String tokens = "1,2";
        MutableList<Integer> list = Lists.mutable.of();
        StringIterate.forEachToken(tokens, ",", Procedures.throwing(string -> list.add(Integer.valueOf(string))));
        Verify.assertSize(2, list);
        Verify.assertContains(1, list);
        Verify.assertContains(2, list);
    }

    @Test
    public void forEachTrimmedToken()
    {
        String tokens = " 1,2 ";
        MutableList<Integer> list = Lists.mutable.of();
        StringIterate.forEachTrimmedToken(tokens, ",", Procedures.throwing(string -> list.add(Integer.valueOf(string))));
        Verify.assertSize(2, list);
        Verify.assertContains(1, list);
        Verify.assertContains(2, list);
    }

    @Test
    public void csvTrimmedTokenToList()
    {
        String tokens = " 1,2 ";
        assertEquals(FastList.newListWith("1", "2"), StringIterate.csvTrimmedTokensToList(tokens));
    }

    @Test
    public void injectIntoTokens()
    {
        assertEquals("123", StringIterate.injectIntoTokens("1,2,3", ",", null, AddFunction.STRING));
    }

    @Test
    public void getLastToken()
    {
        assertEquals("charlie", StringIterate.getLastToken("alpha~|~beta~|~charlie", "~|~"));
        assertEquals("123", StringIterate.getLastToken("123", "~|~"));
        assertEquals("", StringIterate.getLastToken("", "~|~"));
        assertNull(StringIterate.getLastToken(null, "~|~"));
        assertEquals("", StringIterate.getLastToken("123~|~", "~|~"));
        assertEquals("123", StringIterate.getLastToken("~|~123", "~|~"));
    }

    @Test
    public void getFirstToken()
    {
        assertEquals("alpha", StringIterate.getFirstToken("alpha~|~beta~|~charlie", "~|~"));
        assertEquals("123", StringIterate.getFirstToken("123", "~|~"));
        assertEquals("", StringIterate.getFirstToken("", "~|~"));
        assertNull(StringIterate.getFirstToken(null, "~|~"));
        assertEquals("123", StringIterate.getFirstToken("123~|~", "~|~"));
        assertEquals("", StringIterate.getFirstToken("~|~123,", "~|~"));
    }

    @Test
    public void isEmptyOrWhitespace()
    {
        assertTrue(StringIterate.isEmptyOrWhitespace("   "));
        assertFalse(StringIterate.isEmptyOrWhitespace(" 1  "));
    }

    @Test
    public void notEmptyOrWhitespace()
    {
        assertFalse(StringIterate.notEmptyOrWhitespace("   "));
        assertTrue(StringIterate.notEmptyOrWhitespace(" 1  "));
    }

    @Test
    public void isEmpty()
    {
        assertTrue(StringIterate.isEmpty(""));
        assertFalse(StringIterate.isEmpty("   "));
        assertFalse(StringIterate.isEmpty("1"));
    }

    @Test
    public void notEmpty()
    {
        assertFalse(StringIterate.notEmpty(""));
        assertTrue(StringIterate.notEmpty("   "));
        assertTrue(StringIterate.notEmpty("1"));
    }

    @Test
    public void repeat()
    {
        assertEquals("", StringIterate.repeat("", 42));
        assertEquals("    ", StringIterate.repeat(' ', 4));
        assertEquals("        ", StringIterate.repeat(" ", 8));
        assertEquals("CubedCubedCubed", StringIterate.repeat("Cubed", 3));
    }

    @Test
    public void padOrTrim()
    {
        assertEquals("abcdefghijkl", StringIterate.padOrTrim("abcdefghijkl", 12));
        assertEquals("this n", StringIterate.padOrTrim("this needs to be trimmed", 6));
        assertEquals("pad this      ", StringIterate.padOrTrim("pad this", 14));
    }

    @Test
    public void string()
    {
        assertEquals("Token2", StringIterate.getLastToken("Token1DelimiterToken2", "Delimiter"));
    }

    @Test
    public void toList()
    {
        assertEquals(FastList.newListWith('a', 'a', 'b', 'c', 'd', 'e'), StringIterate.toList("aabcde"));
    }

    @Test
    public void toLowercaseList()
    {
        MutableList<Character> set = StringIterate.toLowercaseList("America");
        assertEquals(FastList.newListWith('a', 'm', 'e', 'r', 'i', 'c', 'a'), set);
    }

    @Test
    public void toUppercaseList()
    {
        MutableList<Character> set = StringIterate.toUppercaseList("America");
        assertEquals(FastList.newListWith('A', 'M', 'E', 'R', 'I', 'C', 'A'), set);
    }

    @Test
    public void toSet()
    {
        Verify.assertSetsEqual(UnifiedSet.newSetWith('a', 'b', 'c', 'd', 'e'), StringIterate.toSet("aabcde"));
    }

    @Test
    public void chunk()
    {
        assertEquals(
                Lists.immutable.with("ab", "cd", "ef"),
                StringIterate.chunk("abcdef", 2));

        assertEquals(
                Lists.immutable.with("abc", "def"),
                StringIterate.chunk("abcdef", 3));

        assertEquals(
                Lists.immutable.with("abc", "def", "g"),
                StringIterate.chunk("abcdefg", 3));

        assertEquals(
                Lists.immutable.with("abcdef"),
                StringIterate.chunk("abcdef", 6));

        assertEquals(
                Lists.immutable.with("abcdef"),
                StringIterate.chunk("abcdef", 7));

        assertEquals(
                Lists.immutable.with(),
                StringIterate.chunk("", 2));
    }

    @Test
    public void chunkWithZeroSize()
    {
        assertThrows(IllegalArgumentException.class, () -> StringIterate.chunk("abcdef", 0));
    }

    @Test
    public void chunkWithNegativeSize()
    {
        assertThrows(IllegalArgumentException.class, () -> StringIterate.chunk("abcdef", -42));
    }

    @Test
    public void toLowercaseSet()
    {
        MutableSet<Character> set = StringIterate.toLowercaseSet("America");
        assertEquals(UnifiedSet.newSetWith('a', 'm', 'e', 'r', 'i', 'c'), set);
        assertEquals(StringIterate.asLowercaseSet("America"), set);
    }

    @Test
    public void toUppercaseSet()
    {
        MutableSet<Character> set = StringIterate.toUppercaseSet("America");
        assertEquals(UnifiedSet.newSetWith('A', 'M', 'E', 'R', 'I', 'C'), set);
        assertEquals(StringIterate.asUppercaseSet("America"), set);
    }

    @Test
    public void splitAtIndex()
    {
        String oompaLoompa = "oompaloompa";

        assertEquals(Tuples.twin("oompa", "loompa"), StringIterate.splitAtIndex(oompaLoompa, 5));
        assertEquals(Tuples.twin("", oompaLoompa), StringIterate.splitAtIndex(oompaLoompa, 0));
        assertEquals(Tuples.twin(oompaLoompa, ""), StringIterate.splitAtIndex(oompaLoompa, oompaLoompa.length()));

        assertEquals(Tuples.twin("", ""), StringIterate.splitAtIndex("", 0));

        assertThrows(StringIndexOutOfBoundsException.class, () -> StringIterate.splitAtIndex(oompaLoompa, 17));
        assertThrows(StringIndexOutOfBoundsException.class, () -> StringIterate.splitAtIndex(oompaLoompa, -8));
    }

    @Test
    public void toLowercaseBag()
    {
        MutableBag<Character> lowercaseBag = StringIterate.toLowercaseBag("America");
        assertEquals(2, lowercaseBag.occurrencesOf(Character.valueOf('a')));
        assertEquals(1, lowercaseBag.occurrencesOf(Character.valueOf('m')));
        assertEquals(1, lowercaseBag.occurrencesOf(Character.valueOf('e')));
        assertEquals(1, lowercaseBag.occurrencesOf(Character.valueOf('r')));
        assertEquals(1, lowercaseBag.occurrencesOf(Character.valueOf('i')));
        assertEquals(1, lowercaseBag.occurrencesOf(Character.valueOf('c')));
    }

    @Test
    public void toUppercaseBag()
    {
        MutableBag<Character> uppercaseBag = StringIterate.toUppercaseBag("America");
        assertEquals(2, uppercaseBag.occurrencesOf(Character.valueOf('A')));
        assertEquals(1, uppercaseBag.occurrencesOf(Character.valueOf('M')));
        assertEquals(1, uppercaseBag.occurrencesOf(Character.valueOf('E')));
        assertEquals(1, uppercaseBag.occurrencesOf(Character.valueOf('R')));
        assertEquals(1, uppercaseBag.occurrencesOf(Character.valueOf('I')));
        assertEquals(1, uppercaseBag.occurrencesOf(Character.valueOf('C')));
    }

    @Test
    public void toBag()
    {
        MutableBag<Character> bag = StringIterate.toBag("America");
        assertEquals(1, bag.occurrencesOf(Character.valueOf('A')));
        assertEquals(1, bag.occurrencesOf(Character.valueOf('m')));
        assertEquals(1, bag.occurrencesOf(Character.valueOf('e')));
        assertEquals(1, bag.occurrencesOf(Character.valueOf('r')));
        assertEquals(1, bag.occurrencesOf(Character.valueOf('i')));
        assertEquals(1, bag.occurrencesOf(Character.valueOf('c')));
        assertEquals(1, bag.occurrencesOf(Character.valueOf('a')));
    }

    @Test
    public void classIsNonInstantiable()
    {
        Verify.assertClassNonInstantiable(StringIterate.class);
    }
}
