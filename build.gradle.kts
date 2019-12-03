buildscript {
    val kotlin_version: String by extra
    val gradle_android_version: String by extra
    val shadow_version: String by extra

    repositories {
        maven("https://kotlin.bintray.com/kotlinx")
        maven("https://dl.bintray.com/jetbrains/kotlin-native-dependencies")
        maven("https://dl.bintray.com/kotlin/kotlin-dev")

        google()
        jcenter()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
        classpath("com.android.tools.build:gradle:$gradle_android_version")
        classpath("com.github.jengelman.gradle.plugins:shadow:$shadow_version")
    }
}

allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://dl.bintray.com/kotlin/kotlinx")
        maven("https://dl.bintray.com/kotlin/ktor")
        maven("https://dl.bintray.com/sargunster/maven")
        maven("https://dl.bintray.com/kotlin/squash")
        maven("https://dl.bintray.com/kotlin/kotlin-dev")

        google()
        jcenter()
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}
