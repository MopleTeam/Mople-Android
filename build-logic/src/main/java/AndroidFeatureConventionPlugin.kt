import com.android.build.api.dsl.LibraryExtension
import com.moim.convention.configureGradleManagedDevices
import com.moim.convention.implementation
import com.moim.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

internal class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "moim.android.library")
            apply(plugin = "moim.android.library.compose")
            apply(plugin = "moim.hilt")

            extensions.configure<LibraryExtension> {
                testOptions.animationsDisabled = true
                configureGradleManagedDevices(this)
            }

            extensions.configure<LibraryExtension> {
                configureGradleManagedDevices(this)
            }

            dependencies {
                implementation(project(":core:data"))
                implementation(project(":core:common"))
                implementation(project(":core:domain"))
                implementation(project(":core:designsystem"))
                implementation(project(":core:ui"))
                implementation(project(":core:analytics"))
                implementation(project(":core:crashreport"))

                // AndroidX
                implementation(libs.androidx.core.ktx)
                implementation(libs.androidx.activity.compose)
                implementation(libs.bundles.androidx.lifecycle)
                // AndroidX Navigation
                implementation(libs.androidx.navigation3.runtime)
                implementation(libs.androidx.navigation3.ui)

                // AndroidX Compose material3
                implementation(libs.androidx.compose.material3)
                implementation(libs.androidx.compose.material3.windowSizeClass)

                // AndroidX Hilt
                implementation(libs.androidx.hilt.common)
                implementation(libs.androidx.hilt.navigation.compose)

                // Kotlin
                implementation(libs.kotlin.stdlib)
                implementation(libs.kotlinx.coroutines)

                // ETC
                implementation(libs.bundles.coil)
                implementation(libs.lottie.compose)
                implementation(libs.timber)
            }
        }
    }
}
