# TH-1401 홈 큐레이션 — UI 단계

- Jira: https://3dollarinmypocket.atlassian.net/browse/TH-1401
- 목표: 기존 SDUI 규칙을 유지하며 Figma의 탭, 카테고리, 100dp 가게 카드, 큐레이션 뷰를 구현한다.
- Figma: `Gw367Wy4qqnEWcvSlNUqzB`, 컴포넌트 `11349:47766`, 화면 `11349:59119`, 큐레이션 시트 `11349:46867`.
- 범위: `:core:common` 표시 모델, `:core:ui` 컴포넌트·뷰·Preview, debug 디자인 확인 화면.
- 후속 범위: Retrofit DTO/매퍼/API 조회, HomeViewModel·실제 홈 연결, 서버 실데이터·광고 SDK 연결.
- API 원천: pocket-three/pocket-backend PR #2280, 고정 head `bf04fc9f6154bfcd201ed0521b8af36f1d9e1879`의 DTO와 `05_api_guide.md`.
- 승인: 사용자가 UI-first 구현과 TC-1~5, 표시 UI TDD 예외를 승인했다. 신규 Composable 테스트 코드를 만들지 않고 기존 유닛 테스트·빌드·에뮬레이터 증거로 검증한다.

## 승인된 검증 항목

1. 탭·카테고리 선택 표시와 콜백.
2. 100dp 사진·카드, 긴 가게명, 거리·리뷰 수·평점.
3. 세로 스크롤과 수평 스와이프.
4. 빈 데이터와 미지원 타입.
5. 기존 상세 카드와 주변 목록 유지.

이 번호는 이번 로컬 UI 단계의 검증 목록이다. Jira 테크스펙 필드는 비어 있어 플랫폼 공통 테크스펙 TC와 아직 연결되지 않았다.

## 2026-10-07 후속 확인

현재 Jira에는 공유 테크스펙 https://app.notion.com/p/3f07ad52990e8114b723e1d2d7a64d80 와 TC-1~12가 연결돼 있다. 위 UI 단계의 로컬 번호와 구분한다. 서버의 병합·배포 및 계약 변경은 `05-server-update-2026-10-07.md`, 다음 단계 준비안은 `06-integration-plan.md`에 기록했다. 새로운 API/상태 로직 테스트의 계층 배정은 아직 승인 전이다.

## 2026-10-08 2차 구현

분석 보고 후 사용자가 2차 구현을 승인했다. 공통 SDUI 정리, 최신 서버 데이터 경계와 실홈 상태·클릭 로그·상세 이동을 연결했고, 전체 유닛 253개/Debug 빌드와 에뮬레이터 검증을 통과했다. TC 번호는 공유 테크스펙 TC-1~12를 사용한다. 실광고 SDK는 후속 범위이며 상세는 [09 구현 기록](09-phase2-implementation.md)·[10 검증 기록](10-phase2-verification.md)에 있다.
