plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kotlinSerialization)
    application
    id("io.ktor.plugin") version "2.3.11"
}

kotlin {
    jvmToolchain(11)
}

application {
    mainClass.set("org.jetbrains.kotlinconf.backend.MainKt")
}

dependencies {
    implementation(project(":shared"))
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.ktor.client.cio)
    implementation(libs.ktor.ktor.client.content.negotiation)
    implementation(libs.ktor.server.status.pages)
    implementation(libs.ktor.server.default.headers)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.ktor.server.conditional.headers)
    implementation(libs.ktor.server.compression)
    implementation(libs.ktor.server.partial.content)
    implementation(libs.ktor.server.auto.head.response)
    implementation(libs.ktor.server.forwarded.header)
    implementation(libs.ktor.server.config.yaml)
    implementation(libs.ktor.server.swagger)

    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.h2)
    implementation(libs.postgresql)

    implementation(libs.hikaricp)

    implementation(libs.logback.classic)
}
