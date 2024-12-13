package org.jetbrains.kotlinconf

import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavController
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
data object SettingsScreen

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
