plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization") version "1.8.0"

    id("io.ktor.plugin") version "2.2.2"
}

application {
    mainClass.set("org.jetbrains.kotlinconf.backend.MainKt")
}

val ktor_version = "2.2.2"

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        withJava()
    }

    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(project(":shared"))
                implementation("io.ktor:ktor-server-netty:$ktor_version")
                implementation("io.ktor:ktor-server-auth:$ktor_version")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
                implementation("io.ktor:ktor-client-cio:$ktor_version")
                implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
                implementation("io.ktor:ktor-server-status-pages:$ktor_version")
                implementation("io.ktor:ktor-server-default-headers:$ktor_version")
                implementation("io.ktor:ktor-server-cors:$ktor_version")
                implementation("io.ktor:ktor-server-content-negotiation:$ktor_version")
                implementation("io.ktor:ktor-server-call-logging:$ktor_version")
                implementation("io.ktor:ktor-server-conditional-headers:$ktor_version")
                implementation("io.ktor:ktor-server-compression:$ktor_version")
                implementation("io.ktor:ktor-server-partial-content:$ktor_version")
                implementation("io.ktor:ktor-server-auto-head-response:$ktor_version")
                implementation("io.ktor:ktor-server-forwarded-header:$ktor_version")
                implementation("io.ktor:ktor-server-config-yaml:$ktor_version")

                implementation("org.jetbrains.exposed:exposed-core:0.41.1")
                implementation("org.jetbrains.exposed:exposed-dao:0.41.1")
                implementation("org.jetbrains.exposed:exposed-jdbc:0.41.1")
                implementation("com.h2database:h2:2.1.214")
                implementation("org.postgresql:postgresql:42.2.2")

                implementation("com.zaxxer:HikariCP:5.0.1")

                implementation("ch.qos.logback:logback-classic:1.4.5")
            }
        }
    }
}

tasks.named<JavaExec>("run") {
    dependsOn(tasks.named<Jar>("jvmJar"))
    classpath(tasks.named<Jar>("jvmJar"))
}
