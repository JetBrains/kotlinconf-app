plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.android.library")
    id("org.jetbrains.compose")
}

val precompose_version: String by project

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
                implementation(compose.runtime)
                implementation(compose.foundation)
                api(compose.animation)
                implementation(compose.material)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)

                api("moe.tlaster:precompose:$precompose_version")
            }
        }

        val mobileTest by creating {
            dependsOn(mobileMain)
            dependsOn(commonTest)
        }

        val androidMain by getting {
            dependsOn(mobileMain)

            dependencies {
                api("androidx.activity:activity-compose:1.7.2")
                api("androidx.appcompat:appcompat:1.6.1")
                api("androidx.core:core-ktx:1.10.1")

                implementation("androidx.core:core:1.10.1")
                implementation("androidx.preference:preference:1.2.1")
                implementation("androidx.work:work-runtime-ktx:2.8.1")
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
    compileSdk = (findProperty("android.compileSdk") as String).toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].resources.srcDirs("src/mobileMain/resources")
    sourceSets["main"].res.srcDirs("src/mobileMain/resources")


    defaultConfig {
        minSdk = (findProperty("android.minSdk") as String).toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlin {
        jvmToolchain(11)
    }
}

