package org.jetbrains.kotlinconf.ui

import android.os.*
import android.widget.*
import androidx.appcompat.app.*
import androidx.navigation.*
import androidx.navigation.ui.*
import com.google.firebase.analytics.*
import com.mapbox.mapboxsdk.*
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.kotlinconf.*
import org.jetbrains.kotlinconf.BuildConfig.*
import org.jetbrains.kotlinconf.R
import org.jetbrains.kotlinconf.storage.*
import java.io.*
import java.net.*

class MainActivity : AppCompatActivity() {
    private lateinit var errorsWatcher: Closeable
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val context = ApplicationContext(this, R.drawable.notification_icon)
        KotlinConf.service = ConferenceService(context)
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, null)

        errorsWatcher = KotlinConf.service.errors.watch { cause ->
            when (cause) {
                is Unauthorized -> showActivity<WelcomeActivity> {
                    putExtra("page", PrivacyPolicyFragment.name)
                }
                is TooEarlyVote -> {
                    Toast.makeText(this, "You cannot rate the session before it starts.", Toast.LENGTH_LONG).show()
                }
                is TooLateVote -> {
                    Toast.makeText(this, "Rating is only permitted up to 2 hours after the session end.", Toast.LENGTH_LONG).show()
                }
                is ConnectException -> {
                    Toast.makeText(this, "Failed to get data from server, please check your internet connection.", Toast.LENGTH_LONG).show()
                }
            }
        }

        setContentView(R.layout.activity_main)
        setupNavigationBar()

        if (KotlinConf.service.isFirstLaunch()) {
            showActivity<WelcomeActivity>()
        }
    }

    override fun onDestroy() {
        errorsWatcher.close()
        super.onDestroy()
    }

    private fun setupNavigationBar() {
        val controller = findNavController(R.id.nav_host_fragment)
        bottom_navigation.setupWithNavController(controller)

        Mapbox.getInstance(
            this, MAPBOX_ACCESS_TOKEN
        )
    }
}
