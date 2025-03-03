package org.jetbrains.kotlinconf.android

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.jetbrains.kotlinconf.R
import org.jetbrains.kotlinconf.App
import org.jetbrains.kotlinconf.EXTRA_NOTIFICATION_ID
import org.jetbrains.kotlinconf.navigation.navigateToSession
import org.jetbrains.kotlinconf.platformModule

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()

        processIntent(intent)

        val platformModule = platformModule(
            activity = this,
            application = application,
            notificationIconId = R.drawable.kotlinconf_notification_icon,
        )
        setContent {
            App(
                platformModule = platformModule,
                onThemeChange = { isDarkMode ->
                    enableEdgeToEdge(
                        statusBarStyle = SystemBarStyle.auto(
                            lightScrim = Color.TRANSPARENT,
                            darkScrim = Color.TRANSPARENT,
                            detectDarkMode = { isDarkMode }
                        )
                    )
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
