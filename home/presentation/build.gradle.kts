plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.home.presentation"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
        targetSdk = 36

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // Feature domain
    implementation(project(":home:domain"))
    
    // Core modules - MIGRATION_RULES.md compliance
    implementation(project(":core:common"))      // All strings, base classes
    implementation(project(":core:ui"))          // UI components, ScaleRatingBar
    implementation(project(":core:designsystem")) // Colors, drawables, styles
    
    // Android UI
    implementation(libs.bundles.androidx.ui)
    
    // Architecture Components
    implementation(libs.bundles.androidx.lifecycle)
    implementation(libs.bundles.androidx.navigation)
    
    // Dependency Injection
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    
    // Image Loading
    implementation(libs.glide)
    
    // Maps
    implementation(libs.naver.map)
    
    // Testing
    testImplementation(libs.bundles.testing)
}
