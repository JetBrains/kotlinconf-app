@file:Suppress("UnstableApiUsage")

rootProject.name = "KotlinConfApp"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
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
        maven {
            url = uri("https://androidx.dev/snapshots/builds/14884013/artifacts/repository")
        }
    }
}

dependencyResolutionManagement {
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
        maven {
            url = uri("https://androidx.dev/snapshots/builds/14884013/artifacts/repository")
        }
    }
}

include(":core")

include(":backend")

include(":app:shared")
include(":app:ui-components")
include(":app:androidApp")
//include(":app:desktopApp")
//include(":app:webApp")
