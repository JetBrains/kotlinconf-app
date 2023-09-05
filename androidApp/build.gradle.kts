plugins {
    id("com.android.application")
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

kotlin {
    androidTarget()

    sourceSets {
        val androidMain by getting {
            dependencies {
                implementation(project(":shared"))

                implementation("androidx.core:core-ktx:1.10.1")
                implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")

                implementation("androidx.compose.ui:ui:1.6.0-alpha04")
                implementation("androidx.compose.ui:ui-tooling:1.6.0-alpha04")
                implementation("androidx.compose.ui:ui-tooling-preview:1.6.0-alpha04")
                implementation("androidx.compose.foundation:foundation:1.6.0-alpha04")
                implementation("androidx.compose.material:material:1.6.0-alpha04")
                implementation("androidx.activity:activity-compose:1.7.2")
                implementation("androidx.navigation:navigation-compose:2.7.1")
                implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")
                implementation("io.coil-kt:coil-compose:2.2.2")
            }
        }

        val androidUnitTest by getting {
            dependencies {
                implementation("junit:junit:4.13.2")
                implementation("androidx.test.ext:junit:1.1.5")
                implementation("androidx.test.espresso:espresso-core:3.5.1")
            }
        }
    }
}

android {
    namespace = "com.jetbrains.kotlinconf"
    compileSdk = 34

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    defaultConfig {
        applicationId = "com.jetbrains.kotlinconf"
        minSdk = 24
        versionCode = 31
        versionName = "31.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isDebuggable = false
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlin {
        jvmToolchain(11)
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}


