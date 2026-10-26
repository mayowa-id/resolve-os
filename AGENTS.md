# ResolveOS engineering rules

## Required workflow

1. Read `docs/IMPLEMENTATION_STATUS.md` and relevant architecture/decision records before changing code.
2. Implement only the milestone explicitly authorized by the user. Stop and report against its acceptance gate.
3. Preserve working code and user changes. Record architectural deviations in `docs/DECISIONS.md`.
4. Run and report real checks. Never describe an unexecuted or failing check as passing.
5. Never replace a required integration with a mock without labelling its scope and documenting the production gap.

## Architecture and boundaries

- Use Java 25, Spring Boot 4.1.2, Maven, PostgreSQL, Flyway, and pinned stable dependencies.
- Keep the domain independent of Spring, HTTP, persistence, Temporal, and AI SDKs.
- Keep controllers and adapters thin; business invariants and transactions belong in application/domain services.
- PostgreSQL is authoritative business state. Temporal orchestrates and performs external I/O only through Activities.
- Put genuinely reusable, domain-neutral code in the narrowest shared module (`contracts` for public contracts; otherwise a clearly named common package). Do not create a dumping-ground `util` package.
- Avoid duplicate implementations. Extract shared behavior after a real second use, not speculatively.
- No service class may exceed 600 non-blank lines. Prefer substantially smaller cohesive classes; split by responsibility before reaching the limit.
- Avoid cyclic module dependencies, premature microservices, and unnecessary abstractions.

## Java design

- Use explicit types. Prefer records for immutable DTOs/value objects and enums for closed domain state sets; do not encode closed states as arbitrary strings.
- Interfaces define meaningful ports or substitutable behavior, not one-off indirection.
- Do not return `null` collections. Use `Optional` only for genuinely optional return values, not fields or parameters.
- Model nullability honestly. Never make a field mandatory merely for implementation convenience; validate only business-required input.
- Inject `Clock` for non-Temporal time and deterministic ID suppliers where tests need control.
- Keep methods cohesive, names precise, and side effects visible. No silent catch blocks or unbounded retries.
- Map persistence entities to API DTOs explicitly; never serialize JPA entities directly.

## Persistence and API

- Evolve schema only through immutable, versioned Flyway SQL. Never use `ddl-auto=update`.
- Use UUID primary keys, UTC `timestamptz`, explicit constraints/indexes, and tenant-safe composite relationships for tenant-owned data.
- Preserve optionality in both SQL and Java. Add `NOT NULL` only when a documented invariant requires it.
- Every tenant-owned query and mutation must include verified tenant scope. Do not trust tenant IDs from request bodies or model output.
- Use Bean Validation at transport boundaries and enforce domain invariants again in domain/application code.
- Use RFC 9457 Problem Details, bounded pagination, optimistic concurrency, and idempotency where the plan requires them.

## Security, AI, and operations

- Never commit or log credentials, tokens, private customer data, raw sensitive evidence, or untrusted prompts.
- AI output is untrusted input. Models cannot approve, waive, activate, change policy, execute arbitrary code, or bypass human gates.
- Domain mutation, audit record, and outbox event must commit atomically where required.
- External side effects need idempotency/reconciliation and must not be covered by a database transaction.
- Local/test simulators must be deterministic and clearly marked non-production.

## Quality gates

- Every delivered behavior needs proportionate unit/integration tests; every bug fix needs a regression test.
- Keep formatting, compilation, tests, migrations, secret scanning, and dependency checks green.
- Test tenant isolation, authorization failures, invalid state transitions, concurrency/idempotency, and failure recovery as those features arrive.
- Keep `docs/IMPLEMENTATION_STATUS.md`, `docs/DECISIONS.md`, and dependency records accurate.

