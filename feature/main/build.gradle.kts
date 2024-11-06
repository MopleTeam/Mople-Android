plugins {
    alias(libs.plugins.moim.android.feature)
}

android {
    namespace = "com.moim.feature.main"
}

dependencies {
    implementation(projects.feature.home)
    implementation(projects.feature.meeting)
    implementation(projects.feature.calendar)
    implementation(projects.feature.profile)
}