package org.jetbrains.kotlinconf.ui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
import android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
import android.support.design.widget.CollapsingToolbarLayout.LayoutParams.COLLAPSE_MODE_PARALLAX
import android.support.design.widget.CollapsingToolbarLayout.LayoutParams.COLLAPSE_MODE_PIN
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.util.Linkify
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.bottomOf
import org.jetbrains.anko.centerHorizontally
import org.jetbrains.anko.centerVertically
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.design.themedAppBarLayout
import org.jetbrains.anko.dimen
import org.jetbrains.anko.dip
import org.jetbrains.anko.imageView
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.margin
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.padding
import org.jetbrains.anko.relativeLayout
import org.jetbrains.anko.rightOf
import org.jetbrains.anko.support.v4.nestedScrollView
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textView
import org.jetbrains.anko.verticalLayout
import org.jetbrains.anko.view
import org.jetbrains.anko.wrapContent
import org.jetbrains.kotlinconf.R
import org.jetbrains.kotlinconf.getColor
import org.jetbrains.kotlinconf.getHtmlText
import org.jetbrains.kotlinconf.getResourceId
import org.jetbrains.kotlinconf.multilineCollapsingToolbarLayout
import org.jetbrains.kotlinconf.theme

class InfoFragment : Fragment(), AnkoComponent<Context> {

    private lateinit var toolbar: Toolbar

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setHasOptionsMenu(true)

        (activity as AppCompatActivity).apply {
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            supportActionBar?.setDisplayShowTitleEnabled(false)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return createView(AnkoContext.create(context!!))
    }

