package org.jetbrains.kotlinconf.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import com.github.terrakok.navigation3.browser.ChronologicalBrowserNavigation
import com.github.terrakok.navigation3.browser.HierarchicalBrowserNavigation
import com.github.terrakok.navigation3.browser.buildBrowserHistoryFragment
import com.github.terrakok.navigation3.browser.getBrowserHistoryFragmentName
import com.github.terrakok.navigation3.browser.getBrowserHistoryFragmentParameters
import org.jetbrains.kotlinconf.AwardCategoryId
import org.jetbrains.kotlinconf.NomineeId
import org.jetbrains.kotlinconf.PartnerId
import org.jetbrains.kotlinconf.SessionId
import org.jetbrains.kotlinconf.SpeakerId
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.js

@OptIn(ExperimentalWasmJsInterop::class)
private fun isMobileBrowser(): Boolean = js(
    """{
          if (navigator.userAgentData && navigator.userAgentData.mobile) {
            return navigator.userAgentData.mobile;
          }
          if (window.matchMedia && window.matchMedia('(pointer: coarse)').matches) {
            return window.innerWidth <= 820;
          }
          return /Mobi|Android|iPhone|iPad|iPod|IEMobile|Opera Mini|CriOS/i.test(navigator.userAgent);
      }"""
)

private fun AppRoute.toBrowserHistoryFragment() = when (val key = this) {
    AboutAppScreen -> buildBrowserHistoryFragment("about_app")
    AboutConferenceScreen -> buildBrowserHistoryFragment("about_conference")
    AppPrivacyNoticePrompt -> buildBrowserHistoryFragment("privacy_notice_prompt")
    AppPrivacyNoticeScreen -> buildBrowserHistoryFragment("app_privacy_notice")
    AppTermsOfUseScreen -> buildBrowserHistoryFragment("app_terms_of_use")
    CodeOfConductScreen -> buildBrowserHistoryFragment("code_of_conduct")
    is DeveloperMenuScreen -> buildBrowserHistoryFragment("developer_menu")
    LicensesScreen -> buildBrowserHistoryFragment("licenses")
    PartnersScreen -> buildBrowserHistoryFragment("partners")
    SettingsScreen -> buildBrowserHistoryFragment("settings")
    StartNotificationsScreen -> buildBrowserHistoryFragment("notifications")
    StartPrivacyNoticeScreen -> buildBrowserHistoryFragment("privacy_notice")
    TermsOfUseScreen -> buildBrowserHistoryFragment("terms_of_use")
    VisitorPrivacyNoticeScreen -> buildBrowserHistoryFragment("visitor_privacy_notice")
    ScheduleScreen -> buildBrowserHistoryFragment("schedule")
    SpeakersScreen -> buildBrowserHistoryFragment("speakers")
    MapScreen -> buildBrowserHistoryFragment("map")
    InfoScreen -> buildBrowserHistoryFragment("info")
    is NestedMapScreen -> buildBrowserHistoryFragment(
        "map_detail",
        mapOf("roomName" to key.roomName)
    )

    is PartnerDetailScreen -> buildBrowserHistoryFragment(
        "partner",
        mapOf("partnerId" to key.partnerId.id)
    )

    is SessionScreen -> buildBrowserHistoryFragment(
        "session",
        mapOf("sessionId" to key.sessionId.id)
    )

    is SpeakerDetailScreen -> buildBrowserHistoryFragment(
        "speaker",
        mapOf("speakerId" to key.speakerId.id)
    )

    is SingleLicenseScreen -> buildBrowserHistoryFragment(
        "license",
        mapOf("licenseName" to key.licenseName, "licenseText" to key.licenseText)
    )

    is GoldenKodeeFinalistScreen -> buildBrowserHistoryFragment(
        "golden_kodee_finalist",
        mapOf("categoryId" to key.categoryId.id, "nomineeId" to key.nomineeId.id)
    )

    GoldenKodeeScreen -> buildBrowserHistoryFragment("golden_kodee")
}

@Composable
actual fun BrowserIntegration(navState: NavState) {
    val isMobile = remember { isMobileBrowser() }
    if (isMobile) {
        HierarchicalBrowserNavigation(
            currentDestination = remember { derivedStateOf { navState.currentBackstack.lastOrNull() } },
            currentDestinationName = { it?.toBrowserHistoryFragment() },
        )
    } else {
        ChronologicalBrowserNavigation(
            backStack = navState.currentBackstack,
            saveKey = { key -> key.toBrowserHistoryFragment() },
            restoreKey = { fragment ->
                val name = getBrowserHistoryFragmentName(fragment)
                val params = getBrowserHistoryFragmentParameters(fragment)

                when (name) {
                    "about_app" -> AboutAppScreen
                    "about_conference" -> AboutConferenceScreen
                    "privacy_notice_prompt" -> AppPrivacyNoticePrompt
                    "app_privacy_notice" -> AppPrivacyNoticeScreen
                    "app_terms_of_use" -> AppTermsOfUseScreen
                    "code_of_conduct" -> CodeOfConductScreen
                    "developer_menu" -> DeveloperMenuScreen()
                    "licenses" -> LicensesScreen
                    "partners" -> PartnersScreen
                    "settings" -> SettingsScreen
                    "notifications" -> StartNotificationsScreen
                    "privacy_notice" -> StartPrivacyNoticeScreen
                    "terms_of_use" -> TermsOfUseScreen
                    "visitor_privacy_notice" -> VisitorPrivacyNoticeScreen
                    "schedule" -> ScheduleScreen
                    "speakers" -> SpeakersScreen
                    "map" -> MapScreen
                    "info" -> InfoScreen
                    "map_detail" -> params["roomName"]?.let { NestedMapScreen(it) }
                    "partner" -> params["partnerId"]?.let { PartnerDetailScreen(PartnerId(it)) }
                    "session" -> params["sessionId"]?.let { SessionScreen(SessionId(it)) }
                    "speaker" -> params["speakerId"]?.let { SpeakerDetailScreen(SpeakerId(it)) }
                    "license" -> {
                        val licenseName = params["licenseName"]
                        val licenseText = params["licenseText"]
                        if (licenseName != null && licenseText != null) {
                            SingleLicenseScreen(licenseName, licenseText)
                        } else null
                    }

                    "golden_kodee" -> GoldenKodeeScreen
                    "golden_kodee_finalist" -> {
                        val categoryId = params["categoryId"]
                        val nomineeId = params["nomineeId"]
                        if (categoryId != null && nomineeId != null) {
                            GoldenKodeeFinalistScreen(AwardCategoryId(categoryId), NomineeId(nomineeId))
                        } else null
                    }

                    else -> null
                }
            }
        )
    }
}
