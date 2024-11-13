plugins {
    alias(libs.plugins.moim.android.feature)
}

android {
    namespace = "com.moim.feature.calendar"
}

dependencies {
    implementation(libs.calendar.compose)
}