    override fun createView(ui: AnkoContext<Context>): View = with(ui) {
        coordinatorLayout {
            backgroundColor = Color.WHITE
            themedAppBarLayout(R.style.ThemeOverlay_AppCompat_ActionBar) {
                multilineCollapsingToolbarLayout {
                    relativeLayout {
                        backgroundColor = Color.WHITE
                        contentScrim = ColorDrawable(Color.WHITE)

                        layoutParams = CollapsingToolbarLayout.LayoutParams(matchParent, matchParent).apply {
                            collapseMode = COLLAPSE_MODE_PARALLAX
                        }

                        imageView(R.drawable.info_header_background) {
                            scaleType = ImageView.ScaleType.FIT_START
                        }.lparams(width = matchParent, height = matchParent) {
                            leftMargin = dip(4)
                            topMargin = dip(4)
                        }

                        imageView(R.drawable.info_header_image).lparams {
                            margin = dip(20)
                        }
                    }

                    toolbar = toolbar {
                        layoutParams = CollapsingToolbarLayout.LayoutParams(
                                matchParent,
                                context.dimen(context.getResourceId(R.attr.actionBarSize))
                        ).apply {
                            collapseMode = COLLAPSE_MODE_PIN
                        }
                    }
                }.lparams(width = matchParent, height = matchParent) {
                    scrollFlags = SCROLL_FLAG_SCROLL or SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
                }
            }.lparams(width = matchParent, height = dip(200))

            nestedScrollView {
                verticalLayout {
                    view {
                        backgroundResource = context.getResourceId(android.R.attr.listDivider)
                    }.lparams(width = matchParent, height = dip(2))

                    textView(context.getHtmlText(R.string.app_description)) {
                        autoLinkMask = Linkify.WEB_URLS
                        textSize = 18f
                        setTextIsSelectable(true)
                    }.lparams {
                        margin = dip(20)
                    }

                    view {
                        backgroundResource = context.getResourceId(android.R.attr.listDivider)
                    }.lparams(width = matchParent, height = dip(2))

                    relativeLayout {
                        padding = dip(20)

                        imageView(R.drawable.ic_location) {
                            id = R.id.icon_location

                            setOnClickListener {
                                val gmmIntentUri = Uri.parse("geo:37.8051965,-122.4011537?z=17")
                                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                mapIntent.`package` = "com.google.android.apps.maps"
                                if (mapIntent.resolveActivity(context.packageManager) != null) {
                                    startActivity(mapIntent)
                                }
                            }

                        }.lparams(width = dip(24), height = dip(24)) {
                            centerVertically()
                            leftMargin = dip(10)
                            rightMargin = dip(20)
                        }

                        textView(R.string.kotlin_conf_address) {
                            textSize = 18f
                            textColor = Color.BLACK
                            setTextIsSelectable(true)
                        }.lparams {
                            centerVertically()
                            rightOf(R.id.icon_location)
                        }
                    }

                    view {
                        backgroundResource = context.getResourceId(android.R.attr.listDivider)
                    }.lparams(width = matchParent, height = dip(2))

                    linearLayout {
                        relativeLayout {
                            imageView(R.drawable.ic_web) {
                                id = R.id.icon_website
                            }.lparams(width = dip(24), height = dip(24)) {
                                centerHorizontally()
                            }

                            textView("WEBSITE") {
                                textSize = 16f
                                textColor = theme.getColor(R.attr.colorAccent)
                            }.lparams {
                                centerHorizontally()
                                bottomOf(R.id.icon_website)
                                topMargin = dip(10)
                            }

                            setOnClickListener {
                                val websiteIntent = Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("https://kotlinconf.com"))
                                startActivity(websiteIntent)
                            }

                        }.lparams(width = 0, height = wrapContent) {
                            weight = 0.5f
                        }

                        relativeLayout {
                            imageView(R.drawable.ic_twitter) {
                                id = R.id.icon_twitter
                            }.lparams(width = dip(24), height = dip(24)) {
                                centerHorizontally()
                            }

                            textView("TWITTER") {
                                textSize = 16f
                                textColor = theme.getColor(R.attr.colorAccent)
                            }.lparams {
                                centerHorizontally()
                                bottomOf(R.id.icon_twitter)
                                topMargin = dip(10)
                            }

                            setOnClickListener {
                                val twitterIntent = Intent(Intent.ACTION_VIEW,
                                        Uri.parse("twitter://user?screen_name=kotlinconf"))
                                twitterIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                if (twitterIntent.resolveActivity(context.packageManager) != null) {
                                    startActivity(twitterIntent)
                                } else {
                                    val webTwitterIntent = Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse("https://twitter.com/kotlinconf"))
                                    startActivity(webTwitterIntent)
                                }
                            }

                        }.lparams(width = 0, height = wrapContent) {
                            weight = 0.5f
                        }
                    }.lparams(width = matchParent, height = wrapContent) {
                        margin = dip(30)
                    }

                    view {
                        backgroundResource = context.getResourceId(android.R.attr.listDivider)
                    }.lparams(width = matchParent, height = dip(2))

                    textView(R.string.legal_notice_title) {
                        setTextIsSelectable(true)
                    }.lparams {
                        gravity = Gravity.CENTER_HORIZONTAL
                        margin = dip(20)
                        bottomMargin = dip(5)
                    }

                    textView(R.string.copyright) {
                        setTextIsSelectable(true)
                    }.lparams {
                        gravity = Gravity.CENTER_HORIZONTAL
                    }

                    textView(R.string.libraries) {
                        setTextIsSelectable(true)
                    }.lparams {
                        topMargin = dip(20)
                        leftMargin = dip(20)
                        rightMargin = dip(20)
                    }

                    textView(context.getHtmlText(R.string.apache2_license)) {
                        setTextIsSelectable(true)
                    }.lparams {
                        margin = dip(20)
                        bottomMargin = dip(40)
                    }
                }.lparams(width = matchParent, height = matchParent)
            }.lparams(width = matchParent, height = matchParent) {
                behavior = AppBarLayout.ScrollingViewBehavior()
            }
        }
    }

    companion object {
        const val TAG = "Info"
    }
}