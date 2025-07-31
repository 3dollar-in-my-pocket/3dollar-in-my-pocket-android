# Task Completion Checklist

## After Making Code Changes

### 1. Build Verification
- Run `./gradlew build` to ensure project compiles successfully
- For specific modules, run `./gradlew :module:build`

### 2. Testing (if applicable)
- Run unit tests: `./gradlew test`
- Run instrumented tests if UI changes: `./gradlew connectedAndroidTest`
- Test specific classes: `./gradlew test --tests "ClassName"`

### 3. Code Quality
- Ensure Kotlin code follows official style guide
- Check for proper error handling
- Verify proper resource cleanup (especially for network calls, file operations)

### 4. Architecture Compliance
- Ensure MVVM pattern is followed
- Check proper separation of concerns (UI, Business Logic, Data)
- Verify dependency injection is used correctly

### 5. Before Committing
- Remove any debug logs or temporary code
- Ensure no sensitive data (API keys, tokens) is committed
- Check that build variants (debug/release) work correctly
- Verify proper resource usage (no unused resources)

### 6. Documentation
- Update inline code documentation if significant changes
- Update CLAUDE.md if build commands or architecture changes