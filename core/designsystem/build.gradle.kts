plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kapt)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.hilt)
}

apply(from = "../../common.gradle")

android {
    namespace = "com.zion830.threedollars.core.designsystem"
    
    buildFeatures {
        viewBinding = false
        dataBinding = false
    }
}

dependencies {
    // No additional dependencies needed as they are inherited from common.gradle
}
