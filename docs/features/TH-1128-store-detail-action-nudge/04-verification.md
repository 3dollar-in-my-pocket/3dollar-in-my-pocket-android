# TH-1128 Store Detail Action Nudge Verification

## 04 Verification

- 최종 확인일: 2026-07-12
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

## 남은 수동 확인

- 수정 APK에서 impression이 정확히 한 번만 전송되는 실서버 재검증은 dev 서버가 `503 Service Unavailable`, 이후 `502 Bad Gateway`를 반환해 완료하지 못했다.
- 사라진 가게 신고 제출은 서버 테스트 데이터 변경을 피하기 위해 실행하지 않았다.
- 성공 감사 토스트는 API 성공과 상세 갱신까지 확인했지만 캡처 타이밍 문제로 시각 증적을 남기지 못했다.

위 항목은 외부 서버 상태 및 테스트 데이터 제약이며 자동 테스트와 debug build는 통과했다.
