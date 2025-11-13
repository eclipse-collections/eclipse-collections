/**
 * A factory class providing convenient static access to create various
 * types of {@link org.eclipse.collections.api.set.SetIterable} and related set factories.
 * <p>
 * This class serves as the main entry point for creating instances of:
 * <ul>
 * <li>{@link org.eclipse.collections.api.set.MutableSet} (via {@link #mutable})</li>
 * <li>{@link org.eclipse.collections.api.set.ImmutableSet} (via {@link #immutable})</li>
 * <li>Fixed-size sets (via {@link #fixedSize})</li>
 * <li>Multi-reader sets (via {@link #multiReader})</li>
 * </ul>
 * <p>
 * The factories support creating empty sets, singleton sets, and pre-populated sets
 * with optimized data structures from the Eclipse Collections framework.
 * </p>
 *
 * Example Usage:
 * <pre>{@code
 * // Creating a mutable set
 * MutableSet<String> names = Sets.mutable.with("Alice", "Bob", "Charlie");
 *
 * // Creating an immutable set
 * ImmutableSet<Integer> numbers = Sets.immutable.of(1, 2, 3);
 * }</pre>
 *
 * @see org.eclipse.collections.api.factory.set.MutableSetFactory
 * @see org.eclipse.collections.api.factory.set.ImmutableSetFactory
 * @see org.eclipse.collections.api.factory.set.FixedSizeSetFactory
 * @see org.eclipse.collections.api.factory.set.MultiReaderSetFactory
 *
 * @since 1.0
 */
