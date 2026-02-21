<!--
  ~ Copyright (c) 2026 Goldman Sachs and others.
  ~ All rights reserved. This program and the accompanying materials
  ~ are made available under the terms of the Eclipse Public License v1.0
  ~ and Eclipse Distribution License v. 1.0 which accompany this distribution.
  ~ The Eclipse Public License is available at https://www.eclipse.org/legal/epl-v10.html
  ~ and the Eclipse Distribution License is available at
  ~ https://www.eclipse.org/org/documents/edl-v10.php.
  -->
[![][maven img]][maven]
[![][release img]][release]
[![][javadoc api img]][javadoc api]
[![][javadoc impl img]][javadoc impl]

[![][license-epl img]][license-epl]
[![][license-edl img]][license-edl]
[![][snyk-badge img]][snyk-badge]

[![][actions unit-tests img]][actions unit-tests]
[![][actions acceptance-tests img]][actions acceptance-tests]
[![][actions performance-tests img]][actions performance-tests]
[![][actions checkstyle img]][actions checkstyle]
[![][actions javadoc img]][actions javadoc]

<a href="https://eclipse.dev/collections/"><img src="https://github.com/eclipse-collections/eclipse-collections/blob/master/artwork/eclipse-collections-logo.png" height="50%" width="50%"></a>

#### [English](https://eclipse.dev/collections/) | [Deutsch](https://eclipse.dev/collections/de/index.html) | [Ελληνικά](https://eclipse.dev/collections/el/index.html) | [Español](https://eclipse.dev/collections/es/index.html) | [中文](https://eclipse.dev/collections/cn/index.html) | [Français](https://eclipse.dev/collections/fr/index.html) | [日本語](https://eclipse.dev/collections/ja/index.html) | [Norsk (bokmål)](https://eclipse.dev/collections/no-nb/index.html) | [Português-Brasil](https://eclipse.dev/collections/pt-br/index.html) | [Русский](https://eclipse.dev/collections/ru/index.html) | [हिंदी](https://eclipse.dev/collections/hi/index.html) | [Srpski (latinica)](https://eclipse.dev/collections/sr-rs-latn/index.html)
Eclipse Collections is a comprehensive collections library for Java. The library enables productivity and performance by delivering an expressive and efficient set of APIs and types. The iteration protocol was inspired by the Smalltalk collection framework, and the collections are compatible with the Java Collection Framework types.

