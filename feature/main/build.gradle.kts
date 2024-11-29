plugins {
    alias(libs.plugins.moim.android.feature)
}

android {
    namespace = "com.moim.feature.main"
}

dependencies {
    implementation(projects.feature.home)
    implementation(projects.feature.meeting)
    implementation(projects.feature.meetingWrite)
    implementation(projects.feature.meetingDetail)
    implementation(projects.feature.meetingSetting)
    implementation(projects.feature.planWrite)
    implementation(projects.feature.calendar)
    implementation(projects.feature.profile)
    implementation(projects.feature.profileUpdate)
}