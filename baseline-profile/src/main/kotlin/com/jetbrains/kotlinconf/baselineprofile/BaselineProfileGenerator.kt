package com.jetbrains.kotlinconf.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * This test generates a baseline profile for the target application.
 *
 * You can run it directly from Android Studio or using the command line:
 * `./gradlew :baseline-profile:generateBaselineProfile`
 *
 * After running, the generated profile will be copied to:
 * `androidApp/src/release/generated/baselineProfiles/baseline-prof.txt`
 */
@RunWith(AndroidJUnit4::class)
class BaselineProfileGenerator {

    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    @Test
    fun generateBaselineProfile() = baselineProfileRule.collect(
        packageName = "com.jetbrains.kotlinconf",
        profileBlock = {
            startActivityAndWait()
        }
    )
}