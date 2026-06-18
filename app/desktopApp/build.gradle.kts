plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.metro)
}

kotlin {
    dependencies {
        implementation(project(":app:shared"))
        implementation(compose.desktop.currentOs)
        implementation(libs.compose.components.resources)
        implementation(libs.kotlinx.coroutines.swing)
    }
}

compose.desktop {
    application {
        mainClass = "org.jetbrains.kotlinconf.MainKt"

        buildTypes.release.proguard {
            configurationFiles.from(project.file("compose-desktop.pro"))
        }
    }
}

compose.resources {
    packageOfResClass = "org.jetbrains.kotlinconf.generated.resources.desktop"
}
