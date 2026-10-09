# TH-1401 2차 검증 결과

검증: 2026-10-08. 승인된 공통 SDUI·최신 API·실홈 연결의 로컬 구현과 검증을 마쳤다. 실광고 SDK를 제외한 범위이며 TC-7 전체 완료나 원격 반영을 뜻하지 않는다. [09 구현 기록](09-phase2-implementation.md), [08 실행 기록](08-phase2-ledger.md).

## 빌드·유닛·구조

- JDK17에서 `./gradlew :app:assembleDebug testDebugUnitTest --console=plain` PASS. 최종 `BUILD SUCCESSFUL`과 XML 결과를 확인했다.
- 전체 253개, failure/error/skipped 모두 0. app 155, data 51, domain 4, core/common 29, core/network 14.
- 신규 TH-1401 43개: API·파서·mapper 12, 순수 상태 20, 조회 정책 11. 기존 210개도 통과했다.
- API·파서·mapper와 순수 상태의 예상 미구현 RED를 관측한 뒤 구현해 GREEN을 확인했다. 조회 정책은 초기 RED와 진행 중 요청의 세대 검사를 누락한 조건의 RED도 확인하고 정상 조건으로 복구했다.
- `scripts/check-module-deps.sh` PASS: 18개 간선, 신규 위반 0. `git diff --check` PASS.
- 의존성·toolchain 업그레이드나 Composable 테스트 코드를 추가하지 않았다. 기존 UI TDD 예외에 따라 화면 증거로 검증했다.
- 최종 통합 로그와 TDD 로그 사본: `build/harness/th1401-phase2/`. 테스트 XML은 각 모듈 `build/test-results/testDebugUnitTest/`에 있다.

## 실홈 에뮬레이터

Pixel_7_API_35 / API35 / arm64, 1080×2400 / density420(약 411dp 폭). 최신 Debug APK를 설치하고 정상 launcher인 `SplashActivity`로 홈을 실행했다. 초기 직접 `MainActivity` 실행은 비공개 Activity라 거절됐고, 앱의 exported 설정을 바꾸지 않고 정상 진입점으로 검증했다.

기기 위치는 서울시청 공개 테스트 좌표를 사용했다. 실제 개발 서버 응답으로 초기 cards를 표시했으며, 카드 API를 초기 화면에서 중복 호출하지 않았다. 카드 사진·가게명·거리·리뷰·평점, 서버 탭 순서와 기본 선택을 확인했다. UI 트리에서 좌표를 확인해 탭·칩·카드를 누르고 시트·목록·지도를 움직였다.

| 테크스펙 TC | 결과·검증 범위 | 대표 증거 |
|---|---|---|
| 1 | mapper/reducer의 서버 순서·기본 탭, 실제 초기 큐레이션 표시 PASS | `final-home-tip.png`, `final-home-full.png` |
| 2, 3 | 주변/큐레이션 전환·각 클릭 로그, 같은 조건의 선택·화면 위치 유지 PASS. 왕복 중 section 재조회 0 | `final-nearby.png`, `final-tab-return.png`, `final-tab-return-logs.json` |
| 4 | items·초기 cards·defaultCategoryId·HTML·메타의 실응답 매핑과 표시 PASS. 광고는 모델까지만 검증 | mapper 유닛, `final-home-full.png`, `final-home-initial-logs.json` |
| 5 | POPULAR_SNACKS→WAKBBU_SALT_BREAD, TASTE_SNACKS→SWEET_TASTE 각각 해당 행 API 1회. 독립 가로 스크롤·선택·실패·재시도 PASS. 늦은 응답/동시 요청은 reducer 유닛으로 검증 | `final-independent-rows.png`, `final-category-error.png`, `final-category-retry.png`, `final-filter-logs.json` |
| 6 | 원본 link/log 매핑, 실제 USER_STORE 카드 클릭 1회와 `/api/v2/screen/store/12805078` 상세 진입 PASS | `final-store-detail.png`, `final-store-detail-logs.json` |
| 7 | ADMOB_CARD 순서·높이·로그 모델 파싱 PASS. 실제 광고 SDK·노출·클릭·실기기는 후속 | API/data 유닛. 전체 TC는 미완료 |
| 8 | unknown type/viewType 파싱·제외, debug 미지원/빈 카드·빈 섹션에서 안정적인 화면 PASS | `final-design-unknown-360.png`, `final-design-empty-cards-360.png`, `final-design-empty-sections-360.png` |
| 9 | 실응답의 null subTitle/trailingAction 보존과 별도 빈 영역 없는 표시 PASS | mapper 유닛, `final-home-full.png`, `final-design-unknown-360.png` |
| 10 | 360dp의 100dp 카드와 긴 제목 한 줄 말줄임·메타 표시 PASS. 기존 상세 카드 기본128dp는 소스/기존 유닛으로 확인 | `final-design-long-360.png`, 해당 XML |
| 11 | 전역 필터는 주변 list만 조회, 카메라 이동·탭 왕복에 curation 조회 0. 주변 탭에서 지도 재검색 후 Cur 복귀 시 확정 새 좌표로 section 조회·서버 기본값 초기화 PASS. 기존 cursor paging도 실제 관측 | `final-filter-logs.json`, `final-pan-tab-cache-logs.json`, `final-requery-return-logs.json`, `final-nearby-paging-logs.json` |
| 12 | 시트 접힘/full·지도 보기·마커 표시, 기존 주변 카드→지도 이동→가게 미리보기→닫기 PASS | `final-home-tip.png`, `final-home-full.png`, `final-existing-preview.png`, `final-after-preview-back.png` |

