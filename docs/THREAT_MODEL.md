# Threat model

This threat model reflects the controls implemented through Milestone 1. Detailed STRIDE mappings and executable security scenarios grow with each feature.

| Asset / boundary | Initial threat | Baseline control | Future verification |
|---|---|---|---|
| API | Forged, expired, or wrong-audience token | OIDC issuer discovery, RS256 signature, timestamp, issuer, and audience validation | MockMvc 401 test and live signed-token smoke check |
| Tenant data | Cross-tenant read or identifier enumeration | Database-authoritative membership lookup; inaccessible tenants return 404 | Two-tenant isolation tests and PostgreSQL smoke check |
| Roles | Forged or stale token role escalation | Ignore token roles; authorize against active membership role | Forged-claim and insufficient-role tests |
| Credentials | Secret committed or logged | `.env` ignored, placeholder example, CI secret scan | CI scan and runtime log tests |
| Database | Drift or unsafe runtime DDL | Flyway-only changes; Hibernate `validate` | Migration-from-zero tests |
| Files | Malicious/sensitive upload | No upload surface yet; bounded quarantine design | Milestone 3 adversarial tests |
| AI boundary | Prompt-driven privileged action | No AI dependency/runtime yet; model output always untrusted | Milestone 7 policy tests |
| External actions | Duplicate or ambiguous side effects | No side effects yet; idempotency/reconciliation required | Milestones 5, 6, and 8 |
| Local admin UIs | Accidental network exposure | All published ports bind to loopback | Compose inspection/smoke test |
