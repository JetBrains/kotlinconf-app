package org.jetbrains.kotlinconf.ui

import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.intercept.Interceptor
import coil3.request.ImageResult
import coil3.util.DebugLogger

fun initCoil() {
    SingletonImageLoader.setSafe {
        ImageLoader.Builder(PlatformContext.INSTANCE)
            .components {
                add(SessionizeImageInterceptor())
            }
            .logger(DebugLogger())
            .build()
    }
}

private val sessionizeBaseUrl = "https://sessionize.com/"
private val sessionizeProxy = "https://sessionize-com.labs.jb.gg/"

private class SessionizeImageInterceptor : Interceptor {
    override suspend fun intercept(chain: Interceptor.Chain): ImageResult {
        val originalRequest = chain.request
        val data = originalRequest.data

        val newChain = if (data is String && data.startsWith(sessionizeBaseUrl)) {
            val newUri = data.replace(sessionizeBaseUrl, sessionizeProxy)
            val newRequest = originalRequest.newBuilder().data(newUri).build()
            chain.withRequest(newRequest)
        } else {
            chain
        }

        return newChain.proceed()
    }
}
