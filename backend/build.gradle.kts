plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ktor)
}

kotlin {
    jvmToolchain(11)
}

application {
    mainClass.set("org.jetbrains.kotlinconf.backend.MainKt")
}

dependencies {
    implementation(project(":shared"))
    implementation("io.ktor:ktor-server-netty")
    implementation("io.ktor:ktor-server-auth")
    implementation("io.ktor:ktor-serialization-kotlinx-json")
    implementation("io.ktor:ktor-client-cio")
    implementation("io.ktor:ktor-client-content-negotiation")
    implementation("io.ktor:ktor-server-status-pages")
    implementation("io.ktor:ktor-server-default-headers")
    implementation("io.ktor:ktor-server-cors")
    implementation("io.ktor:ktor-server-content-negotiation")
    implementation("io.ktor:ktor-server-call-logging")
    implementation("io.ktor:ktor-server-conditional-headers")
    implementation("io.ktor:ktor-server-compression")
    implementation("io.ktor:ktor-server-partial-content")
    implementation("io.ktor:ktor-server-auto-head-response")
    implementation("io.ktor:ktor-server-forwarded-header")
    implementation("io.ktor:ktor-server-config-yaml")
    implementation("io.ktor:ktor-server-swagger")

    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.h2)
    implementation(libs.postgresql)

    implementation(libs.hikaricp)

    implementation(libs.logback.classic)
}
