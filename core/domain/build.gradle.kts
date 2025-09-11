plugins {
    alias(libs.plugins.moim.android.library)
    alias(libs.plugins.moim.android.hilt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.moim.core.domain"
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.data)

    // Kotlin
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.coroutines)
    implementation(libs.kotlinx.serialization.json)

    // paging
    implementation(libs.androidx.paging.common)

    // Log tracker
    implementation(libs.timber)
}