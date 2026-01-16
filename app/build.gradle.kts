import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kapt)
    alias(libs.plugins.compose.compiler)
}

apply(from = "../common.gradle")

android {
    namespace = "com.zion830.threedollars"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()

        applicationId = "com.zion830.threedollars"
        versionCode = 114
        versionName = project.findProperty("version_name") as String
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").inputStream())

        resValue("string", "admob_id", properties.getProperty("admob_id", ""))
        buildConfigField("String", "NMF_CLIENT_ID", "\"${properties.getProperty("nmf_client_id")}\"")
        
        manifestPlaceholders["google_map_key"] = properties.getProperty("google_map_key", "")
        manifestPlaceholders["nmf_client_id"] = properties.getProperty("nmf_client_id", "")
        manifestPlaceholders["admob_id"] = properties.getProperty("admob_id", "")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
            
            val properties = Properties()
            properties.load(project.rootProject.file("local.properties").inputStream())
            
            manifestPlaceholders["kakao_key"] = properties.getProperty("kakao_key_release", "")
            manifestPlaceholders["appName3dollar"] = "@string/app_name_3dollar"
            
            buildConfigField("String", "KAKAO_KEY", "\"${properties.getProperty("kakao_key_release")}\"")
            buildConfigField("String", "BASE_URL", "\"${properties.getProperty("release_url")}\"")
            buildConfigField("String", "MAP_KEY", "\"${properties.getProperty("kakao_map_key_release")}\"")
            buildConfigField("String", "DEEP_LINK", "\"${properties.getProperty("deep_link_release")}\"")
        }
        getByName("debug") {
            applicationIdSuffix = ".dev"
            isDebuggable = true
            
            val properties = Properties()
            properties.load(project.rootProject.file("local.properties").inputStream())

            manifestPlaceholders["kakao_key"] = properties.getProperty("kakao_key_dev", "")
            manifestPlaceholders["appName3dollar"] = "@string/app_name_3dollar_debug"
            
            buildConfigField("String", "KAKAO_KEY", "\"${properties.getProperty("kakao_key_dev")}\"")
            buildConfigField("String", "BASE_URL", "\"${properties.getProperty("develop_url")}\"")
            buildConfigField("String", "MAP_KEY", "\"${properties.getProperty("kakao_map_key")}\"")
            buildConfigField("String", "DEEP_LINK", "\"${properties.getProperty("deep_link")}\"")
        }
    }
    
    packaging {
        resources {
            excludes += "META-INF/common_release.kotlin_module"
        }
    }
    
    buildFeatures {
        viewBinding = true
        buildConfig = true
        compose = true
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
    implementation(project(":common"))
    implementation(project(":core:network"))
    implementation(project(":core:common"))
    implementation(project(":core:ui"))
    implementation(project(":core:abtest"))
    implementation(project(":domain"))
    implementation(project(":data"))
    implementation(project(":core:designsystem"))

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.config)
    implementation(libs.firebase.database)
    implementation(libs.firebase.inappmessaging.display.ktx)
    implementation(libs.bundles.androidx.navigation)
    implementation(libs.androidx.lifecycle.extensions)
    implementation(libs.androidx.vectordrawable)
    implementation(libs.flexbox)
    implementation(libs.androidx.webkit)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)
    implementation(libs.bundles.coil.compose)
    implementation(libs.firebase.messaging)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
