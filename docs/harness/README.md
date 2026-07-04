# Harness

이 디렉터리는 반복 검증 절차를 문서화한다. 실행 스크립트는 `tools/harness/`와 `tools/design/`에 둔다.

## Minimal Checks

```bash
tools/harness/run_minimal_checks.sh
```

포함 항목:

- 홈 관련 app unit test
- screen mapper data unit test
- debug assemble

문서/스크립트만 바꾼 경우에는 전체 실행이 필수는 아니다. 문법 확인과 링크 확인으로 충분한지 판단한다.

## Android Smoke

```bash
tools/harness/run_android_smoke.sh
```

전제:

- `adb`가 PATH에 있어야 한다.
- 연결된 device 또는 emulator가 있어야 한다.
- debug build에 필요한 `local.properties` 값이 준비되어 있어야 한다.

출력:

- `build/harness/android-smoke/<timestamp>/screenshot.png`
- `build/harness/android-smoke/<timestamp>/window.xml`
- `build/harness/android-smoke/<timestamp>/crash.log`
- `build/harness/android-smoke/<timestamp>/process.txt`

## Design Verification

```bash
tools/design/capture_home_store_preview.sh
```

전제:

- debug source set의 `HomeStorePreviewDesignActivity`를 사용한다.
- 필요하면 `FIGMA_REFERENCE=/path/to/reference.png`를 넘겨 screenshot diff를 생성한다.

예시:

```bash
FIGMA_REFERENCE=/tmp/figma.png \
DESIGN_MAX_MAE=8 \
DESIGN_MAX_MISMATCH_PERCENT=10 \
tools/design/capture_home_store_preview.sh
```

## Result Reporting

완료 보고에는 다음을 남긴다.

- 실행한 명령
- PASS/FAIL
- output artifact 경로
- 기기/에뮬레이터 이름 또는 `ANDROID_SERIAL`
- 실행하지 못한 항목과 이유
