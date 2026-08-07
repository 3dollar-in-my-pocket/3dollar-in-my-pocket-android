#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "${ROOT_DIR}"

ADB="${ADB:-adb}"
PACKAGE_NAME="${PACKAGE_NAME:-com.zion830.threedollars.dev}"
ACTIVITY_NAME="${ACTIVITY_NAME:-com.zion830.threedollars.ui.splash.ui.SplashActivity}"
START_DELAY_SECONDS="${START_DELAY_SECONDS:-5}"
OUT_DIR="${OUT_DIR:-build/harness/android-smoke/$(date +%Y%m%d-%H%M%S)}"

adb_cmd() {
  if [[ -n "${ANDROID_SERIAL:-}" ]]; then
    "${ADB}" -s "${ANDROID_SERIAL}" "$@"
  else
    "${ADB}" "$@"
  fi
}

mkdir -p "${OUT_DIR}"

if ! command -v "${ADB}" >/dev/null 2>&1; then
  echo "adb command not found: ${ADB}" >&2
  exit 127
fi

adb_cmd get-state >/dev/null

echo "== install debug apk =="
./gradlew :app:installDebug

echo "== launch app =="
adb_cmd logcat -c
adb_cmd shell am force-stop "${PACKAGE_NAME}" >/dev/null
adb_cmd shell am start -n "${PACKAGE_NAME}/${ACTIVITY_NAME}" > "${OUT_DIR}/am-start.txt"
sleep "${START_DELAY_SECONDS}"

echo "== collect artifacts =="
adb_cmd shell pidof "${PACKAGE_NAME}" > "${OUT_DIR}/process.txt" || true
adb_cmd exec-out screencap -p > "${OUT_DIR}/screenshot.png"
adb_cmd shell uiautomator dump /sdcard/window.xml > "${OUT_DIR}/uiautomator-dump.txt" 2>&1 || true
adb_cmd exec-out cat /sdcard/window.xml > "${OUT_DIR}/window.xml" 2>/dev/null || true
adb_cmd logcat -d -b crash > "${OUT_DIR}/crash.log" || true

if [[ ! -s "${OUT_DIR}/process.txt" ]]; then
  echo "app process is not running: ${PACKAGE_NAME}" >&2
  echo "artifacts: ${OUT_DIR}" >&2
  exit 1
fi

if grep -E "FATAL EXCEPTION|AndroidRuntime" "${OUT_DIR}/crash.log" >/dev/null 2>&1; then
  echo "crash detected; see ${OUT_DIR}/crash.log" >&2
  exit 1
fi

echo "android smoke passed"
echo "artifacts: ${OUT_DIR}"
