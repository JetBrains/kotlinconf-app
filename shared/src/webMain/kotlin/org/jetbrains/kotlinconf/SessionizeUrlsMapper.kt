package org.jetbrains.kotlinconf

import com.seiko.imageloader.component.mapper.Mapper
import com.seiko.imageloader.option.Options
import io.ktor.http.*

class SessionizeUrlsMapper : Mapper<Url> {
    override fun map(data: Any, options: Options): Url? {
        if (data !is String) return null

        if (data.startsWith("https://sessionize.com/image/")) {
            val newUri = KOTLINCONF_APP_BACKEND + data.removePrefix("https://")
            return Url(newUri)
        }

        return Url(data)
    }
}
