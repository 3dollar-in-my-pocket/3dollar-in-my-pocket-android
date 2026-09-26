#!/usr/bin/env bash
# 모듈 의존 방향 검사.
# 규칙: 모듈은 자기보다 낮은 계층만 의존한다 (app → data → domain → common → core:*).
# 기존 위반은 scripts/module-deps-baseline.txt 에 동결하고, 새 위반만 실패로 처리한다.
# 문서: docs/process/pr-process.md, docs/context/module-dependencies-current.md
set -uo pipefail

cd "$(dirname "$0")/.."

BASELINE="scripts/module-deps-baseline.txt"

# 계층 랭크. 숫자가 클수록 상위. 의존은 "더 낮은 랭크"로만 허용한다.
rank_of() {
  case "$1" in
    :app) echo 50 ;;
    :data) echo 40 ;;
    :domain) echo 30 ;;
    :common) echo 20 ;;
    :core:network) echo 15 ;;
    :core:ui) echo 12 ;;
    :core:common) echo 11 ;;
    :core:abtest) echo 10 ;;
    :core:designsystem) echo 10 ;;
    *) echo -1 ;;
  esac
}

module_of_file() {
  # app/build.gradle.kts -> :app , core/network/build.gradle.kts -> :core:network
  local dir
  dir="$(dirname "$1")"
  [ "$dir" = "." ] && { echo ""; return; }
  echo ":${dir//\//:}"
}

is_baselined() {
  [ -f "$BASELINE" ] || return 1
  grep -qxF "$1 -> $2" "$BASELINE"
}

violations=0
baselined=0
checked=0

while IFS= read -r gradle_file; do
  module="$(module_of_file "$gradle_file")"
  [ -z "$module" ] && continue
  from_rank="$(rank_of "$module")"
  [ "$from_rank" -lt 0 ] && continue

  while IFS= read -r dep; do
    [ -z "$dep" ] && continue
    checked=$((checked + 1))
    to_rank="$(rank_of "$dep")"
    if [ "$to_rank" -lt 0 ]; then
      echo "::warning::$gradle_file — 알 수 없는 모듈 '$dep' (scripts/check-module-deps.sh 의 rank_of 에 추가하세요)"
      continue
    fi
    if [ "$to_rank" -lt "$from_rank" ]; then
      continue
    fi
    if is_baselined "$module" "$dep"; then
      baselined=$((baselined + 1))
      continue
    fi
    echo "::error file=$gradle_file::의존 방향 위반: $module -> $dep (같거나 상위 계층). 방향을 뒤집거나, 정당한 예외면 사유와 함께 $BASELINE 에 '$module -> $dep' 를 추가하세요."
    violations=$((violations + 1))
  done < <(grep -oE 'project\("[^"]+"\)' "$gradle_file" | sed -E 's/project\("([^"]+)"\)/\1/' | sort -u)
done < <(find . -name 'build.gradle.kts' -not -path './build/*' -not -path '*/build/*' | sed 's|^\./||' | sort)

echo "모듈 의존성 검사: 의존 간선 ${checked}개 확인, 동결 ${baselined}건, 신규 위반 ${violations}건"

if [ "$violations" -gt 0 ]; then
  exit 1
fi
