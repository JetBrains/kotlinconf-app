package org.jetbrains.kotlinconf.ui

import android.os.*
import android.view.*
import androidx.appcompat.app.*
import androidx.recyclerview.widget.*
import com.bumptech.glide.*
import kotlinx.android.synthetic.main.activity_speaker.*
import org.jetbrains.kotlinconf.*
import org.jetbrains.kotlinconf.R
import org.jetbrains.kotlinconf.presentation.*
import org.jetbrains.kotlinconf.ui.details.*

class SpeakerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_speaker)

        val speakerId = intent.getStringExtra("speaker")
        showSpeaker(speakerId)

        speaker_main.autoclear()
    }

    private fun showSpeaker(id: String) {
        val speaker = KotlinConf.service.speaker(id)

        speaker_name.text = speaker.fullName.toUpperCase()
        speaker_description.text = speaker.tagLine
        speaker_bio.text = speaker.bio

        val pictureUrl = speaker.profilePicture ?: return
        Glide.with(this)
            .load(pictureUrl)
            .into(speaker_photo)

        setupCards(KotlinConf.service.speakerSessions(id))
    }

    private fun setupCards(cards: List<SessionCard>) {
        speaker_cards.apply {
            autoclear()
            layoutManager = LinearLayoutManager(context)
            adapter = object : RecyclerView.Adapter<SessionCardHolder>() {
                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): SessionCardHolder = SessionCardHolder(
                    layoutInflater.inflate(
                        R.layout.view_schedule_session_card,
                        parent,
                        false
                    )
                )

                override fun getItemCount(): Int = cards.size

                override fun onBindViewHolder(holder: SessionCardHolder, position: Int) {
                    holder.show(ScheduleItem.Card(cards[position]))
                }
            }
        }
    }
}
