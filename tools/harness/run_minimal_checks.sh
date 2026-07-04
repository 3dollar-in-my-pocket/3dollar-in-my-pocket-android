#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "${ROOT_DIR}"

echo "== app home unit tests =="
./gradlew :app:testDebugUnitTest --tests "com.zion830.threedollars.ui.home.*"

echo "== data screen mapper unit tests =="
./gradlew :data:testDebugUnitTest --tests "com.threedollar.data.screen.*"

echo "== debug assemble =="
./gradlew :app:assembleDebug

echo "minimal checks passed"
