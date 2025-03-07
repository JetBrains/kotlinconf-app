package org.jetbrains.kotlinconf.android

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import org.jetbrains.kotlinconf.App
import org.jetbrains.kotlinconf.EXTRA_NOTIFICATION_ID
import org.jetbrains.kotlinconf.PermissionHandler
import org.jetbrains.kotlinconf.navigation.navigateToSession
import org.koin.dsl.module
import org.koin.mp.KoinPlatformTools

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()

        processIntent(intent)

        val permissionHandler = PermissionHandler(activity = this)
        val activityModule = module {
            single<PermissionHandler> { permissionHandler }
        }
        KoinPlatformTools.defaultContext()
            .get()
            .loadModules(listOf(activityModule))

        setContent {
            App(
                onThemeChange = { isDarkMode ->
                    enableEdgeToEdge(
                        statusBarStyle = SystemBarStyle.auto(
                            lightScrim = Color.TRANSPARENT,
                            darkScrim = Color.TRANSPARENT,
                            detectDarkMode = { isDarkMode }
                        )
                    )
                },
                popEnterTransition = {
                    scaleIn(initialScale = 1.05f) +
                            fadeIn(animationSpec = tween(50))
                },
                popExitTransition = {
                    scaleOut(targetScale = 0.9f, animationSpec = tween(50)) +
                            fadeOut(animationSpec = tween(50, delayMillis = 50))
                },
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        processIntent(intent)
    }

    private fun processIntent(intent: Intent?) {
        val notificationId = intent?.getStringExtra(EXTRA_NOTIFICATION_ID)
        if (notificationId != null) {
            navigateToSession(notificationId)
        }
    }
}
