package org.jetbrains.kotlinconf.ui

import android.app.*
import android.os.*
import android.view.*
import android.view.inputmethod.*
import androidx.core.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.*
import com.brandongogetap.stickyheaders.*
import com.brandongogetap.stickyheaders.exposed.*
import com.google.android.material.tabs.*
import kotlinx.android.synthetic.main.fragment_schedule.view.*
import org.jetbrains.kotlinconf.*
import org.jetbrains.kotlinconf.R
import org.jetbrains.kotlinconf.presentation.*
import org.jetbrains.kotlinconf.ui.details.*

class ScheduleController : Fragment() {
    private val schedule by lazy { ScheduleAdapter() }
    private val favorites by lazy { ScheduleAdapter() }
    private val search by lazy { SearchAdapter() }
    private var lastAdapter: RecyclerView.Adapter<*>? = null

    private lateinit var listView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        KotlinConf.service.schedule.watch {
            schedule.data = it
            schedule.notifyDataSetChanged()
        }

        KotlinConf.service.favoriteSchedule.watch {
            favorites.data = it
            favorites.notifyDataSetChanged()
        }

        KotlinConf.service.sessions.watch {
            search.data = it
            search.notifyDataSetChanged()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_schedule, container, false).apply {
        setupSchedule()
        setupTabs()
        setupSearch()
    }

    private fun View.setupTabs() {
        schedule_tabs.addOnTabSelectedListener(object :
            TabLayout.BaseOnTabSelectedListener<TabLayout.Tab> {
            override fun onTabSelected(tab: TabLayout.Tab) {
                displayTab(tab.position)
            }

            override fun onTabReselected(p0: TabLayout.Tab?) {}
            override fun onTabUnselected(p0: TabLayout.Tab?) {}
        })
    }

    private fun View.setupSearch() {
        search_button.setOnClickListener {
            startSearch()
        }
        search_cancel_button.setOnClickListener {
            val inputManager = context
                .getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

            inputManager.hideSoftInputFromWindow(this@setupSearch.windowToken, 0)
            stopSearch()
        }

        search_box.apply {
            setOnKeyListener { view, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    val inputManager = context
                        .getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

                    inputManager.hideSoftInputFromWindow(this@setupSearch.windowToken, 0)
                }

                false
            }
            addTextChangedListener {
                search.query = it!!.toString()
                search.notifyDataSetChanged()
            }
        }
    }

    private fun View.startSearch() {
        search_bar.visibility = View.VISIBLE
        tab_bar.visibility = View.GONE
        lastAdapter = listView.adapter
        listView.apply {
            listView.adapter = search
            layoutManager = StickyLayoutManager(context, search)
        }
    }

    private fun View.stopSearch() {
        search_bar.visibility = View.GONE
        tab_bar.visibility = View.VISIBLE
        search_box.setText("")
        search.query = ""
        search.notifyDataSetChanged()
        listView.apply {
            adapter = lastAdapter ?: schedule
            layoutManager = StickyLayoutManager(context, (lastAdapter as? StickyHeaderHandler) ?: schedule)
        }
    }

    private fun displayTab(id: Int) {
        listView.apply {
            when (id) {
                0 -> {
                    adapter = schedule
                    layoutManager = StickyLayoutManager(context, schedule)
                }
                else -> {
                    adapter = favorites
                    layoutManager = StickyLayoutManager(context, favorites)
                }
            }
        }
    }

    private fun View.setupSchedule() {
        listView = schedule_list.apply {
            adapter = schedule
            layoutManager = StickyLayoutManager(context, schedule)

            addItemDecoration(SessionCardDecoration())
            autoclear()
        }
    }

    internal inner class ScheduleAdapter : RecyclerView.Adapter<SessionCardHolder>(), StickyHeaderHandler {
        private var schedule: MutableList<ScheduleItem> = mutableListOf()

        var data: List<SessionGroup> = emptyList()
            set(value) {
                updateSchedule(value)
                field = value
            }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionCardHolder {
            val view = when (viewType) {
                ScheduleItem.TYPE_SMALL -> R.layout.view_schedule_header_small
                ScheduleItem.TYPE_LARGE -> R.layout.view_schedule_header_large
                else -> R.layout.view_schedule_session_card
            }

            val holder = layoutInflater.inflate(view, parent, false)
            return SessionCardHolder(holder)
        }

        override fun getItemCount(): Int = schedule.size

        override fun onBindViewHolder(holder: SessionCardHolder, position: Int) {
            holder.show(schedule[position])
        }

        override fun getItemViewType(position: Int): Int {
            return schedule[position].type
        }

        private fun updateSchedule(groups: List<SessionGroup>) {
            val result = mutableListOf<ScheduleItem>()
            for (group in groups) {
                if (group.daySection) {
                    result += ScheduleItem.SmallHeader(
                        group.title, R.color.dark_grey_40
                    )
                    continue
                }
                if (group.lunchSection) {
                    result += ScheduleItem.LargeHeader(group)
                    continue
                }

                result += ScheduleItem.LargeHeader(group)
                result += group.sessions.map { ScheduleItem.Card(it) }
            }

            schedule = result
        }

        override fun getAdapterData(): MutableList<*> = schedule
    }

    internal inner class SearchAdapter : RecyclerView.Adapter<SessionCardHolder>(), StickyHeaderHandler {
        var data: List<SessionCard> = emptyList()
            set(value) {
                field = value
                updateSearchResults()
            }

        var query: String = ""
            set(value) {
                field = value.toLowerCase()
                updateSearchResults()
            }

        private var searchResults: MutableList<SessionCard> = mutableListOf()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionCardHolder {
            val holder = layoutInflater.inflate(R.layout.view_schedule_session_card, parent, false)
            return SessionCardHolder(holder)
        }

        override fun getItemCount(): Int = searchResults.size

        override fun onBindViewHolder(holder: SessionCardHolder, position: Int) {
            holder.show(ScheduleItem.Card(searchResults[position]))
        }

        override fun getAdapterData(): MutableList<*> = searchResults

        private fun updateSearchResults() {
            val result = mutableListOf<SessionCard>().apply {
                addAll(
                    data.filter {
                        val speakers = it.speakers.joinToString { it.fullName.toLowerCase() }
                        val room = it.location.name.toLowerCase()
                        query in it.session.title.toLowerCase() || query in speakers || query in room
                    }
                )
            }

            searchResults = result
        }
    }
}

internal class SessionCardDecoration : RecyclerView.ItemDecoration()
