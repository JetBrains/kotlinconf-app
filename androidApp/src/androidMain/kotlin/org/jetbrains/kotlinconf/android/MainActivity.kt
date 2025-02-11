package org.jetbrains.kotlinconf.android

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.jetbrains.kotlinconf.R
import org.jetbrains.kotlinconf.App
import org.jetbrains.kotlinconf.platformModule

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()

        val platformModule = platformModule(
            activity = this,
            application = application,
            notificationIconId = R.mipmap.ic_launcher,
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
}
