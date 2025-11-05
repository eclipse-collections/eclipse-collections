/*
 * Copyright (c) 2021 The Bank of New York Mellon.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.factory.SortedSets;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.api.tuple.Triplet;
import org.eclipse.collections.api.tuple.Twin;
import org.eclipse.collections.impl.tuple.Tuples;
import org.eclipse.collections.impl.utility.ArrayIterate;
import org.eclipse.collections.impl.utility.StringIterate;

/**
 * A utility class for comparing the method signatures between two classes.
 * <p>
 * This class provides powerful tools for analyzing and comparing the public API of classes,
 * which is particularly useful for ensuring interface compatibility, tracking API changes,
 * and validating class hierarchies in test scenarios.
 * </p>
 *
 * <p><b>Comparison Criteria:</b></p>
 * <p>The comparison can be configured to include different method information:</p>
 * <ul>
 * <li>Method Name only</li>
 * <li>Method Name + Parameter Types</li>
 * <li>Method Name + Return Type</li>
 * <li>Method Name + Parameter Types + Return Type</li>
 * </ul>
 *
 * <p><b>Comparison Operations:</b></p>
 * <p>The following set operations are supported for comparing method signatures:</p>
 * <ul>
 * <li><b>Intersection</b> - Methods present in both classes</li>
 * <li><b>Difference</b> - Methods in one class but not the other</li>
 * <li><b>Symmetric Difference</b> - Methods in either class but not both</li>
 * <li><b>isProperSubsetOf</b> - Check if one class's methods are a proper subset of another</li>
 * <li><b>isProperSupersetOf</b> - Check if one class's methods are a proper superset of another</li>
 * </ul>
 *
 * @see #compare(Class, Class)
 * @see #difference(Class, Class)
 * @see #intersect(Class, Class)
 * @see #symmetricDifference(Class, Class)
 */
public class ClassComparer
{
    private static final String SYMMETRIC_DIFFERENCE = "Symmetric Difference";
    private static final String INTERSECTION = "Intersection";
    private static final String DIFFERENCE = "Difference";
    private final boolean includeParameterTypesInMethods;
    private final boolean includeReturnTypes;
    private final boolean includePackageNames;
    private boolean includeObjectMethods = false;
    private final Appendable appendable;

    /**
     * Creates a new ClassComparer with default settings.
     * Methods are compared by name only, without parameter types, return types, or package names.
     * Output is written to System.out.
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * // Create a basic comparer
     * ClassComparer comparer = new ClassComparer();
     * MutableSortedSet<String> methods = comparer.getMethodNames(ArrayList.class);
     * Assert.assertTrue(methods.contains("add"));
     * }</pre>
     */
    public ClassComparer()
    {
        this(false, false, false);
    }

    /**
     * Creates a new ClassComparer with default comparison settings and custom output destination.
     * Methods are compared by name only, without parameter types, return types, or package names.
     *
     * @param out the Appendable to write comparison output to (e.g., StringBuilder, StringBuffer)
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * // Create comparer with custom output
     * StringBuilder output = new StringBuilder();
     * ClassComparer comparer = new ClassComparer(output);
     * comparer.printClass(String.class);
     * Assert.assertTrue(output.length() > 0);
     * }</pre>
     */
    public ClassComparer(Appendable out)
    {
        this(out, false, false, false);
    }

    /**
     * Creates a new ClassComparer with custom comparison settings.
     * Output is written to System.out.
     *
     * @param includeParameterTypesInMethods if true, include parameter types in method signatures
     * @param includeReturnTypes             if true, include return types in method signatures
     * @param includePackageNames            if true, include package names in class names
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * // Create comparer with detailed method signatures
     * ClassComparer detailedComparer = new ClassComparer(true, true, false);
     * MutableSortedSet<String> methods = detailedComparer.getMethodNames(List.class);
     * // Methods will include both parameters and return types
     * Assert.assertTrue(methods.anySatisfy(m -> m.contains("(") && m.contains(":")));
     * }</pre>
     */
    public ClassComparer(boolean includeParameterTypesInMethods, boolean includeReturnTypes, boolean includePackageNames)
    {
        this(System.out, includeParameterTypesInMethods, includeReturnTypes, includePackageNames);
    }

