# Planner and Engineering Mentor Handoff

Use this sequence for medium or large implementation plans:

1. `product-owner-planner` defines outcome, scope, non-scope, assumptions, product ambiguity, and candidate hierarchy.
2. `engineering-mentor` returns a technical validation brief covering boundaries, invariants, transactions, concurrency, security, tests, sequencing, and spikes.
3. `product-owner-planner` converts the validated design into milestone, feature, and executable task files.
4. `engineering-mentor` performs a feasibility and risk pass on the refined plan.
5. `product-owner-planner` merges feedback and audits the complete plan tree.

## Ownership

| Concern | Owner |
|---|---|
| Product outcome, scope, and non-scope | product-owner-planner |
| Ticket hierarchy, completeness, and ordering | product-owner-planner |
| Architecture, transaction, concurrency, and technical feasibility | engineering-mentor |
| Product ambiguity | product-owner-planner creates a question or spike |
| Technical ambiguity | engineering-mentor recommends a decision or spike |
| Executable ticket contract | product-owner-planner |
| Technical validity of ticket contents | engineering-mentor |

Do not let handoff duplicate full ticket content. The mentor brief contains decisions and constraints; the planner owns the final ticket shape.
