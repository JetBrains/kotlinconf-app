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
data object ScheduleScreen : AppRoute, TopLevelRoute {
    override val title: String get() = "Schedule"
}

@Serializable
@SerialName("Speakers")
data object SpeakersScreen : AppRoute, TopLevelRoute {
    override val title: String get() = "Speakers"
}

@Serializable
@SerialName("Map")
data object MapScreen : AppRoute, TopLevelRoute {
    override val title: String get() = "Map"
}

@Serializable
@SerialName("GoldenKodee")
data object GoldenKodeeScreen : AppRoute, TopLevelRoute {
    override val title: String get() = "Golden Kodee"
}

@Serializable
@SerialName("Info")
data object InfoScreen : AppRoute, TopLevelRoute {
    override val title: String get() = "Info"
}

@Serializable
@SerialName("AboutConference")
data object AboutConferenceScreen : AppRoute {
    override val title: String get() = "About the Conference"
}

@Serializable
@SerialName("CodeOfConduct")
data object CodeOfConductScreen : AppRoute {
    override val title: String get() = "Code of Conduct"
}

@Serializable
@SerialName("AboutApp")
data object AboutAppScreen : AppRoute {
    override val title: String get() = "About the App"
}

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
data object SettingsScreen : AppRoute {
    override val title: String get() = "Settings"
}

@Serializable
@SerialName("VisitorPrivacyNotice")
data object VisitorPrivacyNoticeScreen : AppRoute {
    override val title: String get() = "Privacy Notice for Visitors"
}

@Serializable
@SerialName("AppPrivacyNotice")
data object AppPrivacyNoticeScreen : AppRoute {
    override val title: String get() = "App Privacy Notice"
}

@Serializable
@SerialName("TermsOfUse")
data object TermsOfUseScreen : AppRoute {
    override val title: String get() = "General Terms and Conditions"
}

@Serializable
@SerialName("AppTermsOfUse")
data object AppTermsOfUseScreen : AppRoute {
    override val title: String get() = "App Terms of Use"
}

@Serializable
@SerialName("Licenses")
data object LicensesScreen : AppRoute {
    override val title: String get() = "Licenses"
}

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
data object PartnersScreen : AppRoute {
    override val title: String get() = "Partners"
}

@Serializable
@SerialName("Partner")
data class PartnerDetailScreen(val partnerId: PartnerId) : AppRoute

@Serializable
@SerialName("Session")
data class SessionScreen(
    val sessionId: SessionId,
    override val title: String?,
) : AppRoute

@Serializable
@SerialName("Speaker")
data class SpeakerDetailScreen(val speakerId: SpeakerId, override val title: String, override val subtitle: String) : AppRoute

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
    override val title: String,
    override val subtitle: String
) : AppRoute

@Serializable
@SerialName("DeveloperMenu")
data class DeveloperMenuScreen(val skipWarningDelay: Boolean = false) : AppRoute
