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
for _ in {1..90}; do
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

echo "Bootstrap verification passed: API healthy, Flyway baseline present, worker started."
