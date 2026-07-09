@file:Suppress("UnstableApiUsage")

rootProject.name = "KotlinConfApp"

pluginManagement {
    val kotlin_repo_url: String? by settings

    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("android")
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")

            }
        }
        gradlePluginPortal()
        mavenCentral()

        kotlin_repo_url?.also { maven(it) }
    }
}

dependencyResolutionManagement {
    val kotlin_version: String? by settings
    val kotlin_repo_url: String? by settings

    versionCatalogs {
        create("libs") {
            kotlin_version?.let {
                version("kotlin", it)
            }
        }
    }

    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("android")
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()

        kotlin_repo_url?.also { maven(it) }
    }
}

include(":core")

include(":backend")

include(":app:shared")
include(":app:ui-components")
include(":app:androidApp")
include(":app:desktopApp")
include(":app:webApp")
include(":app:adminApp")
