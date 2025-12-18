plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kapt)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.home.data"

    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(project(":home:domain"))
    implementation(project(":core:network"))
    implementation(project(":core:common"))

    implementation(libs.bundles.androidx.ui)
    testImplementation(libs.bundles.testing)

    // 힐트
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    implementation(libs.bundles.retrofit)

    implementation(libs.bundles.paging)
}
