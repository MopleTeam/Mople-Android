package com.moim.convention

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault
import javax.inject.Inject

@DisableCachingByDefault(because = "Scaffold generation is cheap and not worth caching")
abstract class GenerateFeatureModuleTask @Inject constructor() : DefaultTask() {
    @get:Input
    abstract val featureName: Property<String>

    @get:Input
    abstract val basePackageName: Property<String>

    @get:InputDirectory
    abstract val templateRootDirectory: DirectoryProperty

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @get:InputFile
    abstract val settingsGradleFile: RegularFileProperty

    @get:Inject
    abstract val fileSystem: FileSystemOperations

    init {
        group = "scaffold"
        description = "Create feature module from template"
    }

    @TaskAction
    fun generateFeatureModule() {
        val featureNameValue = featureName.orNull ?: error("-PfeatureName=... 필요")
        val packageName = "${basePackageName.get()}.${featureNameValue.replace("-", "").lowercase()}"
        val outputDirectoryFile = outputDirectory.get().asFile
        require(outputDirectoryFile.list().isNullOrEmpty()) { "이미 존재: feature:$featureNameValue" }

        // 템플릿 복사 + 파일명 치환 + 내용 치환
        fileSystem.copy {
            from(templateRootDirectory.get())
            into(outputDirectoryFile)
            include("**/*")
            includeEmptyDirs = true
            rename { originalName ->
                val upperCamel = featureNameValue.toUpperCamel()
                val lowerCamel = featureNameValue.toLowerCamel()
                originalName
                    .removeSuffix(".tmpl")
                    .replace("__MODULE_NAME_UPPER_CAMEL_CASE__", upperCamel)
                    .replace("__MODULE_NAME_LOWER_CAMEL_CASE__", lowerCamel)
            }
            filter { line: String ->
                line
                    .replace("__MODULE_NAME_LOWER_CAMEL_CASE__", featureNameValue.toLowerCamel())
                    .replace("__MODULE_NAME_UPPER_CAMEL_CASE__", featureNameValue.toUpperCamel())
                    .replace("__MODULE_NAME_SNAKE_CASE__", featureNameValue.toSnakeCase())
                    .replace("__PACKAGE__", packageName)
            }
        }

        // 패키지 디렉터리 정리
        val srcMainJavaDirectory = outputDirectoryFile.resolve("src/main/java")
        val templatePackagePlaceholderDirectory = srcMainJavaDirectory.resolve("__PACKAGE_DIR__")
        val packageRelativePath = packageName.replace('.', '/')
        val targetPackageDirectory = srcMainJavaDirectory.resolve(packageRelativePath)
        targetPackageDirectory.mkdirs()
        templatePackagePlaceholderDirectory.listFiles()?.forEach { it.renameTo(targetPackageDirectory.resolve(it.name)) }
        templatePackagePlaceholderDirectory.deleteRecursively()

        // settings.gradle.kts include 추가
        val settingsFile = settingsGradleFile.asFile.get()
        val settingsText = settingsFile.readText()
        val newIncludeLine = """include(":feature:$featureNameValue")"""
        if (!settingsText.contains(newIncludeLine)) {
            val includeRegex =
                Regex("""^include\(":feature:[^"]*"\)\s*$""", RegexOption.MULTILINE)
            val lastInclude = includeRegex.findAll(settingsText).lastOrNull()
            val updatedText =
                if (lastInclude != null) {
                    val insertPosition = lastInclude.range.last + 1
                    buildString {
                        append(settingsText.substring(0, insertPosition))
                        append("$newIncludeLine\n")
                        append(settingsText.substring(insertPosition))
                    }
                } else {
                    settingsText.trimEnd() + "$newIncludeLine\n"
                }
            settingsFile.writeText(updatedText)
        }

        logger.lifecycle("생성 완료: :feature:$featureNameValue (package=$packageName)")
        logger.lifecycle("Sync 후 바로 빌드 가능")
    }

    private fun String.toUpperCamel(): String =
        split('-', '_', '.')
            .filter { it.isNotBlank() }
            .joinToString("") { it.replaceFirstChar(Char::titlecase) }

    private fun String.toLowerCamel(): String {
        val parts = split('-', '_', '.').filter { it.isNotBlank() }
        if (parts.isEmpty()) {
            return ""
        }
        val head = parts.first().replaceFirstChar(Char::lowercase)
        val tail = parts.drop(1).joinToString("") { it.replaceFirstChar(Char::titlecase) }
        return head + tail
    }

    private fun String.toSnakeCase(): String =
        replace(Regex("([a-z])([A-Z])"), "$1_$2")
            .split('-', '_', '.')
            .filter { it.isNotBlank() }
            .joinToString("_") { it.lowercase() }
}
