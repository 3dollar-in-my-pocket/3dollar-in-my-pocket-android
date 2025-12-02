# 3dollar-in-my-pocket-android Project Overview

## Purpose
This is a location-based Android application for finding street food vendors called "3달러 가게" (3 Dollar Stores). The app provides features for:
- Store discovery and map-based search
- User reviews and ratings 
- Community polling system
- User profiles with favorites and visit history
- Store owner (boss) functionality for managing stores

## Tech Stack
- **Language**: Kotlin
- **Architecture**: Clean Architecture with MVVM pattern 
- **Dependency Injection**: Hilt/Dagger
- **Networking**: Retrofit + OkHttp with Moshi/Kotlinx Serialization
- **UI**: ViewBinding + DataBinding
- **Maps**: Naver Maps SDK (primary), Kakao Map (external navigation)
- **Image Loading**: Glide
- **Analytics**: Firebase Analytics, Crashlytics, Remote Config
- **Authentication**: Kakao SDK, Google Sign-In
- **Navigation**: Navigation Component
- **Animations**: Lottie

## Build Configuration
- **Target SDK**: 34
- **Min SDK**: 24  
- **Kotlin Version**: 1.8.0
- **AGP Version**: 8.3.1
- **Current Version**: 4.8.0 (from gradle.properties)
- **Build Variants**: debug (with `.dev` suffix) and release
- **Java Compatibility**: Version 17