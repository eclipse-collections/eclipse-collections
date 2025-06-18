12.0.0
====================

This is the 12.0.0 major release.

This release focused on migrating the development baseline from Java 8 to Java 11, migrating to JUnit 5, enhancing performance, adding new API methods, improving test coverage, and updating dependencies. The team has also made significant efforts to reduce technical debt and improve code quality.

The Eclipse Collections team gives a huge thank you to everyone who participated in this release.

# New Functionality
-----------------
* Added default toArray/toImmutableList/Set/Bag implementations to LazyIterable.
* Implemented MapIterable.collectKeysUnique(). Fixes #409.
* Implemented MutableOrderedMap.toImmutable() and add ImmutableOrderedMapAdapter.
* Implemented OrderedMapAdapter.flipUniqueValues().
* Implemented Stack.getLast().
* Implemented Stack.distinct(). 
* Added feature to reverse Interval ranges.
* Added withOccurrences and withoutOccurrences to MutableBag. 
* Implemented RichIterable.reduceBy(). Fixes #134.
* Added toStack to Ordered Primitive Iterable.
* Implemented Comparators.fromPredicate(), to create a Comparator from a Predicate2 that can answer isBefore.
* Implemented boxing wrappers for primitive sets. #1408.
* Implemented boxing wrappers for primitive lists. #1408.
* Exposed trimToSize() in HashBag implementations.
* Exposed trimToSize in all primitive maps and sets.
* Added removeIf to primitiveObject maps.
* Changed OrderedIterable.indexOf() to have a default implementation.
* Added missing empty with comparator methods. Fixes #1328.


# Optimizations
----------------------
* Optimized forEach method on Map views to not delegate to an iterator.
* Optimized Map.replaceAll() to not delegate to iterator.
* Optimized forEach method on Map views to not delegate to an iterator.
* Optimized putAll() method.
* Optimized ImmutableArrayList.takeWhile() and dropWhile() for small lists. Fixes #1640.
* Optimized clear() method of sub-lists. Fixes #1625.
* Optimized MutableList.subList().
* Optimized equals for primitive Bags using allSatisfyKeyValue on primitivePrimitive Maps.
* Added allSatisfyKeyValue method to Object/Primitive Maps to optimize HashBag equals method.
* Overrided Map.merge() default method for correctness and efficiency. Partially addresses #500.
* Optimized withAll on MutableXSetFactory and Immutable equivalent. Fixes #1374.
* Overrided Java 8 default method Map.forEach.
* Optimized any/all/noneSatisfy on UnifiedMapWithHashingStrategy. Fixes #1342.
* Optimized withAll for primitive bag factories. Fixes #1372.


# Tech Debt Reduction
---------------------
* Added more files to .idea/.gitignore.
* Used interfaces instead of implementations where possible without breaking backward compatibility.
* Formatted yaml files using Spotless.
* Synced .idea/compiler.xml.
* Fixed MapIterate.forEachKeyValue() to throw NullPointerException on null Map.
* Added missing @override annotations.
* Added suppress warnings to RichIterable for ClassReferencesSubclass.
* Fixed static analysis violations.
* Synced IntelliJ project files.
* Makeed UnifiedMapWithHashingStrategy more similar to UnifiedMap by implementing removeIf() and the detect*() methods.
* Added Feature #1568 - [OSGi] Opting in to Service Loader Mediator.
* Set .idea to linguist-generated=false in .gitattributes so that it shows up in code review diffs.
* Updated Checkstyle AvoidStaticImport to allow JUnit 5 static imports.
* Fixed IntelliJ code style settings.
* Configured CheckStyle check IllegalInstantiation to match the style of upstream configuration.
* Configured CheckStyle check HiddenField to match the style of upstream configuration.
* Turned on CheckStyle check RightCurly for additional tokens.
* Turned on CheckStyle check EmptyBlock for additional tokens.
* Turned on CheckStyle check AnnotationLocation for additional tokens.
* Ran rewrite.activeRecipes=org.openrewrite.java.RemoveUnusedImports.
* Ran org.openrewrite.java.UseStaticImport: methodPattern: org.junit.Assert *(..).
* Extracted interface MapTestCase above MutableMapIterableTestCase. 
* Replaced new Long() with Long.valueOf().
* Updated JMH Benchmarks and library dependencies.
* Simplified Iterate forEach and sort by calling Java 8 default methods.
* Fixed overflow issues in LongInterval. Fixes #1717.
* Fixed OrderedMapAdapter.groupByUniqueKey().
* Fixed NullPointerException in IterableTestCase.assertEquals.
* Fixed generics in map factories.
* Fixed return types of aggregateBy overrides.
* Added default overrides for getFirst and getLast in MutableList and MutableSortedSet to Fixes #1460.
* Fixed a type in mutation.yml. Closes #1440.
* Fixed a bug in addAllAtIndex method. Closes #1433.
* Turned on additional IntelliJ inspections and fix violations (Trivial else). #1323.
* Turned on additional IntelliJ inspections and fix violations (Trivial If). #1323.
* Turned on additional IntelliJ inspections and fix violations (Commented out code). #1323.
* Turned on additional IntelliJ inspections and fix violations (Method is identical to its super method). #1323.
* Added missing overrides in multi-reader interfaces.
* Refactored distinct to use select.
* Refactored FastList to use new InternalArrayIterate primitive collect methods. Fixes #1350.


