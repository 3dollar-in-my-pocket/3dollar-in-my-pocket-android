# TH-1401 큐레이션 AdMob 연결

## 승인·범위 — 2026-10-08

사용자가 기존 AdMob SDK를 재사용해 새 큐레이션 광고 뷰·서버 노출/클릭 로그를 연결하도록 진행을 승인했다. SDK 초기화와 기존 주변 목록 광고는 이미 있다. 이번 작업은 새 큐레이션 슬롯을 연결하며 의존성·버전·서버 API를 변경하지 않는다.

- 서버 ADMOB_CARD는 광고 위치·height·clickLog·impressionLog를 지정한다. 폭/광고 소재/ad unit은 보내지 않는다.
- 공식 고정 배너 규격에 맞춰 height50은320×50 BANNER, height100은320×100 LARGE_BANNER를 사용한다. 일반 가게 카드는100dp를 유지하고 큐레이션 가로 광고 슬롯만320dp로 넓힌다. 50미만 높이나 배너를 충분히 보여줄 폭이 없는 슬롯은 제외한다.
- 실제 화면의 SDK onAdImpression/onAdClicked에서 원본 서버 로그를 전송한다. onAdLoaded나 응답 파싱은 노출 로그를 만들지 않는다. 각 실제 광고 로드 사이클에서 중복 노출 콜백을 억제하고 자동 새 광고 로드는 새 사이클로 취급한다.
- 로드 실패는 해당 응답의 광고 자리와 공간을 제거한다. 다른 광고와 가게 카드는 유지하며 새 서버 응답의 새 광고 ID는 다시 시도한다. 서버 두 factory는 호출마다 UUID를 생성한다.
- 페이지/탭에서 광고 뷰가 제거되면 listener와 AdView를 해제하고 pause/resume을 화면 수명주기에 맞춘다.
- release는 기존 `admob_list_banner` 리소스, debug는 Google 공식 테스트 배너 단위로 검증한다. 기존 주변 광고 구현은 이번 수정에서 바꾸지 않는다.
- TC-7: 크기/좁은 폭/실패/실제 노출 이벤트·중복·재로드·dispose 이후 콜백은 유닛 RED→GREEN, SDK 표시와 스크롤·탭·실패는 에뮬레이터 테스트 광고로 검증한다. 기존 표시 UI TDD 예외를 유지한다. 실광고/실기기는 별도 결과로 구분한다.