    /**
     * Creates a new ClassComparer with full customization of comparison settings and output.
     *
     * @param out                            the Appendable to write comparison output to
     * @param includeParameterTypesInMethods if true, include parameter types in method signatures
     * @param includeReturnTypes             if true, include return types in method signatures
     * @param includePackageNames            if true, include package names in class names
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * // Create fully customized comparer
     * StringBuilder output = new StringBuilder();
     * ClassComparer comparer = new ClassComparer(output, true, true, true);
     * comparer.compareAndPrint(List.class, Collection.class);
     * Assert.assertTrue(output.toString().contains("java.util"));
     * }</pre>
     */
    public ClassComparer(
            Appendable out,
            boolean includeParameterTypesInMethods,
            boolean includeReturnTypes,
            boolean includePackageNames)
    {
        this.includeParameterTypesInMethods = includeParameterTypesInMethods;
        this.includeReturnTypes = includeReturnTypes;
        this.includePackageNames = includePackageNames;
        this.appendable = out;
    }

    /**
     * Checks if the superset class's methods are a proper superset of the subset class's methods.
     * A proper superset means the superset contains all methods of the subset, plus additional methods.
     * This comparison includes all public methods (both static and instance).
     *
     * @param supersetClass the class expected to have more methods
     * @param subsetClass   the class expected to have fewer methods
     * @return true if supersetClass methods are a proper superset of subsetClass methods
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * // Verify interface hierarchy
     * Assert.assertTrue(ClassComparer.isProperSupersetOf(Collection.class, Set.class));
     *
     * // Check that a concrete class has more methods than its interface
     * Assert.assertTrue(ClassComparer.isProperSupersetOf(ArrayList.class, List.class));
     * }</pre>
     */
    public static boolean isProperSupersetOf(Class<?> supersetClass, Class<?> subsetClass)
    {
        return ClassComparer.isProperSubsetOf(subsetClass, supersetClass);
    }

    /**
     * Checks if the subset class's methods are a proper subset of the superset class's methods.
     * A proper subset means the subset's methods are all contained in the superset, but the superset has additional methods.
     * This comparison includes all public methods (both static and instance).
     *
     * @param subsetClass   the class expected to have fewer methods
     * @param supersetClass the class expected to have more methods
     * @return true if subsetClass methods are a proper subset of supersetClass methods
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * // Verify that Set is a proper subset of Collection
     * Assert.assertTrue(ClassComparer.isProperSubsetOf(Set.class, Collection.class));
     *
     * // Check interface vs implementation
     * Assert.assertTrue(ClassComparer.isProperSubsetOf(List.class, ArrayList.class));
     * }</pre>
     */
    public static boolean isProperSubsetOf(Class<?> subsetClass, Class<?> supersetClass)
    {
        ClassComparer comparer = new ClassComparer(true, true, true);
        return comparer.getMethodNames(subsetClass).isProperSubsetOf(comparer.getMethodNames(supersetClass));
    }

    /**
     * Checks if the superset class's instance methods are a proper superset of the subset class's instance methods.
     * This comparison excludes static methods and only considers instance methods.
     *
     * @param supersetClass the class expected to have more instance methods
     * @param subsetClass   the class expected to have fewer instance methods
     * @return true if supersetClass instance methods are a proper superset of subsetClass instance methods
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * // Compare instance methods only
     * Assert.assertTrue(ClassComparer.isProperSupersetOfInstance(ArrayList.class, AbstractList.class));
     * }</pre>
     */
    public static boolean isProperSupersetOfInstance(Class<?> supersetClass, Class<?> subsetClass)
    {
        return ClassComparer.isProperSubsetOfInstance(subsetClass, supersetClass);
    }

    /**
     * Checks if the subset class's instance methods are a proper subset of the superset class's instance methods.
     * This comparison excludes static methods and only considers instance methods.
     *
     * @param subsetClass   the class expected to have fewer instance methods
     * @param supersetClass the class expected to have more instance methods
     * @return true if subsetClass instance methods are a proper subset of supersetClass instance methods
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * // Compare instance methods only
     * Assert.assertTrue(ClassComparer.isProperSubsetOfInstance(AbstractList.class, ArrayList.class));
     * }</pre>
     */
    public static boolean isProperSubsetOfInstance(Class<?> subsetClass, Class<?> supersetClass)
    {
        ClassComparer comparer = new ClassComparer(true, true, true);
        return comparer.getInstanceMethodNames(subsetClass).isProperSubsetOf(comparer.getInstanceMethodNames(supersetClass));
    }

