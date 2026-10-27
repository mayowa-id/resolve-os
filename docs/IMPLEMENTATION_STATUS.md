# Implementation status

## 1. Milestone and status

Milestone 0 - DONE.

## 2. Delivered behavior

- Java 25 / Spring Boot 4.1.2 Maven reactor with nine modules and reproducible `only-script` Maven Wrapper.
- Secured API composition root: public detail-free health endpoint and authenticated application routes.
- Independently bootable, non-web worker composition root; no premature Temporal jobs.
- Flyway-enabled PostgreSQL baseline with Hibernate schema validation and Open Session in View disabled.
- Pinned local database, identity, storage, mail, and workflow services.
- Automated bootstrap runtime check for API health, real PostgreSQL migration, and worker startup.
- CI baseline for Maven verification, Compose model validation, and secret scanning.
- Persistent engineering rules, architecture, decisions, dependency record, threat model, and local-development guide.

## 3. Files changed

- Root: Maven parent/wrapper, `.gitignore`, `.env.example`, `AGENTS.md`, and `README.md`.
- Applications: API/worker POMs, composition roots, configuration, and tests.
- Modules: contracts, domain, application, persistence, integrations, and agent boundaries.
- Infrastructure: Compose stack, Keycloak realm, and `verify-bootstrap.sh`.
- CI/docs: baseline workflow and architecture, decision, dependency, threat, development, and status records.

## 4. Database migrations

- `V001__platform_baseline.sql`: creates a singleton installation marker using PostgreSQL-compatible `timestamp with time zone`. Additive and non-destructive.
- Verified against H2 in the fast application-context test and PostgreSQL 18.6 in the runtime smoke check.

## 5. Commands executed and results

- `./mvnw --batch-mode --no-transfer-progress clean verify`: PASS; 9/9 reactor modules succeeded, 3 tests passed, 0 failed/skipped.
- `docker compose ... ps -a`: PostgreSQL, Keycloak, MinIO-compatible storage, and Mailpit healthy; Temporal and Temporal UI running.
- `infra/scripts/verify-bootstrap.sh`: PASS; API returned `UP`, migration row existed in PostgreSQL, worker reached started state.
- POM XML, Keycloak realm JSON, ignore rules, and whitespace checks passed.
- `git diff --check`: passed; line-ending conversion warnings reflect the Windows Git configuration.

## 6. Security/tenancy checks

- Verified unauthenticated access to the detail-free health endpoint.
- Verified an unauthenticated application route returns 401.
- Admin services bind only to `127.0.0.1`; `.env` and common credential formats are ignored.
- Tenant isolation is not implemented in Milestone 0 and is the primary Milestone 1 acceptance concern.

## 7. Assumptions and deviations

- Private handoff Markdown files remain ignored; the actionable root `AGENTS.md` is versioned.
- Host ports use API `18080` and PostgreSQL `55432` because `8080` and `5432` were already occupied locally.
- Spring AI and Temporal SDK versions are managed but runtime integrations remain deferred to their authorized milestones.
- ADR-004 records the archived MinIO upstream and the exact community rebuild used for local development only.

## 8. Known gaps

- No business/domain functionality is expected in Milestone 0.
- The local MinIO-compatible community image is not approved for production; production object storage must be resolved before Milestone 3.
- JDK 25 emits upstream Maven/Mockito future-compatibility warnings; these do not fail the build and should be addressed as supported plugin versions become available.

## 9. How to run and verify locally

Follow `docs/LOCAL_DEVELOPMENT.md`. The repeatable checks are:

```bash
./mvnw clean verify
docker compose --env-file .env -f infra/compose.yaml up -d --pull never
./infra/scripts/verify-bootstrap.sh
```

## 10. Next milestone

Milestone 1 may now begin when explicitly authorized: organizations, memberships, validated JWT issuer/audience/signature, roles/permissions, tenant context, two synthetic tenants, and cross-tenant isolation tests.
