# TH-1401 — 4.29.0 릴리즈 PR 준비·검증

기록: 2026-10-09. 사용자가 커밋 정리·PR 생성과 `release/v4.29.0` 생성을 승인했다. 원격에는 릴리즈 브랜치가 없고, 최신 배포는 v4.28.0이었다. 이번 작업의 초기4.26.0 기반 기록은 이전 문서에 보존한다.

## 릴리즈 기준·변경 정리

- 최신 `origin/develop`의 `10278e6f`에서 `release/v4.29.0`을 만들고 `version_name=4.29.0`, `version_code=136`을 반영했다(버전 커밋 `2116a6bb`).
- TH-1401은 공통 SDUI/데이터 경계(`3040edbb`), 실제 홈/광고/상태·테스트(`c062dab4`), debug 디자인·검증 기록(`14284be3`)의 세 묶음으로 커밋했다.
- 최신 릴리즈 기준을 병합한 `a4c5a9ff`에서 전체 유닛·Debug/Release 빌드를 검증했다. debug Manifest의 디자인 화면과 GA 도구를 함께 유지하고, 주변 목록에 새 광고 클릭 콜백·높이·빈 카드 처리를 보존했다.
- 지도 버튼의 중복 표시 조건은 기존 큐레이션 표시 정책 하나로 통합했다. 릴리즈의 가게 미리보기 중 숨김·상단 필터와 충돌 시 숨김 동작을 유지하며 큰 버튼의 실제 높이와 레이아웃 재측정을 반영한다.
- `.github/workflows/play-release-internal.yml`은 `release/**` push에 Play 내부 배포를 실행한다. 브랜치 생성용 empty commit `4615063`에 `[skip ci]`를 넣어 요청하지 않은 배포를 건너뛴다. 기능 PR의 HEAD에는 skip 지시를 넣지 않는다. 이 empty commit의 병합 후 소스 트리가 검증한 `a4c5a9ff`와 동일함을 `git diff --exit-code`로 확인했다.
- 사용자 `artifacts/`4개는 커밋에서 제외하고 보존했다. 이번 기능 PR에서 SDK/의존성을 추가·업그레이드하거나 로그인 흐름을 바꾸지 않았다. 최신 develop의 기존 변경은 릴리즈 기준으로 승계했다.

## 최종 빌드·유닛·구조

JDK17 `./gradlew :app:assembleDebug :app:assembleRelease testDebugUnitTest --console=plain`: **BUILD SUCCESSFUL**, 2m51s,732개 task 실행.

| 모듈 | 테스트 | 실패/오류/skip |
|---|---:|---:|
| app |209|0/0/0|
| data |67|0/0/0|
| domain |4|0/0/0|
| core/common |34|0/0/0|
| core/network |15|0/0/0|
| 합계 |329|0/0/0|

- XML 결과를 직접 집계했다. 초기4.26.0기준271개와 최신 develop의 기존 테스트를 포함한329개를 구분한다.
- `scripts/check-module-deps.sh`:18개 간선,동결0,신규 위반0. 충돌 마커 없음, `git diff --cached --check` 통과.
- 기존 표시 UI TDD 예외를 유지하며 Composable/instrumentation 테스트 코드를 추가하지 않았다. release의 minify=false는 기준 브랜치와 동일하며 R8 검증이 아니다.
- 로컬 실행 로그: `/tmp/th1401-pr-release429-verification.log`, 결과 집계: `/tmp/th1401-pr429-tests.json`.

## 4.29.0 릴리즈 에뮬레이터 확인

Pixel_7_API_35/API35/arm64,1080×2400,density420,host GPU. 기존 앱·로그인을 유지한 `install -r` 후 정상 Splash launcher로 실행했다. 설치한 package의 versionName4.29.0/versionCode136을 직접 확인했다.

| 확인 범위 | 실제 관측 |
|---|---|
| 홈·탭 | MainActivity 진입. 현재 서버 기본 주변 탭, 큐레이션 탭 전환 및 실제 cards 표시 |
| 카테고리 | 왁뿌 소금빵 선택 후 명동양과·프랑스루브르바게트 등의 cards로 교체 |
| 탭 왕복 | 주변 목록 표시 후 큐레이션 복귀 시 같은 소금빵 선택·cards 유지 |
| 상세 | 명동양과 카드로 기존 상세 진입, Back으로 홈 복귀. 상세 종료의 기존 홈 갱신은 서버 기본 카테고리로 다시 표시되며 상세 왕복의 선택 유지로 보고하지 않는다 |
| 시트·지도 버튼 | 시트 전개 시 버튼 숨김, 지도 보기로 접은 뒤 현재 위치·즐겨찾기·가게 제보 복원 |
| 광고 | 테스트 광고 표시 및320×50/320×100의 SDK loaded/impression 관측. 이번 실행에서 광고를 클릭하지 않았다 |

로컬 증거: `build/harness/th1401-release429/`의 initial,curation-collapsed,expanded-ready,nearby,tab-return,detail,after-detail,collapsed-restored PNG/XML과 `sdk-smoke.json`, `qa-results.json`. 전체 릴리즈 회귀·실기기 광고 완료를 뜻하지 않는다.

SDK 소재에서 ‘미디어를 재생할 수 없습니다’ 문구도 에뮬레이터에서 관측했다. SDK 로드 콜백은 성공했으나 소재 내부 재생·기존 깜빡임의 원인은 미확정이며 수정 완료로 보고하지 않는다. [14 조사 기록](14-admob-flicker-investigation.md)과 함께 TC-7 실기기 확인 항목으로 남긴다.

## PR 증거·남은 항목

- 기존4.26 작업 단계의 검토한 PNG16개를 저장소가 지정한 `verification-assets` 릴리즈에 업로드하고 원격 목록을 확인했다. PR의 Before/After와 TC 증거에 이 링크를 사용하며4.29 전체 화면 증거로 오표기하지 않는다.
- 추가4.29 PNG4개의 공개 업로드는 자동 승인 검토가 가게 주소·리뷰 등의 외부 공개 승인 근거가 부족하다는 이유로 거절했다. 우회 업로드하지 않고 로컬에 보관한다. PR에는 최신 검증 결과를 문서로 연결한다.
- 전용 `3dollars:pr-body` 설치본은 문서상 위치와 로컬 skill/plugin 경로에서 찾지 못했다. `docs/process/pr-process.md`와 `.github/PULL_REQUEST_TEMPLATE.md`의 풀코스 요구사항 표·TC·증거·스펙 밖 변경·결정 설명을 직접 작성한다.
- 실기기 운영 광고, 다크모드·태블릿/폴더블·느린 네트워크의 별도 화면 검증, 원래 깜빡임 제보의 원인 확정은 남는다. Draft PR로 올리고 미실행 항목은 체크하지 않는다. 병합·배포는 실행하지 않는다.
