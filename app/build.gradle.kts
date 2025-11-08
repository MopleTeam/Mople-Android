import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.moim.android.application)
    alias(libs.plugins.moim.android.application.compose)
    alias(libs.plugins.moim.android.hilt)
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    val localProperties = gradleLocalProperties(rootDir, providers)

    namespace = "com.moim.moimtable"

    signingConfigs {
        create("release") {
            storeFile = file("moimkey.keystore")
            storePassword = localProperties["STORE_PASSWORD"].toString()
            keyAlias = localProperties["KEY_ALIAS"].toString()
            keyPassword = localProperties["KEY_PASSWORD"].toString()
        }
    }

    defaultConfig {
        manifestPlaceholders["NAVER_MAP_CLIENT_ID"] = localProperties["NAVER_MAP_CLIENT_ID"].toString()
    }

    buildTypes {
        debug {
            isDebuggable = true
            isMinifyEnabled = false
            isShrinkResources = false
            manifestPlaceholders["crashlyticsCollectionEnabled"] = false
            manifestPlaceholders["icon_app_launcher"] = "@mipmap/ic_launcher_dev"
            manifestPlaceholders["icon_app_launcher_round"] = "@mipmap/ic_launcher_dev_round"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
        }

        release {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            manifestPlaceholders["crashlyticsCollectionEnabled"] = true
            manifestPlaceholders["icon_app_launcher"] = "@mipmap/ic_launcher"
            manifestPlaceholders["icon_app_launcher_round"] = "@mipmap/ic_launcher_round"
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    flavorDimensions += "environment"

    productFlavors {
        create("dev") {
            dimension = "environment"
        }
        create("prod") {
            dimension = "environment"
        }
    }

    lint {
        disable.add("Instantiatable")
    }
}

dependencies {
    implementation(projects.core.designsystem)
    implementation(projects.core.data)
    implementation(projects.core.domain)
    implementation(projects.core.common)
    implementation(projects.core.ui)
    implementation(projects.feature.main)
    implementation(projects.feature.intro)

    // AndroidX
    implementation(libs.androidx.startup)

    // Kotlin
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.coroutines)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)

    // Image Loader
    implementation(libs.bundles.coil)

    // Log tracker
    implementation(libs.timber)

    implementation(libs.kakao.login)
}