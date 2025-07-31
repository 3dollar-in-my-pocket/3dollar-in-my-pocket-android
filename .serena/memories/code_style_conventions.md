# Code Style and Conventions

## Kotlin Style
- Uses official Kotlin code style (`kotlin.code.style=official` in gradle.properties)
- Follows Android Kotlin style guide conventions

## Naming Conventions
- **Packages**: lowercase with dots (e.g., `com.zion830.threedollars.ui.home`)
- **Classes**: PascalCase (e.g., `HomeViewModel`, `StoreDetailActivity`)
- **Functions**: camelCase (e.g., `getCurrentLocationName`, `showToast`)
- **Variables**: camelCase (e.g., `userName`, `storeId`)
- **Constants**: UPPER_SNAKE_CASE (e.g., `MIN_CLICK_DELAY_MS`, `BITMAP_QUALITY`)

## Architecture Patterns
- **ViewModels**: End with `ViewModel` (e.g., `HomeViewModel`)
- **Activities**: End with `Activity` (e.g., `MainActivity`, `SplashActivity`)
- **Fragments**: End with `Fragment` (e.g., `HomeFragment`)
- **Adapters**: End with `Adapter` or `RecyclerAdapter`
- **Data classes**: Descriptive names based on purpose (e.g., `User`, `StoreDetail`)

## File Organization
- UI components grouped by feature in separate packages
- ViewModels in dedicated `viewModel` packages
- Adapters in `adapter` packages
- Utils in shared `utils` packages
- Data models in `model` or `data` packages

## Dependency Injection
- Uses Hilt/Dagger annotations
- Modules organized by feature and layer
- Interface-based dependency injection