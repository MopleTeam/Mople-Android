plugins {
    alias(libs.plugins.moim.android.library)
    alias(libs.plugins.moim.android.library.compose)
    alias(libs.plugins.moim.hilt)
}

android {
    namespace = "com.moim.core.messaging"
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.data)
    implementation(projects.core.designsystem)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messageing)

    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines)
    implementation(libs.timber)
}
