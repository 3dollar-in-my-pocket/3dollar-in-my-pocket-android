# TH-1401 릴리즈 에뮬레이터 검증

## 승인·범위 — 2026-10-08

사용자가 릴리즈 버전을 에뮬레이터에 설치해 홈·광고를 확인하도록 요청했다. 현재 release 설정 그대로 빌드하며 서명·운영 서버·광고 단위와 별도 패키지 `com.zion830.threedollars`를 사용한다. 기존 debug `.dev` 패키지는 보존한다. 배포·업로드·버전/의존성·서명 설정 변경은 요청 범위에 없다.

- 기존 실기기를 대신했다고 표현하지 않고 릴리즈 APK의 에뮬레이터 실행 결과로 기록한다.
- Pixel_7_API_35/API35/arm64, 공개 서울시청 좌표를 사용한다. 기존 production 패키지가 있으면 데이터 삭제/서명 불일치 강제 설치를 하지 않는다.
- APK 빌드·서명 확인, 정상 launcher 진입, 초기 큐레이션·카테고리·탭·상세·시트 버튼 상태와 실제 release 광고 뷰 경로를 확인한다.
- release SDK는 기존 운영 광고 단위를 사용한다. Android 에뮬레이터는 SDK에서 자동 테스트 기기로 취급하며 테스트 표시를 확인한 광고만 클릭 검증한다. 다른 광고 형식/네트워크가 나오면 노출·실패와 UI까지만 확인한다.
- 현재 `isMinifyEnabled=false`이며 R8 최적화 설정을 바꾸거나 R8 검증으로 보고하지 않는다. release에서는 HTTP BODY와 SDLogSender 디버그 로그가 비활성화되므로 SDK callback·실제 화면과 앞선 유닛/Debug 로그 증거를 구분한다.

