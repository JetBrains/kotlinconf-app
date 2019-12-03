package org.jetbrains.kotlinconf.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.ktor.utils.io.core.Closeable
import kotlinx.android.synthetic.main.fragment_after.view.*
import kotlinx.android.synthetic.main.fragment_before.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.view_session_live_card.view.*
import org.jetbrains.kotlinconf.*
import org.jetbrains.kotlinconf.BuildConfig.YOUTUBE_API_KEY
import org.jetbrains.kotlinconf.presentation.SessionCard
import org.jetbrains.kotlinconf.ui.details.LiveCardHolder
import org.jetbrains.kotlinconf.ui.details.RemainderHolder
import org.jetbrains.kotlinconf.ui.details.TweetHolder
import kotlin.math.min

class HomeController : Fragment() {
    private val liveCards by lazy { LiveCardsAdapter() }
    private val reminders by lazy { RemaindersAdapter() }
    private val feed by lazy { FeedAdapter() }

    private lateinit var homeWatcher: Closeable
    private lateinit var liveWatcher: Closeable
    private lateinit var remindersWatcher: Closeable
    private lateinit var feedWatcher: Closeable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        liveWatcher = KotlinConf.service.liveSessions.watch {
            liveCards.data = it
            liveCards.notifyDataSetChanged()

            val visible = it.isNotEmpty()
            live_title?.isVisible = visible
            live_cards_container?.isVisible = visible
        }

        remindersWatcher = KotlinConf.service.upcomingFavorites.watch {
            reminders.data = it
            reminders.notifyDataSetChanged()

            val visible = it.isNotEmpty()
            dont_miss_title?.isVisible = visible
            dont_miss_block?.isVisible = visible
        }

        feedWatcher = KotlinConf.service.feed.watch {
            feed.data = it.statuses
            feed.notifyDataSetChanged()
        }

        var lastState: HomeState = KotlinConf.service.currentHomeState()
        homeWatcher = KotlinConf.service.homeState.watch { state ->
            if (state != lastState) {
                lastState = state
                clickHome()
                return@watch
            }

            if (state is HomeState.Before) {
                setTimerState(state)
            }

            lastState = state
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        liveWatcher.close()
        remindersWatcher.close()
        feedWatcher.close()
        homeWatcher.close()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = when (KotlinConf.service.currentHomeState()) {
        is HomeState.After -> {
            inflater.inflate(
                R.layout.fragment_after, container, false
            ).apply {
                after_description_view?.apply {
                    text = Html.fromHtml(getString(R.string.after_description))
                    setOnClickListener {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://kotlinconf.com"))
                        startActivity(intent)
                    }
                }
            }
        }
        is HomeState.During -> {
            inflater.inflate(
                R.layout.fragment_home, container, false
            ).apply {
                setupLiveCards()
                setupRemainders()
                setupTwitter()
                setupPartners()
                setupLocator()
                setupVoteMeter()
            }
        }
        is HomeState.Before -> {
            inflater.inflate(
                R.layout.fragment_before, container, false
            )
        }
    }

    private fun View.setupLiveCards() {
        live_cards_container.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false).apply {
                isNestedScrollingEnabled = true
            }
            adapter = liveCards
        }
    }

    private fun clickHome() {
        activity?.findNavController(R.id.nav_host_fragment)?.apply {
            popBackStack()
            navigate(R.id.navigation_home)
        }
    }

    private fun setTimerState(state: HomeState.Before) {
        with(state) {
            before_days?.text = days.toString()
            before_hours?.text = hours.toString()
            before_minutes?.text = minutes.toString()
            before_seconds?.text = seconds.toString()
        }
    }

    private fun View.setupRemainders() {
        dont_miss_block.apply {
            layoutManager = LinearLayoutManager(context)
            isNestedScrollingEnabled = false
            adapter = reminders
        }
    }

    private fun View.setupTwitter() {
        tweet_feed_list.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false).apply {
                isNestedScrollingEnabled = true
            }
            adapter = feed
        }
    }

    private fun View.setupPartners() {
        listOf(
            button_android,
            button_47,
            button_bitrise,
            button_freenow,
            button_instill,
            button_gradle,
            button_n26,
            button_kodein,
            button_badoo
        ).forEach {
            it.setOnClickListener {
                showActivity<PartnerActivity> {
                    putExtra("partner", it.tag.toString())
                }
            }
        }
    }

    private fun View.setupLocator() {
        locator_link.setOnClickListener {
            val url = Uri.parse("https://play.google.com/store/apps/details?id=org.jetbrains.kotlin.locator")
            val intent = Intent(Intent.ACTION_VIEW, url)
            startActivity(intent)
        }
    }

    private fun View.setupVoteMeter() {
        KotlinConf.service.votes.watch {
            val size = it.size
            val progress = min(100.0, 100.0 * size / KotlinConf.service.votesCountRequired()).toInt()

            vote_meter.progress = progress
            val finished = if (progress == 100) View.VISIBLE else View.INVISIBLE
            vote_done.visibility = finished
            vote_done_description.visibility = finished
        }
    }

    inner class LiveCardsAdapter(
        var data: List<SessionCard> = emptyList()
    ) : RecyclerView.Adapter<LiveCardHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LiveCardHolder {
            val view = layoutInflater.inflate(
                R.layout.view_session_live_card, parent, false
            ).apply {
                live_video_view.initialize(YOUTUBE_API_KEY, LiveCardHolder)
            }

            return LiveCardHolder(view)
        }

        override fun getItemCount(): Int = data.size

        override fun onBindViewHolder(holder: LiveCardHolder, position: Int) {
            holder.setupCard(data[position])
        }
    }

    inner class RemaindersAdapter(
        var data: List<SessionCard> = emptyList()
    ) : RecyclerView.Adapter<RemainderHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RemainderHolder {
            val view = layoutInflater.inflate(R.layout.view_dont_miss_card, parent, false)
            return RemainderHolder(view)
        }

        override fun getItemCount(): Int = data.size

        override fun onBindViewHolder(holder: RemainderHolder, position: Int) {
            holder.setupCard(data[position])
        }
    }

    inner class FeedAdapter(
        var data: List<FeedPost> = emptyList()
    ) : RecyclerView.Adapter<TweetHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TweetHolder {
            val view = layoutInflater.inflate(R.layout.view_tweet_card, parent, false)
            return TweetHolder(view)
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun onBindViewHolder(holder: TweetHolder, position: Int) {
            holder.showPost(data[position])
        }
    }
}
