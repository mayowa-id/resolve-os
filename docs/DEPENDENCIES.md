# Dependency and license record

Versions were checked against authoritative project release pages on 2026-10-26. Transitive dependencies remain governed by the Spring Boot BOM and will be captured by automated inventory/scanning as the build environment becomes available.

| Component | Version | Purpose | License |
|---|---:|---|---|
| Java | 25 LTS | Runtime/toolchain | GPL-2.0 with Classpath Exception (OpenJDK) |
| Spring Boot | 4.1.2 | Application framework/BOM | Apache-2.0 |
| Spring AI BOM | 2.0.2 | Reserved for Milestone 7; no AI runtime dependency yet | Apache-2.0 |
| Temporal Java SDK | 1.40.0 | Reserved for workflow milestone; version pin only | MIT |
| PostgreSQL JDBC | Boot BOM | Database driver | BSD-2-Clause |
| Flyway | Boot BOM | Schema migrations | Apache-2.0 |
| PostgreSQL image | 18.6-alpine3.24 | Local authoritative database | PostgreSQL License |
| Keycloak image | 26.4.2 | Local OIDC provider | Apache-2.0 |
| MinIO image | RELEASE.2025-10-15T17-29-55Z | Local S3-compatible storage | AGPL-3.0 |
| Mailpit image | 1.32.0 | Local mail capture | MIT |
| Temporal server image | 1.29.7 | Local durable workflow service | MIT |
| Temporal UI image | 2.57.0 | Local workflow inspection | MIT |

Do not add dependencies without recording their version source, maintenance status, purpose, and license compatibility.

