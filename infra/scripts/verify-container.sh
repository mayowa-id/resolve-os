#!/usr/bin/env bash
set -euo pipefail

project_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "$project_dir"

image="${1:-resolveos-api:deployment-test}"
container_name="resolveos-api-container-check-$$"
container_port=19080
database_name="resolveos_container_check_$$"

if [[ -f .env ]]; then
  set -a
  # shellcheck disable=SC1091
  source .env
  set +a
fi

: "${RESOLVEOS_DB_PASSWORD:?RESOLVEOS_DB_PASSWORD must be set without placing it on the command line}"

cleanup() {
  docker rm --force "$container_name" >/dev/null 2>&1 || true
  docker exec resolveos-postgres-1 dropdb --if-exists --force \
    --username "${RESOLVEOS_DB_USER:-resolveos}" "$database_name" >/dev/null 2>&1 || true
}
trap cleanup EXIT

configured_user="$(docker image inspect --format '{{.Config.User}}' "$image")"
if [[ "$configured_user" == "" || "$configured_user" == "0" || "$configured_user" == "root" ]]; then
  echo "Image must configure a non-root runtime user" >&2
  exit 1
fi

docker exec resolveos-postgres-1 createdb \
  --username "${RESOLVEOS_DB_USER:-resolveos}" "$database_name"

docker run --detach --network host \
  --name "$container_name" \
  --env SPRING_PROFILES_ACTIVE=production \
  --env PORT="$container_port" \
  --env RESOLVEOS_DB_URL="jdbc:postgresql://localhost:${RESOLVEOS_DB_PORT:-55432}/$database_name" \
  --env RESOLVEOS_DB_USER="${RESOLVEOS_DB_USER:-resolveos}" \
  --env RESOLVEOS_DB_PASSWORD \
  --env RESOLVEOS_OIDC_ISSUER="http://localhost:${KEYCLOAK_PORT:-8180}/realms/resolveos" \
  --env RESOLVEOS_OIDC_AUDIENCE=resolveos-api \
  --env JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=70 -XX:InitialRAMPercentage=20 -XX:+ExitOnOutOfMemoryError" \
  "$image" >/dev/null

for _ in {1..180}; do
  if curl --fail --silent "http://127.0.0.1:$container_port/actuator/health/readiness" >/dev/null; then
    unauthenticated_status="$(curl --silent --output /dev/null --write-out '%{http_code}' \
      "http://127.0.0.1:$container_port/api/v1/me/organizations")"
    if [[ "$unauthenticated_status" != "401" ]]; then
      echo "Container returned $unauthenticated_status instead of 401" >&2
      exit 1
    fi
    docker exec --interactive resolveos-postgres-1 psql \
      --username "${RESOLVEOS_DB_USER:-resolveos}" \
      --dbname "$database_name" \
      --set=organization_id=aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaaa \
      --set=organization_slug=container-check \
      --set=organization_name='Container Check' \
      --set=membership_id=11111111-1111-4111-8111-111111111111 \
      --set=subject_id=container-check-subject \
      <infra/scripts/bootstrap-organization.sql >/dev/null
    membership_count="$(docker exec resolveos-postgres-1 psql \
      --username "${RESOLVEOS_DB_USER:-resolveos}" \
      --dbname "$database_name" --tuples-only --no-align \
      --command "select count(*) from memberships where subject_id = 'container-check-subject';")"
    if [[ "$membership_count" != "1" ]]; then
      echo "Organization bootstrap verification failed" >&2
      exit 1
    fi
    echo "Container verification passed with runtime user $configured_user."
    exit 0
  fi
  if ! docker container inspect "$container_name" >/dev/null 2>&1; then
    echo "Container exited before becoming ready" >&2
    exit 1
  fi
  if [[ "$(docker container inspect --format '{{.State.Running}}' "$container_name")" != "true" ]]; then
    echo "Container exited before becoming ready" >&2
    docker logs "$container_name" >&2
    exit 1
  fi
  sleep 1
done

echo "Container did not become ready" >&2
docker logs "$container_name" >&2
exit 1
