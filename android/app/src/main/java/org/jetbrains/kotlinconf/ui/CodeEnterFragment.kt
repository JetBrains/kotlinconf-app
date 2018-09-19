package org.jetbrains.kotlinconf.ui

import android.app.*
import android.content.*
import android.graphics.*
import android.net.*
import android.os.*
import android.support.v4.app.FragmentManager
import android.support.v7.app.AlertDialog
import android.text.*
import android.view.*
import android.view.Gravity.*
import android.view.View.*
import android.view.inputmethod.EditorInfo.*
import android.widget.*
import kotlinx.coroutines.android.*
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.*
import org.jetbrains.kotlinconf.*
import org.jetbrains.kotlinconf.presentation.*


class CodeEnterFragment : BaseDialogFragment(), CodeVerificationView {
    private lateinit var submitButton: Button
    private lateinit var checkBox: CheckBox
    private lateinit var codeEditText: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var dialogContent: View

    private val repository by lazy { (activity!!.application as KotlinConfApplication).dataRepository }
    private val presenter by lazy { CodeVerificationPresenter(UI, this, repository) }

    override var termsAccepted: Boolean
        get() = checkBox.isChecked
        set(accepted) {
            checkBox.isChecked = accepted
        }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(context!!)
            .setView(createView())
            .setCancelable(false)
            .setPositiveButton(R.string.submit_button) { _, _ -> /* no-op */ }
            .setNeutralButton(R.string.skip_button) { _, _ -> /* no-op */ }
            .create()
            .apply {
                setOnShowListener {
                    submitButton = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
                    submitButton.isEnabled = false
                    // the listener is set here to prevent the dialog from being dismissed on click
                    submitButton.setOnClickListener { _ ->
                        val code = codeEditText.text.toString()
                        presenter.onSubmit(code)
                    }

                    val skipButton = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_NEUTRAL)
                    // the listener is set here to prevent the dialog from being dismissed on click
                    skipButton.setOnClickListener { _ ->
                        presenter.onCancel()
                    }

                    dialog.setCanceledOnTouchOutside(false)
                    dialog.setOnCancelListener { activity?.finishAffinity() }
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.onCreate()
    }

    override fun setProgress(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) VISIBLE else INVISIBLE
        dialogContent.visibility = if (isLoading) INVISIBLE else VISIBLE
        submitButton.visibility = if (isLoading) INVISIBLE else VISIBLE
    }

    override fun dismissView() {
        isDisplayed = false
        dismiss()
    }

    override fun showTermsAcceptanceRequired() {
        toast(R.string.msg_need_to_accept_terms)
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
                                        // TODO: This is logic, should be on the presenter
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
        var isDisplayed = false

        fun show(fragmentManager: FragmentManager) {
            if(!isDisplayed) {
                isDisplayed = true
                CodeEnterFragment().show(fragmentManager, CodeEnterFragment.TAG)
            }
        }
    }
}