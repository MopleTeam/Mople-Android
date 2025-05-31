plugins {
    alias(libs.plugins.moim.android.library)
    alias(libs.plugins.moim.android.hilt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.moim.core.datastore"
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.dataModel)

    // AndroidX DataStore
    implementation(libs.androidx.dataStore)

    // Kotlin
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.coroutines)
    implementation(libs.kotlinx.serialization.json)

    // Log tracker
    implementation(libs.timber)
}