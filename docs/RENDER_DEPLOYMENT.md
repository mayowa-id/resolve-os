# Render deployment

This preview topology runs only the ResolveOS API on Render. Neon provides PostgreSQL and Auth0 provides OIDC. Local-only Keycloak, Temporal, MinIO, Mailpit, synthetic tenants, and the currently inactive worker are not deployed.

## Prerequisites

- A merged commit whose CI checks pass.
- A Neon pooled JDBC endpoint with TLS required.
- An Auth0 API configured for RS256.
- A rotated database password that has never been committed or shared.

## Render Blueprint

Create a new Blueprint from this repository and select `render.yaml`. Keep the repository root directory empty because the Maven reactor needs the root POM and all application modules. Render prompts for every `sync: false` value:

| Variable | Value |
|---|---|
| `RESOLVEOS_DB_URL` | Neon JDBC URL without embedded user or password |
| `RESOLVEOS_DB_USER` | Neon database role |
| `RESOLVEOS_DB_PASSWORD` | Current Neon role password |
| `RESOLVEOS_OIDC_ISSUER` | Auth0 issuer URL, including `https://` and trailing slash |
| `RESOLVEOS_OIDC_AUDIENCE` | Exact Auth0 API identifier |

The JDBC URL has this form:

```text
jdbc:postgresql://HOST/DATABASE?sslmode=require&channelBinding=require
```

Never select the `local` Spring profile. The Blueprint sets `production`, which loads only versioned production migrations and fails startup when required configuration is absent.

## Auth0 configuration

Create an Auth0 API with RS256 signing. Its identifier is the ResolveOS audience. Create a user-facing Auth0 application only when a frontend or authorized test client exists; do not use a management token as an end-user token.

ResolveOS trusts the validated token subject for identity and reads tenant membership and roles from PostgreSQL. Auth0 organization or role claims do not grant tenant access.

## First organization bootstrap

There is intentionally no production seed. After a real user signs in, obtain the exact `sub` from that user's validated access token. Run the reviewed bootstrap script with `psql`, supplying fresh UUIDs and non-secret organization values:

```bash
psql "$RESOLVEOS_DATABASE_ADMIN_URL" \
  --set=organization_id="$(uuidgen)" \
  --set=organization_slug="resolveos" \
  --set=organization_name="ResolveOS" \
  --set=membership_id="$(uuidgen)" \
  --set=subject_id="AUTH0_SUBJECT" \
  --file=infra/scripts/bootstrap-organization.sql
```

Set `RESOLVEOS_DATABASE_ADMIN_URL` outside shell history. Review the subject before execution. The transaction fails rather than silently modifying an existing organization or membership.

## Verification

After Render reports the service healthy, run:

```bash
./infra/scripts/verify-deployment.sh https://resolveos.onrender.com
```

This verifies HTTPS readiness, unauthenticated rejection, and invalid-token rejection. To include a real Auth0 access token without putting it in the command line:

```bash
read -rsp "Access token: " RESOLVEOS_TEST_ACCESS_TOKEN
export RESOLVEOS_TEST_ACCESS_TOKEN
./infra/scripts/verify-deployment.sh https://resolveos.onrender.com
unset RESOLVEOS_TEST_ACCESS_TOKEN
```

## Rollback

Use Render's rollback control to restore the last healthy image. Flyway migrations are forward-only: never delete or edit an applied migration. If a schema correction is needed, ship a new additive migration before redeploying.

## Free-tier limitations

The service may sleep when idle and cold starts can be slow. Its local filesystem is ephemeral. This deployment is not approved for customer data, availability commitments, or production operation.
