plugins {
    alias(libs.plugins.moim.android.feature)
}

android {
    namespace = "com.moim.feature.intro"
}

dependencies {
    implementation(libs.kakao.login)
}