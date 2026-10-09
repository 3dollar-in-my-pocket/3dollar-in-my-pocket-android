# TH-1401 지도 버튼과 시트 겹침 수정

## 승인·계획 — 2026-10-08

사용자가 full 시트 위로 내 위치·가게 제보 버튼이 올라와 주소/필터와 겹치는 현상을 지적하고, 펼침 시 숨김·접힘 시 복원을 승인했다.

- 기존 `updateMapControlBottomMargin`은 시트 표시 높이 + 12dp만큼 지도 컨트롤을 올린다. 제보 버튼도 같은 하단에 맞춰져 있다. full 상단은 필터 하단이므로 지도 공간이 없는 상태에서도 컨트롤이 주소·필터 위로 올라간다. 이 위치 계산은 기존 HEAD에도 있었다.
- 남은 지도 높이, 실측 지도 컨트롤/제보 버튼의 큰 높이와 시트 간격으로 표시 가능 여부를 판단한다. 겹치기 전 숨기고, 충분히 접히면 다시 표시한다. 미리보기 중 숨김도 같은 조건에 포함한다.
- 숨김은 `INVISIBLE`로 처리해 실측 크기를 보존한다. 시트 이동과 버튼 높이 변경, 미리보기 열기/닫기에서 같은 정책을 갱신한다.
- 테크스펙 TC-12의 회귀: 순수 표시 조건을 유닛 RED → GREEN으로 검증하고, 실제 홈에서 접힘/full/중간 드래그/주변 탭/미리보기 닫기를 확인한다. 기존 표시 UI TDD 예외를 유지한다.
- 변경은 표시 정책·HomeFragment·해당 테스트와 이 기록으로 한정한다. 공통 SDUI 계약·API·의존성·서버/원격 작업은 변경하지 않는다.

## 결과

- `HomeMapControlsVisibilityPolicy`와 해당 유닛 6개, `HomeFragment` 연결을 구현했다. map/write 실제 크기를 보존하고 높이 변화/시트 이동/미리보기 상태를 같은 표시 조건으로 처리한다.
- RED: 6개 중 예상 AssertionError 5개를 확인했다. GREEN: 최종 전체 유닛 259개(failure/error/skipped 0), 새 정책 6개 모두 통과했다.
- JDK17 `./gradlew :app:assembleDebug testDebugUnitTest --console=plain` — `BUILD SUCCESSFUL`. 중간 컴파일에서 누락된 `android.view.View` import를 추가하고 최종 빌드/전체 유닛으로 다시 검증했다.
- 독립 읽기 전용 리뷰에서 full/중간 드래그/preview 종료/Fragment 뷰 재생성 관련 중요 미해결 회귀는 없었다.
- 실제 개발 서버 홈에서 9개 상태를 확인했다. 접힌 큐레이션과 지도 공간이 충분한 중간 드래그에서는 표시, full 이전 헤더 침범 위험이 생긴 중간 상태와 큐레이션/주변 목록 full에서는 숨김, `지도 보기`로 접으면 표시한다. 가게 미리보기 중에는 숨기고, 미리보기를 닫아 full 목록으로 돌아와도 숨김을 유지하며 그 뒤 접으면 복원한다.
- 9개 상태 XML에서 내 위치/즐겨찾기/화면 위 제보 버튼의 표시 여부를 단언했다. 이미지도 직접 검토해 검색창/필터와의 겹침이 사라진 것을 확인했다. 초기 cold launch 캡처는 splash였으므로 홈 진입 이후 다시 캡처했다.
- 영상 `drag-and-collapse.mp4`(H264 1080×2400, 36.4초)는 중간 드래그와 full 상태를 기록했다. 녹화 종료코드는255였지만 파일을 회수해 길이와 프레임 추출을 확인했다. 디코드 시 중복 timestamp 경고가 있어 화면별 판정은 PNG/XML 증거를 기준으로 했다. AndroidRuntime 앱 오류는 관측되지 않았다.
- 신규 의존성·서버 계약·원격 작업은 없으며 기존 사용자 변경과 artifacts/를 보존했다.

## 화면 증거

`build/harness/th1401-sheet-controls/` 아래에 PNG/XML, `qa-results.json`, 빌드/RED 로그와 참고 영상을 저장했다.

| 상태 | 지도 버튼 | 증거 |
|---|---|---|
| 접힌 큐레이션 | 표시 | `collapsed.png` |
| 지도 공간이 충분한 드래그 | 표시 | `drag-room-visible.png` |
| full 전 헤더 침범 위험이 있는 드래그 | 숨김 | `drag-room-hidden.png` |
| 큐레이션 full | 숨김 | `full.png` |
| 지도 보기로 접힘 | 복원 | `collapsed-restored.png` |
| 주변 목록 full | 숨김 | `nearby-full.png` |
| 가게 미리보기 | 숨김 | `preview.png` |
| 미리보기 종료 후 full 복귀 | 숨김 유지 | `preview-closed.png` |
| 미리보기 종료 후 목록 접힘 | 복원 | `after-preview-collapse.png` |

수정 전 `before-full.png`와 XML도 보존했다. 검증용 에뮬레이터도 종료했다.
