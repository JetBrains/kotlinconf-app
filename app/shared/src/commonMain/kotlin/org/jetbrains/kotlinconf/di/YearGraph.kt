package org.jetbrains.kotlinconf.di

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides
import org.jetbrains.kotlinconf.network.YearlyApi
import org.jetbrains.kotlinconf.storage.YearlyStorage

@GraphExtension(YearScope::class)
interface YearGraph {
    val storage: YearlyStorage
    val api: YearlyApi

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    interface Factory {
        fun create(@Provides year: Int): YearGraph
    }
}