The current GA release of Eclipse Collections (13.0.0) is compatible with Java 17+. Eclipse Collections 12.x is compatible with Java 11+, for projects that are not ready to upgrade to Java 17. Eclipse Collections is a part of the OpenJDK [Quality Outreach](https://wiki.openjdk.java.net/display/quality/Quality+Outreach) program, and it is validated for different versions of the OpenJDK.

## Why Eclipse Collections?

* Productivity
  * Supports _eager_, _lazy_, _serial_ and _parallel_ iteration patterns
  * [Rich][RichIterable], functional, and fluent APIs with eager methods available directly on collection types
  * Provides [`List`][ListIterable], [`Set`][SetIterable], [`Bag`][Bag], [`Stack`][StackIterable], [`Map`][MapIterable], [`Multimap`][Multimap], [`BiMap`][BiMap], [`Interval`][Interval] object container types
  * [Readable][RichIterable], [`Mutable`][MutableCollection], and [`Immutable`][ImmutableCollection] interfaces for each collection type with covariant return types
    * [Blog: Rich, Lazy, Mutable, Immutable interfaces in Eclipse collections][BlogRichLazyMutableImmutable]
  * Mutable and Immutable Collection [Factories][Factories]
    * [Blog series: As a matter of Factory][BlogAsAMatterOfFactory]
  * [Adapters][Adapters] and [Utility][Utilities] classes for JCF Types
    * [Blog: Iterate over any Iterable in Java][BlogIterateOverAnyIterableInJava]
* Performance
    * Memory Efficient Containers
      * [Blog: UnifiedMap: How it works?][BlogUnifiedMapHowItWorks]
      * [Blog: UnifiedSet - The Memory Saver][BlogUnifiedSetTheMemorySaver]
    * Optimized Eager, [`Lazy`][LazyIterable] and [`Parallel`][ParallelIterable] APIs
      * [Blog: The unparalleled design of Eclipse Collections][BlogUnparalleledDesignOfEclipseCollections]
    * [Primitive][PrimitiveIterable] Collections for all primitive types
      * Provides `List`, `Set`, `Bag`, `Stack`, `Map`, `Interval` primitive container types
* Maturity
    * Eclipse Collections has been actively developed and used in financial services applications since 2004
    * Eclipse Collections existed for a decade before concise lambda expressions were added in Java 8
      * [Blog: My ten-year quest for concise lambda expressions in Java][BlogLambdaExpressionsInJava]

## Learn Eclipse Collections

* **Book**: [Eclipse Collections Categorically: Level up your programming game](https://donraab.medium.com/book-eclipse-collections-categorically-37591b2135f5?source=friends_link&sk=03cc75fec4052e377444ede49d6c55b2)
* Blog Series: [Getting Started with Eclipse Collections][BlogGettingStartedWithEclipseCollections]
* Blog Series: [The missing Java data structures no one ever told you about][BlogTheMissingJavaDataStructures]
* Blog: [Java has Streams. Do we need third-party collections?][BlogJavaHasStreamsDoWeNeedCollections]
* [Some Quick Code Examples](./README_EXAMPLES.md)
* [Eclipse Collections Katas](https://github.com/eclipse-collections/eclipse-collections-kata), a fun way to help you learn idiomatic Eclipse Collections usage.
    * Start Here - [Pet Kata](https://eclipse-collections.github.io/eclipse-collections-kata/pet-kata/#/)
    * Continue Here - [Company Kata](https://eclipse-collections.github.io/eclipse-collections-kata/company-kata/#/)
* [Eclipse Collections Reference Guide](https://github.com/eclipse-collections/eclipse-collections/blob/master/docs/0-RefGuide.adoc) and [Javadoc](https://eclipse.dev/collections/javadoc/11.1.0/index.html)
* [Serializing Eclipse Collections with Jackson](./docs/jackson.md)
* [Articles](https://github.com/eclipse-collections/eclipse-collections/wiki/Articles) and [Blogs](https://medium.com/tag/eclipse-collections/latest)
* Some OSS projects that use Eclipse Collections
  * [Neo4J](https://github.com/neo4j/neo4j), [FINOS Legend](https://github.com/finos/legend-pure), [Reladomo](https://github.com/goldmansachs/reladomo), [Liftwizard](https://github.com/motlin/liftwizard), [Exchange Core](https://github.com/mzheravin/exchange-core), [Dataframe EC](https://github.com/vmzakharov/dataframe-ec), [MapDB](https://github.com/jankotek/mapdb), [Code Browser](https://github.com/yawkat/code-browser), [Obevo](https://github.com/goldmansachs/obevo), [BNY Mellon Code Katas](https://github.com/BNYMellon/CodeKatas), [Eclipse Nebula NatTable](https://www.eclipse.org/nattable/index.php), [Eclipse VIATRA](https://github.com/viatra/org.eclipse.viatra), [Jackson Datatypes Collections](https://github.com/FasterXML/jackson-datatypes-collections)
  * If you work on an open source project that uses Eclipse Collections, let us know!

## Eclipse Collections and JDK Compatibility Matrix

| EC     | JDK 5 - 7 | JDK 8   | JDK 9 - 10 | JDK 11 - 14 | JDK 15 - 16 | JDK 17 - 25 
|--------|-----------|---------|------------|-------------|-------------|-------------
| 7.x.x  | &check;   | &check; |            |             |             |
| 8.x.x  |           | &check; |            |             |             |
| 9.x.x  |           | &check; | &check;    | &check;     |             |
| 10.x.x |           | &check; | &check;    | &check;     |             |
| 10.4.0 |           | &check; | &check;    | &check;     | &check;     | &check;
| 11.x.x |           | &check; | &check;    | &check;     | &check;     | &check;
| 12.x.x |           |         |            | &check;     | &check;     | &check;
| 13.x.x |           |         |            |             |             | &check;

**Please Note:** 
1. Eclipse Collections 13.x requires minimum Java 17 and is compatible with Java 17+.
2. Eclipse Collections 12.x requires minimum Java 11 and is compatible with Java 11+. 


## Acquiring Eclipse Collections

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
```

### Gradle

```groovy
implementation 'org.eclipse.collections:eclipse-collections-api:13.0.0'
implementation 'org.eclipse.collections:eclipse-collections:13.0.0'
```

### OSGi Bundle
- p2 update site: https://download.eclipse.org/tools/orbit/simrel/orbit-aggregation/nightly/latest/
- Maven: https://central.sonatype.com/artifact/org.eclipse.collections/p2-site

## How to Contribute

We welcome contributions! We accept contributions via pull requests here in GitHub. Please see [How To Contribute](CONTRIBUTING.md) to get started.


## Additional information

* Project Website: https://eclipse.dev/collections
* Eclipse PMI: https://projects.eclipse.org/projects/technology.collections
* StackOverflow: https://stackoverflow.com/questions/tagged/eclipse-collections
* Mailing lists: https://dev.eclipse.org/mailman/listinfo/collections-dev
* Forum: https://www.eclipse.org/forums/index.php?t=thread&frm_id=329
* Working with GitHub: https://github.com/eclipse-collections/eclipse-collections/wiki/Working-with-GitHub

[actions acceptance-tests]:https://github.com/eclipse-collections/eclipse-collections/actions?query=workflow%3A%22Acceptance+Tests%22
[actions acceptance-tests img]:https://github.com/eclipse-collections/eclipse-collections/workflows/Acceptance%20Tests/badge.svg?branch=master

[actions unit-tests]:https://github.com/eclipse-collections/eclipse-collections/actions?query=workflow%3A%22Unit+tests%22
[actions unit-tests img]:https://github.com/eclipse-collections/eclipse-collections/workflows/Unit%20tests/badge.svg?branch=master

[actions performance-tests]:https://github.com/eclipse-collections/eclipse-collections/actions?query=workflow%3A%22Performance+Tests%22
[actions performance-tests img]:https://github.com/eclipse-collections/eclipse-collections/workflows/Performance%20Tests/badge.svg?branch=master

[actions checkstyle]:https://github.com/eclipse-collections/eclipse-collections/actions?query=workflow%3A%22Checkstyle%22
[actions checkstyle img]:https://github.com/eclipse-collections/eclipse-collections/workflows/Checkstyle/badge.svg?branch=master

[actions javadoc]:https://github.com/eclipse-collections/eclipse-collections/actions?query=workflow%3A%22JavaDoc%22
[actions javadoc img]:https://github.com/eclipse-collections/eclipse-collections/workflows/JavaDoc/badge.svg?branch=master

[maven]:https://search.maven.org/#search|gav|1|g:"org.eclipse.collections"%20AND%20a:"eclipse-collections"
[maven img]:https://maven-badges.herokuapp.com/maven-central/org.eclipse.collections/eclipse-collections/badge.svg

[release]:https://github.com/eclipse-collections/eclipse-collections/releases
[release img]:https://img.shields.io/github/release/eclipse-collections/eclipse-collections.svg

[javadoc api]:https://javadoc.io/doc/org.eclipse.collections/eclipse-collections-api
[javadoc api img]:https://javadoc.io/badge2/org.eclipse.collections/eclipse-collections-api/javadoc_eclipse_collections_api.svg

[javadoc impl]:https://javadoc.io/doc/org.eclipse.collections/eclipse-collections
[javadoc impl img]:https://javadoc.io/badge2/org.eclipse.collections/eclipse-collections/javadoc_eclipse_collections.svg

[license-epl]:LICENSE-EPL-1.0.txt
[license-epl img]:https://img.shields.io/badge/License-EPL-blue.svg

[license-edl]:LICENSE-EDL-1.0.txt
[license-edl img]:https://img.shields.io/badge/License-EDL-blue.svg

[snyk-badge]:https://snyk.io/vuln/maven:org.eclipse.collections:eclipse-collections?utm_medium=referral&utm_source=badge&utm_campaign=snyk-widget
[snyk-badge img]:https://snyk-widget.herokuapp.com/badge/mvn/org.eclipse.collections/eclipse-collections/badge.svg

[RichIterable]: https://eclipse.dev/collections/javadoc/11.1.0/org/eclipse/collections/api/RichIterable.html
[ListIterable]: https://eclipse.dev/collections/javadoc/11.1.0/org/eclipse/collections/api/list/ListIterable.html
[SetIterable]: https://eclipse.dev/collections/javadoc/11.1.0/org/eclipse/collections/api/set/SetIterable.html
[Bag]: https://eclipse.dev/collections/javadoc/11.1.0/org/eclipse/collections/api/bag/Bag.html
[StackIterable]: https://eclipse.dev/collections/javadoc/11.1.0/org/eclipse/collections/api/stack/StackIterable.html
[MapIterable]: https://eclipse.dev/collections/javadoc/11.1.0/org/eclipse/collections/api/map/MapIterable.html
[Multimap]: https://eclipse.dev/collections/javadoc/11.1.0/org/eclipse/collections/api/multimap/Multimap.html
[BiMap]: https://eclipse.dev/collections/javadoc/11.1.0/org/eclipse/collections/api/bimap/BiMap.html
[Interval]: https://eclipse.dev/collections/javadoc/11.1.0/org/eclipse/collections/impl/list/Interval.html
[MutableCollection]: https://eclipse.dev/collections/javadoc/11.1.0/org/eclipse/collections/api/collection/MutableCollection.html
[ImmutableCollection]: https://eclipse.dev/collections/javadoc/11.1.0/org/eclipse/collections/api/collection/ImmutableCollection.html
[LazyIterable]: https://eclipse.dev/collections/javadoc/11.1.0/org/eclipse/collections/api/LazyIterable.html
[ParallelIterable]: https://eclipse.dev/collections/javadoc/11.1.0/org/eclipse/collections/api/ParallelIterable.html
[PrimitiveIterable]: https://eclipse.dev/collections/javadoc/11.1.0/org/eclipse/collections/api/PrimitiveIterable.html
[Utilities]: https://eclipse.dev/collections/javadoc/11.1.0/org/eclipse/collections/impl/utility/package-summary.html
[Adapters]: https://eclipse.dev/collections/javadoc/11.1.0/org/eclipse/collections/impl/collection/mutable/AbstractCollectionAdapter.html
[BlogRichLazyMutableImmutable]: https://betterprogramming.pub/rich-lazy-mutable-and-immutable-interfaces-in-eclipse-collections-ce64a31b5936?source=friends_link&sk=8056191cec086d565643c8c7b9bd3c1c
[BlogLambdaExpressionsInJava]: https://betterprogramming.pub/my-ten-year-quest-for-concise-lambda-expressions-in-java-39fde576b950?source=friends_link&sk=843d797af3f58f893ebdee5e13ce0115
[BlogGettingStartedWithEclipseCollections]: https://donraab.medium.com/blog-series-getting-started-with-eclipse-collections-5634dc39b9e6?source=friends_link&sk=92d8eba8a56167fa840cf9c9ada07326
[BlogTheMissingJavaDataStructures]: https://medium.com/javarevisited/blog-series-the-missing-java-data-structures-no-one-ever-told-you-about-17f34cc4b7e2?source=friends_link&sk=9403ae8464ae3477bfc1e52119c1576d
[BlogUnifiedMapHowItWorks]: https://medium.com/oracledevs/unifiedmap-how-it-works-48af0b80cb37
[BlogUnifiedSetTheMemorySaver]: https://medium.com/oracledevs/unifiedset-the-memory-saver-25b830745959
[BlogUnparalleledDesignOfEclipseCollections]: https://medium.com/javarevisited/the-unparalleled-design-of-eclipse-collections-e4340b00f79f?source=friends_link&sk=629e6384171b18233a167a499b46408c
[BlogIterateOverAnyIterableInJava]: https://medium.com/javarevisited/iterate-over-any-iterable-in-java-bec78eeeb452?source=friends_link&sk=7d460d1494cb76ce6bc9a5543785224a
[BlogAsAMatterOfFactory]: https://medium.com/oracledevs/as-a-matter-of-factory-factories-matter-482d8adff094?source=friends_link&sk=96a4cd8fbc42e309c39a917449e6bff2
[BlogJavaHasStreamsDoWeNeedCollections]: https://motlin.medium.com/java-has-streams-do-we-need-third-party-collections-dd12f473d105

[Factories]: https://eclipse.dev/collections/javadoc/11.1.0/org/eclipse/collections/impl/factory/package-summary.html
[10-0-Release]: https://github.com/eclipse-collections/eclipse-collections/releases/tag/10.0.0
[10-1-Release]: https://github.com/eclipse-collections/eclipse-collections/releases/tag/10.1.0
[10-2-Release]: https://github.com/eclipse-collections/eclipse-collections/releases/tag/10.2.0
[10-3-Release]: https://github.com/eclipse-collections/eclipse-collections/releases/tag/10.3.0
[10-4-Release]: https://github.com/eclipse-collections/eclipse-collections/releases/tag/10.4.0
[11-0-Release]: https://github.com/eclipse-collections/eclipse-collections/releases/tag/11.0.0
[11-1-Release]: https://github.com/eclipse-collections/eclipse-collections/releases/tag/11.1.0
