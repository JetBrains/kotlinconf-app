import org.jetbrains.compose.ExperimentalComposeLibrary

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    androidTarget()
    jvm()

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

                api(compose.components.resources)

                api("io.ktor:ktor-client-logging:2.3.4")
                api("io.ktor:ktor-serialization-kotlinx-json:2.3.4")
                api("io.ktor:ktor-client-content-negotiation:2.3.4")
                api("io.ktor:ktor-client-cio:2.3.4")
                api("io.ktor:ktor-utils:2.3.4")
                api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
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
                @OptIn(ExperimentalComposeLibrary::class)
                api(compose.components.resources)

                api(libs.precompose)
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
                @OptIn(ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)

                implementation(libs.android.svg)
                implementation(libs.androidx.core.ktx)
                implementation(libs.androidx.work.runtime)
                implementation(libs.androidx.preference)
            }

            resources.srcDirs("src/commonMain/resources", "src/mobileMain/resources")
        }

        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting

        val iosMain by creating {
            dependsOn(mobileMain)

            dependencies {
                implementation("io.ktor:ktor-client-ios:2.3.4")
            }

            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
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
}

