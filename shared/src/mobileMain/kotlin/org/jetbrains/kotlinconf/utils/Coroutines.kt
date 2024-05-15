package org.jetbrains.kotlinconf.utils

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class StateFlowClass<T>(private val delegate: StateFlow<T>) : StateFlow<T> by delegate {
    fun subscribe(block: (T) -> Unit) = GlobalScope.launch(Dispatchers.App) {
        delegate.collect {
            block(it)
        }
    }
}

fun <T> StateFlow<T>.asStateFlowClass(): StateFlowClass<T> = StateFlowClass(this)
