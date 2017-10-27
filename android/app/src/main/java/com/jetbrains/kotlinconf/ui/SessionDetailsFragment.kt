package org.jetbrains.kotlinconf.ui

import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
import android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
import android.support.design.widget.CollapsingToolbarLayout.LayoutParams.COLLAPSE_MODE_PARALLAX
import android.support.design.widget.CollapsingToolbarLayout.LayoutParams.COLLAPSE_MODE_PIN
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.*
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import org.jetbrains.kotlinconf.*
import org.jetbrains.kotlinconf.model.SessionRating
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.design.floatingActionButton
import org.jetbrains.anko.design.themedAppBarLayout
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.support.v4.nestedScrollView

class SessionDetailsFragment : Fragment() {

    private lateinit var toolbar: Toolbar
    private lateinit var speakersTextView: TextView
    private lateinit var timeTextView: TextView
    private lateinit var detailsTextView: TextView
    private lateinit var descriptionTextView: TextView
    private val speakerImageViews: MutableList<ImageView> = mutableListOf()
    private lateinit var collapsingToolbar: CollapsingToolbarLayout
    private lateinit var favoriteButton: FloatingActionButton

    private lateinit var goodButton: ImageButton
    private lateinit var badButton: ImageButton
    private lateinit var okButton: ImageButton

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setHasOptionsMenu(true)

        (activity as AppCompatActivity).apply {
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            supportActionBar?.setDisplayShowTitleEnabled(false)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        val sessionId = arguments.get(KEY_SESSION_ID) as String
        val viewModel = ViewModelProviders.of(
                this,
                ViewModelProviders.DefaultFactory(activity.application))
                .get(SessionDetailsViewModel::class.java)
                .apply { setSession(sessionId) }

        favoriteButton.setOnClickListener {
            launch(UI) { viewModel.toggleFavorite() }
        }

        val clickListener = View.OnClickListener { view: View ->
            val rating = when (view) {
                goodButton -> SessionRating.GOOD
                okButton -> SessionRating.OK
                badButton -> SessionRating.BAD
                else -> null
            }

            launch(UI) {
                if (rating != null) {
                    if (viewModel.rating.value != rating) {
                        viewModel.setRating(rating)
                    }
                    else {
                        viewModel.removeRating()
                    }
                }
            }
        }

        viewModel.session.observe(this, this::updateView)

        viewModel.isFavorite.observe(this) { isFavorite ->
            if (isFavorite == true) {
                favoriteButton.setImageResource(R.drawable.ic_favorite_white_24dp)
            }
            else {
                favoriteButton.setImageResource(R.drawable.ic_favorite_border_white_24dp)
            }
        }

        viewModel.rating.value.let { rating ->
            setupRatingButtons(rating)
        }

        viewModel.rating.observe(this) { rating ->
            setupRatingButtons(rating)
        }

        goodButton.setOnClickListener(clickListener)
        okButton.setOnClickListener(clickListener)
        badButton.setOnClickListener(clickListener)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
    }

    private fun setupRatingButtons(rating: SessionRating?) {
        goodButton.backgroundResource = if (rating == SessionRating.GOOD)
            R.drawable.round_toggle_button_background_selected
        else
            R.drawable.round_toggle_button_background

        okButton.backgroundResource = if (rating == SessionRating.OK)
            R.drawable.round_toggle_button_background_selected
        else
            R.drawable.round_toggle_button_background

        badButton.backgroundResource = if (rating == SessionRating.BAD)
            R.drawable.round_toggle_button_background_selected
        else
            R.drawable.round_toggle_button_background
    }

    private fun updateView(session: SessionModel?) {
        if (session == null) {
            return
        }

        with (session) {
            collapsingToolbar.title = session.title
            speakersTextView.text = session.speakers.joinToString(separator = ", ") { it.fullName ?: "" }
            val time = (session.startsAt to session.endsAt).toReadableString()
            timeTextView.text = time
            detailsTextView.text = listOfNotNull(roomText, category).joinToString(", ")
            descriptionTextView.text = session.description

            session.speakers
                    .takeIf { it.size < 3 }
                    ?.mapNotNull { it.profilePicture }
                    ?.apply {
                        forEachIndexed { index, imageUrl ->
                            speakerImageViews[index].showSpeakerImage(imageUrl)
                        }
                    }
        }
    }

    private val SessionModel.roomText: String?
        get() = room?.let { getString(R.string.room_format_details, it) }

    private fun ImageView.showSpeakerImage(imageUrl: String) {
        visibility = View.VISIBLE
        Glide.with(this@SessionDetailsFragment)
                .load(imageUrl)
                .centerCrop()
                .into(this)
    }

