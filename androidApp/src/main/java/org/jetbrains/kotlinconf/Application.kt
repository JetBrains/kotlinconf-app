package org.jetbrains.kotlinconf

import android.app.*
import android.content.*
import androidx.multidex.*
import com.google.firebase.*
import com.google.firebase.analytics.*

class KotlinConf : Application() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(base)
    }

    companion object {
        @Volatile
        lateinit var service: ConferenceService
    }
}
