package org.jetbrains.kotlinconf.ui

import android.app.*
import android.graphics.*
import android.os.*
import android.support.v7.app.AlertDialog
import android.text.*
import android.text.method.*
import android.view.*
import android.view.Gravity.*
import android.view.inputmethod.EditorInfo.*
import android.widget.*
import kotlinx.coroutines.*
import kotlinx.coroutines.android.*
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.*
import org.jetbrains.kotlinconf.*
import org.jetbrains.kotlinconf.presentation.*


class PrivacyPolicyAcceptanceFragment : BaseDialogFragment(), CodeVerificationView {
    private lateinit var submitButton: Button
    private lateinit var checkBox: CheckBox
    private lateinit var codeEditText: EditText
    private lateinit var policyPrivacyText: TextView

    private val repository by lazy { (activity!!.application as KotlinConfApplication).dataRepository }
    private val codeVerificationPresenter by lazy { CodeVerificationPresenter(Dispatchers.Main, this, repository) }
    private val privacyPolicyPresenter by lazy { PrivacyPolicyPresenter(repository) }

    init {
        isCancelable = false
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = AlertDialog.Builder(context!!)
            .setCancelable(false)
            .setView(createView())
            .setPositiveButton(R.string.submit_button) { _, _ ->
                privacyPolicyPresenter.onAcceptPrivacyPolicyClicked()
                val code = codeEditText.text.toString()
                codeVerificationPresenter.onSubmitButtonClicked(code)
            }
            .create()
        dialog.setOnShowListener {
            submitButton = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
            submitButton.isEnabled = false
        }
        dialog.setOnKeyListener { _, keyCode, event ->
            // Listen for a back button pressed to close the activity
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
                activity?.finish()
            }
            return@setOnKeyListener true
        }
        dialog.setCanceledOnTouchOutside(false)
        dialog.setOnCancelListener { activity?.finishAffinity() }
        return dialog
    }

    override fun dismissView() {
        dismiss()
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
                    policyPrivacyText = textView(context.getHtmlText(R.string.privacy_policy_text)) {
                        movementMethod = LinkMovementMethod.getInstance()
                        textSize = 18f
                        setTextIsSelectable(true)
                    }.lparams {
                        margin = dip(20)
                    }
                    checkBox = checkBox(R.string.privacy_policy_accept) {
                        textSize = 18f
                        textColor = policyPrivacyText.currentTextColor
                        setOnCheckedChangeListener { _, _ ->
                            submitButton.isEnabled = checkBox.isChecked
                        }
                    }.lparams {
                        leftMargin = dip(20)
                        rightMargin = dip(20)
                        bottomMargin = dip(20)
                    }
                    view {
                        backgroundResource = context.getResourceId(android.R.attr.listDivider)
                    }.lparams(width = matchParent, height = dip(2))

                    textView(R.string.rating_code_prompt) {
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
                        textColor = policyPrivacyText.currentTextColor
                    }.lparams(width = matchParent, height = wrapContent) {
                        gravity = Gravity.CENTER_HORIZONTAL
                        margin = dip(20)
                        topMargin = dip(0)
                    }
                }.lparams(width = matchParent, height = wrapContent)
            }
        }.view
    }

    companion object {
        const val TAG = "PrivacyPolicyAcceptanceFragment"
    }
}