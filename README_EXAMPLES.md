<!--
  ~ Copyright (c) 2026 Goldman Sachs and others.
  ~ All rights reserved. This program and the accompanying materials
  ~ are made available under the terms of the Eclipse Public License v1.0
  ~ and Eclipse Distribution License v. 1.0 which accompany this distribution.
  ~ The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
  ~ and the Eclipse Distribution License is available at
  ~ http://www.eclipse.org/org/documents/edl-v10.php.
  -->
<a href="https://www.eclipse.org/collections/"><img src="https://github.com/eclipse/eclipse-collections/blob/master/artwork/eclipse-collections-logo.png" height="50%" width="50%"></a>

## Some Quick Code Examples

Eclipse Collections puts iteration methods directly on the container types. Here's several code examples that demonstrate the simple and flexible style of programming with Eclipse Collections.

First, we will define a record class named `Person` with a first and last name. We will add a `lastNameEquals` method to the record to be used as a `Predicate` in either a lambda or a method reference. Using `lastNameEquals`, which takes a String parameter, as a method reference would be impossible with Java `Stream` today. When you see the examples that follow, you will discover APIs in Eclipse Collections that make it possible to use methods with a single parameter as a method reference.

```java
public record Person(String firstName, String lastName)
{
    public boolean lastNameEquals(String name)
    {
        return name.equals(this.lastName);
    }
}
```

#### Example 1: `Collect` (aka `map`, `transform`)
First we will create a `MutableList` with three instances of the `Person` class.
```java
MutableList<Person> people = Lists.mutable.with(
        new Person("Sally", "Smith"),
        new Person("Ted", "Watson"),
        new Person("Mary", "Williams"));
```
Then we will `collect` their last names into a new `MutableList`, and finally output the names to a comma delimited String using `makeString`.
```java
MutableList<String> lastNames = people.collect(person -> person.lastName());
assertEquals("Smith, Watson, Williams", lastNames.makeString());
```
The lambda in the example above can also be replaced with a method reference.

```java
MutableList<String> lastNames = people.collect(Person::lastName);
```
Eclipse Collections has support for both [Mutable][MutableCollection] and [Immutable][ImmutableCollection] collections, and the return types of methods are covariant.  Here we use the same [Lists][Lists] factory to create an `ImmutableList`.
```java
ImmutableList<Person> people = Lists.immutable.with(
        new Person("Sally", "Smith"),
        new Person("Ted", "Watson"),
        new Person("Mary", "Williams"));
```
While the `collect` method on a `MutableList` returned a `MutableList`, the `collect` method on an `ImmutableList` will return an `ImmutableList`.
```java
ImmutableList<String> lastNames = people.collect(Person::lastName);
assertEquals("Smith, Watson, Williams", lastNames.makeString());
```

Eclipse Collections has a [lazy API][LazyIterable] as well, which is available by calling the method `asLazy`.  The method `collect` will now return a [`LazyIterable`][LazyIterable].  The `LazyIterable` that is returned does not evaluate anything until the call to a terminal method is made.  In this case, the call to `makeString` will force the `LazyIterable` to collect the last names.

```java
LazyIterable<String> lastNames = people.asLazy().collect(Person::lastName);
assertEquals("Smith, Watson, Williams", lastNames.makeString());
```
#### Example 2: `Select` / `Reject` (aka `filter` / `!filter`)
We can find all of the people with the last name "Smith" using the method named `select`.
```java
MutableList<Person> people = Lists.mutable.with(
        new Person("Sally", "Smith"),
        new Person("Ted", "Watson"),
        new Person("Mary", "Williams"));

MutableList<Person> smiths = people.select(person -> person.lastNameEquals("Smith"));
assertEquals("Smith", smiths.collect(Person::lastName).makeString());
```
If we want to use a method reference, we can use the method `selectWith`.
```java
MutableList<Person> smiths = people.selectWith(Person::lastNameEquals, "Smith");
assertEquals("Smith", smiths.collect(Person::lastName).makeString());
```
We can find all the people who do not have a last name of "Smith" using the method named `reject`.
```java
MutableList<Person> notSmiths = people.reject(person -> person.lastNameEquals("Smith"));
assertEquals("Watson, Williams", notSmiths.collect(Person::lastName).makeString());
```
If we want to use a method reference, we can use the method `rejectWith`.
```java
MutableList<Person> notSmiths = people.rejectWith(Person::lastNameEquals, "Smith");
assertEquals("Watson, Williams", notSmiths.collect(Person::lastName).makeString());
```

#### Example 3: `Any` / `All` / `None`
We can test whether any, all or none of the elements of a collection satisfy a given condition.
```java
// anySatisfy with lambda
assertTrue(people.anySatisfy(person -> person.lastNameEquals("Smith"));
// anySatisfyWith with method reference
assertTrue(people.anySatisfyWith(Person::lastNameEquals, "Smith"));

// allSatisfy with lambda
assertFalse(people.allSatisfy(person -> person.lastNameEquals("Smith"));
// allSatisfyWith with method reference
assertFalse(people.allSatisfyWith(Person::lastNameEquals, "Smith"));

// noneSatisfy with lambda
assertFalse(people.noneSatisfy(person -> person.lastNameEquals("Smith"));
// noneSatisfyWith with method reference
assertFalse(people.noneSatisfyWith(Person::lastNameEquals, "Smith"));
```

## [Back to README](./README.md)

[MutableCollection]: https://www.eclipse.org/collections/javadoc/11.1.0/org/eclipse/collections/api/collection/MutableCollection.html
[ImmutableCollection]: https://www.eclipse.org/collections/javadoc/11.1.0/org/eclipse/collections/api/collection/ImmutableCollection.html
[LazyIterable]: https://www.eclipse.org/collections/javadoc/11.1.0/org/eclipse/collections/api/LazyIterable.html
[Lists]: https://www.eclipse.org/collections/javadoc/11.1.0/org/eclipse/collections/api/factory/Lists.html
