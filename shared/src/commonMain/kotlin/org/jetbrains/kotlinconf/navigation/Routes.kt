package org.jetbrains.kotlinconf.navigation

import kotlinx.serialization.Serializable
import org.jetbrains.kotlinconf.PartnerId
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
data object PrivacyPolicyScreen

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
data object LicensesScreen

@Serializable
data class SingleLicenseScreen(val licenseName: String, val licenseText: String)

@Serializable
data object PartnersScreen

@Serializable
data class PartnerDetailsScreen(val partnerId: PartnerId)

@Serializable
data object MainScreen

@Serializable
data object ScheduleScreen

@Serializable
data class SessionScreen(val sessionId: SessionId)

@Serializable
data object SpeakersScreen

@Serializable
data class SpeakerDetailsScreen(val speakerId: SpeakerId)

@Serializable
data object MapScreen

@Serializable
data object NewsListScreen

@Serializable
data class NewsDetailScreen(val newsId: String)
