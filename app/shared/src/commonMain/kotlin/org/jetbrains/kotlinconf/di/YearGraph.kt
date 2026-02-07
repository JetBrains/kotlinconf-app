package org.jetbrains.kotlinconf.di

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides
import org.jetbrains.kotlinconf.YearlyAPIClient
import org.jetbrains.kotlinconf.storage.YearlyStorage

@GraphExtension(YearScope::class)
interface YearGraph {
    val yearlyStorage: YearlyStorage
    val yearlyAPIClient: YearlyAPIClient

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    interface Factory {
        fun create(@Provides year: Int): YearGraph
    }
}
