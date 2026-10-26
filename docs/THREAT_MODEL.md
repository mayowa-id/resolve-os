# Threat model

This is the Milestone 0 baseline. Detailed STRIDE mappings and executable security scenarios grow with each feature.

| Asset / boundary | Initial threat | Baseline control | Future verification |
|---|---|---|---|
| API | Unauthenticated data access | Deny all routes except detail-free health; OAuth2 resource server | Milestone 1 JWT and role tests |
| Tenant data | Cross-tenant access | Mandatory tenant-scoped design rules | Milestone 1 repository/service isolation tests |
| Credentials | Secret committed or logged | `.env` ignored, placeholder example, CI secret scan | CI scan and runtime log tests |
| Database | Drift or unsafe runtime DDL | Flyway-only changes; Hibernate `validate` | Migration-from-zero tests |
| Files | Malicious/sensitive upload | No upload surface yet; bounded quarantine design | Milestone 3 adversarial tests |
| AI boundary | Prompt-driven privileged action | No AI dependency/runtime yet; model output always untrusted | Milestone 7 policy tests |
| External actions | Duplicate or ambiguous side effects | No side effects yet; idempotency/reconciliation required | Milestones 5, 6, and 8 |
| Local admin UIs | Accidental network exposure | All published ports bind to loopback | Compose inspection/smoke test |

