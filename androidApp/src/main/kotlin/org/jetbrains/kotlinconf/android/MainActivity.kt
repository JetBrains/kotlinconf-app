package org.jetbrains.kotlinconf.android

import android.os.*
import androidx.activity.*
import androidx.activity.compose.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.ui.*
import com.jetbrains.kotlinconf.R
import org.jetbrains.kotlinconf.*
import org.jetbrains.kotlinconf.android.theme.*
import org.jetbrains.kotlinconf.android.ui.*
import org.jetbrains.kotlinconf.storage.*

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val service = ConferenceService(
            ApplicationContext(
                application,
                R.mipmap.ic_launcher,
            ),
            "https://kotlin-conf-staging.labs.jb.gg/"
        )

        fun addOnBackCallback(block: OnBackPressedCallback) {
            onBackPressedDispatcher.addCallback(this, block)
        }

        setContent {
            KotlinConfTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainScreen(service, ::addOnBackCallback)
                }
            }
        }
    }
}
