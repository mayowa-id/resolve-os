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

Pin exact image tags rather than floating tags. MinIO's upstream repository was archived in April 2026, and the final October 2025 security release instructs container users to build from source instead of publishing an official image. Milestone 0 therefore uses an exact SourceMation rebuild for local development only. Before Milestone 3, select a supported production S3 provider or create and verify an image directly from the pinned official source; the community rebuild is not approved for production.

## ADR-005: Database-authoritative tenant access

- Status: accepted
- Date: 2026-10-27

Treat the validated JWT subject as identity, but resolve organizations and roles from active database memberships on every request. Token-supplied organization and role claims are not authorization inputs. Return 404 for an inaccessible organization so callers cannot enumerate tenants, and 403 only when membership exists but lacks the required role.

## ADR-006: Local identity fixtures

- Status: accepted
- Date: 2026-10-27

Import three synthetic Keycloak users and seed matching memberships in a local-profile-only repeatable Flyway migration. Production environments never load these fixtures. Local direct password grants exist solely for automated smoke verification; production clients use the external OIDC provider's approved interactive flow.
