plugins {
    alias(libs.plugins.moim.android.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.moim.core.model"
}

dependencies {
    implementation(projects.core.data)

    implementation(libs.kotlinx.serialization.json)
    compileOnly(libs.compose.stable.marker)
}