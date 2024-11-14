plugins {
    alias(libs.plugins.moim.android.feature)
}

android {
    namespace = "com.moim.feature.planwrite"
}

dependencies {
    implementation(libs.bundles.naver.map.compose)
}