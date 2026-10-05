package com.jetbrains.kotlinconf.benchmarks

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BaselineProfileGenerator {

    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    @Test
    fun generate() = baselineProfileRule.collect(
        packageName = PACKAGE_NAME,
        includeInStartupProfile = true
    ) {
        startActivityAndAllowNotifications()

        // Handle Privacy Notice if it appears
        device.findObject(By.text("Accept"))?.click()
        device.waitForIdle()

        // Handle onboarding if it appears
        device.findObject(By.text("Let’s get started!"))?.click()
        device.waitForIdle()

        // Wait for the schedule to be visible
        device.wait(Until.hasObject(By.desc("Schedule")), 10_000)

        // Navigate to Speakers
        val speakers = device.findObject(By.desc("Speakers"))
        speakers?.click()
        device.waitForIdle()

        // Navigate to Map
        val map = device.findObject(By.desc("Map"))
        map?.click()
        device.waitForIdle()

        // Navigate to Info
        val info = device.findObject(By.desc("Info"))
        info?.click()
        device.waitForIdle()

        // Navigate back to Schedule
        val schedule = device.findObject(By.desc("Schedule"))
        schedule?.click()
        device.waitForIdle()

        // Scroll the schedule list
        val list = device.findObject(By.scrollable(true))
        list?.fling(androidx.test.uiautomator.Direction.DOWN)
        device.waitForIdle()
    }
}
