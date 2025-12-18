import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kapt)
    alias(libs.plugins.hilt)
}

val properties = Properties()
properties.load(project.rootProject.file("local.properties").inputStream())

android {
    namespace = "com.threedollar.presentation"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        resValue("string", "admob_id", properties.getProperty("admob_id", ""))
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    viewBinding {
        enable = true
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(project(":common"))
    implementation(project(":core:common"))
    implementation(project(":core:network"))
    implementation(project(":community:data"))
    implementation(project(":community:domain"))
    implementation(project(":home:domain"))

    implementation(libs.bundles.androidx.ui)

    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    implementation(libs.play.services.ads)
    implementation(libs.play.services.location)

    implementation(libs.bundles.paging)

    testImplementation(libs.bundles.testing)
}