# Documentation Changes
----------------------
* Added "Eclipse Collections Categorically" book to "Learn Eclipse Collections" section of README.
* Improved structural search templates, mostly for collection factories and assertions.
* Clarified Java version compatibility in README.
* Added method categories with emojis in RichIterable using region/endregion comments.
* Added method categories with emojis in RichIterable javadoc summary.
* Fixed incorrect Javadoc for SortedSetIterable.intersect() and SetIterable.intersect().
* Fixed typo in 3-Code_Blocks.adoc.
* Fixed bold markup typos in the reference guide.
* Fixed typo in Primitive set doc.
* Fixed Java version in CONTRIBUTING.MD
* Added NOTICE.md file.
* Added Javadoc to ConcurrentMutableMap.merge().
* Updated README.md compatibility table and add blog links.
* Fixed some Javadoc errors.
* Fixed all language links, their order, old website links and http links.
* Added missing Javadoc for Iterate.getOnly().
* Removed prompt from code blocks in CONTRIBUTING.MD.
* Removed a typo in ImmutableSet Javadoc.
* Removed anonymous inner class examples from RichIterable and Iterate JavaDoc.
* Updated 2-Collection_Containers.adoc file for adding IntInterval Documentation.
* Updated 2-Collection_Containers.adoc file for Primitive sets documentation.
* Fixed dead links in CONTRIBUTING.MD


# Build Changes
-----------------
* Added job forbid-merge-commits to .github/workflows/pull-request.yml.
* Replaced `mvn install` with `mvn verify` in GitHub workflows.
* Updated GitHub Actions builds to latest JDK versions.
* Moved bnd-maven-plugin to a profile.
* Added Spotless maven profiles.
* Configured maven-surefire-plugin printSummary to false, to stop logging successful tests.
* Added maven profiles and caching to speed up builds.
* Updated IntelliJ language level to 11.
* Added consistent use of `./mvnw --color=always` from GitHub workflows.
* Combined all the GitHub workflows for pull requests.
* Upgraded maven from 3.6.3 to 3.9.6.
* Added javadoc:jar step and Java 11 to Javadoc GitHub Action.
* Upgraded Maven wrapper.
* Removed sonar-maven-plugin. Fixes #1466.


