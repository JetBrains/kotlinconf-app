package org.jetbrains.kotlinconf.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.kotlinconf.PartnerId
import org.jetbrains.kotlinconf.SessionId
import org.jetbrains.kotlinconf.SpeakerId

@Serializable
sealed interface AppRoute : NavKey

@Serializable
@SerialName("AboutConference")
data object AboutConferenceScreen : AppRoute

@Serializable
@SerialName("CodeOfConduct")
data object CodeOfConductScreen : AppRoute

@Serializable
@SerialName("AboutApp")
data object AboutAppScreen : AppRoute

@Serializable
@SerialName("WelcomePrivacyNotice")
data object StartPrivacyNoticeScreen : AppRoute

@Serializable
@SerialName("WelcomeSetupNotifications")
data object StartNotificationsScreen : AppRoute

@Serializable
@SerialName("AppPrivacyNoticePrompt")
data object AppPrivacyNoticePrompt : AppRoute

@Serializable
@SerialName("Settings")
data object SettingsScreen : AppRoute

@Serializable
@SerialName("VisitorPrivacyNotice")
data object VisitorPrivacyNoticeScreen : AppRoute

@Serializable
@SerialName("AppPrivacyNotice")
data object AppPrivacyNoticeScreen : AppRoute

@Serializable
@SerialName("TermsOfUse")
data object TermsOfUseScreen : AppRoute

@Serializable
@SerialName("AppTermsOfUse")
data object AppTermsOfUseScreen : AppRoute

@Serializable
@SerialName("Licenses")
data object LicensesScreen : AppRoute

@Serializable
@SerialName("License")
data class SingleLicenseScreen(
    val licenseName: String,
    val licenseText: String,
) : AppRoute

@Serializable
@SerialName("Partners")
data object PartnersScreen : AppRoute

@Serializable
@SerialName("Partner")
data class PartnerDetailScreen(val partnerId: PartnerId) : AppRoute

@Serializable
@SerialName("Main")
data object MainScreen : AppRoute

@Serializable
@SerialName("Session")
data class SessionScreen(
    val sessionId: SessionId,
    val openedForFeedback: Boolean = false,
) : AppRoute

@Serializable
@SerialName("Speaker")
data class SpeakerDetailScreen(val speakerId: SpeakerId) : AppRoute

@Serializable
@SerialName("MapDetail")
data class NestedMapScreen(val roomName: String) : AppRoute

@Serializable
@SerialName("DeveloperMenu")
data object DeveloperMenuScreen : AppRoute
