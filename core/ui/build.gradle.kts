plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
}

apply(from = "../../common.gradle")

android {
    namespace = "com.zion830.threedollars.core.ui"
    
    buildFeatures {
        viewBinding = true
        dataBinding = false // Overriding common.gradle's dataBinding = true
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:designsystem"))
    
    // UI Dependencies not in common.gradle
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.viewpager2)
    
    // ScaleRatingBar - CRITICAL: Must be accessed through core:ui only
    api(libs.simple.rating.bar)
}
