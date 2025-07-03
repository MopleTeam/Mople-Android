plugins {
    alias(libs.plugins.moim.android.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.moim.core.model"
}

dependencies {
    implementation(projects.core.dataModel)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.timber)
    compileOnly(libs.compose.stable.marker)
}