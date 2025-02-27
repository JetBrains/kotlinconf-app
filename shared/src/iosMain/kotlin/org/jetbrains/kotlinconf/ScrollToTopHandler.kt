package org.jetbrains.kotlinconf

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.launch
import platform.CoreGraphics.CGPointMake
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.UIKit.UIScrollView
import platform.UIKit.UIScrollViewDelegateProtocol
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun ScrollToTopHandler(scrollState: ScrollState) {
    val coroutineScope = rememberCoroutineScope()
    val scrollViewDelegate = remember {
        object : NSObject(), UIScrollViewDelegateProtocol {
            // Prerequisites for the scroll-to-top gesture to work are (https://developer.apple.com/documentation/uikit/uiscrollview/scrollstotop):
            // * scrollsToTop property set to true
            // * scrollViewShouldScrollToTop should return true
            // * current position shouldn't be at 0
            override fun scrollViewShouldScrollToTop(scrollView: UIScrollView): Boolean = true

            // Sync from Native to Compose
            override fun scrollViewDidScroll(scrollView: UIScrollView) {
                val currentOffset = scrollView.contentOffset.useContents { y.toInt() }
                if (currentOffset != scrollState.value) {
                    coroutineScope.launch {
                        scrollState.scrollTo(currentOffset)
                    }
                }
            }
        }
    }
    val scrollView = remember {
        UIScrollView(CGRectMake(0.0, 0.0, 10000.0, 1.0)).apply {
            scrollsToTop = true
            delegate = scrollViewDelegate
            // without setting content size, scroll to top wouldn't work
            setContentSize(CGSizeMake(10000.0, scrollState.viewportSize.toDouble()))
        }
    }

    // Native view to handle system event
    UIKitView(
        factory = { scrollView },
        modifier = Modifier.height(1.dp).fillMaxWidth()
    )

    // Sync from Compose to Native
    LaunchedEffect(scrollState.value) {
        val currentOffset = scrollView.contentOffset.useContents { y.toInt() }
        if (currentOffset != scrollState.value) {
            scrollView.setContentOffset(CGPointMake(0.0, scrollState.value.toDouble()))
        }
    }
}
