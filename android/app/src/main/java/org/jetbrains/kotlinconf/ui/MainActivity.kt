package org.jetbrains.kotlinconf.ui

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import org.jetbrains.kotlinconf.R
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.frameLayout

class MainActivity :
        AppCompatActivity(),
        AnkoComponent<Context>,
        NavigationManager,
        SearchQueryProvider,
        AnkoLogger {

    private var _searchQuery: String = ""
    override val searchQuery: String
        get() = _searchQuery

    private val queryTextChangedListeners: MutableList<(String) -> Unit> = mutableListOf()

    override fun addOnQueryChangedListener(listener: (String) -> Unit) {
        queryTextChangedListeners.add(listener)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(createView(AnkoContext.create(this)))

        if (savedInstanceState == null) {
            showSessionList()
        }
        else {
            savedInstanceState.getString(SEARCH_QUERY_KEY)?.let { _searchQuery = it }
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putString(SEARCH_QUERY_KEY, _searchQuery)
    }

    override fun createView(ui: AnkoContext<Context>): View = with(ui) {
        frameLayout {
            id = R.id.fragment_container
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val searchViewMenuItem = menu.findItem(R.id.search)
        val searchView = searchViewMenuItem.actionView as SearchView
        if (_searchQuery.isNotEmpty()) {
            searchView.setQuery(_searchQuery, false)
            searchView.isIconified = false
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean = false
            override fun onQueryTextChange(newText: String): Boolean {
                queryTextChangedListeners.forEach { it.invoke(newText) }
                _searchQuery = newText
                return true
            }
        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_info -> showInfo()
            android.R.id.home -> supportFragmentManager.popBackStack()
        }
        return true
    }

    private fun showInfo() {
        if (supportFragmentManager.findFragmentByTag(InfoFragment.TAG) != null)
            return

        supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.enter_from_right,
                        R.anim.exit_to_left,
                        R.anim.enter_from_left,
                        R.anim.exit_to_right
                )
                .addToBackStack("Info")
                .replace(R.id.fragment_container, InfoFragment(), InfoFragment.TAG)
                .commit()
    }

    override fun showSessionList() {
        supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, SessionPagerFragment(), SessionListFragment.TAG)
                .commit()
    }

    override fun showSessionDetails(sessionId: String) {
        val fragment = SessionDetailsFragment.forSession(sessionId)
        supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.enter_from_right,
                        R.anim.exit_to_left,
                        R.anim.enter_from_left,
                        R.anim.exit_to_right
                )
                .addToBackStack("Session")
                .replace(R.id.fragment_container, fragment, SessionDetailsFragment.TAG)
                .commit()
    }

    companion object {
        private const val SEARCH_QUERY_KEY = "SearchQuery"
    }
}

