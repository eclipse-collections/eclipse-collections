/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl;

/**
 * SpreadFunctions provides static utility methods for spreading hash codes to improve distribution
 * in hash-based data structures. These spreading functions help reduce hash collisions by
 * thoroughly mixing the bits of input values.
 * <p>
 * The class contains two primary spreading algorithms for both 32-bit and 64-bit values,
 * along with specialized variants for primitive types (int, long, float, double, short, char).
 * These functions are used internally by Eclipse Collections to enhance the performance of
 * hash-based collections like sets and maps.
 * </p>
 * <p>
 * Thread Safety: All methods are static, stateless, and thread-safe.
 * </p>
 *
 * @since 1.0
 */
public final class SpreadFunctions
{
    private SpreadFunctions()
    {
    }

    private static int thirtyTwoBitSpread1(int code)
    {
        int code1 = code;
        code1 ^= code1 >>> 15;
        code1 *= 0xACAB2A4D;
        code1 ^= code1 >>> 15;
        code1 *= 0x5CC7DF53;
        code1 ^= code1 >>> 12;
        return code1;
    }

    private static int thirtyTwoBitSpread2(int code)
    {
        int code1 = code;
        code1 ^= code1 >>> 14;
        code1 *= 0xBA1CCD33;
        code1 ^= code1 >>> 13;
        code1 *= 0x9B6296CB;
        code1 ^= code1 >>> 12;
        return code1;
    }

    private static long sixtyFourBitSpread1(long code)
    {
        long code1 = code;
        code1 ^= code1 >>> 28;
        code1 *= -4254747342703917655L;
        code1 ^= code1 >>> 43;
        code1 *= -908430792394475837L;
        code1 ^= code1 >>> 23;
        return code1;
    }

    private static long sixtyFourBitSpread2(long code)
    {
        long code1 = code;
        code1 ^= code1 >>> 23;
        code1 *= -6261870919139520145L;
        code1 ^= code1 >>> 39;
        code1 *= 2747051607443084853L;
        code1 ^= code1 >>> 37;
        return code1;
    }

    /**
     * Applies the first 64-bit spreading function to a double value.
     * <p>
     * This method converts the double to its bit representation and applies a spreading
     * function to improve hash distribution. Use this for the primary hash computation
     * in double-keyed hash structures.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * double value = 3.14159;
     * long spread = SpreadFunctions.doubleSpreadOne(value);
     * int bucket = (int) (spread % bucketCount);
     * }</pre>
     *
     * @param element the double value to spread
     * @return the spread hash code as a long
     */
    public static long doubleSpreadOne(double element)
    {
        long code = Double.doubleToLongBits(element);
        return SpreadFunctions.sixtyFourBitSpread1(code);
    }

    /**
     * Applies the second 64-bit spreading function to a double value.
     * <p>
     * This method provides an alternative spreading algorithm for double values,
     * useful for secondary hashing or double hashing schemes.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * double value = 3.14159;
     * long spread = SpreadFunctions.doubleSpreadTwo(value);
     * int secondaryHash = (int) (spread % tableSize);
     * }</pre>
     *
     * @param element the double value to spread
     * @return the spread hash code as a long
     */
    public static long doubleSpreadTwo(double element)
    {
        long code = Double.doubleToLongBits(element);
        return SpreadFunctions.sixtyFourBitSpread2(code);
    }

    /**
     * Applies the first 64-bit spreading function to a long value.
     * <p>
     * This spreading function improves the distribution of long hash codes,
     * particularly useful for long-keyed hash structures.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * long value = 123456789L;
     * long spread = SpreadFunctions.longSpreadOne(value);
     * int bucket = (int) (spread % bucketCount);
     * }</pre>
     *
     * @param element the long value to spread
     * @return the spread hash code as a long
     */
    public static long longSpreadOne(long element)
    {
        return SpreadFunctions.sixtyFourBitSpread1(element);
    }

    /**
     * Applies the second 64-bit spreading function to a long value.
     * <p>
     * This provides an alternative spreading algorithm for long values,
     * useful for secondary hashing strategies.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * long value = 987654321L;
     * long spread = SpreadFunctions.longSpreadTwo(value);
     * int secondaryHash = (int) (spread % tableSize);
     * }</pre>
     *
     * @param element the long value to spread
     * @return the spread hash code as a long
     */
    public static long longSpreadTwo(long element)
    {
        return SpreadFunctions.sixtyFourBitSpread2(element);
    }

