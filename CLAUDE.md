# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

- **Build the project**: `./gradlew build`
- **Build debug APK**: `./gradlew assembleDebug`  
- **Build release APK**: `./gradlew assembleRelease`
- **Clean project**: `./gradlew clean`
- **Run tests**: `./gradlew test`
- **Run instrumented tests**: `./gradlew connectedAndroidTest`
- **Run single test class**: `./gradlew test --tests "com.example.ClassName"`
- **Check dependencies**: `./gradlew dependencies`

## Project Architecture

This is a multi-module Android application following Clean Architecture principles with MVVM pattern:

### Module Structure
- **app**: Main application module containing UI, Activities, Fragments, ViewModels, and legacy datasources
- **core/common**: Shared utilities and common data models
- **core/network**: Network layer with API definitions and responses
- **Feature modules** (following domain-data-presentation pattern):
  - **home**: Store discovery, map view, search functionality
  - **community**: Polling system and neighborhood features  
  - **my**: User profile, reviews, favorites, medals
  - **login**: Authentication with Kakao and Google
- **common**: Legacy shared module

### Key Technologies
- **Language**: Kotlin
- **Dependency Injection**: Hilt/Dagger
- **Networking**: Retrofit + OkHttp with Moshi/Kotlinx Serialization
- **UI**: ViewBinding + DataBinding
- **Architecture**: MVVM with LiveData
- **Maps**: Naver Maps SDK (primary), Kakao Map (external navigation)
- **Image Loading**: Glide
- **Analytics**: Firebase Analytics, Crashlytics, Remote Config
- **Login**: Kakao SDK, Google Sign-In
- **Navigation**: Navigation Component
- **Animations**: Lottie

### Configuration
- **Target SDK**: 34
- **Min SDK**: 24
- **Kotlin Version**: 1.8.0
- **AGP Version**: 8.3.1
- **Current Version**: 4.7.3 (versionCode 98)

### Build Configuration
- Two build variants: `debug` (with `.dev` suffix) and `release`
- Signing configuration uses `3dollor-android-key.jks`
- API keys and sensitive data stored in `local.properties`
- Proguard disabled in both variants for debugging

### Testing
- Unit tests in each module's `test` directory
- Instrumented tests in `androidTest` directories
- Test runner: AndroidJUnitRunner

The app is a location-based service for finding street food vendors ("3달러 가게") with features for store discovery, reviews, community polling, and user management.

## Development Notes

### Data Layer Patterns
- Repository pattern with data/domain/presentation separation in feature modules
- API responses handled in `core/network` with centralized error handling
- SharedPreferences used for local data persistence

### UI Patterns
- Single Activity architecture with Navigation Component
- Custom views for rating bars, hashtag displays, and image pickers
- Material Design components with custom theming

### External Integrations
- Deep linking support with Firebase Dynamic Links
- Push notifications via Firebase Cloud Messaging
- AdMob integration for monetization
- Social login with Kakao and Google SDKs

## Resource Migration Guidelines

This project follows Clean Architecture with strict resource management rules.
See `MIGRATION_RULES.md` for comprehensive migration guidelines.

### Quick Reference
- **ALL strings**: `core:common/strings.xml` (centralized)
- **Colors/Drawables**: `core:designsystem`
- **Reusable UI**: `core:ui`
- **ScaleRatingBar**: Access only through `core:ui`

### Module Dependencies Template
```gradle
dependencies {
    implementation project(':core:common')      
    implementation project(':core:ui')          
    implementation project(':core:designsystem')
}
```

### Current Migration Status

**Phase 2-1: String Resource Centralization** ✅ **COMPLETED**
- All string resources have been moved to `core:common/strings.xml`
- All modules now reference strings via `import com.threedollar.common.R as CommonR`
- Feature modules (`home`, `my`, `community`, `login`) use `CommonR.string.xxx` syntax
- App module manifest strings (`app_name_3dollar*`) remain in app module for AndroidManifest.xml
- Google Services strings (`default_web_client_id`) remain in app module (auto-generated)

### String Resource Usage
```kotlin
// In any module (except core:common itself):
import com.threedollar.common.R as CommonR

// Usage:
getString(CommonR.string.your_string_name)
showToast(CommonR.string.your_string_name)
```

### Important Migration Notes
- Never create new resources without checking existing ones
- Follow Clean Architecture dependency rules
- Maintain module independence
- See MIGRATION_RULES.md for detailed patterns