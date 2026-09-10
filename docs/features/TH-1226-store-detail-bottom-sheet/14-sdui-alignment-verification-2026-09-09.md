# SDUI 정비 검증 기록

검증 기간: 2026-09-09~10 KST. **승인 범위의 구현·필수 검증 완료.** 기준 운영 배포는 v4.76.1 / `3d269129`. 원래 조사 10·11 문서는 보존한다.

## 자동 검사

| 범위 | 실제 관측 결과 |
| --- | --- |
| 계약 common/data | 최초 35 tests PASS, 독립 계약 리뷰 승인. 운영 fixture 14→14, 15→15, 15→15, 16→16 및 배포 16종 보존 |
| 초기 통합 | common 5 + data 30 + app home/v2/contributor 101 = 136 tests PASS, Debug APK PASS |
| 중간 통합 | common 7 + data 30 + app home/v2/contributor 101 = 138 tests PASS, Debug APK PASS. `/private/tmp/gasam-sdui-integrated-20260910.log` |
| 최종 통합 | **common 7 + data 31 + app 123 = 161 tests PASS**, skipped/failures/errors 모두 0. app에는 Home 81개가 포함된다. `:app:assembleDebug` PASS. `/private/tmp/gasam-sdui-final-integrated.log` |
| 답글 조건 fixture 보강 | 배포 생성 코드의 comment 작성자·날짜·본문·배경 fixture 추가 후 data screen **31 tests PASS**, 검사 APK PASS. `/private/tmp/gasam-sdui-host-probe-build.log` |
| 중첩 HTML 보완 | 새로운 2 tests assertion RED → common 7 tests PASS. nested 스타일 상속/복원, bold, 줄바꿈, entity 1회 처리 |
| 상세 액션 | fake repository에서 쿠폰 키, POST/리뷰 mutation 식별자, 갱신 및 stale 응답, link 우선, 지도 alias, 계좌 plain text 검증 |
| 좌표 | 완전한 pair 우선순위, 잘못된 좌표, 다른 가게 marker 배제, null fallback unit PASS |
| 광고 | 카드별 최신 log, 최초 실패, loaded 후 refresh 실패 유지, destroy 후 callback 차단 unit PASS |
| 공통 UI | 실제 Compose UI tree/PNG: 288×180→120×75, 빈 chip 0×0, END 순서, header 부제목·action, 광고 state 2개 PASS |
| 명시적 이미지 0 크기 | 0×-1이 16×16이 되는 RED → 렌더 생략 0×0 GREEN |
| 홈 경쟁 조건 | 실제 ViewModel fake repository 및 정책 테스트를 포함한 Home 81 tests fresh PASS. 필터 지연/실패/재시도, 오래된 응답, query snapshot, pending focus ACK/gesture, 선택 복원, HTML, 로그 순서/진입을 검증 |

최종 명령은 승인 계획의 common/data/app home+v2 tasks에 contributor package를 추가하고 `:app:assembleDebug --offline --console=plain`을 함께 실행했다. Gradle은 JDK17을 사용했다. debug 검증만 보강한 마지막 APK 빌드도 `/private/tmp/gasam-sdui-final-harness-build.log`에서 PASS이며 production source는 최종 통합 이후 변경하지 않았다.

## emulator-5554 기기 결과

`com.zion830.threedollars.dev` 4.22.1 (128)을 `adb install -r`로 설치했다. 앱 데이터는 보존했다. 동작 tap은 직전 UI tree bounds를 확인한 뒤 수행했다. 기기 사용 충돌 이후 별도의 `-read-only -no-snapshot -no-window` 인스턴스에서 독립 검증했다.

증거 디렉터리: `build/harness/sdui-alignment-20260909/`.

