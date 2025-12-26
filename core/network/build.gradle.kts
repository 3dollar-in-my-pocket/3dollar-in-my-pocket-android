import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kapt)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.threedollar.network"
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
            
            val properties = Properties()
            properties.load(project.rootProject.file("local.properties").inputStream())

            manifestPlaceholders["kakao_key"] = properties.getProperty("kakao_key_release", "")
            manifestPlaceholders["appName3dollar"] = "@string/app_name_3dollar"
            
            buildConfigField("String", "KAKAO_KEY", "\"${properties.getProperty("kakao_key_release")}\"")
            buildConfigField("String", "BASE_URL", "\"${properties.getProperty("release_url")}\"")
            buildConfigField("String", "MAP_KEY", "\"${properties.getProperty("kakao_map_key_release")}\"")
            buildConfigField("String", "APPLICATION_ID", "\"${properties.getProperty("application_id_release")}\"")
            buildConfigField("String", "VERSION_NAME", "\"${project.findProperty("version_name")}\"")
        }
        debug {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            
            val properties = Properties()
            properties.load(project.rootProject.file("local.properties").inputStream())

            manifestPlaceholders["kakao_key"] = properties.getProperty("kakao_key_dev", "")
            manifestPlaceholders["appName3dollar"] = "@string/app_name_3dollar_debug"
            
            buildConfigField("String", "KAKAO_KEY", "\"${properties.getProperty("kakao_key_dev")}\"")
            buildConfigField("String", "BASE_URL", "\"${properties.getProperty("develop_url")}\"")
            buildConfigField("String", "MAP_KEY", "\"${properties.getProperty("kakao_map_key")}\"")
            buildConfigField("String", "APPLICATION_ID", "\"${properties.getProperty("application_id_dev")}\"")
            buildConfigField("String", "VERSION_NAME", "\"${project.findProperty("version_name")}\"")
        }
    }
    
    buildFeatures {
        buildConfig = true
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

    implementation(libs.bundles.androidx.ui)
    implementation(project(":common"))
    testImplementation(libs.bundles.testing)

    implementation(libs.bundles.retrofit)

    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    implementation(libs.kakao.login)
}
