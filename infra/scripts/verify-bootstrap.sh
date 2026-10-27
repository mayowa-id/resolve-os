#!/usr/bin/env bash
set -euo pipefail

project_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "$project_dir"

if [[ -f .env ]]; then
  set -a
  # shellcheck disable=SC1091
  source .env
  set +a
fi

api_log="$(mktemp)"
worker_log="$(mktemp)"
api_pid=""
worker_pid=""

cleanup() {
  [[ -z "$api_pid" ]] || kill "$api_pid" 2>/dev/null || true
  [[ -z "$worker_pid" ]] || kill "$worker_pid" 2>/dev/null || true
  rm -f "$api_log" "$worker_log"
}
trap cleanup EXIT

java -jar apps/api/target/resolveos-api-0.1.0-SNAPSHOT.jar >"$api_log" 2>&1 &
api_pid=$!

api_ready=false
for _ in {1..180}; do
  if ! kill -0 "$api_pid" 2>/dev/null; then
    break
  fi
  if curl --fail --silent http://127.0.0.1:${RESOLVEOS_API_PORT:-18080}/actuator/health; then
    api_ready=true
    break
  fi
  sleep 1
done

if [[ "$api_ready" != true ]]; then
  echo "API did not become healthy" >&2
  tail -n 120 "$api_log" >&2
  exit 1
fi
echo

bootstrap_id="$(docker exec resolveos-postgres-1 psql -U "${RESOLVEOS_DB_USER:-resolveos}" -d "${RESOLVEOS_DB_NAME:-resolveos}" -Atc 'select id from platform_bootstrap;')"
if [[ "$bootstrap_id" != "1" ]]; then
  echo "Flyway bootstrap row was not found" >&2
  exit 1
fi

token_response="$(curl --fail --silent --show-error \
  --request POST \
  --data-urlencode 'client_id=resolveos-api' \
  --data-urlencode 'username=northwind-admin' \
  --data-urlencode 'password=local-only' \
  --data-urlencode 'grant_type=password' \
  "http://localhost:${KEYCLOAK_PORT:-8180}/realms/resolveos/protocol/openid-connect/token")"
access_token="$(sed -n 's/.*"access_token":"\([^"]*\)".*/\1/p' <<<"$token_response")"
if [[ -z "$access_token" ]]; then
  echo "Keycloak did not issue the synthetic tenant token" >&2
  exit 1
fi

organizations="$(curl --fail --silent --show-error \
  --oauth2-bearer "$access_token" \
  "http://127.0.0.1:${RESOLVEOS_API_PORT:-18080}/api/v1/me/organizations")"
if [[ "$organizations" != *'aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaaa'* ]] ||
   [[ "$organizations" == *'bbbbbbbb-bbbb-4bbb-8bbb-bbbbbbbbbbbb'* ]]; then
  echo "Tenant-scoped organization listing was incorrect" >&2
  exit 1
fi

cross_tenant_status="$(curl --silent --output /dev/null --write-out '%{http_code}' \
  --oauth2-bearer "$access_token" \
  "http://127.0.0.1:${RESOLVEOS_API_PORT:-18080}/api/v1/organizations/bbbbbbbb-bbbb-4bbb-8bbb-bbbbbbbbbbbb")"
if [[ "$cross_tenant_status" != "404" ]]; then
  echo "Cross-tenant lookup returned $cross_tenant_status instead of 404" >&2
  exit 1
fi

invalid_token_status="$(curl --silent --output /dev/null --write-out '%{http_code}' \
  --oauth2-bearer 'not-a-signed-jwt' \
  "http://127.0.0.1:${RESOLVEOS_API_PORT:-18080}/api/v1/me/organizations")"
if [[ "$invalid_token_status" != "401" ]]; then
  echo "Invalid token returned $invalid_token_status instead of 401" >&2
  exit 1
fi

java -jar apps/worker/target/resolveos-worker-0.1.0-SNAPSHOT.jar >"$worker_log" 2>&1 &
worker_pid=$!

worker_ready=false
for _ in {1..20}; do
  if grep -q "Started ResolveOsWorkerApplication" "$worker_log"; then
    worker_ready=true
    break
  fi
  if ! kill -0 "$worker_pid" 2>/dev/null; then
    break
  fi
  sleep 1
done

if [[ "$worker_ready" != true ]]; then
  echo "Worker did not start successfully" >&2
  tail -n 120 "$worker_log" >&2
  exit 1
fi

echo "Bootstrap verification passed: API healthy, migrations present, tenant isolation enforced, worker started."
