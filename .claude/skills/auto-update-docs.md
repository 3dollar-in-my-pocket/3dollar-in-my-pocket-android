# Auto Update Documentation Skill

## Purpose
Automatically analyze recent code changes and update project documentation to maintain accuracy and consistency.

## When to Use
- After completing a significant feature or refactoring
- After modifying module dependencies
- After adding or removing resources
- After architectural changes
- When explicitly requested by the user

## Documentation Files to Maintain
1. **CLAUDE.md** - Claude Code working guide (build commands, architecture, tech stack)
2. **DEPENDENCY_MAP.md** - Module dependency structure and issues
3. **MIGRATION_RULES.md** - Clean Architecture migration guidelines and checklists

## Analysis Process

### Step 1: Analyze Recent Changes
First, gather information about what changed:

```bash
# Get recent commits (last 10 or since last update)
git log --oneline -10

# Get detailed changes with stats
git log -5 --stat --pretty=format:"%h - %an, %ar : %s"

# Get changed files
git diff --name-only HEAD~5..HEAD

# For uncommitted changes
git status
git diff --stat
```

### Step 2: Identify Affected Documentation Areas

Check which documentation needs updating based on changes:

#### For CLAUDE.md Updates:
- **Build system changes**: Modified build.gradle files, Gradle version updates
- **New dependencies**: Added libraries in build.gradle files
- **Architecture changes**: New modules, renamed packages, changed patterns
- **Configuration changes**: SDK versions, build variants, signing configs
- **New features**: Major feature additions that change app description

Search patterns:
```bash
# Check for build file changes
git diff HEAD~5..HEAD -- "**/build.gradle*" "gradle.properties" "settings.gradle"

# Check for new modules
ls -la | grep -E "^d" | wc -l  # Compare module count

# Check version changes
grep -r "versionCode\|versionName" app/build.gradle
```

#### For DEPENDENCY_MAP.md Updates:
- **Module dependency changes**: build.gradle dependency blocks
- **New modules created**: New feature/core modules
- **Circular dependencies**: New cross-module references
- **Layer violations**: Domain depending on Android/Network

Search patterns:
```bash
# Find all module dependencies
find . -name "build.gradle" -type f -exec grep -l "project(':')" {} \;

# Check dependencies in each module
./gradlew app:dependencies --configuration debugRuntimeClasspath
```

#### For MIGRATION_RULES.md Updates:
- **Resource migrations**: Strings, colors, drawables moved between modules
- **Pattern changes**: New architectural patterns implemented
- **Checklist progress**: Completed migration tasks
- **New rules**: Discovered best practices or pitfalls

Search patterns:
```bash
# Check resource movements
git diff HEAD~5..HEAD -- "**/res/**"

# Check completed migrations
git log --all --grep="migrate\|refactor\|move" --oneline -10
```

### Step 3: Update Each Document

#### Updating CLAUDE.md

**What to update:**
- Project Architecture section if modules added/removed/renamed
- Key Technologies if new libraries added
- Configuration section if SDK/versions changed
- Build Commands if new Gradle tasks added
- Current Version when version bumped

**Example updates:**
```markdown
# If a new module was added
## Project Architecture
- **new-module**: Description of what this module does

# If dependencies changed
### Key Technologies
- **New Library**: NewLib version X.Y.Z

# If version changed
### Configuration
- **Current Version**: X.Y.Z (versionCode N)
```

#### Updating DEPENDENCY_MAP.md

**What to update:**
- Current dependency structure tree when modules added/changed
- Problems section if new issues found or fixed
- Correction plan if architecture improved

**Steps:**
1. Generate current dependency tree for changed modules
2. Check for circular dependencies
3. Verify layer violations
4. Update the visual structure diagram
5. Update problem list (add new issues or mark as fixed)

**Example process:**
```bash
# For each feature module, check dependencies
./gradlew :home:presentation:dependencies --configuration debugCompileClasspath | grep "project"
./gradlew :my:data:dependencies --configuration debugCompileClasspath | grep "project"

# Analyze the output and update the markdown structure
```

