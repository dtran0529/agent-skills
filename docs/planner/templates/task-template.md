# Task Template

- Title: imperative verb phrase prefixed by task type when useful, such as `[BE]`, `[DB]`, `[TEST]`, or `[INFRA]`
- Outcome: the observable capability produced by the task
- Scope: work required to produce the outcome
- Non-scope: nearby work intentionally excluded
- Artifacts: files, APIs, migrations, reports, or configuration expected from the task
- Dependencies:
  - blocked by
  - related work when relevant
  - work unblocked by completion
- Run: exact verification command when the tool/provider is known; otherwise state the decision that must be resolved before implementation
- Expected result: observable output, status, state, or artifact
- Failure scenarios: dependency, input, concurrency, retry, or security failures proportional to risk
- Acceptance criteria: pass/fail checklist
- Evidence: diff, command output, response, query, report, or test result required for review

Do not add a `Starting state` section by default. Repository inspection still informs the task, but the ticket should describe the target work rather than snapshotting transient repo state.
