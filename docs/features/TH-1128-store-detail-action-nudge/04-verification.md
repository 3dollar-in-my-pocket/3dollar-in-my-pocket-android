# TH-1128 Store Detail Action Nudge Verification

## 04 Verification

- 최종 확인일: 2026-07-14
- 기기: `Pixel_7_API_35(AVD)` / `emulator-5554`
- 앱: `com.zion830.threedollars.dev`

## 자동 검증

다음 명령을 실행해 성공했다.

```bash
./gradlew :domain:testDebugUnitTest :app:testDebugUnitTest :app:assembleDebug --console=plain
```

추가 확인:

```bash
git diff --check
```

## 실서버 수동 QA

- store `120023`:
  - 사라진 가게 item이 `isVisible=true`로 내려오는 것을 확인했다.
  - 서버의 3초 지연과 5초 유지 설정을 확인했다.
  - impression API `200`을 확인했다.
- 방문 유도:
  - 에뮬레이터 위치를 가게 반경 안으로 이동했다.
  - 첫 번째 상세 진입에서는 `sessionViewCountRange.min=2`로 노출되지 않는 것을 확인했다.
  - 두 번째 상세 진입에서 방문 유도 UI가 표시되는 것을 확인했다.
  - store `120024`에서 `열었어요`를 선택해 방문 인증 API `200`과 방문 성공 인원 갱신을 확인했다.
- 사라진 가게 UI:
  - `STORE` 신고 사유 4개가 동적으로 표시되는 것을 확인했다.
  - 여러 item이 응답 순서대로 표시되고 각 duration 이후 다음 item으로 전환되는 것을 확인했다.
- 크래시 로그는 발생하지 않았다.

## 발견 및 수정

- 전체 display item 객체를 `LaunchedEffect` key로 사용해 제출 상태 변경 시 impression이 중복 전송되는 문제를 발견했다.
- effect key를 `storeId + itemType`으로 변경하고 최신 item/callback은 `rememberUpdatedState`로 참조하도록 수정했다.
- 방문 제출 상태와 신고 사유 transient 상태가 effect key를 바꾸지 않는 회귀 테스트를 추가했다.

## 서버 복구 후 재검증

- 직접 API 확인:
  - `GET /api/v4/store/120023`: `200`
  - `GET /api/v1/store/120023/display-items`: `200`
  - `GET /api/v1/report/group/STORE/reasons`: `200`, 사유 4개
- 첫 번째 상세 진입:
  - 사라진 가게 item 노출 및 impression POST 1회 `200`
  - 방문 유도 impression 미전송
- 두 번째 상세 진입:
  - 서버가 사라진 가게 item을 `isVisible=false`로 전환한 것을 확인
  - 방문 유도 item 노출 및 impression POST 1회 `200`
- 방문 액션:
  - store `120024`에 `EXISTS` 제출 결과 `200`, `ok=true`
  - 제출 후 store `120024` 상세 조회가 `403 forbidden`을 반환해 인원 증가 비교는 완료하지 못함
- 관련 테스트 3종을 `--rerun-tasks`로 재실행했고 모두 통과했다.
- 재검증 로그에 `502`, `503`, `FATAL EXCEPTION`은 없었다.

증적은 `build/harness/th-1128-server-retest/`에 저장했다.

## 2026-07-14 최종 QA

- 최신 `feature/TH-1128` Debug APK를 `Pixel_7_API_35`에 다시 설치했다.
- store `120023` 첫 진입:
  - 사라진 가게 item `isVisible=true`, 사유 4개, impression API `200`을 다시 확인했다.
  - impression 이후 서버가 같은 계정에 두 item을 `isVisible=false`로 전환하는 것을 확인했다.
- store `120024` 방문 유도:
  - 첫 번째 진입에서는 노출되지 않고 두 번째 진입에서 방문 유도 UI가 표시됐다.
  - 방문 유도 impression API `200`을 확인했다.
- 기존 `403` 재확인:
  - store `120024` 상세 API가 다시 `200`을 반환했다.
  - `2026-07-12`에 제출한 현재 계정의 `EXISTS` 방문 이력과 `existsCounts=2`를 확인했다.
  - 따라서 당시 방문 제출은 저장됐고, 제출 직후의 `403`은 후속 상세 조회의 일시적 실패로 판정했다.
- 사라진 가게 제출:
  - 오늘 impression 이후 신규 모달이 재노출되지 않아 동일한 `NOSTORE` API를 사용하는 기존 상단 신고 화면에서 제출했다.
  - 기존 화면은 Activity를 즉시 종료해 클라이언트 요청이 `Canceled`로 기록됐지만 서버에서는 요청을 처리했다.
  - 이후 store `120023` 상세 조회가 `404`, `not_exists_store`를 반환해 삭제 처리를 확인했다.
  - dev 어드민에는 삭제 가게 복구 기능이 없어 서버 측 데이터 복구가 필요하다.

증적은 `build/harness/th-1128-final-qa/`에 저장했다.

## 남은 제한

- 신규 사라진 가게 모달의 선택 UI와 impression은 확인했지만, 일일 impression 이후 서버가 비노출로 전환해 해당 모달에서 직접 제출하는 전체 흐름은 재실행하지 못했다.
- 성공 감사 토스트는 API 성공과 데이터 반영까지 확인했지만 시각 증적을 남기지 못했다.
- 기존 상단 신고 화면의 요청 취소 위험은 TH-1128 신규 모달에는 해당하지 않으며 별도 이슈로 분리해야 한다.

## 2026-08-08 모달 이동 중 닫힘 리뷰 조사