#### Updating MIGRATION_RULES.md

**What to update:**
- Phase completion status when migration tasks completed
- Implementation Summary when significant work done
- Checklist items when tasks finished
- New patterns if new approaches used
- Common Pitfalls if new issues discovered

**Key sections to check:**
- Migration Checklist (update [x] for completed items)
- Phase status (mark as COMPLETED if finished)
- Verification Commands (add new verification steps)
- Common Pitfalls (add newly discovered issues)

### Step 4: Verification

After updating documentation:

1. **Accuracy Check**: Verify all technical details match codebase
   ```bash
   # Verify version numbers
   grep -r "versionCode\|versionName" app/build.gradle

   # Verify module list
   cat settings.gradle | grep "include"

   # Verify dependencies match
   ./gradlew dependencies
   ```

2. **Consistency Check**: Ensure docs don't contradict each other
   - Cross-reference module names across all docs
   - Ensure dependency rules match between DEPENDENCY_MAP and MIGRATION_RULES
   - Verify architectural descriptions consistent with CLAUDE.md

3. **Completeness Check**: All recent changes reflected
   - Review git log again
   - Check if all major changes documented
   - Verify no critical information missing

### Step 5: Summary Report

Provide a clear summary to the user:

```markdown
## Documentation Update Summary

### Files Updated:
- CLAUDE.md: [specific sections updated]
- DEPENDENCY_MAP.md: [specific sections updated]
- MIGRATION_RULES.md: [specific sections updated]

### Key Changes:
1. [Description of change 1]
2. [Description of change 2]
3. [Description of change 3]

### Verification:
- Build commands tested: ✅/❌
- Module structure verified: ✅/❌
- Dependencies confirmed: ✅/❌

### Recommended Next Actions:
- [Any follow-up tasks]
```

## Best Practices

1. **Be Thorough**: Check all aspects of changes, not just obvious ones
2. **Be Accurate**: Verify all facts before updating documentation
3. **Be Consistent**: Use same terminology across all docs
4. **Be Specific**: Use concrete examples and exact module/file names
5. **Be Current**: Include version numbers, dates, and latest status
6. **Preserve Format**: Maintain existing markdown structure and style
7. **Update Incrementally**: Small, focused updates are better than large rewrites

## Commands Reference

Useful commands for documentation updates:

```bash
# Quick module overview
ls -d */ | grep -v "^\."

# Count total modules
find . -name "build.gradle" -type f | wc -l

# List all feature modules
find . -type d -name "presentation" -o -name "domain" -o -name "data" | grep -v build

# Check resource locations
find . -path "*/src/main/res/values/strings.xml" | head -20

# Get dependency graph (requires graphviz)
./gradlew app:dependencies --configuration debugCompileClasspath

# Find TODO/FIXME comments that might indicate work in progress
grep -r "TODO\|FIXME" --include="*.kt" --include="*.java" | head -20
```

## Error Handling

If documentation update fails:
1. Check if documentation files are readable/writable
2. Verify git commands work in current directory
3. Ensure Gradle wrapper is executable
4. Report specific error to user and ask for guidance

## Example Workflow

```
User: "작업 완료했으니 문서 업데이트해줘"

Step 1: Analyze changes
- Run git log, git diff
- Identify modified modules and files

Step 2: Determine scope
- Check if CLAUDE.md needs updates (architecture/config changes)
- Check if DEPENDENCY_MAP.md needs updates (module dependencies changed)
- Check if MIGRATION_RULES.md needs updates (migration progress)

Step 3: Update documents
- Read current documentation
- Update relevant sections with verified information
- Maintain consistent formatting

Step 4: Verify
- Cross-check technical details
- Ensure consistency across docs
- Test that examples/commands still work

Step 5: Report
- Provide summary of what was updated
- Highlight important changes
- Suggest any needed follow-ups
```

## Notes

- Always use `Read` tool before `Edit` or `Write` on documentation files
- Prefer `Edit` over `Write` to preserve other content
- Use git commands to understand change context
- Don't assume - always verify facts from codebase
- When uncertain, ask user for clarification before updating
