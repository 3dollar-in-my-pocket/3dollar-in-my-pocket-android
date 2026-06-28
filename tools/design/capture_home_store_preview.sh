#!/usr/bin/env bash
set -euo pipefail

OUT_DIR="${OUT_DIR:-build/design-verification/home-store-preview}"
PACKAGE_NAME="${PACKAGE_NAME:-com.zion830.threedollars.dev}"
ACTIVITY_NAME="${ACTIVITY_NAME:-com.zion830.threedollars.ui.home.design.HomeStorePreviewDesignActivity}"
FIGMA_REFERENCE="${FIGMA_REFERENCE:-}"
CAPTURE_DELAY_SECONDS="${CAPTURE_DELAY_SECONDS:-3}"

mkdir -p "${OUT_DIR}"

ORIGINAL_SIZE="$(adb shell wm size | tr -d '\r')"
ORIGINAL_DENSITY="$(adb shell wm density | tr -d '\r')"
ORIGINAL_FONT_SCALE="$(adb shell settings get system font_scale | tr -d '\r')"

restore_device() {
  adb shell wm size reset >/dev/null
  adb shell wm density reset >/dev/null
  if [[ -n "${ORIGINAL_FONT_SCALE}" && "${ORIGINAL_FONT_SCALE}" != "null" ]]; then
    adb shell settings put system font_scale "${ORIGINAL_FONT_SCALE}" >/dev/null
  fi
}

trap restore_device EXIT

adb shell wm size 375x812 >/dev/null
adb shell wm density 160 >/dev/null
adb shell settings put system font_scale 1.0 >/dev/null

./gradlew :app:installDebug

adb shell am force-stop "${PACKAGE_NAME}" >/dev/null
adb shell am start -n "${PACKAGE_NAME}/${ACTIVITY_NAME}" >/dev/null
sleep "${CAPTURE_DELAY_SECONDS}"
adb exec-out screencap -p > "${OUT_DIR}/app-full.png"

echo "original_size=${ORIGINAL_SIZE}" > "${OUT_DIR}/device.txt"
echo "original_density=${ORIGINAL_DENSITY}" >> "${OUT_DIR}/device.txt"
echo "original_font_scale=${ORIGINAL_FONT_SCALE}" >> "${OUT_DIR}/device.txt"
echo "capture=${OUT_DIR}/app-full.png"

if [[ -n "${FIGMA_REFERENCE}" ]]; then
  COMPARE_ARGS=(
    --figma "${FIGMA_REFERENCE}"
    --capture "${OUT_DIR}/app-full.png"
    --out-dir "${OUT_DIR}"
  )
  if [[ -n "${DESIGN_MAX_MAE:-}" ]]; then
    COMPARE_ARGS+=(--max-mae "${DESIGN_MAX_MAE}")
  fi
  if [[ -n "${DESIGN_MAX_MISMATCH_PERCENT:-}" ]]; then
    COMPARE_ARGS+=(--max-mismatch-percent "${DESIGN_MAX_MISMATCH_PERCENT}")
  fi
  python3 tools/design/compare_home_store_preview.py "${COMPARE_ARGS[@]}"
fi
