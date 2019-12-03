package org.jetbrains.kotlinconf.ui.details

import android.view.*
import androidx.core.view.*
import androidx.recyclerview.widget.*
import io.ktor.utils.io.core.*
import kotlinx.android.synthetic.main.view_dont_miss_card.view.*
import org.jetbrains.kotlinconf.*
import org.jetbrains.kotlinconf.presentation.*
import org.jetbrains.kotlinconf.showActivity
import org.jetbrains.kotlinconf.ui.*

class RemainderHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    private var favoriteSubscription: Closeable? = null
    private var liveSubscription: Closeable? = null

    fun setupCard(sessionCard: SessionCard) {
        favoriteSubscription?.close()
        liveSubscription?.close()

        with(view) {
            setOnTouchListener { view, event ->
                view.speaker_card_remainder.setPressedColor(
                    event,
                    R.color.dark_grey_card,
                    R.color.dark_grey_card_pressed
                )

                if (event.action == MotionEvent.ACTION_UP) {
                    showActivity<SessionActivity> {
                        putExtra("session", sessionCard.session.id)
                    }
                }
                true
            }

            card_session_title.text = sessionCard.session.displayTitle
            card_session_speakers.text = sessionCard.speakers.joinToString { it.fullName }
            val isWorkshop = sessionCard.session.isWorkshop()
            card_location_label.text = sessionCard.location.displayName(isWorkshop)

            card_favorite_button.setOnClickListener {
                KotlinConf.service.markFavorite(sessionCard.session.id)
            }

            favoriteSubscription = sessionCard.isFavorite.watch {
                val image = if (it) {
                    R.drawable.favorite_orange
                } else {
                    R.drawable.favorite_white_empty
                }
                card_favorite_button.setImageResource(image)
            }

            liveSubscription = sessionCard.isLive.watch {
                card_live_label.text = if (it != null) "Live now" else sessionCard.displayTime()
                card_live_icon.isVisible = it != null
            }
        }
    }
}