    /**
     * Compares two classes and returns their method signature intersection and differences.
     * The result is a triplet containing: (intersection, difference-left, difference-right).
     *
     * @param leftClass  the first class to compare
     * @param rightClass the second class to compare
     * @return a Triplet containing intersection, methods only in leftClass, and methods only in rightClass
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * // Compare two classes
     * ClassComparer comparer = new ClassComparer();
     * Triplet<MutableSortedSet<String>> result = comparer.compare(ArrayList.class, LinkedList.class);
     * MutableSortedSet<String> common = result.getOne();
     * MutableSortedSet<String> onlyInArrayList = result.getTwo();
     * MutableSortedSet<String> onlyInLinkedList = result.getThree();
     * Assert.assertTrue(common.contains("add"));
     * }</pre>
     */
    public Triplet<MutableSortedSet<String>> compare(Class<?> leftClass, Class<?> rightClass)
    {
        MutableSortedSet<String> intersection = this.intersect(leftClass, rightClass);
        MutableSortedSet<String> differenceLeft = this.difference(leftClass, rightClass);
        MutableSortedSet<String> differenceRight = this.difference(rightClass, leftClass);
        return Tuples.triplet(intersection, differenceLeft, differenceRight);
    }

    /**
     * Returns the method signatures present in the left class but not in the right class.
     * This is the set difference operation (left - right).
     *
     * @param leftClass  the class whose unique methods to find
     * @param rightClass the class to compare against
     * @return a sorted set of method signatures present only in leftClass
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * // Find methods unique to ArrayList
     * ClassComparer comparer = new ClassComparer();
     * MutableSortedSet<String> uniqueMethods = comparer.difference(ArrayList.class, AbstractList.class);
     * Assert.assertTrue(uniqueMethods.notEmpty());
     * }</pre>
     */
    public MutableSortedSet<String> difference(Class<?> leftClass, Class<?> rightClass)
    {
        MutableSortedSet<String> leftMethods = this.getMethodNames(leftClass);
        MutableSortedSet<String> rightMethods = this.getMethodNames(rightClass);
        return leftMethods.difference(rightMethods);
    }

    /**
     * Computes and prints the method signatures present in the left class but not in the right class.
     * Output is written to the configured Appendable.
     *
     * @param leftClass  the class whose unique methods to find
     * @param rightClass the class to compare against
     * @return a sorted set of method signatures present only in leftClass
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * // Print methods unique to HashMap
     * StringBuilder output = new StringBuilder();
     * ClassComparer comparer = new ClassComparer(output);
     * comparer.printDifference(HashMap.class, Map.class);
     * Assert.assertTrue(output.length() > 0);
     * }</pre>
     */
    public MutableSortedSet<String> printDifference(Class<?> leftClass, Class<?> rightClass)
    {
        MutableSortedSet<String> difference = this.difference(leftClass, rightClass);
        this.output(Tuples.twin(leftClass, rightClass), DIFFERENCE, difference);
        return difference;
    }

    /**
     * Returns the symmetric difference of method signatures between two classes.
     * The symmetric difference contains methods present in either class but not both.
     *
     * @param leftClass  the first class to compare
     * @param rightClass the second class to compare
     * @return a sorted set of method signatures present in either class but not both
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * // Find methods that differ between two implementations
     * ClassComparer comparer = new ClassComparer();
     * MutableSortedSet<String> diff = comparer.symmetricDifference(ArrayList.class, LinkedList.class);
     * // Contains methods unique to either ArrayList or LinkedList
     * }</pre>
     */
    public MutableSortedSet<String> symmetricDifference(Class<?> leftClass, Class<?> rightClass)
    {
        MutableSortedSet<String> leftMethods = this.getMethodNames(leftClass);
        MutableSortedSet<String> rightMethods = this.getMethodNames(rightClass);
        return leftMethods.symmetricDifference(rightMethods);
    }

    /**
     * Computes and prints the symmetric difference of method signatures between two classes.
     * Output is written to the configured Appendable.
     *
     * @param leftClass  the first class to compare
     * @param rightClass the second class to compare
     * @return a sorted set of method signatures present in either class but not both
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * // Print symmetric difference
     * StringBuilder output = new StringBuilder();
     * ClassComparer comparer = new ClassComparer(output);
     * comparer.printSymmetricDifference(HashSet.class, TreeSet.class);
     * Assert.assertTrue(output.toString().contains("Symmetric Difference"));
     * }</pre>
     */
    public MutableSortedSet<String> printSymmetricDifference(Class<?> leftClass, Class<?> rightClass)
    {
        MutableSortedSet<String> symmetricDifference = this.symmetricDifference(leftClass, rightClass);
        this.output(Tuples.twin(leftClass, rightClass), SYMMETRIC_DIFFERENCE, symmetricDifference);
        return symmetricDifference;
    }

