package org.jetbrains.kotlinconf

import androidx.compose.ui.uikit.OnFocusBehavior
import androidx.compose.ui.window.ComposeUIViewController
import dev.zacsweers.metro.createGraphFactory
import org.jetbrains.kotlinconf.di.IosAppGraph
import org.jetbrains.kotlinconf.flags.Flags
import org.jetbrains.kotlinconf.navigation.AppRoute
import org.jetbrains.kotlinconf.navigation.ExternalNavigator
import org.jetbrains.kotlinconf.navigation.TopLevelRoute
import org.jetbrains.kotlinconf.utils.Logger
import platform.Foundation.NSLog
import platform.UIKit.UIViewController

@Suppress("unused") // Called from Swift
fun initApp() = initApp(
    appGraph = appGraph,
    platformLogger = IOSLogger(),
)

class IOSLogger : Logger {
    override fun log(tag: String, lazyMessage: () -> String) {
        NSLog("[$tag] ${lazyMessage()}")
    }
}

private val appGraph = createGraphFactory<IosAppGraph.Factory>()
    .create(
        Flags(
            enableBackOnTopLevelScreens = false,
            rippleEnabled = false,
            hideKeyboardOnDrag = true,
        )
    )

@Suppress("unused") // Called from Swift
fun MainViewController(topLevelRoute: TopLevelRoute): UIViewController = ComposeUIViewController(
    configure = { onFocusBehavior = OnFocusBehavior.DoNothing },
) {
    App(appGraph, topLevelRoute)
}

@Suppress("unused") // Called from Swift
fun MainViewController(
    topLevelRoute: TopLevelRoute,
    onNavigate: (AppRoute) -> Unit,
    onGoBack: () -> Unit,
    onSet: (AppRoute) -> Unit,
    onActivate: (TopLevelRoute) -> Unit,
): UIViewController = ComposeUIViewController(
    configure = { onFocusBehavior = OnFocusBehavior.DoNothing },
) {
    App(
        appGraph = appGraph,
        topLevelRoute = topLevelRoute,
        navigatorFactory = { navState, topLevelBackEnabled ->
            ExternalNavigator(
                state = navState,
                topLevelBackEnabled = topLevelBackEnabled,
                onAdd = onNavigate,
                onGoBack = onGoBack,
                onSet = onSet,
                onActivate = onActivate,
            )
        },
    )
}

@Suppress("unused") // Called from Swift
fun ScreenViewController(
    route: AppRoute,
    onNavigate: (AppRoute) -> Unit,
    onGoBack: () -> Unit,
    onSet: (AppRoute) -> Unit,
    onActivate: (TopLevelRoute) -> Unit,
): UIViewController = ComposeUIViewController(
    configure = { onFocusBehavior = OnFocusBehavior.DoNothing },
) {
    SingleScreenApp(
        appGraph = appGraph,
        route = route,
        onNavigate = onNavigate,
        onGoBack = onGoBack,
        onSet = onSet,
        onActivate = onActivate,
    )
}
