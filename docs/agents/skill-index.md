# Skill Index

## Local Android Skills

로컬 Android skill은 `.skills/android/` 아래에 vendoring 되어 있다.

- `devtools/android-cli/SKILL.md`: Android CLI, SDK, emulator, run, docs workflow
- `jetpack-compose/migration/migrate-xml-views-to-jetpack-compose/SKILL.md`: XML View to Compose migration
- `system/edge-to-edge/SKILL.md`: Compose edge-to-edge, system bar, IME inset
- `navigation/navigation-3/SKILL.md`: Navigation 3 setup and migration
- `performance/r8-analyzer/SKILL.md`: R8/ProGuard keep rule analysis
- `build/agp/agp-9-upgrade/SKILL.md`: AGP 9 upgrade guidance

## Usage Rules

- Android 구현, 마이그레이션, 빌드 도구, 성능/R8 작업 전 관련 skill을 확인한다.
- `android-cli`는 `android` 명령 설치가 필요하다. 없으면 Gradle wrapper와 adb workflow를 사용한다.
- 단순 문서 정리나 작은 텍스트 변경에는 Android skill을 불필요하게 적용하지 않는다.

## External/Agent Skills

- `agents-md-generator`: `AGENTS.md`를 간결한 루트 내비게이션 문서로 유지할 때 사용한다.
- Serena: 코드 구조 탐색, 심볼 검색, 참조 추적, 리팩터링이 필요할 때만 사용한다.
