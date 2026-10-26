# Implementation status

## 1. Milestone and status

Milestone 0 — PARTIAL pending executable build and local-stack health verification.

## 2. Delivered behavior

- Maven multi-module architecture and two Spring Boot composition roots.
- API security defaults: public detail-free health, authenticated remainder.
- Independently bootable non-web worker context with Temporal intentionally disabled.
- Flyway baseline and pinned local-development infrastructure.
- CI baseline for reactor verification, Compose validation, and secret scanning.
- Persistent engineering, architecture, threat, dependency, and development guidance.

## 3. Files changed

Root build/rules, API and worker applications/tests, module POMs, infrastructure configuration, CI, and documentation were created for Milestone 0.

## 4. Database migrations

- `V001__platform_baseline.sql`: creates a non-domain singleton installation marker. It is additive and non-destructive.

## 5. Commands executed and results

- Toolchain inspection: Windows has no Java, Maven, or Docker on `PATH`; WSL has Docker CLI and Git but no Java/Maven.
- `.\mvnw.cmd --version`: failed as expected with `JAVA_HOME not found`; wrapper scripts are present, but JDK 25 is absent.
- `docker compose -f infra/compose.yaml config --quiet` through WSL: blocked by `Access is denied` when entering the Windows-mounted workspace under the sandbox identity.
- All nine `pom.xml` files parsed successfully as XML.
- `git check-ignore -v` confirmed all three private handoff Markdown files are ignored.
- Compose service health: not run because the daemon/workspace combination is unavailable from this environment.

## 6. Security/tenancy checks

Health/security tests are present but not yet executed. Tenant behavior is deliberately deferred to Milestone 1.

## 7. Assumptions and deviations

- The private handoff Markdown files are ignored; the actionable root `AGENTS.md` is versioned.
- Spring AI and Temporal SDK versions are pinned for compatibility records but runtime dependencies are deferred to their milestones.
- See ADR-004 for the archived MinIO upstream risk.

## 8. Known gaps

- Build and tests require JDK 25.
- Compose services and health checks require a reachable Docker daemon and image pulls.
- No business/domain functionality is expected in Milestone 0.

## 9. How to run and verify locally

Follow `docs/LOCAL_DEVELOPMENT.md`, then run the Maven reactor, Compose config, service health, API health, and worker startup checks.

## 10. Next milestone

After Milestone 0 is fully verified and explicitly accepted: Milestone 1 adds organizations, memberships, JWT validation, roles, tenant context, and isolation tests. Do not begin it automatically.
