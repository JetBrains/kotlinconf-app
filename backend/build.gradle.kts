import com.github.jengelman.gradle.plugins.shadow.tasks.*
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonOptions
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilationToRunnableFiles

val kotlin_version: String by project
val coroutines_version: String by project
val ktor_version: String by project
val squash_version: String by project
val hikari_version: String by project
val logback_version: String by project
val junit_version: String by project

plugins {
    kotlin("multiplatform")
    id("kotlinx-serialization")

    id("com.github.johnrengelman.shadow")
}

kotlin {
    jvm()

    sourceSets {
        val jvmMain by getting {
            kotlin.srcDir("src")
            resources.srcDir("resources")

            dependencies {
                implementation(project(":common"))

                implementation("io.ktor:ktor-server-netty:$ktor_version")
                implementation("io.ktor:ktor-auth:$ktor_version")
                implementation("io.ktor:ktor-serialization:$ktor_version")
                implementation("io.ktor:ktor-client-cio:$ktor_version")

                implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version")
                implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlin_version")

                implementation("org.jetbrains.squash:squash:$squash_version")
                implementation("org.jetbrains.squash:squash-h2:$squash_version")
                implementation("com.zaxxer:HikariCP:$hikari_version")

                implementation("ch.qos.logback:logback-classic:$logback_version")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
                implementation("org.jetbrains.kotlin:kotlin-test:$kotlin_version")
                implementation("junit:junit:$junit_version")
            }
        }
    }
}

task<JavaExec>("run") {
    main = "org.jetbrains.kotlinconf.backend.ServerKt"
    val jvm by kotlin.targets.getting
    val main: KotlinCompilation<KotlinCommonOptions> by jvm.compilations

    val runtimeDependencies = (main as KotlinCompilationToRunnableFiles<KotlinCommonOptions>).runtimeDependencyFiles
    classpath = files(main.output.allOutputs, runtimeDependencies)
}

tasks.withType<ShadowJar> {
    val jvmJar: Jar by tasks
    val jvmRuntimeClasspath by project.configurations

    configurations = listOf(jvmRuntimeClasspath)

    from(jvmJar.archiveFile)

    archiveBaseName.value("backend")
    classifier = null
    version = null
}
