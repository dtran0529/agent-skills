---
name: project-context-cache
description: Reusable project context memory for any code repository. Use when the agent is working in a repo and needs to avoid repeatedly rediscovering project structure, build tools, conventions, entry points, test commands, architecture notes, or when the user asks to remember, refresh, cache, onboard, or reuse project context across future feature, refactor, review, or debugging tasks. Supports explicit invocation like "Use project-context-cache <path-project> <language-or-stack>" (Codex: $project-context-cache, Claude: /project-context-cache).
---

# Project Context Cache

## Purpose

Use a per-repository context file as lightweight project memory. The skill is reusable across projects; the cached data lives inside each repo at `~/.codex/project-context.md`.

This skill does not replace reading code. It reduces repeated discovery and gives Codex a stable starting map before project-specific work.

## Workflow

1. Parse the user's invocation for an optional project path and declared language/stack.
2. Resolve the project path. If omitted, use the current working directory.
3. Treat the user-declared stack as the primary hint. Verify it against project config; do not ignore it or replace it with guesswork.
4. Check for `.codex/project-context.md` inside that project.
5. If it exists, read it before scanning the repo.
6. If it is missing, stale, clearly wrong, or the user asks to refresh context, generate or update it.
7. After reading the cache, inspect only the files relevant to the current task.
8. When the task reveals durable project knowledge, update `.codex/project-context.md` with concise notes.

## Invocation Contract

Preferred explicit form:

```text
Use $project-context-cache <path-project> <language-or-stack>
```

Examples:

```text
Use $project-context-cache /Users/me/work/api Java Maven Spring Boot
Use $project-context-cache ~/work/web React TypeScript Next.js
Use $project-context-cache . Go
Use $project-context-cache ../engine Rust Cargo workspace
```

If the user provides a stack, record it in the cache as "User-declared stack" and use it to guide what files to inspect. If config contradicts the declared stack, state the mismatch and prefer verified repository facts.

## Creating Or Refreshing Context

Use the bundled script when a quick repo map is needed:

```bash
python3 ~/.codex/skills/project-context-cache/scripts/generate_project_context.py . --stack "Java Gradle"
```

Backward-compatible form:

```bash
python3 ~/.codex/skills/project-context-cache/scripts/generate_project_context.py --root .
```

The script writes `~/.codex/project-context.md` by default. It uses only Python standard library modules. It is stack-agnostic bootstrapper, not a complete architecture analyzer.

It detects common project families from durable config files, including Gradle, Maven, Node/React/Next/Vite/Express-style projects, Go modules, Rust/Cargo, and Python packaging. Treat generated output as a draft.

After generation, refine the file manually when needed. The most valuable additions are:

- public API entry points
- architectural boundaries
- conventions that are not obvious from file names
- build/test commands that actually work
- security or correctness rules
- project-specific "do not do this" notes

For frontend, backend, and polyglot repos, inspect the real config before finalizing the cache:

- Node/React/Next/Express: read `package.json`, lockfile, framework config, `src`/`app`/`pages`/`server` layout, and scripts.
- Maven/Gradle Java: read `pom.xml` or Gradle files, Java version/toolchain, module layout, source sets, and test framework.
- Go: read `go.mod`, package layout, command packages under `cmd/`, and integration test conventions.
- Rust: read `Cargo.toml`, workspace members, crate layout, features, and test/bench conventions.
- Monorepos: identify package/workspace boundaries before summarizing commands or architecture.

## Cache Content Guidelines

Keep the cache short and operational. Prefer facts that save future discovery work.

Include:

- project purpose and current stage
- build tool, language/runtime version, package/module system
- top-level source and test layout
- important packages/modules and their responsibilities
- main public APIs or user workflows
- common commands for build, test, format, lint, and run
- known constraints, invariants, and risky areas
- current implementation status for active roadmap items

Avoid:

- full file trees for large repos
- copied source code
- long explanations of standard framework behavior
- speculative plans that are not settled
- stale TODO lists that no longer describe the code

## Refresh Rules

Refresh the cache when:

- build files change
- source/test layout changes
- public API entry points move
- a new major module or feature area is added
- the current task repeatedly requires rediscovering the same facts
- the user explicitly says to refresh, remember, or update project context

Do not refresh the cache for every small code edit. Update only durable context.

## Using With Other Skills

When combined with a mentor, architecture, code review, or language-specific skill, read the project context first, then apply the other skill's reasoning to the current task.

Example:

```text
Use $project-context-cache and $engineering-mentor to design the next API change.
```
