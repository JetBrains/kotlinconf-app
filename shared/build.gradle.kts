plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization") version "1.8.10"
    id("com.android.library")
}

kotlin {
    android()
    jvm()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api("io.ktor:ktor-client-logging:2.2.2")
                api("io.ktor:ktor-serialization-kotlinx-json:2.2.2")
                api("io.ktor:ktor-client-content-negotiation:2.2.2")
                api("io.ktor:ktor-client-cio:2.2.2")
                api("io.ktor:ktor-utils:2.2.2")
                api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("androidx.core:core:1.9.0")
                implementation("androidx.preference:preference:1.2.0")

            }
        }

        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependencies {
                implementation("io.ktor:ktor-client-ios:2.1.3")
            }

            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }
    }
}

android {
    namespace = "org.jetbrains.kotlinconf"
    compileSdk = 33
    defaultConfig {
        minSdk = 23
    }
}
dependencies {
    implementation("androidx.work:work-runtime-ktx:2.7.1")
}
