package org.jetbrains.kotlinconf

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
@JvmInline
value class AwardCategoryId(val id: String)

@Serializable
@JvmInline
value class NomineeId(val id: String)

data class Nominee(
    val id: NomineeId,
    val name: String,
    val photoUrl: String,
    val position: String = "",
    val bio: String = "",
    val projectName: String = "",
    val projectDescription: String = "",
    val winner: Boolean = false,
)

data class AwardCategory(
    val id: AwardCategoryId,
    val title: String,
    val description: String,
    val nominees: List<Nominee>,
)

@Inject
@SingleIn(AppScope::class)
class FakeGoldenKodeeService {
    // All fake data, please ignore its contents
    private val awardCategories: List<AwardCategory> = listOf(
        AwardCategory(
            id = AwardCategoryId("creativity"),
            title = "Golden Kodee for Creativity",
            description = "Celebrating the most creative and innovative use of Kotlin in a project or library.",
            nominees = listOf(
                Nominee(
                    id = NomineeId("aaron-todd"),
                    name = "Aaron Todd",
                    photoUrl = "https://sessionize.com/image/2ef9-400o400o1-XGxKBoqZvxxQxosrZHQHTT.png",
                    position = "Senior Software Engineer at AWS",
                    bio = "Aaron is a senior software engineer at AWS, where he works on the AWS SDK for Kotlin. He is passionate about building developer tools and making cloud development more accessible.",
                    projectName = "AWS SDK for Kotlin",
                    projectDescription = "A multiplatform SDK that brings the full power of AWS services to Kotlin developers with idiomatic coroutine-based APIs.",
                ),
                Nominee(
                    id = NomineeId("brian-norman"),
                    name = "Brian Norman",
                    photoUrl = "https://sessionize.com/image/2ef9-400o400o1-XGxKBoqZvxxQxosrZHQHTT.png",
                    position = "Kotlin Compiler Engineer at JetBrains",
                    bio = "Brian works on the Kotlin compiler at JetBrains, focusing on language features and performance improvements.",
                    projectName = "Kotlin Power-Assert",
                    projectDescription = "A Kotlin compiler plugin that provides detailed assertion messages by transforming assert calls at compile time.",
                ),
                Nominee(
                    id = NomineeId("chet-haase"),
                    name = "Chet Haase",
                    photoUrl = "https://sessionize.com/image/2ef9-400o400o1-XGxKBoqZvxxQxosrZHQHTT.png",
                    position = "Engineering Lead at Google",
                    bio = "Chet is an engineering lead at Google, focused on Android developer experience and Jetpack Compose.",
                    projectName = "Compose Animations Toolkit",
                    projectDescription = "A creative set of animation utilities and examples showcasing the power of Jetpack Compose's animation system.",
                    winner = true,
                ),
            ),
        ),
        AwardCategory(
            id = AwardCategoryId("community-impact"),
            title = "Golden Kodee for Community Impact",
            description = "Recognizing outstanding contributions to the Kotlin community through talks, articles, and open-source work.",
            nominees = listOf(
                Nominee(
                    id = NomineeId("dave-leeds"),
                    name = "Dave Leeds",
                    photoUrl = "https://sessionize.com/image/2ef9-400o400o1-XGxKBoqZvxxQxosrZHQHTT.png",
                    position = "Author of Kotlin Quick Reference",
                    bio = "Dave is an author and educator who has helped thousands of developers learn Kotlin through clear, practical content.",
                    projectName = "Kotlin Quick Reference",
                    projectDescription = "A comprehensive reference guide for Kotlin developers, covering language features with practical examples.",
                ),
                Nominee(
                    id = NomineeId("elena-marchetti"),
                    name = "Elena Marchetti",
                    photoUrl = "https://sessionize.com/image/2ef9-400o400o1-XGxKBoqZvxxQxosrZHQHTT.png",
                    position = "Developer Advocate at JetBrains",
                    bio = "Elena is a developer advocate at JetBrains who organizes community events and creates educational content for Kotlin developers.",
                    projectName = "KotlinConf Global",
                    projectDescription = "A worldwide initiative to bring KotlinConf content and community meetups to developers everywhere.",
                ),
                Nominee(
                    id = NomineeId("frank-sommers"),
                    name = "Frank Sommers",
                    photoUrl = "https://sessionize.com/image/2ef9-400o400o1-XGxKBoqZvxxQxosrZHQHTT.png",
                    position = "Editor-in-Chief at Artima",
                    bio = "Frank is a longtime JVM community leader who has championed Kotlin adoption through editorial content and conference presentations.",
                    projectName = "Kotlin Adoption Stories",
                    projectDescription = "A curated collection of real-world Kotlin migration stories and best practices from industry leaders.",
                    winner = true,
                ),
            ),
        ),
        AwardCategory(
            id = AwardCategoryId("best-library"),
            title = "Golden Kodee for Best Library",
            description = "The most impactful open-source Kotlin library of the year.",
            nominees = listOf(
                Nominee(
                    id = NomineeId("grace-chen"),
                    name = "Grace Chen",
                    photoUrl = "https://sessionize.com/image/2ef9-400o400o1-XGxKBoqZvxxQxosrZHQHTT.png",
                    position = "Open Source Developer",
                    bio = "Grace is a prolific open-source contributor focused on building multiplatform Kotlin libraries.",
                    projectName = "KMP-NativeCoroutines",
                    projectDescription = "A library that simplifies using Kotlin coroutines from Swift code in Kotlin Multiplatform projects.",
                ),
                Nominee(
                    id = NomineeId("heidi-zhang"),
                    name = "Heidi Zhang",
                    photoUrl = "https://sessionize.com/image/2ef9-400o400o1-XGxKBoqZvxxQxosrZHQHTT.png",
                    position = "Mobile Engineer at Square",
                    bio = "Heidi works on mobile infrastructure at Square, with a focus on build tooling and developer productivity.",
                    projectName = "Molecule",
                    projectDescription = "A library for building StateFlow streams using Jetpack Compose's compiler and runtime.",
                    winner = true,
                ),
                Nominee(
                    id = NomineeId("ivan-petrov"),
                    name = "Ivan Petrov",
                    photoUrl = "https://sessionize.com/image/2ef9-400o400o1-XGxKBoqZvxxQxosrZHQHTT.png",
                    position = "Backend Developer at Stripe",
                    bio = "Ivan specializes in backend systems and has contributed to multiple Kotlin server-side frameworks.",
                    projectName = "Kover",
                    projectDescription = "A Gradle plugin for Kotlin code coverage, supporting JVM and multiplatform projects.",
                ),
            ),
        ),
        AwardCategory(
            id = AwardCategoryId("rising-star"),
            title = "Golden Kodee for Rising Star",
            description = "A newcomer who made a big splash in the Kotlin ecosystem this year.",
            nominees = listOf(
                Nominee(
                    id = NomineeId("judy-kim"),
                    name = "Judy Kim",
                    photoUrl = "https://sessionize.com/image/2ef9-400o400o1-XGxKBoqZvxxQxosrZHQHTT.png",
                    position = "Android Developer at Lyft",
                    bio = "Judy is an Android developer at Lyft who quickly became known for her contributions to the Kotlin community through blog posts and conference talks.",
                    projectName = "Compose UI Testing Toolkit",
                    projectDescription = "A set of testing utilities that simplify UI testing for Jetpack Compose applications.",
                ),
                Nominee(
                    id = NomineeId("karl-weber"),
                    name = "Karl Weber",
                    photoUrl = "https://sessionize.com/image/2ef9-400o400o1-XGxKBoqZvxxQxosrZHQHTT.png",
                    position = "Student & Open Source Contributor",
                    bio = "Karl is a computer science student whose open-source Kotlin projects have gained significant traction in the developer community.",
                    projectName = "KotlinDL Playground",
                    projectDescription = "An interactive environment for experimenting with KotlinDL, making machine learning accessible to Kotlin developers.",
                ),
                Nominee(
                    id = NomineeId("liam-obrien"),
                    name = "Liam O'Brien",
                    photoUrl = "https://sessionize.com/image/2ef9-400o400o1-XGxKBoqZvxxQxosrZHQHTT.png",
                    position = "Full Stack Developer",
                    bio = "Liam is a full stack developer who bridges the gap between Kotlin backend and frontend development using Kotlin/JS and Kotlin/Wasm.",
                    projectName = "Kotlin Wasm Showcase",
                    projectDescription = "A collection of demos and templates showcasing Kotlin/Wasm capabilities for web development.",
                    winner = true,
                ),
            ),
        ),
    )

    fun getCategories(): Flow<List<AwardCategory>> = flowOf(awardCategories)

    fun getCategory(id: AwardCategoryId): Flow<AwardCategory?> =
        flowOf(awardCategories.find { it.id == id })

    fun getNominee(categoryId: AwardCategoryId, nomineeId: NomineeId): Flow<Nominee?> =
        flowOf(awardCategories.find { it.id == categoryId }?.nominees?.find { it.id == nomineeId })
}
