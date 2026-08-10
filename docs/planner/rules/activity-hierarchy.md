# Product Activity Hierarchy

Use this hierarchy for implementation programs that span multiple capabilities:

```text
Product Activity
└── Milestone
    └── Feature
        └── Task
```

## Layer responsibilities

- Product Activity: overall outcome, scope, non-scope, ordering, and definition of done.
- Milestone: coherent delivery capability with a runnable or otherwise observable exit condition.
- Feature: one independently reviewable outcome or technical enablement.
- Task: one implementation concern required by a feature.

Name milestones by capability, such as `foundation`, `catalog`, or `checkout-order`. Do not force numeric `step-*` or priority prefixes when the user prefers capability names.

Split features vertically by outcome. Split tasks by technical concern, using types such as `SETUP`, `INFRA`, `BE`, `FE`, `DB`, `TEST`, `SECURITY`, `OBSERVABILITY`, and `DOCS`.

Do not create horizontal features named Backend, Frontend, or Database. Do not force every feature to contain every task type. In particular, do not invent frontend tasks before a frontend project and UX scope exist.

Use the phase/priority-folder structure instead when priority and ticket scheduling are the user's primary organizing model.
