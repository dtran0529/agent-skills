# Refinement Completeness

## Full refinement

When the user asks to create or complete an entire phase/activity:

- refine every milestone in the requested scope to the same declared depth
- replace bullet-only task maps with linked task files
- resolve execution-critical ambiguity with a decision or spike
- audit the entire tree, not only the first milestone

Do not mix fully executable features with placeholder `Planned task map` sections unless the user explicitly requests progressive refinement.

## Progressive refinement

When the user requests or accepts progressive refinement:

- label each milestone `refined` or `outline-only`
- state which milestone is current and which is next
- do not describe outline-only tickets as backlog-ready

## Completion audit

Before handoff, check:

- every feature links to its task files
- every task satisfies the executable task contract
- internal Markdown links resolve
- no TODO, placeholder, or planned-task-map marker remains unintentionally
- unresolved technical decisions are explicit spikes or decisions
- task types reflect actual scope; no invented FE/infra work

Run the plan-tree validator for filesystem-backed plans, then perform a judgment-based review for business and technical quality.
