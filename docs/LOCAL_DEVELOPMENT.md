# Local development

## First-time setup

1. Install JDK 25 and Docker Desktop/Engine with Compose v2.
2. Copy `.env.example` to `.env`; the defaults are synthetic and local-only.
3. Run `.\mvnw.cmd clean verify` on Windows or `./mvnw clean verify` on Unix.
4. Validate configuration with `docker compose --env-file .env -f infra/compose.yaml config`.
5. Start services with `docker compose --env-file .env -f infra/compose.yaml up -d`.

Do not expose the local admin interfaces to a non-local network. Compose binds published ports to loopback by default.

## Services

| Service | Default address |
|---|---|
| PostgreSQL | `localhost:5432` |
| Keycloak | `http://localhost:8180` |
| MinIO API / console | `http://localhost:9000` / `http://localhost:9001` |
| Mailpit UI | `http://localhost:8025` |
| Temporal / UI | `localhost:7233` / `http://localhost:8233` |

## Application

Start the API and worker in separate terminals. The API requires PostgreSQL and Keycloak configuration; the worker currently starts only its Spring dependency-injection context and registers no jobs.

```powershell
.\mvnw.cmd -pl apps/api -am spring-boot:run
.\mvnw.cmd -pl apps/worker -am spring-boot:run
```

Stop local dependencies with `docker compose --env-file .env -f infra/compose.yaml down`. Add `--volumes` only when deliberately discarding all local development data.

