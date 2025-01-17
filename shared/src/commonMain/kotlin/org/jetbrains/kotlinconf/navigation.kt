package org.jetbrains.kotlinconf

import androidx.compose.runtime.compositionLocalOf
import androidx.core.bundle.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavType
import kotlinx.serialization.Serializable


internal val LocalNavController = compositionLocalOf<NavController> {
    error("No local NavController")
}

@Serializable
data object StartScreen

@Serializable
data object AboutConferenceScreen

@Serializable
data object CodeOfConductScreen

@Serializable
data object AboutAppScreen

@Serializable
data object InfoScreen

@Serializable
data object StartPrivacyPolicyScreen

@Serializable
data object StartNotificationsScreen

@Serializable
data object SettingsScreen

@Serializable
data object PrivacyPolicyForVisitorsScreen

@Serializable
data object TermsOfUseScreen

@Serializable
data object PartnersScreen

@Serializable
data class PartnerDetailsScreen(val partnerId: String)

@Serializable
data object MainScreen

@Serializable
data object ScheduleScreen

@Serializable
data class TalkDetailsScreen(val talkId: SessionId)

@Serializable
data object SpeakersScreen

@Serializable
data class SpeakerDetailsScreen(val speakerId: SpeakerId)

internal val SpeakerIdNavType = object : NavType<SpeakerId>(isNullableAllowed = true) {
    override fun get(bundle: Bundle, key: String): SpeakerId? = bundle.getString(key)?.let(::SpeakerId)
    override fun parseValue(value: String): SpeakerId = SpeakerId(value)
    override fun put(bundle: Bundle, key: String, value: SpeakerId) = bundle.putString(key, value.id)
}

internal val SessionIdNavType = object : NavType<SessionId>(isNullableAllowed = true) {
    override fun get(bundle: Bundle, key: String): SessionId? = bundle.getString(key)?.let(::SessionId)
    override fun parseValue(value: String): SessionId = SessionId(value)
    override fun put(bundle: Bundle, key: String, value: SessionId) = bundle.putString(key, value.id)
}
