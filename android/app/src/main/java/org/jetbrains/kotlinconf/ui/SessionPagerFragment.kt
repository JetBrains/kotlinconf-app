package org.jetbrains.kotlinconf.ui

import android.content.*
import android.os.*
import android.support.design.widget.*
import android.support.design.widget.AppBarLayout.LayoutParams.*
import android.support.v4.app.*
import android.support.v4.view.*
import android.support.v7.app.*
import android.support.v7.widget.*
import android.view.*
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.design.*
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.support.v4.*
import org.jetbrains.anko.wrapContent
import org.jetbrains.kotlinconf.*

class SessionPagerFragment : Fragment(), AnkoComponent<Context> {

    private lateinit var toolbar: Toolbar
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setUpActionBar()

        viewPager.adapter = object : FragmentPagerAdapter(childFragmentManager) {
            val fragments = listOf(AllSessionsFragment(), FavoriteSessionsFragment())
            override fun getPageTitle(position: Int): CharSequence = fragments[position].title
            override fun getItem(position: Int): Fragment = fragments[position]
            override fun getCount(): Int = fragments.size
        }

        tabLayout.setupWithViewPager(viewPager)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return createView(AnkoContext.create(context!!))
    }

    override fun createView(ui: AnkoContext<Context>): View = with(ui) {
        coordinatorLayout {
            lparams(width = matchParent, height = matchParent)
            themedAppBarLayout(R.style.ThemeOverlay_AppCompat_Dark_ActionBar) {
                toolbar = toolbar().lparams(width = matchParent, height = wrapContent) {
                    scrollFlags = SCROLL_FLAG_SCROLL or SCROLL_FLAG_ENTER_ALWAYS or SCROLL_FLAG_SNAP
                }
                tabLayout = tabLayout().lparams(width = matchParent, height = wrapContent) {
                    scrollFlags = 0
                }
            }.lparams(width = matchParent, height = wrapContent)

            viewPager = viewPager {
                id = R.id.sessions_view_pager
            }.lparams {
                behavior = AppBarLayout.ScrollingViewBehavior()
            }
        }
    }

    private fun setUpActionBar() {
        (activity as AppCompatActivity).apply {
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            supportActionBar?.setDisplayShowTitleEnabled(false)
            supportActionBar?.setLogo(R.drawable.kotlinconf_logo_text)
        }
    }

    class AllSessionsFragment : SessionListFragment() {
        override val title: String = "All"
        override fun onUpdate(sessions: List<SessionModel>, favorites: List<SessionModel>) {
            sessionsAdapter.sessions = sessions
        }
    }

    class FavoriteSessionsFragment : SessionListFragment() {
        override val title: String = "Favorites"
        override fun onUpdate(sessions: List<SessionModel>, favorites: List<SessionModel>) {
            sessionsAdapter.sessions = favorites
        }
    }
}