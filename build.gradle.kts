plugins {
    alias(libs.plugins.aboutLibraries) apply false
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.jetbrainsCompose) apply false
    alias(libs.plugins.kotlinParcelize) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinSerialization) apply false
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.composeHotReload) apply false
    alias(libs.plugins.jib) apply false
    alias(libs.plugins.googleServices) apply false
}

tasks.wrapper {
    distributionType = Wrapper.DistributionType.BIN
    gradleVersion = "8.10.2"
    distributionUrl = "https://services.gradle.org/distributions/gradle-$gradleVersion-bin.zip"
    distributionSha256Sum = "31c55713e40233a8303827ceb42ca48a47267a0ad4bab9177123121e71524c26"
}

tasks.register("updateAppVersions") {
    group = "versioning"
    description = "Updates app version code and name across all platforms"

    doLast {
        val versionCodes = mutableListOf<Int?>()
        val versionNames = mutableListOf<String?>()

        val androidBuildGradle = file("androidApp/build.gradle.kts")
        val androidBuildGradleContent = androidBuildGradle.readText()
        versionCodes.add(
            """versionCode\s*=\s*(\d+)""".toRegex().find(androidBuildGradleContent)?.groupValues?.get(1)?.toIntOrNull()
        )
        versionNames.add(
            """versionName\s*=\s*"([^"]+)"""".toRegex().find(androidBuildGradleContent)?.groupValues?.get(1)
        )

        val iosProjectFile = file("iosApp/KotlinConf.xcodeproj/project.pbxproj")
        val iosProjectContent = iosProjectFile.readText()
        versionCodes.addAll(
            """CURRENT_PROJECT_VERSION\s*=\s*(\d+)""".toRegex().findAll(iosProjectContent)
                .map { it.groupValues[1].toIntOrNull() }.toList()
        )
        versionNames.addAll(
            """MARKETING_VERSION\s*=\s*([^;]+)""".toRegex().findAll(iosProjectContent).map { it.groupValues[1].trim() }
                .toList()
        )

        val sharedVersionXml = file("shared/src/commonMain/composeResources/values/version.xml")
        val sharedVersionXmlContent = sharedVersionXml.readText()
        val sharedVersionMatch =
            """<string name="app_version">([^(]+) \((\d+)\)</string>""".toRegex().find(sharedVersionXmlContent)
        versionCodes.add(sharedVersionMatch?.groupValues?.get(2)?.toIntOrNull())
        versionNames.add(sharedVersionMatch?.groupValues?.get(1))

        if (versionCodes.distinct().size != 1 || versionNames.distinct().size != 1) {
            throw GradleException("Version mismatch.\nVersion codes found: $versionCodes\nVersion names found: $versionNames")
        }

        val currentVersionCode = versionCodes.first()
        val currentVersionName = versionNames.first()
        if (currentVersionCode == null || currentVersionName == null) {
            throw GradleException("Invalid versions. Code was $currentVersionCode, name was $currentVersionName")
        }
        println("Current version: $currentVersionName ($currentVersionCode)")

        val newVersionCode = currentVersionCode + 1
        val newVersionName =
            "${currentVersionName.substringBeforeLast('.')}.${currentVersionName.substringAfterLast('.').toInt() + 1}"
        println("New version: $newVersionName ($newVersionCode)")

        androidBuildGradle.writeText(
            androidBuildGradleContent
                .replace("""versionCode = $currentVersionCode""", """versionCode = $newVersionCode""")
                .replace("""versionName = "$currentVersionName"""", """versionName = "$newVersionName"""")
        )
        iosProjectFile.writeText(
            iosProjectContent
                .replace(
                    """CURRENT_PROJECT_VERSION = $currentVersionCode""",
                    """CURRENT_PROJECT_VERSION = $newVersionCode"""
                )
                .replace("""MARKETING_VERSION = $currentVersionName""", """MARKETING_VERSION = $newVersionName""")
        )
        sharedVersionXml.writeText(
            sharedVersionXmlContent
                .replace(
                    """<string name="app_version">$currentVersionName ($currentVersionCode)</string>""",
                    """<string name="app_version">$newVersionName ($newVersionCode)</string>"""
                )
        )

        println("Successfully updated version to $newVersionName ($newVersionCode)")
    }
}
