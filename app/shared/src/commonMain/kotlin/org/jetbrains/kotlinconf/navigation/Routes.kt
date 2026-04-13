package org.jetbrains.kotlinconf.navigation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.kotlinconf.AwardCategoryId
import org.jetbrains.kotlinconf.NomineeId
import org.jetbrains.kotlinconf.PartnerId
import org.jetbrains.kotlinconf.SessionId
import org.jetbrains.kotlinconf.SpeakerId

@Serializable
sealed interface AppRoute {
    val title: String? get() = null
    val subtitle: String? get() = null
}

@Serializable
sealed interface TopLevelRoute : AppRoute

@Serializable
@SerialName("Schedule")
data object ScheduleScreen : AppRoute, TopLevelRoute

@Serializable
@SerialName("Speakers")
data object SpeakersScreen : AppRoute, TopLevelRoute

@Serializable
@SerialName("Map")
data object MapScreen : AppRoute, TopLevelRoute

@Serializable
@SerialName("GoldenKodee")
data object GoldenKodeeScreen : AppRoute, TopLevelRoute

@Serializable
@SerialName("Info")
data object InfoScreen : AppRoute, TopLevelRoute

@Serializable
@SerialName("AboutConference")
data class AboutConferenceScreen(override val title: String = "") : AppRoute

@Serializable
@SerialName("CodeOfConduct")
data class CodeOfConductScreen(override val title: String = "") : AppRoute

@Serializable
@SerialName("AboutApp")
data class AboutAppScreen(override val title: String = "") : AppRoute

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
data class SettingsScreen(override val title: String = "") : AppRoute

@Serializable
@SerialName("VisitorPrivacyNotice")
data class VisitorPrivacyNoticeScreen(override val title: String = "") : AppRoute

@Serializable
@SerialName("AppPrivacyNotice")
data class AppPrivacyNoticeScreen(override val title: String = "") : AppRoute

@Serializable
@SerialName("TermsOfUse")
data class TermsOfUseScreen(override val title: String = "") : AppRoute

@Serializable
@SerialName("AppTermsOfUse")
data class AppTermsOfUseScreen(override val title: String = "") : AppRoute

@Serializable
@SerialName("Licenses")
data class LicensesScreen(override val title: String = "") : AppRoute

@Serializable
@SerialName("License")
data class SingleLicenseScreen(
    val licenseName: String,
    val licenseText: String,
) : AppRoute {
    override val title: String get() = licenseName
}

@Serializable
@SerialName("Partners")
data class PartnersScreen(override val title: String = "") : AppRoute

@Serializable
@SerialName("Partner")
data class PartnerDetailScreen(val partnerId: PartnerId) : AppRoute

@Serializable
@SerialName("Session")
data class SessionScreen(
    val sessionId: SessionId,
    override val title: String? = null,
) : AppRoute

@Serializable
@SerialName("Speaker")
data class SpeakerDetailScreen(
    val speakerId: SpeakerId,
    override val title: String = "",
    override val subtitle: String = "",
) : AppRoute

@Serializable
@SerialName("MapDetail")
data class NestedMapScreen(val roomName: String) : AppRoute {
    override val title: String get() = roomName
}

@Serializable
@SerialName("GoldenKodeeFinalist")
data class GoldenKodeeFinalistScreen(
    val categoryId: AwardCategoryId,
    val nomineeId: NomineeId,
    override val title: String = "",
    override val subtitle: String = "",
) : AppRoute

@Serializable
@SerialName("DeveloperMenu")
data class DeveloperMenuScreen(val skipWarningDelay: Boolean = false) : AppRoute
