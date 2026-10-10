# TH-1401 연동 전 분석

작성: 2026-10-07. 사용자가 구현을 보류하고 분석 보고를 요청했다. 아래 내용은 제안이며 구현 승인이나 완료 상태가 아니다.

## 결론

서버는 최신 계약과 실제 가게 조회를 공급하는 단계다. Android의 기존 Debug UI는 초기 PR 계약을 사용하므로 그대로 API를 연결할 수 없다. 공통 SDUI primitive와 ImagePreviewCardModel은 재사용하고, 큐레이션 구조·초기 cards·100dp 크기·광고 혼합 처리를 좁게 맞추는 방식이 적절하다. 기존 홈/상세 파서를 통합할 필요는 없다.

API·DTO·매퍼·Repository는 화면 정책과 독립적으로 준비할 수 있다. 실제 홈에서 CURATION을 기본 탭으로 연결하려면 지도 마커, 재조회, 상태 복원, 빈/오류/로딩, 광고 정책이 먼저 정해져야 한다.

## 서버와 기존 UI 입력의 차이

기준은 서버 main `31214b0988d627049091a8be301fa1e71e701c61`의 DTO/controller/factory이다. 최신 API 가이드와 테크스펙 일부 필드 설명은 이전 계약을 따른다.

| 항목 | 현재 Debug UI 입력 | 최신 서버 | 연동 영향 |
|---|---|---|---|
| 카테고리 | categories | categoryFilters | 그대로 파싱하면 칩 입력을 얻지 못함 |
| 초기 카드 | cardsByCarousel 별도 입력, Carousel에는 없음 | Carousel.cards 포함 | 응답의 초기 cards를 보존하고 바로 표시해야 함 |
| 카드 | PREVIEW_CARD | IMAGE_PREVIEW_CARD | 기존 홈 PreviewCard보다 공통 ImagePreviewCardModel 재사용이 적절 |
| 메타 | primary/secondary chip group | metricLabel/contextLabel chip 배열 | 별도 chip group 중복 제거 가능 |
| 캐러셀 노출 로그 | impressionLog 선언 | 필드 없음 | 임의로 만들거나 전송하지 않음 |
| 기본 카테고리 | Figma 예시 첫 칩 | defaultCategoryId | 서버가 지정한 ID와 categoryFilters 순서를 사용 |

초기 흐름은 홈 탭·위치 준비 → 큐레이션 section 한 번 → 포함된 cards 표시다. 캐러셀마다 초기 cards API를 다시 부를 필요가 없다. cards API는 칩 변경 때 해당 캐러셀에만 사용한다. 두 API의 지도 좌표는 query, 기기 위치는 선택 header이며 기기 위치가 없으면 서버가 거리 칩을 생략한다.

## 직접 재사용과 좁은 확장

| 기존 공통 요소 | 판단 |
|---|---|
| SDText/SDImage/SDChip/SDHeader/SDSurfaceStyle/SDLink/SDLogModel | 서버 primitive와 일치. 새 원시 모델·렌더러는 불필요 |
| ImagePreviewCardModel | 최신 type·metricLabel/contextLabel·link/clickLog와 일치 |
| SDImagePreviewCardLayout | 이미 imageSize slot 존재. typed 카드에 modifier/크기 전달을 추가해 상세128dp·홈100dp를 함께 처리 가능 |
| 제목 말줄임 | typed 카드에 maxLines=1이 이미 있음. 필수 변경은 카드 외부100dp 폭 제한 |
| SDHeader | 서버 HTML20/700과 null 옵션을 표현 가능. Figma 줄높이28 보존은 렌더 확인/작은 옵션 확장 후보 |
| SDRelatedStoresSection | 카테고리·혼합 광고가 없어 홈 캐러셀 전체를 대체하지는 못함. 요소를 조합하는 홈 섹션은 필요 |
| ADMOB_CARD | 공통 카드 enum/model/parser에는 없음. cardId/height/clickLog/impressionLog를 가진 공통 표현이 필요 |

