# TH-1070 · TH-1071 딥링크 진입 시 로그인 확인과 로그인 후 원래 링크로 복귀 (TH-1047 하위)

## Brief

- [TH-1071](https://3dollarinmypocket.atlassian.net/browse/TH-1071): 미로그인 상태에서 동적링크/스킴 링크로 들어오면 로그인 없이 대상 화면(가게 상세 등)이 열린다.
- [TH-1070](https://3dollarinmypocket.atlassian.net/browse/TH-1070): 푸시·링크로 들어왔다가 로그인하면 원래 링크가 아니라 홈으로 간다.

## Investigation

- `DynamicLinkActivity.onCreate`: 미로그인이면 로티만 재생하고 그대로 `handleDeepLink()` → 대상 화면을 연다. 로그인 확인 없음.
- `SplashActivity`(백그라운드 푸시 `link` extra): 토큰 확인 실패 시 `LoginActivity` 를 extra 없이 연다 → 링크 유실.
- `GlobalApplication.moveToLoginBySessionExpired()`(401): `LoginActivity.getSessionExpiredIntent` 에 링크를 넘길 통로가 없다.
- `LoginActivity` 로그인/가입 성공: 항상 `MainActivity.getIntent()` 만 연다.

## Implementation

- `DynamicLinkActivity`: 링크를 먼저 해석하고, 미로그인이면 `LoginActivity.getIntent(pendingLink)` 로 보낸 뒤 종료. 즐겨찾기 공유(`bookmark`)는 기존처럼 미로그인 열람 허용. 로그인 상태에서 앱 밖 링크를 열 때 `PendingDeepLink.record()`.
- `PendingDeepLink`(신규): 앱 밖 링크를 연 시각을 기억해 60초 안에 401 이 나면 세션 만료 로그인 화면에 링크를 넘긴다.
- `SplashActivity`: 토큰 확인 실패 시 `link` extra 를 로그인 화면에 넘긴다.
- `LoginActivity`: `getIntent(pendingLink)`, `getSessionExpiredIntent(pendingLink)`. 로그인·가입 성공 시 `MainActivity.getIntent(resumeLink)`.
- `MainActivity`: `resumeLink` 가 있으면 메인을 띄운 뒤 `DynamicLinkActivity.getExternalIntent(link)` 로 한 번 연다(재생성 시 반복 안 함).

## Verification

emulator-5554(API 35), dev 서버, 기기에 이미 있는 구글 계정으로 로그인(비밀번호 입력 없음).

| 경로 | release 4.35.0 | 이 브랜치 |
|---|---|---|
| 로그아웃 → `dollars-dev://store?storeId=527060` | 로그인 없이 가게 상세 | 로그인 화면 → 로그인 → 홈 지도 + MUJABEE 시트(TH-1537 랜딩) |
| 로그아웃 → 포그라운드 푸시 경로(`DynamicLinkActivity` + `link=dollars-dev://myCoupons`) | 쿠폰 → 401 → 로그인 → 홈 | 로그인 화면 → 로그인 → 내 쿠폰함, 뒤로 → 메인 |
| 로그아웃 → 백그라운드 푸시 경로(`SplashActivity` + `link`) | 로그인 → 홈 | 로그인 → 내 쿠폰함 |

유닛: `PendingDeepLinkTest` 4건(`TH1070_TC1~4`).

## 남은 질문

- 세션 만료 이어가기 유효시간 60초는 임의 값이다.
- 미로그인 `bookmark` 열람 허용은 기존 FavoriteViewerActivity 동작을 유지한 것이다.
- Splash 에서 토큰 확인 실패 시 LoginActivity 가 두 개(세션 만료 + Splash) 뜨는 기존 문제는 그대로다(release 에서도 같음).
- 카카오 공유 링크(`KakaoSchemeActivity` → Splash `STORE_TYPE`)의 미로그인 진입은 이번 범위 밖이다.
