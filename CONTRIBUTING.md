Contributor License
-------------------

If this is your first time contributing to an Eclipse Foundation project, you'll need to sign the [Eclipse Contributor Agreement][ECA].

- [Create an account](https://dev.eclipse.org/site_login/createaccount.php) on dev.eclipse.org
- Open your [Account Settings tab](https://dev.eclipse.org/site_login/myaccount.php#open_tab_accountsettings), enter your GitHub ID and click Update Account
- Read and [sign the ECA](https://dev.eclipse.org/site_login/myaccount.php#open_tab_cla)
- Use the exact same email address for your Eclipse account and your commit author.

Issues
------

Search the [issue tracker](https://github.com/eclipse/eclipse-collections/issues) for a relevant issue or create a new one.

Making changes
--------------

Fork the repository in GitHub and make changes in your fork.

Please add a description of your changes to the [draft release notes](RELEASE_NOTE_DRAFT.md).

Finally, submit a pull request.

Details on [working with GitHub for Eclipse Collections](https://github.com/eclipse/eclipse-collections/wiki/Working-with-GitHub) is located at the Wiki.

Contact us
----------

[Join the mailing list][mailing-list] and email the community at collections-dev@eclipse.org to discuss your ideas and get help.

Build
-----
The Eclipse Collections build requires below as dependencies.

- Java 11+
- Maven 3.1.0+

The Eclipse Collections build performs code generation to create primitive collections. Run the full build once before opening your IDE.

```bash
mvn install -DskipTests=true
```

Now you can open the project in your IDE and it won't complain about missing files. You'll be able to use the IDE to perform incremental builds and run tests. You should rarely need to run the maven build, except when:

- you want to force a clean build
- you work on [JMH][jmh] tests
- your changes affect code generation
- you want to see if your changes will pass [the same builds that GitHub Actions runs][github]

Semantic Versioning
-------------------

Eclipse Collections version numbers follow [Semantic Versioning][semver]. This means we increment the major version when we make incompatible API changes. This includes any changes which

- break binary compatibility
- break source compatibility
- break serialization compatibility

Normally, collections will have the same serialized form across major releases, indefinitely. But if we have to break serialization for some reason, it will be in a major release. Eclipse Collections includes a suite of serialization tests to prevent accidental changes.

While preparing a minor release, the master branch won't contain any compatibility breaking changes. Feel free to work on major, compatibility-breaking changes whenever you'd like. However, if you submit a pull request to master while we're preparing a minor release, you'll have to be patient and you'll need to rebase your changes once the release is complete.

Coding Style
------------

Eclipse Collections follows a coding style that is similar to [Google's Style Guide for Java][style-guide], but with curly braces on their own lines. Many aspects of the style guide are enforced by CheckStyle, but not all, so please take care.

```bash
mvn clean install checkstyle:check --projects "!scala-unit-tests,!jmh-scala-tests,!jmh-tests" -DskipTests=true
```

Avoid changing whitespace on lines that are unrelated to your pull request. This helps preserve the accuracy of the git blame view, and makes code reviews easier.

Commit messages
---------------

- [Use the imperative mood][imperative-mood] as in "Fix bug" or "Add feature" rather than "Fixed bug" or "Added feature"
- [Mention the GitHub issue][github-issue] when relevant
- It's a good idea to follow the [advice in Pro Git](https://git-scm.com/book/en/v2/Distributed-Git-Contributing-to-a-Project)

Pull requests
-------------

Excessive branching and merging can make git history confusing. With that in mind

- Squash your commits down to a few commits, or one commit, before submitting a pull request
- [Rebase your pull request changes on top of the current master][rebase]. Pull requests shouldn't include merge commits.

Submit your pull request when ready. Three checks will be kicked off automatically.

- IP Validation: Checks that all committers signed the Eclipse CLA and signed their commits.
- Continuous integration: [GitHub Actions][github] that run JUnit tests, CheckStyle, and FindBugs.
- The standard GitHub check that the pull request has no conflicts with the base branch.

Make sure all the checks pass. One of the committers will take a look and provide feedback or merge your contribution.

That's it! Thanks for contributing to Eclipse Collections!

[ECA]:             https://www.eclipse.org/legal/ECA.php
[jmh]:             http://openjdk.java.net/projects/code-tools/jmh/
[semver]:          http://semver.org/
[style-guide]:     https://google.github.io/styleguide/javaguide.html
[rebase]:          https://github.com/edx/edx-solutions-edx-platform/wiki/How-to-Rebase-a-Pull-Request
[github]:          https://github.com/eclipse/eclipse-collections/actions
[imperative-mood]: https://github.com/git/git/blob/master/Documentation/SubmittingPatches
[github-issue]:    https://help.github.com/articles/closing-issues-via-commit-messages/
[mailing-list]:    https://dev.eclipse.org/mailman/listinfo/collections-dev
