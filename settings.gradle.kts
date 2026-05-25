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

include(":core:ui")
include(":core:designsystem")
include(":core:common")
include(":core:analytics")
include(":core:crashreport")
include(":core:messaging")
include(":core:domain")
include(":core:data")
include(":core:data-local")
include(":core:data-remote")

include(":feature:alarm")
include(":feature:alarm-setting")
include(":feature:calendar")
include(":feature:comment-detail")
include(":feature:home")
include(":feature:image-viewer")
include(":feature:intro")
include(":feature:main")
include(":feature:map-detail")
include(":feature:meeting")
include(":feature:meeting-detail")
include(":feature:meeting-notice")
include(":feature:meeting-setting")
include(":feature:meeting-write")
include(":feature:participant-list")
include(":feature:participant-list-for-leader-change")
include(":feature:plan-detail")
include(":feature:plan-write")
include(":feature:profile")
include(":feature:profile-update")
include(":feature:review-write")
include(":feature:theme-setting")
include(":feature:user-withdrawal-for-leader-change")
include(":feature:webview")
