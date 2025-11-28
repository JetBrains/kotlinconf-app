package org.jetbrains.kotlinconf.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.github.terrakok.navigation3.browser.ChronologicalBrowserNavigation
import com.github.terrakok.navigation3.browser.HierarchicalBrowserNavigation
import com.github.terrakok.navigation3.browser.buildBrowserHistoryFragment
import com.github.terrakok.navigation3.browser.getBrowserHistoryFragmentName
import com.github.terrakok.navigation3.browser.getBrowserHistoryFragmentParameters
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
    DeveloperMenuScreen -> buildBrowserHistoryFragment("developer_menu")
    LicensesScreen -> buildBrowserHistoryFragment("licenses")
    MainScreen -> buildBrowserHistoryFragment("main")
    PartnersScreen -> buildBrowserHistoryFragment("partners")
    SettingsScreen -> buildBrowserHistoryFragment("settings")
    StartNotificationsScreen -> buildBrowserHistoryFragment("notifications")
    StartPrivacyNoticeScreen -> buildBrowserHistoryFragment("privacy_notice")
    TermsOfUseScreen -> buildBrowserHistoryFragment("terms_of_use")
    VisitorPrivacyNoticeScreen -> buildBrowserHistoryFragment("visitor_privacy_notice")
    is NestedMapScreen -> buildBrowserHistoryFragment("map", mapOf("roomName" to key.roomName))
    is PartnerDetailScreen -> buildBrowserHistoryFragment("partner", mapOf("partnerId" to key.partnerId.id))
    is SessionScreen -> buildBrowserHistoryFragment("session", mapOf("sessionId" to key.sessionId.id))
    is SpeakerDetailScreen -> buildBrowserHistoryFragment("speaker", mapOf("speakerId" to key.speakerId.id))
    is SingleLicenseScreen -> buildBrowserHistoryFragment(
        "license",
        mapOf("licenseName" to key.licenseName, "licenseText" to key.licenseText)
    )
}

@Composable
actual fun BrowserIntegration(backStack: SnapshotStateList<AppRoute>) {
    val isMobile = remember { isMobileBrowser() }
    if (isMobile) {
        HierarchicalBrowserNavigation {
            backStack.lastOrNull()?.toBrowserHistoryFragment()
        }
    } else {
        ChronologicalBrowserNavigation(
            backStack = backStack,
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
                    "developer_menu" -> DeveloperMenuScreen
                    "licenses" -> LicensesScreen
                    "main" -> MainScreen
                    "partners" -> PartnersScreen
                    "settings" -> SettingsScreen
                    "notifications" -> StartNotificationsScreen
                    "privacy_notice" -> StartPrivacyNoticeScreen
                    "terms_of_use" -> TermsOfUseScreen
                    "visitor_privacy_notice" -> VisitorPrivacyNoticeScreen
                    "map" -> params["roomName"]?.let { NestedMapScreen(it) }
                    "partner" -> params["partnerId"]?.let { PartnerDetailScreen(PartnerId(it)) }
                    "session" -> params["sessionId"]?.let { SessionScreen(SessionId(it)) }
                    "speaker" -> params["speakerId"]?.let { SpeakerDetailScreen(SpeakerId(it)) }
                    "license" -> SingleLicenseScreen(params["licenseName"]!!, params["licenseText"]!!)
                    else -> null
                }
            }
        )
    }
}