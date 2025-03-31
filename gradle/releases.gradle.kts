// Common regex patterns
private val APP_VERSION_REGEX = """<string name="app_version">([^(]+) \((\d+)\)</string>""".toRegex()
private val VERSION_HEADER_REGEX = """## Version (.*) \(([a-f0-9]+)\)""".toRegex()
private val COMMIT_HASH_REGEX = """([a-f0-9]+) -.*""".toRegex()
private val MERGE_PR_REGEX = """([a-f0-9]+) - Merge pull request #(\d+) from.*""".toRegex()

// Helper function to run git commands
private fun runGitCommand(vararg args: String): String {
    val process = ProcessBuilder("git", *args)
        .directory(projectDir)
        .redirectOutput(ProcessBuilder.Redirect.PIPE)
        .start()

    val output = process.inputStream.bufferedReader().readText().trim()
    process.waitFor()
    return output
}

// Function to normalize content by ensuring there's only one empty line between sections
private fun normalizeContent(content: String): String {
    return content.replace(Regex("\n{3,}"), "\n\n")
}

tasks.register("prepareRelease") {
    group = "releases"
    description = "Bumps versions and prepares release notes"

    dependsOn("updateVersion", "updateReleaseNotes")

    doLast {
        // Export library definitions for licenses
        project.exec {
            commandLine = listOf(
                "./gradlew",
                ":shared:exportLibraryDefinitions",
                "-PaboutLibraries.exportPath=src/commonMain/composeResources/files"
            )
        }
    }
}

