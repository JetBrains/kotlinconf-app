plugins {
    id("com.android.test")
    id("androidx.baselineprofile")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.jetbrains.kotlinconf.baselineprofile"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    targetProjectPath = ":androidApp"
}

dependencies {
    implementation(libs.androidx.test.ext.junit)
    implementation(libs.androidx.test.espresso.core)
    implementation(libs.androidx.benchmark.macro)
    implementation(libs.androidx.profileinstaller)
}

baselineProfile {
    packageName = "com.jetbrains.kotlinconf"
}


