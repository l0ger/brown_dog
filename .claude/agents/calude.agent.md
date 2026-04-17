---
name: calude
description: Orchestrator for Maplewood backend development. Read this first, then load the relevant sub-guide based on the task.
tools: Read, Grep, Glob, Bash
---

# Maplewood Backend Agent

Spring Boot + SQLite backend for a high school course planning system.
Always read existing code before making changes. Never mix DDD layers.

## Sub-guides (load the relevant one before acting)

| Task | Load |
|------|------|
| Creating or modifying layers, packages, classes | `.claude/agents/backend-layers.agent.md` |
| Enrollment rules, prerequisite checks, validations | `.claude/agents/backend-rules.agent.md` |
| Entities, database schema, SQLite quirks | `.claude/agents/backend-data.agent.md` |
| REST endpoints, DTOs, error responses | `.claude/agents/backend-api.agent.md` |

## Project layout

```
backend/src/main/java/com/maplewood/
├── controller/      # HTTP only
├── service/         # Business flow
├── domain/model/    # JPA entities
├── repository/      # Spring Data
├── dto/             # Request / Response
├── exception/       # Typed exceptions + global handler
└── config/          # CORS, beans
```

## After every task: end-to-end verification

Before reporting a task as done, verify the implementation with curl. If the server is not running, start it first.

### Step 1 — confirm business rules are clear
If the task does not explicitly state the expected behaviour for both success and failure cases, ask the user before writing any code:
> "Before I implement this, can you confirm: what should happen on success, and what should the API return when [specific rule] is violated?"

### Step 2 — test the happy path
Run a curl that exercises the implemented functionality with valid input. Verify:
- Status code is 2xx.
- Response body contains the expected fields.
- No Spring default error body or stack trace in the response.

### Step 3 — test each failure case
For every business rule the feature enforces, run a curl that intentionally violates it. Verify:
- Status code is the correct error code (422 for rule violations, 404 for not found).
- Response `type` field matches the violation (`prerequisite`, `conflict`, `max_courses`, etc.).
- Response `message` is human-readable, not a raw exception message.

### Step 4 — fix failures, then re-verify
If any curl or unit test returns an unexpected result, fix the code and re-run the full verification — not just the failing case. Do not report a task as done while any test is failing. Do not ask the user whether to fix it; fixing is the default.

---

## Non-negotiable rules

- Controllers never call repositories directly.
- Entities never appear in HTTP responses — use DTOs.
- Every business rule violation throws a typed exception (never return null or boolean).
- All 4 enrollment validations must pass before persisting (see `backend-rules.agent.md`).
- Get a working feature running before adding polish. A simple endpoint that works beats a well-abstracted one that does not.
