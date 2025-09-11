plugins {
    alias(libs.plugins.moim.android.library)
    alias(libs.plugins.moim.android.hilt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.moim.core.data"
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.dataLocal)
    implementation(projects.core.dataRemote)

    // Kotlin
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.coroutines)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.okhttp.logging)

    // image compress
    implementation(libs.compressor)

    // Log tracker
    implementation(libs.timber)
}