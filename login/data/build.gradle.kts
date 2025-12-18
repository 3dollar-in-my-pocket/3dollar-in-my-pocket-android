plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kapt)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.login.data"

    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }
}

dependencies {
    implementation(project(":login:domain"))
    implementation(project(":core:network"))
    implementation(project(":core:common"))

    implementation(libs.bundles.androidx.ui)
    testImplementation(libs.bundles.testing)

    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    implementation(libs.bundles.retrofit)
    implementation(libs.gson)
}
