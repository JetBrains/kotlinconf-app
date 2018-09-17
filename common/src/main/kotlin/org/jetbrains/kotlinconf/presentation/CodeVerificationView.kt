package org.jetbrains.kotlinconf.presentation

interface CodeVerificationView : BaseView {
    fun setProgress(isLoading: Boolean)
    fun dismissView()
}