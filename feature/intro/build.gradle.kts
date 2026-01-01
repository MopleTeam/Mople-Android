import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.moim.android.feature)
}

android {
    val localProperties = gradleLocalProperties(rootDir, providers)

    namespace = "com.moim.feature.intro"

    buildTypes {
        debug {
            // Kakao
            resValue("string", "KAKAO_API_KEY", localProperties["KAKAO_API_KEY"].toString())
            resValue("string", "KAKAO_APP_SCHEME", "kakao".plus(localProperties["KAKAO_API_KEY"].toString()))
        }

        release {
            // Kakao
            resValue("string", "KAKAO_API_KEY", localProperties["KAKAO_API_KEY"].toString())
            resValue("string", "KAKAO_APP_SCHEME", "kakao".plus(localProperties["KAKAO_API_KEY"].toString()))
        }
    }
}

dependencies {
    implementation(libs.kakao.login)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
}