package org.jetbrains.kotlinconf.navigation

class ExternalNavigator(
    override val state: NavState,
    override val topLevelBackEnabled: Boolean,
    private val onAdd: (AppRoute) -> Unit,
    private val onGoBack: () -> Unit,
    private val onSet: (AppRoute) -> Unit,
    private val onActivate: (TopLevelRoute) -> Unit,
) : Navigator {
    override fun goBack() = onGoBack()

    override fun add(route: AppRoute) {
        if (route is TopLevelRoute) onActivate(route) else onAdd(route)
    }

    override fun set(route: AppRoute) = onSet(route)

    override fun activate(route: TopLevelRoute) = onActivate(route)
}