서버 카드 factory는 현재 white-only surface다. 공통 ImagePreviewCardModel.Style의 border 미지원은 원형 계약 보존 검토 사항이며 현재 표시 결함으로 관측된 것은 아니다. 별도 스타일 시스템을 만들 이유는 없다.

서버 SDText의 크기·굵기는 HTML 안에 있다. 최신 칩은 HTML12px/500이고 기존 Figma fixture는14/regular다. SDUI 원칙을 따르면 서버 스타일을 적용한다. Figma 예시와 완전히 같은 스타일이 요구되면 서버 공급값/기획을 맞춰야 한다. 클라이언트가 서버 HTML을 임의로 덮어쓰는 것은 기본안이 아니다.

최신 카테고리도 Figma 예시와 다르다. 인기 섹션은 붕어빵/왁뿌 소금빵/후르츠산도/피자설기, 입맛 섹션은 할미입맛/달달한/상큼한/빵순이/든든·짭짤이다. 앱에 이 목록과 순서를 고정하지 않는다.

## 파싱 경계

- NetworkModule에서 ServerApi는 기본 Gson, StoreApi만 SDUIGson을 사용한다.
- 따라서 새 ServerApi 응답 DTO에 List<SDCardModel> 인터페이스를 넣는 것만으로는 파싱되지 않는다.
- concrete DTO → mapper → 공통 sdui 모델 경로가 기존 구조를 가장 적게 바꾼다. ServerApi Gson을 전체 교체하지 않는다.
- 기존 HomeFilterClickLogResponse에는 eventType이 없다. 최신 로그의 eventType/screenName/objectType/objectId/extraParameters를 보존해야 하므로 큐레이션 로그 DTO로 그대로 쓰면 안 된다.
- eventType을 가진 기존 SDClickLogResponse/SDImpressionLogResponse는 재사용 후 공통 SDLogModel로 변환할 수 있다. 새 로그 원시 DTO를 무조건 추가할 필요는 없다.
- 서버 로그 extras는 Map<String,String>, 공통 SDLogSender는 현재 로그 전달에 사용할 수 있다.
- refs는 deprecated이며 큐레이션 factory에서 채우지 않는다. link/clickLog를 사용하고 refs로 storeId·로그를 복원하지 않는다.
- 현재 서버 링크는 APP_SCHEME이며 기존 SDLinkType으로 지원된다. 새로운 링크 enum 추가는 필요 없다.

## 실제 홈에서 결합된 부분

1. 홈 설정 요청과 위치 완료 후 목록 요청은 독립 실행된다. 기본 탭 결정과 위치 준비의 순서를 정리해야 한다.
2. 하나의 homeListSection이 주변 목록과 지도 마커를 함께 공급한다. 큐레이션 카드를 이 상태에 합치면 marker 필수 계약·미리보기 흐름이 충돌한다.
3. 기존 카드 탭은 지도 이동 → 미리보기다. 큐레이션은 marker 없이 상세 link를 제공하므로 별도 callback을 사용해야 한다.
4. 시트 nested scroll은 기존 listState가 최상단인지 검사한다. 활성 탭의 세로 스크롤 상태와 이 판정을 함께 연결해야 한다.
5. 지도 제스처는 재검색 버튼만 노출하고, 필터는 즉시 목록을 조회한다. 신규 큐레이션 API는 기존 정렬/음식/인증/영업/즐겨찾기 filter query를 받지 않는다.
6. 주소 선택 코드에는 새 좌표를 전달하기 전에 fetchAroundStores()를 부르는 경로가 있다. 이전 좌표 요청 가능성이 있으므로 신규 조회에서는 좌표 스냅샷·응답 세대 관리가 중요하다. 현재 코드 흐름에 대한 분석이며 이번에 이 동작을 변경하지 않았다.

