# SDUI 규칙과 디자인 조사

- `docs/context/module-dependencies-current.md`: 홈 `serverdriven`은 concrete DTO → data mapper, 상세 `sdui`는 Gson 다형성 파싱이다. 이번 변경에서 두 계약을 통합하지 않는다.
- 모델은 `:core:common`, 렌더러는 `:core:ui`. 원본 SDText/SDImage/SDChip/style/link/log를 보존하고 navigation·analytics·조회는 상위 화면에서 수행한다.
- category selected/unselected는 SDChip, tab selected/unselected는 title/style이다. CAROUSEL에는 별도 카드·style 필드가 없고 카드 응답은 캐러셀별로 조회된다.
- 광고는 `items`와 `cards`에서 같은 ADMOB_CARD 계약(cardId/height/clickLog/impressionLog)을 사용한다. 실제 SDK는 상위 slot으로 주입한다.
- 홈 모델을 입력받되, HTML span·dimmed 이미지 등 표시에는 기존 sdui 요소 렌더러를 명시적으로 변환해 재사용한다. 네트워크 파서 등록은 바꾸지 않는다.
- `SDImagePreviewCard`의 model-free layout을 추출해 기존 상세의 128dp 기본값을 보존하고 홈에서 100dp 폭/사진을 지정한다.
- Figma: 탭 48dp(내부40/패딩4), 섹션20dp, 카테고리 간격6dp/가로패딩12/세로8, 카드 간격8dp/좌우20dp, 사진 radius16, 제목14/20 SemiBold, 메타12/18.
- 문구·색·이미지·카테고리·섹션 순서는 표시 모델에서 받는다. Figma 예시는 Preview/debug fixture에만 둔다.
- 서버 선작업은 고정 PR head에서 목데이터를 공급한다. 거리순·4~10개 실가게 정책은 이번 표시 UI의 완료 기준이 아니다.
