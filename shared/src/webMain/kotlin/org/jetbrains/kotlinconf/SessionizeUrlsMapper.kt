package org.jetbrains.kotlinconf

import com.seiko.imageloader.component.mapper.Mapper
import com.seiko.imageloader.option.Options
import io.ktor.http.*


class SessionizeUrlsMapper : Mapper<Url> {
    override fun map(data: Any, options: Options): Url? {
        if (data !is String) return null
        if (!isApplicable(data)) return null
        if (data.startsWith("https://sessionize.com/")) {
            val newUri = data.replace("https://sessionize.com/",  "$apiEndpoint/sessionize/")
            return Url(newUri)
        }
        return Url(data)
    }

    private fun isApplicable(data: String): Boolean {
        return data.startsWith("http:") || data.startsWith("https:")
    }
}
