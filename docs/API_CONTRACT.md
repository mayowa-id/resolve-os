# API contract

All application endpoints require a bearer access token issued for `resolveos-api`. Error responses use RFC 9457 problem details.

## Identity and tenancy

| Method | Path | Result |
|---|---|---|
| `GET` | `/api/v1/me/organizations` | Active organizations for the authenticated subject |
| `GET` | `/api/v1/organizations/{organizationId}` | One accessible active organization |
| `GET` | `/api/v1/organizations/{organizationId}/memberships` | Active memberships; organization administrator or operations manager only |

An unknown or inaccessible organization returns 404. An authenticated member with an insufficient database role returns 403. An absent, invalid, expired, incorrectly signed, wrong-issuer, or wrong-audience token returns 401.
