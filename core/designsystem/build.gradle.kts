plugins {
    alias(libs.plugins.moim.android.library)
    alias(libs.plugins.moim.android.library.compose)
}

android {
    namespace = "com.moim.core.designsystem"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.windowSizeClass)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.ui.util)
    implementation(libs.lottie.compose)
    implementation(libs.bundles.coil)
    implementation(libs.timber)

    api(libs.androidx.compose.ui.tooling)
}