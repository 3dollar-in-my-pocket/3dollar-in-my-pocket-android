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
