import com.moim.convention.Plugins
import com.moim.convention.implementation
import com.moim.convention.ksp
import com.moim.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

internal class HiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = Plugins.KSP)

            dependencies {
                ksp(libs.hilt.android.compiler)
            }

            pluginManager.withPlugin(Plugins.KOTLIN_JVM) {
                dependencies {
                    implementation(libs.hilt.core)
                }
            }

            pluginManager.withPlugin(Plugins.ANDROID_BASE) {
                apply(plugin = Plugins.ANDROID_HILT)
                dependencies {
                    implementation(libs.hilt.android)
                }
            }
        }
    }
}
