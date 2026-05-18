package org.jetbrains.kotlinconf.di

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import org.jetbrains.kotlinconf.flags.Flags

@DependencyGraph(AppScope::class)
interface JvmAppGraph : AppGraph {

    @DependencyGraph.Factory
    interface Factory {
        fun create(
            @Provides platformFlags: Flags = Flags(),
        ): JvmAppGraph
    }
}
