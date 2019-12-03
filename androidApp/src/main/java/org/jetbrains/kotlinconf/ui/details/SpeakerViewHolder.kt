package org.jetbrains.kotlinconf.ui.details

import android.graphics.*
import android.view.*
import androidx.recyclerview.widget.*
import com.bumptech.glide.*
import kotlinx.android.synthetic.main.view_speakers_list_item.view.*
import org.jetbrains.kotlinconf.*
import org.jetbrains.kotlinconf.R
import org.jetbrains.kotlinconf.showActivity
import org.jetbrains.kotlinconf.ui.*

class SpeakerViewHolder(private val speakerView: View) : RecyclerView.ViewHolder(speakerView) {
    fun update(speaker: SpeakerData) {
        val picture = speaker.profilePicture

        speakerView.apply {
            setOnClickListener {
                setBackgroundColor(color(R.color.selected_white))
                showActivity<SpeakerActivity> {
                    putExtra("speaker", speaker.id)
                }
                setBackgroundColor(color(R.color.white))
            }

            speaker_name.text = speaker.fullName.toUpperCase()
            speaker_description.text = speaker.tagLine

            Glide.with(this)
                .load(picture)
                .into(speaker_photo)

            speaker_photo.colorFilter = ColorMatrixColorFilter(ColorMatrix().apply {
                setSaturation(0f)
            })
        }
    }
}
