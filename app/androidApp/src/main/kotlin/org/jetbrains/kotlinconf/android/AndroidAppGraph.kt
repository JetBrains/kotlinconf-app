package org.jetbrains.kotlinconf.android

import android.app.Application
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import org.jetbrains.kotlinconf.flags.Flags
import org.jetbrains.kotlinconf.di.NotificationIcon
import org.jetbrains.kotlinconf.di.BaseAndroidAppGraph

@DependencyGraph(AppScope::class)
interface AndroidAppGraph : BaseAndroidAppGraph {
    @DependencyGraph.Factory
    interface Factory {
        fun create(
            @Provides application: Application,
            @Provides @NotificationIcon iconRes: Int,
            @Provides platformFlags: Flags = Flags(),
        ): AndroidAppGraph
    }
}
