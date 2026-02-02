package org.jetbrains.kotlinconf.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.savedstate.compose.serialization.serializers.MutableStateSerializer
import androidx.savedstate.compose.serialization.serializers.SnapshotStateListSerializer

class Navigator(
    val state: NavigationState,
    val mainBackEnabled: Boolean,
) {
    private fun currentBackstack(): SnapshotStateList<AppRoute> {
        val topLevel = state.topLevelRoute
        return if (topLevel != null) state.topLevelBackStacks[topLevel]!! else state.defaultBackstack
    }

    fun goBack() {
        if (state.topLevelRoute == null) {
            state.defaultBackstack.removeLastOrNull()
            return
        }

        val currentBackstack = currentBackstack()
        if (currentBackstack.size == 1) {
            if (state.topLevelRoute != state.primaryTopLevelRoute) {
                // Go back to the main top-level route
                state.topLevelRoute = state.primaryTopLevelRoute
            } else if (mainBackEnabled) {
                currentBackstack.removeLastOrNull()
            }
        } else {
            currentBackstack.removeLastOrNull()
        }
    }

    fun add(route: AppRoute) {
        if (route is TopLevelRoute) {
            activate(route)
        } else {
            currentBackstack().add(route)
        }
    }

    fun clear() {
        currentBackstack().clear()
    }

    fun activate(route: TopLevelRoute) {
        if (route == state.topLevelRoute) {
            // Reselected the current top-level route
            val backstack = state.topLevelBackStacks.getValue(route)
            if (backstack.size > 1) {
                backstack.removeRange(1, backstack.size)
            }
        }
        state.topLevelRoute = route
    }
}


@Composable
fun rememberNavigationState(
    startRoute: AppRoute,
    primaryTopLevelRoute: TopLevelRoute,
    topLevelRoutes: Set<TopLevelRoute>,
): NavigationState {

    val topLevelRoute = rememberSerializable(
        startRoute, topLevelRoutes,
        serializer = MutableStateSerializer<TopLevelRoute?>(),
    ) {
        mutableStateOf(startRoute as? TopLevelRoute)
    }

    val topLevelBackstacks: Map<TopLevelRoute, SnapshotStateList<AppRoute>> = buildMap {
        topLevelRoutes.forEach { route ->
            put(route, rememberSerializable(serializer = SnapshotStateListSerializer()) {
                mutableStateListOf(route)
            })
        }
    }

    val defaultBackstack = rememberSerializable(serializer = SnapshotStateListSerializer()) {
        if (startRoute !is TopLevelRoute) {
            mutableStateListOf(startRoute)
        } else {
            mutableStateListOf()
        }
    }

    return remember(startRoute, topLevelRoutes) {
        NavigationState(
            topLevelRoute = topLevelRoute,
            primaryTopLevelRoute = primaryTopLevelRoute,
            topLevelBackStacks = topLevelBackstacks,
            defaultBackstack = defaultBackstack,
        )
    }
}

class NavigationState(
    topLevelRoute: MutableState<TopLevelRoute?>,
    val topLevelBackStacks: Map<TopLevelRoute, SnapshotStateList<AppRoute>>,
    val defaultBackstack: SnapshotStateList<AppRoute>,
    val primaryTopLevelRoute: TopLevelRoute,
) {
    var topLevelRoute by topLevelRoute

    @Composable
    fun toDecoratedEntries(
        entryProvider: (AppRoute) -> NavEntry<AppRoute>
    ): SnapshotStateList<NavEntry<AppRoute>> {
        val decorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator<AppRoute>(),
            rememberViewModelStoreNavEntryDecorator(),
        )

        val topLevelEntries = topLevelBackStacks
            .mapValues { (_, stack) ->
                rememberDecoratedNavEntries(
                    backStack = stack,
                    entryDecorators = decorators,
                    entryProvider = entryProvider
                )
            }
            .withDefault { emptyList() }

        val defaultEntries = rememberDecoratedNavEntries(
            backStack = defaultBackstack,
            entryDecorators = decorators,
            entryProvider = entryProvider,
        )

        return when (val topRoute = topLevelRoute) {
            null -> defaultEntries
            primaryTopLevelRoute -> topLevelEntries.getValue(primaryTopLevelRoute)
            else -> topLevelEntries.getValue(primaryTopLevelRoute) + topLevelEntries.getValue(topRoute)
        }.toMutableStateList()
    }
}
