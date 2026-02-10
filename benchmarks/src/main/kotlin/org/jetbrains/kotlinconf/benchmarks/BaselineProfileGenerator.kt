package org.jetbrains.kotlinconf.benchmarks

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class BaselineProfileGenerator {

    @get:Rule
    val rule = BaselineProfileRule()

    @Test
    fun generateBaselineProfile() {
        rule.collect(
            packageName = "com.jetbrains.kotlinconf",
        ) {
            // Start the app and wait for the main screen to render.
            pressHome()
            startActivityAndWait()

            // Scroll the main schedule list to exercise common UI paths.
            device.waitForIdle()
        }
    }
}
