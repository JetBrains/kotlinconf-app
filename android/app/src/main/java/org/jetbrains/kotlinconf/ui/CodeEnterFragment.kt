package org.jetbrains.kotlinconf.ui

import android.app.Dialog
import android.arch.lifecycle.ViewModelProvider.AndroidViewModelFactory
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.Gravity
import android.view.Gravity.CENTER
import android.view.View
import android.view.View.*
import android.view.inputmethod.EditorInfo.IME_ACTION_DONE
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ProgressBar
import kotlinx.coroutines.android.UI
import kotlinx.coroutines.launch
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.kotlinconf.R
import org.jetbrains.kotlinconf.getResourceId


class CodeEnterFragment : DialogFragment() {
    private lateinit var submitButton: Button
    private lateinit var checkBox: CheckBox
    private lateinit var codeEditText: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var dialogContent: View

    private lateinit var viewModel: CodeVerificationViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(context!!)
            .setView(createView())
            .setPositiveButton(R.string.submit_button) { _, _ -> }
            .setNeutralButton(R.string.skip_button) { dialog, _ -> dialog.dismiss() }
            .create()
            .apply {
                setOnShowListener {
                    submitButton = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
                    submitButton.isEnabled = false
                    // the listener is set here to prevent the dialog from being dismissed on click
                    submitButton.setOnClickListener { _ -> verifyCode() }
                }
            }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(this, AndroidViewModelFactory.getInstance(activity!!.application))
            .get(CodeVerificationViewModel::class.java)
    }

    private fun verifyCode() {
        launch(UI) {
            progressBar.visibility = VISIBLE
            dialogContent.visibility = INVISIBLE
            submitButton.visibility = INVISIBLE
            viewModel.verifyCode(codeEditText.text.toString())
            dismiss()
        }
    }

    private fun toggleSubmitButtonEnable() {
        submitButton.isEnabled = checkBox.isChecked && !codeEditText.text.isNullOrBlank()
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
                    frameLayout {
                        dialogContent = verticalLayout {
                            textView(R.string.code_enter_label) {
                                textSize = 18f
                            }.lparams {
                                gravity = Gravity.CENTER_HORIZONTAL
                                topMargin = dip(20)
                            }
                            codeEditText = editText {
                                textSize = 18f
                                gravity = CENTER
                                lines = 1
                                inputType = InputType.TYPE_CLASS_TEXT
                                imeOptions = IME_ACTION_DONE
                                addTextChangedListener(object : TextWatcher {
                                    override fun beforeTextChanged(
                                        s: CharSequence?,
                                        start: Int,
                                        count: Int,
                                        after: Int
                                    ) {
                                    }

                                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                                        toggleSubmitButtonEnable()
                                    }

                                    override fun afterTextChanged(s: Editable?) {
                                    }
                                })
                            }.lparams(width = matchParent, height = wrapContent) {
                                gravity = Gravity.CENTER_HORIZONTAL
                                margin = dip(20)
                            }
                            textView(R.string.terms_text) {
                                textSize = 16f
                                setOnClickListener {
                                    val i = Intent(Intent.ACTION_VIEW)
                                    i.data = Uri.parse(getString(R.string.terms_url))
                                    startActivity(i)
                                }
                            }.lparams {
                                leftMargin = dip(20)
                                rightMargin = dip(20)
                            }
                            checkBox = checkBox(R.string.terms_agree) {
                                textSize = 16f
                                textColor = R.color.colorPrimary
                                setOnCheckedChangeListener { _, _ ->
                                    toggleSubmitButtonEnable()
                                }
                            }.lparams {
                                margin = dip(20)
                            }
                        }
                        progressBar = progressBar {
                            visibility = GONE
                        }.lparams {
                            gravity = CENTER
                        }
                    }
                }.lparams(width = matchParent, height = wrapContent)
            }
        }.view
    }

    companion object {
        const val TAG = "CodeEnterFragment"
    }
}