plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.googleServices)
}

kotlin {
    androidTarget()

    sourceSets {
        androidMain.dependencies {
            implementation(projects.shared)
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.core.splashscreen)
            implementation(libs.androidx.navigation3.ui)
        }

        androidUnitTest.dependencies {
            implementation(libs.junit)
        }
    }

    jvmToolchain(21)
}

android {
    namespace = "com.jetbrains.kotlinconf"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.jetbrains.kotlinconf"
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        versionCode = 67
        versionName = "39.0.5"

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
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    debugImplementation(libs.compose.ui.tooling)
}
