package com.moim.convention

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinBaseExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

internal fun Project.configureKotlinAndroid(commonExtension: CommonExtension) {
    commonExtension.apply {
        defaultConfig.apply {
            minSdk = MoimConfig.MIN_SDK
            compileSdk = MoimConfig.COMPILE_SDK
        }

        compileOptions.apply {
            sourceCompatibility = MoimConfig.javaCompileTarget
            targetCompatibility = MoimConfig.javaCompileTarget
            isCoreLibraryDesugaringEnabled = true
        }

        buildFeatures.apply {
            buildConfig = true
        }
    }

    configureKotlin<KotlinAndroidProjectExtension>()

    dependencies {
        coreLibraryDesugaring(libs.android.desugarJdkLibs)
    }
}

internal fun Project.configureKotlinJvm() {
    extensions.configure<JavaPluginExtension> {
        sourceCompatibility = MoimConfig.javaCompileTarget
        targetCompatibility = MoimConfig.javaCompileTarget
    }

    configureKotlin<KotlinJvmProjectExtension>()
}

private inline fun <reified T : KotlinBaseExtension> Project.configureKotlin() =
    configure<T> {
        // Treat all Kotlin warnings as errors (disabled by default)
        // Override by setting warningsAsErrors=true in your ~/.gradle/gradle.properties
        val warningsAsErrors =
            providers
                .gradleProperty("warningsAsErrors")
                .map {
                    it.toBoolean()
                }.orElse(false)
        when (this) {
            is KotlinAndroidProjectExtension -> compilerOptions
            is KotlinJvmProjectExtension -> compilerOptions
            else -> TODO("Unsupported project extension $this ${T::class}")
        }.apply {
            jvmTarget = JvmTarget.JVM_11
            allWarningsAsErrors = warningsAsErrors
            freeCompilerArgs.addAll(
                "-Xconsistent-data-class-copy-visibility",
                "-Xstring-concat=inline",
                "-opt-in=kotlin.RequiresOptIn",
                // Enable experimental coroutines APIs, including Flow
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-opt-in=kotlinx.coroutines.FlowPreview",
                // Enable experimental compose APIs
                "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
                "-opt-in=androidx.compose.animation.ExperimentalSharedTransitionApi",
                "-opt-in=androidx.lifecycle.compose.ExperimentalLifecycleComposeApi",
            )
        }
    }
