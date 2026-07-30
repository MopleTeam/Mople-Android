import com.moim.convention.Plugins
import com.moim.convention.configureKotlinJvm
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

internal class JvmLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = Plugins.KOTLIN_JVM)
            configureKotlinJvm()
        }
    }
}
