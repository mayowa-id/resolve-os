# ResolveOS

ResolveOS is a production-oriented, multi-tenant onboarding operations platform. Development proceeds one verified milestone at a time; the AI layer is intentionally deferred until the deterministic workflow is complete.

## Prerequisites

- JDK 25
- Docker Engine with Compose v2
- Git

Maven is downloaded by the checked-in Maven Wrapper, so a separate Maven installation is not required.

## Verify the codebase

```bash
./mvnw clean verify
```

On Windows PowerShell:

```powershell
.\mvnw.cmd clean verify
```

## Start local infrastructure

```bash
cp .env.example .env
docker compose --env-file .env -f infra/compose.yaml up -d
docker compose --env-file .env -f infra/compose.yaml ps
```

All host-facing development ports bind to `127.0.0.1` and can be changed in `.env`. The defaults are PostgreSQL `5432`, Keycloak `8180`, MinIO `9000/9001`, Mailpit `1025/8025`, and Temporal `7233/8233`.

## Run the processes

```bash
./mvnw -pl apps/api -am spring-boot:run
./mvnw -pl apps/worker -am spring-boot:run
```

The API exposes a public, detail-free health endpoint at `http://localhost:8080/actuator/health`. All other routes require a valid JWT. Milestone 1 will implement tenant memberships and role authorization.

See [local development](docs/LOCAL_DEVELOPMENT.md), [architecture](docs/ARCHITECTURE.md), and [implementation status](docs/IMPLEMENTATION_STATUS.md).

