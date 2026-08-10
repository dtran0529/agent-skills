# Planning Risk Checklist

Load only the sections relevant to the plan.

## Money and pricing

- Decimal representation and currency
- Server-authoritative price
- Historical snapshot and rounding
- Duplicate charge/refund behavior

## Inventory, quota, and limited resources

- Non-negative invariant
- Contention for the final unit
- Reserve, confirm, release, and expiry semantics
- Double release and retry behavior

## State machines

- Allowed transitions and actor permissions
- Invalid, repeated, concurrent, and out-of-order transitions
- Audit history

## Retry and idempotency

- Idempotency-key scope and payload conflict
- Database uniqueness as the final guard
- Crash and partial-result behavior

## Transactions and integrations

- Application-service transaction ownership
- External calls outside long database transactions
- Timeout, duplicate callback, late callback, and reconciliation behavior

## Authentication and authorization

- Credential expiry and revocation
- `401` versus `403`
- Ownership and IDOR
- Secret handling, abuse limits, and sensitive logs

## Migration and recovery

- Forward migration from an empty and supported existing schema
- Backup observability
- Isolated restore drill and integrity checks
- RPO, RTO, and runbook ownership

## Concurrency

- Lost update, stale version, uniqueness race, and deadlock behavior
- Tests synchronized by barriers/latches rather than timing sleeps
- Final database-state assertions

## Observability and capacity

- Technical and business failure signals
- Bounded metric cardinality
- Representative dataset/workload
- Correctness under load, not latency alone
- Evidence before cache, replica, queue, partition, or service extraction
