plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.login.domain"

    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:network"))

    implementation(libs.bundles.retrofit)

    implementation(libs.bundles.androidx.ui)
}
