package org.jetbrains.kotlinconf.navigation

class Navigator(
    val state: NavState,
    val topLevelBackEnabled: Boolean,
) {
    fun goBack() {
        if (state.topLevelRoute == null) {
            // We're using the default stack, remove an entry if possible
            if (state.defaultBackstack.size > 1) {
                state.defaultBackstack.removeLastOrNull()
            }
            return
        }

        val currentBackstack = state.currentBackstack
        if (currentBackstack.size == 1 && state.topLevelRoute != state.primaryTopLevelRoute) {
            // Can't go further up on current backstack, but we're not on the primary route
            if (topLevelBackEnabled) {
                // Go back to the primary top-level route if enabled
                state.topLevelRoute = state.primaryTopLevelRoute
            }
        } else if (currentBackstack.size > 1) {
            currentBackstack.removeLastOrNull()
        }
    }

    fun add(route: AppRoute) {
        if (route is TopLevelRoute) {
            activate(route)
        } else {
            state.currentBackstack.add(route)
        }
    }

    fun set(route: AppRoute) {
        state.currentBackstack.clear()
        add(route)
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
