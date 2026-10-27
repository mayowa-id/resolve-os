# Implementation status

## 1. Milestone and status

Milestone 1 - DONE. Render deployment readiness - DONE; external deployment not yet executed.

## 2. Delivered behavior

- Organization and membership domain types use explicit status and role enums.
- Database-authoritative tenant access resolves active memberships from the JWT subject.
- Tenant-scoped organization and membership endpoints return stable response records.
- OIDC resource-server validation enforces issuer, audience, timestamps, and RS256 signatures.
- Local-only Keycloak and PostgreSQL fixtures provide three users across two tenants.
- Provider-neutral API container and Render Blueprint support an external PostgreSQL/OIDC preview.

## 3. Files changed

- Domain/application: identity models, access port, tenant access service, and authorization exceptions.
- Persistence: JPA entities, repositories, adapter, identity migration, and local seed.
- API: composition configuration, endpoints, DTOs, and RFC 9457 exception mapping.
- Infrastructure: Keycloak users/client mapper and expanded authenticated runtime verification.
- Tests/docs: tenant authorization scenarios, contract, decisions, threat model, and local guide.
- Deployment: hardened Docker image, production profile, Render Blueprint, bootstrap SQL, and verification scripts.

## 4. Database migrations

- `V001__platform_baseline.sql`: unchanged installation baseline.
- `V002__identity_and_tenancy.sql`: organizations, memberships, constraints, uniqueness, and lookup indexes.
- `R__synthetic_local_tenants.sql`: local-profile-only, idempotent synthetic organizations and memberships.
- PostgreSQL 18.6 applied and Hibernate validated the resulting schema.

## 5. Commands executed and results

- `./mvnw --batch-mode --no-transfer-progress clean verify`: PASS; 9/9 modules, 11 tests, zero failures/skips.
- `docker compose ... up -d --force-recreate --wait keycloak`: PASS; updated realm imported and healthy.
- `./infra/scripts/verify-bootstrap.sh`: PASS; signed login, PostgreSQL migrations, tenant isolation, and worker startup verified.
- `docker build --tag resolveos-api:deployment-test .`: PASS with pinned Java 25 build/runtime images.
- `./infra/scripts/verify-container.sh`: PASS; non-root runtime, fresh production migrations, 401 protection, and organization bootstrap verified.
- Gitleaks working-tree scan, shell syntax, Compose model, and whitespace checks: PASS.

## 6. Security/tenancy checks

- Unauthenticated and malformed-token application requests return 401.
- A subject sees only organizations backed by its active memberships.
- Foreign organization identifiers return 404 without revealing tenant existence.
- Forged token organization/role claims cannot override database membership or role.
- Insufficient active membership roles return RFC 9457 403 responses.

## 7. Assumptions and deviations

- The database is authoritative for tenant roles; Keycloak realm roles are illustrative local identity data.
- Local direct password grants exist only to automate the smoke check and are not a production authentication design.
- Fast authorization tests use H2 PostgreSQL mode; the same migrations and signed-token flow were additionally verified against PostgreSQL 18.6.
- Render is a preview target only; Neon and Auth0 remain external runtime dependencies configured through secrets.

## 8. Known gaps

- Organization administration and invitation workflows belong to a later explicitly authorized scope.
- Fine-grained permissions beyond the current organization roles will be introduced with their owning features.
- The local MinIO-compatible image remains unapproved for production and must be resolved before Milestone 3.
- The Render/Neon/Auth0 deployment has not yet been executed or authenticated end-to-end.
- Any externally disclosed database credential must be rotated before deployment.

## 9. How to run and verify locally

Follow `docs/LOCAL_DEVELOPMENT.md`, then run:

```bash
./mvnw --batch-mode --no-transfer-progress clean verify
docker compose --env-file .env -f infra/compose.yaml up -d --pull never
./infra/scripts/verify-bootstrap.sh
docker build --tag resolveos-api:deployment-test .
./infra/scripts/verify-container.sh
```

## 10. Next milestone

Configure the Render Blueprint with rotated Neon credentials and Auth0 issuer/audience, deploy, then run `infra/scripts/verify-deployment.sh`. Milestone 2 may begin only when explicitly authorized; no Milestone 2 business code has been added.
