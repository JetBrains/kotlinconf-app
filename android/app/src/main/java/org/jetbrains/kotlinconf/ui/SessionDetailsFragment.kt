package org.jetbrains.kotlinconf.ui

import android.arch.lifecycle.*
import android.arch.lifecycle.ViewModelProvider.*
import android.graphics.*
import android.graphics.drawable.*
import android.os.*
import android.support.design.widget.*
import android.support.design.widget.AppBarLayout.LayoutParams.*
import android.support.design.widget.CollapsingToolbarLayout.LayoutParams.*
import android.support.v4.app.*
import android.support.v7.app.*
import android.support.v7.widget.Toolbar
import android.view.*
import android.view.View.*
import android.widget.*
import com.bumptech.glide.*
import kotlinx.coroutines.*
import kotlinx.coroutines.android.*
import net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.design.*
import org.jetbrains.anko.support.v4.*
import org.jetbrains.kotlinconf.*
import org.jetbrains.kotlinconf.R
import org.jetbrains.kotlinconf.data.*

class SessionDetailsFragment : Fragment() {
    private lateinit var toolbar: Toolbar
    private lateinit var speakersTextView: TextView
    private lateinit var timeTextView: TextView
    private lateinit var detailsTextView: TextView
    private lateinit var descriptionTextView: TextView
    private val speakerImageViews: MutableList<ImageView> = mutableListOf()
    private lateinit var collapsingToolbar: CollapsingToolbarLayout
    private lateinit var favoriteButton: FloatingActionButton
    private lateinit var votingButtonsLayout: LinearLayout
    private lateinit var votingPromptLayout: LinearLayout
    private lateinit var verifyCodeButton: Button

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

        val sessionId = arguments?.get(KEY_SESSION_ID) as String
        val sessionDetailsViewModel = ViewModelProviders.of(
            this,
            AndroidViewModelFactory.getInstance(activity!!.application)
        )
            .get(SessionDetailsViewModel::class.java)
            .apply { setSession(sessionId) }

        val codeVerificationViewModel = ViewModelProviders.of(
            this,
            AndroidViewModelFactory.getInstance(activity!!.application)
        )
            .get(CodeVerificationViewModel::class.java)

        codeVerificationViewModel.isCodeVerified.observe(this) {
            val isCodeVerified = it ?: false
            votingButtonsLayout.visibility = if (isCodeVerified) VISIBLE else GONE
            votingPromptLayout.visibility = if (isCodeVerified) GONE else VISIBLE
        }

        favoriteButton.setOnClickListener {
            launch(UI) { sessionDetailsViewModel.toggleFavorite() }
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
                    if (sessionDetailsViewModel.rating.value != rating) {
                        sessionDetailsViewModel.setRating(rating)
                    } else {
                        sessionDetailsViewModel.removeRating()
                    }
                }
            }
        }

        sessionDetailsViewModel.session.observe(this, this::updateView)

        sessionDetailsViewModel.isFavorite.observe(this) { isFavorite ->
            val resource = if (isFavorite == true)
                R.drawable.ic_favorite_border_white_24dp
            else
                R.drawable.ic_favorite_border_white_24dp

            favoriteButton.setImageResource(resource)
        }

        setupRatingButtons(sessionDetailsViewModel.rating.value)

        sessionDetailsViewModel.rating.observe(this) { rating ->
            setupRatingButtons(rating)
        }

        goodButton.setOnClickListener(clickListener)
        okButton.setOnClickListener(clickListener)
        badButton.setOnClickListener(clickListener)

        verifyCodeButton.setOnClickListener {
            CodeEnterFragment().show(fragmentManager, CodeEnterFragment.TAG)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
    }

    private fun setupRatingButtons(rating: SessionRating?) {
        fun selectButton(target: SessionRating): Int =
            if (rating == target)
                R.drawable.round_toggle_button_background_selected
            else
                R.drawable.round_toggle_button_background

        goodButton.backgroundResource = selectButton(SessionRating.GOOD)
        okButton.backgroundResource = selectButton(SessionRating.OK)
        badButton.backgroundResource = selectButton(SessionRating.BAD)
    }

    private fun updateView(session: SessionModel?) {
        if (session == null) {
            return
        }

        with(session) {
            collapsingToolbar.title = session.title
            speakersTextView.text = session.speakers.joinToString(separator = ", ") { it.fullName }
            val time = (session.startsAt to session.endsAt).toReadableString()
            timeTextView.text = time
            detailsTextView.text = listOfNotNull(roomText, category).joinToString(", ")
            descriptionTextView.text = session.descriptionText

            session.speakers
                .takeIf { it.size < 3 }
                ?.map { it.profilePicture }
                ?.apply {
                    forEachIndexed { index, imageUrl ->
                        imageUrl?.let { speakerImageViews[index].showSpeakerImage(it) }
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
        inflater: LayoutInflater,
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
                                dimen(context.getResourceId(R.attr.actionBarSize))
                            ).apply {
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

                        votingPromptLayout = verticalLayout {
                            textView(R.string.voting_text) {
                                textSize = 18f
                            }
                            verifyCodeButton = button(R.string.verify_button_text) {
                                textColor = theme.getColor(R.attr.colorAccent)
                                backgroundResource = context.getResourceId(R.attr.selectableItemBackground)
                            }.lparams(width = matchParent, height = wrapContent) {
                                topMargin = dip(10)
                            }
                        }.lparams(width = matchParent, height = wrapContent) {
                            topMargin = dip(20)
                            bottomMargin = dip(80)
                            gravity = Gravity.CENTER_HORIZONTAL
                        }

                        votingButtonsLayout = linearLayout {
                            goodButton = imageButton {
                                imageResource = R.drawable.ic_happy
                            }
                            okButton = imageButton {
                                imageResource = R.drawable.ic_neutral
                            }
                            badButton = imageButton {
                                imageResource = R.drawable.ic_sad
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
                        // Making a Button widget selectable causes problems with clicking
                        // (Button class extends TextView)
                        if (view is Button) return@applyRecursively
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