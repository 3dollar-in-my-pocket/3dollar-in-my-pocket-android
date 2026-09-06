# 현재 프로젝트 요약

## 목적

"가슴속삼천원" Android 앱은 위치 기반 가게 탐색, 지도/리스트 검색, 리뷰, 방문 인증, 커뮤니티 투표, 로그인/사용자 관리, 광고, 푸시, 지도 연동을 제공한다.

## Toolchain

원천:

- 버전 카탈로그: `gradle/libs.versions.toml`
- 앱 버전: `gradle.properties`
- 모듈 목록: `settings.gradle.kts`

현재 값:

- AGP: `8.13.2`
- Kotlin: `2.2.20`
- compileSdk: `36`
- targetSdk: `36`
- minSdk: `24`
- Java/Kotlin toolchain: JDK 17
- versionName: `4.22.1`
- versionCode: `128`

## 주요 기술

- Kotlin, Android Gradle Plugin, Gradle Kotlin DSL
- XML/ViewBinding/DataBinding과 Jetpack Compose interop 혼합
- MVVM, Hilt
- Retrofit, OkHttp, Kotlinx Serialization, Gson
- Naver Map SDK, Kakao SDK, Google/Firebase, AdMob
- JUnit4 기반 unit test, 일부 androidTest smoke/test skeleton

## 앱 진입점

- launcher: `com.zion830.threedollars.ui.splash.ui.SplashActivity`
- main shell: `com.zion830.threedollars.MainActivity`
- main navigation graph: `app/src/main/res/navigation/mobile_navigation.xml`
- debug 디자인 검증 Activity: `com.zion830.threedollars.ui.home.design.HomeStorePreviewDesignActivity`

## 민감정보

- `local.properties`, keystore, Firebase/AdMob/지도/소셜 로그인 키는 노출하지 않는다.
- `app/build.gradle.kts`와 `core/network/build.gradle.kts`는 `local.properties`에서 buildConfig와 manifest placeholder 값을 읽는다.

## 현재 문서 원칙

- 이 문서는 현재 상태만 기록한다.
- 목표 구조, 개선 계획, migration checklist는 `docs/context/migration-rules.md` 또는 feature plan에 기록한다.
