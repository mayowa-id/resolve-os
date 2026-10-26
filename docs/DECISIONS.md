# Architecture decisions

## ADR-001: Modular monolith and separate worker

- Status: accepted
- Date: 2026-10-26

Use one Maven reactor, one API deployable, and one worker deployable. This preserves enforceable module boundaries without premature distributed services.

## ADR-002: Health endpoint security

- Status: accepted
- Date: 2026-10-26

Expose only Actuator health paths without authentication and never expose health details publicly. Require JWT authentication for every other route. Tenant and role authorization arrives in Milestone 1.

## ADR-003: Baseline database migration

- Status: accepted
- Date: 2026-10-26

Start Flyway with a minimal singleton `platform_bootstrap` table. Domain schema is intentionally deferred to its owning milestones so it is not prematurely locked or made falsely mandatory.

## ADR-004: Local infrastructure images

- Status: accepted with review note
- Date: 2026-10-26

Pin exact image tags rather than floating tags. MinIO is retained because it is required by the approved plan, but its upstream repository was archived in April 2026; reassess the production object-storage implementation before Milestone 3.

