# TH-919 OnBackPressedDispatcher 마이그레이션 (Android 16 predictive back)

## Brief

[TH-919](https://3dollarinmypocket.atlassian.net/browse/TH-919): targetSdk 36 앱은 Android 16 에서 predictive back(`enableOnBackInvokedCallback`)이 기본으로 켜진다. 이때 `Activity.onBackPressed()` override 와 `KEYCODE_BACK` 키 리스너가 호출되지 않는다.

## Investigation

- `MainActivity.onBackPressed()` override: MY 하위 Fragment(`OnBackPressedListener` 8곳) → 팝업 닫기 → 제보 탭 → 다른 탭이면 홈 탭 → 홈이면 기본 동작. predictive back 에서는 통째로 무시돼 MY 탭에서 뒤로가기 시 앱이 꺼진다.
- `VersionUpdateDialog`: `setOnKeyListener(KEYCODE_BACK)` 로 강제 업데이트 다이얼로그에서 뒤로가기 시 앱 종료 → predictive back 에서는 호출되지 않음.
- `EditAddressBottomSheetDialogFragment`: `setOnKeyListener(KEYCODE_BACK)` 로 `CancelLocationEdit` → predictive back 에서는 시트만 닫히고 취소 인텐트가 빠짐.
- 이미 dispatcher 를 쓰는 곳(HomeFragment, NewAddressFragment, AddStoreDetailFragment, EditStoreFragment, 각 Activity)은 그대로 둔다.

## Implementation

- `MainActivity`: override 를 `OnBackPressedCallback` 하나로 옮김. 규칙은 그대로이고, 기존 `super.onBackPressed()` 자리는 콜백을 잠시 끄고 `onBackPressedDispatcher.onBackPressed()` 로 다음 콜백(홈 미리보기 닫기·제보 나가기 다이얼로그)이나 기본 동작에 넘긴다.
- `VersionUpdateDialog`: `ComponentDialog.onBackPressedDispatcher` 콜백으로 앱 종료.
- `EditAddressBottomSheetDialogFragment`: 키 리스너 대신 `onCancel` 에서 `CancelLocationEdit` (뒤로가기는 두 모드 모두 cancel 로 온다. 시트는 전체 높이·드래그 불가라 바깥 터치·스와이프 닫기는 없음).

## Verification

emulator-5554(API 35). 검증용 빌드 = 이 브랜치 + `<application android:enableOnBackInvokedCallback="true">`(Android 16 기본값과 같음, 커밋하지 않음).

| 시나리오 | release(검증용) | 이 브랜치(검증용) | 이 브랜치(일반) |
|---|---|---|---|
| MY 탭 → 뒤로 | 앱 종료(런처) | 홈 탭 | 홈 탭 |
| MY > 설정 → 뒤로 | - | MY | MY |
| 커뮤니티 → 뒤로 | - | 홈 탭 | - |
| 홈 가게 미리보기 → 뒤로 | - | 미리보기 닫힘 | - |
| 제보 탭 → 뒤로 | - | 나가기 다이얼로그 | - |
| 위치 수정 시트 → 뒤로 | 시트 닫힘 | 시트 닫힘 | - |
| 홈 → 뒤로 | - | 앱 백그라운드 | 앱 백그라운드 |

API 36(emulator-5556)은 미로그인이라 로그인 화면에서 더 들어가지 못했다. VersionUpdateDialog 는 서버 강제 업데이트 응답이 있어야 떠서 에뮬레이터로 확인하지 못했다.
