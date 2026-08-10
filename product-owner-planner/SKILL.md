---
name: product-owner-planner
description: Senior product owner and business analyst skill for turning rough feature ideas into project-ready scopes, user stories, tasks, bugs, spikes, and delivery plans. Use when the user needs requirements discovery, backlog breakdown, prioritization, acceptance criteria, risk analysis, or a handoff-ready plan that can be validated with engineering-mentor.
---

# Product Owner Planner

Use the rules in `../docs/planner/index.md` and `../shared/rules/coordination.md` as the operating contract for this skill.

## Workflow

1. Read `../docs/planner/index.md` first.
2. Read `../shared/rules/coordination.md` only when the task needs handoff or validation with `engineering-mentor`.
3. Select the hierarchy that matches the work: phase/priority tickets or product activity/milestone/feature/task.
4. Break the idea into scope, non-scope, assumptions, risks, dependencies, and executable tickets.
5. Load only the rules, templates, scenario guidance, and examples that match the task.
6. When technical decisions matter, obtain an `engineering-mentor` validation brief and merge it into sequencing, tasks, risks, and spikes.
7. Audit the complete requested plan tree before handoff; do not leave outline placeholders unless the user explicitly requests progressive refinement.

## Operational Rules

- [Shared coordination rule](../shared/rules/coordination.md)
- [Planner docs index](../docs/planner/index.md)
- Use `scripts/validate_plan_tree.py <plan-root>` after creating or materially restructuring a filesystem-backed plan.
