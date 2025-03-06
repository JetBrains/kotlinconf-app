import com.mikepenz.aboutlibraries.plugin.DuplicateMode
import com.mikepenz.aboutlibraries.plugin.DuplicateRule
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeFeatureFlag

plugins {
    alias(libs.plugins.aboutLibraries)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.kotlinParcelize)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.composeHotReload)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }
    androidTarget {
        compilerOptions {
            freeCompilerArgs.addAll(
                "-P",
                "plugin:org.jetbrains.kotlin.parcelize:additionalAnnotation=org.jetbrains.kotlinconf.zoomable.internal.AndroidParcelize"
            )
        }
    }

    jvm()

    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
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

    // Required as we create additional custom source sets below
    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain.dependencies {
            api(projects.uiComponents)

            api(compose.runtime)
            api(compose.foundation)
            api(compose.animation)
            api(compose.components.resources)

            api(libs.components.ui.tooling.preview)
            api(libs.koin.compose.viewmodel.navigation)

            api(libs.ktor.client.logging)
            api(libs.ktor.serialization.kotlinx.json)
            api(libs.ktor.client.content.negotiation)
            api(libs.ktor.utils)

            implementation(libs.kotlinx.datetime)

            implementation(libs.androidx.navigation.compose)
            implementation(libs.ktor.client.core)

            implementation(libs.aboutlibraries.core)

            // Multiplatform Settings
            implementation(libs.settings)
            implementation(libs.settings.serialization)
            implementation(libs.settings.observable)
            implementation(libs.settings.coroutines)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }

        val nonWebMain by creating {
            dependsOn(commonMain.get())
            dependencies {
                // TODO move this to commonMain once Wasm version is available
                implementation(libs.doistx.normalize)
            }
        }

        val nonAndroidMain by creating {
            dependsOn(commonMain.get())
        }

        androidMain {
            dependsOn(nonWebMain)
            dependencies {
                implementation(libs.android.svg)
                implementation(libs.androidx.core.ktx)
                implementation(libs.androidx.work.runtime)
                implementation(libs.androidx.preference)
                implementation(libs.compose.ui.tooling.preview)
                implementation(libs.ktor.client.okhttp)
            }
        }

        iosMain {
            dependsOn(nonWebMain)
            dependsOn(nonAndroidMain)
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.ktor.client.darwin)
            }
        }

        jvmMain {
            dependsOn(nonWebMain)
            dependsOn(nonAndroidMain)
            dependencies {
                implementation(libs.ktor.client.okhttp)
                implementation(compose.desktop.currentOs)
                implementation(libs.android.svg)
                implementation(libs.kotlinx.coroutines.swing)
            }
        }

        val webMain by creating {
            dependsOn(commonMain.get())
            dependsOn(nonAndroidMain)
            dependencies {
                implementation(libs.ktor.client.js)
                implementation(npm("@js-joda/timezone", "2.3.0"))
            }
        }

        val wasmJsMain by getting {
            dependsOn(webMain)
        }

        jsMain {
            dependsOn(webMain)
        }
    }
}

android {
    namespace = "org.jetbrains.kotlinconf"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlin {
        jvmToolchain(11)
    }
}

// Android preview support
dependencies {
    debugImplementation(compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "org.jetbrains.kotlinconf.MainKt"
    }
}

val buildWebApp by tasks.creating(Copy::class) {
    val wasmWebpack = "wasmJsBrowserProductionWebpack"
    val jsWebpack = "jsBrowserProductionWebpack"

    dependsOn(wasmWebpack, jsWebpack)

    from(tasks.named(jsWebpack).get().outputs.files)
    from(tasks.named(wasmWebpack).get().outputs.files)

    into(layout.buildDirectory.dir("webApp"))

    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

// Hot reload support
composeCompiler {
    featureFlags.add(ComposeFeatureFlag.OptimizeNonSkippingGroups)
}

aboutLibraries {
    duplicationMode = DuplicateMode.MERGE
    duplicationRule = DuplicateRule.SIMPLE
}
