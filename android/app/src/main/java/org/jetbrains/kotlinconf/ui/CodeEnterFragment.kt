package org.jetbrains.kotlinconf.ui

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.*
import android.view.Gravity.CENTER
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.EditorInfo.IME_ACTION_DONE
import android.widget.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.design.themedAppBarLayout
import org.jetbrains.anko.support.v4.nestedScrollView
import org.jetbrains.kotlinconf.R
import org.jetbrains.kotlinconf.getResourceId
import org.jetbrains.kotlinconf.multilineCollapsingToolbarLayout
import org.jetbrains.kotlinconf.observe

class CodeEnterFragment : Fragment(), AnkoComponent<Context> {

    private lateinit var toolbar: Toolbar
    private lateinit var checkBox: CheckBox
    private lateinit var submitButton: Button
    private lateinit var codeEditText: EditText
    private lateinit var viewModel: CodeEnterViewModel
    private lateinit var progressBar: ProgressBar

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(this, ViewModelProviders.DefaultFactory(activity.application))
                .get(CodeEnterViewModel::class.java)
                .apply {
                    setNavigationManager(activity as NavigationManager)
                }

        viewModel.codeVerified.observe(this) {
            updateView(it)
        }

        viewModel.showLoading.observe(this) {
            it?.apply {
                if (this) {
                    progressBar.visibility = VISIBLE
                    submitButton.visibility = GONE
                } else {
                    progressBar.visibility = GONE
                    submitButton.visibility = VISIBLE
                }
            }
        }

        setHasOptionsMenu(true)

        (activity as AppCompatActivity).apply {
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            supportActionBar?.setDisplayShowTitleEnabled(false)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun updateView(verified: Boolean?) {
       verified?.apply {
           if(this){
               viewModel.showSessionList()
           }
       }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return createView(AnkoContext.create(context))
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
                            collapseMode = android.support.design.widget.CollapsingToolbarLayout.LayoutParams.COLLAPSE_MODE_PARALLAX
                        }

                        imageView(R.drawable.kotlin_conf_app_header).lparams {
                            margin = dip(20)
                        }
                        imageView(R.drawable.kotlin_conf_app_header).scaleType = ImageView.ScaleType.FIT_CENTER
                    }

                    toolbar = toolbar {
                        layoutParams = CollapsingToolbarLayout.LayoutParams(
                                matchParent,
                                context.dimen(context.getResourceId(R.attr.actionBarSize))
                        ).apply {
                            collapseMode = android.support.design.widget.CollapsingToolbarLayout.LayoutParams.COLLAPSE_MODE_PIN
                        }
                    }
                }.lparams(width = matchParent, height = matchParent) {
                    scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
                }
            }.lparams(width = matchParent, height = dip(200))

            nestedScrollView {
                verticalLayout {
                    view {
                        backgroundResource = context.getResourceId(android.R.attr.listDivider)
                    }.lparams(width = matchParent, height = dip(2))

                    textView(R.string.code_enter_label) {
                        setTextIsSelectable(true)
                        textSize = 20f
                    }.lparams {
                        gravity = Gravity.CENTER_HORIZONTAL
                        margin = dip(20)
                        bottomMargin = dip(5)
                    }

                    codeEditText = editText {
                        textSize = 20f
                        gravity = CENTER
                        lines = 1
                        inputType = InputType.TYPE_CLASS_TEXT
                        imeOptions = IME_ACTION_DONE
                        addTextChangedListener(object : TextWatcher {
                            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                            }

                            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                                toggleSubmitEnabled()
                            }

                            override fun afterTextChanged(s: Editable?) {
                            }

                        })
                    }.lparams(width = matchParent, height = wrapContent) {
                        gravity = Gravity.CENTER_HORIZONTAL
                        margin = dip(20)
                        bottomMargin = dip(5)
                    }

                    textView(R.string.terms_text) {
                        textSize = 16f
                        setOnClickListener {
                            val i = Intent(Intent.ACTION_VIEW)
                            i.data = Uri.parse(getString(R.string.terms_url))
                            startActivity(i)
                        }
                    }.lparams {
                        margin = dip(20)
                        bottomMargin = dip(5)
                    }

                    checkBox = checkBox(R.string.terms_agree) {

                    }.lparams {
                        margin = dip(20)
                        bottomMargin = dip(5)
                    }

                    progressBar = progressBar {
                        visibility = GONE
                    }.lparams {
                        margin = dip(20)
                        gravity = Gravity.CENTER_HORIZONTAL
                        bottomMargin = dip(5)
                    }

                    submitButton = button(R.string.submit_button) {
                        isEnabled = false
                        setOnClickListener {
                            verifyCode()
                        }
                    }.lparams(width = matchParent, height = wrapContent) {
                        margin = dip(20)
                        bottomMargin = dip(5)
                    }

                    checkBox.setOnCheckedChangeListener { _, _ ->
                        toggleSubmitEnabled()
                    }


                }.lparams(width = matchParent, height = matchParent)
            }.lparams(width = matchParent, height = matchParent) {
                behavior = AppBarLayout.ScrollingViewBehavior()
            }

        }

    }

    private fun verifyCode() {
        launch(UI) {
            viewModel.submitCode(codeEditText.text.toString())
        }
    }

    private fun toggleSubmitEnabled() {
        submitButton.isEnabled = checkBox.isChecked && codeEditText.text.isNotEmpty()
    }

    companion object {
        const val TAG = "Code"
    }

}