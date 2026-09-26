/*
 * Copyright (c) 2026 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api.factory.primitive;

import org.eclipse.collections.api.factory.ServiceLoaderUtils;
import org.eclipse.collections.api.tuple.primitive.BooleanBooleanPair;
import org.eclipse.collections.api.tuple.primitive.BooleanBytePair;
import org.eclipse.collections.api.tuple.primitive.BooleanCharPair;
import org.eclipse.collections.api.tuple.primitive.BooleanDoublePair;
import org.eclipse.collections.api.tuple.primitive.BooleanFloatPair;
import org.eclipse.collections.api.tuple.primitive.BooleanIntPair;
import org.eclipse.collections.api.tuple.primitive.BooleanLongPair;
import org.eclipse.collections.api.tuple.primitive.BooleanObjectPair;
import org.eclipse.collections.api.tuple.primitive.BooleanShortPair;
import org.eclipse.collections.api.tuple.primitive.ByteBooleanPair;
import org.eclipse.collections.api.tuple.primitive.ByteBytePair;
import org.eclipse.collections.api.tuple.primitive.ByteCharPair;
import org.eclipse.collections.api.tuple.primitive.ByteDoublePair;
import org.eclipse.collections.api.tuple.primitive.ByteFloatPair;
import org.eclipse.collections.api.tuple.primitive.ByteIntPair;
import org.eclipse.collections.api.tuple.primitive.ByteLongPair;
import org.eclipse.collections.api.tuple.primitive.ByteObjectPair;
import org.eclipse.collections.api.tuple.primitive.ByteShortPair;
import org.eclipse.collections.api.tuple.primitive.CharBooleanPair;
import org.eclipse.collections.api.tuple.primitive.CharBytePair;
import org.eclipse.collections.api.tuple.primitive.CharCharPair;
import org.eclipse.collections.api.tuple.primitive.CharDoublePair;
import org.eclipse.collections.api.tuple.primitive.CharFloatPair;
import org.eclipse.collections.api.tuple.primitive.CharIntPair;
import org.eclipse.collections.api.tuple.primitive.CharLongPair;
import org.eclipse.collections.api.tuple.primitive.CharObjectPair;
import org.eclipse.collections.api.tuple.primitive.CharShortPair;
import org.eclipse.collections.api.tuple.primitive.DoubleBooleanPair;
import org.eclipse.collections.api.tuple.primitive.DoubleBytePair;
import org.eclipse.collections.api.tuple.primitive.DoubleCharPair;
import org.eclipse.collections.api.tuple.primitive.DoubleDoublePair;
import org.eclipse.collections.api.tuple.primitive.DoubleFloatPair;
import org.eclipse.collections.api.tuple.primitive.DoubleIntPair;
import org.eclipse.collections.api.tuple.primitive.DoubleLongPair;
import org.eclipse.collections.api.tuple.primitive.DoubleObjectPair;
import org.eclipse.collections.api.tuple.primitive.DoubleShortPair;
import org.eclipse.collections.api.tuple.primitive.FloatBooleanPair;
import org.eclipse.collections.api.tuple.primitive.FloatBytePair;
import org.eclipse.collections.api.tuple.primitive.FloatCharPair;
import org.eclipse.collections.api.tuple.primitive.FloatDoublePair;
import org.eclipse.collections.api.tuple.primitive.FloatFloatPair;
import org.eclipse.collections.api.tuple.primitive.FloatIntPair;
import org.eclipse.collections.api.tuple.primitive.FloatLongPair;
import org.eclipse.collections.api.tuple.primitive.FloatObjectPair;
import org.eclipse.collections.api.tuple.primitive.FloatShortPair;
import org.eclipse.collections.api.tuple.primitive.IntBooleanPair;
import org.eclipse.collections.api.tuple.primitive.IntBytePair;
import org.eclipse.collections.api.tuple.primitive.IntCharPair;
import org.eclipse.collections.api.tuple.primitive.IntDoublePair;
import org.eclipse.collections.api.tuple.primitive.IntFloatPair;
import org.eclipse.collections.api.tuple.primitive.IntIntPair;
import org.eclipse.collections.api.tuple.primitive.IntLongPair;
import org.eclipse.collections.api.tuple.primitive.IntObjectPair;
import org.eclipse.collections.api.tuple.primitive.IntShortPair;
import org.eclipse.collections.api.tuple.primitive.LongBooleanPair;
import org.eclipse.collections.api.tuple.primitive.LongBytePair;
import org.eclipse.collections.api.tuple.primitive.LongCharPair;
import org.eclipse.collections.api.tuple.primitive.LongDoublePair;
import org.eclipse.collections.api.tuple.primitive.LongFloatPair;
import org.eclipse.collections.api.tuple.primitive.LongIntPair;
import org.eclipse.collections.api.tuple.primitive.LongLongPair;
import org.eclipse.collections.api.tuple.primitive.LongObjectPair;
import org.eclipse.collections.api.tuple.primitive.LongShortPair;
import org.eclipse.collections.api.tuple.primitive.ObjectBooleanPair;
import org.eclipse.collections.api.tuple.primitive.ObjectBytePair;
import org.eclipse.collections.api.tuple.primitive.ObjectCharPair;
import org.eclipse.collections.api.tuple.primitive.ObjectDoublePair;
import org.eclipse.collections.api.tuple.primitive.ObjectFloatPair;
import org.eclipse.collections.api.tuple.primitive.ObjectIntPair;
import org.eclipse.collections.api.tuple.primitive.ObjectLongPair;
import org.eclipse.collections.api.tuple.primitive.ObjectShortPair;
import org.eclipse.collections.api.tuple.primitive.ShortBooleanPair;
import org.eclipse.collections.api.tuple.primitive.ShortBytePair;
import org.eclipse.collections.api.tuple.primitive.ShortCharPair;
import org.eclipse.collections.api.tuple.primitive.ShortDoublePair;
import org.eclipse.collections.api.tuple.primitive.ShortFloatPair;
import org.eclipse.collections.api.tuple.primitive.ShortIntPair;
import org.eclipse.collections.api.tuple.primitive.ShortLongPair;
import org.eclipse.collections.api.tuple.primitive.ShortObjectPair;
import org.eclipse.collections.api.tuple.primitive.ShortShortPair;

/**
 * Public factory for creating primitive/object and primitive/primitive pairs.
 * <p>
 * This class belongs in the API module and delegates creation to an implementation provider.
 */
