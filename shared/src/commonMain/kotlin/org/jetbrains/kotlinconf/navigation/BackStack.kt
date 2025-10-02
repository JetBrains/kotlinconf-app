package org.jetbrains.kotlinconf.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember

@Composable
fun <T : Any> rememberBackstack(initial: T): BackStack<T> = remember { BackStack(initial) }

class BackStack<T>(initial: T) {
    private val _backStack = mutableStateListOf(initial)
    val backStack: List<T> get() = _backStack

    fun edit(actions: MutableList<T>.() -> Unit) {
        _backStack.actions()
    }

    fun add(element: T, clearOthers: Boolean = false) {
        _backStack.add(element)

        if (clearOthers) {
            _backStack.removeRange(0, _backStack.lastIndex)
        }
    }

    fun pop() {
        _backStack.removeAt(_backStack.lastIndex)
    }
}
