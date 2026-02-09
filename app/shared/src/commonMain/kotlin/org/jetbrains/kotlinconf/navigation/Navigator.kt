package org.jetbrains.kotlinconf.navigation

class Navigator(
    val state: NavState,
    val mainBackEnabled: Boolean,
) {
    fun goBack() {
        if (state.topLevelRoute == null) {
            state.defaultBackstack.removeLastOrNull()
            return
        }

        val currentBackstack = state.currentBackstack
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
            state.currentBackstack.add(route)
        }
    }

    fun clear() {
        state.currentBackstack.clear()
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
