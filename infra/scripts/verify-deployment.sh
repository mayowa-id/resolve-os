#!/usr/bin/env bash
set -euo pipefail

if [[ $# -ne 1 ]]; then
  echo "Usage: $0 https://service.example.com" >&2
  exit 64
fi

base_url="${1%/}"
if [[ "$base_url" != https://* ]]; then
  echo "Deployment URL must use HTTPS" >&2
  exit 64
fi

health_status="$(curl --silent --show-error --output /dev/null --write-out '%{http_code}' \
  "$base_url/actuator/health/readiness")"
if [[ "$health_status" != "200" ]]; then
  echo "Readiness endpoint returned $health_status" >&2
  exit 1
fi

unauthenticated_status="$(curl --silent --show-error --output /dev/null --write-out '%{http_code}' \
  "$base_url/api/v1/me/organizations")"
if [[ "$unauthenticated_status" != "401" ]]; then
  echo "Protected endpoint returned $unauthenticated_status without a token" >&2
  exit 1
fi

invalid_token_status="$(curl --silent --show-error --output /dev/null --write-out '%{http_code}' \
  --oauth2-bearer 'invalid-deployment-verification-token' \
  "$base_url/api/v1/me/organizations")"
if [[ "$invalid_token_status" != "401" ]]; then
  echo "Protected endpoint returned $invalid_token_status for an invalid token" >&2
  exit 1
fi

if [[ -n "${RESOLVEOS_TEST_ACCESS_TOKEN:-}" ]]; then
  curl --fail --silent --show-error \
    --oauth2-bearer "$RESOLVEOS_TEST_ACCESS_TOKEN" \
    "$base_url/api/v1/me/organizations" >/dev/null
  echo "Deployment verification passed, including authenticated access."
else
  echo "Deployment verification passed without authenticated access; set RESOLVEOS_TEST_ACCESS_TOKEN to include it."
fi
