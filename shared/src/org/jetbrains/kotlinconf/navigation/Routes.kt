package org.jetbrains.kotlinconf.navigation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.kotlinconf.PartnerId
import org.jetbrains.kotlinconf.SessionId
import org.jetbrains.kotlinconf.SpeakerId

@Serializable
@SerialName("AboutConference")
data object AboutConferenceScreen

@Serializable
@SerialName("CodeOfConduct")
data object CodeOfConductScreen

@Serializable
@SerialName("AboutApp")
data object AboutAppScreen

@Serializable
@SerialName("Info")
data object InfoScreen

@Serializable
@SerialName("Welcome")
data object StartScreens

@Serializable
@SerialName("WelcomePrivacyNotice")
data object StartPrivacyNoticeScreen

@Serializable
@SerialName("WelcomeSetupNotifications")
data object StartNotificationsScreen

@Serializable
@SerialName("AppPrivacyNoticePrompt")
data object AppPrivacyNoticePrompt

@Serializable
@SerialName("Settings")
data object SettingsScreen

@Serializable
@SerialName("VisitorPrivacyNotice")
data object VisitorPrivacyNoticeScreen

@Serializable
@SerialName("AppPrivacyNotice")
data object AppPrivacyNoticeScreen

@Serializable
@SerialName("TermsOfUse")
data object TermsOfUseScreen

@Serializable
@SerialName("AppTermsOfUse")
data object AppTermsOfUseScreen

@Serializable
@SerialName("Licenses")
data object LicensesScreen

@Serializable
@SerialName("License")
data class SingleLicenseScreen(
    val licenseName: String,
    val licenseText: String,
)

@Serializable
@SerialName("Partners")
data object PartnersScreen

@Serializable
@SerialName("Partner")
data class PartnerDetailScreen(val partnerId: PartnerId)

@Serializable
@SerialName("Main")
data object MainScreen

@Serializable
@SerialName("Schedule")
data object ScheduleScreen

@Serializable
@SerialName("Session")
data class SessionScreen(
    val sessionId: SessionId,
    val openedForFeedback: Boolean = false,
)

@Serializable
@SerialName("Speakers")
data object SpeakersScreen

@Serializable
@SerialName("Speaker")
data class SpeakerDetailScreen(val speakerId: SpeakerId)

@Serializable
@SerialName("Map")
data object MapScreen

@Serializable
@SerialName("MapDetail")
data class NestedMapScreen(val roomName: String)

@Serializable
@SerialName("News")
data object NewsListScreen

@Serializable
@SerialName("News")
data class NewsDetailScreen(val newsId: String)

@Serializable
@SerialName("DeveloperMenu")
data object DeveloperMenuScreen
