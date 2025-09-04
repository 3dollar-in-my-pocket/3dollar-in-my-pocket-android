# MIGRATION_RULES.md

## Clean Architecture Migration Guidelines

### Core Principles

1. **Separation of Concerns**
   - Domain layer: Business logic only, no Android dependencies
   - Data layer: Data sources and repository implementations
   - Presentation layer: UI components and ViewModels

2. **Dependency Rule**
   - Dependencies point inward (Presentation → Domain ← Data)
   - Domain layer must be independent
   - No circular dependencies

3. **Module Independence**
   - Each feature module is self-contained
   - Communication through domain interfaces
   - Shared resources through core modules only

### Resource Management Strategy

#### Core Module Responsibilities

##### core:common
- **Purpose**: Shared business logic and utilities
- **Contains**:
  - ALL string resources (centralized management)
  - Base classes (BaseActivity, BaseFragment, BaseViewModel)
  - Common utilities and extensions
  - Shared data models
  - Animation resources
  - Constants and configurations

##### core:ui  
- **Purpose**: Reusable UI components and utilities
- **Contains**:
  - Common UI components (EmptyStateView, LoadingDialog, CustomToast)
  - UI utilities (DialogUtils, ViewExtensions)
  - Third-party UI library dependencies (CRITICAL: ScaleRatingBar)
  - Common layouts for reusable components

##### core:designsystem
- **Purpose**: Visual consistency across the app
- **Contains**:
  - ALL color resources
  - ALL drawable resources (icons, backgrounds, selectors)
  - Text styles and themes
  - Font resources
  - Dimension values
  - Material Design components configuration

##### core:network
- **Purpose**: Network infrastructure
- **Contains**:
  - API definitions and interfaces
  - Network response models
  - Retrofit/OkHttp configuration
  - Error handling mechanisms

### Migration Patterns

#### Resource Migration Rules

1. **String Resources**
   - ALL strings must be in core:common
   - No feature-specific string files
   - Use descriptive naming: feature_context_description
   - Example: home_search_hint, my_profile_title

2. **Visual Resources**
   - Colors → core:designsystem/colors.xml
   - Drawables → core:designsystem/drawable/
   - Styles → core:designsystem/styles.xml
   - Never duplicate visual resources

3. **UI Components**
   - Reusable components → core:ui
   - Feature-specific layouts → feature/presentation/res/layout/
   - Custom views used in multiple features → core:ui

4. **Third-party Dependencies**
   - UI libraries → core:ui (expose as api)
   - Example: ScaleRatingBar must be in core:ui only
   ```gradle
   // core:ui/build.gradle
   api "com.github.ome450901:SimpleRatingBar:1.5.1"
   ```

#### Module Structure Pattern

```
feature/
├── domain/           # Business logic (no Android dependencies)
│   ├── model/       # Domain models
│   ├── repository/  # Repository interfaces
│   └── usecase/     # Business use cases
├── data/            # Data layer implementation
│   ├── repository/  # Repository implementations
│   ├── datasource/  # Remote/Local data sources
│   └── mapper/      # Data to Domain mappers
└── presentation/    # UI layer
    ├── ui/         # Activities, Fragments
    ├── viewmodel/  # ViewModels
    ├── adapter/    # RecyclerView adapters
    └── res/        # ONLY feature-specific layouts
```

#### Dependency Configuration Pattern

```gradle
// Feature presentation module dependencies
dependencies {
    implementation project(':feature:domain')
    implementation project(':core:common')      // Strings, base classes
    implementation project(':core:ui')          // UI components
    implementation project(':core:designsystem') // Visual resources
    
    // Android UI dependencies
    implementation 'androidx.fragment:fragment-ktx'
    implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx'
    // ... other UI dependencies
}
```

### Migration Checklist

#### Pre-Migration
- [ ] Identify all resources used in the feature
- [ ] Check for existing resources in core modules
- [ ] List third-party dependencies

#### During Migration
- [ ] Move strings to core:common
- [ ] Move colors/drawables to core:designsystem
- [ ] Move shared UI components to core:ui
- [ ] Update all resource references
- [ ] Update import statements
- [ ] Configure module dependencies

#### Post-Migration
- [ ] Run `./gradlew build`
- [ ] Verify resource accessibility
- [ ] Check layout previews
- [ ] Test runtime functionality
- [ ] Remove unused resources from app module

### Critical Rules

1. **NEVER create duplicate resources**
   - Always search existing resources first
   - Consolidate similar resources

2. **ScaleRatingBar Special Handling**
   - Only in core:ui module
   - Access through module dependency
   - Never add directly to feature modules

3. **Clean Architecture Compliance**
   - Domain layer: Pure Kotlin/Java only
   - Data layer: Can have Android dependencies
   - Presentation: Full Android framework access

4. **Resource Naming Conventions**
   - Strings: feature_context_description
   - Layouts: feature_type_name (e.g., home_fragment_main)
   - IDs: feature_view_purpose (e.g., home_text_title)

### Common Pitfalls to Avoid

1. Adding Android dependencies to domain layer
2. Creating feature-specific string files
3. Duplicating colors or drawables
4. Adding UI libraries directly to feature modules
5. Circular dependencies between modules
6. Not following the dependency rule

### References
- Clean Architecture by Robert C. Martin
- Android Architecture Components Guide
- Multi-module project best practices