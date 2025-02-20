package org.eclipse.collections.impl.test;

import java.io.IOException;
import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.ComparisonFailure;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;
import org.junit.internal.ArrayComparisonFailure;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;

/**
 * Based on the test org.junit.tests.assertion.AssertionTest in JUnit 4, replacing all usages of {@link Assert} with {@link Verify}. Ensures that {@code Verify} remains source compatible with {@code Assert}.
 */
@SuppressWarnings("StaticMethodReferencedViaSubclass")
public class AssertTest
{
    private static final String ASSERTION_ERROR_EXPECTED = "AssertionError expected";

    @Test(expected = AssertionError.class)
    public void fails()
    {
        Verify.fail();
    }

    @Test
    public void failWithNoMessageToString()
    {
        try
        {
            Verify.fail();
        }
        catch (AssertionError exception)
        {
            Verify.assertEquals("java.lang.AssertionError", exception.toString());
            return;
        }
        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void failWithMessageToString()
    {
        try
        {
            Verify.fail("woops!");
        }
        catch (AssertionError exception)
        {
            Verify.assertEquals("java.lang.AssertionError: woops!", exception.toString());
            return;
        }
        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void arraysNotEqual()
    {
        this.assertArrayEqualsFailure(
                new Object[]{"right"},
                new Object[]{"wrong"},
                "arrays first differed at element [0]; expected:<[right]> but was:<[wrong]>");
    }

    @Test
    public void arraysNotEqualWithMessage()
    {
        this.assertArrayEqualsFailure(
                "not equal",
                new Object[]{"right"},
                new Object[]{"wrong"},
                "not equal: arrays first differed at element [0]; expected:<[right]> but was:<[wrong]>");
    }

    @Test
    public void arraysExpectedNullMessage()
    {
        try
        {
            Verify.assertArrayEquals("not equal", null, new Object[]{new Object()});
        }
        catch (AssertionError exception)
        {
            Verify.assertEquals("not equal: expected array was null", exception.getMessage());
            return;
        }
        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void arraysActualNullMessage()
    {
        try
        {
            Verify.assertArrayEquals("not equal", new Object[]{new Object()}, null);
        }
        catch (AssertionError exception)
        {
            Verify.assertEquals("not equal: actual array was null", exception.getMessage());
            return;
        }
        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void arraysDifferentLengthDifferingAtStartMessage()
    {
        this.assertArrayEqualsFailure(
                "not equal",
                new Object[]{true},
                new Object[]{false, true},
                "not equal: array lengths differed, expected.length=1 actual.length=2; arrays first differed at element [0]; expected:<true> but was:<false>");
    }

    @Test
    public void arraysDifferentLengthDifferingAtEndMessage()
    {
        this.assertArrayEqualsFailure(
                "not equal",
                new Object[]{true},
                new Object[]{true, false},
                "not equal: array lengths differed, expected.length=1 actual.length=2; arrays first differed at element [1]; expected:<end of array> but was:<false>");
    }

    @Test
    public void arraysDifferentLengthDifferingAtEndAndExpectedArrayLongerMessage()
    {
        this.assertArrayEqualsFailure(
                "not equal",
                new Object[]{true, false},
                new Object[]{true},
                "not equal: array lengths differed, expected.length=2 actual.length=1; arrays first differed at element [1]; expected:<false> but was:<end of array>");
    }

    @Test
    public void arraysElementsDiffer()
    {
        this.assertArrayEqualsFailure(
                "not equal",
                new Object[]{"this is a very long string in the middle of an array"},
                new Object[]{"this is another very long string in the middle of an array"},
                "not equal: arrays first differed at element [0]; expected:<this is a[] very long string in...> but was:<this is a[nother] very long string in...>");
    }

    @Test
    public void arraysDifferAtElement0nullMessage()
    {
        this.assertArrayEqualsFailure(
                new Object[]{true},
                new Object[]{false},
                "arrays first differed at element [0]; expected:<true> but was:<false>");
    }

    @Test
    public void arraysDifferAtElement1nullMessage()
    {
        this.assertArrayEqualsFailure(
                new Object[]{true, true},
                new Object[]{true, false},
                "arrays first differed at element [1]; expected:<true> but was:<false>");
    }

    @Test
    public void arraysDifferAtElement0withMessage()
    {
        this.assertArrayEqualsFailure(
                "message",
                new Object[]{true},
                new Object[]{false},
                "message: arrays first differed at element [0]; expected:<true> but was:<false>");
    }

    @Test
    public void arraysDifferAtElement1withMessage()
    {
        this.assertArrayEqualsFailure(
                "message",
                new Object[]{true, true},
                new Object[]{true, false},
                "message: arrays first differed at element [1]; expected:<true> but was:<false>");
    }

    @Test
    public void multiDimensionalArraysAreEqual()
    {
        Verify.assertArrayEquals(
                new Object[][]{{true, true}, {false, false}},
                new Object[][]{{true, true}, {false, false}});
    }

    @Test
    public void multiDimensionalIntArraysAreEqual()
    {
        int[][] int1 = {{1, 2, 3}, {4, 5, 6}};
        int[][] int2 = {{1, 2, 3}, {4, 5, 6}};
        Verify.assertArrayEquals(int1, int2);
    }

    @Test
    public void oneDimensionalPrimitiveArraysAreEqual()
    {
        Verify.assertArrayEquals(new boolean[]{true}, new boolean[]{true});
        Verify.assertArrayEquals(new byte[]{1}, new byte[]{1});
        Verify.assertArrayEquals(new char[]{1}, new char[]{1});
        Verify.assertArrayEquals(new short[]{1}, new short[]{1});
        Verify.assertArrayEquals(new int[]{1}, new int[]{1});
        Verify.assertArrayEquals(new long[]{1}, new long[]{1});
        Verify.assertArrayEquals(new double[]{1.0}, new double[]{1.0}, 1.0);
        Verify.assertArrayEquals(new float[]{1.0f}, new float[]{1.0f}, 1.0f);
    }

    @Test(expected = AssertionError.class)
    public void oneDimensionalDoubleArraysAreNotEqual()
    {
        Verify.assertArrayEquals(new double[]{1.0}, new double[]{2.5}, 1.0);
    }

    @Test(expected = AssertionError.class)
    public void oneDimensionalFloatArraysAreNotEqual()
    {
        Verify.assertArrayEquals(new float[]{1.0f}, new float[]{2.5f}, 1.0f);
    }

    @Test(expected = AssertionError.class)
    public void oneDimensionalBooleanArraysAreNotEqual()
    {
        Verify.assertArrayEquals(new boolean[]{true}, new boolean[]{false});
    }

    @Test(expected = AssertionError.class)
    public void integerDoesNotEqualLong()
    {
        Verify.assertEquals(Integer.valueOf(1), Long.valueOf(1));
    }

    @Test
    public void intsEqualLongs()
    {
        Verify.assertEquals(1, 1L);
    }

    @Test
    public void multiDimensionalArraysDeclaredAsOneDimensionalAreEqual()
    {
        Verify.assertArrayEquals(
                new Object[]{new Object[]{true, true}, new Object[]{false, false}},
                new Object[]{new Object[]{true, true}, new Object[]{false, false}});
    }

    @Test
    public void multiDimensionalArraysAreNotEqual()
    {
        this.assertArrayEqualsFailure(
                "message",
                new Object[][]{{true, true}, {false, false}},
                new Object[][]{{true, true}, {true, false}},
                "message: arrays first differed at element [1][0]; expected:<false> but was:<true>");
    }

    @Test
    public void multiDimensionalArraysAreNotEqualNoMessage()
    {
        this.assertArrayEqualsFailure(
                new Object[][]{{true, true}, {false, false}},
                new Object[][]{{true, true}, {true, false}},
                "arrays first differed at element [1][0]; expected:<false> but was:<true>");
    }

    @Test
    public void twoDimensionalArraysDifferentOuterLengthNotEqual()
    {
        this.assertArrayEqualsFailure(
                "not equal",
                new Object[][]{{true}, {}},
                new Object[][]{{}},
                "not equal: array lengths differed, expected.length=1 actual.length=0; arrays first differed at element [0][0]; expected:<true> but was:<end of array>");
        this.assertArrayEqualsFailure(
                "not equal",
                new Object[][]{{}, {true}},
                new Object[][]{{}},
                "not equal: array lengths differed, expected.length=2 actual.length=1; arrays first differed at element [1]; expected:<java.lang.Object[1]> but was:<end of array>");
        this.assertArrayEqualsFailure(
                "not equal",
                new Object[][]{{}},
                new Object[][]{{true}, {}},
                "not equal: array lengths differed, expected.length=0 actual.length=1; arrays first differed at element [0][0]; expected:<end of array> but was:<true>");
        this.assertArrayEqualsFailure(
                "not equal",
                new Object[][]{{}},
                new Object[][]{{}, {true}},
                "not equal: array lengths differed, expected.length=1 actual.length=2; arrays first differed at element [1]; expected:<end of array> but was:<java.lang.Object[1]>");
    }

    @Test
    public void primitiveArraysConvertedToStringCorrectly()
    {
        this.assertArrayEqualsFailure(
                "not equal",
                new boolean[][]{{}, {true}},
                new boolean[][]{{}},
                "not equal: array lengths differed, expected.length=2 actual.length=1; arrays first differed at element [1]; expected:<boolean[1]> but was:<end of array>");
        this.assertArrayEqualsFailure(
                "not equal",
                new int[][]{{}, {23}},
                new int[][]{{}},
                "not equal: array lengths differed, expected.length=2 actual.length=1; arrays first differed at element [1]; expected:<int[1]> but was:<end of array>");
    }

    @Test
    public void twoDimensionalArraysConvertedToStringCorrectly()
    {
        this.assertArrayEqualsFailure(
                "not equal",
                new Object[][][]{{}, {{true}}},
                new Object[][][]{{}},
                "not equal: array lengths differed, expected.length=2 actual.length=1; arrays first differed at element [1]; expected:<java.lang.Object[][1]> but was:<end of array>");
    }

    @Test
    public void twoDimensionalArraysDifferentInnerLengthNotEqual()
    {
        this.assertArrayEqualsFailure(
                "not equal",
                new Object[][]{{true}, {}},
                new Object[][]{{}, {}},
                "not equal: array lengths differed, expected.length=1 actual.length=0; arrays first differed at element [0][0]; expected:<true> but was:<end of array>");
        this.assertArrayEqualsFailure(
                "not equal",
                new Object[][]{{}, {true}},
                new Object[][]{{}, {}},
                "not equal: array lengths differed, expected.length=1 actual.length=0; arrays first differed at element [1][0]; expected:<true> but was:<end of array>");
        this.assertArrayEqualsFailure(
                "not equal",
                new Object[][]{{}, {}},
                new Object[][]{{true}, {}},
                "not equal: array lengths differed, expected.length=0 actual.length=1; arrays first differed at element [0][0]; expected:<end of array> but was:<true>");
        this.assertArrayEqualsFailure(
                "not equal",
                new Object[][]{{}, {}},
                new Object[][]{{}, {true}},
                "not equal: array lengths differed, expected.length=0 actual.length=1; arrays first differed at element [1][0]; expected:<end of array> but was:<true>");
    }

    private void assertArrayEqualsFailure(Object[] expecteds, Object[] actuals, String expectedMessage)
    {
        try
        {
            Verify.assertArrayEquals(expecteds, actuals);
        }
        catch (ArrayComparisonFailure e)
        {
            Verify.assertEquals(expectedMessage, e.getMessage());
            return;
        }
        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    private void assertArrayEqualsFailure(String message, Object[] expecteds, Object[] actuals, String expectedMessage)
    {
        try
        {
            Verify.assertArrayEquals(message, expecteds, actuals);
        }
        catch (ArrayComparisonFailure e)
        {
            Verify.assertEquals(expectedMessage, e.getMessage());
            return;
        }
        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void multiDimensionalArraysDifferentLengthMessage()
    {
        try
        {
            Verify.assertArrayEquals(
                    "message",
                    new Object[][]{{true, true}, {false, false}},
                    new Object[][]{{true, true}, {false}});
        }
        catch (AssertionError exception)
        {
            Verify.assertEquals(
                    "message: array lengths differed, expected.length=2 actual.length=1; arrays first differed at element [1][1]; expected:<false> but was:<end of array>",
                    exception.getMessage());
            return;
        }

        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void multiDimensionalArraysDifferentLengthNoMessage()
    {
        try
        {
            Verify.assertArrayEquals(
                    new Object[][]{{true, true}, {false, false}},
                    new Object[][]{{true, true}, {false}});
        }
        catch (AssertionError exception)
        {
            Verify.assertEquals(
                    "array lengths differed, expected.length=2 actual.length=1; arrays first differed at element [1][1]; expected:<false> but was:<end of array>",
                    exception.getMessage());
            return;
        }

        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void arraysWithNullElementEqual()
    {
        Object[] objects1 = {null};
        Object[] objects2 = {null};
        Verify.assertArrayEquals(objects1, objects2);
    }

    @Test
    public void stringsDifferWithUserMessage()
    {
        try
        {
            Verify.assertEquals("not equal", "one", "two");
        }
        catch (ComparisonFailure exception)
        {
            Verify.assertEquals("not equal expected:<[one]> but was:<[two]>", exception.getMessage());
            return;
        }
        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void arraysEqual()
    {
        Object element = new Object();
        Object[] objects1 = {element};
        Object[] objects2 = {element};
        Verify.assertArrayEquals(objects1, objects2);
    }

    @Test
    public void arraysEqualWithMessage()
    {
        Object element = new Object();
        Object[] objects1 = {element};
        Object[] objects2 = {element};
        Verify.assertArrayEquals("equal", objects1, objects2);
    }

    @Test
    public void equals()
    {
        Object o = new Object();
        Verify.assertEquals(o, o);
        Verify.assertEquals("abc", "abc");
        Verify.assertEquals(true, true);
        Verify.assertEquals((byte) 1, (byte) 1);
        Verify.assertEquals('a', 'a');
        Verify.assertEquals((short) 1, (short) 1);
        Verify.assertEquals(1, 1); // int by default, cast is unnecessary
        Verify.assertEquals(1L, 1L);
        Verify.assertEquals(1.0, 1.0, 0.0);
        Verify.assertEquals(1.0d, 1.0d, 0.0d);
    }

    @Test(expected = AssertionError.class)
    public void notEqualsObjectWithNull()
    {
        Verify.assertEquals(new Object(), null);
    }

    @Test(expected = AssertionError.class)
    public void notEqualsNullWithObject()
    {
        Verify.assertEquals(null, new Object());
    }

    @Test
    public void notEqualsObjectWithNullWithMessage()
    {
        Object o = new Object();
        try
        {
            Verify.assertEquals("message", null, o);
        }
        catch (AssertionError e)
        {
            Verify.assertEquals("message expected:<null> but was:<" + o + ">", e.getMessage());
            return;
        }
        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void notEqualsNullWithObjectWithMessage()
    {
        Object o = new Object();
        try
        {
            Verify.assertEquals("message", o, null);
        }
        catch (AssertionError e)
        {
            Verify.assertEquals("message expected:<" + o + "> but was:<null>", e.getMessage());
            return;
        }
        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test(expected = AssertionError.class)
    public void objectsNotEquals()
    {
        Verify.assertEquals(new Object(), new Object());
    }

    @Test(expected = ComparisonFailure.class)
    public void stringsNotEqual()
    {
        Verify.assertEquals("abc", "def");
    }

    @Test(expected = AssertionError.class)
    public void booleansNotEqual()
    {
        Verify.assertEquals(true, false);
    }

    @Test(expected = AssertionError.class)
    public void bytesNotEqual()
    {
        Verify.assertEquals((byte) 1, (byte) 2);
    }

    @Test(expected = AssertionError.class)
    public void charsNotEqual()
    {
        Verify.assertEquals('a', 'b');
    }

    @Test(expected = AssertionError.class)
    public void shortsNotEqual()
    {
        Verify.assertEquals((short) 1, (short) 2);
    }

    @Test(expected = AssertionError.class)
    public void intsNotEqual()
    {
        Verify.assertEquals(1, 2);
    }

    @Test(expected = AssertionError.class)
    public void longsNotEqual()
    {
        Verify.assertEquals(1L, 2L);
    }

    @Test(expected = AssertionError.class)
    public void floatsNotEqual()
    {
        Verify.assertEquals(1.0, 2.0, 0.9);
    }

    @SuppressWarnings("deprecation")
    @Test(expected = AssertionError.class)
    public void floatsNotEqualWithoutDelta()
    {
        Verify.assertEquals(1.0, 1.1);
    }

    @Test
    public void floatsNotDoublesInArrays()
    {
        float delta = 4.444f;
        float[] f1 = {1.111f};
        float[] f2 = {5.555f};
        Verify.assertArrayEquals(f1, f2, delta);
    }

    @Test(expected = AssertionError.class)
    public void bigDecimalsNotEqual()
    {
        Verify.assertEquals(new BigDecimal("123.4"), new BigDecimal("123.0"));
    }

    @Test(expected = AssertionError.class)
    public void doublesNotEqual()
    {
        Verify.assertEquals(1.0d, 2.0d, 0.9d);
    }

    @Test
    public void naNsAreEqual()
    {
        Verify.assertEquals(Float.NaN, Float.NaN, Float.POSITIVE_INFINITY);
        Verify.assertEquals(Double.NaN, Double.NaN, Double.POSITIVE_INFINITY);
    }

    @SuppressWarnings("unused")
    @Test
    public void nullNullmessage()
    {
        try
        {
            Verify.assertNull("junit");
        }
        catch (AssertionError e)
        {
            Verify.assertEquals("expected null, but was:<junit>", e.getMessage());
            return;
        }
        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @SuppressWarnings("unused")
    @Test
    public void nullWithMessage()
    {
        try
        {
            Verify.assertNull("message", "hello");
        }
        catch (AssertionError exception)
        {
            Verify.assertEquals("message expected null, but was:<hello>", exception.getMessage());
            return;
        }
        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void same()
    {
        Object o1 = new Object();
        Verify.assertSame(o1, o1);
    }

    @Test
    public void notSame()
    {
        Object o1 = new Object();
        Object o2 = new Object();
        Verify.assertNotSame(o1, o2);
    }

    @Test(expected = AssertionError.class)
    public void objectsNotSame()
    {
        Verify.assertSame(new Object(), new Object());
    }

    @Test(expected = AssertionError.class)
    public void objectsAreSame()
    {
        Object o = new Object();
        Verify.assertNotSame(o, o);
    }

    @Test
    public void sameWithMessage()
    {
        try
        {
            Verify.assertSame("not same", "hello", "good-bye");
        }
        catch (AssertionError exception)
        {
            Verify.assertEquals(
                    "not same expected same:<hello> was not:<good-bye>",
                    exception.getMessage());
            return;
        }
        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void sameNullMessage()
    {
        try
        {
            Verify.assertSame("hello", "good-bye");
        }
        catch (AssertionError exception)
        {
            Verify.assertEquals("expected same:<hello> was not:<good-bye>", exception.getMessage());
            return;
        }
        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void notSameWithMessage()
    {
        Object o = new Object();
        try
        {
            Verify.assertNotSame("message", o, o);
        }
        catch (AssertionError exception)
        {
            Verify.assertEquals("message expected not same", exception.getMessage());
            return;
        }
        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void notSameNullMessage()
    {
        Object o = new Object();
        try
        {
            Verify.assertNotSame(o, o);
        }
        catch (AssertionError exception)
        {
            Verify.assertEquals("expected not same", exception.getMessage());
            return;
        }
        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void nullMessage()
    {
        try
        {
            Verify.fail(null);
        }
        catch (AssertionError exception)
        {
            // we used to expect getMessage() to return ""; see failWithNoMessageToString()
            Verify.assertNull(exception.getMessage());
            return;
        }
        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void nullMessageDisappearsWithStringAssertEquals()
    {
        try
        {
            Verify.assertEquals(null, "a", "b");
        }
        catch (ComparisonFailure e)
        {
            Verify.assertEquals("expected:<[a]> but was:<[b]>", e.getMessage());
            return;
        }
        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void nullMessageDisappearsWithAssertEquals()
    {
        try
        {
            Verify.assertEquals(null, 1, 2);
        }
        catch (AssertionError e)
        {
            Verify.assertEquals("expected:<1> but was:<2>", e.getMessage());
            return;
        }
        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test(expected = AssertionError.class)
    public void arraysDeclaredAsObjectAreComparedAsObjects()
    {
        Object a1 = new Object[]{"abc"};
        Object a2 = new Object[]{"abc"};
        Verify.assertEquals(a1, a2);
    }

    @Test
    public void implicitTypecastEquality()
    {
        byte b = 1;
        short s = 1;
        int i = 1;
        long l = 1L;
        float f = 1.0f;
        double d = 1.0;

        Verify.assertEquals(b, s);
        Verify.assertEquals(b, i);
        Verify.assertEquals(b, l);
        Verify.assertEquals(s, i);
        Verify.assertEquals(s, l);
        Verify.assertEquals(i, l);
        Verify.assertEquals(f, d, 0);
    }

    @Test
    public void errorMessageDistinguishesDifferentValuesWithSameToString()
    {
        try
        {
            Verify.assertEquals("4", Integer.valueOf(4));
        }
        catch (AssertionError e)
        {
            Verify.assertEquals("expected: java.lang.String<4> but was: java.lang.Integer<4>", e.getMessage());
            return;
        }
        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void assertThatIncludesDescriptionOfTestedValueInErrorMessage()
    {
        String expected = "expected";
        String actual = "actual";

        String expectedMessage = "identifier\nExpected: \"expected\"\n     but: was \"actual\"";

        try
        {
            Verify.assertThat("identifier", actual, equalTo(expected));
        }
        catch (AssertionError e)
        {
            Verify.assertEquals(expectedMessage, e.getMessage());
            return;
        }
        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void assertThatIncludesAdvancedMismatch()
    {
        String expectedMessage =
                "identifier\nExpected: is an instance of java.lang.Integer\n     but: \"actual\" is a java.lang.String";

        try
        {
            Verify.assertThat("identifier", "actual", is(instanceOf(Integer.class)));
        }
        catch (AssertionError e)
        {
            Verify.assertEquals(expectedMessage, e.getMessage());
            return;
        }
        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void assertThatDescriptionCanBeElided()
    {
        String expected = "expected";
        String actual = "actual";

        String expectedMessage = "\nExpected: \"expected\"\n     but: was \"actual\"";

        try
        {
            Verify.assertThat(actual, equalTo(expected));
        }
        catch (AssertionError e)
        {
            Verify.assertEquals(expectedMessage, e.getMessage());
            return;
        }
        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void nullAndStringNullPrintCorrectError()
    {
        try
        {
            Verify.assertEquals(null, "null");
        }
        catch (AssertionError e)
        {
            Verify.assertEquals("expected: null<null> but was: java.lang.String<null>", e.getMessage());
            return;
        }
        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test(expected = AssertionError.class)
    public void stringNullAndNullWorksToo()
    {
        Verify.assertEquals("null", null);
    }

    private static class NullToString
    {
        @Override
        public String toString()
        {
            return null;
        }
    }

    @Test
    public void nullToString()
    {
        try
        {
            Verify.assertEquals(new NullToString(), new NullToString());
        }
        catch (AssertionError e)
        {
            Verify.assertEquals(
                    "expected: org.eclipse.collections.impl.test.AssertTest$NullToString<null> but "
                            + "was: org.eclipse.collections.impl.test.AssertTest$NullToString<null>",
                    e.getMessage());
            return;
        }

        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test(expected = AssertionError.class)
    public void compareBigDecimalAndInteger()
    {
        BigDecimal bigDecimal = new BigDecimal("1.2");
        Integer integer = Integer.valueOf("1");
        Verify.assertEquals(bigDecimal, integer);
    }

    @Test(expected = AssertionError.class)
    public void sameObjectIsNotEqual()
    {
        Object o = new Object();
        Verify.assertNotEquals(o, o);
    }

    @Test
    public void objectsWithDifferentReferencesAreNotEqual()
    {
        Verify.assertNotEquals(new Object(), new Object());
    }

    @Test
    public void assertNotEqualsIncludesCorrectMessage()
    {
        Integer value1 = 1;
        Integer value2 = 1;
        String message = "The values should be different";

        try
        {
            Verify.assertNotEquals(message, value1, value2);
        }
        catch (AssertionError e)
        {
            Verify.assertEquals(message + ". Actual: " + value1, e.getMessage());
            return;
        }

        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void assertNotEqualsIncludesTheValueBeingTested()
    {
        Integer value1 = 1;
        Integer value2 = 1;

        try
        {
            Verify.assertNotEquals(value1, value2);
        }
        catch (AssertionError e)
        {
            Verify.assertTrue(e.getMessage().contains(value1.toString()));
            return;
        }

        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void assertNotEqualsWorksWithPrimitiveTypes()
    {
        Verify.assertNotEquals(1L, 2L);
        Verify.assertNotEquals("The values should be different", 1L, 2L);
        Verify.assertNotEquals(1.0, 2.0, 0);
        Verify.assertNotEquals("The values should be different", 1.0, 2.0, 0);
        Verify.assertNotEquals(1.0f, 2.0f, 0.0f);
        Verify.assertNotEquals("The values should be different", 1.0f, 2.0f, 0.0f);
    }

    @Test(expected = AssertionError.class)
    public void assertNotEqualsConsidersDeltaCorrectly()
    {
        Verify.assertNotEquals(1.0, 0.9, 0.1);
    }

    @Test(expected = AssertionError.class)
    public void assertNotEqualsConsidersFloatDeltaCorrectly()
    {
        Verify.assertNotEquals(1.0f, 0.75f, 0.25f);
    }

    @Test(expected = AssertionError.class)
    public void assertNotEqualsIgnoresDeltaOnNaN()
    {
        Verify.assertNotEquals(Double.NaN, Double.NaN, 1);
    }

    @Test(expected = AssertionError.class)
    public void assertNotEqualsIgnoresFloatDeltaOnNaN()
    {
        Verify.assertNotEquals(Float.NaN, Float.NaN, 1.0f);
    }

    @Test(expected = AssertionError.class)
    public void assertThrowsRequiresAnExceptionToBeThrown()
    {
        Verify.assertThrows(Throwable.class, nonThrowingRunnable());
    }

    @Test
    public void assertThrowsIncludesAnInformativeDefaultMessage()
    {
        try
        {
            Verify.assertThrows(Throwable.class, nonThrowingRunnable());
        }
        catch (AssertionError ex)
        {
            Verify.assertEquals("expected java.lang.Throwable to be thrown, but nothing was thrown", ex.getMessage());
            return;
        }
        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void assertThrowsIncludesTheSpecifiedMessage()
    {
        try
        {
            Verify.assertThrows("Foobar", Throwable.class, nonThrowingRunnable());
        }
        catch (AssertionError ex)
        {
            Verify.assertEquals(
                    "Foobar: expected java.lang.Throwable to be thrown, but nothing was thrown",
                    ex.getMessage());
            return;
        }
        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void assertThrowsReturnsTheSameObjectThrown()
    {
        NullPointerException npe = new NullPointerException();

        Throwable throwable = Verify.assertThrows(Throwable.class, throwingRunnable(npe));

        Verify.assertSame(npe, throwable);
    }

    @Test(expected = AssertionError.class)
    public void assertThrowsDetectsTypeMismatchesViaExplicitTypeHint()
    {
        NullPointerException npe = new NullPointerException();

        Verify.assertThrows(IOException.class, throwingRunnable(npe));
    }

    @Test
    public void assertThrowsWrapsAndPropagatesUnexpectedExceptions()
    {
        NullPointerException npe = new NullPointerException("inner-message");

        try
        {
            Verify.assertThrows(IOException.class, throwingRunnable(npe));
        }
        catch (AssertionError ex)
        {
            Verify.assertSame(npe, ex.getCause());
            Verify.assertEquals("inner-message", ex.getCause().getMessage());
            return;
        }
        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void assertThrowsSuppliesACoherentErrorMessageUponTypeMismatch()
    {
        NullPointerException npe = new NullPointerException();

        try
        {
            Verify.assertThrows(IOException.class, throwingRunnable(npe));
        }
        catch (AssertionError error)
        {
            Verify.assertEquals(
                    "unexpected exception type thrown; expected:<java.io.IOException> but was:<java.lang.NullPointerException>",
                    error.getMessage());
            Verify.assertSame(npe, error.getCause());
            return;
        }
        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void assertThrowsSuppliesTheSpecifiedMessageUponTypeMismatch()
    {
        NullPointerException npe = new NullPointerException();

        try
        {
            Verify.assertThrows("Foobar", IOException.class, throwingRunnable(npe));
        }
        catch (AssertionError error)
        {
            Verify.assertEquals(
                    "Foobar: unexpected exception type thrown; expected:<java.io.IOException> but was:<java.lang.NullPointerException>",
                    error.getMessage());
            Verify.assertSame(npe, error.getCause());
            return;
        }
        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void assertThrowsUsesCanonicalNameUponTypeMismatch()
    {
        NullPointerException npe = new NullPointerException();

        try
        {
            Verify.assertThrows(NestedException.class, throwingRunnable(npe));
        }
        catch (AssertionError error)
        {
            Verify.assertEquals(
                    "unexpected exception type thrown; expected:<org.eclipse.collections.impl.test.AssertTest.NestedException>"
                            + " but was:<java.lang.NullPointerException>",
                    error.getMessage());
            Verify.assertSame(npe, error.getCause());
            return;
        }
        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void assertThrowsUsesNameUponTypeMismatchWithAnonymousClass()
    {
        NullPointerException npe = new NullPointerException()
        {
        };

        try
        {
            Verify.assertThrows(IOException.class, throwingRunnable(npe));
        }
        catch (AssertionError error)
        {
            Verify.assertEquals(
                    "unexpected exception type thrown; expected:<java.io.IOException>"
                            + " but was:<org.eclipse.collections.impl.test.AssertTest$1>",
                    error.getMessage());
            Verify.assertSame(npe, error.getCause());
            return;
        }
        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void assertThrowsUsesCanonicalNameWhenRequiredExceptionNotThrown()
    {
        try
        {
            Verify.assertThrows(NestedException.class, nonThrowingRunnable());
        }
        catch (AssertionError error)
        {
            Verify.assertEquals(
                    "expected org.eclipse.collections.impl.test.AssertTest.NestedException to be thrown,"
                            + " but nothing was thrown", error.getMessage());
            return;
        }
        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    private static class NestedException extends RuntimeException
    {
        private static final long serialVersionUID = 1L;
    }

    private static ThrowingRunnable nonThrowingRunnable()
    {
        return new ThrowingRunnable()
        {
            public void run()
            {
            }
        };
    }

    private static ThrowingRunnable throwingRunnable(Throwable t)
    {
        return new ThrowingRunnable()
        {
            public void run() throws Throwable
            {
                throw t;
            }
        };
    }
}
