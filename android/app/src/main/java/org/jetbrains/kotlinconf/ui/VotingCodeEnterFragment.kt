package org.jetbrains.kotlinconf.ui

import android.app.*
import android.graphics.*
import android.os.*
import android.support.v7.app.AlertDialog
import android.text.*
import android.view.*
import android.view.Gravity.*
import android.view.inputmethod.EditorInfo.*
import android.widget.*
import kotlinx.coroutines.android.*
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.*
import org.jetbrains.kotlinconf.*
import org.jetbrains.kotlinconf.presentation.*


class VotingCodeEnterFragment : BaseDialogFragment() {
    private lateinit var submitButton: Button
    private lateinit var codeEditText: EditText
    private lateinit var votingPromptText: TextView

    private val repository by lazy { (activity!!.application as KotlinConfApplication).dataRepository }
    private val presenter by lazy { CodeVerificationPresenter(UI, this, repository) }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(context!!)
            .setView(createView())
            .setPositiveButton(R.string.submit_button) { _, _ ->
                val code = codeEditText.text.toString()
                presenter.onSubmitButtonClicked(code)
            }
            .create()
            .apply {
                setOnShowListener {
                    submitButton = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
                    submitButton.isEnabled = false
                }
            }
    }

    private fun createView(): View {
        return UI {
            scrollView {
                verticalLayout {
                    backgroundColor = Color.WHITE
                    imageView(R.drawable.kotlin_conf_app_header) {
                        adjustViewBounds = true
                    }
                    view {
                        backgroundResource = context.getResourceId(android.R.attr.listDivider)
                    }.lparams(width = matchParent, height = dip(2))
                    votingPromptText = textView(R.string.voting_code_prompt) {
                        textSize = 18f
                    }.lparams {
                        topMargin = dip(20)
                        leftMargin = dip(20)
                        rightMargin = dip(20)
                    }
                    codeEditText = editText {
                        textSize = 18f
                        gravity = CENTER
                        lines = 1
                        inputType = InputType.TYPE_CLASS_TEXT
                        imeOptions = IME_ACTION_DONE
                        textColor = votingPromptText.currentTextColor
                        addTextChangedListener(object : TextWatcher {
                            override fun afterTextChanged(s: Editable?) {
                                submitButton.isEnabled = !s.isNullOrBlank()
                            }

                            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                            }

                            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                            }
                        })
                    }.lparams(width = matchParent, height = wrapContent) {
                        gravity = Gravity.CENTER_HORIZONTAL
                        margin = dip(20)
                    }
                }.lparams(width = matchParent, height = wrapContent)
            }
        }.view
    }

    companion object {
        const val TAG = "VotingCodeEnterFragment"
    }
}