원천: [고정 배너 규격](https://developers.google.com/admob/android/banner/fixed-size), [AdView 이벤트와 해제](https://developers.google.com/admob/android/banner).

## 진행

- [x] SDK·서버 계약·기존 광고 경로와 지원 크기 확인.
- [x] 공통 SDUI에 광고 폭·숨김 ID 전달 계약 추가.
- [x] 광고 크기·이벤트 gate RED→GREEN.
- [x] 기존 SDK를 사용하는 큐레이션 광고 renderer와 실제 홈 연결.
- [x] 독립 리뷰·전체 유닛·Debug 빌드·테스트 광고 화면 증거.

## 결과

- 공통 SDUI는 SDK를 직접 참조하지 않고 광고 폭·숨김 ID·광고 슬롯을 받는다. 실제 홈에서 app 모듈의 `HomeCurationAdMobCard`를 주입했다. 320dp 광고 폭, 서버 슬롯 높이, 원본 server log를 보존한다.
- 크기 정책6개·이벤트 gate6개: RED12/9개 예상 AssertionError → GREEN12/0개 실패. 일반 성공 onLoaded에서 로그를 보내지 않고 SDK onAdImpression/onAdClicked에서만 전송하며, 자동 refresh의 새 광고는 새 로드 사이클이다.
- 최종 JDK17 `./gradlew :app:assembleDebug testDebugUnitTest --console=plain` PASS. 전체271개(failure/error/skipped 0), app173/data51/domain4/core-common29/core-network14. 중간 SDK AdListener의 nullable 해제 컴파일 오류를 무동작 listener 교체 후 destroy로 수정하고 새 통합 빌드로 확인했다.
- 독립 SDK/레이아웃 리뷰에 중요 확정 결함은 없었다. 안정 ID의 새 응답 재시도 후보는 현재 서버가 factory 호출마다 `ADMOB:${UUID.randomUUID()}`를 생성함을 확인해 제외했다. 실패 ID는 현재 광고 ID 집합과 교집합으로 정리한다. 서버가 나중에 ID를 안정화하면 응답 세대와 실패 범위를 함께 조정해야 한다.
- 실제 개발 서버 홈 + Google 공식 테스트 광고로 두 크기 표시, 실제 클릭/노출 로그, 필터 재구성의 요청 중복 없음, 탭 해제·복귀 새 로드,320dp 좁은 폭, SDK 실패/공간 제거, 새 카테고리 응답 재로드를 확인했다. 광고 내용은100×100 Native로 임의 재구성하지 않는다.
- 최종 `git diff --check`와 feature 문서 참조 확인 PASS. 의존성·SDK 버전·API·기존 주변 광고 코드는 바꾸지 않았다.

## TC-7 에뮬레이터 증거

Pixel_7_API_35/API35/arm64, 정상 launcher로 실행했다. 공개 서울시청 테스트 좌표를 사용했고 debug의 신규 큐레이션은 공식 Google 테스트 단위로 요청했다. 실광고를 누르지 않았다.

`build/harness/th1401-admob/`에 PNG/XML·필터링 로그·`qa-results.json`·빌드와 RED/GREEN 로그를 저장했다. 실제 화면과 로그 단언9개를 확인했다.

| 검증 | 관측 결과 | 증거 |
|---|---|---|
| 가로·섹션 배너 | 411dp 화면에서 SDK WebView320×100/320×50 표시. 서버 노출 extras와 SDK impression 수가 일치 | `carousel-ad.png/xml`, `full-before-scroll-logs.json` |
| 클릭 | 테스트 배너를1회 눌러 실제 SDK click1회와 POPULAR_SNACKS/BUNGEOPPANG 서버 click1회 | `test-ad-click-logs.json` |
| 필터 재구성 | 주변 list 조회만 발생하고 큐레이션 SDK request0 | `recomposition-logs.json` |
| 탭 왕복 | 이탈 release3개, 복귀 request3개·실제impression3개·서버impression3개. Cur section 중복 조회 없음 | `tab-away-logs.json`, `tab-return-logs.json` |
| 좁은 화면 | 320dp에서 가로 광고는 제외하고 가게 카드 유지, 전체 폭50dp 배너만 표시 | `narrow-320.png/xml` |
| SDK 실패 | 통신 중단 후 새 요청3개가 실패하고 release3개. 가게 목록 유지, 서버 광고 impression0, 섹션50dp 자리도 제거 | `load-failed.png/xml`, `load-failed-logs.json` |
| 새 응답 재시도 | 연결 복구 후 소금빵 cards API의 새 광고 UUID가320×100으로 loaded/impression, 원본 categoryId 로그 확인 | `category-retry-logs.json` |

가로 광고는 좌우20dp 목록 여백을 제외한 가시 폭이320dp 이상일 때만 표시한다. 섹션 배너는 전체 시트 폭320dp 이상이면 표시한다. 320dp 검증 전 밀도/해상도를 각각 바꾸는 중간360dp 상태에서 이전 광고 요청이 잠깐 생겨 즉시 해제된 기록과 최종320dp 표시를 구분했다.

초기 launcher 캡처의 로딩/빈 상태는 응답 완료 뒤 다시 확인했다. 테스트 광고는 외부 Chrome 리디렉션을 열었고 첫 Back만으로 홈에 돌아오지 않아 후속 탭 조작이 실패했다. 실제 브라우저 상태를 확인해 Back으로 홈 복귀한 뒤 탭/필터 시나리오를 다시 수행했다. 이전 실패 캡처를 성공 근거로 사용하지 않는다. AndroidRuntime 앱 오류는 관측되지 않았다.

## 남은 범위

- SDK 연결과 debug 테스트 광고 검증은 완료했다. release 실제 광고 fill·실기기 노출/클릭·운영 광고 단위 검증은 미실행이며 TC-7 실광고 수동 항목을 완료로 처리하지 않는다.
- 광고 노출은 실제 SDK 이벤트 기준이다. SDUI 응답의 모든 광고를 미리 노출로 집계하지 않는다. 기존 주변 목록의 별도 노출 방식은 이번 수정 범위 밖이다.
- 검증용 size/density override를 제거했고 Wi-Fi와 mobile data를 원래 활성 상태로 복구했다. 검증용 에뮬레이터도 종료했다.
- 사용자 변경·artifacts/를 보존했고 커밋·푸시·배포·외부 업로드는 수행하지 않았다.
