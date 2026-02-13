13.0.0
====================

This is the 13.0.0 major release.

This release focused on migrating the development baseline from Java 11 to Java 17, and updating our builds for p2 site to publish directly to maven central.

The Eclipse Collections team gives a huge thank you to everyone who participated in this release.

# Documentation Changes
----------------------
* Improved mutablePrimitivePrimitiveMap's remove() and removeKey() Javadoc.
* Added iteration pattern examples for primitive stacks. Fixes #1386.

# Build Changes
-----------------
* Upgraded the minimum Java version to Java 17.
* Build a maven p2 site from the reactor content. Fixes #1411, #288
* Upgrade bnd plugin to 7.1.0.
* Publish p2 artifacts to maven central. Fixes #294 . Example [here](https://central.sonatype.com/artifact/org.eclipse.collections/p2-site)


# Bug Fixes
-----------------
* Fixed ConcurrentHashMap keySet(), values(), and entrySet() spliterators to ensure they are CONCURRENT and not SIZED. Fixes #1801.
* Fixed ConcurrentHashMapUnsafe keySet(), values(), and entrySet() spliterators to ensure they are CONCURRENT and not SIZED.



# Note
-------
_We have taken all the measures to ensure all features are captured in the release notes. 
However, release notes compilation is manual, so it is possible that a commit might be missed. 
For a comprehensive list of commits please go through the commit log._

Acquiring Eclipse Collections
-----------------------------

### Maven

```xml
<dependency>
    <groupId>org.eclipse.collections</groupId>
    <artifactId>eclipse-collections-api</artifactId>
    <version>13.0.0</version>
</dependency>

<dependency>
    <groupId>org.eclipse.collections</groupId>
    <artifactId>eclipse-collections</artifactId>
    <version>13.0.0</version>
</dependency>

<dependency>
    <groupId>org.eclipse.collections</groupId>
    <artifactId>eclipse-collections-testutils</artifactId>
    <version>13.0.0</version>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>org.eclipse.collections</groupId>
    <artifactId>p2-site</artifactId>
    <version>13.0.0</version>
</dependency>

<dependency>
    <groupId>org.eclipse.collections</groupId>
    <artifactId>eclipse-collections-forkjoin</artifactId>
    <version>13.0.0</version>
</dependency>
```

### Gradle

```groovy
implementation 'org.eclipse.collections:eclipse-collections-api:13.0.0'
implementation 'org.eclipse.collections:eclipse-collections:13.0.0'
testImplementation 'org.eclipse.collections:eclipse-collections-testutils:13.0.0'
implementation 'org.eclipse.collections:p2-site:13.0.0'
implementation 'org.eclipse.collections:eclipse-collections-forkjoin:13.0.0'
```

### Ivy

```xml
<dependency org="org.eclipse.collections" name="eclipse-collections-api" rev="13.0.0" />
<dependency org="org.eclipse.collections" name="eclipse-collections" rev="13.0.0" />
<dependency org="org.eclipse.collections" name="eclipse-collections-testutils" rev="13.0.0" />
<dependency org="org.eclipse.collections" name="p2-site" rev="13.0.0" />
<dependency org="org.eclipse.collections" name="eclipse-collections-forkjoin" rev="13.0.0"/>
```
