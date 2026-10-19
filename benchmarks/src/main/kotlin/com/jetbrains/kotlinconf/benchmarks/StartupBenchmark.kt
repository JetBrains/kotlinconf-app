package com.jetbrains.kotlinconf.benchmarks

import androidx.benchmark.macro.BaselineProfileMode
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StartupBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun startupNoCompilation() = startup(CompilationMode.None())

    @Test
    fun startupBaselineProfile() = startup(
        CompilationMode.Partial(baselineProfileMode = BaselineProfileMode.Require)
    )

    private fun startup(compilationMode: CompilationMode) = benchmarkRule.measureRepeated(
        packageName = PACKAGE_NAME,
        metrics = listOf(StartupTimingMetric()),
        compilationMode = compilationMode,
        iterations = 10,
        startupMode = StartupMode.COLD,
        setupBlock = {
            pressHome()
        }
    ) {
        startActivityAndAllowNotifications()
        
        // Handle Privacy Notice and Onboarding if they appear
        device.findObject(By.text("Accept"))?.click()
        device.waitForIdle()
        device.findObject(By.text("Let’s get started!"))?.click()
        device.waitForIdle()

        // Wait for the schedule to be visible to capture Time To Full Display (approx)
        device.wait(Until.hasObject(By.desc("Schedule")), 10_000)
    }
}
