allprojects {
    repositories {
        maven("https://maven.pkg.jetbrains.space/public/p/ktor/eap")
        google()
        mavenCentral()
    }
}

plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.jetbrainsCompose) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinSerialization) apply false
    alias(libs.plugins.kotlinJvm) apply false
}

tasks.wrapper {
    distributionType = Wrapper.DistributionType.BIN
    gradleVersion = "8.10.2"
    distributionUrl = "https://services.gradle.org/distributions/gradle-$gradleVersion-bin.zip"
    distributionSha256Sum = "31c55713e40233a8303827ceb42ca48a47267a0ad4bab9177123121e71524c26"
}