| 항목 | 결과와 증거 |
| --- | --- |
| USER/BOSS/공식인증 4종 × Home/full | 상단 `fixture-*-top.xml/png`에서 EDIT·CALLOUT, 하단 `lower-*`의 48개 스크롤 위치 및 24 PNG에서 정보/갤러리/메뉴/영업일/리뷰/CTA를 확인했다. 8개 조합 모두 앱 화면과 본문 유지. 캡처 checker PASS |
| 초기 위치 재확인 | 과거 BOSS 캡처 일부에서 title이 위로 밀린 상태를 관측했으나, 새 캡처31/32 및 최종 8개 lower step0 모두 제목이 상단에 있었다. USER/BOSS y=53, 공식인증 y=192. 현재 회귀에서 제목 소실 없음 |
| V2 방문 인증 | 이전 APK에서 Main으로 돌아가는 RED09 → 새 APK `StoreCertificationActivity` 진입 GREEN14. 인증 제출 안 함 |
| V2 공유 | 카카오 공유 대상 선택 화면 진입16, 취소 후 상세 복귀18. 대상 선택·공유 전송 안 함 |
| V2 길안내 | 카카오/네이버 지도 선택 창19 진입 후 닫음 |
| V2 지도 확대 | EDIT의 확대 버튼으로 지도 보기21 진입 후 상세 복귀 |
| V2 주소 복사 | 23 PNG에서 주소가 든 clipboard preview와 복사 완료 toast 확인 |
| V2 정보 수정 | 25 진입, 26 취소. 가게 이름·주소·정보 수정 bounds 유지. 89에서 실제 Edit 진입 후 시스템 back으로 상세 유지 확인 |
| Home 정보 수정 | 51 진입·52 취소·53 back의 가게/스크롤 유지. 89에서 같은 main-loop의 Edit 요청 2회에 Activity FM 화면/stack 각 1개, NavHost FM 0개. 취소 갱신 0회, fake 저장 성공 뒤 목록/상세 각 1회 및 선택 가게 유지 |
| Home 방문 인증·지도 확대·주소 복사 | 54 인증 화면, 56 지도 보기 진입. 59 PNG에서 clipboard 주소와 toast 확인. 인증 제출 안 함 |
| POST 탭 존재/부재 | 60~61에서 운영 BOSS 탭 + 생성 코드 POST fixture로 실제 POST 본문에 스크롤. 62에서 POST 없는 운영 BOSS 탭 enabled=false 확인. 외부 딥링크 실행 안 함 |
| Home POST 탭 | 88에서 POST 존재 시 본문으로 스크롤, 부재 시 enabled=false 확인 |
| Home 공유·길안내 | 79 카카오 공유 대상 선택 화면, 80 전송 없이 취소, 81 지도 선택 창 확인. 해당 이벤트 가게는 refs의 USER_STORE로 식별했다 |
| Home 재진입 로그 | 87에서 Home 최초 진입 및 마이페이지→Home 복귀 각각 서버 pageview+preset 1회, local pageview 중복 0회 |
| 계좌 복사 host | 89에서 Home/V2의 실제 ClipboardManager에 `테스트은행 123-456` 전달 확인. renderer/VM은 account.text만 선택하고 HTML 제거하며 예금주를 제외하는 단위 검증과 결합 |
| 이벤트/페이지 추가 지도 | 72/73 실제 이벤트 선택 후 제주 카드·마커 표시. 89의 실제 SDK projection에서 두 bounds 점이 필터/바텀시트 가림 밖에 위치. fake page2가 다른 bounds를 보내도 target/zoom/tilt/bearing 및 첫 페이지 query snapshot 유지 |
| 무좌표 방문 인증 | 85 실제 markerless+noMap의 강제 닫기 assertion RED → 닫기 제거 후 89에서 선택 가게·preview·expanded 모두 유지 |
| 리뷰 저장 fake | 89에서 기존 리뷰 ViewModel에 fake HomeRepository를 연결해 가게 ID·본문·평점 및 성공 후 Home 목록/상세 각 1회 갱신 확인. 실제 저장 API 호출 없음 |
| 기여자 pageview | 28 로그에서 서버 pageview 1개, store_id=120024, legacy pageview 0개 |
| 기여자 resume | 38→39 로그 delta에서 서버 pageview 추가 1개, store_id 유지, legacy 0개 |
| 기여자 수정 클릭/취소 | 40에서 서버 edit click 1개, 41 취소 시 추가 pageview 없음 |
| 기여자 오류 fallback | debug에서 빈 storeId로 실제 Activity의 Error를 만들었다. 44 로그 delta에 legacy pageview 1개, 서버 pageview 0개. 이 경로는 API 요청 없음 |
| 기여자 actionBar log 우선 | 실제 BottomActionBar를 debug에서 fake callback으로 실행. 43 UI tree에 outer log 한 개 수신. 서버에는 전송하지 않음 |
| 테스트 광고 | debug 배너를 Google 데모 ID로 고정했다. 66에서 실제 SDK의 2개 Loaded 및 높이 144dp, 네트워크 차단 후 67에서 2개 Failed 및 숨김 높이 0dp를 확인했다. wifi/data는 원래 1/1로 복원하고 재조회했다. 클릭은 fake callback만 검증 |
| 리뷰 답글 | 68에서 생성 코드 기반 답글의 작성자·2026.09.09·본문과 #F4F4F4 배경을 Home/full 모두 확인. `check_sdui_device_contracts.py` PASS |
| Host instrumentation | 63/64는 미국 지역 정상 EMPTY로 test setup 대기 timeout. dev 지역을 명시한 뒤 69/82/86/89로 단계별 검증을 완료했다. 89는 모든 최종 host assertion PASS |
| SDK 초기 추적 | 84의 보존한 수정 전 bytecode baseline은 Follow RED. 정상 최신 소스 89에서는 onMapReady 직후 NoFollow/None 확인. 83은 오래된 APK 실행이므로 tracking 증거로 세지 않음 |

