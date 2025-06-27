# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

- **Build the project**: `./gradlew build`
- **Build debug APK**: `./gradlew assembleDebug`  
- **Build release APK**: `./gradlew assembleRelease`
- **Clean project**: `./gradlew clean`
- **Run tests**: `./gradlew test`
- **Run instrumented tests**: `./gradlew connectedAndroidTest`

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
- **Maps**: Naver Maps SDK
- **Image Loading**: Glide
- **Analytics**: Firebase Analytics
- **Login**: Kakao SDK, Google Sign-In

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