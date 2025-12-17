/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.block.factory;

import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.function.primitive.BooleanFunction;
import org.eclipse.collections.api.block.function.primitive.ByteFunction;
import org.eclipse.collections.api.block.function.primitive.CharFunction;
import org.eclipse.collections.api.block.function.primitive.DoubleFunction;
import org.eclipse.collections.api.block.function.primitive.FloatFunction;
import org.eclipse.collections.api.block.function.primitive.IntFunction;
import org.eclipse.collections.api.block.function.primitive.LongFunction;
import org.eclipse.collections.api.block.function.primitive.ShortFunction;
import org.eclipse.collections.api.factory.primitive.ObjectDoubleMaps;
import org.eclipse.collections.api.map.primitive.MutableObjectDoubleMap;
import org.eclipse.collections.api.map.primitive.MutableObjectLongMap;

public final class PrimitiveFunctions
{
    private static final IntegerIsPositive INTEGER_IS_POSITIVE = new IntegerIsPositive();
    private static final UnboxNumberToInt UNBOX_NUMBER_TO_INT = new UnboxNumberToInt();
    private static final UnboxIntegerToByte UNBOX_INTEGER_TO_BYTE = new UnboxIntegerToByte();
    private static final UnboxIntegerToChar UNBOX_INTEGER_TO_CHAR = new UnboxIntegerToChar();
    private static final UnboxIntegerToInt UNBOX_INTEGER_TO_INT = new UnboxIntegerToInt();
    private static final UnboxIntegerToFloat UNBOX_INTEGER_TO_FLOAT = new UnboxIntegerToFloat();
    private static final UnboxIntegerToLong UNBOX_INTEGER_TO_LONG = new UnboxIntegerToLong();
    private static final UnboxIntegerToShort UNBOX_INTEGER_TO_SHORT = new UnboxIntegerToShort();
    private static final UnboxIntegerToDouble UNBOX_INTEGER_TO_DOUBLE = new UnboxIntegerToDouble();
    private static final UnboxDoubleToDouble UNBOX_DOUBLE_TO_DOUBLE = new UnboxDoubleToDouble();
    private static final UnboxFloatToFloat UNBOX_FLOAT_TO_FLOAT = new UnboxFloatToFloat();
    private static final UnboxNumberToFloat UNBOX_NUMBER_TO_FLOAT = new UnboxNumberToFloat();
    private static final UnboxNumberToLong UNBOX_NUMBER_TO_LONG = new UnboxNumberToLong();
    private static final UnboxNumberToDouble UNBOX_NUMBER_TO_DOUBLE = new UnboxNumberToDouble();

    private PrimitiveFunctions()
    {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }

    public static BooleanFunction<Integer> integerIsPositive()
    {
        return INTEGER_IS_POSITIVE;
    }

    public static IntFunction<Number> unboxNumberToInt()
    {
        return UNBOX_NUMBER_TO_INT;
    }

    public static ByteFunction<Integer> unboxIntegerToByte()
    {
        return UNBOX_INTEGER_TO_BYTE;
    }

    public static CharFunction<Integer> unboxIntegerToChar()
    {
        return UNBOX_INTEGER_TO_CHAR;
    }

    public static IntFunction<Integer> unboxIntegerToInt()
    {
        return UNBOX_INTEGER_TO_INT;
    }

    public static FloatFunction<Number> unboxNumberToFloat()
    {
        return UNBOX_NUMBER_TO_FLOAT;
    }

    public static LongFunction<Number> unboxNumberToLong()
    {
        return UNBOX_NUMBER_TO_LONG;
    }

    public static DoubleFunction<Number> unboxNumberToDouble()
    {
        return UNBOX_NUMBER_TO_DOUBLE;
    }

    public static FloatFunction<Integer> unboxIntegerToFloat()
    {
        return UNBOX_INTEGER_TO_FLOAT;
    }

    public static LongFunction<Integer> unboxIntegerToLong()
    {
        return UNBOX_INTEGER_TO_LONG;
    }

    public static ShortFunction<Integer> unboxIntegerToShort()
    {
        return UNBOX_INTEGER_TO_SHORT;
    }

    public static DoubleFunction<Integer> unboxIntegerToDouble()
    {
        return UNBOX_INTEGER_TO_DOUBLE;
    }

    public static DoubleFunction<Double> unboxDoubleToDouble()
    {
        return UNBOX_DOUBLE_TO_DOUBLE;
    }

    public static FloatFunction<Float> unboxFloatToFloat()
    {
        return UNBOX_FLOAT_TO_FLOAT;
    }

    public static <T, V> Function2<MutableObjectLongMap<V>, T, MutableObjectLongMap<V>> sumByIntFunction(Function<? super T, ? extends V> groupBy, IntFunction<? super T> function)
    {
        return new Function2<MutableObjectLongMap<V>, T, MutableObjectLongMap<V>>()
        {
            private static final long serialVersionUID = 1L;

            @Override
            public MutableObjectLongMap<V> value(MutableObjectLongMap<V> map, T each)
            {
                map.addToValue(groupBy.valueOf(each), function.intValueOf(each));
                return map;
            }
        };
    }

