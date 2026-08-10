# Planning Validation

Use this workflow when reviewing a roadmap, product activity, milestone, feature breakdown, or backlog before implementation.

1. Confirm the product outcome, constraints, assumptions, and non-goals.
2. Select the smallest coherent architecture for the current stage.
3. Identify critical invariants, state transitions, trust boundaries, and transaction ownership.
4. Identify concurrency, retry, idempotency, migration, recovery, and integration risks that actually apply.
5. Check that features are vertical outcomes and technical concerns are child tasks.
6. Check sequencing and hidden dependencies.
7. Require tests and evidence proportional to risk.
8. Return a technical validation brief for `product-owner-planner` to merge.

## Validation rules

- Do not approve a plan that stops at milestone summaries when implementation is in scope.
- Do not approve a task that lacks an observable result and verification path.
- Distinguish commands that currently run from target verification commands and provider-specific illustrations.
- Do not recommend Redis, messaging, replicas, Kubernetes, or service extraction without a measured or explicit operational need.
- Split a spike when it combines decisions that can be resolved independently. Keep a decision cluster together only when the same constraint and evidence unblock one design.
- Do not invent frontend, infrastructure, provider, or business work outside the stated scope.

## Vertical slicing

Prefer:

```text
Feature: Browse products
├── DB task
├── backend/API task
├── test task
└── frontend task only when frontend is in scope
```

Avoid horizontal delivery features named Backend, Database, or Frontend unless the user explicitly manages work that way and accepts the integration cost.

## Output

Use the technical validation brief in `../docs/planning-validation-brief.md`. Load `../docs/planning-risk-checklist.md` only for relevant risk domains.
