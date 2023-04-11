pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

val MAPBOX_DOWNLOADS_TOKEN: String? by settings

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()

        maven(url = "https://api.mapbox.com/downloads/v2/releases/maven") {
            authentication {
                val basic by creating(BasicAuthentication::class)
            }

            credentials {
                username = "mapbox"
                password = MAPBOX_DOWNLOADS_TOKEN
            }
        }
    }
}

rootProject.name = "KotlinConf_2023"
include(":androidApp")
include(":shared")
include(":backend")
