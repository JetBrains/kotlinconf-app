package org.jetbrains.kotlinconf.ui

import android.os.*
import android.view.*
import androidx.fragment.app.*
import androidx.recyclerview.widget.*
import kotlinx.android.synthetic.main.fragment_speakers.view.*
import org.jetbrains.kotlinconf.*
import org.jetbrains.kotlinconf.ui.details.*

class SpeakersController : Fragment() {
    private val speakers by lazy { SpeakersAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        KotlinConf.service.speakers.watch {
            speakers.speakers = it
            speakers.notifyDataSetChanged()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_speakers, container, false).apply {
        setupSpeakers()
    }

    private fun View.setupSpeakers() {
        speakers_list.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = speakers

            addItemDecoration(SpeakerViewDecoration())
        }
    }

    private inner class SpeakersAdapter(
        var speakers: List<SpeakerData> = emptyList()
    ) : RecyclerView.Adapter<SpeakerViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpeakerViewHolder {
            val holder = layoutInflater.inflate(R.layout.view_speakers_list_item, parent, false)
            return SpeakerViewHolder(holder)
        }

        override fun getItemCount(): Int = speakers.size

        override fun onBindViewHolder(holder: SpeakerViewHolder, position: Int) {
            val speaker = speakers[position]
            holder.update(speaker)
        }
    }
}
