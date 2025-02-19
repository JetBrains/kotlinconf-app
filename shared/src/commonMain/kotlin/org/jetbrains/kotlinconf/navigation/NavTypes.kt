package org.jetbrains.kotlinconf.navigation

import androidx.core.bundle.Bundle
import androidx.navigation.NavType
import org.jetbrains.kotlinconf.SessionId
import org.jetbrains.kotlinconf.SpeakerId

internal val SpeakerIdNavType = object : NavType<SpeakerId>(isNullableAllowed = true) {
    override fun get(bundle: Bundle, key: String): SpeakerId? = bundle.getString(key)?.let(::SpeakerId)
    override fun put(bundle: Bundle, key: String, value: SpeakerId) = bundle.putString(key, value.id)
    override fun parseValue(value: String): SpeakerId = SpeakerId(value)
    override fun serializeAsValue(value: SpeakerId): String = value.id
}

internal val SessionIdNavType = object : NavType<SessionId>(isNullableAllowed = true) {
    override fun get(bundle: Bundle, key: String): SessionId? = bundle.getString(key)?.let(::SessionId)
    override fun put(bundle: Bundle, key: String, value: SessionId) = bundle.putString(key, value.id)
    override fun parseValue(value: String): SessionId = SessionId(value)
    override fun serializeAsValue(value: SessionId): String = value.id
}
