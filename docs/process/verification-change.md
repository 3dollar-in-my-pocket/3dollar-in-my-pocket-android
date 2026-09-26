# 검증 장치 변경 감지

테스트·CI·모듈 규칙은 PR을 검증하는 **장치**다. 장치 자체를 바꾸는 PR은 테스트가 초록불이어도 그 결과를 그대로 믿을 수 없다.
그래서 **경로만 보고 결정론적으로** 라벨을 붙이고, 바뀐 장치 파일 목록을 코멘트로 남긴다. AI 판단은 개입하지 않는다.

## 라벨 (`.github/labeler.yml`, `actions/labeler`)

| 라벨 | 붙는 조건 (경로) | 리뷰어가 볼 것 |
|---|---|---|
| `verification-change` | `.github/**`, `scripts/**`, `tools/**`, `**/src/test/**`, `**/src/androidTest/**`, `AGENTS.md`, `CLAUDE.md`, `docs/process/**`, `docs/context/**` | 코드보다 **이 파일들을 먼저** 본다. 왜 바꿨는지가 본문 "설명이 필요한 결정"에 있어야 한다 |
| `build-config` | `gradle/libs.versions.toml`, `gradle.properties`, `settings.gradle.kts`, `**/build.gradle.kts`, `common.gradle`, `**/proguard-rules.pro`, `scripts/module-deps-baseline.txt` | 버전·의존성 변경은 **사용자 승인이 있었는지** 먼저 확인한다(`AGENTS.md` Safety). R8 규칙 변경이면 release 빌드 smoke 증거를 본다. `module-deps-baseline.txt`는 **줄어들기만** 해야 한다 |
| `module-boundary` | `settings.gradle.kts`, `**/build.gradle.kts`, `common.gradle`, `scripts/module-deps-baseline.txt` | 의존 방향이 뒤집히지 않았는지. `scripts/check-module-deps.sh`가 통과해도 계층에 맞는 모듈에 코드를 뒀는지 눈으로 본다 |

라벨은 **표시**일 뿐 검증을 실행하지 않는다. `lint.yml`, `test.yml`은 라벨과 무관하게 모든 PR에서 돈다.
라벨을 조건으로 추가 검증을 돌리는 것(풀코스)은 `docs/process/pr-process.md`(위험도 차등)에서 정한다.

## 코멘트 (`.github/workflows/labeler.yml`)

`verification-change`가 붙으면 바뀐 장치 파일 목록을 PR 코멘트 하나에 갱신형으로 남긴다. 작성자는 각 파일을 왜 바꿨는지 본문에 적는다.

## 최초 1회: 라벨 생성

`actions/labeler`는 라벨을 만들지 않는다. 레포에 미리 있어야 한다.

```bash
gh label create verification-change --color FBCA04 --description "테스트·CI·모듈 규칙 등 검증 장치 변경"
gh label create build-config        --color 0E8A16 --description "Gradle·버전·의존성·R8 설정 변경"
gh label create module-boundary     --color 1D76DB --description "모듈 경계·의존 방향에 영향"
```

## 경로 목록을 바꿀 때

- `.github/labeler.yml`과 `labeler.yml` 워크플로의 grep 패턴을 **같이** 바꾼다.
- 새 검증 장치(예: 새 CI 워크플로, 새 스크립트, ktlint/detekt 도입)를 추가하면 그 경로도 추가한다.
- 스크린샷 테스트는 쓰지 않는다(자동화 TC가 대신한다 — `docs/process/testing.md`).