tasks.register("updateVersion") {
    group = "releases"
    description = "Updates app version code and name across all platforms"

    doLast {
        // Read current state
        val versionCodes = mutableListOf<Int?>()
        val versionNames = mutableListOf<String?>()

        // Android version
        val androidBuildGradle = file("androidApp/build.gradle.kts")
        val androidBuildGradleContent = androidBuildGradle.readText()
        versionCodes.add("""versionCode\s*=\s*(\d+)""".toRegex().find(androidBuildGradleContent)?.groupValues?.get(1)?.toIntOrNull())
        versionNames.add("""versionName\s*=\s*"([^"]+)"""".toRegex().find(androidBuildGradleContent)?.groupValues?.get(1))

        // iOS versions from project file
        val iosProjectFile = file("iosApp/KotlinConf.xcodeproj/project.pbxproj")
        val iosProjectContent = iosProjectFile.readText()
        versionCodes.addAll("""CURRENT_PROJECT_VERSION\s*=\s*(\d+)""".toRegex().findAll(iosProjectContent)
            .map { it.groupValues[1].toIntOrNull() }.toList())
        versionNames.addAll("""MARKETING_VERSION\s*=\s*([^;]+)""".toRegex().findAll(iosProjectContent)
            .map { it.groupValues[1].trim() }.toList())

        // iOS versions from Info.plist
        val iosInfoPlist = file("iosApp/iosApp/Info.plist")
        val iosInfoPlistContent = iosInfoPlist.readText()
        versionCodes.add("""<key>CFBundleVersion</key>\s*<string>(\d+)</string>""".toRegex()
            .find(iosInfoPlistContent)?.groupValues?.get(1)?.toIntOrNull())
        versionNames.add("""<key>CFBundleShortVersionString</key>\s*<string>([^<]+)</string>""".toRegex()
            .find(iosInfoPlistContent)?.groupValues?.get(1))

        // Shared version
        val sharedVersionXml = file("shared/src/commonMain/composeResources/values/version.xml")
        val sharedVersionXmlContent = sharedVersionXml.readText()
        val sharedVersionMatch = APP_VERSION_REGEX.find(sharedVersionXmlContent)
        versionCodes.add(sharedVersionMatch?.groupValues?.get(2)?.toIntOrNull())
        versionNames.add(sharedVersionMatch?.groupValues?.get(1))

        // Validate versions
        if (versionCodes.distinct().size != 1 || versionNames.distinct().size != 1) {
            throw GradleException("Version mismatch.\nVersion codes found: $versionCodes\nVersion names found: $versionNames")
        }

        val currentVersionCode = versionCodes.first() ?: throw GradleException("Invalid version code")
        val currentVersionName = versionNames.first() ?: throw GradleException("Invalid version name")
        println("Current version: $currentVersionName ($currentVersionCode)")

        // Calculate new version
        val newVersionCode = currentVersionCode + 1
        val newVersionName = "${currentVersionName.substringBeforeLast('.')}.${currentVersionName.substringAfterLast('.').toInt() + 1}"
        println("New version: $newVersionName ($newVersionCode)")

        // Update Android version
        androidBuildGradle.writeText(androidBuildGradleContent
            .replace("""versionCode = $currentVersionCode""", """versionCode = $newVersionCode""")
            .replace("""versionName = "$currentVersionName"""", """versionName = "$newVersionName""""))

        // Update iOS project file
        iosProjectFile.writeText(iosProjectContent
            .replace("""CURRENT_PROJECT_VERSION = $currentVersionCode""", """CURRENT_PROJECT_VERSION = $newVersionCode""")
            .replace("""MARKETING_VERSION = $currentVersionName""", """MARKETING_VERSION = $newVersionName"""))

        // Update iOS Info.plist
        iosInfoPlist.writeText(iosInfoPlistContent
            .replace("""<key>CFBundleVersion</key>\s*<string>$currentVersionCode</string>""".toRegex(),
                """<key>CFBundleVersion</key>
	<string>$newVersionCode</string>""")
            .replace("""<key>CFBundleShortVersionString</key>\s*<string>$currentVersionName</string>""".toRegex(),
                """<key>CFBundleShortVersionString</key>
	<string>$newVersionName</string>"""))

        // Update shared version
        sharedVersionXml.writeText(sharedVersionXmlContent
            .replace("""<string name="app_version">$currentVersionName ($currentVersionCode)</string>""",
                """<string name="app_version">$newVersionName ($newVersionCode)</string>"""))

        println("Successfully updated version to $newVersionName ($newVersionCode)")
    }
}

tasks.register("updateReleaseNotes") {
    group = "releases"
    description = "Updates release notes with commits since the last release"

    mustRunAfter("updateVersion")

    doLast {
        // Get current version information
        val sharedVersionXml = file("shared/src/commonMain/composeResources/values/version.xml")
        val currentVersionName = APP_VERSION_REGEX.find(sharedVersionXml.readText())?.groupValues?.get(1)
            ?: throw GradleException("Could not find current version name")

        // Update release notes
        val releaseNotesFile = file("CHANGELOG.md")
        val releaseNotesContent = releaseNotesFile.readText()
        val contentAfterTitle = releaseNotesContent.substringAfter("# Changelog", "")

        // Check if the current version already exists in the release notes
        val versionMatch = """## Version $currentVersionName \(([a-f0-9]+)\)""".toRegex().find(releaseNotesContent)

        // Extract the latest git hash and version from the release notes
        val lastReleaseMatch = VERSION_HEADER_REGEX.find(contentAfterTitle)
            ?: throw GradleException("Could not find last release in release notes")
        val lastVersionName = lastReleaseMatch.groupValues[1]
        val lastGitHash = lastReleaseMatch.groupValues[2]

        println("Last version: $lastVersionName, Last git hash: $lastGitHash")

        // Get commit messages since the last git hash
        val allCommitMessages = runGitCommand("log", "--pretty=format:%h - %s", "$lastGitHash..HEAD")
        val nonMergeCommitMessages = runGitCommand("log", "--no-merges", "--pretty=format:%h - %s", "$lastGitHash..HEAD")

        if (nonMergeCommitMessages.isBlank()) {
            println("No new commits since last release")
            return@doLast
        }

        println("New commits since last release:")
        println(nonMergeCommitMessages)

        // Extract PR numbers and associate them with commits
        val prMap = mutableMapOf<String, String>() // Map of commit hash to PR number
        var currentPR: String? = null

        // Process all commits to extract PR numbers from merge commits
        allCommitMessages.lines().forEach { line ->
            val mergeMatch = MERGE_PR_REGEX.find(line)
            if (mergeMatch != null) {
                currentPR = mergeMatch.groupValues[2]
            } else {
                COMMIT_HASH_REGEX.find(line)?.groupValues?.get(1)?.let { commitHash ->
                    if (currentPR != null) prMap[commitHash] = currentPR!!
                }
            }
        }

        // Group commits by PR number
        val commitsByPR = mutableMapOf<String?, MutableList<String>>()
        nonMergeCommitMessages.lines().filter { it.isNotBlank() }.forEach { line ->
            val commitMatch = COMMIT_HASH_REGEX.find(line)
            if (commitMatch != null) {
                val commitHash = commitMatch.groupValues[1]
                commitsByPR.getOrPut(prMap[commitHash]) { mutableListOf() }.add(line)
            } else {
                commitsByPR.getOrPut(null) { mutableListOf() }.add(line)
            }
        }

        // Format commits grouped by PR number
        val formattedCommits = commitsByPR.entries.joinToString("\n\n") { (prNumber, commits) ->
            if (prNumber != null) {
                // Format commits with the same PR number as a nested list
                val prLink = "[#$prNumber](https://github.com/JetBrains/kotlinconf-app/pull/$prNumber)"
                val formattedCommitsList = commits.joinToString("\n") { 
                    "  * ${it.substringAfter(" - ")}"
                }
                "* $prLink\n$formattedCommitsList"
            } else {
                // Format commits without a PR number as regular bullet points
                commits.joinToString("\n") { "* ${it.substringAfter(" - ")}" }
            }
        }

        // Get the current git hash
        val currentGitHash = runGitCommand("rev-parse", "--short", "HEAD")

        // Check if the current commit hash is the same as the latest release hash
        if (currentGitHash == lastGitHash && currentVersionName != lastVersionName) {
            // If they are the same, update that version in the release notes to the current version
            println("Current commit hash is the same as the latest release hash. Updating version in release notes from $lastVersionName to $currentVersionName")
            releaseNotesFile.writeText(releaseNotesContent.replaceFirst(
                """## Version $lastVersionName \($lastGitHash\)""",
                """## Version $currentVersionName ($lastGitHash)"""
            ))
            println("Successfully updated version in release notes")
            return@doLast
        }

        // Define the regex pattern for the "Unreleased" section
        val unreleasedRegex = """## Unreleased\s*\n\s*\n(.*?)(?=\n## |$)""".toRegex(RegexOption.DOT_MATCHES_ALL)

        // Update release notes with new section
        val newReleaseNotesContent = if (versionMatch != null) {
            // If the current version already exists, add/update Unreleased section
            val contentWithoutUnreleased = releaseNotesContent.replace(unreleasedRegex, "")
            normalizeContent("""# Changelog

## Unreleased

$formattedCommits

${contentWithoutUnreleased.substringAfter("# Changelog")}""")
        } else {
            // If the current version doesn't exist, check if there's an unreleased section
            val unreleasedMatch = unreleasedRegex.find(releaseNotesContent)
            val contentWithoutUnreleased = if (unreleasedMatch != null) {
                releaseNotesContent.replace(unreleasedRegex, "")
            } else {
                releaseNotesContent
            }

            // Add new version section
            normalizeContent("""# Changelog

## Version $currentVersionName ($currentGitHash)

$formattedCommits
${contentWithoutUnreleased.substringAfter("# Changelog")}""")
        }

        releaseNotesFile.writeText(newReleaseNotesContent)
        println("Successfully updated release notes")
    }
}
