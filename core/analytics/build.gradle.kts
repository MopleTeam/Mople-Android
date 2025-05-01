plugins {
    alias(libs.plugins.moim.android.library)
    alias(libs.plugins.moim.android.library.compose)
    alias(libs.plugins.moim.android.hilt)
}

android {
    namespace = "com.moim.core.analytics"
}

dependencies {
    implementation(libs.androidx.compose.runtime)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.timber)
}