# Build/Test Dependency Upgrades
-----------------
* Added dependency on junit-jupiter-api.
* Upgraded versions of JUnit to JUnit 5.
* Upgraded org.jacoco:jacoco-maven-plugin to 0.8.12.
* Upgraded biz.aQute.bnd:bnd-maven-plugin to 7.0.0.
* Upgraded net.alchim31.maven:scala-maven-plugin to 4.9.2.
* Upgraded org.apache.maven.plugins:maven-assembly-plugin to 3.7.1.
* Upgraded org.apache.maven.plugins:maven-source-plugin to 3.3.0.
* Upgraded org.apache.maven.plugins:maven-compiler-plugin to 3.13.0.
* Upgraded org.scala-lang:scala-library to 2.13.15.
* Upgraded ch.qos.logback:logback-classic to 1.5.8.
* Upgraded slf4j.version to 2.0.16.
* Upgraded jcstress-core to 0.16.
* Upgraded com.puppycrawl.tools:checkstyle to 10.18.2.
* Upgraded org.apache.maven:maven-core to 3.9.9.
* Upgraded org.apache.maven.plugins:maven-gpg-plugin to 3.2.7.
* Upgraded org.apache.maven.plugin-tools:maven-plugin-annotations to 3.15.0.
* Upgraded org.apache.maven:maven-plugin-api to 3.9.9.
* Upgraded org.apache.maven.plugins:maven-release-plugin to 3.1.1.
* Upgraded org.apache.maven.plugins:maven-project-info-reports-plugin to 3.7.0.
* Upgraded org.apache.maven.plugins:maven-javadoc-plugin to 3.10.0.
* Upgraded org.apache.maven.plugins:maven-checkstyle-plugin.
* Upgraded org.apache.maven.plugins:maven-enforcer-plugin to 3.5.0.
* Upgraded org.apache.maven.plugins:maven-install-plugin to 3.1.3.
* Upgraded org.apache.maven.plugins:maven-plugin-plugin to 3.15.0.
* Upgraded org.apache.maven.plugins:maven-surefire-plugin to 3.5.0.
* Upgraded org.codehaus.mojo:versions-maven-plugin to 2.17.1.
* Upgraded org.apache.maven.plugins:maven-deploy-plugin to 3.1.3.
* Upgraded org.apache.maven.plugins:maven-resources-plugin to 3.3.1.
* Upgraded actions/setup-java to 4.
* Upgraded actions/upload-artifact to 4.
* Upgraded actions/cache from to 4.0.0.
* Upgraded actions/checkout to 4.


# Test Improvements
---------------------
* Added tests for equals() and forEach() on keySet() and entrySet().
* Added tests Map_entrySet_setValue() and MutableMapIterable_entrySet_setValue().
* Stopped throwing mangled exceptions in Verify.
* Added test coverage for MutableBagIterable.
* Removed redundant tests from unit-tests that live in unit-tests-java8.
* Changed IterableTestCase.assertIterablesEqual to assert that the arguments are actually iterable.
* Added missing @Test annotations and fix broken assertion.
* Added better test coverage of List.subList().
* Added test coverage for Map.put() and Map.putAll().
* Improved Verify assertions to not rely on iterators.
* Deleted four flaky tests: Collectors2AdditionalTest.sumBy*Parallel().
* Added more assertions to IterableTestCase.assertIterablesEqual().
* Reduced duplication between tests for unmodifiable maps.
* Improve test coverage for the optimization of MutableList.subList().
* Removed @Test(expected) from tests and use assertThrows instead.
* Uplifted Junit syntax for UnmodifiableSortedBagTest.
* Fixed test setup in SortedMapAdapterTest which was accidentally setting up a MutableOrderedMap.
* Replaced usages of impl.factory.(Bags|Lists|Sets|Maps) with api.factory in unit-tests.
* Combined tests in unit-tests-java8 that cover the same method contract.
* Removed check for Java 21 in UnmodifiableMutableOrderedMapSerializationTest.


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
 <version>12.0.0</version>
</dependency>

<dependency>
 <groupId>org.eclipse.collections</groupId>
 <artifactId>eclipse-collections</artifactId>
 <version>12.0.0</version>
</dependency>

<dependency>
 <groupId>org.eclipse.collections</groupId>
 <artifactId>eclipse-collections-testutils</artifactId>
 <version>12.0.0</version>
 <scope>test</scope>
</dependency>

<dependency>
 <groupId>org.eclipse.collections</groupId>
 <artifactId>eclipse-collections-forkjoin</artifactId>
 <version>12.0.0</version>
</dependency>
```

### Gradle

```groovy
implementation 'org.eclipse.collections:eclipse-collections-api:12.0.0'
implementation 'org.eclipse.collections:eclipse-collections:12.0.0'
testImplementation 'org.eclipse.collections:eclipse-collections-testutils:12.0.0'
implementation 'org.eclipse.collections:eclipse-collections-forkjoin:12.0.0'
```

### Ivy

```xml
<dependency org="org.eclipse.collections" name="eclipse-collections-api" rev="12.0.0" />
<dependency org="org.eclipse.collections" name="eclipse-collections" rev="12.0.0" />
<dependency org="org.eclipse.collections" name="eclipse-collections-testutils" rev="12.0.0" />
<dependency org="org.eclipse.collections" name="eclipse-collections-forkjoin" rev="12.0.0"/>
```
