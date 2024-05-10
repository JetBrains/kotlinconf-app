package org.jetbrains.kotlinconf.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.jetbrains.kotlinconf.R
import org.jetbrains.kotlinconf.App
import org.jetbrains.kotlinconf.ApplicationContext

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val context = ApplicationContext(
            application,
            R.mipmap.ic_launcher,
        )

        setContent {
            App(context)
        }
    }
}
