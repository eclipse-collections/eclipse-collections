Eclipse Collections AI Policy
-----------------------------

- Autonomous AI agents are not permitted to submit PRs or issues. A human must be able to take responsibility for any code contribution.
- The submitting person must understand and be able to discuss, modify and take responsibility for the submitted code.
- Submitting non-trivial [^1] AI generated code in a PR is permitted, with the following restrictions:
	- The commit message must include a notice in the following format::

            Assisted-by: AGENT_NAME:MODEL_VERSION [TOOL1] [TOOL2]

        Where:

        * ``AGENT_NAME`` is the name of the AI tool or framework
        * ``MODEL_VERSION`` is the specific model version used
        * ``[TOOL1] [TOOL2]`` are optional specialized analysis tools used
          (e.g., coccinelle, sparse, smatch, clang-tidy)

        Basic development tools (git, Eclipse, Intellij, etc) should not be listed.

        Example::

          Assisted-by: Claude:claude-3-opus coccinelle sparse

        This may be conveniently added using the `--trailer` option with the git commit command.
        See the [Working with Github](https://github.com/eclipse-collections/eclipse-collections/wiki/Working-with-GitHub) wiki page for an example.

	- No AI specific files should be included in the commit.
	- All other contribution guidelines must be followed, including copyright/license compatibility and notices, Eclipse ECA, etc.
	- Additionally, overly verbose AI generated PR descriptions are highly discouraged.  The faster/easier a human reviewer can read the text, the faster it'll be merged.
- Use of AI for translation purposes in issues and other non-committed text is explicitly permitted.

[^1]: Trivial LLM generated content such as variable renames or autocompleted function calls, often branded "predictions" or "suggestions", that is otherwise indistinguishable from traditional methods such as a regex search/replace or an IDE autocompletion can be treated like other regular IDE tools such as Intellisense. This does not include cases where the prediction generates things like entire function blocks.
