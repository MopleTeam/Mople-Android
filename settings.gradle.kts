pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://devrepo.kakao.com/nexus/content/groups/public/")
        maven("https://repository.map.naver.com/archive/maven")
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
rootProject.name = "MoimTable"

include(":app")

include(":core:designsystem")
include(":core:model")
include(":core:data")
include(":core:common")

include(":feature:intro")
include(":feature:main")
include(":feature:home")
include(":feature:meeting")
include(":feature:meeting-detail")
include(":feature:meeting-write")
include(":feature:calendar")
include(":feature:profile")
include(":feature:profile-update")
include(":feature:plan-write")