    /**
     * Returns the intersection of method signatures between two classes.
     * The intersection contains methods present in both classes.
     *
     * @param leftClass  the first class to compare
     * @param rightClass the second class to compare
     * @return a sorted set of method signatures present in both classes
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * // Find common methods
     * ClassComparer comparer = new ClassComparer();
     * MutableSortedSet<String> common = comparer.intersect(List.class, Collection.class);
     * Assert.assertTrue(common.contains("add"));
     * Assert.assertTrue(common.contains("size"));
     * }</pre>
     */
    public MutableSortedSet<String> intersect(Class<?> leftClass, Class<?> rightClass)
    {
        MutableSortedSet<String> leftMethods = this.getMethodNames(leftClass);
        MutableSortedSet<String> rightMethods = this.getMethodNames(rightClass);
        return leftMethods.intersect(rightMethods);
    }

    /**
     * Computes and prints the intersection of method signatures between two classes.
     * Output is written to the configured Appendable.
     *
     * @param leftClass  the first class to compare
     * @param rightClass the second class to compare
     * @return a sorted set of method signatures present in both classes
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * // Print common methods
     * StringBuilder output = new StringBuilder();
     * ClassComparer comparer = new ClassComparer(output);
     * comparer.printIntersection(ArrayList.class, LinkedList.class);
     * Assert.assertTrue(output.toString().contains("Intersection"));
     * }</pre>
     */
    public MutableSortedSet<String> printIntersection(Class<?> leftClass, Class<?> rightClass)
    {
        MutableSortedSet<String> intersect = this.intersect(leftClass, rightClass);
        this.output(Tuples.twin(leftClass, rightClass), INTERSECTION, intersect);
        return intersect;
    }

    /**
     * Retrieves and prints all public method signatures for the given class.
     * Output is written to the configured Appendable.
     *
     * @param clazz the class whose methods to print
     * @return a sorted set of method signatures from the class
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * // Print all methods of a class
     * StringBuilder output = new StringBuilder();
     * ClassComparer comparer = new ClassComparer(output);
     * MutableSortedSet<String> methods = comparer.printClass(String.class);
     * Assert.assertTrue(methods.contains("charAt"));
     * Assert.assertTrue(output.toString().contains("Class: String"));
     * }</pre>
     */
    public MutableSortedSet<String> printClass(Class<?> clazz)
    {
        MutableSortedSet<String> methodNames = this.getMethodNames(clazz);
        this.outputTitle("Class: " + (this.includePackageNames ? clazz.getName() : clazz.getSimpleName()));
        this.outputGroupByToString(methodNames);
        return methodNames;
    }

    /**
     * Compares two classes and prints their intersection and both differences.
     * This combines the functionality of compare() with formatted output.
     *
     * @param leftClass  the first class to compare
     * @param rightClass the second class to compare
     * @return a Triplet containing intersection, methods only in leftClass, and methods only in rightClass
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * // Compare and print detailed analysis
     * StringBuilder output = new StringBuilder();
     * ClassComparer comparer = new ClassComparer(output);
     * Triplet<MutableSortedSet<String>> result = comparer.compareAndPrint(Map.class, HashMap.class);
     * Assert.assertTrue(output.toString().contains("Intersection"));
     * Assert.assertTrue(output.toString().contains("Difference"));
     * }</pre>
     */
    public Triplet<MutableSortedSet<String>> compareAndPrint(Class<?> leftClass, Class<?> rightClass)
    {
        Triplet<MutableSortedSet<String>> compare = this.compare(leftClass, rightClass);
        Twin<Class<?>> classPair = Tuples.twin(leftClass, rightClass);
        this.output(classPair, INTERSECTION, compare.getOne());
        this.output(classPair, DIFFERENCE, compare.getTwo());
        this.output(Tuples.twin(rightClass, leftClass), DIFFERENCE, compare.getThree());
        return compare;
    }

    private void output(Twin<Class<?>> classPair, String operation, RichIterable<String> strings)
    {
        this.outputTitle(operation + " (" + this.classNames(classPair) + ")");
        this.outputGroupByToString(strings);
    }

    private String classNames(Twin<Class<?>> classPair)
    {
        Class<?> left = classPair.getOne();
        Class<?> right = classPair.getTwo();
        return (this.includePackageNames ? left.getName() : left.getSimpleName())
                + ", "
                + (this.includePackageNames ? right.getName() : right.getSimpleName());
    }

