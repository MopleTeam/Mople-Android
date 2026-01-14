import com.android.build.api.dsl.ApplicationExtension
import com.moim.convention.Plugins
import com.moim.convention.configureAndroidCompose
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

internal class AndroidApplicationComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(Plugins.ANDROID_APPLICATION)
                apply(Plugins.KOTLIN_COMPOSE)
            }

            val extension = extensions.getByType<ApplicationExtension>()
            configureAndroidCompose(extension)
        }
    }
}
