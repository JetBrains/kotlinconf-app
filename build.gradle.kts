plugins {
    alias(libs.plugins.aboutLibraries) apply false
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.kotlinParcelize) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinSerialization) apply false
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.composeHotReload) apply false
    alias(libs.plugins.jib) apply false
    alias(libs.plugins.googleServices) apply false
}

apply(from = "gradle/releases.gradle.kts")
