package org.jetbrains.kotlinconf.ui

import android.graphics.*
import android.os.*
import android.view.*
import android.widget.*
import androidx.fragment.app.*
import androidx.recyclerview.widget.*
import com.google.android.material.bottomsheet.*
import com.google.android.material.tabs.*
import com.mapbox.mapboxsdk.maps.*
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.android.synthetic.main.fragment_map.view.*
import org.jetbrains.kotlinconf.*
import org.jetbrains.kotlinconf.presentation.*
import org.jetbrains.kotlinconf.ui.details.*


class MapController : Fragment() {
    private val KEYNOTE_ROOM = 7972

    private val floors = listOf(
        Style.Builder().fromUri("mapbox://styles/denisvoronov1/cjzikqjgb41rf1cnnb11cv0xw"),
        Style.Builder().fromUri("mapbox://styles/denisvoronov1/cjzsessm40k341clcoer2tn9v")
    )

    private val roomPhoto = mapOf(
        7972 to R.drawable.keynote,
        7973 to R.drawable.aud_10_11_12,
        7974 to R.drawable.aud_10_11_12,
        7975 to R.drawable.aud15,
        7976 to R.drawable.room20
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_map, container, false).apply {
        setupTabs()
        setupMap()

        map_bottom_sheet.setOnTouchListener { view, _ ->
            view.clearFocus()
            true
        }

        map_close_button.setOnClickListener {
            bottomCard(display = false)
        }
    }

    override fun onStart() {
        super.onStart()
        map_mapview.onStart()
    }

    override fun onStop() {
        super.onStop()
        map_mapview.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        map_mapview.onLowMemory()
    }

    override fun onPause() {
        super.onPause()
        map_mapview.onPause()
    }

    override fun onResume() {
        super.onResume()
        map_mapview.onResume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val room = KotlinConf.service.room(KEYNOTE_ROOM)
        room?.let { showRoom(it) }
    }

    private fun View.setupTabs() {
        map_tabs.addOnTabSelectedListener(object :
            TabLayout.BaseOnTabSelectedListener<TabLayout.Tab> {
            override fun onTabSelected(tab: TabLayout.Tab) {
                showFloor(tab.position)
            }

            override fun onTabReselected(p0: TabLayout.Tab?) {}
            override fun onTabUnselected(p0: TabLayout.Tab?) {}
        })
    }

    private fun View.setupMap() {
        showFloor(0)

        map_mapview.getMapAsync { map ->
            map.addOnMapClickListener { position ->
                val point = map.projection.toScreenLocation(position)
                val touchZone = RectF(point.x - 10, point.y - 10, point.x + 10, point.y + 10)
                val features = map
                    .queryRenderedFeatures(touchZone)
                    .mapNotNull { it.getStringProperty("name") }

                if (features.isEmpty()) {
                    return@addOnMapClickListener false
                }

                KotlinConf.service.roomByMapName(features)?.let {
                    showRoom(it)
                    bottomCard(display = true)
                    return@addOnMapClickListener true
                }

                val partner = features.mapNotNull { Partners.partnerByRoomName(it) }.firstOrNull()
                    ?: return@addOnMapClickListener true

                showPartner(partner)
                bottomCard(display = true)
                return@addOnMapClickListener true
            }
        }
    }

    private fun showPartner(partner: Partner) {
        map_room_name.text = partner.title.toUpperCase()
        map_room_photo.apply {
            val logo = PARTNER_LOGOS[partner.key]
            setImageResource(logo!!)
            scaleType = ImageView.ScaleType.FIT_CENTER
        }

        map_room_description.text = Partners.descriptionByName(partner.key)
        map_room_description.visibility = View.VISIBLE
        map_room_cards.visibility = View.GONE
        map_card_scroll.smoothScrollTo(0, 0)
    }

    private fun showRoom(room: RoomData) {
        map_room_name.text = room.displayName(isWorkshop = false).toUpperCase()
        map_room_photo.apply {
            scaleType = ImageView.ScaleType.CENTER_CROP
            setImageResource(roomPhoto[room.id]!!)
        }
        map_room_description.visibility = View.GONE
        map_room_cards.visibility = View.VISIBLE

        val cards = KotlinConf.service.roomSessions(room.id)

        map_room_cards.apply {
            layoutManager = LinearLayoutManager(context)
            isNestedScrollingEnabled = false
            adapter = object : RecyclerView.Adapter<SessionCardHolder>() {
                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): SessionCardHolder = SessionCardHolder(
                    layoutInflater.inflate(
                        R.layout.view_schedule_session_card,
                        parent,
                        false
                    ),
                    displayTime = true
                )

                override fun getItemCount(): Int = cards.size

                override fun onBindViewHolder(holder: SessionCardHolder, position: Int) {
                    holder.show(ScheduleItem.Card(cards[position]))
                }
            }
        }

        map_card_scroll.smoothScrollTo(0, 0)
    }

    private fun bottomCard(display: Boolean) {
        val bottom = BottomSheetBehavior.from(map_bottom_sheet)
        bottom.state = if (display) BottomSheetBehavior.STATE_EXPANDED else BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun View.showFloor(index: Int) {
        map_mapview.getMapAsync { map ->
            map.setStyle(floors[index])
            map.uiSettings.apply {
                setAttributionMargins(20.dp, 10.dp, 10.dp, 230.dp)
                setAttributionTintColor(color(R.color.dark_grey))
            }
        }
    }
}
