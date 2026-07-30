import com.android.build.api.dsl.ApplicationExtension
import com.moim.convention.Plugins
import com.moim.convention.configureAndroidCompose
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.getByType

internal class AndroidApplicationComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = Plugins.ANDROID_APPLICATION)
            apply(plugin = Plugins.KOTLIN_COMPOSE)

            val extension = extensions.getByType<ApplicationExtension>()
            configureAndroidCompose(extension)
        }
    }
}
