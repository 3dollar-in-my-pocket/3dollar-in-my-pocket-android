plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kapt)
    alias(libs.plugins.hilt)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.home.presentation"

    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

dependencies {
    implementation(project(":home:domain"))
    
    implementation(project(":core:common"))
    implementation(project(":core:ui"))
    implementation(project(":core:designsystem"))
    
    implementation(libs.bundles.androidx.ui)
    
    implementation(libs.bundles.androidx.lifecycle)
    implementation(libs.bundles.androidx.navigation)
    
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    
    implementation(libs.glide)
    
    implementation(libs.naver.map)
    
    testImplementation(libs.bundles.testing)
}
