# Version Drift Triage

Use this checklist when a build, test discovery, runtime, or plugin behavior fails in a way that looks like a dependency mismatch rather than a code bug.

## First Checks

- Inspect the exact failing layer before changing code:
  - compile vs test discovery vs runtime vs packaging
- Check whether the project already manages versions through a BOM, platform, plugin, or version catalog.
- Prefer `dependencyInsight`, `dependencies`, or the equivalent tool to confirm the actual resolved graph.
- Compare the resolved runtime/test classpath against the versions you expected from the build files.

## Common Drift Patterns

- A starter or BOM already manages a dependency, but a direct version pin overrides part of the stack.
- Related artifacts are pinned to different minor or patch versions:
  - example families: `junit-jupiter`, `junit-platform-engine`, `junit-platform-launcher`
  - example families: `spring-core`, `spring-test`, `spring-boot-starter-test`
  - example families: `mockito-core`, `mockito-junit-jupiter`
- A plugin or toolchain version expects a newer or older runtime than the repo currently provides.

## Decision Rule

- Do not assume the test code is broken until the resolved dependency graph is checked.
- If a managed stack already exists, align to it first and remove unnecessary direct pins.
- Only override versions intentionally, and document why the override is safe.

## Useful Evidence To Record

- The exact task that failed.
- The first user-visible error line.
- The dependency or plugin family involved.
- The resolved version(s) that differed from expectation.
- The fix applied, especially if it was to remove a conflicting direct pin.
