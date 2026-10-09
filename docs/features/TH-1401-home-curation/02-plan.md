# UI 구현 계획

1. 홈 표시 모델을 기존 공통 SDUI primitive로 구성하고 unknown type/raw viewType을 보존한다.
2. 공통 이미지 카드의 slot layout을 추출한다. 홈용 텍스트·이미지·chip group·탭·카테고리·캐러셀을 기존 요소와 조합한다.
3. controlled view가 선택 탭/카테고리/캐러셀별 카드를 입력받고 원본 모델을 callback으로 전달한다. 실제 조회나 로그 전송은 하지 않는다.
4. 주변 목록·광고·빈 상태는 slot으로 받는다. 알 수 없는 타입은 렌더링에서 제외한다.
5. element/component/section/screen Preview와 debug Activity에서 기본·긴 텍스트·빈/미지원 타입을 확인한다.
6. JDK17로 core UI compile, debug assemble, 기존 home/screen mapper unit tests와 모듈 의존 검사를 실행한다. 승인된 TC는 에뮬레이터 캡처·터치·스크롤로 검증한다.

의존성·버전·실제 홈 동작·원격 상태는 변경하지 않는다. 새 서버 DTO 파싱과 네트워크 상태/로그 정책은 후속 작업이다.
