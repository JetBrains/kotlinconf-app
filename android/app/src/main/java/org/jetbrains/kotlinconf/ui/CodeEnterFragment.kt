package org.jetbrains.kotlinconf.ui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v4.app.DialogFragment
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.Gravity
import android.view.Gravity.CENTER
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo.IME_ACTION_DONE
import android.widget.*
import com.jetbrains.kotlinconf.presentation.CodeEnterPresenter
import com.jetbrains.kotlinconf.presentation.CodeEnterView
import kotlinx.coroutines.experimental.android.UI
import org.jetbrains.anko.*
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.support.v4.nestedScrollView
import org.jetbrains.kotlinconf.KotlinConfApplication
import org.jetbrains.kotlinconf.R
import org.jetbrains.kotlinconf.getResourceId
import kotlin.properties.Delegates


class CodeEnterFragment : DialogFragment(), AnkoComponent<Context>, CodeEnterView {
    override var isLoading: Boolean by Delegates.observable(false) { _, _, isUpdating ->
        if (isUpdating) {
            progressBar.visibility = VISIBLE
            submitButton.visibility = GONE
        } else {
            progressBar.visibility = GONE
            submitButton.visibility = VISIBLE
        }
    }

    private lateinit var checkBox: CheckBox
    private lateinit var submitButton: Button
    private lateinit var codeEditText: EditText
    private lateinit var progressBar: ProgressBar
    private val repository by lazy {
        (activity!!.application as KotlinConfApplication).repository
    }

    private val presenter by lazy {
        CodeEnterPresenter(UI, this, repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.onCreate()
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar);
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
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
            nestedScrollView {
                verticalLayout {
                    backgroundColor = Color.WHITE
                    imageView(R.drawable.kotlin_conf_app_header) {
                        adjustViewBounds = true
                    }
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

                    button(R.string.skip_button) {
                        setOnClickListener {
                            dismiss()
                        }
                    }.lparams(width = matchParent, height = wrapContent) {
                        margin = dip(20)
                        bottomMargin = dip(5)
                    }
                }.lparams(width = matchParent, height = wrapContent)
            }.lparams(width = matchParent, height = wrapContent) {
                behavior = AppBarLayout.ScrollingViewBehavior()
            }
        }

    }

    override fun dismissDialog() {
        dismiss()
    }

    private fun verifyCode() {
        presenter.submitCode(codeEditText.text.toString())
    }

    private fun toggleSubmitEnabled() {
        submitButton.isEnabled = checkBox.isChecked && !codeEditText.text.isNullOrBlank()
    }

    companion object {
        const val TAG = "Code"
    }
}
