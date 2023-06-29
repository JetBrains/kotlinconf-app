package org.jetbrains.kotlinconf.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import org.jetbrains.kotlinconf.App

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        val service = ConferenceService(
//            ApplicationContext(
//                application,
//                R.mipmap.ic_launcher,
//            ),
//            "https://kotlin-conf-staging.labs.jb.gg/"
//        )
//        fun addOnBackCallback(block: OnBackPressedCallback) {
//            onBackPressedDispatcher.addCallback(this, block)
//        }

        setContent {
            App()
        }
    }
}
