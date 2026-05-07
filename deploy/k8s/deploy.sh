#!/usr/bin/env bash
#
# iKindle - one-shot deployer for OrbStack Kubernetes.
#
#   1. Build the backend Docker image (ikindle/backend:latest)
#   2. Apply all manifests under deploy/k8s/
#   3. Wait for rollout to finish, then print URLs
#
# Usage:
#   ./deploy/k8s/deploy.sh                # build image + apply + wait
#   ./deploy/k8s/deploy.sh --no-build     # apply only (re-use existing image)
#   ./deploy/k8s/deploy.sh --restart      # rollout restart backend after apply
#   ./deploy/k8s/deploy.sh --clean        # delete the namespace, then redeploy
#
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"
NS="ikindle"
IMAGE="ikindle/backend:latest"
ADMIN_IMAGE="ikindle/admin:latest"

DO_BUILD=true
DO_RESTART=false
DO_CLEAN=false

for arg in "$@"; do
  case "$arg" in
    --no-build) DO_BUILD=false ;;
    --restart)  DO_RESTART=true ;;
    --clean)    DO_CLEAN=true ;;
    -h|--help)
      grep '^#' "$0" | sed 's/^# //; s/^#//'
      exit 0
      ;;
    *) echo "Unknown flag: $arg" >&2; exit 2 ;;
  esac
done

cyan() { printf "\033[36m==>\033[0m %s\n" "$*"; }
green() { printf "\033[32m✓\033[0m %s\n" "$*"; }
red()   { printf "\033[31m✗\033[0m %s\n" "$*" >&2; }

if ! command -v kubectl >/dev/null 2>&1; then
  red "kubectl not found in PATH"; exit 1
fi
if ! command -v docker >/dev/null 2>&1; then
  red "docker not found in PATH"; exit 1
fi

if ! kubectl cluster-info >/dev/null 2>&1; then
  red "kubectl cannot reach a cluster. Start OrbStack and enable Kubernetes."
  exit 1
fi

if $DO_CLEAN; then
  cyan "Deleting namespace ${NS} (if it exists)…"
  kubectl delete namespace "${NS}" --ignore-not-found --wait=true
fi

if $DO_BUILD; then
  cyan "Building image ${IMAGE} …"
  cd "${PROJECT_ROOT}"
  DOCKER_BUILDKIT=1 docker build \
    -t "${IMAGE}" \
    -f backend/Dockerfile \
    backend/
  green "Image built: ${IMAGE}"

  cyan "Building image ${ADMIN_IMAGE} …"
  DOCKER_BUILDKIT=1 docker build \
    -t "${ADMIN_IMAGE}" \
    -f admin/Dockerfile \
    admin/
  green "Image built: ${ADMIN_IMAGE}"
else
  cyan "Skipping image build (--no-build)"
fi

cyan "Applying manifests in ${SCRIPT_DIR}…"
# Apply in sorted order so namespace exists before everything else.
for f in $(ls "${SCRIPT_DIR}"/*.yaml | sort); do
  echo "  - $(basename "$f")"
  kubectl apply -f "$f"
done

if $DO_RESTART; then
  cyan "Rolling restart of backend deployment…"
  kubectl -n "${NS}" rollout restart deployment/backend
fi

cyan "Waiting for postgres StatefulSet…"
kubectl -n "${NS}" rollout status statefulset/postgres --timeout=180s

cyan "Waiting for redis StatefulSet…"
kubectl -n "${NS}" rollout status statefulset/redis --timeout=120s

cyan "Waiting for backend Deployment…"
kubectl -n "${NS}" rollout status deployment/backend --timeout=300s

cyan "Waiting for admin Deployment…"
kubectl -n "${NS}" rollout status deployment/admin --timeout=300s

green "Deployment finished"

cat <<EOF

────────────── iKindle is up ──────────────

Backend API     : http://api.ikindle.local
Health endpoint : http://api.ikindle.local/api/actuator/health
Swagger UI      : http://api.ikindle.local/api/swagger-ui.html
Admin Dashboard : http://admin.ikindle.local

Useful commands:
  kubectl -n ikindle get pods
  kubectl -n ikindle logs -f deployment/backend
  kubectl -n ikindle exec -it postgres-0 -- psql -U ikindle -d ikindle
  kubectl -n ikindle exec -it redis-0 -- redis-cli

If api.ikindle.local does not resolve, OrbStack should add it automatically
the first time the Ingress is created. As a fallback, port-forward:
  kubectl -n ikindle port-forward svc/backend 8080:8080
EOF
