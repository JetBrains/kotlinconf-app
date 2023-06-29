
plugins {
    kotlin("multiplatform").apply(false)
    kotlin("plugin.serialization").apply(false)

    id("com.android.application").apply(false)
    id("com.android.library").apply(false)
    id("org.jetbrains.compose").apply(false)
}
