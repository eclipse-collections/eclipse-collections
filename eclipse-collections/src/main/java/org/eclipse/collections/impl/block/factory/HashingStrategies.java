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

import java.util.Objects;

import org.eclipse.collections.api.block.HashingStrategy;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.primitive.BooleanFunction;
import org.eclipse.collections.api.block.function.primitive.ByteFunction;
import org.eclipse.collections.api.block.function.primitive.CharFunction;
import org.eclipse.collections.api.block.function.primitive.DoubleFunction;
import org.eclipse.collections.api.block.function.primitive.FloatFunction;
import org.eclipse.collections.api.block.function.primitive.IntFunction;
import org.eclipse.collections.api.block.function.primitive.LongFunction;
import org.eclipse.collections.api.block.function.primitive.ShortFunction;

public final class HashingStrategies
{
    private static final HashingStrategy<Object> DEFAULT_HASHING_STRATEGY = new DefaultStrategy();
    private static final HashingStrategy<Object> IDENTITY_HASHING_STRATEGY = new IdentityHashingStrategy();

    private HashingStrategies()
    {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }

    public static <T> HashingStrategy<T> defaultStrategy()
    {
        return (HashingStrategy<T>) DEFAULT_HASHING_STRATEGY;
    }

    public static <T> HashingStrategy<T> nullSafeHashingStrategy(HashingStrategy<T> nonNullSafeStrategy)
    {
        return new NullSafeHashingStrategy<>(nonNullSafeStrategy);
    }

    public static <T, V> HashingStrategy<T> nullSafeFromFunction(Function<? super T, ? extends V> function)
    {
        return new NullSafeFunctionHashingStrategy<>(function);
    }

    public static <T, V> HashingStrategy<T> fromFunction(Function<? super T, ? extends V> function)
    {
        return new FunctionHashingStrategy<>(function);
    }

