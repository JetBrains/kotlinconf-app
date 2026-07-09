import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsEnvSpec
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsPlugin
import org.jetbrains.kotlin.gradle.targets.wasm.nodejs.WasmNodeJsEnvSpec
import org.jetbrains.kotlin.gradle.targets.wasm.nodejs.WasmNodeJsPlugin

plugins {
    alias(libs.plugins.aboutLibraries) apply false
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidMultiplatformLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.kotlinParcelize) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinPowerAssert) apply false
    alias(libs.plugins.kotlinSerialization) apply false
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.jib) apply false
    alias(libs.plugins.googleServices) apply false
    alias(libs.plugins.metro) apply false
}

// Pin Node.js to the last 22.x LTS. Node 24/25 prebuilt linux-x64 binaries
// require GLIBC newer than what some CI images provide (see GLIBC_2.27 errors).
val pinnedNodeVersion = "22.22.0"
allprojects {
    plugins.withType<NodeJsPlugin> {
        extensions.configure<NodeJsEnvSpec> { version.set(pinnedNodeVersion) }
    }
    plugins.withType<WasmNodeJsPlugin> {
        extensions.configure<WasmNodeJsEnvSpec> { version.set(pinnedNodeVersion) }
    }

    val kotlinLanguageVersionOverride = providers.gradleProperty("kotlin_language_version")
        .map(org.jetbrains.kotlin.gradle.dsl.KotlinVersion::fromVersion)
        .orNull
    val kotlinApiVersionOverride = providers.gradleProperty("kotlin_api_version")
        .map(org.jetbrains.kotlin.gradle.dsl.KotlinVersion::fromVersion)
        .orNull
    val kotlinAdditionalCliOptions = providers.gradleProperty("kotlin_additional_cli_options")
        .map { it.split(" ").filter { option -> option.isNotEmpty() } }
        .orNull

    plugins.withId("org.jetbrains.kotlin.multiplatform") {
        extensions.configure(KotlinMultiplatformExtension::class.java) {
            compilerOptions {
                kotlinLanguageVersionOverride?.let {
                    languageVersion.set(it)
                    logger.info("<KUP> ${project.path} : set LV to $it")
                }

                kotlinApiVersionOverride?.let {
                    apiVersion.set(it)
                    logger.info("<KUP> ${project.path} : set APIV to $it")
                }

                kotlinAdditionalCliOptions?.let {
                    freeCompilerArgs.addAll(it)
                    logger.info(
                        "<KUP> ${project.path} : added ${
                            kotlinAdditionalCliOptions.joinToString(
                                " "
                            )
                        }"
                    )
                }

                freeCompilerArgs.add("-Xreport-all-warnings")
                logger.info("<KUP> ${project.path} : added -Xreport-all-warnings")
                // output kotlin.git-searchable names of reported diagnostics
                freeCompilerArgs.add("-Xrender-internal-diagnostic-names")
                logger.info("<KUP> ${project.path} : added -Xrender-internal-diagnostic-names")

                allWarningsAsErrors.set(false)
            }
        }
    }

}

apply(from = "gradle/releases.gradle.kts")
