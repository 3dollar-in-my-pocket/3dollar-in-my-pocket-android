plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kapt)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.compose.compiler)
}

apply(from = "../common.gradle")

android {
    namespace = "zion830.com.common"
    
    packaging {
        resources {
            excludes += "META-INF/common_release.kotlin_module"
        }
    }
    
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(libs.androidx.compose.ui)
}
