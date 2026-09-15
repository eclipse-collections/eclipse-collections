/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.set.MutableSet;
import org.junit.Test;

import static org.eclipse.collections.impl.factory.Iterables.mList;
import static org.eclipse.collections.impl.factory.Iterables.mMap;
import static org.eclipse.collections.impl.factory.Iterables.mSet;
import static org.junit.Assert.fail;

/**
 * JUnit test to make sure that the methods {@link Verify#assertListsEqual(String, List, List)},
 * {@link Verify#assertSetsEqual(String, Set, Set)},
 * and {@link Verify#assertMapsEqual(String, Map, Map)} really throw when they ought to.
 */
@SuppressWarnings("AssertionFailureIgnored")
public class CollectionsEqualTest
{
    private final MutableList<String> list = mList("asdf", "qwer");
    private final MutableList<String> list2 = mList("asdf", "zxcv");
    private final MutableList<String> list3 = mList("asdf");

    private final MutableSet<String> set = mSet("asdf", "qwer");
    private final MutableSet<String> set2 = mSet("asdf", "zxcv");
    private final MutableSet<String> set3 = mSet("asdf");

    private final MutableSet<String> bigSet1 = mSet("1", "2", "3", "4", "5", "6");
    private final MutableSet<String> bigSet2 = mSet("7", "8", "9", "10", "11", "12");

    private final Map<String, String> map = mMap("asdf", "asdf", "qwer", "qwer");
    private final Map<String, String> map2 = mMap("asdf", "zxcv", "qwer", "qwer");
    private final Map<String, String> map3 = mMap("zxcv", "asdf", "qwer", "qwer");
    private final Map<String, String> map4 = mMap("zxcv", "zxcv", "qwer", "qwer");
    private final Map<String, String> map5 = mMap("asdf", "asdf");

    @Test
    public void listsEqual()
    {
        List<?> nullList = null;

        Verify.assertListsEqual(nullList, nullList);
        Verify.assertListsEqual("assertListsEqual(nullList, nullList)", nullList, nullList);

        Verify.assertListsEqual(this.list, this.list);
        Verify.assertListsEqual("assertListsEqual(list, list)", this.list, this.list);

        try
        {
            Verify.assertListsEqual("assertListsEqual(nullList, list)", nullList, this.list);
            fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(CollectionsEqualTest.class.getName(), e.getStackTrace()[5].toString());
        }

        try
        {
            Verify.assertListsEqual(nullList, this.list);
            fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(CollectionsEqualTest.class.getName(), e.getStackTrace()[6].toString());
        }

        try
        {
            Verify.assertListsEqual("assertListsEqual(list, nullList)", this.list, nullList);
            fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(CollectionsEqualTest.class.getName(), e.getStackTrace()[5].toString());
        }

        try
        {
            Verify.assertListsEqual(this.list, nullList);
            fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(CollectionsEqualTest.class.getName(), e.getStackTrace()[6].toString());
        }
    }

    @Test
    public void setsEqual()
    {
        Set<?> nullSet = null;

        Verify.assertSetsEqual(nullSet, nullSet);
        Verify.assertSetsEqual("assertSetsEqual(nullSet, nullSet)", nullSet, nullSet);

        Verify.assertSetsEqual(this.set, this.set);
        Verify.assertSetsEqual("assertSetsEqual(set, set)", this.set, this.set);

        try
        {
            Verify.assertSetsEqual("assertSetsEqual(nullSet, set)", nullSet, this.set);
            fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(CollectionsEqualTest.class.getName(), e.getStackTrace()[4].toString());
        }

        try
        {
            Verify.assertSetsEqual(nullSet, this.set);
            fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(CollectionsEqualTest.class.getName(), e.getStackTrace()[5].toString());
        }

        try
        {
            Verify.assertSetsEqual("assertSetsEqual(set, nullSet)", this.set, nullSet);
            fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(CollectionsEqualTest.class.getName(), e.getStackTrace()[5].toString());
        }

        try
        {
            Verify.assertSetsEqual(this.set, nullSet);
            fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(CollectionsEqualTest.class.getName(), e.getStackTrace()[6].toString());
        }
    }

