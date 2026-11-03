# TH-413 DynamicLinkActivity 가 모든 https 링크를 가로채는 문제

## Brief

[TH-413](https://3dollarinmypocket.atlassian.net/browse/TH-413): 앱 설치 후 아무 웹 링크(https://www.google.com 등)를 열면 '연결 프로그램'에 가슴속3천원이 뜬다.

## Investigation

- `DynamicLinkActivity` 의 intent-filter 하나에 `https`/`http`/`dollars-dev`/`dollars` scheme 과 `link.threedollars.co.kr`/`*` host 가 함께 있다.
- 한 intent-filter 안의 `<data>` 는 scheme × host 로 합쳐지므로 `https` + `host="*"` 조합이 생겨 모든 https/http 링크와 매칭됐다.
- 실제로 앱이 쓰는 링크: 커스텀 스킴 `dollars://`(release)·`dollars-dev://`(dev) 전부, 웹은 `link.threedollars.co.kr`(netlify 랜딩)만. 카카오 링크·OAuth 는 `KakaoSchemeActivity`·`AuthCodeHandlerActivity` 의 별도 필터.

## Implementation

- `AndroidManifest.xml`: 필터를 둘로 나눔. ① `https`/`http` + `link.threedollars.co.kr` ② `dollars`/`dollars-dev` + 모든 host. 매칭 대상은 기존 의도와 같고 다른 웹 링크만 빠진다.

## Verification

emulator-5554(API 35) `cmd package query-activities`: 수정 전 `https://www.google.com`·노션 링크에 dev 앱 매칭 → 수정 후 매칭 없음. `link.threedollars.co.kr`·`dollars-dev://` 는 계속 매칭, `dollars-dev://store?storeId=527060` 정상 이동.

## 남은 질문

- `https://link.threedollars.co.kr/...` 로 들어오면 `handleDeepLink()` 가 scheme 에 `dollars` 가 없어 바로 `finish()` 한다(기존 동작, 이번 변경 없음). 웹 링크로 화면 이동이 필요한지, `app.threedollars.co.kr` 도 받아야 하는지는 제품 결정이 필요하다.
