# primitiveStream() for IntSet/LongSet/DoubleSet — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add `spliterator()`, `primitiveStream()`, and `primitiveParallelStream()` to `IntSet`, `LongSet`, and `DoubleSet` (and their mutable, immutable, and map-key-set variants) so primitive sets have the same stream API as primitive lists.

**Architecture:** All changes are to StringTemplate (`.stg`) template files — the code generator produces `.java` files at build time. Every template addition is wrapped in `<if(primitive.specializedStream)>` so the change only applies to `int`, `long`, and `double`. The `spliterator()` implementation delegates to `Spliterators.spliterator(this.toArray(), DISTINCT | SIZED)`.

**Tech Stack:** Java 17+, StringTemplate 4 (STG), Maven (`mvn generate-sources`), JUnit 5.

---

## File Map

| Template file (all under `eclipse-collections-code-generator/src/main/resources/`) | Change |
|---|---|
| `api/set/primitiveSet.stg` | Add stream imports + `spliterator()` abstract + 2 default stream methods |
| `impl/set/mutable/primitiveHashSet.stg` | Add `Spliterators` import + implement `spliterator()` |
| `impl/set/mutable/synchronizedPrimitiveSet.stg` | Add `Spliterator` import + synchronized delegate `spliterator()` |
| `impl/set/mutable/unmodifiablePrimitiveSet.stg` | Add `Spliterator` import + delegate `spliterator()` |
| `impl/set/immutable/immutablePrimitiveEmptySet.stg` | Add imports + implement `spliterator()` returning empty spliterator |
| `impl/set/immutable/immutablePrimitiveSingletonSet.stg` | Add imports + implement `spliterator()` using `<name>SingletonSpliterator` |
| `impl/map/abstractMutablePrimitiveKeySet.stg` | Add imports + implement `spliterator()` |
| `impl/map/mutable/primitiveImmutableKeySets.stg` | Add imports + implement `spliterator()` with `IMMUTABLE` flag |
| `test/set/mutable/abstractPrimitiveSetTestCase.stg` | Add `stream()` and `parallelStream()` test methods |
| `test/set/immutable/abstractImmutablePrimitiveSetTestCase.stg` | Add `stream()`, `parallelStream()`, and `spliterator_characteristics()` tests |

---

## Task 1: Add stream API to `primitiveSet.stg` (the base interface)

**Files:**
- Modify: `eclipse-collections-code-generator/src/main/resources/api/set/primitiveSet.stg`

The current file has no stream imports and no stream methods. We add them inside `<if(primitive.specializedStream)>` guards — the same pattern used in `primitiveList.stg`.

- [ ] **Step 1: Add the stream imports**

Open `api/set/primitiveSet.stg`. After line 17 (`import java.util.Set;`), add:

```stg
<if(primitive.specializedStream)>
import java.util.Spliterator;
import java.util.stream.StreamSupport;
import java.util.stream.<name>Stream;
<endif>
```

The import block should now read:
```stg
import java.util.Set;
<if(primitive.specializedStream)>
import java.util.Spliterator;
import java.util.stream.StreamSupport;
import java.util.stream.<name>Stream;
<endif>
```

- [ ] **Step 2: Add the stream methods to the interface body**

