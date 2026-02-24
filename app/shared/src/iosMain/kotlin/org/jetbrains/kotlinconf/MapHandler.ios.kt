@file:OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)

package org.jetbrains.kotlinconf

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSCharacterSet
import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.Foundation.URLQueryAllowedCharacterSet
import platform.Foundation.create
import platform.Foundation.stringByAddingPercentEncodingWithAllowedCharacters
import platform.UIKit.UIApplication

@Composable
actual fun rememberMapHandler(): MapHandler = remember { IosMapHandler }

private object IosMapHandler : MapHandler {
    override fun openNavigation(address: String) {
        val nsAddress = NSString.create(string = address)
        val encodedAddress = nsAddress.stringByAddingPercentEncodingWithAllowedCharacters(
            NSCharacterSet.URLQueryAllowedCharacterSet
        ) ?: ""

        val url = NSURL(string = "geo-navigation:///place?address=$encodedAddress")

        UIApplication.sharedApplication.openURL(url = url, options = emptyMap<Any?, Any>()) { _ -> }
    }
}
