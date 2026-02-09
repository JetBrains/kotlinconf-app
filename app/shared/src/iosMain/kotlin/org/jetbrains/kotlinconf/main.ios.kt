package org.jetbrains.kotlinconf

import androidx.compose.ui.uikit.OnFocusBehavior
import androidx.compose.ui.window.ComposeUIViewController
import dev.zacsweers.metro.createGraphFactory
import org.jetbrains.kotlinconf.di.IosAppGraph
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
            enableBackOnMainScreens = false,
            rippleEnabled = false,
            hideKeyboardOnDrag = true,
        )
    )

@Suppress("unused") // Called from Swift
fun MainViewController(): UIViewController = ComposeUIViewController(
    configure = { onFocusBehavior = OnFocusBehavior.DoNothing },
) {
    App(appGraph)
}
