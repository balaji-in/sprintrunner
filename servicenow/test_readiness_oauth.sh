#!/usr/bin/env bash
# Tests the Release Readiness API with an OAuth client-credentials token.
# Prompts for the client ID and secret, so nothing sensitive is stored in this file.
#
# Usage: ./servicenow/test_readiness_oauth.sh [version ...]
#        ./servicenow/test_readiness_oauth.sh S24.0 S25

set -uo pipefail

INSTANCE_URL="${SN_INSTANCE_URL:-https://dprtesting1.service-now.com}"
API_PATH="/api/snc/release_readiness_api/release/readiness"

read -r -p "OAuth Client ID: " CLIENT_ID
read -r -s -p "OAuth Client Secret: " CLIENT_SECRET
echo

if [[ -z "$CLIENT_ID" || -z "$CLIENT_SECRET" ]]; then
  echo "Client ID and secret are both required." >&2
  exit 1
fi

token_body=$(mktemp)
trap 'rm -f "$token_body"' EXIT

token_status=$(curl -sS -o "$token_body" -w '%{http_code}' -X POST \
  -H "Accept: application/json" \
  --data-urlencode "grant_type=client_credentials" \
  --data-urlencode "client_id=$CLIENT_ID" \
  --data-urlencode "client_secret=$CLIENT_SECRET" \
  "${INSTANCE_URL%/}/oauth_token.do")
if [[ "$token_status" != "200" ]]; then
  echo "Token request failed with HTTP $token_status. Response from ServiceNow:" >&2
  cat "$token_body" >&2
  echo >&2
  exit 1
fi

token=$(jq -r '.access_token // empty' "$token_body")
if [[ -z "$token" ]]; then
  echo "No access token in the response:" >&2
  cat "$token_body" >&2
  exit 1
fi
echo "Token obtained."

versions=("$@")
if [[ ${#versions[@]} -eq 0 ]]; then
  versions=("S24.0")
fi

for version in "${versions[@]}"; do
  echo
  echo "== Version $version"
  body=$(mktemp)
  status=$(curl -sS -o "$body" -w '%{http_code}' --get \
    -H "Authorization: Bearer $token" \
    -H "Accept: application/json" \
    --data-urlencode "version=$version" \
    "${INSTANCE_URL%/}${API_PATH}")
  echo "HTTP $status"
  jq . "$body" 2>/dev/null || cat "$body"
  rm -f "$body"
done