    public static HashingStrategy<Object> identityStrategy()
    {
        return IDENTITY_HASHING_STRATEGY;
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <T> HashingStrategy<T> chain(HashingStrategy<T>... hashingStrategies)
    {
        if (hashingStrategies.length == 0)
        {
            throw new IllegalArgumentException("Nothing to chain");
        }

        return new ChainedHashingStrategy<>(hashingStrategies);
    }

    public static <T, V1, V2> HashingStrategy<T> fromFunctions(Function<? super T, ? extends V1> one, Function<? super T, ? extends V2> two)
    {
        return HashingStrategies.chain(
                HashingStrategies.fromFunction(one),
                HashingStrategies.fromFunction(two));
    }

    public static <T, V1, V2, V3> HashingStrategy<T> fromFunctions(Function<? super T, ? extends V1> one, Function<? super T, ? extends V2> two, Function<? super T, ? extends V3> three)
    {
        return HashingStrategies.chain(
                HashingStrategies.fromFunction(one),
                HashingStrategies.fromFunction(two),
                HashingStrategies.fromFunction(three));
    }

    public static <T> HashingStrategy<T> fromBooleanFunction(BooleanFunction<? super T> function)
    {
        return new BooleanFunctionHashingStrategy<>(function);
    }

    public static <T> HashingStrategy<T> fromByteFunction(ByteFunction<? super T> function)
    {
        return new ByteFunctionHashingStrategy<>(function);
    }

    public static <T> HashingStrategy<T> fromCharFunction(CharFunction<? super T> function)
    {
        return new CharFunctionHashingStrategy<>(function);
    }

    public static <T> HashingStrategy<T> fromDoubleFunction(DoubleFunction<? super T> function)
    {
        return new DoubleFunctionHashingStrategy<>(function);
    }

    public static <T> HashingStrategy<T> fromFloatFunction(FloatFunction<? super T> function)
    {
        return new FloatFunctionHashingStrategy<>(function);
    }

    public static <T> HashingStrategy<T> fromIntFunction(IntFunction<? super T> function)
    {
        return new IntFunctionHashingStrategy<>(function);
    }

    public static <T> HashingStrategy<T> fromLongFunction(LongFunction<? super T> function)
    {
        return new LongFunctionHashingStrategy<>(function);
    }

    public static <T> HashingStrategy<T> fromShortFunction(ShortFunction<? super T> function)
    {
        return new ShortFunctionHashingStrategy<>(function);
    }

    private static class DefaultStrategy implements HashingStrategy<Object>
    {
        private static final long serialVersionUID = 1L;

        @Override
        public int computeHashCode(Object object)
        {
            return object.hashCode();
        }

        @Override
        public boolean equals(Object object1, Object object2)
        {
            return object1.equals(object2);
        }
    }

    private static final class NullSafeHashingStrategy<T> implements HashingStrategy<T>
    {
        private static final long serialVersionUID = 1L;

        private final HashingStrategy<T> nonNullSafeStrategy;

        private NullSafeHashingStrategy(HashingStrategy<T> nonNullSafeStrategy)
        {
            this.nonNullSafeStrategy = nonNullSafeStrategy;
        }

        @Override
        public int computeHashCode(T object)
        {
            return object == null ? 0 : this.nonNullSafeStrategy.computeHashCode(object);
        }

        @Override
        public boolean equals(T object1, T object2)
        {
            return object1 == null || object2 == null
                    ? object1 == object2
                    : this.nonNullSafeStrategy.equals(object1, object2);
        }
    }

    private static final class NullSafeFunctionHashingStrategy<T, V> implements HashingStrategy<T>
    {
        private static final long serialVersionUID = 1L;

        private final Function<? super T, ? extends V> function;

        private NullSafeFunctionHashingStrategy(Function<? super T, ? extends V> function)
        {
            this.function = function;
        }

        @Override
        public int computeHashCode(T object)
        {
            return Objects.hashCode(this.function.valueOf(object));
        }

        @Override
        public boolean equals(T object1, T object2)
        {
            return Objects.equals(
                    this.function.valueOf(object1),
                    this.function.valueOf(object2));
        }
    }

    private static final class FunctionHashingStrategy<T, V> implements HashingStrategy<T>
    {
        private static final long serialVersionUID = 1L;

        private final Function<? super T, ? extends V> function;

        private FunctionHashingStrategy(Function<? super T, ? extends V> function)
        {
            this.function = function;
        }

        @Override
        public int computeHashCode(T object)
        {
            return this.function.valueOf(object).hashCode();
        }

        @Override
        public boolean equals(T object1, T object2)
        {
            return this.function.valueOf(object1).equals(this.function.valueOf(object2));
        }
    }

    private static final class BooleanFunctionHashingStrategy<T> implements HashingStrategy<T>
    {
        private static final long serialVersionUID = 1L;

        private final BooleanFunction<? super T> function;

        private BooleanFunctionHashingStrategy(BooleanFunction<? super T> function)
        {
            this.function = function;
        }

        @Override
        public int computeHashCode(T object)
        {
            return this.function.booleanValueOf(object) ? Boolean.TRUE.hashCode() : Boolean.FALSE.hashCode();
        }

        @Override
        public boolean equals(T object1, T object2)
        {
            return this.function.booleanValueOf(object1) == this.function.booleanValueOf(object2);
        }
    }

    private static final class ByteFunctionHashingStrategy<T> implements HashingStrategy<T>
    {
        private static final long serialVersionUID = 1L;

        private final ByteFunction<? super T> function;

        private ByteFunctionHashingStrategy(ByteFunction<? super T> function)
        {
            this.function = function;
        }

        @Override
        public int computeHashCode(T object)
        {
            return this.function.byteValueOf(object);
        }

        @Override
        public boolean equals(T object1, T object2)
        {
            return this.function.byteValueOf(object1) == this.function.byteValueOf(object2);
        }
    }

    private static final class CharFunctionHashingStrategy<T> implements HashingStrategy<T>
    {
        private static final long serialVersionUID = 1L;

        private final CharFunction<? super T> function;

        private CharFunctionHashingStrategy(CharFunction<? super T> function)
        {
            this.function = function;
        }

        @Override
        public int computeHashCode(T object)
        {
            return this.function.charValueOf(object);
        }

        @Override
        public boolean equals(T object1, T object2)
        {
            return this.function.charValueOf(object1) == this.function.charValueOf(object2);
        }
    }

    private static final class DoubleFunctionHashingStrategy<T> implements HashingStrategy<T>
    {
        private static final long serialVersionUID = 1L;

        private final DoubleFunction<? super T> function;

        private DoubleFunctionHashingStrategy(DoubleFunction<? super T> function)
        {
            this.function = function;
        }

        @Override
        public int computeHashCode(T object)
        {
            return Long.hashCode(Double.doubleToLongBits(this.function.doubleValueOf(object)));
        }

        @Override
        public boolean equals(T object1, T object2)
        {
            return Double.compare(this.function.doubleValueOf(object1), this.function.doubleValueOf(object2)) == 0;
        }
    }

    private static final class FloatFunctionHashingStrategy<T> implements HashingStrategy<T>
    {
        private static final long serialVersionUID = 1L;

        private final FloatFunction<? super T> function;

        private FloatFunctionHashingStrategy(FloatFunction<? super T> function)
        {
            this.function = function;
        }

        @Override
        public int computeHashCode(T object)
        {
            return Float.floatToIntBits(this.function.floatValueOf(object));
        }

        @Override
        public boolean equals(T object1, T object2)
        {
            return Float.compare(this.function.floatValueOf(object1), this.function.floatValueOf(object2)) == 0;
        }
    }

    private static final class IntFunctionHashingStrategy<T> implements HashingStrategy<T>
    {
        private static final long serialVersionUID = 1L;

        private final IntFunction<? super T> function;

        private IntFunctionHashingStrategy(IntFunction<? super T> function)
        {
            this.function = function;
        }

        @Override
        public int computeHashCode(T object)
        {
            return this.function.intValueOf(object);
        }

        @Override
        public boolean equals(T object1, T object2)
        {
            return this.function.intValueOf(object1) == this.function.intValueOf(object2);
        }
    }

    private static final class LongFunctionHashingStrategy<T> implements HashingStrategy<T>
    {
        private static final long serialVersionUID = 1L;

        private final LongFunction<? super T> function;

        private LongFunctionHashingStrategy(LongFunction<? super T> function)
        {
            this.function = function;
        }

        @Override
        public int computeHashCode(T object)
        {
            return Long.hashCode(this.function.longValueOf(object));
        }

        @Override
        public boolean equals(T object1, T object2)
        {
            return this.function.longValueOf(object1) == this.function.longValueOf(object2);
        }
    }

    private static final class ShortFunctionHashingStrategy<T> implements HashingStrategy<T>
    {
        private static final long serialVersionUID = 1L;

        private final ShortFunction<? super T> function;

        private ShortFunctionHashingStrategy(ShortFunction<? super T> function)
        {
            this.function = function;
        }

        @Override
        public int computeHashCode(T object)
        {
            return this.function.shortValueOf(object);
        }

        @Override
        public boolean equals(T object1, T object2)
        {
            return this.function.shortValueOf(object1) == this.function.shortValueOf(object2);
        }
    }

    private static final class IdentityHashingStrategy implements HashingStrategy<Object>
    {
        private static final long serialVersionUID = 1L;

        @Override
        public int computeHashCode(Object object)
        {
            return System.identityHashCode(object);
        }

        @Override
        public boolean equals(Object object1, Object object2)
        {
            return object1 == object2;
        }
    }

    private static final class ChainedHashingStrategy<T> implements HashingStrategy<T>
    {
        private static final long serialVersionUID = 1L;
        private final HashingStrategy<T>[] hashingStrategies;

        @SafeVarargs
        @SuppressWarnings("varargs")
        private ChainedHashingStrategy(HashingStrategy<T>... hashingStrategies)
        {
            this.hashingStrategies = hashingStrategies;
        }

        @Override
        public int computeHashCode(T object)
        {
            int hashCode = this.hashingStrategies[0].computeHashCode(object);
            for (int i = 1; i < this.hashingStrategies.length; i++)
            {
                hashCode = hashCode * 31 + this.hashingStrategies[i].computeHashCode(object);
            }
            return hashCode;
        }

        @Override
        public boolean equals(T object1, T object2)
        {
            for (HashingStrategy<T> hashingStrategy : this.hashingStrategies)
            {
                if (!hashingStrategy.equals(object1, object2))
                {
                    return false;
                }
            }
            return true;
        }
    }
}
