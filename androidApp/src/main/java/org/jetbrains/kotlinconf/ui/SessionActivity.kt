package org.jetbrains.kotlinconf.ui

import android.content.*
import android.os.*
import android.view.*
import android.widget.*
import androidx.appcompat.app.*
import androidx.core.view.*
import com.google.android.youtube.player.*
import io.ktor.utils.io.core.*
import kotlinx.android.synthetic.main.activity_session.*
import org.jetbrains.kotlinconf.*
import org.jetbrains.kotlinconf.BuildConfig.*
import org.jetbrains.kotlinconf.presentation.*

class SessionActivity : AppCompatActivity() {
    private var favoriteWatcher: Closeable? = null
    private var ratingWatcher: Closeable? = null
    private var liveWatcher: Closeable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_session)
        val sessionId = intent.getStringExtra("session")
        showSession(sessionId)
    }

    override fun onDestroy() {
        super.onDestroy()

        favoriteWatcher?.close()
        ratingWatcher?.close()
        liveWatcher?.close()
    }

    private fun showSession(id: String) {
        val card = KotlinConf.service.sessionCard(id)
        val session = card.session

        session_title.text = session.displayTitle.toUpperCase()

        val speakers = card.speakers

        fun showSpeaker(
            speaker: SpeakerData?,
            name: TextView,
            icon: ImageView,
            divider: ImageView
        ) {
            name.apply {
                isVisible = speaker != null
                if (speaker != null) {
                    text = speaker.fullName

                    setOnClickListener {
                        showActivity<SpeakerActivity> {
                            putExtra("speaker", speaker.id)
                        }
                    }
                }
            }

            icon.isVisible = speaker != null
            divider.isVisible = speaker != null
        }

        val speaker = if (speakers.isNotEmpty()) speakers[0] else null
        showSpeaker(speaker, speaker_1_name, session_human_1, session_divider_11)
        val speaker1 = if (speakers.size >= 2) speakers[1] else null
        showSpeaker(speaker1, speaker_2_name, session_human_2, session_divider_2)

        val isWorkshop = session.isWorkshop()
        session_location_text.text = card.location.displayName(isWorkshop)
        session_description.text = session.descriptionText
        session_time_label.text = "${card.date} ${card.time}"

        favoriteWatcher = card.isFavorite.watch {
            val image = if (it) {
                R.drawable.favorite_white
            } else {
                R.drawable.favorite_white_empty
            }

            session_favorite.setImageResource(image)
        }

        ratingWatcher = card.ratingData.watch { rating ->
            session_vote_good.setImageResource(if (rating == RatingData.GOOD) R.drawable.good_orange else R.drawable.good_empty)
            session_vote_ok.setImageResource(if (rating == RatingData.OK) R.drawable.ok_orange else R.drawable.ok_empty)
            session_vote_bad.setImageResource(if (rating == RatingData.BAD) R.drawable.bad_orange else R.drawable.bad_empty)

            val voteResource = when (rating) {
                RatingData.GOOD -> R.drawable.good_white
                RatingData.OK -> R.drawable.ok_white
                RatingData.BAD -> R.drawable.bad_white
                else -> R.drawable.good_white_empty
            }

            session_vote.setImageResource(voteResource)
        }

        val sessionId = card.session.id

        fun vote(rating: RatingData) {
            session_vote_popup.visibility = View.INVISIBLE
            KotlinConf.service.vote(sessionId, rating)
        }

        session_vote.setOnClickListener { session_vote_popup.visibility = View.VISIBLE }
        session_vote_good.setOnClickListener { vote(RatingData.GOOD) }
        session_vote_ok.setOnClickListener { vote(RatingData.OK) }
        session_vote_bad.setOnClickListener { vote(RatingData.BAD) }

        session_favorite.setOnClickListener { KotlinConf.service.markFavorite(sessionId) }

        session_share.setOnClickListener {
            val sharingIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Share")
                putExtra(Intent.EXTRA_TEXT, card.session.url)
            }

            startActivity(Intent.createChooser(sharingIntent, "Share"))
        }

        session_main.setOnTouchListener { _, _ ->
            session_vote_popup.isVisible = false
            false
        }
        val videoView = fragmentManager
            .findFragmentById(R.id.session_video_view) as LiveVideoFragment

        liveWatcher = card.isLive.watch {
            if (it != null) {
                session_video_box.visibility = View.VISIBLE
                videoView.showVideo(it)
            } else {
                session_video_box.visibility = View.GONE
            }
        }
    }

}

class LiveVideoFragment : YouTubePlayerFragment(), YouTubePlayer.OnInitializedListener {
    private var player: YouTubePlayer? = null
    private var videoId: String? = null

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        if (YOUTUBE_API_KEY.isNotBlank()) {
            initialize(YOUTUBE_API_KEY, this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player = null
    }

    override fun onInitializationSuccess(
        provider: YouTubePlayer.Provider,
        newPlayer: YouTubePlayer,
        restored: Boolean
    ) {
        player = newPlayer
        newPlayer.setPlayerStateChangeListener(object : YouTubePlayer.PlayerStateChangeListener {
            override fun onAdStarted() {
            }

            override fun onLoading() {
            }

            override fun onVideoStarted() {
            }

            override fun onLoaded(p0: String?) {
            }

            override fun onVideoEnded() {
            }

            override fun onError(p0: YouTubePlayer.ErrorReason?) {
                print(p0)
            }

        })
        videoId?.let { newPlayer.loadVideo(it) }
    }

    fun showVideo(id: String) {
        videoId = id
        player?.loadVideo(id)
    }

    override fun onInitializationFailure(
        provider: YouTubePlayer.Provider,
        result: YouTubeInitializationResult
    ) {
    }
}
