package org.jetbrains.kotlinconf.presentation

import org.jetbrains.kotlinconf.*
import kotlin.coroutines.*

class CodeVerificationPresenter(
    private val uiContext: CoroutineContext,
    private val view: BaseView,
    private val repository: DataRepository
) {

    fun onSubmitButtonClicked(code: String) {
        if (code.isNotBlank()) {
            launchAndCatch(uiContext, view::showError) {
                repository.verifyCode(code)
            }
        }
    }
}