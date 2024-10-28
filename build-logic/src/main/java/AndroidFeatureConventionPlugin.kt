import com.android.build.gradle.LibraryExtension
import com.moim.convention.configureGradleManagedDevices
import com.moim.convention.implementation
import com.moim.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

internal class AndroidFeatureConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply("moim.android.library")
                apply("moim.android.library.compose")
                apply("moim.android.hilt")
            }

            extensions.configure<LibraryExtension> {
                configureGradleManagedDevices(this)
            }

            dependencies {
                implementation(project(":core:data"))
                implementation(project(":core:common"))
                implementation(project(":core:designsystem"))
                implementation(project(":core:model"))

                // AndroidX
                implementation(libs.androidx.core.ktx)
                implementation(libs.androidx.activity.compose)
                implementation(libs.bundles.androidx.lifecycle)
                // AndroidX Paging
                implementation(libs.bundles.androidx.paging)
                // AndroidX Navigation
                implementation(libs.androidx.navigation.compose)

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