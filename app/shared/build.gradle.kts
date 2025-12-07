@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import com.mikepenz.aboutlibraries.plugin.DuplicateMode
import com.mikepenz.aboutlibraries.plugin.DuplicateRule
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.aboutLibraries)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.metro)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    androidLibrary {
        namespace = "org.jetbrains.kotlinconf"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        compilerOptions.jvmTarget = JvmTarget.JVM_11
        androidResources.enable = true
    }

    jvm()

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs { browser() }
    js { browser() }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain.dependencies {
            api(projects.core)
            api(projects.app.uiComponents)

            api(libs.compose.runtime)
            api(libs.compose.foundation)
            api(libs.compose.animation)
            api(libs.compose.components.resources)
            implementation(libs.compose.ui.tooling.preview)

            implementation(libs.metrox.viewmodel.compose)

            api(libs.ktor.client.logging)
            api(libs.ktor.serialization.kotlinx.json)
            api(libs.ktor.client.content.negotiation)
            api(libs.ktor.utils)

            implementation(libs.kotlinx.datetime)

            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.androidx.navigation3.ui)
            implementation(libs.androidx.lifecycle.viewmodel.navigation3)
            implementation(libs.ktor.client.core)

            implementation(libs.aboutlibraries.core)

            // Multiplatform Settings
            implementation(libs.settings)
            implementation(libs.settings.serialization)
            implementation(libs.settings.observable)
            implementation(libs.settings.coroutines)

            api(libs.kmpnotifier)

            implementation(libs.doistx.normalize)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.settings.test)
            implementation(libs.kotlinx.coroutines.test)
        }

        val nonAndroidMain by creating {
            dependsOn(commonMain.get())
        }
        configure(listOf(iosMain, jvmMain, webMain)) {
            get().dependsOn(nonAndroidMain)
        }

        androidMain.dependencies {
            implementation(libs.android.svg)
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.preference)
            implementation(libs.ktor.client.okhttp)
        }

        iosMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.ktor.client.darwin)
        }

        jvmMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            implementation(libs.android.svg)
            implementation(libs.slf4j.nop)
        }

        webMain.dependencies {
            implementation(libs.ktor.client.js)
            implementation(npm("@js-joda/timezone", "2.22.0"))
        }
    }

    jvmToolchain(21)
}

// Android-based preview support
dependencies {
    "androidRuntimeClasspath"(libs.compose.ui.tooling)
}

compose.resources {
    packageOfResClass = "org.jetbrains.kotlinconf.generated.resources"
}

aboutLibraries {
    library.duplicationMode = DuplicateMode.MERGE
    library.duplicationRule = DuplicateRule.SIMPLE
    export.outputFile = File("src/commonMain/composeResources/files")
}
