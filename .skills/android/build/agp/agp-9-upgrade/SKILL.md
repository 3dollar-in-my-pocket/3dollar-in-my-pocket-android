---
name: agp-9-upgrade
description: Upgrades, or migrates, an Android project to use Android Gradle Plugin
  (AGP) version 9. Do not use this skill for migrating Kotlin Multiplatform (KMP)
  projects.
license: Complete terms in LICENSE.txt
metadata:
  author: Google LLC
  last-updated: '2026-04-17'
  keywords:
  - Android Gradle Plugin 9
  - AGP 9
  - AGP Upgrade
  - AGP Migration
  - New AGP DSL
  - Migrate to built-in Kotlin
---

## Migration guide

Project policy: [Android skill usage](../../../README.md#codex-usage).

See the [AGP 9 migration guide](references/android/build/releases/agp-9-0-0-release-notes.md) for the major changes, many
breaking, in AGP 9 compared to AGP 8.

## Requirements

If the user requests to update or migrate to AGP 9, first check the AGP version
used in the project and the approved target version. If it is lower than 9,
require the user to complete the AGP Upgrade Assistant in Android Studio or
explicitly waive that step before changing migration code. Reuse a previously
confirmed completion or waiver for the same migration; do not ask again.

While waiting, continue read-only compatibility research and prepare the
proposed changes and verification plan. A general upgrade request does not by
itself waive the Assistant requirement. If the user explicitly waives it, update
AGP only to the approved target before following the steps below. If a target
has not been specified, identify a compatible AGP 9 release for approval rather
than silently choosing a different major version. See the
[AGP 9 migration guide](references/android/build/releases/agp-9-0-0-release-notes.md) for how to do this.

Each version of AGP has its own set of compatibilities with other tools, such as
Gradle, JDK, and Kotlin. The release notes for each of these versions will
include a **Compatibility** table indicating the minimum versions for these
tools.
Identify required Gradle, JDK, Kotlin, and library changes in the approval scope;
the compatibility requirements and examples below are not permission to apply
unapproved upgrades.

Do not use this skill for KMP projects, as they are unsupported.

## Steps

If AGP is already at 9 or higher, then do the following:

### Step 1: Update dependencies

If KSP (`com.google.devtools.ksp`) is used in the project, ensure it is on
version 2.3.6 or higher.

If Hilt is used in the project, ensure it is on version 2.59.2 or higher.

### Step 2: Migrate to built-in Kotlin

See [the guide](references/android/build/migrate-to-built-in-kotlin.md) for detailed information.

### Step 3. Migrate to the new AGP DSL

See [the guide](references/android/build/releases/agp-9-0-0-release-notes.md) for detailed information.

See also [gradle-recipes](references/recipes.md) for examples on how to migrate old code to code
that is compatible with AGP 9 and the new DSL.

### Step 4. Migrate kapt to KSP or legacy-kapt

If KSP (`com.google.devtools.ksp`) or kapt (`org.jetbrains.kotlin.kapt`) are
used in the project, see [KSP, kapt, and legacy-kapt](references/ksp-kapt.md) for detailed migration
steps.

### Step 5. BuildConfig

If any Android module contains custom BuildConfig fields, see [BuildConfig](references/buildconfig.md)
for detailed information.

### Step 6. Update gradle.properties

After the migration, check gradle.properties. Remove the following flags:

1. android.builtInKotlin
2. android.newDsl
3. android.uniquePackageNames
4. android.enableAppCompileTimeRClass

Additionally, delete all temporary files you've created.

## Guidelines

- Never write or run python scripts.
- Only search the Gradle dependency cache when inspecting external dependencies, and only as a last resort.
- Never add `android.disallowKotlinSourceSets=false` to `gradle.properties`.
- When verifying changes, don't run the `clean` task. This is a waste of time.

## Verification

After migration, verify the following:

1. Gradle IDE sync succeeds.
2. `./gradlew help` succeeds.
3. `./gradlew build --dry-run` succeeds.
4. The affected modules actually build, and relevant tests from the project's
   verification matrix run successfully. `help` and dry-run do not compile or
   test the changed project.

Run the checks available to you instead of handing them back as suggestions.
Report any unobserved IDE sync, failed build, or unavailable required check as
unverified; do not describe the migration as fully verified in that state.

## Troubleshooting

Paparazzi v2.0.0-alpha04 and lower versions have issues with AGP 9. See
[references/paparazzi-gradle-9.md](references/paparazzi-gradle-9.md) for details.