    /**
     * Applies the first 32-bit spreading function to an int value.
     * <p>
     * This spreading function improves the distribution of int hash codes,
     * essential for int-keyed hash structures.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * int value = 42;
     * int spread = SpreadFunctions.intSpreadOne(value);
     * int bucket = spread % bucketCount;
     * }</pre>
     *
     * @param element the int value to spread
     * @return the spread hash code as an int
     */
    public static int intSpreadOne(int element)
    {
        return SpreadFunctions.thirtyTwoBitSpread1(element);
    }

    /**
     * Applies the second 32-bit spreading function to an int value.
     * <p>
     * This provides an alternative spreading algorithm for int values,
     * useful for secondary hashing or double hashing schemes.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * int value = 42;
     * int spread = SpreadFunctions.intSpreadTwo(value);
     * int secondaryHash = spread % tableSize;
     * }</pre>
     *
     * @param element the int value to spread
     * @return the spread hash code as an int
     */
    public static int intSpreadTwo(int element)
    {
        return SpreadFunctions.thirtyTwoBitSpread2(element);
    }

    /**
     * Applies the first 32-bit spreading function to a float value.
     * <p>
     * This method converts the float to its bit representation and applies a spreading
     * function to improve hash distribution for float-keyed structures.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * float value = 3.14f;
     * int spread = SpreadFunctions.floatSpreadOne(value);
     * int bucket = spread % bucketCount;
     * }</pre>
     *
     * @param element the float value to spread
     * @return the spread hash code as an int
     */
    public static int floatSpreadOne(float element)
    {
        int code = Float.floatToIntBits(element);
        return SpreadFunctions.thirtyTwoBitSpread1(code);
    }

    /**
     * Applies the second 32-bit spreading function to a float value.
     * <p>
     * This provides an alternative spreading algorithm for float values,
     * useful for secondary hashing strategies.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * float value = 2.71f;
     * int spread = SpreadFunctions.floatSpreadTwo(value);
     * int secondaryHash = spread % tableSize;
     * }</pre>
     *
     * @param element the float value to spread
     * @return the spread hash code as an int
     */
    public static int floatSpreadTwo(float element)
    {
        int code = Float.floatToIntBits(element);
        return SpreadFunctions.thirtyTwoBitSpread2(code);
    }

    /**
     * Applies the first 32-bit spreading function to a short value.
     * <p>
     * The short value is promoted to int before spreading. This improves
     * hash distribution for short-keyed structures.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * short value = 100;
     * int spread = SpreadFunctions.shortSpreadOne(value);
     * int bucket = spread % bucketCount;
     * }</pre>
     *
     * @param element the short value to spread
     * @return the spread hash code as an int
     */
    public static int shortSpreadOne(short element)
    {
        return SpreadFunctions.thirtyTwoBitSpread1(element);
    }

    /**
     * Applies the second 32-bit spreading function to a short value.
     * <p>
     * The short value is promoted to int before spreading. This provides
     * an alternative hashing strategy for short values.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * short value = 200;
     * int spread = SpreadFunctions.shortSpreadTwo(value);
     * int secondaryHash = spread % tableSize;
     * }</pre>
     *
     * @param element the short value to spread
     * @return the spread hash code as an int
     */
    public static int shortSpreadTwo(short element)
    {
        return SpreadFunctions.thirtyTwoBitSpread2(element);
    }

    /**
     * Applies the first 32-bit spreading function to a char value.
     * <p>
     * The char value is promoted to int before spreading. This improves
     * hash distribution for char-keyed structures.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * char value = 'A';
     * int spread = SpreadFunctions.charSpreadOne(value);
     * int bucket = spread % bucketCount;
     * }</pre>
     *
     * @param element the char value to spread
     * @return the spread hash code as an int
     */
    public static int charSpreadOne(char element)
    {
        return SpreadFunctions.thirtyTwoBitSpread1(element);
    }

    /**
     * Applies the second 32-bit spreading function to a char value.
     * <p>
     * The char value is promoted to int before spreading. This provides
     * an alternative hashing strategy for char values.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * char value = 'Z';
     * int spread = SpreadFunctions.charSpreadTwo(value);
     * int secondaryHash = spread % tableSize;
     * }</pre>
     *
     * @param element the char value to spread
     * @return the spread hash code as an int
     */
    public static int charSpreadTwo(char element)
    {
        return SpreadFunctions.thirtyTwoBitSpread2(element);
    }
}
