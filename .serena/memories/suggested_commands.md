# Suggested Development Commands

## Build Commands
- **Build the project**: `./gradlew build`
- **Build debug APK**: `./gradlew assembleDebug`  
- **Build release APK**: `./gradlew assembleRelease`
- **Clean project**: `./gradlew clean`

## Testing Commands
- **Run unit tests**: `./gradlew test`
- **Run instrumented tests**: `./gradlew connectedAndroidTest`
- **Run single test class**: `./gradlew test --tests "com.example.ClassName"`

## Analysis Commands
- **Check dependencies**: `./gradlew dependencies`
- **Generate dependency report**: `./gradlew dependencyInsight --dependency <dependency-name>`

## Git Commands (Darwin/macOS)
- **Status**: `git status`
- **Add files**: `git add .`
- **Commit**: `git commit -m "message"`
- **Push**: `git push`
- **Pull**: `git pull`

## File System Commands (Darwin/macOS)
- **List files**: `ls -la`
- **Find files**: `find . -name "*.kt"`
- **Search in files**: `grep -r "pattern" .`
- **Copy files**: `cp source destination`
- **Move files**: `mv source destination`