    public static <T, V> Function2<MutableObjectDoubleMap<V>, T, MutableObjectDoubleMap<V>> sumByFloatFunction(Function<? super T, ? extends V> groupBy, FloatFunction<? super T> function)
    {
        return new Function2<MutableObjectDoubleMap<V>, T, MutableObjectDoubleMap<V>>()
        {
            private static final long serialVersionUID = 1L;
            private final MutableObjectDoubleMap<V> compensation = ObjectDoubleMaps.mutable.empty();

            @Override
            public MutableObjectDoubleMap<V> value(MutableObjectDoubleMap<V> map, T each)
            {
                V groupKey = groupBy.valueOf(each);
                double compensation = this.compensation.getIfAbsent(groupKey, 0.0d);
                double adjustedValue = function.floatValueOf(each) - compensation;
                double currentSum = map.get(groupKey);
                double nextSum = currentSum + adjustedValue;
                this.compensation.put(groupKey, nextSum - currentSum - adjustedValue);
                map.put(groupKey, nextSum);
                return map;
            }
        };
    }

    public static <T, V> Function2<MutableObjectLongMap<V>, T, MutableObjectLongMap<V>> sumByLongFunction(Function<? super T, ? extends V> groupBy, LongFunction<? super T> function)
    {
        return new Function2<MutableObjectLongMap<V>, T, MutableObjectLongMap<V>>()
        {
            private static final long serialVersionUID = 1L;

            @Override
            public MutableObjectLongMap<V> value(MutableObjectLongMap<V> map, T each)
            {
                map.addToValue(groupBy.valueOf(each), function.longValueOf(each));
                return map;
            }
        };
    }

    public static <T, V> Function2<MutableObjectDoubleMap<V>, T, MutableObjectDoubleMap<V>> sumByDoubleFunction(Function<? super T, ? extends V> groupBy, DoubleFunction<? super T> function)
    {
        return new Function2<MutableObjectDoubleMap<V>, T, MutableObjectDoubleMap<V>>()
        {
            private static final long serialVersionUID = 1L;
            private final MutableObjectDoubleMap<V> compensation = ObjectDoubleMaps.mutable.empty();

            @Override
            public MutableObjectDoubleMap<V> value(MutableObjectDoubleMap<V> map, T each)
            {
                V groupKey = groupBy.valueOf(each);
                double compensation = this.compensation.getIfAbsent(groupKey, 0.0d);
                double adjustedValue = function.doubleValueOf(each) - compensation;
                double currentSum = map.get(groupKey);
                double nextSum = currentSum + adjustedValue;
                this.compensation.put(groupKey, nextSum - currentSum - adjustedValue);
                map.put(groupKey, nextSum);
                return map;
            }
        };
    }

    private static class IntegerIsPositive
            implements BooleanFunction<Integer>
    {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean booleanValueOf(Integer integer)
        {
            return integer.intValue() > 0;
        }
    }

    private static class UnboxNumberToInt
            implements IntFunction<Number>
    {
        private static final long serialVersionUID = 1L;

        @Override
        public int intValueOf(Number number)
        {
            return number.intValue();
        }
    }

    private static class UnboxIntegerToByte
            implements ByteFunction<Integer>
    {
        private static final long serialVersionUID = 1L;

        @Override
        public byte byteValueOf(Integer integer)
        {
            return integer.byteValue();
        }
    }

    private static class UnboxIntegerToChar
            implements CharFunction<Integer>
    {
        private static final long serialVersionUID = 1L;

        @Override
        public char charValueOf(Integer integer)
        {
            return (char) integer.intValue();
        }
    }

    private static class UnboxIntegerToInt
            implements IntFunction<Integer>
    {
        private static final long serialVersionUID = 1L;

        @Override
        public int intValueOf(Integer integer)
        {
            return integer;
        }
    }

    private static class UnboxIntegerToFloat
            implements FloatFunction<Integer>
    {
        private static final long serialVersionUID = 1L;

        @Override
        public float floatValueOf(Integer integer)
        {
            return integer;
        }
    }

    private static class UnboxIntegerToLong
            implements LongFunction<Integer>
    {
        private static final long serialVersionUID = 1L;

        @Override
        public long longValueOf(Integer integer)
        {
            return integer;
        }
    }

    private static class UnboxIntegerToShort
            implements ShortFunction<Integer>
    {
        private static final long serialVersionUID = 1L;

        @Override
        public short shortValueOf(Integer integer)
        {
            return integer.shortValue();
        }
    }

    private static class UnboxIntegerToDouble
            implements DoubleFunction<Integer>
    {
        private static final long serialVersionUID = 1L;

        @Override
        public double doubleValueOf(Integer integer)
        {
            return integer;
        }
    }

    private static class UnboxDoubleToDouble
            implements DoubleFunction<Double>
    {
        private static final long serialVersionUID = 1L;

        @Override
        public double doubleValueOf(Double aDouble)
        {
            return aDouble;
        }
    }

    private static class UnboxFloatToFloat implements FloatFunction<Float>
    {
        private static final long serialVersionUID = 1L;

        @Override
        public float floatValueOf(Float aFloat)
        {
            return aFloat;
        }
    }

    private static class UnboxNumberToFloat
            implements FloatFunction<Number>
    {
        private static final long serialVersionUID = 1L;

        @Override
        public float floatValueOf(Number number)
        {
            return number.floatValue();
        }
    }

    private static class UnboxNumberToLong
            implements LongFunction<Number>
    {
        private static final long serialVersionUID = 1L;

        @Override
        public long longValueOf(Number number)
        {
            return number.longValue();
        }
    }

    private static class UnboxNumberToDouble
            implements DoubleFunction<Number>
    {
        private static final long serialVersionUID = 1L;

        @Override
        public double doubleValueOf(Number number)
        {
            return number.doubleValue();
        }
    }
}
