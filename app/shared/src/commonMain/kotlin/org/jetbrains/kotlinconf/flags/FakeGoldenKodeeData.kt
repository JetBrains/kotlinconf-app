package org.jetbrains.kotlinconf.flags

import org.jetbrains.kotlinconf.AwardCategory
import org.jetbrains.kotlinconf.AwardCategoryId
import org.jetbrains.kotlinconf.GoldenKodeeData
import org.jetbrains.kotlinconf.Nominee
import org.jetbrains.kotlinconf.NomineeId

val FakeGoldenKodeeData = GoldenKodeeData(
    categories = listOf(
        AwardCategory(
            id = AwardCategoryId("creativity"),
            title = "Creativity",
            nominees = listOf(
                Nominee(
                    id = NomineeId("aaron-todd"),
                    name = "Aaron Todd",
                    photoUrl = "https://sessionize.com/image/2ef9-400o400o1-XGxKBoqZvxxQxosrZHQHTT.png",
                    bio = "Aaron is a senior software engineer at AWS, where he works on the AWS SDK for Kotlin. He is passionate about building developer tools and making cloud development more accessible.",
                    description = "A multiplatform SDK that brings the full power of AWS services to Kotlin developers with idiomatic coroutine-based APIs.\n\n[KotlinConf](https://kotlinconf.com) | [Kotlin on Bluesky](https://bsky.app/profile/kotlinlang.org)",
                    winner = false,
                ),
                Nominee(
                    id = NomineeId("brian-norman"),
                    name = "Brian Norman",
                    photoUrl = "https://sessionize.com/image/2ef9-400o400o1-XGxKBoqZvxxQxosrZHQHTT.png",
                    bio = "Brian works on the Kotlin compiler at JetBrains, focusing on language features and performance improvements.",
                    description = "A Kotlin compiler plugin that provides detailed assertion messages by transforming assert calls at compile time.\n\n[Kotlin Programming Language](https://kotlinlang.org) | [KotlinConf on Bluesky](https://bsky.app/profile/kotlinconf.com)",
                    winner = false,
                ),
                Nominee(
                    id = NomineeId("chet-haase"),
                    name = "Chet Haase",
                    photoUrl = "https://sessionize.com/image/2ef9-400o400o1-XGxKBoqZvxxQxosrZHQHTT.png",
                    bio = "Chet is an engineering lead at Google, focused on Android developer experience and Jetpack Compose.",
                    description = "A creative set of animation utilities and examples showcasing the power of Jetpack Compose's animation system.\n\n[KotlinConf](https://kotlinconf.com) | [Kotlin Blog](https://blog.jetbrains.com/kotlin/)",
                    winner = true,
                ),
            ),
        ),
        AwardCategory(
            id = AwardCategoryId("community-impact"),
            title = "Community Impact",
            nominees = listOf(
                Nominee(
                    id = NomineeId("dave-leeds"),
                    name = "Dave Leeds",
                    photoUrl = "https://sessionize.com/image/2ef9-400o400o1-XGxKBoqZvxxQxosrZHQHTT.png",
                    bio = "Dave is an author and educator who has helped thousands of developers learn Kotlin through clear, practical content.",
                    description = "A comprehensive reference guide for Kotlin developers, covering language features with practical examples.\n\n[Kotlin Programming Language](https://kotlinlang.org) | [Kotlin on Bluesky](https://bsky.app/profile/kotlinlang.org)",
                    winner = false,
                ),
                Nominee(
                    id = NomineeId("elena-marchetti"),
                    name = "Elena Marchetti",
                    photoUrl = "https://sessionize.com/image/2ef9-400o400o1-XGxKBoqZvxxQxosrZHQHTT.png",
                    bio = "Elena is a developer advocate at JetBrains who organizes community events and creates educational content for Kotlin developers.",
                    description = "A worldwide initiative to bring KotlinConf content and community meetups to developers everywhere.\n\n[KotlinConf](https://kotlinconf.com) | [KotlinConf on Bluesky](https://bsky.app/profile/kotlinconf.com)",
                    winner = true,
                ),
                Nominee(
                    id = NomineeId("frank-sommers"),
                    name = "Frank Sommers",
                    photoUrl = "https://sessionize.com/image/2ef9-400o400o1-XGxKBoqZvxxQxosrZHQHTT.png",
                    bio = "Frank is a longtime JVM community leader who has championed Kotlin adoption through editorial content and conference presentations.",
                    description = "A curated collection of real-world Kotlin migration stories and best practices from industry leaders.\n\n[Kotlin Blog](https://blog.jetbrains.com/kotlin/) | [Kotlin on Bluesky](https://bsky.app/profile/kotlinlang.org)",
                    winner = false,
                ),
            ),
        ),
        AwardCategory(
            id = AwardCategoryId("best-library"),
            title = "Best Library",
            nominees = listOf(
                Nominee(
                    id = NomineeId("grace-chen"),
                    name = "Grace Chen",
                    photoUrl = "https://sessionize.com/image/2ef9-400o400o1-XGxKBoqZvxxQxosrZHQHTT.png",
                    bio = "Grace is a prolific open-source contributor focused on building multiplatform Kotlin libraries.",
                    description = "A library that simplifies using Kotlin coroutines from Swift code in Kotlin Multiplatform projects.\n\n[Kotlin Multiplatform](https://kotlinlang.org/lp/multiplatform/) | [KotlinConf](https://kotlinconf.com)",
                    winner = false,
                ),
                Nominee(
                    id = NomineeId("heidi-zhang"),
                    name = "Heidi Zhang",
                    photoUrl = "https://sessionize.com/image/2ef9-400o400o1-XGxKBoqZvxxQxosrZHQHTT.png",
                    bio = "Heidi works on mobile infrastructure at Square, with a focus on build tooling and developer productivity.",
                    description = "A library for building StateFlow streams using Jetpack Compose's compiler and runtime.\n\n[Kotlin Programming Language](https://kotlinlang.org) | [KotlinConf on Bluesky](https://bsky.app/profile/kotlinconf.com)",
                    winner = true,
                ),
                Nominee(
                    id = NomineeId("ivan-petrov"),
                    name = "Ivan Petrov",
                    photoUrl = "https://sessionize.com/image/2ef9-400o400o1-XGxKBoqZvxxQxosrZHQHTT.png",
                    bio = "Ivan specializes in backend systems and has contributed to multiple Kotlin server-side frameworks.",
                    description = "A Gradle plugin for Kotlin code coverage, supporting JVM and multiplatform projects.\n\n[Kotlin Blog](https://blog.jetbrains.com/kotlin/) | [Kotlin on Bluesky](https://bsky.app/profile/kotlinlang.org)",
                    winner = false,
                ),
            ),
        ),
        AwardCategory(
            id = AwardCategoryId("rising-star"),
            title = "Rising Star",
            nominees = listOf(
                Nominee(
                    id = NomineeId("judy-kim"),
                    name = "Judy Kim",
                    photoUrl = "https://sessionize.com/image/2ef9-400o400o1-XGxKBoqZvxxQxosrZHQHTT.png",
                    bio = "Judy is an Android developer at Lyft who quickly became known for her contributions to the Kotlin community through blog posts and conference talks.",
                    description = "A set of testing utilities that simplify UI testing for Jetpack Compose applications.\n\n[KotlinConf](https://kotlinconf.com) | [Kotlin Programming Language](https://kotlinlang.org)",
                    winner = false,
                ),
                Nominee(
                    id = NomineeId("karl-weber"),
                    name = "Karl Weber",
                    photoUrl = "https://sessionize.com/image/2ef9-400o400o1-XGxKBoqZvxxQxosrZHQHTT.png",
                    bio = "Karl is a computer science student whose open-source Kotlin projects have gained significant traction in the developer community.",
                    description = "An interactive environment for experimenting with KotlinDL, making machine learning accessible to Kotlin developers.\n\n[Kotlin Multiplatform](https://kotlinlang.org/lp/multiplatform/) | [KotlinConf on Bluesky](https://bsky.app/profile/kotlinconf.com)",
                    winner = false,
                ),
                Nominee(
                    id = NomineeId("liam-obrien"),
                    name = "Liam O'Brien",
                    photoUrl = "https://sessionize.com/image/2ef9-400o400o1-XGxKBoqZvxxQxosrZHQHTT.png",
                    bio = "Liam is a full stack developer who bridges the gap between Kotlin backend and frontend development using Kotlin/JS and Kotlin/Wasm.",
                    description = "A collection of demos and templates showcasing Kotlin/Wasm capabilities for web development.\n\n[Kotlin Blog](https://blog.jetbrains.com/kotlin/) | [KotlinConf](https://kotlinconf.com)",
                    winner = false,
                ),
            ),
        ),
    ),
)
