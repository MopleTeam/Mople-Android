package com.moim.convention

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension

internal fun Project.configureAndroidCompose(commonExtension: CommonExtension) {
    commonExtension.apply {
        buildFeatures.apply {
            compose = true
        }

        dependencies {
            implementation(platform(libs.androidx.compose.bom))
            implementation(libs.androidx.compose.runtime)
        }
    }

    extensions.configure<ComposeCompilerGradlePluginExtension> {
        @Suppress("UnstableApiUsage")
        stabilityConfigurationFiles
            .add(isolated.rootProject.projectDirectory.file("compose-stability.conf"))
    }
}
