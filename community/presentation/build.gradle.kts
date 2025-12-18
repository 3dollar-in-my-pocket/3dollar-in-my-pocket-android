import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt)
}

val properties = Properties()
properties.load(project.rootProject.file("local.properties").inputStream())

android {
    namespace = "com.threedollar.presentation"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
        targetSdk = 36

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

    kotlinOptions {
        jvmTarget = "17"
    }
    viewBinding {
        enable = true
    }
}

dependencies {
    implementation(project(":common"))
    implementation(project(":core:common"))
    implementation(project(":core:network"))
    implementation(project(":community:data"))
    implementation(project(":community:domain"))
    implementation(project(path = ":home:domain"))

    implementation(libs.bundles.androidx.ui)

    // 힐트
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    implementation(libs.play.services.ads)
    implementation(libs.play.services.location)

    implementation(libs.bundles.paging)

    testImplementation(libs.bundles.testing)
}
