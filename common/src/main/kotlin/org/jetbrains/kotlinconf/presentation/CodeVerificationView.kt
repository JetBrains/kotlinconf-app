package org.jetbrains.kotlinconf.presentation

interface CodeVerificationView : BaseView {
    var termsAccepted: Boolean
    fun setProgress(isLoading: Boolean)
    fun dismissView()
    fun showTermsAcceptanceRequired()
}