실홈의 통신을 잠시 끊어 카테고리 실패를 확인했다. 기존 붕어빵 선택·카드를 유지하고 해당 행에 오류/재시도가 표시됐다. 연결 복구 후 재시도에서 요청했던 소금빵 선택·카드로 함께 바뀌었다. section 실패와 요청 경쟁은 유닛으로 확인했으며 별도의 서버 장애를 만들지 않았다.

증거 XML/로그를 대상으로 18개 단언을 확인해 `build/harness/th1401-phase2/qa-results.json`에 기록했다. 화면 사진도 직접 검토했다. 전체 logcat이나 인증 헤더/응답 본문은 저장하지 않고 검증에 필요한 GET 경로와 클릭 식별자만 추출했다. AndroidRuntime 오류는 관측되지 않았다.

## 독립 리뷰와 수정

- 공통 UI와 서버 계약/mapper의 독립 리뷰에서 중요한 결함이 남지 않았다.
- 통합 리뷰에서 지도 카메라 이동 후 탭 복귀가 선택을 초기화하는 문제, 숨은 주변 탭의 동일 좌표 재검색이 큐레이션 캐시를 무효화하지 않는 문제를 찾았다.
- `HomeCurationQuerySnapshot(location, revision)`을 첫 위치/명시적 검색에서만 갱신하고, 재사용 시 revision·좌표·탭을 모두 검사하도록 수정했다. 진행 중 이전 요청에도 같은 검사를 적용한다. 최종 정책 11개와 상태 20개 GREEN 후 읽기 전용 재리뷰에서 중요 미해결 결함이 없음을 확인했다.

## 남은 범위·정리

- 큐레이션 광고 SDK 공급과 가로 슬롯 형식/폭·실제 로드 실패·노출/클릭·중복 방지·실기기 광고 확인은 후속이다. 실제 홈은 `adMobContent`를 주입하지 않아 광고와 그 자리를 건너뛴다. 광고 노출 로그를 임의로 보내지 않는다.
- 실제 카드 상세 QA는 USER_STORE를 확인했다. 다른 링크/가게 종류는 기존 처리 경로를 재사용하며 이번 실행에서 모든 외부 링크를 방문하지 않았다.
- 주소 선택의 좌표 적용 순서와 선택 기기 헤더 유무는 코드/API 유닛으로 확인했다. 사용자 GPS/계정 토큰을 검증 자료로 조회하지 않았다.
- 별도 단위 테스트 라이브러리·새 의존성을 추가하지 않고 상태/조회 정책을 순수 로직으로 검증했다. 실제 HomeViewModel 연결은 실홈 흐름과 독립 리뷰로 확인했다.
- 검증 중 켠 영업 중 필터를 해제하고 에뮬레이터 Wi-Fi/mobile data를 원래 활성 상태로 복구했다. 360dp 검증용 density override는 제거해 physical420을 확인했다. 검증을 위해 실행한 에뮬레이터도 종료했다.
- 사용자 `artifacts/`와 기존 변경은 보존했다. 커밋·푸시·배포는 수행하지 않았다.

## 후속 지도 버튼 겹침 수정 — 2026-10-08

초기 full 화면의 지도 버튼이 주소/필터와 겹치는 결함을 사용자 피드백으로 확인했다. 지도 공간에 따른 숨김/복원과 미리보기 상태를 통합한 표시 조건으로 수정했다. 새 유닛6개를 포함해 전체259개·Debug 빌드, 실홈9개 표시 상태를 검증했다. 최신 증거와 결과는 [11 지도 버튼 수정](11-map-controls-fix.md)에 있다. 앞의 253개 결과와 화면은 수정 전 단계의 기록으로 보존한다.

## 후속 큐레이션 광고 SDK 연결 — 2026-10-08

사용자 진행 승인으로 기존 SDK를 새 큐레이션에 연결했다. 서버 높이50/100에 맞는320×50/320×100, 실제 노출/클릭 로그, 수명주기 해제, 실패/좁은 폭 제외를 구현했다. 전체271개 유닛·Debug 빌드와 테스트 광고 실홈 검증9항목을 통과했다. [12 광고 연결 기록](12-admob-integration.md)이 최신 광고 상태다. 앞의 ‘SDK 후속’ 기록은 이전 단계의 범위이며 실제 운영 광고·실기기 수동 확인은 아직 남는다.
