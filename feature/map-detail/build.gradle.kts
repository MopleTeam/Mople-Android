plugins {
    alias(libs.plugins.moim.android.feature)
}

android {
    namespace = "com.moim.feature.mapdetail"
}

dependencies {
    implementation(libs.android.play.services.location)
    implementation(libs.bundles.naver.map.compose)
}