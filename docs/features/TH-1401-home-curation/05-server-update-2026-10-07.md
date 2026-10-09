# 2026-10-07 서버 업데이트 확인

## 원천과 확인 범위

- 현재 저장소: `pocket-eight/pocket-backend` (이전 경로에서 redirect).
- PR https://github.com/pocket-eight/pocket-backend/pull/2280 는 2026-10-02 19:53:27 KST에 develop으로 병합됐다. merge `13dc30caf8392d9cd254922e8324571e3ac3c2f7`.
- 조사 main SHA: `31214b0988d627049091a8be301fa1e71e701c61` (`v4.85.0`). develop SHA: `796b09225af1456702a362b13659deb5989cab8f`.
- 운영 배포 https://github.com/pocket-eight/pocket-backend/actions/runs/37496202765 및 개발 배포 https://github.com/pocket-eight/pocket-backend/actions/runs/37523033808 성공과 유저 서비스 ECS job 성공을 확인했다. 두 배포 SHA는 큐레이션 merge를 포함한다.
- 실제 DTO/controller/composer/layout을 읽었다. 실서비스 API 응답·runtime layout 설정은 호출하거나 확인하지 않았다.
- Jira TH-1401에 연결된 현재 테크스펙: https://app.notion.com/p/3f07ad52990e8114b723e1d2d7a64d80 . 브라우저의 읽기 전용 화면으로 요구사항과 TC-1~12를 확인했다. Notion connector는 해당 페이지 권한이 없어 브라우저로 대체했다.

## 이전 UI 입력과 달라진 계약

| 대상 | 초기 PR head 기반 UI | 최신 main |
|---|---|---|
| 카테고리 배열 | categories | categoryFilters |
| 초기 카드 | 캐러셀별 별도 API 조회 | CAROUSEL.cards에 포함 |
| 카드 type | PREVIEW_CARD | IMAGE_PREVIEW_CARD |
| 메타 | primary/secondary chip group | metricLabel/contextLabel SDChip 배열 |
| 캐러셀 로그 | impressionLog | 필드 없음 |

`defaultCategoryId`는 유지된다. 탭은 CURATION/STORE_LIST, selected/unselected title/style와 defaultSelected/clickLog를 유지한다. 큐레이션에는 cursor가 없다.

원천:
- [CAROUSEL DTO](https://github.com/pocket-eight/pocket-backend/blob/31214b0988d627049091a8be301fa1e71e701c61/three-app/api-user/src/main/kotlin/com/three/api/user/application/screen/home/v2/section/bottomsheet/curation/carousel/HomeCurationCarousel.kt#L9)
- [카드 DTO](https://github.com/pocket-eight/pocket-backend/blob/31214b0988d627049091a8be301fa1e71e701c61/three-app/api-user/src/main/kotlin/com/three/api/user/application/screen/common/section/card/ImagePreviewCard.kt#L15)

## 요청·응답

- 섹션: `GET /v1/screen/home/section/curation/{curationTabId}`.
- 칩 변경: `GET /v1/screen/home/section/curation/{curationTabId}/carousel/{carouselId}/cards?categoryId=...`.
- 두 API의 mapLatitude/mapLongitude는 필수 query, 기기 위치 헤더는 선택이다. Android 외부 경로는 기존 `/api/v1` prefix에 맞추되 실제 응답 검증이 필요하다.
- 초기 캐러셀별 카드 API 호출은 필요 없다. 카테고리 변경 때 해당 캐러셀 카드만 재조회한다.
- 잘못된 tab은 400, 없는 carousel/category는 각각 404 NF037/NF038.
- 카드가 없으면 cards=[]이고 내부 광고를 넣지 않는다. 캐러셀 자체는 유지된다.

## 실데이터 정책

- 목데이터를 제거하고 지도 중심 기본 3km 내 카테고리 FoodType/label 조건으로 실제 가게를 조회한다.
- 거리순 100개 후보 안에서 인기순 최대10개를 선정한다. 최소4개나 거리순 최종 노출은 보장하지 않는다. 신고 가게 제외 후 결과 수가 줄어들 수 있다.
- 광고 위치·섹션 광고 높이·반경은 HOME_CURATION screen layout 설정이다. 기본 광고 위치는 비어 있다.
- 기기 위치가 없으면 거리 칩을 생략한다. 사진 조회의 부하 차단은 이미지 fallback을 사용한다.
- 조사한 홈 소스에는 큐레이션 버전 gate가 없고 탭은 항상 제공한다. 실제 노출은 실응답 확인 전까지 단정하지 않는다.

원천: [composer](https://github.com/pocket-eight/pocket-backend/blob/31214b0988d627049091a8be301fa1e71e701c61/three-app/api-user/src/main/kotlin/com/three/api/user/application/screen/home/v2/section/bottomsheet/curation/HomeCurationSectionComposer.kt#L55), [layout](https://github.com/pocket-eight/pocket-backend/blob/31214b0988d627049091a8be301fa1e71e701c61/three-core/domain/src/main/java/com/three/domain/screen/layout/domain/HomeCurationScreenSectionLayout.kt#L5).

## 스펙과 문서 차이

최신 API 가이드에도 categories/PREVIEW_CARD/primary/secondary/캐러셀 impressionLog/초기 별도 카드 조회 등 옛 설명이 남는다. 테크스펙의 primary/secondary 표현과 일부 열린 질문도 이전 계약을 따른다. 최신 JSON 계약은 실제 main DTO/controller를 기준으로 맞추고 사용자 요구와 TC 번호는 연결된 테크스펙을 따른다. 원격 문서는 수정하지 않았다.

지도 마커 유지, 큐레이션 재조회 시점, 선택·가로 스크롤 복원, 빈/오류/로딩 UI는 테크스펙에서 여전히 열린 질문이다. 서버 정렬·개수 정책은 테크스펙의 범위 밖이다.
