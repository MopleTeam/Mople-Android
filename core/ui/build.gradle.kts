plugins {
    alias(libs.plugins.moim.android.library)
    alias(libs.plugins.moim.android.library.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.moim.android.hilt)
}

android {
    namespace = "com.moim.core.ui"
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.designsystem)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messageing)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.windowSizeClass)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.ui.util)
    implementation(libs.androidx.paging.common)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.lottie.compose)
    implementation(libs.bundles.coil)
    implementation(libs.timber)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    api(libs.androidx.compose.ui.tooling)
}