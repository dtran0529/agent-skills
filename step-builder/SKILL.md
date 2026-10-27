---
name: step-builder
description: Execute a numbered filesystem plan one step at a time (PLAN.md with "## STEP x.y" sections + PROGRESS.md). Use when the user types "/step-builder <id|next|status|report id>", or asks "làm step tiếp theo", "thực hiện STEP 2.4", "tiến độ tới đâu rồi". Implements exactly one step, verifies it with real commands, gets it reviewed, writes a Vietnamese learning report (what was done, key code, core knowledge, interview Q&A), and updates PROGRESS.md, docs/problems.md, docs/interview-qa.md. Coordinates the backend-java-developer, frontend-developer, engineering-mentor and research-engineer rules.
argument-hint: "[next | <step-id> | status | report <step-id>] [plan-root]"
---

# Step Builder

Build a learning project **one verified step at a time**. A step is done only when the code works, there is evidence, and the report teaches what was built.

Arguments: `$ARGUMENTS`

## Modes

| Invocation | Behavior |
|---|---|
| `next` (default when empty) | First step in PROGRESS.md whose status is not ✅/⏭️ and whose dependencies are all ✅ |
| `<id>` (e.g. `2.4`, `2.5a`) | That step |
| `status` | Summarize PROGRESS.md: done / in progress / next 3 runnable steps; no code changes |
| `report <id>` | Rewrite only the report for an already implemented step from the current code |

Plan root: second argument if given, else `shopnow/` relative to the project root. Files: `<root>/PLAN.md`, `<root>/PROGRESS.md`, `<root>/docs/`.

Paths below such as `../docs/...` are relative to this skill's folder inside the `agent-skills` repo (resolve the symlink). If one does not resolve, look for the `agent-skills/` directory at the project root.

## Workflow

### 1. Load context – narrowly
- Read project `CLAUDE.md` if present.
- From PLAN.md read only: the "Quy ước dự án" section (including the Boot 4 pitfalls table and Definition of Done) and the target step section — find the heading `## STEP <id> ` with Grep and read until the next `## STEP` / `# PHASE`. Do not load the whole plan.
- Read PROGRESS.md. If the target is 🟡, read its report first and **resume** from the recorded gap instead of restarting.
- Skim the report of the immediately previous step(s) the target depends on (sections 2, 3, 12) for continuity.
- Follow [shared project-context rule](../shared/rules/project-context.md).

### 2. Gate
- Every step in **Phụ thuộc** must be ✅ (or ⏭️ with a note that does not block this step). Otherwise stop, name the missing steps, and suggest `/step-builder <first-missing>`.
- If the step clearly exceeds one session, propose a split (`2.5a`, `2.5b`), update PLAN.md and PROGRESS.md, then do only the first part.

### 3. Pick the role rules for this step
- Java / Spring / Maven / Kafka / SQL → [backend rules](../docs/backend/index.md); load only the matching files in `../docs/backend/tech/` (e.g. `spring-data.md` for JPA, `spring-messaging.md` for Kafka, `spring-security.md` for auth).
- React / TypeScript / UI → [frontend rules](../docs/frontend/index.md).
- Trade-offs, ADRs, concurrency strategy → [mentor core](../engineering-mentor/rules/core.md) + [decision workflow](../engineering-mentor/rules/decision-workflow.md).
- Version-sensitive configuration (artifact names, property prefixes, new APIs) → verify against official docs per [research-engineer](../research-engineer/SKILL.md) before writing it. Never rely on Boot 3-era memory when the plan says Boot 4.

### 4. Announce the plan (short)
3–7 bullets: files to create/change, approach, how it will be verified. Do not re-ask decisions fixed in "Quy ước dự án". Ask only for decisions the step leaves open, always with a recommended default; if the user said "tự chọn", pick the default and record it in the report.

### 5. Implement
- Smallest coherent change that satisfies the step's Prompt and **Hoàn thành khi**; follow the conventions (package layout, DTO records, error contract, Flyway-only schema, topic naming, secrets from env).
- Concurrency / performance steps: write the reproducing test first, record the failing numbers, then fix and record the new numbers.
- Do not build work owned by later steps; mark deliberate gaps as `TODO(STEP x.y)`.
- If the plan's instruction is wrong for the real library version, follow verified behavior, explain it in report section 8, and correct PLAN.md.

### 6. Verify (evidence, not belief)
- Run what the step needs: `./mvnw -pl <module> -am verify`, `npm run lint && npm run test && npm run build`, `docker compose ps`, `curl`, `EXPLAIN (ANALYZE, BUFFERS)`, k6.
- Keep the real output (trimmed) for the report. If command execution is not permitted, list exact commands per [testing rules](../shared/rules/testing-and-commands.md) and mark the step 🟡 until the user confirms results.
- A failure that cannot be fixed within scope → status 🟡, reason in PROGRESS "Ghi chú" and report section 8.

### 7. Review
- If a `step-reviewer` subagent is available, delegate the review to it with: step id, plan root, list of changed files. Otherwise self-review with [references/review-checklist.md](references/review-checklist.md).
- Fix every 🔴 finding before continuing; list 🟡 findings in report section 8 if left.

### 8. Write the report
- Path: `<root>/docs/steps/STEP-<id>-<kebab-slug>.md`, from [templates/step-report.md](templates/step-report.md).
- Follow [learning content rules](../engineering-mentor/rules/learning-content.md): natural Vietnamese with diacritics; explain *why*; show only decisive code lines with real file links; numbers and outputs must be real.
- Depth proportional to the step: keep every heading; write "Không áp dụng" for a section that genuinely does not apply. ⭐ steps (Phase 4, 5, 7) get the deepest sections 6, 7, 10.
- Section 4: every diagram (mermaid or ASCII block) is followed by a `**Giải thích:**` numbered list that walks the diagram in order. Each item names the step as it appears on the diagram, links the class/config that does it, says what happens and why, and covers the failure branch (what the caller gets, or what breaks if the step were skipped). A diagram without this walk-through is incomplete; `CheckStep.java` rejects it.
- 4–6 interview questions, answered with reference to this project's code.

### 9. Update tracking
- PROGRESS.md row: status, date `YYYY-MM-DD`, report link `[báo cáo](docs/steps/STEP-...md)`, short note. Keep the table format.
- `docs/problems.md`: append an entry (format at the bottom of the template) whenever a problem was reproduced/solved or a measurement was taken.
- `docs/interview-qa.md`: append `## STEP <id> – <tên>` with the questions and short answers.
- New ADR in `docs/adr/NNNN-*.md` when a durable decision was made.

### 10. Self-check
Run `java <this-skill>/scripts/CheckStep.java <root> <id>` (single-file launch, JDK 17+, no compilation). Fix every reported problem before the final reply.

### 11. Final reply (Vietnamese, short)
- What was done + verification result (pass/fail with the key number).
- Link to the report.
- 2–3 concepts to review before moving on.
- Proposed Conventional Commit message (commit only if the user asked).
- Next runnable step id.

## Rules
- One step per invocation; never chain into the next step unless asked.
- Code first, report second: the report describes what really exists.
- Never claim success without evidence; never invent numbers.
- No destructive commands (drop volumes, reset DB, force push) without asking.
- Respect [shared completion rule](../shared/rules/completion.md).
