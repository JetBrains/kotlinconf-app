package org.jetbrains.kotlinconf.utils

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import org.jetbrains.kotlinconf.ui.components.NormalErrorWithLoading

sealed class ErrorLoadingState<out T : Any> {
    data object Loading : ErrorLoadingState<Nothing>()
    data object Error : ErrorLoadingState<Nothing>()
    data class Content<T : Any>(val data: T) : ErrorLoadingState<T>()
}

@Composable
fun <T : Any> ErrorLoadingContent(
    state: ErrorLoadingState<T>,
    errorMessage: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (T) -> Unit,
) {
    AnimatedContent(
        state,
        modifier = modifier.clipToBounds(),
        contentKey = {
            when (it) {
                is ErrorLoadingState.Content -> 1
                ErrorLoadingState.Error, ErrorLoadingState.Loading -> 2
            }
        },
        transitionSpec = { FadingAnimationSpec }
    ) { targetState ->
        when (targetState) {
            is ErrorLoadingState.Content -> content(targetState.data)
            ErrorLoadingState.Loading, ErrorLoadingState.Error -> {
                NormalErrorWithLoading(
                    message = errorMessage,
                    isLoading = targetState is ErrorLoadingState.Loading,
                    modifier = Modifier.fillMaxSize(),
                    onRetry = onRetry,
                )
            }
        }
    }
}
