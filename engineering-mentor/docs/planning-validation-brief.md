# Technical Planning Validation Brief

Return the sections that apply:

- Selected approach and why it fits the current stage
- Architecture and module boundaries
- Critical invariants
- State transitions
- Transaction boundaries
- Concurrency and locking strategy
- Retry and idempotency boundaries
- Authentication, authorization, ownership, and trust boundaries
- Failure and recovery behavior
- Required unit, integration, concurrency, security, and load tests
- Implementation sequence and hidden dependencies
- Decisions that require a spike
- Deferred infrastructure and the evidence that should trigger it
- Tickets to split, merge, rename, or reprioritize

Be concrete enough for `product-owner-planner` to convert the brief into tasks. Do not prescribe framework details that the repository or a later implementation decision must determine.
