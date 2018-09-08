package com.jetbrains.kotlinconf.presentation

import kotlinx.coroutines.experimental.launch
import org.jetbrains.kotlinconf.ui.NavigationManager
import kotlin.coroutines.experimental.CoroutineContext

class CodeEnterPresenter(private val uiContext: CoroutineContext,
                         private val view: CodeEnterView,
                         private val repository: KotlinConfDataRepository) {

    private val codeValidatedListener: () -> Unit = { view.dismissDialog() }

    fun onCreate() {
        repository.onCodeValidated += codeValidatedListener
    }

    fun onDestroy() {
        repository.onCodeValidated -= codeValidatedListener
    }

    fun submitCode(code: String) {
        launch(uiContext) {
            view.isLoading = true
            repository.submitCode(code)
            view.isLoading = false
        }
    }
}