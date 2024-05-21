package org.jetbrains.kotlinconf

import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.cache.memory.maxSizePercent
import com.seiko.imageloader.component.setupDefaultComponents
import com.seiko.imageloader.intercept.bitmapMemoryCacheConfig
import com.seiko.imageloader.intercept.imageMemoryCacheConfig
import com.seiko.imageloader.intercept.painterMemoryCacheConfig
import kotlinx.browser.window
import org.w3c.dom.url.URL

val defaultSessionizeProxy = "https://hi-kotlin-wasm.bashorov.workers.dev" 

actual fun createImageLoader(context: ApplicationContext): ImageLoader {
    return ImageLoader {
        components {
            val proxy = URL(window.location.href).searchParams.get("sessionizeProxy") ?: defaultSessionizeProxy
            add(SessionizeUrlsMapper(proxy))
            setupDefaultComponents { HTTP_CLIENT }
        }
        interceptor {
            bitmapMemoryCacheConfig {
                maxSizePercent(0.25)
            }
            imageMemoryCacheConfig {
                maxSize(50)
            }
            painterMemoryCacheConfig {
                maxSize(50)
            }
        }
    }
}