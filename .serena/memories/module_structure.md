# Module Structure

This is a multi-module Android application with Clean Architecture:

## Core Modules
- **app**: Main application module with UI, Activities, Fragments, ViewModels, and legacy datasources
- **core/common**: Shared utilities, common data models, base classes, extensions
- **core/network**: Network layer with API definitions, responses, and networking utilities

## Feature Modules (Domain-Data-Presentation pattern)
- **home**: Store discovery, map view, search functionality
- **community**: Polling system and neighborhood features  
- **my**: User profile, reviews, favorites, medals
- **login**: Authentication with Kakao and Google
- **common**: Legacy shared module

## Architecture Patterns
- Clean Architecture with separation of concerns
- MVVM pattern with LiveData
- Repository pattern for data access
- Dependency injection with Hilt
- Single Activity architecture with Navigation Component