    /**
     * Retrieves all public method signatures for the given class.
     * The method signatures are formatted according to the comparer's configuration
     * (with or without parameter types, return types, and package names).
     *
     * @param classOne the class whose method signatures to retrieve
     * @return a sorted set of method signature strings
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * // Get method names only
     * ClassComparer simpleComparer = new ClassComparer();
     * MutableSortedSet<String> methods = simpleComparer.getMethodNames(List.class);
     * Assert.assertTrue(methods.contains("add"));
     * Assert.assertTrue(methods.contains("size"));
     *
     * // Get detailed method signatures with parameters and return types
     * ClassComparer detailedComparer = new ClassComparer(true, true, false);
     * MutableSortedSet<String> detailed = detailedComparer.getMethodNames(List.class);
     * // Will contain entries like "add(Object):boolean"
     * }</pre>
     */
    public MutableSortedSet<String> getMethodNames(Class<?> classOne)
    {
        return ArrayIterate.collectIf(classOne.getMethods(), this::includeMethod, this::methodName, SortedSets.mutable.empty());
    }

    /**
     * Retrieves all public instance (non-static) method signatures for the given class.
     * Static methods are excluded from the result. The method signatures are formatted
     * according to the comparer's configuration.
     *
     * @param classOne the class whose instance method signatures to retrieve
     * @return a sorted set of instance method signature strings
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * // Get only instance methods
     * ClassComparer comparer = new ClassComparer();
     * MutableSortedSet<String> instanceMethods = comparer.getInstanceMethodNames(Collections.class);
     * // Will not include static methods like "sort" or "reverse"
     *
     * // Useful for comparing object behavior vs. utility methods
     * MutableSortedSet<String> arrayListInstance = comparer.getInstanceMethodNames(ArrayList.class);
     * Assert.assertTrue(arrayListInstance.contains("add"));
     * }</pre>
     */
    public MutableSortedSet<String> getInstanceMethodNames(Class<?> classOne)
    {
        Method[] instanceMethods = Arrays.stream(classOne.getMethods()).filter(method -> !Modifier.isStatic(method.getModifiers())).toArray(Method[]::new);
        return ArrayIterate.collectIf(instanceMethods, this::includeMethod, this::methodName, SortedSets.mutable.empty());
    }

    /**
     * Configures this comparer to include methods inherited from {@link Object} in comparisons.
     * By default, Object methods (toString, equals, hashCode, etc.) are excluded.
     * Calling this method will include them in subsequent method retrieval and comparison operations.
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * // Include Object methods in comparison
     * ClassComparer comparer = new ClassComparer();
     * comparer.includeObjectMethods();
     * MutableSortedSet<String> methods = comparer.getMethodNames(String.class);
     * Assert.assertTrue(methods.contains("toString"));
     * Assert.assertTrue(methods.contains("equals"));
     * Assert.assertTrue(methods.contains("hashCode"));
     * }</pre>
     */
    public void includeObjectMethods()
    {
        this.includeObjectMethods = true;
    }

    private boolean includeMethod(Method method)
    {
        return this.includeObjectMethods || !method.getDeclaringClass().equals(Object.class);
    }

    private String methodName(Method method)
    {
        String methodNamePlusParams = this.includeParameterTypesInMethods
                ? method.getName() + "(" + this.parameterNames(method) + ")"
                : method.getName();
        return methodNamePlusParams + (this.includeReturnTypes ? ":" + method.getReturnType().getSimpleName() : "");
    }

    private String parameterNames(Method method)
    {
        return ArrayIterate.collect(method.getParameters(), parm -> parm.getType().getSimpleName())
                .makeString();
    }

    private void outputTitle(String title)
    {
        try
        {
            this.appendable.append(title).append(System.lineSeparator());
            this.appendable.append(StringIterate.repeat('-', title.length())).append(System.lineSeparator());
        }
        catch (IOException e)
        {
            throw new RuntimeException("MethodComparer error in outputTitle", e);
        }
    }

    private void outputGroupByToString(RichIterable<String> methods)
    {
        String output = methods.groupBy(string -> string.charAt(0))
                .keyMultiValuePairsView()
                .toSortedListBy(Pair::getOne)
                .makeString(System.lineSeparator());

        try
        {
            this.appendable.append(output).append(System.lineSeparator());
            this.appendable.append(System.lineSeparator());
        }
        catch (IOException e)
        {
            throw new RuntimeException("MethodComparer error in outputGroupByToString", e);
        }
    }
}
