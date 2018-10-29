package org.jetbrains.kotlinconf.presentation

import kotlinx.coroutines.*
import kotlin.coroutines.*

open class CoroutinePresenter(
    private val mainContext: CoroutineContext, // TODO: Use Dispatchers.Main instead when it will be supported on iOS
    private val baseView: BaseView
): CoroutineScope {

    private val job = Job()
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        baseView.showError(throwable)
    }

    override val coroutineContext: CoroutineContext
        get() = mainContext + job + exceptionHandler

    open fun onDestroy() {
        job.cancel()
    }
}