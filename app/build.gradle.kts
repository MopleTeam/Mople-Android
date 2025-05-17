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
            isMinifyEnabled = false
            isShrinkResources = false
            isDebuggable = true
            manifestPlaceholders["crashlyticsCollectionEnabled"] = false
        }

        release {
            isDebuggable = false
            isMinifyEnabled = false
            isShrinkResources = false
            signingConfig = signingConfigs.getByName("release")
            manifestPlaceholders["crashlyticsCollectionEnabled"] = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    lint {
        disable.add("Instantiatable")
    }
}

dependencies {
    implementation(projects.core.designsystem)
    implementation(projects.core.model)
    implementation(projects.core.dataModel)
    implementation(projects.core.data)
    implementation(projects.core.domain)
    implementation(projects.core.common)
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