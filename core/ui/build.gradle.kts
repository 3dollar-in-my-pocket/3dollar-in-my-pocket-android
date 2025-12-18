plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kapt)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.hilt)
}

apply(from = "../../common.gradle")

android {
    namespace = "com.zion830.threedollars.core.ui"
    
    buildFeatures {
        viewBinding = true
        dataBinding = false
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:designsystem"))

    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.viewpager2)

    api(libs.simple.rating.bar)
}
