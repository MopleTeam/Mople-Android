plugins {
    alias(libs.plugins.moim.android.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.moim.core.model"
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.timber)
    compileOnly(libs.compose.stable.marker)
}