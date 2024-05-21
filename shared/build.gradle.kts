@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    id("kotlin-parcelize")
}

kotlin {
    androidTarget()
    jvm()

    wasmJs {
        binaries.executable()
        browser {
            commonWebpackConfig {
                outputFileName = "kotlin-app-wasm-js.js"
            }
        }
    }

    js {
        binaries.executable()
        browser {
            commonWebpackConfig {
                outputFileName = "kotlin-app-js.js"
            }
        }
    }

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

    sourceSets {
          val commonMain by getting {
            dependencies {
                compileOnly(compose.runtime)

                api(libs.components.ui.tooling.preview)
                api(compose.components.resources)

                api(libs.ktor.client.logging)
                api(libs.ktor.serialization.kotlinx.json)
                api(libs.ktor.client.content.negotiation)
                api(libs.ktor.utils)

                implementation(libs.kotlinx.datetime)
                implementation(libs.material3)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val mobileMain by creating {
            dependsOn(commonMain)
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.animation)
                api(compose.material)
                api(compose.components.resources)

                implementation(libs.androidx.navigation.compose)
                implementation(libs.multiplatform.markdown.renderer.m3)
                implementation(libs.ktor.client.core)

                api(libs.image.loader)
            }
        }

        val mobileTest by creating {
            dependsOn(mobileMain)
            dependsOn(commonTest)
        }

        val androidMain by getting {
            dependsOn(mobileMain)

            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.ui)
                implementation(compose.components.resources)

                implementation(libs.android.svg)
                implementation(libs.androidx.core.ktx)
                implementation(libs.androidx.work.runtime)
                implementation(libs.androidx.preference)
                implementation(libs.compose.ui.tooling.preview)

                implementation(libs.ktor.client.cio)
            }

            resources.srcDirs("src/commonMain/resources", "src/mobileMain/resources")
        }

        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting

        val iosMain by creating {
            dependsOn(mobileMain)

            dependencies {
                implementation(libs.ktor.client.darwin)
            }

            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }

        val jvmMain by getting {
            dependsOn(mobileMain)

            dependencies {
                implementation(libs.ktor.client.cio)
                implementation(compose.desktop.currentOs)
                implementation(libs.android.svg)
            }
        }
        val webMain by creating {
            dependsOn(mobileMain)

            dependencies {
                implementation(libs.ktor.client.js)
            }
        }

        val wasmJsMain by getting {
            dependsOn(webMain)
        }

        val jsMain by getting {
            dependsOn(webMain)
        }
    }
}

android {
    namespace = "org.jetbrains.kotlinconf"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/mobileMain/resources")
    sourceSets["main"].resources.srcDirs("src/mobileMain/resources")

    defaultConfig {
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlin {
        jvmToolchain(11)
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }
    buildFeatures {
        compose = true
    }
}

compose.desktop {
    application {
        mainClass = "org.jetbrains.kotlinconf.MainKt"
    }
}

compose.experimental {
    web.application {}
}

val buildWebApp by tasks.creating(Copy::class) {
    val wasmWebpack = "wasmJsBrowserProductionWebpack"
    val jsWebpack = "jsBrowserProductionWebpack"

    dependsOn(wasmWebpack, jsWebpack)
    
    // TODO could be removed after migration to Kotlin 2.0+
    kotlin.wasmJs {
        applyBinaryen()
    }

    from(tasks.named(jsWebpack).get().outputs.files)
    from(tasks.named(wasmWebpack).get().outputs.files)

    into("$buildDir/webApp")

    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}