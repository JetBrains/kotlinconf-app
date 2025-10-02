package org.jetbrains.kotlinconf.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalUriHandler
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.rememberSceneSetupNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.defaultPopTransitionSpec
import kotlinx.coroutines.channels.Channel
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
import org.jetbrains.kotlinconf.screens.LicensesScreen
import org.jetbrains.kotlinconf.screens.MainScreen
import org.jetbrains.kotlinconf.screens.NestedMapScreen
import org.jetbrains.kotlinconf.screens.NewsDetailScreen
import org.jetbrains.kotlinconf.screens.NewsListScreen
import org.jetbrains.kotlinconf.screens.PartnerDetailScreen
import org.jetbrains.kotlinconf.screens.PartnersScreen
import org.jetbrains.kotlinconf.screens.SessionScreen
import org.jetbrains.kotlinconf.screens.SettingsScreen
import org.jetbrains.kotlinconf.screens.SingleLicenseScreen
import org.jetbrains.kotlinconf.screens.SpeakerDetailScreen
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

fun navigateToNews(newsId: String) {
    notificationNavRequests.trySend(NewsDetailScreen(newsId = newsId))
}

private val notificationNavRequests = Channel<Any>(capacity = 1)

@Composable
private fun NotificationHandler(backStack: BackStack<Any>) {
    LaunchedEffect(Unit) {
        while (true) {
            val destination = notificationNavRequests.receive()
            backStack.add(destination)
        }
    }
}

@Composable
internal fun KotlinConfNavHost(
    isOnboardingComplete: Boolean,
    popTransactionSpec: (AnimatedContentTransitionScope<Scene<Any>>.() -> ContentTransform)?,
) {
    // TODO: make this saveable!
    val startDestination = if (isOnboardingComplete) MainScreen else StartPrivacyNoticeScreen
    val appBackStack: BackStack<Any> = rememberBackstack(startDestination)

    NotificationHandler(appBackStack)
    //PlatformNavHandler(navController)

    NavDisplay(
        backStack = appBackStack.backStack,
        entryProvider = entryProvider {
            screens(appBackStack)
        },
        entryDecorators = listOf(
            rememberSceneSetupNavEntryDecorator(),
            rememberSavedStateNavEntryDecorator(),
            // rememberViewModelStoreNavEntryDecorator(), // TODO
        ),
        popTransitionSpec = popTransactionSpec ?: defaultPopTransitionSpec(),
    )
}


fun EntryProviderBuilder<Any>.screens(backStack: BackStack<Any>) {
    startScreens(backStack) // TODO inline these later

    entry<MainScreen> {
        MainScreen(onNavigate = { backStack.add(it) })
    }
    entry<SpeakerDetailScreen> {
        SpeakerDetailScreen(
            speakerId = it.speakerId,
            onBack = backStack::pop,
            onSession = { backStack.add(SessionScreen(it)) },
        )
    }
    entry<SessionScreen> {
        val urlHandler = LocalUriHandler.current
        SessionScreen(
            sessionId = it.sessionId,
            openedForFeedback = it.openedForFeedback,
            onBack = backStack::pop,
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
            onBack = backStack::pop,
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
            onBack = backStack::pop,
        )
    }
    entry<SingleLicenseScreen> {
        SingleLicenseScreen(
            licenseName = it.licenseName,
            licenseContent = it.licenseText,
            onBack = backStack::pop,
        )
    }
    entry<AboutConferenceScreen> {
        val urlHandler = LocalUriHandler.current
        AboutConference(
            onPrivacyNotice = { backStack.add(VisitorPrivacyNoticeScreen) },
            onGeneralTerms = { backStack.add(TermsOfUseScreen) },
            onWebsiteLink = { urlHandler.openUri(URLs.KOTLINCONF_HOMEPAGE) },
            onBack = backStack::pop,
            onSpeaker = { speakerId -> backStack.add(SpeakerDetailScreen(speakerId)) },
        )
    }
    entry<CodeOfConductScreen> {
        CodeOfConduct(onBack = backStack::pop)
    }
    entry<SettingsScreen> {
        SettingsScreen(onBack = backStack::pop)
    }
    entry<VisitorPrivacyNoticeScreen> {
        VisitorPrivacyNotice(onBack = backStack::pop)
    }
    entry<AppPrivacyNoticeScreen> {
        AppPrivacyNotice(
            onBack = backStack::pop,
            onAppTermsOfUse = { backStack.add(AppTermsOfUseScreen) },
        )
    }
    entry<TermsOfUseScreen> {
        VisitorTermsOfUse(
            onBack = backStack::pop,
            onCodeOfConduct = { backStack.add(CodeOfConductScreen) },
            onVisitorPrivacyNotice = { backStack.add(VisitorPrivacyNoticeScreen) },
        )
    }
    entry<AppTermsOfUseScreen> {
        AppTermsOfUse(
            onBack = backStack::pop,
            onAppPrivacyNotice = {
                backStack.add(AppPrivacyNoticeScreen)
            },
        )
    }
    entry<PartnersScreen> {
        PartnersScreen(
            onBack = backStack::pop,
            onPartnerDetail = { partnerId ->
                backStack.add(PartnerDetailScreen(partnerId))
            }
        )
    }
    entry<PartnerDetailScreen> {
        PartnerDetailScreen(
            partnerId = it.partnerId,
            onBack = backStack::pop,
        )
    }

    entry<AppPrivacyNoticePrompt> {
        AppPrivacyNoticePrompt(
            onRejectNotice = backStack::pop,
            onAcceptNotice = backStack::pop,
            onAppTermsOfUse = { backStack.add(AppTermsOfUseScreen) },
            confirmationRequired = true,
        )
    }

    entry<NewsListScreen> {
        NewsListScreen(
            onNewsClick = { newsId -> backStack.add(NewsDetailScreen(newsId)) },
            onBack = backStack::pop,
        )
    }
    entry<NewsDetailScreen> {
        NewsDetailScreen(it.newsId, onBack = backStack::pop)
    }

    entry<DeveloperMenuScreen> {
        DeveloperMenuScreen(onBack = backStack::pop)
    }

    entry<NestedMapScreen> {
        NestedMapScreen(
            roomName = it.roomName,
            onBack = backStack::pop,
        )
    }
}

fun EntryProviderBuilder<Any>.startScreens(backStack: BackStack<Any>) {
    entry<StartPrivacyNoticeScreen> {
        val skipNotifications = LocalFlags.current.supportsNotifications.not()
        AppPrivacyNoticePrompt(
            onRejectNotice = {
                if (skipNotifications) {
                    backStack.add(MainScreen, clearOthers = true)
                } else {
                    backStack.add(StartNotificationsScreen)
                }
            },
            onAcceptNotice = {
                if (skipNotifications) {
                    backStack.add(MainScreen, clearOthers = true)
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
                backStack.add(MainScreen, clearOthers = true)
            }
        )
    }
}
