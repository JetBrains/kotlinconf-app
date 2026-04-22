package org.jetbrains.kotlinconf

import androidx.compose.ui.uikit.OnFocusBehavior
import androidx.compose.ui.window.ComposeUIViewController
import org.jetbrains.kotlinconf.flags.Flags
import org.jetbrains.kotlinconf.utils.Logger
import platform.Foundation.NSLog
import platform.UIKit.UIViewController

@Suppress("unused") // Called from Swift
fun initApp() = initApp(
    platformLogger = IOSLogger(),
    platformFlags = Flags(
        enableBackOnTopLevelScreens = false,
        rippleEnabled = false,
        hideKeyboardOnDrag = true,
    )
)

class IOSLogger : Logger {
    override fun log(tag: String, lazyMessage: () -> String) {
        NSLog("[$tag] ${lazyMessage()}")
    }
}

@Suppress("unused") // Called from Swift
fun MainViewController(): UIViewController = ComposeUIViewController(
    configure = { onFocusBehavior = OnFocusBehavior.DoNothing },
) {
    App()
}