원천: [Google 테스트 광고·자동 에뮬레이터 테스트 기기](https://developers.google.com/admob/android/test-ads).

## 결과

- JDK17 `./gradlew :app:assembleRelease --console=plain` PASS, BUILD SUCCESSFUL(1m34s).
- release4.26.0/versionCode132, package `com.zion830.threedollars`. APK133,062,973bytes, SHA256 `19312d006f3cd95b1ad02a33b800870c3a60088a8b58e47ac25a45613e8e96a5`.
- `apksigner verify --verbose` PASS: APK Signature v2, signer1. 인증서/키 값은 출력하지 않았다.
- APK의 debuggable=false 및 debug HomeCurationDesignActivity 부재를 확인했다. release-specific DTO/mapper 분기는 없으며 운영 BASE_URL과 광고 리소스 선택만 다르다. 현재 minify=false로 R8 최적화 검증은 아니다.
- 기존 에뮬레이터에 debug `.dev`만 설치돼 있음을 확인한 뒤 release를 별도로 설치했다. 설치·정상 Splash launcher 실행 PASS. 앱 오류는 관측되지 않았다.
- 새 production 패키지는 소셜 로그인 화면으로 진입했다. 게스트 UI/코드 경로는 없고 로그인 없이 Main에 우회 진입하지 않았다. 홈·운영 광고 검증은 유효한 운영 로그인 전이라 미실행이다.
- 사용자에게 직접 로그인 또는 사용할 테스트 계정/방법을 요청했다. headless 검증 에뮬레이터를 종료하고 사용자 조작 가능한 visible 에뮬레이터를 열어 release 로그인 화면을 준비했다. 계정/토큰을 읽거나 debug 세션을 production으로 복사하지 않았다.
- `build/harness/th1401-release/launch.png/xml`, `build-results.json`, `build.log`에 현재 증거를 보존했다. production 로그인과 홈 표시를 완료한 것으로 보고하지 않는다.

## 사용자 지정 Google 로그인 진행

사용자가 “에뮬열고 구글로그인으로 하면 될꺼야”라고 경로를 지정했다. UI의 Google 로그인 버튼을 누르고 등록된 유일한 계정을 선택했다. Google 인증 화면(`com.google.android.gms/.signin.activity.ConsentActivity`)의 로딩이 지속됐다. 앱 데이터를 보존해 앱만 재시작한 뒤 한 번 재시도했으나 같은 상태였다. 실패/cancel을 성공으로 처리하거나 Main에 우회 진입하지 않았다.

- 관측: Wi-Fi 연결·인터넷 VALIDATED, 전역 proxy 없음. 현재 release APK의 서명이 production Google Android OAuth 설정과 일치하고, 생성된 release 웹 client ID도 main 설정과 일치함을 값 없이 Boolean으로 확인했다. 이 사실이 원격 OAuth 서비스 상태 전체를 검증하는 것은 아니다.
- 알려진 Google 오류명/숫자 statusCode는 기존 필터에서 관측되지 않았다. 원문 로그·토큰은 저장하지 않았다. 앱 크래시는 관측되지 않았다.
- 독립 소스 조사: 현재 Google flow는 추가 scope/ID token/server auth code와 GoogleAuthUtil access token을 사용하며 Google 인증 단계에 앱 timeout이 없다. 실패/cancel result는 현재 조용히 무시된다. 이 코드는 TH-1401 변경에 포함되지 않았고, 현재 spinner의 직접 원인을 소스나 로그 부재만으로 단정하지 않았다. 로그인 흐름을 임의 변경하지 않았다.
- 운영 서버 공개 GET4개(home, curation section 기기 좌표 있음/없음, category cards)를 토큰 없이 공개 테스트 좌표로 확인했다. 모두 ok=true이며 categoryFilters/initial cards/IMAGE_PREVIEW_CARD/ADMOB_CARD 계약이 일치한다. 현재 운영 응답은 height50 섹션 광고가 캐러셀 앞에 있고 각 캐러셀 cards11개(가게+광고)다. 실제 앱 화면 검증을 대신하는 결과는 아니다.
- 사용자에게 현재 인증 화면 확인 또는 다른 승인된 로그인 방법을 요청했다. visible 에뮬레이터는 인증 화면으로 열어 두었다. 릴리즈 홈·실제 운영 광고 뷰는 인증 완료 전이라 여전히 미검증이다.

계정 선택 화면 증거는 개인정보가 포함될 수 있는 로컬 ignored QA 자료로만 보관하며 보고/외부 게시에 사용하지 않는다. `build/harness/th1401-release/build-results.json`과 production-response/log에 인증·API·화면 상태를 구분해 기록했다.

## 로그인 이후 관측 — 광고 깜빡임 조사

위 인증 대기 기록 이후 production `MainActivity`가 foreground이며 운영 큐레이션 가게·카테고리·광고가 표시된 것을 직접 확인했다. 인증이 완료된 과정 자체는 관측하지 않았다. 앱 데이터나 계정을 복사하지 않았고, 그래픽 모드를 바꿔 에뮬레이터를 재시작한 뒤에도 로그인이 유지됐다.

- 운영 홈의 섹션 배너320×50, 카드 사이 배너320×100과 `테스트 광고` 표시, SDK loaded/impression을 확인했다. 시트를 펼치고 인기 캐러셀을 수평으로 이동하여 광고 전체를 노출했다.
- 사용자의 심한 깜빡임 제보를 조사하면서 SwiftShader와 host 그래픽 모드의 정지 영상을 각각 기록했다. host20초 영상에는 광고 내부의 반복 fade가 있고 그 구간의 앱 request/released/SDK loaded 이벤트는 없다. 소재 애니메이션이라는 해석과 미확인 사항은 [14-admob-flicker-investigation.md](14-admob-flicker-investigation.md)에 기록한다.
- `home_ads=PENDING_AUTH`는 현재 상태가 아니다. 홈·광고 표시 경로는 관측했지만 릴리즈 카테고리 변경·탭 전환·상세·페이지네이션·광고 클릭의 전체 검증은 아직 끝내지 않았다. 광고 깜빡임을 수정 완료 또는 실기기 정상으로 보고하지 않는다.
- 앱 소스·APK는 변경하지 않았다. 현재 visible 에뮬레이터는 데이터 삭제 없이 `-gpu host`로 실행 중이다. `flicker-final-home.png/xml`, `flicker-full-idle.mp4`, `flicker-host-idle.mp4`가 로컬 증거다.
