package org.jetbrains.kotlinconf.navigation

import kotlinx.serialization.Serializable
import org.jetbrains.kotlinconf.SessionId
import org.jetbrains.kotlinconf.SpeakerId

@Serializable
data object AboutConferenceScreen

@Serializable
data object CodeOfConductScreen

@Serializable
data object AboutAppScreen

@Serializable
data object InfoScreen

@Serializable
data object StartScreens

@Serializable
data object StartPrivacyPolicyScreen

@Serializable
data object StartNotificationsScreen

@Serializable
data object SettingsScreen

@Serializable
data object PrivacyPolicyForVisitorsScreen

@Serializable
data object AppPrivacyPolicyScreen

@Serializable
data object TermsOfUseScreen

@Serializable
data object AppTermsOfUseScreen

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

@Serializable
data object MapScreen
