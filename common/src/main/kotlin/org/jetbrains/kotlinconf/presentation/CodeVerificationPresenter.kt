package org.jetbrains.kotlinconf.presentation

import org.jetbrains.kotlinconf.launchAndCatch
import kotlin.coroutines.CoroutineContext

class CodeVerificationPresenter(
    private val uiContext: CoroutineContext,
    private val view: CodeVerificationView,
    private val repository: DataRepository
) {
    fun verifyCode(code: String) {
        launchAndCatch(uiContext, view::showError) {
            view.setProgress(true)
            repository.verifyCode(code)
            view.dismissView()
        } finally {
            view.setProgress(false)
        }
    }
}