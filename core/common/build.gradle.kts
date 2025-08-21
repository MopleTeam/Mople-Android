plugins {
    alias(libs.plugins.moim.android.library)
    alias(libs.plugins.moim.android.library.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.moim.android.hilt)
}

android {
    namespace = "com.moim.core.common"
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.designsystem)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.windowSizeClass)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.ui.util)
    implementation(libs.androidx.paging.common)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messageing)

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.lottie.compose)
    implementation(libs.timber)
}