    override fun onCreateView(
            inflater: LayoutInflater?,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return UI {
            coordinatorLayout {
                lparams(width = matchParent, height = matchParent)
                themedAppBarLayout(R.style.ThemeOverlay_AppCompat_Dark_ActionBar) {
                    id = R.id.app_bar_layout
                    collapsingToolbar = multilineCollapsingToolbarLayout {
                        contentScrim = ColorDrawable(theme.getColor(R.attr.colorPrimary))
                        maxLines = 5
                        expandedTitleMarginStart = dip(20)
                        expandedTitleMarginEnd = dip(20)
                        setExpandedTitleTextAppearance(R.style.SessionTitleExpanded)

                        linearLayout {
                            layoutParams = CollapsingToolbarLayout.LayoutParams(matchParent, matchParent).apply {
                                collapseMode = COLLAPSE_MODE_PARALLAX
                            }

                            imageView {
                                visibility = View.GONE
                            }.lparams(width = 0, height = matchParent) {
                                weight = 0.5f
                            }.also { speakerImageViews.add(it) }

                            imageView {
                                visibility = View.GONE
                            }.lparams(width = 0, height = matchParent) {
                                weight = 0.5f
                            }.also { speakerImageViews.add(it) }
                        }

                        view {
                            backgroundResource = R.drawable.appbar_buttons_scrim
                            layoutParams = CollapsingToolbarLayout.LayoutParams(
                                    matchParent,
                                    dimen(context.getResourceId(R.attr.actionBarSize))).apply {
                                gravity = Gravity.TOP
                            }
                        }

                        view {
                            backgroundResource = R.drawable.appbar_title_scrim
                            layoutParams = CollapsingToolbarLayout.LayoutParams(matchParent, matchParent).apply {
                                gravity = Gravity.BOTTOM
                            }
                        }

                        toolbar = toolbar {
                            layoutParams = CollapsingToolbarLayout.LayoutParams(
                                    matchParent,
                                    dimen(context.getResourceId(R.attr.actionBarSize))
                            ).apply {
                                collapseMode = COLLAPSE_MODE_PIN
                            }
                        }
                    }.lparams(width = matchParent, height = matchParent) {
                        scrollFlags = SCROLL_FLAG_SCROLL or SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
                    }
                }.lparams(width = matchParent, height = dip(256))

                favoriteButton = floatingActionButton().lparams {
                    anchorId = R.id.app_bar_layout
                    anchorGravity = Gravity.BOTTOM or Gravity.END
                    margin = dip(8)
                }

                nestedScrollView {
                    verticalLayout {
                        speakersTextView = textView {
                            textSize = 26f
                            textColor = Color.BLACK
                        }.lparams {
                            bottomMargin = dip(6)
                        }

                        timeTextView = textView {
                            textSize = 17f
                        }.lparams {
                            bottomMargin = dip(4)
                        }

                        detailsTextView = textView {
                            textSize = 17f
                        }

                        descriptionTextView = textView {
                            textSize = 19f
                        }.lparams {
                            topMargin = dip(20)
                        }

                        linearLayout {
                            goodButton = imageButton {
                                imageResource = R.drawable.ic_thumb_up_white_24dp
                            }
                            okButton = imageButton {
                                imageResource = R.drawable.ic_sentiment_neutral_white_36dp
                            }
                            badButton = imageButton {
                                imageResource = R.drawable.ic_thumb_down_white_24dp
                            }
                        }.lparams {
                            topMargin = dip(10)
                            bottomMargin = dip(80)
                            gravity = Gravity.CENTER_HORIZONTAL
                        }.applyRecursively { view ->
                            when (view) {
                                is ImageButton -> {
                                    view.lparams {
                                        width = dip(56)
                                        height = dip(56)
                                        gravity = Gravity.CENTER_VERTICAL
                                        margin = dip(10)
                                    }
                                }
                            }
                        }

                    }.lparams(width = matchParent, height = wrapContent) {
                        margin = dip(20)
                    }.applyRecursively { view ->
                        (view as? TextView)?.setTextIsSelectable(true)
                    }

                }.lparams(width = matchParent, height = matchParent) {
                    behavior = AppBarLayout.ScrollingViewBehavior()
                }
            }
        }.view
    }

    companion object {
        const val TAG = "SessionDetailsFragment"
        private const val KEY_SESSION_ID = "SessionId"

        fun forSession(id: String): SessionDetailsFragment = SessionDetailsFragment().apply {
            arguments = Bundle().apply { putString(KEY_SESSION_ID, id) }
        }
    }
}