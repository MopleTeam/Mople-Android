import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.moim.android.library)
    alias(libs.plugins.moim.android.hilt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
}

android {
    val localProperties = gradleLocalProperties(rootDir, providers)

    namespace = "com.moim.core.remote"

    defaultConfig {
        buildConfigField("String", "VERSION_NAME", "\"${MoimConfig.VERSION_NAME}\"")
    }

    buildTypes {
        debug {
            buildConfigField("String", "API_URL", localProperties["DEV_API_URL"].toString())
        }

        release {
            buildConfigField("String", "API_URL", localProperties["PROD_API_URL"].toString())
        }
    }
}

dependencies {
    implementation(projects.core.common)

    // Kotlin
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.coroutines)
    implementation(libs.kotlinx.serialization.json)

    // Retrofit
    implementation(libs.bundles.retrofit)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messageing)

    // Log tracker
    implementation(libs.timber)
}