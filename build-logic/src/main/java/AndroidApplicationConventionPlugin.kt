import com.android.build.api.dsl.ApplicationExtension
import com.moim.convention.GenerateFeatureModuleTask
import com.moim.convention.Plugins
import com.moim.convention.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

internal class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = Plugins.ANDROID_APPLICATION)

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)

                defaultConfig.apply {
                    applicationId = MoimConfig.APPLICATION_ID
                    targetSdk = MoimConfig.TARGET_SDK
                    versionCode = MoimConfig.VERSION_CODE
                    versionName = MoimConfig.VERSION_NAME
                }
            }

            registerGenerateFeatureModuleTask(rootProject)
        }
    }

    private fun registerGenerateFeatureModuleTask(rootProject: Project) {
        if (rootProject.tasks.findByName("generateFeatureModule") != null) return

        val featureNameProvider = rootProject.providers.gradleProperty("featureName")
        val templateRootDirectory = rootProject.layout.projectDirectory.dir("templates/feature-module")
        val settingsGradleFile = rootProject.layout.projectDirectory.file("settings.gradle.kts")
        val featuresRootDirectory = rootProject.layout.projectDirectory.dir("feature")

        rootProject.tasks.register("generateFeatureModule", GenerateFeatureModuleTask::class.java) {
            featureName.set(featureNameProvider)
            basePackageName.set("com.moim.feature")
            this.templateRootDirectory.set(templateRootDirectory)
            outputDirectory.set(featureNameProvider.map { name -> featuresRootDirectory.dir(name) })
            this.settingsGradleFile.set(settingsGradleFile)
        }
    }
}
