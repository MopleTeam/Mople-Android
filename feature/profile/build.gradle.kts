plugins {
    alias(libs.plugins.moim.android.feature)
}

android {
    namespace = "com.moim.feature.profile"

    defaultConfig {
        buildConfigField("String","VERSION_NAME","\"${MoimConfig.VERSION_NAME}\"")
    }
}