package org.jetbrains.kotlinconf.navigation

import androidx.navigation.NavType
import androidx.savedstate.SavedState
import androidx.savedstate.read
import androidx.savedstate.write
import org.jetbrains.kotlinconf.PartnerId
import org.jetbrains.kotlinconf.SessionId
import org.jetbrains.kotlinconf.SpeakerId

internal val SpeakerIdNavType = object : NavType<SpeakerId>(isNullableAllowed = false) {
    override fun get(bundle: SavedState, key: String): SpeakerId = bundle.read { SpeakerId(getString(key)) }
    override fun put(bundle: SavedState, key: String, value: SpeakerId) = bundle.write { putString(key, value.id) }
    override fun parseValue(value: String): SpeakerId = SpeakerId(value)
    override fun serializeAsValue(value: SpeakerId): String = value.id
}

internal val SessionIdNavType = object : NavType<SessionId>(isNullableAllowed = false) {
    override fun get(bundle: SavedState, key: String): SessionId = bundle.read { SessionId(getString(key)) }
    override fun put(bundle: SavedState, key: String, value: SessionId) = bundle.write { putString(key, value.id) }
    override fun parseValue(value: String): SessionId = SessionId(value)
    override fun serializeAsValue(value: SessionId): String = value.id
}

internal val PartnerIdNavType = object : NavType<PartnerId>(isNullableAllowed = false) {
    override fun get(bundle: SavedState, key: String): PartnerId = bundle.read { PartnerId(getString(key)) }
    override fun put(bundle: SavedState, key: String, value: PartnerId) = bundle.write { putString(key, value.id) }
    override fun parseValue(value: String): PartnerId = PartnerId(value)
    override fun serializeAsValue(value: PartnerId): String = value.id
}
