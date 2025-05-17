plugins {
    alias(libs.plugins.moim.android.feature)
}

android {
    namespace = "com.moim.feature.imageviewer"
}

dependencies {
    implementation(libs.image.zoomable)
}