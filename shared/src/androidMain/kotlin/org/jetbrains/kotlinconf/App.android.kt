package org.jetbrains.kotlinconf

import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.cache.memory.maxSizePercent
import com.seiko.imageloader.component.setupDefaultComponents
import com.seiko.imageloader.intercept.bitmapMemoryCacheConfig
import com.seiko.imageloader.intercept.imageMemoryCacheConfig
import com.seiko.imageloader.intercept.painterMemoryCacheConfig
import com.seiko.imageloader.option.androidContext
import okio.Path.Companion.toOkioPath

actual fun createImageLoader(context: ApplicationContext): ImageLoader {
    val app = context.application
    return ImageLoader {
        options {
            androidContext(app)
        }
        components {
            setupDefaultComponents()
        }
        interceptor {
            bitmapMemoryCacheConfig {
                maxSizePercent(app, 0.25)
            }
            imageMemoryCacheConfig {
                maxSize(50)
            }
            painterMemoryCacheConfig {
                maxSize(50)
            }
            diskCacheConfig {
                directory(app.cacheDir.resolve("image_cache").toOkioPath())
                maxSizeBytes(512L * 1024 * 1024)
            }
        }
    }
}