접힌 시트의 내용 노출도 확인해야 한다. 기존 HomeSheetLayout의 peek은293dp이고, 이전393×688 Debug 증거에서 첫 사진은y188~288, 첫 제목은y296~316이다. 같은 배치와 peek높이를 유지하면 접힌 상태에서 가게명·메타는 화면 아래에 놓인다. 이는 코드/기존증거에 근거한 배치 추론이며 실홈에서 결함으로 재현한 결과가 아니다. 스펙이 기존 시트 단계를 유지하도록 하므로 높이를 임의로 바꾸지 않고 접힌 디자인을 먼저 확인해야 한다.

## 광고는 별도 결정이 필요

기존 홈은 AdView + AdSize.BANNER, 높이50dp다. Google 공식 문서의 BANNER는320×50dp이며 배너보다 작은 container에서는 광고가 표시되지 않는다: https://developers.google.com/admob/android/banner/fixed-size .

따라서 현재 Debug UI의 가로100dp 광고 slot에 기존 BANNER를 그대로 넣을 수 없다. 서버의 card height=100만으로 광고 형식·폭·ad unit은 결정되지 않는다. 세로 전체 폭 광고와 가로 카드 광고를 구분해 형식·폭·로드 실패 시 공간을 정해야 한다.

현재 주변 목록 impression은 응답받은 모든 카드에 즉시 전송하고 광고 renderer에는 원본 광고 모델/clickLog를 전달하지 않는다. 이를 재사용하면 화면에 보이지 않은 큐레이션 광고도 노출로 기록될 수 있다. 신규 광고 노출/클릭과 중복 방지는 실제 표시·SDK 이벤트 기준으로 결정해야 한다. 실API의 runtime 광고 위치 설정은 확인하지 않았다.

## 실홈 연결 전 정책과 권장 출발점

| 정책 | 권장 출발점 | 필요한 확인 |
|---|---|---|
| CURATION 지도 마커 | 기존 주변 마커 유지 | 주변 목록 병행 조회 필요. 큐레이션 마커는 서버 위치 계약이 없음 |
| 지도/주소 재조회 | 명시적 재검색·주소 확정 시 활성 큐레이션 갱신 | 숨은 탭 stale 처리와 신규 좌표 적용 시점 |
| 전역 필터 | 주변 목록/마커에 기존대로 적용 | 큐레이션에도 동일 필터 적용하려면 서버 계약 확대 필요 |
| 탭 복원 | 같은 지도 조건에서는 선택·스크롤 유지 | 위치 변경 때 선택 유지/초기화 정책 |
| 빈 결과 | header/chips를 유지하고 cards=[]를 정상 빈 결과로 취급 | 표시 문구·상태 디자인. 서버는 빈 캐러셀을 유지함 |
| 칩 실패 | 이전 카드 유지, 선택 확정과 응답을 일치시킴 | 실패 선택 복구 또는 오류/재시도 표시 |
| 광고 | slot/model 지원과 실제 광고 공급을 구분 | 가로 광고 형식·폭·실패 공간·노출 이벤트 |

이 표는 승인된 정책이 아니다. 최신 서버 데이터는 인기순 최대10개이며 최소4개나 거리순 노출은 보장하지 않는다. 앱에서 빈 칸을 채우거나 거리순으로 재정렬하는 것은 분석에서 권장하지 않는다.

## 안전하게 나눌 수 있는 작업

1. 공통 primitive/card 재사용과 최신 구조 반영: UI 배치만 옮기지 않고 categories/cards/metadata/type를 함께 맞춘다.
2. API·DTO·매퍼·Repository: 탭/default·초기 cards·unknown·link/log/height·좌표 전달을 검증한다. 실홈 정책과 독립적이다.
3. 정책 확정 뒤 HomeViewModel·실홈 연결: 캐러셀별 요청 취소/늦은 응답 방지, 링크·로그·광고와 시트 상태를 연결한다.

이번에는 제품 코드 수정·빌드·테스트·기기 조작·실API 호출을 하지 않았다. 실제 JSON 응답 캡처, 광고 runtime 설정과 신규 공통 UI 렌더 확인은 아직 남아 있다.
