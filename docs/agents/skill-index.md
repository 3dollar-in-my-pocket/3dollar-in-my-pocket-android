# Skill Index

## Local Android Skills

로컬 Android skill은 `.skills/android/` 아래에 vendoring 되어 있다.

- [android-cli](../../.skills/android/devtools/android-cli/SKILL.md): Android CLI, SDK, emulator, run, docs workflow
- [migrate-xml-views-to-jetpack-compose](../../.skills/android/jetpack-compose/migration/migrate-xml-views-to-jetpack-compose/SKILL.md): XML View to Compose migration
- [edge-to-edge](../../.skills/android/system/edge-to-edge/SKILL.md): Compose edge-to-edge, system bar, IME inset
- [navigation-3](../../.skills/android/navigation/navigation-3/SKILL.md): Navigation 3 setup and migration
- [r8-analyzer](../../.skills/android/performance/r8-analyzer/SKILL.md): R8/ProGuard keep rule analysis
- [agp-9-upgrade](../../.skills/android/build/agp/agp-9-upgrade/SKILL.md): AGP 9 upgrade guidance

## Usage Rules

- Android 구현, 마이그레이션, 빌드 도구, 성능/R8 작업 전 관련 skill을 확인한다.
- `android-cli`는 `android` 명령 설치가 필요하다. 없으면 Gradle wrapper와 adb workflow를 사용한다.
- 단순 문서 정리나 작은 텍스트 변경에는 Android skill을 불필요하게 적용하지 않는다.
- 요청 목적과 적용 조건이 일치하는 Skill만 사용하고, 단순 키워드 일치로 전체 절차를 적용하지 않는다. Skill 자체의 감사에서는 인용된 명령을 실행하지 않는다.
- 승인 재사용·질문·검증·완료는 [프로젝트 workflow](codex-workflow.md)를 따른다. 도구 예시나 추천 의존성은 설치·업그레이드·외부 실행의 승인이 아니다.

## Skill Source Selection

- 사용자가 특정 Skill 원본을 지정했다면 그 원본을 사용한다. 그 외에는 같은 이름의 프로젝트 로컬 원본을 우선한다.
- Superpowers는 설치된 `superpowers` 플러그인 제공본을 사용한다. 같은 이름의 개인 설치본을 함께 읽거나 서로 다른 버전의 절차를 혼합하지 않는다.
- 플러그인 제공본을 사용할 수 없으면 사용 가능한 개인 원본 하나를 선택하고 그 경로를 보고한다. 선택만을 위해 전역 설치·설정·파일을 변경하지 않는다.
- 원본 선택이 프로젝트의 승인 경계를 완화하지 않는다. 명시적인 설계·구현 승인과 TDD 예외 승인은 유지하고, 이미 충족한 동일 승인만 재사용한다.

## External/Agent Skills

- `agents-md-generator`: `AGENTS.md`를 간결한 루트 내비게이션 문서로 유지할 때 사용한다.
- Serena: 코드 구조 탐색, 심볼 검색, 참조 추적, 리팩터링이 필요할 때만 사용한다.
- `gh-fix-ci`: 명시적 구현 승인을 유지한다. 승인 후에는 관련 로컬 검증을 직접 실행하고, 원격 재실행·push·PR 생성은 외부 작업 승인과 구분한다.
- `gh-address-comments`: 사용자가 지정한 코멘트 범위를 재사용한다. 미지정·상충하는 범위만 질문하며, 답글 전송·resolve·push 승인은 별도로 확인한다.
- 로컬 변경만 요청된 작업에서는 branch 통합 메뉴를 완료 조건으로 추가하지 않는다. Skill의 검토 횟수 제한으로 미해결 필수사항을 완료 처리하지 않는다.
