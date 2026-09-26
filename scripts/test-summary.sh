#!/usr/bin/env bash
# JUnit XML 결과를 PR 코멘트용 마크다운으로 만든다.
# 사용: scripts/test-summary.sh [검색 루트]   (기본: 저장소 루트)
# 문서: docs/process/testing.md "CI (PR 증거)"
set -uo pipefail

cd "$(dirname "$0")/.."
ROOT="${1:-.}"

python3 - "$ROOT" <<'PY'
import glob, os, re, sys, xml.etree.ElementTree as ET

root = sys.argv[1]
files = sorted(glob.glob(os.path.join(root, "**", "build", "test-results", "**", "TEST-*.xml"), recursive=True))

if not files:
    print("### ⚠️ 유닛 테스트 결과 파일이 없습니다\n")
    print("`./gradlew testDebugUnitTest` 가 실행되지 않았거나 리포트 경로가 바뀌었습니다.")
    raise SystemExit(0)

TC_RE = re.compile(r"^TH(\d+)_TC(\d+)_(.*)$")

total = failed = skipped = 0
failures = []   # (classname, name, message)
tc_rows = []    # (ticket, tcnum, name, classname, ok)

for path in files:
    try:
        tree = ET.parse(path)
    except ET.ParseError:
        continue
    for case in tree.getroot().iter("testcase"):
        total += 1
        name = case.get("name", "")
        cls = (case.get("classname") or "").split(".")[-1]
        fail_nodes = list(case.findall("failure")) + list(case.findall("error"))
        is_skipped = case.find("skipped") is not None
        if fail_nodes:
            failed += 1
            msg = (fail_nodes[0].get("message") or "").strip().replace("\n", " ")[:160]
            failures.append((cls, name, msg))
        elif is_skipped:
            skipped += 1
        m = TC_RE.match(name)
        if m:
            tc_rows.append((f"TH-{m.group(1)}", int(m.group(2)), name, cls, not fail_nodes and not is_skipped))

head = "✅" if failed == 0 else "❌"
print(f"### {head} 유닛 테스트 — 전체 {total}, 실패 {failed}, 건너뜀 {skipped}\n")

if failures:
    print(f"#### 실패 ({len(failures)})\n")
    print("| 클래스 | 테스트 | 메시지 |")
    print("|---|---|---|")
    for cls, name, msg in failures[:40]:
        print(f"| `{cls}` | `{name}` | {msg or '—'} |")
    if len(failures) > 40:
        print(f"\n_외 {len(failures) - 40}건_")
    print()

print("#### TC 커버리지\n")
if tc_rows:
    print("| 티켓 | TC | 테스트 | 클래스 | 결과 |")
    print("|---|---|---|---|---|")
    for ticket, tcnum, name, cls, ok in sorted(tc_rows, key=lambda r: (r[0], r[1], r[2])):
        print(f"| {ticket} | TC{tcnum} | `{name}` | `{cls}` | {'✅' if ok else '❌'} |")
    print()
    print("_스펙 TC 중 이 표에 없는 번호가 미커버 TC 입니다 (자동화·수동 계층이면 PR 본문 체크리스트를 보세요)._")
else:
    print("`TH{티켓}_TC{n}_` 네이밍의 테스트가 없습니다. 이번 티켓 TC 가 전부 자동화·수동 계층이면 정상이고, 아니면 `docs/process/testing.md` 네이밍 규칙을 확인하세요.")
PY
