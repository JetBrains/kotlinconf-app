package org.jetbrains.kotlinconf.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.compose.serialization.serializers.SnapshotStateListSerializer
import kotlinx.coroutines.channels.Channel
import org.jetbrains.kotlinconf.ConferenceService
import org.jetbrains.kotlinconf.LocalAppGraph
import org.jetbrains.kotlinconf.LocalFlags
import org.jetbrains.kotlinconf.LocalNotificationId
import org.jetbrains.kotlinconf.SessionId
import org.jetbrains.kotlinconf.URLs
import org.jetbrains.kotlinconf.screens.AboutAppScreen
import org.jetbrains.kotlinconf.screens.AboutConference
import org.jetbrains.kotlinconf.screens.AppPrivacyNotice
import org.jetbrains.kotlinconf.screens.AppPrivacyNoticePrompt
import org.jetbrains.kotlinconf.screens.AppTermsOfUse
import org.jetbrains.kotlinconf.screens.CodeOfConduct
import org.jetbrains.kotlinconf.screens.DeveloperMenuScreen
import org.jetbrains.kotlinconf.screens.InfoScreen
import org.jetbrains.kotlinconf.screens.LicensesScreen
import org.jetbrains.kotlinconf.screens.MapScreen
import org.jetbrains.kotlinconf.screens.NestedMapScreen
import org.jetbrains.kotlinconf.screens.PartnerDetailScreen
import org.jetbrains.kotlinconf.screens.PartnersScreen
import org.jetbrains.kotlinconf.screens.ScheduleScreen
import org.jetbrains.kotlinconf.screens.SessionScreen
import org.jetbrains.kotlinconf.screens.SettingsScreen
import org.jetbrains.kotlinconf.screens.SingleLicenseScreen
import org.jetbrains.kotlinconf.screens.SpeakerDetailScreen
import org.jetbrains.kotlinconf.screens.SpeakersScreen
import org.jetbrains.kotlinconf.screens.StartNotificationsScreen
import org.jetbrains.kotlinconf.screens.VisitorPrivacyNotice
import org.jetbrains.kotlinconf.screens.VisitorTermsOfUse
import org.jetbrains.kotlinconf.utils.getStoreUrl

fun navigateByLocalNotificationId(notificationId: String) {
    LocalNotificationId.parse(notificationId)?.let {
        when (it.type) {
            LocalNotificationId.Type.SessionStart, LocalNotificationId.Type.SessionEnd ->
                navigateToSession(SessionId(it.id))
        }
    }
}

fun navigateToSession(sessionId: SessionId) {
    notificationNavRequests.trySend(SessionScreen(sessionId))
}

private val notificationNavRequests = Channel<AppRoute>(capacity = 1)

@Composable
private fun NotificationHandler(backStack: MutableList<AppRoute>) {
    LaunchedEffect(Unit) {
        while (true) {
            backStack.add(notificationNavRequests.receive())
        }
    }
}

@Composable
internal fun KotlinConfNavHost(isOnboardingComplete: Boolean) {
    val backstack: MutableList<AppRoute> =
        rememberSerializable(serializer = SnapshotStateListSerializer()) {
            val startDestination =
                if (isOnboardingComplete) ScheduleScreen else StartPrivacyNoticeScreen
            mutableStateListOf(startDestination)
        }

    // TODO Integrate with browser navigation here https://github.com/JetBrains/kotlinconf-app/issues/557

    NotificationHandler(backstack)

    val mainBackEnabled = LocalFlags.current.enableBackOnMainScreens
    val onBack = {
        if (backstack.last() is MainRoute && !mainBackEnabled) {
            // Ignore back nav
        } else if (backstack.size > 1) {
            backstack.removeLastOrNull()
        }
    }

    NavScaffold(backstack = backstack) {
        NavDisplay(
            backStack = backstack,
            onBack = onBack,
            entryProvider = entryProvider {
                screens(backstack, onBack)
            },
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            ),
        )
    }
}

private val noAnimationTransition = EnterTransition.None togetherWith ExitTransition.None

private val noAnimationMetadata =
    NavDisplay.transitionSpec { noAnimationTransition } +
            NavDisplay.popTransitionSpec { noAnimationTransition } +
            NavDisplay.predictivePopTransitionSpec { noAnimationTransition }

