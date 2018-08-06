package org.jetbrains.kotlinconf.ui

import android.arch.lifecycle.LiveData
import android.content.Context
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.AppBarLayout.LayoutParams.*
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.jetbrains.kotlinconf.R
import org.jetbrains.kotlinconf.SessionModel
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.design.tabLayout
import org.jetbrains.anko.design.themedAppBarLayout
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.support.v4.viewPager
import org.jetbrains.anko.wrapContent

class SessionPagerFragment : Fragment(), AnkoComponent<Context> {

    private lateinit var toolbar: Toolbar
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (activity as AppCompatActivity).apply {
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            supportActionBar?.setDisplayShowTitleEnabled(false)
            supportActionBar?.setLogo(R.mipmap.kotlinconf_logo_text)
        }

        viewPager.adapter = object : FragmentPagerAdapter(childFragmentManager) {
            val fragments = listOf(AllSessionsFragment(), FavoriteSessionsFragment())
            override fun getPageTitle(position: Int): CharSequence = fragments[position].title
            override fun getItem(position: Int): Fragment = fragments[position]
            override fun getCount(): Int = fragments.size
        }

        tabLayout.setupWithViewPager(viewPager)
    }

    override fun onCreateView(
            inflater: LayoutInflater?,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return createView(AnkoContext.create(context))
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

    class AllSessionsFragment : SessionListFragment() {
        override val title: String = "All"
        override fun getSessions(model: SessionListViewModel): LiveData<List<SessionModel>> {
            return model.sessions
        }
    }

    class FavoriteSessionsFragment : SessionListFragment() {
        override val title: String = "Favorites"
        override fun getSessions(model: SessionListViewModel): LiveData<List<SessionModel>> {
            return model.favorites
        }
    }
}