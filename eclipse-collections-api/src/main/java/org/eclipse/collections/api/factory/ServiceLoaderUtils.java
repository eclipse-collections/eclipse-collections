/*
 * Copyright (c) 2021 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api.factory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

public final class ServiceLoaderUtils
{
    private static final Map<String, String> FACTORY_IMPL = new HashMap<>();

    static
    {
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.bag.ImmutableBagFactory", "org.eclipse.collections.impl.bag.immutable.ImmutableBagFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.bag.MultiReaderBagFactory", "org.eclipse.collections.impl.bag.mutable.MultiReaderMutableBagFactory");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.bag.MutableBagFactory", "org.eclipse.collections.impl.bag.mutable.MutableBagFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.bag.primitive.ImmutableBooleanBagFactory", "org.eclipse.collections.impl.bag.immutable.primitive.ImmutableBooleanBagFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.bag.primitive.ImmutableByteBagFactory", "org.eclipse.collections.impl.bag.immutable.primitive.ImmutableByteBagFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.bag.primitive.ImmutableCharBagFactory", "org.eclipse.collections.impl.bag.immutable.primitive.ImmutableCharBagFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.bag.primitive.ImmutableDoubleBagFactory", "org.eclipse.collections.impl.bag.immutable.primitive.ImmutableDoubleBagFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.bag.primitive.ImmutableFloatBagFactory", "org.eclipse.collections.impl.bag.immutable.primitive.ImmutableFloatBagFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.bag.primitive.ImmutableIntBagFactory", "org.eclipse.collections.impl.bag.immutable.primitive.ImmutableIntBagFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.bag.primitive.ImmutableLongBagFactory", "org.eclipse.collections.impl.bag.immutable.primitive.ImmutableLongBagFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.bag.primitive.ImmutableShortBagFactory", "org.eclipse.collections.impl.bag.immutable.primitive.ImmutableShortBagFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.bag.primitive.MutableBooleanBagFactory", "org.eclipse.collections.impl.bag.mutable.primitive.MutableBooleanBagFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.bag.primitive.MutableByteBagFactory", "org.eclipse.collections.impl.bag.mutable.primitive.MutableByteBagFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.bag.primitive.MutableCharBagFactory", "org.eclipse.collections.impl.bag.mutable.primitive.MutableCharBagFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.bag.primitive.MutableDoubleBagFactory", "org.eclipse.collections.impl.bag.mutable.primitive.MutableDoubleBagFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.bag.primitive.MutableFloatBagFactory", "org.eclipse.collections.impl.bag.mutable.primitive.MutableFloatBagFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.bag.primitive.MutableIntBagFactory", "org.eclipse.collections.impl.bag.mutable.primitive.MutableIntBagFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.bag.primitive.MutableLongBagFactory", "org.eclipse.collections.impl.bag.mutable.primitive.MutableLongBagFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.bag.primitive.MutableShortBagFactory", "org.eclipse.collections.impl.bag.mutable.primitive.MutableShortBagFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.bag.sorted.ImmutableSortedBagFactory", "org.eclipse.collections.impl.bag.sorted.immutable.ImmutableSortedBagFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.bag.sorted.MutableSortedBagFactory", "org.eclipse.collections.impl.bag.sorted.mutable.MutableSortedBagFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.bimap.ImmutableBiMapFactory", "org.eclipse.collections.impl.bimap.immutable.ImmutableBiMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.bimap.MutableBiMapFactory", "org.eclipse.collections.impl.bimap.mutable.MutableBiMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.list.FixedSizeListFactory", "org.eclipse.collections.impl.list.fixed.FixedSizeListFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.list.ImmutableListFactory", "org.eclipse.collections.impl.list.immutable.ImmutableListFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.list.MultiReaderListFactory", "org.eclipse.collections.impl.list.mutable.MultiReaderMutableListFactory");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.list.MutableListFactory", "org.eclipse.collections.impl.list.mutable.MutableListFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.list.primitive.ImmutableBooleanListFactory", "org.eclipse.collections.impl.list.immutable.primitive.ImmutableBooleanListFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.list.primitive.ImmutableByteListFactory", "org.eclipse.collections.impl.list.immutable.primitive.ImmutableByteListFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.list.primitive.ImmutableCharListFactory", "org.eclipse.collections.impl.list.immutable.primitive.ImmutableCharListFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.list.primitive.ImmutableDoubleListFactory", "org.eclipse.collections.impl.list.immutable.primitive.ImmutableDoubleListFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.list.primitive.ImmutableFloatListFactory", "org.eclipse.collections.impl.list.immutable.primitive.ImmutableFloatListFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.list.primitive.ImmutableIntListFactory", "org.eclipse.collections.impl.list.immutable.primitive.ImmutableIntListFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.list.primitive.ImmutableLongListFactory", "org.eclipse.collections.impl.list.immutable.primitive.ImmutableLongListFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.list.primitive.ImmutableShortListFactory", "org.eclipse.collections.impl.list.immutable.primitive.ImmutableShortListFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.list.primitive.MutableBooleanListFactory", "org.eclipse.collections.impl.list.mutable.primitive.MutableBooleanListFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.list.primitive.MutableByteListFactory", "org.eclipse.collections.impl.list.mutable.primitive.MutableByteListFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.list.primitive.MutableCharListFactory", "org.eclipse.collections.impl.list.mutable.primitive.MutableCharListFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.list.primitive.MutableDoubleListFactory", "org.eclipse.collections.impl.list.mutable.primitive.MutableDoubleListFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.list.primitive.MutableFloatListFactory", "org.eclipse.collections.impl.list.mutable.primitive.MutableFloatListFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.list.primitive.MutableIntListFactory", "org.eclipse.collections.impl.list.mutable.primitive.MutableIntListFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.list.primitive.MutableLongListFactory", "org.eclipse.collections.impl.list.mutable.primitive.MutableLongListFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.list.primitive.MutableShortListFactory", "org.eclipse.collections.impl.list.mutable.primitive.MutableShortListFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.FixedSizeMapFactory", "org.eclipse.collections.impl.map.fixed.FixedSizeMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.ImmutableMapFactory", "org.eclipse.collections.impl.map.immutable.ImmutableMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.MutableMapFactory", "org.eclipse.collections.impl.map.mutable.MutableMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableBooleanBooleanMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableBooleanBooleanMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableBooleanByteMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableBooleanByteMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableBooleanCharMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableBooleanCharMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableBooleanDoubleMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableBooleanDoubleMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableBooleanFloatMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableBooleanFloatMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableBooleanIntMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableBooleanIntMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableBooleanLongMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableBooleanLongMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableBooleanShortMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableBooleanShortMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableByteBooleanMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableByteBooleanMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableByteByteMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableByteByteMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableByteCharMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableByteCharMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableByteDoubleMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableByteDoubleMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableByteFloatMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableByteFloatMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableByteIntMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableByteIntMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableByteLongMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableByteLongMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableByteObjectMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableByteObjectMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableByteShortMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableByteShortMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableCharBooleanMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableCharBooleanMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableCharByteMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableCharByteMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableCharCharMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableCharCharMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableCharDoubleMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableCharDoubleMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableCharFloatMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableCharFloatMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableCharIntMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableCharIntMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableCharLongMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableCharLongMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableCharObjectMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableCharObjectMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableCharShortMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableCharShortMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableDoubleBooleanMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableDoubleBooleanMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableDoubleByteMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableDoubleByteMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableDoubleCharMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableDoubleCharMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableDoubleDoubleMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableDoubleDoubleMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableDoubleFloatMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableDoubleFloatMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableDoubleIntMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableDoubleIntMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableDoubleLongMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableDoubleLongMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableDoubleObjectMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableDoubleObjectMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableDoubleShortMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableDoubleShortMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableFloatBooleanMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableFloatBooleanMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableFloatByteMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableFloatByteMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableFloatCharMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableFloatCharMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableFloatDoubleMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableFloatDoubleMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableFloatFloatMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableFloatFloatMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableFloatIntMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableFloatIntMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableFloatLongMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableFloatLongMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableFloatObjectMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableFloatObjectMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableFloatShortMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableFloatShortMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableIntBooleanMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableIntBooleanMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableIntByteMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableIntByteMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableIntCharMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableIntCharMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableIntDoubleMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableIntDoubleMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableIntFloatMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableIntFloatMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableIntIntMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableIntIntMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableIntLongMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableIntLongMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableIntObjectMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableIntObjectMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableIntShortMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableIntShortMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableLongBooleanMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableLongBooleanMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableLongByteMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableLongByteMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableLongCharMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableLongCharMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableLongDoubleMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableLongDoubleMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableLongFloatMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableLongFloatMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableLongIntMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableLongIntMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableLongLongMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableLongLongMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableLongObjectMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableLongObjectMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableLongShortMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableLongShortMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableObjectBooleanMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableObjectBooleanMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableObjectByteMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableObjectByteMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableObjectCharMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableObjectCharMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableObjectDoubleMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableObjectDoubleMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableObjectFloatMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableObjectFloatMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableObjectIntMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableObjectIntMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableObjectLongMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableObjectLongMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableObjectShortMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableObjectShortMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableShortBooleanMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableShortBooleanMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableShortByteMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableShortByteMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableShortCharMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableShortCharMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableShortDoubleMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableShortDoubleMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableShortFloatMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableShortFloatMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableShortIntMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableShortIntMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableShortLongMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableShortLongMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableShortObjectMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableShortObjectMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.ImmutableShortShortMapFactory", "org.eclipse.collections.impl.map.immutable.primitive.ImmutableShortShortMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableBooleanBooleanMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableBooleanBooleanMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableBooleanByteMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableBooleanByteMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableBooleanCharMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableBooleanCharMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableBooleanDoubleMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableBooleanDoubleMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableBooleanFloatMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableBooleanFloatMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableBooleanIntMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableBooleanIntMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableBooleanLongMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableBooleanLongMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableBooleanShortMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableBooleanShortMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableByteBooleanMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableByteBooleanMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableByteByteMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableByteByteMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableByteCharMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableByteCharMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableByteDoubleMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableByteDoubleMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableByteFloatMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableByteFloatMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableByteIntMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableByteIntMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableByteLongMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableByteLongMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableByteObjectMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableByteObjectMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableByteShortMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableByteShortMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableCharBooleanMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableCharBooleanMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableCharByteMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableCharByteMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableCharCharMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableCharCharMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableCharDoubleMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableCharDoubleMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableCharFloatMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableCharFloatMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableCharIntMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableCharIntMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableCharLongMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableCharLongMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableCharObjectMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableCharObjectMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableCharShortMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableCharShortMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableDoubleBooleanMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableDoubleBooleanMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableDoubleByteMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableDoubleByteMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableDoubleCharMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableDoubleCharMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableDoubleDoubleMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableDoubleDoubleMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableDoubleFloatMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableDoubleFloatMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableDoubleIntMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableDoubleIntMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableDoubleLongMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableDoubleLongMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableDoubleObjectMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableDoubleObjectMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableDoubleShortMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableDoubleShortMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableFloatBooleanMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableFloatBooleanMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableFloatByteMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableFloatByteMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableFloatCharMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableFloatCharMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableFloatDoubleMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableFloatDoubleMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableFloatFloatMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableFloatFloatMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableFloatIntMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableFloatIntMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableFloatLongMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableFloatLongMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableFloatObjectMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableFloatObjectMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableFloatShortMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableFloatShortMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableIntBooleanMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableIntBooleanMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableIntByteMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableIntByteMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableIntCharMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableIntCharMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableIntDoubleMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableIntDoubleMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableIntFloatMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableIntFloatMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableIntIntMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableIntIntMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableIntLongMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableIntLongMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableIntObjectMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableIntObjectMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableIntShortMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableIntShortMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableLongBooleanMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableLongBooleanMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableLongByteMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableLongByteMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableLongCharMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableLongCharMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableLongDoubleMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableLongDoubleMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableLongFloatMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableLongFloatMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableLongIntMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableLongIntMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableLongLongMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableLongLongMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableLongObjectMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableLongObjectMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableLongShortMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableLongShortMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableObjectBooleanHashingStrategyMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableObjectBooleanHashingStrategyMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableObjectBooleanMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableObjectBooleanMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableObjectByteHashingStrategyMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableObjectByteHashingStrategyMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableObjectByteMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableObjectByteMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableObjectCharHashingStrategyMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableObjectCharHashingStrategyMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableObjectCharMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableObjectCharMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableObjectDoubleHashingStrategyMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableObjectDoubleHashingStrategyMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableObjectDoubleMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableObjectDoubleMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableObjectFloatHashingStrategyMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableObjectFloatHashingStrategyMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableObjectFloatMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableObjectFloatMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableObjectIntHashingStrategyMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableObjectIntHashingStrategyMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableObjectIntMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableObjectIntMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableObjectLongHashingStrategyMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableObjectLongHashingStrategyMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableObjectLongMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableObjectLongMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableObjectShortHashingStrategyMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableObjectShortHashingStrategyMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableObjectShortMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableObjectShortMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableShortBooleanMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableShortBooleanMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableShortByteMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableShortByteMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableShortCharMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableShortCharMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableShortDoubleMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableShortDoubleMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableShortFloatMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableShortFloatMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableShortIntMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableShortIntMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableShortLongMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableShortLongMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableShortObjectMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableShortObjectMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.primitive.MutableShortShortMapFactory", "org.eclipse.collections.impl.map.mutable.primitive.MutableShortShortMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.sorted.ImmutableSortedMapFactory", "org.eclipse.collections.impl.map.sorted.immutable.ImmutableSortedMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.map.sorted.MutableSortedMapFactory", "org.eclipse.collections.impl.map.sorted.mutable.MutableSortedMapFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.set.FixedSizeSetFactory", "org.eclipse.collections.impl.set.fixed.FixedSizeSetFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.set.ImmutableSetFactory", "org.eclipse.collections.impl.set.immutable.ImmutableSetFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.set.MultiReaderSetFactory", "org.eclipse.collections.impl.set.mutable.MultiReaderMutableSetFactory");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.set.MutableSetFactory", "org.eclipse.collections.impl.set.mutable.MutableSetFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.set.primitive.ImmutableBooleanSetFactory", "org.eclipse.collections.impl.set.immutable.primitive.ImmutableBooleanSetFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.set.primitive.ImmutableByteSetFactory", "org.eclipse.collections.impl.set.immutable.primitive.ImmutableByteSetFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.set.primitive.ImmutableCharSetFactory", "org.eclipse.collections.impl.set.immutable.primitive.ImmutableCharSetFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.set.primitive.ImmutableDoubleSetFactory", "org.eclipse.collections.impl.set.immutable.primitive.ImmutableDoubleSetFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.set.primitive.ImmutableFloatSetFactory", "org.eclipse.collections.impl.set.immutable.primitive.ImmutableFloatSetFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.set.primitive.ImmutableIntSetFactory", "org.eclipse.collections.impl.set.immutable.primitive.ImmutableIntSetFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.set.primitive.ImmutableLongSetFactory", "org.eclipse.collections.impl.set.immutable.primitive.ImmutableLongSetFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.set.primitive.ImmutableShortSetFactory", "org.eclipse.collections.impl.set.immutable.primitive.ImmutableShortSetFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.set.primitive.MutableBooleanSetFactory", "org.eclipse.collections.impl.set.mutable.primitive.MutableBooleanSetFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.set.primitive.MutableByteSetFactory", "org.eclipse.collections.impl.set.mutable.primitive.MutableByteSetFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.set.primitive.MutableCharSetFactory", "org.eclipse.collections.impl.set.mutable.primitive.MutableCharSetFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.set.primitive.MutableDoubleSetFactory", "org.eclipse.collections.impl.set.mutable.primitive.MutableDoubleSetFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.set.primitive.MutableFloatSetFactory", "org.eclipse.collections.impl.set.mutable.primitive.MutableFloatSetFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.set.primitive.MutableIntSetFactory", "org.eclipse.collections.impl.set.mutable.primitive.MutableIntSetFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.set.primitive.MutableLongSetFactory", "org.eclipse.collections.impl.set.mutable.primitive.MutableLongSetFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.set.primitive.MutableShortSetFactory", "org.eclipse.collections.impl.set.mutable.primitive.MutableShortSetFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.set.sorted.ImmutableSortedSetFactory", "org.eclipse.collections.impl.set.sorted.immutable.ImmutableSortedSetFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.set.sorted.MutableSortedSetFactory", "org.eclipse.collections.impl.set.sorted.mutable.MutableSortedSetFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.stack.ImmutableStackFactory", "org.eclipse.collections.impl.stack.immutable.ImmutableStackFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.stack.MutableStackFactory", "org.eclipse.collections.impl.stack.mutable.MutableStackFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.stack.primitive.ImmutableBooleanStackFactory", "org.eclipse.collections.impl.stack.immutable.primitive.ImmutableBooleanStackFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.stack.primitive.ImmutableByteStackFactory", "org.eclipse.collections.impl.stack.immutable.primitive.ImmutableByteStackFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.stack.primitive.ImmutableCharStackFactory", "org.eclipse.collections.impl.stack.immutable.primitive.ImmutableCharStackFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.stack.primitive.ImmutableDoubleStackFactory", "org.eclipse.collections.impl.stack.immutable.primitive.ImmutableDoubleStackFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.stack.primitive.ImmutableFloatStackFactory", "org.eclipse.collections.impl.stack.immutable.primitive.ImmutableFloatStackFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.stack.primitive.ImmutableIntStackFactory", "org.eclipse.collections.impl.stack.immutable.primitive.ImmutableIntStackFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.stack.primitive.ImmutableLongStackFactory", "org.eclipse.collections.impl.stack.immutable.primitive.ImmutableLongStackFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.stack.primitive.ImmutableShortStackFactory", "org.eclipse.collections.impl.stack.immutable.primitive.ImmutableShortStackFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.stack.primitive.MutableBooleanStackFactory", "org.eclipse.collections.impl.stack.mutable.primitive.MutableBooleanStackFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.stack.primitive.MutableByteStackFactory", "org.eclipse.collections.impl.stack.mutable.primitive.MutableByteStackFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.stack.primitive.MutableCharStackFactory", "org.eclipse.collections.impl.stack.mutable.primitive.MutableCharStackFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.stack.primitive.MutableDoubleStackFactory", "org.eclipse.collections.impl.stack.mutable.primitive.MutableDoubleStackFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.stack.primitive.MutableFloatStackFactory", "org.eclipse.collections.impl.stack.mutable.primitive.MutableFloatStackFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.stack.primitive.MutableIntStackFactory", "org.eclipse.collections.impl.stack.mutable.primitive.MutableIntStackFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.stack.primitive.MutableLongStackFactory", "org.eclipse.collections.impl.stack.mutable.primitive.MutableLongStackFactoryImpl");
        FACTORY_IMPL.put("org.eclipse.collections.api.factory.stack.primitive.MutableShortStackFactory", "org.eclipse.collections.impl.stack.mutable.primitive.MutableShortStackFactoryImpl");
    }

    private ServiceLoaderUtils()
    {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }

    public static <T> T loadServiceClass(Class<T> serviceClass)
    {
        T result =
                ServiceLoaderUtils.loadServiceClass(serviceClass, Thread.currentThread().getContextClassLoader());
        if (result == null)
        {
            result = ServiceLoaderUtils.loadServiceClass(serviceClass, ServiceLoaderUtils.class.getClassLoader());
        }
        if (result == null)
        {
            result = ServiceLoaderUtils.loadByReflection(serviceClass, Thread.currentThread().getContextClassLoader());
        }
        if (result == null)
        {
            result = ServiceLoaderUtils.loadByReflection(serviceClass, ServiceLoaderUtils.class.getClassLoader());
        }
        if (result == null)
        {
            String message = "Could not find any implementations of "
                    + serviceClass.getSimpleName()
                    + ". Check that eclipse-collections.jar is on the classpath and that its META-INF/services directory is intact.";
            result = ServiceLoaderUtils.createProxyInstance(serviceClass, message);
        }
        return result;
    }

    private static <T> T loadServiceClass(Class<T> serviceClass, ClassLoader loader)
    {
        List<T> factories = new ArrayList<>();
        for (T factory : ServiceLoader.load(serviceClass, loader))
        {
            factories.add(factory);
        }
        if (factories.isEmpty())
        {
            return null;
        }
        if (factories.size() > 1)
        {
            String message = String.format(
                    "Found multiple implementations of %s on the classpath. Check that there is only one copy of eclipse-collections.jar on the classpath. Found implementations: %s.",
                    serviceClass.getSimpleName(),
                    factories.stream()
                            .map(T::getClass)
                            .map(Class::getSimpleName)
                            .collect(Collectors.joining(", ")));
            return ServiceLoaderUtils.createProxyInstance(serviceClass, message);
        }
        return factories.get(0);
    }

    private static <T> T loadByReflection(Class<T> serviceClass, ClassLoader loader)
    {
        String fallbackName = FACTORY_IMPL.get(serviceClass.getName());
        try
        {
            Class<T> fallbackClass = (Class<T>) Class.forName(fallbackName, true, loader);
            return fallbackClass.getDeclaredConstructor().newInstance();
        }
        catch (Exception e)
        {
            // ignore
        }
        return null;
    }

    private static <T> T createProxyInstance(Class<T> serviceClass, String message)
    {
        InvocationHandler handler = new ThrowingInvocationHandler(message);
        Object proxyInstance = Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                handler);
        return serviceClass.cast(proxyInstance);
    }
}
