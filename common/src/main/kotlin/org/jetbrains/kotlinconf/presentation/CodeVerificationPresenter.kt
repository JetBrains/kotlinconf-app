package org.jetbrains.kotlinconf.presentation

import org.jetbrains.kotlinconf.*
import kotlin.coroutines.*

class CodeVerificationPresenter(
    private val uiContext: CoroutineContext,
    private val view: CodeVerificationView,
    private val repository: DataRepository
) {

    fun onSubmitButtonClicked(code: String) {
        if (code.isNotBlank()) {
            launchAndCatch(uiContext, view::showError) {
                repository.verifyAndSetCode(code)
                repository.update()
                view.dismissView()
            }
        }
    }
}