private fun EntryProviderScope<AppRoute>.screens(
    backStack: MutableList<AppRoute>,
    onBack: () -> Unit,
) {
    entry<StartPrivacyNoticeScreen> {
        val skipNotifications = LocalFlags.current.supportsNotifications.not()
        AppPrivacyNoticePrompt(
            onRejectNotice = {
                if (skipNotifications) {
                    backStack.clear()
                    backStack.add(ScheduleScreen)
                } else {
                    backStack.add(StartNotificationsScreen)
                }
            },
            onAcceptNotice = {
                if (skipNotifications) {
                    backStack.clear()
                    backStack.add(ScheduleScreen)
                } else {
                    backStack.add(StartNotificationsScreen)
                }
            },
            onAppTermsOfUse = { backStack.add(AppTermsOfUseScreen) },
            confirmationRequired = false,
        )
    }
    entry<StartNotificationsScreen> {
        StartNotificationsScreen(
            onDone = {
                backStack.clear()
                backStack.add(ScheduleScreen)
            }
        )
    }

    entry<ScheduleScreen>(metadata = noAnimationMetadata) {
        val service: ConferenceService = LocalAppGraph.current.conferenceService
        LaunchedEffect(Unit) {
            service.completeOnboarding()
        }
        ScheduleScreen(
            onSession = { backStack.add(SessionScreen(it)) },
            onPrivacyNoticeNeeded = { backStack.add(AppPrivacyNoticePrompt) },
            onRequestFeedbackWithComment = { sessionId ->
                backStack.add(SessionScreen(sessionId, openedForFeedback = true))
            },
        )
    }

    entry<SpeakersScreen>(metadata = noAnimationMetadata) {
        SpeakersScreen(
            onSpeaker = { backStack.add(SpeakerDetailScreen(it)) }
        )
    }

    entry<MapScreen>(metadata = noAnimationMetadata) {
        MapScreen()
    }

    entry<InfoScreen>(metadata = noAnimationMetadata) {
        val uriHandler = LocalUriHandler.current
        InfoScreen(
            onAboutConf = { backStack.add(AboutConferenceScreen) },
            onAboutApp = { backStack.add(AboutAppScreen) },
            onOurPartners = { backStack.add(PartnersScreen) },
            onCodeOfConduct = { backStack.add(CodeOfConductScreen) },
            onTwitter = { uriHandler.openUri(URLs.TWITTER) },
            onSlack = { uriHandler.openUri(URLs.SLACK) },
            onBluesky = { uriHandler.openUri(URLs.BLUESKY) },
            onSettings = { backStack.add(SettingsScreen) },
        )
    }

    entry<SpeakerDetailScreen> {
        SpeakerDetailScreen(
            speakerId = it.speakerId,
            onBack = onBack,
            onSession = { sessionId -> backStack.add(SessionScreen(sessionId)) },
        )
    }
    entry<SessionScreen> {
        val urlHandler = LocalUriHandler.current
        SessionScreen(
            sessionId = it.sessionId,
            openedForFeedback = it.openedForFeedback,
            onBack = onBack,
            onPrivacyNoticeNeeded = { backStack.add(AppPrivacyNoticePrompt) },
            onSpeaker = { speakerId -> backStack.add(SpeakerDetailScreen(speakerId)) },
            onWatchVideo = { videoUrl -> urlHandler.openUri(videoUrl) },
            onNavigateToMap = { roomName ->
                backStack.add(NestedMapScreen(roomName))
            },
        )
    }

    entry<AboutAppScreen> {
        val uriHandler = LocalUriHandler.current
        AboutAppScreen(
            onBack = onBack,
            onGitHubRepo = { uriHandler.openUri(URLs.GITHUB_REPO) },
            onRateApp = { getStoreUrl()?.let { uriHandler.openUri(it) } },
            onPrivacyNotice = { backStack.add(AppPrivacyNoticeScreen) },
            onTermsOfUse = { backStack.add(AppTermsOfUseScreen) },
            onLicenses = { backStack.add(LicensesScreen) },
            onJunie = { uriHandler.openUri(URLs.JUNIE_LANDING_PAGE) },
            onDeveloperMenu = { backStack.add(DeveloperMenuScreen) },
        )
    }
    entry<LicensesScreen> {
        LicensesScreen(
            onLicenseClick = { licenseName, licenseText ->
                backStack.add(SingleLicenseScreen(licenseName, licenseText))
            },
            onBack = onBack,
        )
    }
    entry<SingleLicenseScreen> {
        SingleLicenseScreen(
            licenseName = it.licenseName,
            licenseContent = it.licenseText,
            onBack = onBack,
        )
    }
    entry<AboutConferenceScreen> {
        val urlHandler = LocalUriHandler.current
        AboutConference(
            onPrivacyNotice = { backStack.add(VisitorPrivacyNoticeScreen) },
            onGeneralTerms = { backStack.add(TermsOfUseScreen) },
            onWebsiteLink = { urlHandler.openUri(URLs.KOTLINCONF_HOMEPAGE) },
            onBack = onBack,
            onSpeaker = { speakerId -> backStack.add(SpeakerDetailScreen(speakerId)) },
        )
    }
    entry<CodeOfConductScreen> {
        CodeOfConduct(onBack = onBack)
    }
    entry<SettingsScreen> {
        SettingsScreen(onBack = onBack)
    }
    entry<VisitorPrivacyNoticeScreen> {
        VisitorPrivacyNotice(onBack = onBack)
    }
    entry<AppPrivacyNoticeScreen> {
        AppPrivacyNotice(
            onBack = onBack,
            onAppTermsOfUse = { backStack.add(AppTermsOfUseScreen) },
        )
    }
    entry<TermsOfUseScreen> {
        VisitorTermsOfUse(
            onBack = onBack,
            onCodeOfConduct = { backStack.add(CodeOfConductScreen) },
            onVisitorPrivacyNotice = { backStack.add(VisitorPrivacyNoticeScreen) },
        )
    }
    entry<AppTermsOfUseScreen> {
        AppTermsOfUse(
            onBack = onBack,
            onAppPrivacyNotice = {
                backStack.add(AppPrivacyNoticeScreen)
            },
        )
    }
    entry<PartnersScreen> {
        PartnersScreen(
            onBack = onBack,
            onPartnerDetail = { partnerId ->
                backStack.add(PartnerDetailScreen(partnerId))
            }
        )
    }
    entry<PartnerDetailScreen> {
        PartnerDetailScreen(
            partnerId = it.partnerId,
            onBack = onBack,
        )
    }

    entry<AppPrivacyNoticePrompt> {
        AppPrivacyNoticePrompt(
            onRejectNotice = onBack,
            onAcceptNotice = onBack,
            onAppTermsOfUse = { backStack.add(AppTermsOfUseScreen) },
            confirmationRequired = true,
        )
    }

    entry<DeveloperMenuScreen> {
        DeveloperMenuScreen(onBack = onBack)
    }

    entry<NestedMapScreen> {
        NestedMapScreen(
            roomName = it.roomName,
            onBack = onBack,
        )
    }
}