- 기준 코드: `feature/TH-1128`의 `cfd5cf48`
- 기기: `Pixel_7_API_35(AVD)` / Android 15
- 수정 전 코드의 동작을 서버 노출 조건에서 분리한 debug 전용 재현 화면에서 `onDismiss` 호출 여부로 확인했다.
- 모달이 표시된 상태에서 배경을 800px/800ms 스와이프했을 때 닫히지 않았다.
- 실제 상세 화면처럼 스크롤 영역 위의 `ComposeView`를 `GONE`으로 두고, 2.2초 스크롤 도중 모달을 표시해도 닫히지 않았다.
- 배경 탭은 닫혔고, 10px/800ms 이동은 Android 터치 슬롭 이내의 탭으로 판정되어 닫혔다.
- 따라서 큰 스크롤은 직접 `onDismiss`를 호출하지 않았지만, 스크롤 시작 시 손가락 이동량이 터치 슬롭 이내이면 배경 탭으로 판정되어 닫힐 수 있었다.
- 모달은 진입 애니메이션 완료 후 서버의 `displayDurationSeconds`만큼 유지된 뒤 자동 종료된다. 기존 dev 설정은 5초였으므로, 제보 영상의 실제 노출 시간이 5초인지 확인해야 이동과 무관한 정상 자동 종료인지 판정할 수 있다.
- `AnimatedVisibility`의 종료 조건인 `displayItemState.isVisible=false`는 `StoreDetailViewModel.dismissCurrentDisplayItem()`에서만 설정된다.
- 수정 전 해당 함수의 호출 경로는 다음 네 가지였다.
  - 모달 바깥 배경 탭. 터치 슬롭 이내의 미세 이동도 Android에서 탭으로 판정된다.
  - 진입 애니메이션 완료 후 `displayDurationSeconds` 타이머 만료.
  - 방문 인증 API 성공.
  - 사라진 가게 신고 API 성공.
- 모달 내부 컨테이너는 클릭을 소비하므로 버튼 외 내부 탭으로는 닫히지 않는다.
- 2026-07-14 실서버 로그에서 방문 유도 응답은 `displayAfterSeconds=3.0`, `displayDurationSeconds=5.0`이었다. 응답은 `21:14:54.805`, 진입 완료/impression은 `21:14:58.381`에 기록되어 코드의 3초 대기와 500ms 진입 애니메이션이 일치했다.

### 리뷰 반영

- 수정 브랜치: `fix/TH-1128-modal-dismiss-drag`
- Android에만 있던 전체 화면 배경 `clickable`과 `onDismiss` 콜백을 제거했다.
- 바깥 dismiss에만 사용되던 `StoreDetailViewModel.dismissCurrentDisplayItem()` 공개 래퍼도 제거했다.
- `ComposeView` 높이를 전체 화면 제약(`0dp`)에서 모달 콘텐츠 높이(`wrap_content`)로 변경해 모달 위쪽 상세 영역의 스크롤을 차단하지 않도록 했다.
- 모달 카드 내부의 no-op `clickable`은 유지해 비버튼 영역의 터치가 아래 상세 화면으로 전달되지 않도록 했다.
- 수정 후 dismiss 경로는 다음 세 가지로 제한된다.
  - 진입 애니메이션 완료 후 `displayDurationSeconds` 타이머 만료.
  - 방문 인증 API 성공.
  - 사라진 가게 신고 API 성공.
- iOS 구현도 모달 크기의 view만 상세 화면 하단에 배치하며 바깥 탭 dismiss를 제공하지 않는 것을 확인했다.
- 방문 유도, 신고 사유 로딩, 신고 사유 선택 상태 Compose Preview 3종은 그대로 유지했다.
- `Pixel_7_API_35` debug 검증 화면에서 overlay 높이는 `594px`로 측정되어 화면 높이 `2400px` 전체를 덮지 않았다.
- UI 트리 기준 모달 바깥 영역에서 배경 탭과 10px/800ms 이동 후에도 모달이 유지됐다.
- 같은 영역을 1000px/800ms 스크롤했을 때 상세 영역의 `scrollY`가 `0`에서 `1116`까지 이동했고 모달은 계속 표시됐다.
- 검증 중 Android crash buffer는 비어 있었다.
- 조사와 최종 검증에 사용한 debug 전용 화면은 검증 후 제거했다.
- 터치 표시를 활성화한 최종 화면 녹화에서도 `displayed` 이후 배경 탭, 10px 이동, 배경 스크롤을 순서대로 수행했다.
- 녹화 로그에서 스크롤은 `scrollY=11`에서 `1030`까지 진행됐으며 모달은 유지됐고, `displayed` 이후 5초에 기록된 `timerDismissed`에서만 내려갔다.
- 영상과 로그는 `build/harness/th-1128-modal-dismiss-review/`에 저장했다.

### 최종 정적 검토 및 빌드

- `git diff --check` 통과.
- `./gradlew :domain:testDebugUnitTest :app:testDebugUnitTest :app:assembleDebug --console=plain` 통과.
- 전체 `./gradlew test :app:assembleDebug --console=plain`은 변경과 무관한 기존 `:core:abtest:kaptDebugUnitTestKotlin`의 `ExampleUnitTest` 스텁 오류(`NonExistentClass cannot be converted to Annotation`)로 중단됐다.
- 최종 코드 검토에서 모달 카드 내부의 no-op `clickable`을 유지해 아래 상세 화면으로 터치가 통과하지 않는 것을 확인했다.

## Git 이력 확인

- PR #293 병합 커밋 `00e15704`와 기능 최종 커밋 `cfd5cf48`은 `v4.21.0` 태그와 현재 `origin/develop`의 조상이 아니다.
- 현재 원격에서 TH-1128 구현은 `origin/feature/TH-1128`에만 남아 있다.
