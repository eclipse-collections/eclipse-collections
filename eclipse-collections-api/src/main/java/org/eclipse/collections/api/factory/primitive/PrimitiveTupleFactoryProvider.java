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
 * SPI bridge for {@link PrimitiveTuples} to avoid API-to-impl compile-time
 * dependencies.
 */
public interface PrimitiveTupleFactoryProvider {
    <T> ObjectBooleanPair<T> pair(T one, boolean two);

    <T> ObjectBytePair<T> pair(T one, byte two);

    <T> ObjectCharPair<T> pair(T one, char two);

    <T> ObjectShortPair<T> pair(T one, short two);

    <T> ObjectIntPair<T> pair(T one, int two);

    <T> ObjectFloatPair<T> pair(T one, float two);

    <T> ObjectLongPair<T> pair(T one, long two);

    <T> ObjectDoublePair<T> pair(T one, double two);

    <T> BooleanObjectPair<T> pair(boolean one, T two);

    <T> ByteObjectPair<T> pair(byte one, T two);

    <T> CharObjectPair<T> pair(char one, T two);

    <T> ShortObjectPair<T> pair(short one, T two);

    <T> IntObjectPair<T> pair(int one, T two);

    <T> FloatObjectPair<T> pair(float one, T two);

    <T> LongObjectPair<T> pair(long one, T two);

    <T> DoubleObjectPair<T> pair(double one, T two);

    IntIntPair pair(int one, int two);

    IntFloatPair pair(int one, float two);

    IntDoublePair pair(int one, double two);

    IntLongPair pair(int one, long two);

    IntShortPair pair(int one, short two);

    IntBytePair pair(int one, byte two);

    IntCharPair pair(int one, char two);

    IntBooleanPair pair(int one, boolean two);

    FloatIntPair pair(float one, int two);

    FloatFloatPair pair(float one, float two);

    FloatDoublePair pair(float one, double two);

    FloatLongPair pair(float one, long two);

    FloatShortPair pair(float one, short two);

    FloatBytePair pair(float one, byte two);

    FloatCharPair pair(float one, char two);

    FloatBooleanPair pair(float one, boolean two);

    DoubleIntPair pair(double one, int two);

    DoubleFloatPair pair(double one, float two);

    DoubleDoublePair pair(double one, double two);

    DoubleLongPair pair(double one, long two);

    DoubleShortPair pair(double one, short two);

    DoubleBytePair pair(double one, byte two);

    DoubleCharPair pair(double one, char two);

    DoubleBooleanPair pair(double one, boolean two);

    LongIntPair pair(long one, int two);

    LongFloatPair pair(long one, float two);

    LongDoublePair pair(long one, double two);

    LongLongPair pair(long one, long two);

    LongShortPair pair(long one, short two);

    LongBytePair pair(long one, byte two);

    LongCharPair pair(long one, char two);

    LongBooleanPair pair(long one, boolean two);

    ShortIntPair pair(short one, int two);

    ShortFloatPair pair(short one, float two);

    ShortDoublePair pair(short one, double two);

    ShortLongPair pair(short one, long two);

    ShortShortPair pair(short one, short two);

    ShortBytePair pair(short one, byte two);

    ShortCharPair pair(short one, char two);

    ShortBooleanPair pair(short one, boolean two);

    ByteIntPair pair(byte one, int two);

    ByteFloatPair pair(byte one, float two);

    ByteDoublePair pair(byte one, double two);

    ByteLongPair pair(byte one, long two);

    ByteShortPair pair(byte one, short two);

    ByteBytePair pair(byte one, byte two);

    ByteCharPair pair(byte one, char two);

    ByteBooleanPair pair(byte one, boolean two);

    CharIntPair pair(char one, int two);

    CharFloatPair pair(char one, float two);

    CharDoublePair pair(char one, double two);

    CharLongPair pair(char one, long two);

    CharShortPair pair(char one, short two);

    CharBytePair pair(char one, byte two);

    CharCharPair pair(char one, char two);

    CharBooleanPair pair(char one, boolean two);

    BooleanIntPair pair(boolean one, int two);

    BooleanFloatPair pair(boolean one, float two);

    BooleanDoublePair pair(boolean one, double two);

    BooleanLongPair pair(boolean one, long two);

    BooleanShortPair pair(boolean one, short two);

    BooleanBytePair pair(boolean one, byte two);

    BooleanCharPair pair(boolean one, char two);

    BooleanBooleanPair pair(boolean one, boolean two);
}
