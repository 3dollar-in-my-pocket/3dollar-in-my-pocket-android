plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "com.home.domain"

    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }
}

dependencies {
    implementation(project(":core:common"))

    implementation(libs.bundles.androidx.ui)
    testImplementation(libs.bundles.testing)

    implementation(libs.bundles.retrofit)

    implementation(libs.bundles.paging)
}
