# 검증 매트릭스

변경 범위에 맞는 최소 검증을 선택한다. 자세한 하네스 사용법은 `docs/harness/README.md`를 본다.

## 기본

| 변경 유형 | 권장 검증 |
| --- | --- |
| 문서만 변경 | `rg` 링크 확인, 관련 Markdown 검토 |
| AGENTS/Skill 지침 변경 | 수정된 링크·Skill frontmatter 확인, 승인 보존·중복 질문·실패·완료 시나리오 검토 |
| Gradle/의존성 변경 | `./gradlew dependencies`, 영향 모듈 build |
| 순수 Kotlin mapper/model 변경 | 해당 module `testDebugUnitTest --tests ...` |
| 홈 서버드리븐 변경 | `:data` screen mapper test, `:app` home unit test, `:app:assembleDebug` |
| Compose UI 변경 | 관련 unit test, `:app:assembleDebug`, 필요 시 design harness |
| 지도/위치/Activity flow 변경 | `:app:assembleDebug`, adb smoke, 수동 QA |
| 리소스 이동 | 영향 모듈 build, 앱 launch smoke |
| release/CI 설정 변경 | 관련 workflow dry review, release build는 아래 승인 기준 적용 |

Release build가 사용자의 명시적 요청·승인에 포함되면 같은 task를 위해 재승인을 요구하지 않는다. 포함되지 않은 release build는 실행할 task와 영향·산출물을 설명하고 별도 승인을 받는다. 로컬 build 승인은 서명 키 변경, 업로드, 배포 또는 원격 workflow 실행의 승인이 아니다.

## 자주 쓰는 명령

```bash
./gradlew :app:testDebugUnitTest --tests "com.zion830.threedollars.ui.home.*"
./gradlew :data:testDebugUnitTest --tests "com.threedollar.data.screen.*"
./gradlew :app:assembleDebug
tools/harness/run_minimal_checks.sh
```

## 보고 규칙

- 실행한 검증, 실패한 검증, 실행하지 못한 검증을 완료 보고에 구분해서 남긴다.
- `help`, dry-run, 정적 검토는 실제 compile·테스트·기기 실행을 대체하지 않는다. 필요한 검증이 막히면 해당 항목은 미완료로 보고한다.
- 네트워크, 기기, 권한, 로그인 상태에 의존한 검증은 제약을 함께 적는다.
- 실서버 응답 기반 QA는 데이터가 실행 시점에 달라질 수 있음을 기록한다.
