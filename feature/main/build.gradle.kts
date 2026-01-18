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
    implementation(projects.feature.reviewWrite)
    implementation(projects.feature.planWrite)
    implementation(projects.feature.planDetail)
    implementation(projects.feature.commentDetail)
    implementation(projects.feature.participantList)
    implementation(projects.feature.calendar)
    implementation(projects.feature.mapDetail)
    implementation(projects.feature.profile)
    implementation(projects.feature.profileUpdate)
    implementation(projects.feature.alarm)
    implementation(projects.feature.alarmSetting)
    implementation(projects.feature.webview)
    implementation(projects.feature.imageViewer)
    implementation(projects.feature.themeSetting)

    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
}