# Android Agent Skills

This directory vendors selected official Android skills for project-local agent use.

Source: https://github.com/android/skills
Imported from commit: `a9315d8`
Imported on: 2026-05-14

## Included Skills

- `devtools/android-cli`: Android CLI, SDK, emulator, run, docs workflows.
- `jetpack-compose/migration/migrate-xml-views-to-jetpack-compose`: XML View to Compose migration.
- `system/edge-to-edge`: Adaptive edge-to-edge support for Compose apps.
- `navigation/navigation-3`: Navigation 3 setup and migration patterns.
- `performance/r8-analyzer`: R8 and ProGuard rule analysis.
- `build/agp/agp-9-upgrade`: AGP 9 upgrade guidance.

## Codex Usage

Before Android-specific implementation, migration, build-tooling, or performance work, check whether one of the `SKILL.md` files below this directory applies. If it does, follow that skill alongside the repository rules in `AGENTS.md`.

The Android CLI skill assumes the `android` command is installed. Verify with `command -v android` before using Android CLI commands; if it is unavailable, use the project's existing Gradle wrapper and adb workflows.

## Updating

Preferred update path when Android CLI is installed:

```bash
android skills add --all --project=.
```

Manual update path:

```bash
git clone --depth 1 https://github.com/android/skills /private/tmp/android-skills
```

Then copy only the project-relevant skill directories and keep `LICENSE.txt` with the vendored files.
