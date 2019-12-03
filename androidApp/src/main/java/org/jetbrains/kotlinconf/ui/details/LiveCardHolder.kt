package org.jetbrains.kotlinconf.ui.details

import android.view.*
import androidx.recyclerview.widget.*
import com.google.android.youtube.player.*
import io.ktor.utils.io.core.*
import kotlinx.android.synthetic.main.view_session_live_card.view.*
import org.jetbrains.kotlinconf.*
import org.jetbrains.kotlinconf.presentation.*
import org.jetbrains.kotlinconf.showActivity
import org.jetbrains.kotlinconf.ui.*

class LiveCardHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    private var favoriteSubscription: Closeable? = null
    private var liveSubscription: Closeable? = null

    fun setupCard(sessionCard: SessionCard) {
        favoriteSubscription?.close()
        liveSubscription?.close()

        with(view) {
            setOnTouchListener { view, event ->
                val action = event.action

                view.live_card.setPressedColor(
                    event,
                    R.color.dark_grey_card,
                    R.color.dark_grey_card_pressed
                )
                if (action == MotionEvent.ACTION_UP) {
                    showActivity<SessionActivity> {
                        putExtra("session", sessionCard.session.id)
                    }
                }

                true
            }

            liveSubscription = sessionCard.isLive.watch {
                live_video_view.tag = it
            }
            live_session_title.text = sessionCard.session.displayTitle
            live_session_speakers.text = sessionCard.speakers.joinToString { it.fullName }

            live_location.text = sessionCard.location.displayName(sessionCard.session.isWorkshop())
            live_favorite.setOnClickListener {
                KotlinConf.service.markFavorite(sessionCard.session.id)
            }

            live_time.text = "${KotlinConf.service.minutesLeft(sessionCard)} minutes left"

            favoriteSubscription = sessionCard.isFavorite.watch {
                val image = if (it) {
                    R.drawable.favorite_orange
                } else {
                    R.drawable.favorite_white_empty
                }

                live_favorite.setImageResource(image)
            }
        }
    }

    companion object ThumbnailListener : YouTubeThumbnailView.OnInitializedListener {
        override fun onInitializationFailure(
            view: YouTubeThumbnailView,
            loader: YouTubeInitializationResult
        ) {
        }

        override fun onInitializationSuccess(
            view: YouTubeThumbnailView,
            loader: YouTubeThumbnailLoader
        ) {
            val tag = view.tag as? String ?: return
            loader.setVideo(tag)
        }
    }
}