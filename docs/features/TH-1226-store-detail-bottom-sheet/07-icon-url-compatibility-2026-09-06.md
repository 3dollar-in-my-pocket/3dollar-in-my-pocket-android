# TH-1226 아이콘 URL 호환 보정 — 2026-09-06

## 승인 및 구현

서버 응답을 수정할 수 없으므로 앱에서 확인된 아이콘 경로를 보완하자는 사용자 요청에 따라 구현했다. 이 요청은 기존 strict server-driven 표시 정책에 대한 제한적인 예외다. 서버 이미지를 로컬 drawable로 교체하지 않고, 접근 가능한 동일 파일의 `/app/` 경로를 display model에 적용한다.

`StoreDetailScreenMapper.kt`의 `SDImageResponse.asModelOrNull()`에서 다음 조건을 모두 만족할 때만 보정한다.

- prefix가 정확히 `https://storage.threedollars.co.kr/`이다.
- 루트 파일명이 `copy.png`, `zoom_3x.png`, `deletion.png`, `Edit_fill.png`, `heart_fill.png`, `heart_line.png` 중 하나다.
- 예: `https://storage.threedollars.co.kr/copy.png` → `https://storage.threedollars.co.kr/app/copy.png`.

query와 fragment는 원문 그대로 이어 붙인다. 이미 `/app/`이 있는 URL, 다른 host·port·scheme·path·파일명, 사용자 사진 URL, 버튼 link, image style와 clickLog는 변경하지 않는다. 원본 response JSON도 변경하지 않는다. 적용 범위는 V2 상세 mapper이며 Home Preview 등 다른 mapper를 일괄 변경하지 않는다.

## 검증

```bash
./gradlew :data:testDebugUnitTest --tests 'com.threedollar.data.screen.StoreDetailScreenMapperIconUrlTest'
./gradlew :data:testDebugUnitTest --tests 'com.threedollar.data.screen.*' :app:assembleDebug
git diff --check
```

- 보정 전 새 테스트 3개 중 2개가 잘못된 URL 유지 때문에 assertion 실패했다. 다른 URL 보존 테스트는 기존에도 통과했다.
- 보정 후 전체 screen mapper **24 tests**, failures/errors/skipped **0**.
- `:app:assembleDebug` 성공. 기존 앱 데이터를 보존하며 `emulator-5554`에 APK 업데이트.
- mapper 수정 범위를 별도 읽기 전용 리뷰했으며 실질적 문제는 발견되지 않았다.
- 실서버 store `120024`의 MAP 복사/확대, EDIT 정보 수정/없는 장소 제보 아이콘이 표시됐다.
- 리뷰 화면의 unselected 하트도 표시됐다. 이번 새 응답은 두 리뷰 모두 `좋아요 0`이었다. 이 검증에서는 좋아요를 클릭하거나 서버 상태를 변경하지 않았다.
- filled/unfilled 두 하트 URL 변환은 mapper 테스트와 원본 PNG HTTP 200 확인으로 검증했다. 최종 화면에서 직접 본 것은 unselected 하트다.

증거:

- [지도·수정·제보 아이콘 복원](../../../build/harness/th-1226-click-20260906/74-icon-patch-map.png)
- [리뷰 하트 복원](../../../build/harness/th-1226-click-20260906/75-icon-patch-reviews.png)
- [테스트 집계](../../../build/harness/th-1226-click-20260906/icon-path-test-summary.json)
- [원본/정상 경로 HTTP 대조](../../../build/harness/th-1226-click-20260906/icon-url-diagnosis.json)

## 남은 범위

서버의 원래 루트 URL은 여전히 403이다. 이번 보정은 확인된 6개 파일만 다루며, 새로운 잘못된 아이콘 경로나 정상 경로 자체의 접근 장애를 일반적으로 복구하지 않는다. 기존 문구·레이아웃·이미지 색상 정책은 유지한다.
