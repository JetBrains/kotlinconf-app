@file:Suppress("UnstableApiUsage")

rootProject.name = "KotlinConfApp"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        // TODO remove dev repo later
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")

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
    }
}

dependencyResolutionManagement {
    repositories {
        // TODO remove dev repo later
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")

        google {
            mavenContent {
                includeGroupAndSubgroups("android")
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include(":androidApp")
include(":ui-components")
include(":ui-components-gallery")
include(":shared")
//include(":backend")
