# UI 검증 결과

## 자동 검증

- JDK17: `./gradlew :app:assembleDebug testDebugUnitTest --console=plain` — PASS.
- 기존 유닛 210개, failure 0 / error 0 / skipped 0. app 124, data 43, domain 4, core/common 29, core/network 10.
- `scripts/check-module-deps.sh` — 의존 간선 18, 신규 위반 0.
- `git diff --check` — PASS.
- Preview fixture의 asset URL 확인 후 `:app:assembleDebug`를 다시 실행해 최종 소스 빌드 PASS.
- 신규 Composable 테스트 코드는 사용자 승인한 UI TDD 예외에 따라 추가하지 않았다.

## 에뮬레이터 증거

- Pixel_7_API_35, Android API35, arm64. Debug `HomeCurationDesignActivity`를 직접 실행했다.
- Figma 비교: 393×688 / density160. 추가 폭 320·640dp와 dark mode를 확인했다.
- UI 트리 기반 좌표로 탭·카테고리·카드를 누르고 캐러셀/세로 목록을 스와이프했다.
- 검증 결과는 `build/harness/th1401/qa-results.json`에 18개 PASS로 저장했다.
- `default.xml`에서 사진 `[20,188][120,288]`와 한 줄 제목 `[20,296][120,316]`의 100dp 폭을 확인했다.

| 승인 항목 | 결과 | 증거 |
|---|---|---|
| TC-1 탭·카테고리 선택/콜백 | PASS, 탭 왕복 후 카테고리 선택 유지 | `category_selection.png`, `nearby_tab.png`, `tab_return.png`, `callbacks.log` |
| TC-2 사진100dp·메타·긴 이름 | PASS, 제목 말줄임·원본 이미지·거리/리뷰/평점 표시 | `default.png`, `default.xml`, `long_text.png` |
| TC-3 세로·가로 스크롤 | PASS | `horizontal_swipe.png`, `vertical_swipe.png`, 해당 XML |
| TC-4 빈/미지원 입력 | PASS, unknown-only 탭은 nearby slot fallback | `empty_sections.png`, `empty_cards.png`, `unknown.png`, `empty_tabs.png`, `unknown_tabs.png`, `mixed.png` |
| TC-5 기존 상세/주변 유지 | 원본 코드 비교·유닛 PASS. 상세 카드 128dp/클릭/메타 유지, 실홈 소스 미변경 | 독립 코드 리뷰, 기존 유닛 결과. 실제 홈 API 연동 QA는 후속 |

시각 확인 추가 증거: `small_width.png`, `large_width.png`, `dark_mode.png`. 서버가 지정한 색을 그대로 표현하므로 dark mode에서도 fixture의 흰 배경을 유지한다.

검증 후 임시 해상도·밀도 override를 제거하고 야간 모드를 원래 `no`로 복원했다. 이번 검증을 위해 실행한 에뮬레이터도 종료했다.

## 제한과 실패 후 처리

- 초기 offline 빌드는 기존 viewbinding 캐시 누락으로 실패했다. 기존 버전 그대로 다운로드 후 빌드/테스트 PASS.
- Debug Toast Context 컴파일 오류를 수정한 뒤 새 빌드 PASS.
- 초기 UI 트리 캡처가 앱 시작 전에 실행돼 XML 없이 반환됐다. 앱 실행 완료 대기와 최대3회 조건부 캡처로 절차를 수정했고 전체 시나리오 PASS. 앱 크래시는 관측되지 않았다.
- 사진/placeholder는 Figma 원본 asset이다. OS별 emoji와 image fill crop은 Figma/iOS 예시와 일부 다르지만 100dp frame·간격·행·줄임을 확인했다.
- API·실데이터 거리순·가게 수 정책, 서버 로그 전송, 광고 SDK, 실제 상세/주변 화면 연결은 이번 UI 범위에 포함하지 않았으며 미검증이다.

## 2026-10-08 2차 후속

위 기록은 1차 UI 범위의 당시 결과다. 최신 공통 SDUI·서버 API·실홈 연결은 [09 구현 기록](09-phase2-implementation.md), 전체 253개 유닛·빌드와 실홈 QA 결과 및 광고 후속 상태는 [10 검증 기록](10-phase2-verification.md)에 별도로 기록했다.
