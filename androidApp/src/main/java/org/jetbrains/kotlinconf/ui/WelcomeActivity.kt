package org.jetbrains.kotlinconf.ui

import android.content.*
import android.net.*
import android.os.*
import android.text.*
import android.view.*
import android.view.View.*
import androidx.appcompat.app.*
import androidx.fragment.app.*
import kotlinx.android.synthetic.main.activity_welcome.*
import kotlinx.android.synthetic.main.fragment_notifications.view.*
import kotlinx.android.synthetic.main.fragment_privacy.view.*
import org.jetbrains.kotlinconf.*
import org.jetbrains.kotlinconf.ui.details.*

internal class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val page: String? = intent.getStringExtra("page")

        val pages = listOf(
            PrivacyPolicyFragment.name,
            NotificationFragment.name
        )

        val pageIndex = page?.let { pages.indexOf(it) }

        val fragments = listOf(
            PrivacyPolicyFragment(),
            NotificationFragment()
        )

        val show = if (pageIndex == null) fragments else listOf(fragments[pageIndex])

        val adapter = PagerAdapter(show, supportFragmentManager)
        welcome_pager.adapter = adapter

        welcome_tab_dots.setupWithViewPager(welcome_pager)
        welcome_tab_dots.visibility = if (page == null) VISIBLE else INVISIBLE
    }
}

internal class PrivacyPolicyFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_privacy, container, false).apply {
        privacy_policy_text.text = Html.fromHtml(getString(R.string.privacy_description))

        privacy_policy_text.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.jetbrains.com/company/privacy.html"))
            startActivity(intent)
        }

        welcome_privacy_accept.setOnClickListener {
            KotlinConf.service.acceptPrivacyPolicy()
            activity!!.welcome_pager.currentItem += 1
        }

        welcome_privacy_next.setOnClickListener {
            activity!!.welcome_pager.currentItem += 1
        }
    }

    companion object {
        val name: String = "privacy policy"
    }
}

internal class NotificationFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_notifications, container, false).apply {
        welcome_notifications_allow.setOnClickListener {
            KotlinConf.service.requestNotificationPermissions()
            it.isEnabled = false
            activity!!.finish()
        }

        welcome_close.setOnClickListener {
            activity!!.finish()
        }
    }

    companion object {
        val name: String = "notification"
    }
}
