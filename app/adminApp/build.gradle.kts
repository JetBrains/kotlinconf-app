// ABOUTME: Build config for the server-hosted admin panel SPA.
// ABOUTME: Kotlin/JS + Compose HTML (DOM); bundled to admin.js and served by the backend under /admin.

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    js(IR) {
        binaries.executable()
        browser {
            commonWebpackConfig {
                outputFileName = "admin.js"
            }
        }
    }

    sourceSets {
        jsMain.dependencies {
            implementation(libs.compose.html.core)
            implementation(libs.compose.runtime)

            implementation(projects.core)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.js)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
        }
    }
}

// Expose the production browser bundle so the backend can package and serve it as static resources.
val adminDistribution: Configuration by configurations.creating {
    isCanBeConsumed = true
    isCanBeResolved = false
}

artifacts {
    add(adminDistribution.name, tasks.named("jsBrowserDistribution"))
}
