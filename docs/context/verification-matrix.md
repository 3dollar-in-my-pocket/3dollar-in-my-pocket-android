# 검증 매트릭스

변경 범위에 맞는 최소 검증을 선택한다. 자세한 하네스 사용법은 `docs/harness/README.md`를 본다.

## 기본

| 변경 유형 | 권장 검증 |
| --- | --- |
| 문서만 변경 | `rg` 링크 확인, 관련 Markdown 검토 |
| Gradle/의존성 변경 | `./gradlew dependencies`, 영향 모듈 build |
| 순수 Kotlin mapper/model 변경 | 해당 module `testDebugUnitTest --tests ...` |
| 홈 서버드리븐 변경 | `:data` screen mapper test, `:app` home unit test, `:app:assembleDebug` |
| Compose UI 변경 | 관련 unit test, `:app:assembleDebug`, 필요 시 design harness |
| 지도/위치/Activity flow 변경 | `:app:assembleDebug`, adb smoke, 수동 QA |
| 리소스 이동 | 영향 모듈 build, 앱 launch smoke |
| release/CI 설정 변경 | 관련 workflow dry review, release build task는 필요 시 별도 승인 |

## 자주 쓰는 명령

```bash
./gradlew :app:testDebugUnitTest --tests "com.zion830.threedollars.ui.home.*"
./gradlew :data:testDebugUnitTest --tests "com.threedollar.data.screen.*"
./gradlew :app:assembleDebug
tools/harness/run_minimal_checks.sh
```

## 보고 규칙

- 실행한 검증, 실패한 검증, 실행하지 못한 검증을 완료 보고에 구분해서 남긴다.
- 네트워크, 기기, 권한, 로그인 상태에 의존한 검증은 제약을 함께 적는다.
- 실서버 응답 기반 QA는 데이터가 실행 시점에 달라질 수 있음을 기록한다.
