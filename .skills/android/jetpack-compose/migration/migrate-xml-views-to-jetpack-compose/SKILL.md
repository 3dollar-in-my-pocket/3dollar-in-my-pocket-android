---
name: migrate-xml-views-to-jetpack-compose
description: Provides a structured workflow for migrating an Android XML View to Jetpack
  Compose. This skill details the step-by-step process, from planning and dependency
  setup, to theming and layout migration, validation and XML cleanup. Use this skill
  when you need to migrate an XML View to Jetpack Compose in an Android project. It
  solves the problem of converting the UI of a legacy XML View into modern, declarative
  Compose components while maintaining interoperability.
license: Complete terms in LICENSE.txt
metadata:
  author: Google LLC
  last-updated: '2026-05-06'
  keywords:
  - Jetpack Compose
  - migration
  - XML
  - Views
  - interoperability
  - incremental adoption
  - UI development
---

This skill guides through the process of migrating an existing Android XML View
to Jetpack Compose. It performs a stable, safe and visually consistent
transition by following a structured, 10-step methodology. This skill migrates
UI (XML to Jetpack Compose) only.

Project policy: [Android skill usage](../../../README.md#codex-usage).

## Objective

To systematically convert a single legacy XML layout into modern, declarative
Jetpack Compose UI while maintaining pixel-perfect visual parity and functional
integrity.

## Summary of the 10-step migration process

1. **Identify the optimal XML candidate for migration**
2. **Analyze the project and layout**
3. **Create a plan**
4. **Capture the XML View UI**
5. **Set up Compose dependencies and compiler**
6. **Set up Compose theming**
7. **Migrate the XML layout to Compose**
8. **Replace usages**
9. **Validate the migration**
10. **XML code removal**

## Detailed steps

### Step 1: Identify the optimal XML candidate for migration

If the user has explicitly specified a target XML layout, proceed to Step 2.
Otherwise, analyze the codebase to identify the best candidate for migration by
following the logic in [references/identify-optimal-xml-candidate.md](references/identify-optimal-xml-candidate.md).

### Step 2: Analyze the project and layout

Analyze the identified XML View's structure, hierarchy, and implementation
details.
Use [references/analysis-of-the-project-and-layout.md](references/analysis-of-the-project-and-layout.md) to
guide your technical audit of the layout and surrounding project context.

### Step 3: Create a plan

Using the outputs of Steps 1 and 2, present a migration plan covering the target,
behavior to preserve, affected files, dependencies, and verification. Obtain
explicit plan approval before implementation. If the same plan and scope have
already been approved, reuse that approval and continue without asking again.
Capture available baseline evidence while waiting. Lack of user interaction
does not grant approval; keep dependent implementation pending until approved.

### Step 4: Capture the XML View UI

Use a supplied image or an existing screenshot of the requested UI state. If no
usable image exists, capture the XML UI with the project's existing emulator,
adb, design harness, or screenshot tests. User interaction being available is
not a reason to require the user to capture an image that you can obtain.

If the required UI state cannot be captured, explain the missing evidence and
request only the needed image or access. Continue independent analysis and
approved preparation. Do not claim visual parity without baseline evidence.
Adding a new test framework or dependency requires the repository's approval;
prefer existing capture tools.

### Step 5: Set up Compose dependencies and compiler

Check `build.gradle` or `libs.versions.toml` for Compose dependencies and
compiler setup. Preserve existing versions. If additions or upgrades are needed,
include the exact changes in the approval scope before applying them, using
[Setup Compose Dependencies and Compiler](references/android/develop/ui/compose/setup-compose-dependencies-and-compiler.md).
Run a sync to ensure dependencies resolve without errors.

### Step 6: Set up Compose theming

If the project already has Compose theming set up, proceed to Step 7. If Compose
theming is missing, initialize it. For Material-based projects, follow
[Material 3 migration guidelines](references/android/develop/ui/compose/designsystems/migrate-xml-theme-to-compose.md).
For custom design systems, apply expert judgment to migrate XML theming and
match existing styles.
**Constraints:** Do not migrate the entire theme. Implement only the minimum
theming required for the specific XML candidate. Maintain original XML themes
for interoperability. Maintain existing project code conventions, patterns,
names and values.

### Step 7: Migrate the XML View to Compose

Convert the XML candidate to Jetpack Compose code, referencing
[references/xml-layout-migration.md](references/xml-layout-migration.md) and the image from Step 4.
You must include a **Compose Preview** for the newly created composable to
facilitate visual verification.

### Step 8: Replace usages

Replace the usages of the migrated XML layout to use the new Compose component.

- To add Compose in Views, use [Compose in Views](references/android/develop/ui/compose/migrate/interoperability-apis/compose-in-views.md).
- To add Views in Compose, use [Views in Compose](references/android/develop/ui/compose/migrate/interoperability-apis/views-in-compose.md).

### Step 9: Validate the migration

Compare the baseline screenshot image from Step 4 with the rendered Compose
Preview of the new composable. Ignore string content; focus on layout and
styling. Iterate on the Compose code until visual parity is achieved. Once
verified, write a Compose UI test for the new composable.
Run the relevant checks from the project's verification matrix. If baseline,
preview, or runtime verification cannot be completed, report that specific gap
instead of marking the migration fully verified.

### Step 10: XML code removal

Delete the migrated XML file and its associated legacy tests. **Caution:** Only
remove code and resources that are not referenced by other parts of the project.
