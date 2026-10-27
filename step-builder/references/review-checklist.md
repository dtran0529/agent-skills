# Step Review Checklist

Use after implementing a step and before writing its report. Severity:
🔴 must fix before the step is ✅ · 🟡 should fix or record in report section 8 · 🟢 suggestion.

## 1. Đúng phạm vi step
- [ ] Every bullet of the step's Prompt is done or explicitly deferred with a reason.
- [ ] **Hoàn thành khi** is met and backed by real output.
- [ ] No work belonging to later steps slipped in (scope creep makes later reports wrong).

## 2. Quy ước dự án
- [ ] Spring Boot 4 artifacts/annotations (webmvc starter, flyway starter, `@MockitoBean`, Jackson 3 packages, gateway-server-webflux, core `@Retryable`).
- [ ] Package layout `com.shopnow.<service>.<feature>.{api,application,domain,infrastructure}`.
- [ ] DTO records at the API boundary; no entity returned.
- [ ] Errors via `BusinessException` + stable `ErrorCode`; response shape matches common-lib.
- [ ] Schema change only via a new Flyway migration (never edit an applied one).
- [ ] Secrets only from env; nothing sensitive logged.

## 3. Correctness
- [ ] Transaction boundary at the service layer; no remote call (HTTP/Kafka) while holding DB locks or inside long transactions.
- [ ] Concurrency-sensitive writes use the strategy chosen in ADRs (atomic conditional update, optimistic lock, ordered locking); `@Retryable` sits outside `@Transactional`.
- [ ] Kafka: key = aggregate id, consumer idempotent, publish through outbox from STEP 5.3 onward.
- [ ] State changes go through domain methods / state machine, not setters.
- [ ] Money as `BigDecimal`, time as `Instant`/UTC.
- [ ] Input validated; authorization checks ownership (no IDOR).

## 4. Tests
- [ ] Tests assert behavior (not just "no exception"); negative and edge cases for security/concurrency.
- [ ] Concurrency tests really run concurrently (latch start, enough pool connections) and are not flaky across 3 runs.
- [ ] Integration tests use Testcontainers (Postgres/Kafka/Redis), not H2/embedded fakes.
- [ ] Build/test command for the touched modules passes.

## 5. Report accuracy
- [ ] Every file path in the report exists; snippets match the current code.
- [ ] Every diagram in section 4 has a numbered `**Giải thích:**` walk-through in the diagram's order, and each item matches what the code really does (including error branches).
- [ ] Numbers in report and docs/problems.md come from real runs (with conditions: thread count, data size).
- [ ] Interview answers are correct and tied to this project's code.
- [ ] PROGRESS.md, docs/interview-qa.md updated; ADR added if a durable decision was made.

## Output format
```text
Verdict: PASS | NEEDS_FIX
🔴 <file:line> – <problem> – <fix>
🟡 ...
🟢 ...
```
