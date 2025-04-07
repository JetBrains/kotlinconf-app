package org.jetbrains.kotlinconf.android

import android.content.Intent
import android.graphics.Color
import android.os.Build
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
import com.mmk.kmpnotifier.extensions.onCreateOrOnNewIntent
import com.mmk.kmpnotifier.notification.NotifierManager
import org.jetbrains.kotlinconf.App
import org.jetbrains.kotlinconf.EXTRA_LOCAL_NOTIFICATION_ID
import org.jetbrains.kotlinconf.PermissionHandler
import org.jetbrains.kotlinconf.navigation.navigateByLocalNotificationId
import org.koin.mp.KoinPlatform

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        processIntent(intent)

        KoinPlatform.getKoin().declare(PermissionHandler(activity = this))

        setContent {
            App(
                onThemeChange = { isDarkMode ->
                    val systemBarStyle = SystemBarStyle.auto(
                        lightScrim = Color.TRANSPARENT,
                        darkScrim = Color.TRANSPARENT,
                        detectDarkMode = { isDarkMode }
                    )
                    enableEdgeToEdge(
                        statusBarStyle = systemBarStyle,
                        navigationBarStyle = systemBarStyle,
                    )
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        // Don't enforce scrim https://issuetracker.google.com/issues/298296168
                        window.isNavigationBarContrastEnforced = false
                    }
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
        if (intent == null) return

        val notificationId = intent.getStringExtra(EXTRA_LOCAL_NOTIFICATION_ID)
        if (notificationId != null) {
            // Local notification clicked
            navigateByLocalNotificationId(notificationId)
            return
        }

        // Process push notifications
        NotifierManager.onCreateOrOnNewIntent(intent)
    }
}