public final class PrimitiveTuples
{
    private static volatile PrimitiveTupleFactoryProvider provider;

    private PrimitiveTuples()
    {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }

    public static void initialize(PrimitiveTupleFactoryProvider primitiveTupleFactoryProvider)
    {
        if (primitiveTupleFactoryProvider == null)
        {
            throw new NullPointerException("primitiveTupleFactoryProvider");
        }
        PrimitiveTuples.provider = primitiveTupleFactoryProvider;
    }

    private static PrimitiveTupleFactoryProvider provider()
    {
        PrimitiveTupleFactoryProvider result = PrimitiveTuples.provider;
        if (result == null)
        {
            result = ServiceLoaderUtils.loadServiceClass(PrimitiveTupleFactoryProvider.class);
            PrimitiveTuples.provider = result;
        }
        return result;
    }

    public static <T> ObjectBooleanPair<T> pair(T one, boolean two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static <T> ObjectBytePair<T> pair(T one, byte two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static <T> ObjectCharPair<T> pair(T one, char two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static <T> ObjectShortPair<T> pair(T one, short two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static <T> ObjectIntPair<T> pair(T one, int two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static <T> ObjectFloatPair<T> pair(T one, float two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static <T> ObjectLongPair<T> pair(T one, long two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static <T> ObjectDoublePair<T> pair(T one, double two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static <T> BooleanObjectPair<T> pair(boolean one, T two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static <T> ByteObjectPair<T> pair(byte one, T two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static <T> CharObjectPair<T> pair(char one, T two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static <T> ShortObjectPair<T> pair(short one, T two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static <T> IntObjectPair<T> pair(int one, T two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static <T> FloatObjectPair<T> pair(float one, T two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static <T> LongObjectPair<T> pair(long one, T two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static <T> DoubleObjectPair<T> pair(double one, T two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static IntIntPair pair(int one, int two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static IntFloatPair pair(int one, float two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static IntDoublePair pair(int one, double two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static IntLongPair pair(int one, long two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static IntShortPair pair(int one, short two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static IntBytePair pair(int one, byte two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static IntCharPair pair(int one, char two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static IntBooleanPair pair(int one, boolean two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static FloatIntPair pair(float one, int two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static FloatFloatPair pair(float one, float two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static FloatDoublePair pair(float one, double two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static FloatLongPair pair(float one, long two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static FloatShortPair pair(float one, short two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static FloatBytePair pair(float one, byte two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static FloatCharPair pair(float one, char two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static FloatBooleanPair pair(float one, boolean two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static DoubleIntPair pair(double one, int two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static DoubleFloatPair pair(double one, float two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static DoubleDoublePair pair(double one, double two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static DoubleLongPair pair(double one, long two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static DoubleShortPair pair(double one, short two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static DoubleBytePair pair(double one, byte two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static DoubleCharPair pair(double one, char two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static DoubleBooleanPair pair(double one, boolean two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static LongIntPair pair(long one, int two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static LongFloatPair pair(long one, float two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static LongDoublePair pair(long one, double two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static LongLongPair pair(long one, long two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static LongShortPair pair(long one, short two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static LongBytePair pair(long one, byte two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static LongCharPair pair(long one, char two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static LongBooleanPair pair(long one, boolean two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static ShortIntPair pair(short one, int two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static ShortFloatPair pair(short one, float two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static ShortDoublePair pair(short one, double two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static ShortLongPair pair(short one, long two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static ShortShortPair pair(short one, short two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static ShortBytePair pair(short one, byte two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static ShortCharPair pair(short one, char two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static ShortBooleanPair pair(short one, boolean two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static ByteIntPair pair(byte one, int two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static ByteFloatPair pair(byte one, float two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static ByteDoublePair pair(byte one, double two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static ByteLongPair pair(byte one, long two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static ByteShortPair pair(byte one, short two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static ByteBytePair pair(byte one, byte two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static ByteCharPair pair(byte one, char two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static ByteBooleanPair pair(byte one, boolean two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static CharIntPair pair(char one, int two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static CharFloatPair pair(char one, float two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static CharDoublePair pair(char one, double two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static CharLongPair pair(char one, long two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static CharShortPair pair(char one, short two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static CharBytePair pair(char one, byte two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static CharCharPair pair(char one, char two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static CharBooleanPair pair(char one, boolean two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static BooleanIntPair pair(boolean one, int two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static BooleanFloatPair pair(boolean one, float two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static BooleanDoublePair pair(boolean one, double two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static BooleanLongPair pair(boolean one, long two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static BooleanShortPair pair(boolean one, short two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static BooleanBytePair pair(boolean one, byte two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static BooleanCharPair pair(boolean one, char two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }

    public static BooleanBooleanPair pair(boolean one, boolean two)
    {
        return PrimitiveTuples.provider().pair(one, two);
    }
}
