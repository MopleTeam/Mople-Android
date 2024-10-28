import com.android.build.api.dsl.ApplicationExtension
import com.moim.convention.Plugins
import com.moim.convention.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

internal class AndroidApplicationConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(Plugins.ANDROID_APPLICATION)
                apply(Plugins.KOTLIN_ANDROID)
            }

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)

                defaultConfig.apply {
                    applicationId = MoimConfig.APPLICATION_ID
                    targetSdk = MoimConfig.TARGET_SDK
                    versionCode = MoimConfig.VERSION_CODE
                    versionName = MoimConfig.VERSION_NAME
                }
            }
        }
    }
}