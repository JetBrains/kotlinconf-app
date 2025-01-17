package org.jetbrains.kotlinconf.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.jetbrains.kotlinconf.R
import org.jetbrains.kotlinconf.App
import org.jetbrains.kotlinconf.ApplicationContext

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        val context = ApplicationContext(
            application,
            R.mipmap.ic_launcher,
        )

        setContent {
            Box(Modifier.windowInsetsPadding(WindowInsets.systemBars)) {
                App(context)
            }
        }
    }
}
