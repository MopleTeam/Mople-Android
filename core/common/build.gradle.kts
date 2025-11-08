plugins {
    alias(libs.plugins.moim.jvm.library)
    alias(libs.plugins.moim.jvm.hilt)
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    // Kotlin
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.coroutines)
    implementation(libs.kotlinx.serialization.json)

    compileOnly(libs.compose.stable.marker)
}