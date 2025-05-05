plugins {
    alias(libs.plugins.moim.android.feature)
}

android {
    namespace = "com.moim.feature.plandetail"
}

dependencies {
    implementation(libs.bundles.naver.map.compose)
    implementation(libs.image.zoomable)
}