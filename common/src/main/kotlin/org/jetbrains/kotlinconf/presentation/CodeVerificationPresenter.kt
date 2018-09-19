package org.jetbrains.kotlinconf.presentation

import org.jetbrains.kotlinconf.*
import kotlin.coroutines.*

class CodeVerificationPresenter(
    private val uiContext: CoroutineContext,
    private val view: CodeVerificationView,
    private val repository: DataRepository
) {
    fun onCreate() {
        view.termsAccepted = repository.termsAccepted
    }

    fun onSubmit(code: String) {
        checkTermsAcceptedAnd {
            verifyCode(code)
        }
    }

    fun onCancel() {
        checkTermsAcceptedAnd {
            view.dismissView()
        }
    }

    private fun verifyCode(code: String) {
        launchAndCatch(uiContext, view::showError) {
            view.setProgress(true)
            repository.verifyAndSetCode(code)
            repository.update()
            view.dismissView()
        } finally {
            view.setProgress(false)
        }
    }

    private inline fun checkTermsAcceptedAnd(f: ()->Unit) {
        val termsAccepted = view.termsAccepted
        repository.termsAccepted = termsAccepted
        if(termsAccepted) {
            f()
        } else {
            view.showTermsAcceptanceRequired()
        }
    }
}