운영 fixture는 익명 응답으로 렌더 구조를 검증하며, 실제 앱 navigation은 dev 서버의 가게를 이용했다. 이 둘을 운영 서버에 로그인한 실제 동작 검증으로 혼동하지 않는다.

데모 배너 ID와 에뮬레이터의 test-device 정책은 [Google 공식 문서](https://developers.google.com/admob/android/test-ads)를 확인했다. 광고 resource override와 instrumentation은 debug source set에만 있다.

## 산출물 확인

- Debug APK: `app/build/outputs/apk/debug/app-debug.apk`.
- 기기 검증 당시 APK SHA-256: `6bd6bc203587d4ac012a709b26cbb48e2dc4e02f74ac9024d881ae28b5177156`. 이후 재빌드 산출물은 별도 checksum으로 구분한다.
- APK dex/resources에 unit-test 전용 `ImmediateMainDispatcherFactory`가 포함되지 않음을 확인했다.
- 문서 local link, fixture JSON 7개, `git diff --check`와 아래 artifact checker 모두 PASS.

```bash
python3 tools/harness/check_sdui_component_render.py build/harness/sdui-alignment-20260909/30-components-green.xml build/harness/sdui-alignment-20260909/30-components-green.png
python3 tools/harness/check_sdui_contributor_logs.py build/harness/sdui-alignment-20260909
python3 tools/harness/check_sdui_device_contracts.py build/harness/sdui-alignment-20260909
```

실제 host 회귀는 debug APK에 등록된 `com.zion830.threedollars.ui.home.design.SduiHostInstrumentation`을 `adb shell am instrument -w`로 실행했다. 초기 가게 읽기 이후 repository는 검증 범위에서 fake로 교체하고 finally에서 복원한다. 기기 ID는 실행할 QA 인스턴스를 명시해야 한다.

검증을 마친 자체 read-only 인스턴스는 정상 종료했다. 기존 앱 데이터와 다른 작업의 기기는 보존했다.

## 판정과 후속 범위

필수 미실행 항목과 확인된 잔여 P1/P2는 없다. 계약·상세/공통·홈·최종 통합 리뷰는 모두 Approved다. TDD 회복은 2026-09-10 사용자 승인을 받아 기존 변경을 보존했으며, 최초 compile-error RED 및 NoFollow 보완 순서는 13 문서에 구분했다.

검증의 한계는 명시적으로 유지한다. 외부 저장/사용/전송은 실제 서비스에 실행하지 않고 fake repository/콜백으로 검증했다. 운영 계정의 모든 가게·로그인 데이터 조합을 전수 확인했다는 뜻은 아니다. 서버·버전·의존성·원격 배포는 변경하지 않았다.

후속 UX QA: 사진 빈 상태와 바로 앨범 진입, 메뉴 접기, Preview 본문 드래그, 딥링크 지도 복귀, USER 사진 리뷰 작성. 이 항목들은 이번 승인 범위 밖이다.

## 커밋 준비 중 추가 검증 — 2026-09-10

- 등록 batch `sdui-prepush-20260910/attempt-2` PASS: SDUI 161개 테스트는 Gradle UP-TO-DATE 및 XML/count/hash로 유효한 캐시 결과를 확인했고 Debug APK는 새로 패키징했다. 새 APK SHA-256은 `3e37635fc39a5918cf9508eb9eabc6e6bd14a565f57d3fcc9369f68a85bb7a9b`이며 위 기기 검증 당시 산출물과 구분한다.
- 전체 `testDebugUnitTest`에서는 변경되지 않은 `core:abtest`의 기본 `ExampleUnitTest`가 JUnit 의존성 누락으로 컴파일되지 않았다. 실패 task는 `:core:abtest:kaptDebugUnitTestKotlin`이며 SDUI 변경으로 발생한 회귀가 아니다. 해당 테스트와 build.gradle.kts는 기준 HEAD 대비 변경이 없다.
- 나머지 실행 가능한 전체 Debug 범위를 완료했다: app 131 + data 34 + core:common 10 + domain 4 + core:network 3 = **182 tests PASS**, skipped/failures/errors 0. src/test가 없는 모듈의 과거 XML은 집계하지 않았다. 전체 suite 자체가 PASS라는 뜻은 아니다.
- 로그: `build/harness/sdui-prepush-full-debug.log`, `build/harness/sdui-prepush-remaining-debug.log`. 사용자는 기존 abtest 실패를 기록하고 SDUI 변경만 커밋·푸시하는 통합 예외를 승인했다. JUnit 의존성은 추가하지 않았으며 전체 suite를 PASS로 표현하지 않는다.