    @Test
    public void mapsEqual()
    {
        Map<?, ?> nullMap = null;

        Verify.assertMapsEqual(nullMap, nullMap);
        Verify.assertMapsEqual("assertMapsEqual(nullMap, nullMap)", nullMap, nullMap);

        Verify.assertMapsEqual(this.map, this.map);
        Verify.assertMapsEqual("assertMapsEqual(map, map)", this.map, this.map);

        try
        {
            Verify.assertMapsEqual("assertMapsEqual(nullMap, map)", nullMap, this.map);
            fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(CollectionsEqualTest.class.getName(), e.getStackTrace()[4].toString());
        }

        try
        {
            Verify.assertMapsEqual(nullMap, this.map);
            fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(CollectionsEqualTest.class.getName(), e.getStackTrace()[5].toString());
        }

        try
        {
            Verify.assertMapsEqual("assertMapsEqual(map, nullMap)", this.map, nullMap);
            fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(CollectionsEqualTest.class.getName(), e.getStackTrace()[4].toString());
        }

        try
        {
            Verify.assertMapsEqual(this.map, nullMap);
            fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(CollectionsEqualTest.class.getName(), e.getStackTrace()[5].toString());
        }
    }

    @Test
    public void listsDifferentSizes()
    {
        try
        {
            Verify.assertListsEqual("assertListsEqual(list, list3)", this.list, this.list3);
            fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(CollectionsEqualTest.class.getName(), e.getStackTrace()[4].toString());
        }

        try
        {
            Verify.assertListsEqual(this.list, this.list3);
            fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(CollectionsEqualTest.class.getName(), e.getStackTrace()[5].toString());
        }
    }

    @Test
    public void differentLists()
    {
        try
        {
            Verify.assertListsEqual("assertListsEqual(list, list2)", this.list, this.list2);
            fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(CollectionsEqualTest.class.getName(), e.getStackTrace()[3].toString());
        }

        try
        {
            Verify.assertListsEqual(this.list, this.list2);
            fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(CollectionsEqualTest.class.getName(), e.getStackTrace()[4].toString());
        }
    }

    @Test
    public void setsDifferentSizes()
    {
        try
        {
            Verify.assertSetsEqual("assertSetsEqual(set, set2)", this.set, this.set2);
            fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(CollectionsEqualTest.class.getName(), e.getStackTrace()[3].toString());
        }

        try
        {
            Verify.assertSetsEqual(this.set, this.set2);
            fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(CollectionsEqualTest.class.getName(), e.getStackTrace()[4].toString());
        }
    }

    @Test
    public void differentSets()
    {
        try
        {
            Verify.assertSetsEqual("assertSetsEqual(set, set3)", this.set, this.set3);
            fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(CollectionsEqualTest.class.getName(), e.getStackTrace()[3].toString());
        }

        try
        {
            Verify.assertSetsEqual(this.set, this.set3);
            fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(CollectionsEqualTest.class.getName(), e.getStackTrace()[4].toString());
        }
    }

    @Test
    public void mapsDifferentValue()
    {
        try
        {
            Verify.assertMapsEqual("assertMapsEqual(map, map2)", this.map, this.map2);
            fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(CollectionsEqualTest.class.getName(), e.getStackTrace()[5].toString());
        }

        try
        {
            Verify.assertMapsEqual(this.map, this.map2);
            fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(CollectionsEqualTest.class.getName(), e.getStackTrace()[6].toString());
        }
    }

    @Test
    public void mapsDifferentKey()
    {
        try
        {
            Verify.assertMapsEqual("assertMapsEqual(map, map3)", this.map, this.map3);
            fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(CollectionsEqualTest.class.getName(), e.getStackTrace()[5].toString());
        }
        try

        {
            Verify.assertMapsEqual(this.map, this.map3);
            fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(CollectionsEqualTest.class.getName(), e.getStackTrace()[6].toString());
        }
    }

    @Test
    public void mapsDifferentEntry()
    {
        try
        {
            Verify.assertMapsEqual("assertMapsEqual(map, map4)", this.map, this.map4);
            fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(CollectionsEqualTest.class.getName(), e.getStackTrace()[5].toString());
        }

        try
        {
            Verify.assertMapsEqual(this.map, this.map4);
            fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(CollectionsEqualTest.class.getName(), e.getStackTrace()[6].toString());
        }
    }

    @Test
    public void mapsDifferentSize()
    {
        try
        {
            Verify.assertMapsEqual("assertMapsEqual(map, map5)", this.map, this.map5);
            fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(CollectionsEqualTest.class.getName(), e.getStackTrace()[5].toString());
        }

        try
        {
            Verify.assertMapsEqual(this.map, this.map5);
            fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(CollectionsEqualTest.class.getName(), e.getStackTrace()[6].toString());
        }
    }

    @Test
    public void bigSetsDiffer()
    {
        try
        {
            Verify.assertSetsEqual(this.bigSet1, this.bigSet2);
            fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(CollectionsEqualTest.class.getName(), e.getStackTrace()[3].toString());
        }
    }
}
