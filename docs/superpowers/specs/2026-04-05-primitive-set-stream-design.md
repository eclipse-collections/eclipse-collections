# Design: primitiveStream() for IntSet, LongSet, DoubleSet

**Issue:** [#1454](https://github.com/eclipse-collections/eclipse-collections/issues/1454)
**Date:** 2026-04-05
**Version target:** 14.0.0

---

## Problem

Since Eclipse Collections 10.0, `IntList`, `LongList`, and `DoubleList` expose:
- `spliterator()` — abstract method
- `primitiveStream()` — default method (sequential)
- `primitiveParallelStream()` — default method (parallel)

The equivalent set types (`IntSet`, `LongSet`, `DoubleSet`) have no such methods, breaking parity and forcing users to convert to a list or box values to get a stream.

**Desired usage after fix:**
```java
Set<String> set = IntSets.immutable.of(1, 2, 3)
    .primitiveStream()
    .mapToObj(Integer::toString)
    .collect(Collectors.toSet());
// → Set.of("1", "2", "3")
```

---

## Scope

Only `int`, `long`, and `double` are supported — these are the three primitives with JDK-specialized stream types (`IntStream`, `LongStream`, `DoubleStream`). The existing `primitive.specializedStream` flag in the code generator already encodes this constraint and gates all additions.

`boolean`, `byte`, `char`, `short`, `float` are **out of scope** (no JDK specialized streams).

---

## Approach

Use **`toArray()` + `Spliterators.spliterator()`** (Approach A).

Rationale: matches the existing pattern used by `IntArrayList`/`LongArrayList`/`DoubleArrayList`. Simple, correct, easy to review. The one-time O(n) array copy on spliterator creation is acceptable — sets are unordered so there is no cheaper structural shortcut without a custom iterator.

---

## Spliterator Characteristics

Sets differ from lists in two ways:
- Add `DISTINCT` (no duplicate elements)
- Remove `ORDERED` (hash sets have no defined iteration order)

| Implementation | Characteristics |
|---|---|
| `IntHashSet` (mutable) | `DISTINCT \| SIZED` |
| `SynchronizedIntSet` | delegates to inner set |
| `UnmodifiableIntSet` | delegates to inner set |
| `ImmutableIntEmptySet` | `DISTINCT \| SIZED \| IMMUTABLE` |
| `ImmutableIntSingletonSet` | `DISTINCT \| SIZED \| IMMUTABLE` |
| `AbstractMutableIntKeySet` (map key set, mutable) | `DISTINCT \| SIZED` |
| `ImmutableIntMapKeySet` (map key set, immutable) | `DISTINCT \| SIZED \| IMMUTABLE` |
| `ImmutableIntIntMapKeySet` (same-type primitive map key set) | `DISTINCT \| SIZED \| IMMUTABLE` |

---

## Files to Change

All changes are in `.stg` template files. The code generator produces the final `.java` files at build time.

### API

| File | Change |
|---|---|
| `eclipse-collections-code-generator/src/main/resources/api/set/primitiveSet.stg` | Add `spliterator()` abstract + `primitiveStream()` + `primitiveParallelStream()` default methods inside `<if(primitive.specializedStream)>` guard. Add imports for `Spliterator`, `StreamSupport`, `<name>Stream`. |

### Mutable Set Implementations

| File | Change |
|---|---|
| `impl/set/mutable/primitiveHashSet.stg` | Implement `spliterator()` using `Spliterators.spliterator(this.toArray(), DISTINCT \| SIZED)` |
| `impl/set/mutable/synchronizedPrimitiveSet.stg` | Delegate `spliterator()` to inner set, marked `synchronized` |
| `impl/set/mutable/unmodifiablePrimitiveSet.stg` | Delegate `spliterator()` to inner set |

### Immutable Set Implementations

| File | Change |
|---|---|
| `impl/set/immutable/immutablePrimitiveEmptySet.stg` | Return `Spliterators.emptyIntSpliterator()` (or Long/Double equivalent) |
| `impl/set/immutable/immutablePrimitiveSingletonSet.stg` | Return `new <name>SingletonSpliterator(this.element1)` (reuse existing spliterator class) |

### Map Key Sets

| File | Change |
|---|---|
| `impl/map/abstractMutablePrimitiveKeySet.stg` | Implement `spliterator()` using `toArray()` with `DISTINCT \| SIZED` |
| `impl/map/mutable/immutablePrimitiveMapKeySet.stg` | Implement `spliterator()` using `toArray()` with `DISTINCT \| SIZED \| IMMUTABLE` |
| `impl/map/mutable/immutablePrimitivePrimitiveMapKeySet.stg` | Same as above |

### Tests

| File | Change |
|---|---|
| `test/set/mutable/abstractPrimitiveSetTestCase.stg` | Add `spliterator()`, `primitiveStream()`, `primitiveParallelStream()` tests inside `<if(primitive.specializedStream)>` |
| `test/set/immutable/abstractImmutablePrimitiveSetTestCase.stg` | Same, plus assert `IMMUTABLE` characteristic |

---

## Template Snippets

### API — `primitiveSet.stg` addition

```stg
<if(primitive.specializedStream)>
import java.util.Spliterator;
import java.util.stream.StreamSupport;
import java.util.stream.<name>Stream;
<endif>

// inside interface body:
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
```

### `primitiveHashSet.stg` addition

```stg
<if(primitive.specializedStream)>
    @Override
    public Spliterator.Of<name> spliterator()
    {
        return Spliterators.spliterator(this.toArray(), Spliterator.DISTINCT | Spliterator.SIZED);
    }
<endif>
```

### `immutablePrimitiveEmptySet.stg` addition

```stg
<if(primitive.specializedStream)>
    @Override
    public Spliterator.Of<name> spliterator()
    {
        return Spliterators.empty<name>Spliterator();
    }
<endif>
```

### `immutablePrimitiveSingletonSet.stg` addition

```stg
<if(primitive.specializedStream)>
    @Override
    public Spliterator.Of<name> spliterator()
    {
        return new <name>SingletonSpliterator(this.element1);
    }
<endif>
```

### `abstractMutablePrimitiveKeySet.stg` and immutable map key set additions

```stg
<if(primitive.specializedStream)>
    @Override
    public Spliterator.Of<name> spliterator()
    {
        return Spliterators.spliterator(this.toArray(), Spliterator.DISTINCT | Spliterator.SIZED);
    }
<endif>
```

---

## Test Strategy

Each test template addition covers three cases:

1. **`spliterator()` characteristics** — assert `DISTINCT` and `SIZED` are set; assert `IMMUTABLE` for immutable variants; assert `estimateSize()` equals collection size.
2. **`primitiveStream()` correctness** — stream count matches set size; all streamed elements pass `set::contains`.
3. **`primitiveParallelStream()` correctness** — parallel count matches set size.

Empty set and singleton set edge cases are covered by the existing immutable test cases once the spliterator is implemented.

---

## Generated Files (after build)

The following `.java` files will be regenerated automatically:

- `IntHashSet.java`, `LongHashSet.java`, `DoubleHashSet.java`
- `ImmutableIntEmptySet.java`, `ImmutableLongEmptySet.java`, `ImmutableDoubleEmptySet.java`
- `ImmutableIntSingletonSet.java`, `ImmutableLongSingletonSet.java`, `ImmutableDoubleSingletonSet.java`
- `AbstractMutableIntKeySet.java`, `AbstractMutableLongKeySet.java`, `AbstractMutableDoubleKeySet.java`
- `ImmutableIntMapKeySet.java`, `ImmutableLongMapKeySet.java`, `ImmutableDoubleMapKeySet.java`
- `ImmutableIntIntMapKeySet.java`, `ImmutableLongLongMapKeySet.java`, `ImmutableDoubleDoubleMapKeySet.java`
- Corresponding test classes for all the above

---

## Out of Scope

- `byte`, `short`, `char`, `float`, `boolean` set types — no JDK specialized streams exist for these.
- Custom zero-copy `Spliterator` implementation (not needed for correctness; can be a follow-up optimization).
- Sorted set variants (`IntTreeSet` etc.) — not part of this issue; sorted sets may warrant `SORTED` characteristic in a separate issue.
