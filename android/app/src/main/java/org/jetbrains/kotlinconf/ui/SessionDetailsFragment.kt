package org.jetbrains.kotlinconf.ui

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
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.*
import android.widget.LinearLayout.FOCUS_AFTER_DESCENDANTS
import android.widget.LinearLayout.VERTICAL
import com.bumptech.glide.*
import com.jetbrains.kotlinconf.presentation.*
import kotlinx.coroutines.experimental.android.*
import net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.design.*
import org.jetbrains.anko.support.v4.*
import org.jetbrains.kotlinconf.*
import org.jetbrains.kotlinconf.R
import org.jetbrains.kotlinconf.model.*

class SessionDetailsFragment : Fragment(), SessionDetailsView {

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
    private lateinit var votingButtonsLayout: LinearLayout
    private lateinit var votingPromptLayout: LinearLayout

    private val sessionId by lazy { arguments!!.get(KEY_SESSION_ID) as String }
    private val repository by lazy { (activity!!.application as KotlinConfApplication).repository }
    private val presenter by lazy { SessionDetailsPresenter(UI, this, sessionId, repository) }
    private val navigationManager by lazy { activity as NavigationManager }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setUpToolbar()
        presenter.onCreate()
        favoriteButton.setOnClickListener { presenter.onFavoriteButtonClicked() }
        goodButton.setOnClickListener { presenter.rateSession(SessionRating.GOOD) }
        okButton.setOnClickListener { presenter.rateSession(SessionRating.OK) }
        badButton.setOnClickListener { presenter.rateSession(SessionRating.BAD) }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }

    override fun setIsFavorite(isFavorite: Boolean) {
        val icon = if (isFavorite) R.drawable.ic_favorite_white_24dp else R.drawable.ic_favorite_border_white_24dp
        favoriteButton.setImageResource(icon)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
    }

    override fun setRatingClickable(clickable: Boolean) {
        goodButton.isClickable = clickable
        okButton.isClickable = clickable
        badButton.isClickable = clickable
    }

    override fun updateView(session: SessionModel) {
        collapsingToolbar.title = session.title
        speakersTextView.text = session.speakers.joinToString(separator = ", ") {
            it.fullName ?: ""
        }
        timeTextView.text = (session.startsAt to session.endsAt).toReadableString()
        val roomText = session.room?.let { room -> getString(R.string.room_format_details, room) }
        detailsTextView.text = listOfNotNull(roomText, session.category).joinToString(separator = ", ")
        descriptionTextView.text = session.description
        votingButtonsLayout.visibility = if (codeVerified()) VISIBLE else GONE
        votingPromptLayout.visibility = if (codeVerified()) GONE else VISIBLE
        session.speakers
                .takeIf { it.size < 3 }
                ?.mapNotNull { it.profilePicture }
                ?.forEachIndexed { index, imageUrl ->
                    speakerImageViews[index].showSpeakerImage(imageUrl)
                }
    }

    override fun setupRatingButtons(rating: SessionRating?) {
        goodButton.backgroundResource = if (rating == SessionRating.GOOD) R.drawable.round_toggle_button_background_selected else R.drawable.round_toggle_button_background
        okButton.backgroundResource = if (rating == SessionRating.OK) R.drawable.round_toggle_button_background_selected else R.drawable.round_toggle_button_background
        badButton.backgroundResource = if (rating == SessionRating.BAD) R.drawable.round_toggle_button_background_selected else R.drawable.round_toggle_button_background
    }

    private fun setUpToolbar() {
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).apply {
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            supportActionBar?.setDisplayShowTitleEnabled(false)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }

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

                        votingPromptLayout = linearLayout {
                            gravity = Gravity.CENTER
                            orientation = VERTICAL
                            textView(R.string.voting_text) {
                                textSize = 20f
                                padding = 10
                            }
                            button(R.string.verify_button_text) {
                                setOnClickListener {
                                    navigationManager.showCodeEnterFragment()
                                }
                                //Hack: For whatever reason the first click was being ignored
                                setOnFocusChangeListener { view, b ->
                                    if (b) {
                                        view.performClick()
                                    }
                                }
                            }
                        }.lparams(width = matchParent, height = matchParent) {
                            topMargin = dip(30)
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

    private fun codeVerified(): Boolean {
        return repository.getCodeVerified()
    }

    companion object {
        const val TAG = "SessionDetailsFragment"
        private const val KEY_SESSION_ID = "SessionId"

        fun forSession(id: String): SessionDetailsFragment = SessionDetailsFragment().apply {
            arguments = Bundle().apply { putString(KEY_SESSION_ID, id) }
        }
    }
}