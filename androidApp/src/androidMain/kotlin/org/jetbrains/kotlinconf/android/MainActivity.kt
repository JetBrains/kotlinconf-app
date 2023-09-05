package org.jetbrains.kotlinconf.android

import android.os.Bundle
import com.jetbrains.kotlinconf.R
import moe.tlaster.precompose.lifecycle.PreComposeActivity
import moe.tlaster.precompose.lifecycle.setContent
import org.jetbrains.kotlinconf.App
import org.jetbrains.kotlinconf.ApplicationContext

class MainActivity : PreComposeActivity() {

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
