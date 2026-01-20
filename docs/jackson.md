<!--
  ~ Copyright (c) 2021 Goldman Sachs and others.
  ~ All rights reserved. This program and the accompanying materials
  ~ are made available under the terms of the Eclipse Public License v1.0
  ~ and Eclipse Distribution License v. 1.0 which accompany this distribution.
  ~ The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
  ~ and the Eclipse Distribution License is available at
  ~ http://www.eclipse.org/org/documents/edl-v10.php.
  -->
# Serializing Eclipse Collections with Jackson

Unfortunately, with new collection types comes incompatibility with normal serialization frameworks. [Jackson](https://github.com/FasterXML/jackson), arguably the most popular JSON serialization framework for Java, is unable to deserialize Eclipse Collections types out-of-the-box. For this purpose, there is now a [Jackson module](https://github.com/FasterXML/jackson-datatypes-collections/) supporting most Eclipse Collections types directly (including primitive collections).

In this article we will cover the basics of Eclipse Collections, Jackson and that module.

## Eclipse Collections

Eclipse Collections replaces many of the familiar container types from the standard library with new and improved versions. For this article, we will look at three variants of the `List` type:

```java
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.IntList;
import org.eclipse.collections.impl.factory.primitive.IntLists;

MutableList<String> mutable = Lists.mutable.of("foo", "bar");
ImmutableList<String> immutable = Lists.immutable.of("foo", "bar");
IntList intList = IntLists.immutable.of(1, 2, 3);
```

`MutableList` behaves a lot like `java.util.List`, but offers additional stream-like API we will not go into detail about here. Every `MutableList` also implements `java.util.List`, so you can pass any `MutableList` to APIs that still use the standard Java collections.

`ImmutableList` does not extend `java.util.List` and - as its name suggests - does not offer any methods for mutation such as `add` or `remove`. Instead you can create copies of these collections with elements added or removed. If you wish to use these collections with traditional `java.util.List` APIs you can use the `castToList()` method on `ImmutableList`, which will return an instance of `java.util.List` that will throw exceptions on mutation operations like `remove`.

`IntList` is a special type of list that offers lightweight APIs for working with primitives. Similar collection types exist for all other primitives. The problem with "normal" collections and primitives is that using a `List<Integer>` or similar type will always carry additional overhead both in terms of more memory use and more CPU time due to indirection and additional garbage collection load. Because of this, for performance-critical operations, a dedicated `IntList` type is superior.

Eclipse Collections is hardly the first library to provide primitive collections but combined with its handy API it makes for arguably the best library of its kind. Using Eclipse primitive collections you can have the performance of primitive arrays with the ease-of-use of the Eclipse Collections API.

For all the primitive collection types there also exist mutable and immutable versions (`MutableIntList`, `ImmutableIntList`). They behave much the same as their object-based counterparts so we will not cover them here.

Eclipse Collections is split into two parts: The API and the implementation of that API. Since the implementation depends on the API, we will only have to add that as a dependency to our Maven project. See the [latest release](https://github.com/eclipse/eclipse-collections/releases) for the details on the Maven or Gradle coordinates.

## Jackson

Jackson is a library for transforming your data structures from and to JSON and other serialized forms. According to [mvnrepository.com](https://mvnrepository.com/popular) it is the most popular framework for this purpose.

Jackson's architecture consists of three parts: A *core* that is responsible for writing streams of data (typically JSON), *databind* which is responsible for the basic object serialization API, and various Jackson modules that each handle serialization of some special types. Adding Jackson to our project is simple by using the Maven or Gradle coordinates for the latest release of [jackson-databind](https://search.maven.org/search?q=a:jackson-databind) and [jackson-datatype-eclipse-collections](https://search.maven.org/search?q=a:jackson-datatype-eclipse-collections).

To start out with Jackson we create an `ObjectMapper`:

```java
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.eclipsecollections.EclipseCollectionsModule;

ObjectMapper mapper = new ObjectMapper().registerModule(new EclipseCollectionsModule());
```

We are now ready to serialize data:

```java
public class MyClass {
    // The fields of this class must be public, or must have getters and
    // setters. For simplicity we will stick with public fields here.
    public String text;
    public int value;
}

MyClass object = new MyClass();
object.text = "foo";
object.value = 42;
String json = mapper.writeValueAsString(object);
// json = {"text":"foo","value":42}

MyClass deserialized = mapper.readValue(json, MyClass.class);
```

## Combining the two

Now we are ready to serialize our Eclipse Collections! Lets start with a simple example:

```java
public class EclipseClass {
    public ImmutableList<String> strings;
}

EclipseClass object = new EclipseClass();
object.strings = Lists.immutable.of("foo", "bar");
String json = mapper.writeValueAsString(object);
// json = {"strings":["foo","bar"]}

EclipseClass deserialized = mapper.readValue(json, EclipseClass.class);
```

As you can see, the list was serialized to a JSON array, as expected. Lets try with a primitive collection:

```java
public class EclipseClassInt {
    public IntList ints;
}

EclipseClassInt object = new EclipseClassInt();
object.ints = IntLists.immutable.of(1, 2, 3);
String json = mapper.writeValueAsString(object);
// json = {"ints":[1,2,3]}

EclipseClassInt deserialized = mapper.readValue(json, EclipseClassInt.class);
```

Using the `jackson-datatype-eclipse-collections` module primitive collections are treated just like normal collections.

### Serializing directly

Sometimes you do not want to write your own type wrapper but want to serialize collections directly. Because of generics, this takes some extra work to deserialize in Jackson:

```java
MutableList<String> mutable = Lists.mutable.of("foo", "bar");
String json = mapper.writeValueAsString(mutable);
// json = ["foo","bar"]

MutableList<String> deserialized = mapper.readValue(json, new TypeReference<MutableList<String>>() {});
```

Because Java does not have a syntax like `MutableList<String>.class`, we have to stick with the more convoluted Jackson type references. **Do not attempt to use `MutableList.class` instead - you will get an error that does not immediately make clear what the issue is.**

For primitive collections, because they do not use generics, using `.class` is possible:

```java
IntList intList = IntLists.immutable.of(1, 2, 3);
String json = mapper.writeValueAsString(intList);
// json = [1,2,3]

IntList deserialized = mapper.readValue(json, IntList.class);
```

### Compact serialization

The Eclipse Collections module for Jackson attempts to be consistent in the serialization of primitive collections with their array counterparts. `IntList` will serialize like an `int[]` would, `CharList` like a `char[]` would and so on. For `char` and `byte` this can make for a more compact representation. `ByteList` is serialized as base64 strings (or a binary type for non-JSON formats):

```java
ByteList byteList = ByteLists.immutable.of(new byte[]{ 73, 20, 32, 23 });
String json = mapper.writeValueAsString(byteList);
// json = "SRQgFw=="
```

Similarly, `CharList` will just be serialized to a JSON string:

```java
CharList charList = CharLists.immutable.of('f', 'o', 'o');
String json = mapper.writeValueAsString(charList);
// json = "foo"
```

For `CharList`, this feature can be turned off in Jackson:

```java
CharList charList = CharLists.immutable.of('f', 'o', 'o');
String json = mapper.writer(SerializationFeature.WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS)
        .writeValueAsString(charList);
// json = ["f","o","o"]
```