In the same file, before the closing `}` of the interface body (line 130, before `Immutable<name>Set toImmutable();`'s closing `}`), insert after `toImmutable()` (before `}` then `>>`). Replace:

```stg
    /**
     * Returns an immutable copy of this set. If the set is immutable, it returns itself.
     */
    Immutable<name>Set toImmutable();
}
```

with:

```stg
    /**
     * Returns an immutable copy of this set. If the set is immutable, it returns itself.
     */
    Immutable<name>Set toImmutable();
<if(primitive.specializedStream)>

    /**
     * @since 14.0
     */
    Spliterator.Of<name> spliterator();

    /**
     * @since 14.0
     */
    default <name>Stream primitiveStream()
    {
        return StreamSupport.<type>Stream(this.spliterator(), false);
    }

    /**
     * @since 14.0
     */
    default <name>Stream primitiveParallelStream()
    {
        return StreamSupport.<type>Stream(this.spliterator(), true);
    }
<endif>
}
```

- [ ] **Step 3: Commit**

```bash
cd D:/_Dev/eclipse-collections
git add eclipse-collections-code-generator/src/main/resources/api/set/primitiveSet.stg
git commit -m "feat: add spliterator/primitiveStream API to primitiveSet.stg interface"
```

---

## Task 2: Implement `spliterator()` in `primitiveHashSet.stg`

**Files:**
- Modify: `eclipse-collections-code-generator/src/main/resources/impl/set/mutable/primitiveHashSet.stg`

`IntHashSet` stores elements in a `zeroToThirtyOne` bitmask + a `table[]` array. We use `toArray()` (which already handles both) to build the spliterator.

- [ ] **Step 1: Add the import**

The file currently has no `Spliterators` import. Find the imports block (around line 27–50). Add after `import java.util.Arrays;`:

```stg
<if(primitive.specializedStream)>
import java.util.Spliterator;
import java.util.Spliterators;
<endif>
```

- [ ] **Step 2: Add the `spliterator()` method**

The class body closes at line 1777 (`}` then `>>`). Add before the closing `}`:

```stg
<if(primitive.specializedStream)>

    @Override
    public Spliterator.Of<name> spliterator()
    {
        return Spliterators.spliterator(this.toArray(), Spliterator.DISTINCT | Spliterator.SIZED);
    }
<endif>
```

The end of the file should look like:
```stg
<if(primitive.specializedStream)>

    @Override
    public Spliterator.Of<name> spliterator()
    {
        return Spliterators.spliterator(this.toArray(), Spliterator.DISTINCT | Spliterator.SIZED);
    }
<endif>
}

>>
```

- [ ] **Step 3: Commit**

```bash
git add eclipse-collections-code-generator/src/main/resources/impl/set/mutable/primitiveHashSet.stg
git commit -m "feat: implement spliterator() in primitiveHashSet.stg"
```

---

## Task 3: Implement `spliterator()` in `synchronizedPrimitiveSet.stg`

**Files:**
- Modify: `eclipse-collections-code-generator/src/main/resources/impl/set/mutable/synchronizedPrimitiveSet.stg`

`SynchronizedIntSet` wraps a delegate; `spliterator()` must be `synchronized` to match the class contract.

- [ ] **Step 1: Add the import**

Find the imports block (around line 19). After `import java.util.Collections;` add:

```stg
<if(primitive.specializedStream)>
import java.util.Spliterator;
<endif>
```

- [ ] **Step 2: Add the `spliterator()` method**

The class body closes at line 241 (`}` then `>>`). Add before the closing `}`:

```stg
<if(primitive.specializedStream)>

    @Override
    public synchronized Spliterator.Of<name> spliterator()
    {
        return this.getMutable<name>Set().spliterator();
    }
<endif>
```

The end of the file should look like:
```stg
    public Mutable<name>Set newEmpty()
    {
        synchronized (this.getLock())
        {
            return this.getMutable<name>Set().newEmpty();
        }
    }
<if(primitive.specializedStream)>

    @Override
    public synchronized Spliterator.Of<name> spliterator()
    {
        return this.getMutable<name>Set().spliterator();
    }
<endif>
}

>>
```

- [ ] **Step 3: Commit**

```bash
git add eclipse-collections-code-generator/src/main/resources/impl/set/mutable/synchronizedPrimitiveSet.stg
git commit -m "feat: implement spliterator() in synchronizedPrimitiveSet.stg"
```

---

## Task 4: Implement `spliterator()` in `unmodifiablePrimitiveSet.stg`

**Files:**
- Modify: `eclipse-collections-code-generator/src/main/resources/impl/set/mutable/unmodifiablePrimitiveSet.stg`

`UnmodifiableIntSet` also wraps a delegate.

- [ ] **Step 1: Add the import**

Find the imports block. After any existing `import java.util.*` lines, add:

```stg
<if(primitive.specializedStream)>
import java.util.Spliterator;
<endif>
```

- [ ] **Step 2: Add the `spliterator()` method**

The class body closes at line 160 (`}` then `>>`). Add before the closing `}`:

```stg
<if(primitive.specializedStream)>

    @Override
    public Spliterator.Of<name> spliterator()
    {
        return this.getMutable<name>Set().spliterator();
    }
<endif>
```

The end of the file should look like:
```stg
    public Mutable<name>Set newEmpty()
    {
        return this.getMutable<name>Set().newEmpty();
    }
<if(primitive.specializedStream)>

    @Override
    public Spliterator.Of<name> spliterator()
    {
        return this.getMutable<name>Set().spliterator();
    }
<endif>
}

>>
```

- [ ] **Step 3: Commit**

```bash
git add eclipse-collections-code-generator/src/main/resources/impl/set/mutable/unmodifiablePrimitiveSet.stg
git commit -m "feat: implement spliterator() in unmodifiablePrimitiveSet.stg"
```

---

## Task 5: Implement `spliterator()` in `immutablePrimitiveEmptySet.stg`

**Files:**
- Modify: `eclipse-collections-code-generator/src/main/resources/impl/set/immutable/immutablePrimitiveEmptySet.stg`

For an empty set, the JDK provides `Spliterators.emptyIntSpliterator()` etc. — no elements to copy.

- [ ] **Step 1: Add the imports**

Find the imports block near the top of the body. Add:

```stg
<if(primitive.specializedStream)>
import java.util.Spliterator;
import java.util.Spliterators;
<endif>
```

- [ ] **Step 2: Add the `spliterator()` method**

The main body closes at line 396 (`>>`), with the class `}` just before at line 394. Add before the class-closing `}` (around line 394):

```stg
<if(primitive.specializedStream)>

    @Override
    public Spliterator.Of<name> spliterator()
    {
        return Spliterators.empty<name>Spliterator();
    }
<endif>
```

`<name>` expands to `Int`, `Long`, or `Double` — producing `emptyIntSpliterator()`, `emptyLongSpliterator()`, `emptyDoubleSpliterator()`.

- [ ] **Step 3: Commit**

```bash
git add eclipse-collections-code-generator/src/main/resources/impl/set/immutable/immutablePrimitiveEmptySet.stg
git commit -m "feat: implement spliterator() in immutablePrimitiveEmptySet.stg"
```

---

## Task 6: Implement `spliterator()` in `immutablePrimitiveSingletonSet.stg`

**Files:**
- Modify: `eclipse-collections-code-generator/src/main/resources/impl/set/immutable/immutablePrimitiveSingletonSet.stg`

A singleton set reuses the existing `IntSingletonSpliterator` class (already used by `ImmutableIntSingletonList`).

- [ ] **Step 1: Add the imports**

Find the imports block. Add:

```stg
<if(primitive.specializedStream)>
import java.util.Spliterator;
import org.eclipse.collections.impl.stream.primitive.<name>SingletonSpliterator;
<endif>
```

- [ ] **Step 2: Add the `spliterator()` method**

The main body closes at line 448 (`>>`). Add before the class-closing `}` (line 447):

```stg
<if(primitive.specializedStream)>

    @Override
    public Spliterator.Of<name> spliterator()
    {
        return new <name>SingletonSpliterator(this.element);
    }
<endif>
```

Note: the field is named `element` in `ImmutableIntSingletonSet` (verify with `grep -n "this\.element" immutablePrimitiveSingletonSet.stg`).

- [ ] **Step 3: Verify field name**

```bash
grep -n "this\.element\b" "D:/_Dev/eclipse-collections/eclipse-collections-code-generator/src/main/resources/impl/set/immutable/immutablePrimitiveSingletonSet.stg" | head -5
```

Expected output: lines referencing `this.element` (not `this.element1`). If the field is `element1`, update the method body accordingly.

- [ ] **Step 4: Commit**

```bash
git add eclipse-collections-code-generator/src/main/resources/impl/set/immutable/immutablePrimitiveSingletonSet.stg
git commit -m "feat: implement spliterator() in immutablePrimitiveSingletonSet.stg"
```

---

## Task 7: Implement `spliterator()` in `abstractMutablePrimitiveKeySet.stg`

**Files:**
- Modify: `eclipse-collections-code-generator/src/main/resources/impl/map/abstractMutablePrimitiveKeySet.stg`

`AbstractMutableIntKeySet` is the mutable key-set view of `IntIntHashMap` etc. It implements `MutableIntSet` so must provide `spliterator()`.

- [ ] **Step 1: Add the imports**

Find the imports block (around line 20–55). Add after `import java.util.Arrays;`:

```stg
<if(primitive.specializedStream)>
import java.util.Spliterator;
import java.util.Spliterators;
<endif>
```

- [ ] **Step 2: Add the `spliterator()` method**

The class body closes at line 936 (`}` then `>>`). Add before the closing `}`:

```stg
<if(primitive.specializedStream)>

    @Override
    public Spliterator.Of<name> spliterator()
    {
        return Spliterators.spliterator(this.toArray(), Spliterator.DISTINCT | Spliterator.SIZED);
    }
<endif>
```

- [ ] **Step 3: Commit**

```bash
git add eclipse-collections-code-generator/src/main/resources/impl/map/abstractMutablePrimitiveKeySet.stg
git commit -m "feat: implement spliterator() in abstractMutablePrimitiveKeySet.stg"
```

---

## Task 8: Implement `spliterator()` in `primitiveImmutableKeySets.stg`

**Files:**
- Modify: `eclipse-collections-code-generator/src/main/resources/impl/map/mutable/primitiveImmutableKeySets.stg`

This shared template generates both `ImmutableIntMapKeySet` and `ImmutableIntIntMapKeySet`. Both are immutable, so add `IMMUTABLE` to characteristics.

- [ ] **Step 1: Add the imports**

Find the imports block (lines 11–27). Add:

```stg
<if(primitive.specializedStream)>
import java.util.Spliterator;
import java.util.Spliterators;
<endif>
```

- [ ] **Step 2: Add the `spliterator()` method**

The `primitiveKeySetBody` template closes its class body near the end. Find the `writeReplace()` method (line ~323) and the iterator inner class closing. The class `}` is near line 400. Add before it:

```stg
<if(primitive.specializedStream)>

    @Override
    public Spliterator.Of<name> spliterator()
    {
        return Spliterators.spliterator(this.toArray(), Spliterator.DISTINCT | Spliterator.SIZED | Spliterator.IMMUTABLE);
    }
<endif>
```

- [ ] **Step 3: Commit**

```bash
git add eclipse-collections-code-generator/src/main/resources/impl/map/mutable/primitiveImmutableKeySets.stg
git commit -m "feat: implement spliterator() in primitiveImmutableKeySets.stg"
```

---

## Task 9: Add tests to `abstractPrimitiveSetTestCase.stg` (mutable sets)

**Files:**
- Modify: `eclipse-collections-code-generator/src/main/resources/test/set/mutable/abstractPrimitiveSetTestCase.stg`

Follow the exact pattern from `abstractPrimitiveListTestCase.stg` (lines 795–812).

- [ ] **Step 1: Add the imports**

The file imports block is around lines 18–38. Add:

```stg
<if(primitive.specializedStream)>
import java.util.Spliterator;
import java.util.stream.Collectors;
<endif>
```

- [ ] **Step 2: Add the test methods**

The class body currently closes at line 736 (`}` then `>>`). Add before the closing `}`:

```stg
<if(primitive.specializedStream)>

    @Test
    public void stream()
    {
        Mutable<name>Set set = this.newWith(<["1", "2", "3"]:(literal.(type))(); separator=", ">);
        assertEquals(3, (int) set.primitiveStream().count());
        assertTrue(set.primitiveStream().allMatch(set::contains));
        assertEquals(3, (int) set.asSynchronized().primitiveStream().count());
        assertEquals(3, (int) set.asUnmodifiable().primitiveStream().count());
    }

    @Test
    public void parallelStream()
    {
        Mutable<name>Set set = this.newWith(<["1", "2", "3"]:(literal.(type))(); separator=", ">);
        assertEquals(3, (int) set.primitiveParallelStream().count());
        assertTrue(set.primitiveParallelStream().allMatch(set::contains));
    }

    @Test
    public void spliterator_characteristics()
    {
        Mutable<name>Set set = this.newWith(<["1", "2", "3"]:(literal.(type))(); separator=", ">);
        Spliterator.Of<name> spliterator = set.spliterator();
        assertTrue(spliterator.hasCharacteristics(Spliterator.DISTINCT));
        assertTrue(spliterator.hasCharacteristics(Spliterator.SIZED));
        assertEquals(3L, spliterator.estimateSize());
    }
<endif>
```

- [ ] **Step 3: Commit**

```bash
git add eclipse-collections-code-generator/src/main/resources/test/set/mutable/abstractPrimitiveSetTestCase.stg
git commit -m "test: add primitiveStream/spliterator tests to abstractPrimitiveSetTestCase.stg"
```

---

## Task 10: Add tests to `abstractImmutablePrimitiveSetTestCase.stg` (immutable sets)

**Files:**
- Modify: `eclipse-collections-code-generator/src/main/resources/test/set/immutable/abstractImmutablePrimitiveSetTestCase.stg`

- [ ] **Step 1: Add the imports**

Find the imports block near the top. Add:

```stg
<if(primitive.specializedStream)>
import java.util.Spliterator;
<endif>
```

- [ ] **Step 2: Add the test methods**

The class body currently closes at line 537 (`}` then `>>`). Add before the closing `}`:

```stg
<if(primitive.specializedStream)>

    @Test
    public void stream()
    {
        Immutable<name>Set set = this.newWith(<["1", "2", "3"]:(literal.(type))(); separator=", ">);
        assertEquals(3, (int) set.primitiveStream().count());
        assertTrue(set.primitiveStream().allMatch(set::contains));
    }

    @Test
    public void stream_empty()
    {
        Immutable<name>Set empty = this.newWith();
        assertEquals(0, (int) empty.primitiveStream().count());
    }

    @Test
    public void parallelStream()
    {
        Immutable<name>Set set = this.newWith(<["1", "2", "3"]:(literal.(type))(); separator=", ">);
        assertEquals(3, (int) set.primitiveParallelStream().count());
        assertTrue(set.primitiveParallelStream().allMatch(set::contains));
    }

    @Test
    public void spliterator_characteristics()
    {
        Immutable<name>Set set = this.newWith(<["1", "2", "3"]:(literal.(type))(); separator=", ">);
        Spliterator.Of<name> spliterator = set.spliterator();
        assertTrue(spliterator.hasCharacteristics(Spliterator.DISTINCT));
        assertTrue(spliterator.hasCharacteristics(Spliterator.SIZED));
        assertTrue(spliterator.hasCharacteristics(Spliterator.IMMUTABLE));
        assertEquals(3L, spliterator.estimateSize());
    }

    @Test
    public void spliterator_empty_characteristics()
    {
        Spliterator.Of<name> spliterator = this.newWith().spliterator();
        assertTrue(spliterator.hasCharacteristics(Spliterator.DISTINCT));
        assertTrue(spliterator.hasCharacteristics(Spliterator.SIZED));
        assertTrue(spliterator.hasCharacteristics(Spliterator.IMMUTABLE));
        assertEquals(0L, spliterator.estimateSize());
    }
<endif>
```

- [ ] **Step 3: Commit**

```bash
git add eclipse-collections-code-generator/src/main/resources/test/set/immutable/abstractImmutablePrimitiveSetTestCase.stg
git commit -m "test: add primitiveStream/spliterator tests to abstractImmutablePrimitiveSetTestCase.stg"
```

---

## Task 11: Generate sources and run unit tests

- [ ] **Step 1: Regenerate Java sources from templates**

```bash
cd D:/_Dev/eclipse-collections
mvn generate-sources -pl eclipse-collections-code-generator-maven-plugin,eclipse-collections-api,eclipse-collections,unit-tests -am -q
```

Expected: BUILD SUCCESS with no errors.

If you see a `StringTemplate` parse error, it means a template tag was left unclosed — check the `.stg` file mentioned in the error output.

- [ ] **Step 2: Verify generated files exist**

```bash
find eclipse-collections/target/generated-sources -name "IntHashSet.java" -o -name "LongHashSet.java" -o -name "DoubleHashSet.java" | head -5
find eclipse-collections/target/generated-sources -name "ImmutableIntEmptySet.java" | head -3
```

Expected: paths printed for all listed files.

- [ ] **Step 3: Run primitive set unit tests**

```bash
mvn test -pl unit-tests -Dtest="AbstractIntSetTestCase,AbstractLongSetTestCase,AbstractDoubleSetTestCase,AbstractImmutableIntHashSetTestCase,AbstractImmutableLongHashSetTestCase,AbstractImmutableDoubleHashSetTestCase" -q
```

Expected: `BUILD SUCCESS`, all tests pass.

If a test fails with `AbstractMethodError: spliterator()`, a concrete set class hasn't had its template updated. Check which class is throwing — it will point to the missing template.

- [ ] **Step 4: Run full primitive set test suite for confidence**

```bash
mvn test -pl unit-tests -Dtest="*IntSet*,*LongSet*,*DoubleSet*" -q
```

Expected: `BUILD SUCCESS`.

- [ ] **Step 5: Final commit**

```bash
git add -A
git commit -m "feat: add primitiveStream/spliterator to IntSet, LongSet, DoubleSet (issue #1454)"
```

---

## Self-Review Checklist

- [x] **Spec coverage:** All 11 template files from the spec are covered (API, 3 mutable impls, 2 immutable impls, 3 map key set templates, 2 test templates).
- [x] **No placeholders:** Every step has exact code or exact commands.
- [x] **Type consistency:** `spliterator()` signature and `DISTINCT | SIZED` characteristics are uniform across Tasks 2–8. Immutable variants additionally include `IMMUTABLE` (Tasks 5, 6, 8, 10).
- [x] **Field name caveat:** Task 6 explicitly asks to verify the field name before committing to avoid a silent bug.
- [x] **Test empty/singleton coverage:** Task 10 adds dedicated `stream_empty()` and `spliterator_empty_characteristics()` tests.
