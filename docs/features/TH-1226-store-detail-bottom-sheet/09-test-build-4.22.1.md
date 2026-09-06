# 4.22.1 (128) 테스트 빌드 준비

2026-09-07 사용자 요청으로 TH-1226 수정과 에이전트·스킬 문서 변경을 로컬 커밋하고 앱 버전을 올렸다.

- `versionName`: `4.22.0` → `4.22.1`
- `versionCode`: `127` → `128`
- 버전 원천: `gradle.properties`
- 현재 프로젝트 문서의 오래된 앱 버전도 실제 값에 맞췄다.

## 검증

```bash
./gradlew :app:testDebugUnitTest --tests 'com.zion830.threedollars.ui.storeDetail.v2.*' --tests 'com.zion830.threedollars.ui.home.*' :data:testDebugUnitTest --tests 'com.threedollar.data.screen.*' :app:assembleDebug
```

- app 상세·홈 67 tests, data screen 24 tests: 총 91건, failures/errors/skipped 0.
- Debug APK build 성공.
- `aapt2 dump badging`으로 APK 내부 package `com.zion830.threedollars.dev`, versionName `4.22.1`, versionCode `128`을 확인했다.
- 변경된 Markdown 29개에서 끊어진 로컬 파일 링크 0건. 변경/추가된 SKILL frontmatter 6개 YAML 파싱 성공.
- `.skills/android/build`의 Markdown 문서만 추적되고 APK/일반 build 산출물은 계속 무시되는 것을 확인했다.
- 새로 추적한 AGP 원문 참고 문서의 두 Markdown 강제 줄바꿈에서 기본 whitespace 검사의 trailing-space 경고가 발생했다. 원문 서식을 보존했으며, 해당 두 줄 외 whitespace 검사는 통과했다.

## 산출물과 커밋

- [Debug APK](../../../app/build/outputs/apk/debug/app-debug.apk)
- [테스트·버전 집계](../../../build/harness/commit-preflight-20260907/summary.json)
- `fcbd5988`: 에이전트 승인/검증 지침과 Android skill 문서.
- `f4e1dd5c`: 상세 액션, 서버드리븐 표시, 아이콘 경로와 중앙 정렬 수정.
- 앱 버전과 이 기록은 별도 버전 커밋에 포함한다.

Firebase 업로드와 원격 push는 실행하지 않았다. 이전 클릭 검증의 실제 서버 변경 미실행 범위는 기존 검증 문서에 기록되어 있다.
