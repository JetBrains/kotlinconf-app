package org.jetbrains.kotlinconf.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.platform.LocalUriHandler
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import kotlinx.coroutines.channels.Channel
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

private val notificationNavRequests = Channel<Any>(capacity = 1)

@Composable
private fun NotificationHandler(navController: NavHostController) {
    LaunchedEffect(Unit) {
        while (true) {
            val destination = notificationNavRequests.receive()
            navController.navigate(destination)
        }
    }
}

@Composable
internal fun KotlinConfNavHost(
    isOnboardingComplete: Boolean,
    popEnterTransition: @JvmSuppressWildcards (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition)?,
    popExitTransition: @JvmSuppressWildcards (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition)?,
) {
//    val navController = rememberNavController()

//    NotificationHandler(navController)
//    PlatformNavHandler(navController)

    val backStack = remember { mutableStateListOf<Any>(MainScreen) }
    NavDisplay(
        backStack = backStack,
        entryProvider = entryProvider {
            screens(backStack)
        }
    )

//    val startDestination = if (isOnboardingComplete) MainScreen else StartScreens
//    if (popEnterTransition != null && popExitTransition != null) {
//        NavHost(
//            navController = navController,
//            startDestination = startDestination,
//            modifier = Modifier.fillMaxSize(),
//            popEnterTransition = popEnterTransition,
//            popExitTransition = popExitTransition,
//        ) {
//            screens(navController)
//        }
//    } else {
//        NavHost(
//            navController = navController,
//            startDestination = startDestination,
//            modifier = Modifier.fillMaxSize(),
//        ) {
//            screens(navController)
//        }
//    }
}


fun EntryProviderBuilder<Any>.screens(backStack: SnapshotStateList<Any>) {
    fun popBackStack() {
        backStack.removeAt(backStack.lastIndex)
    }

//    startScreens(
//        navController = navController,
//    )

    entry<MainScreen> {
        MainScreen(onNavigate = { backStack.add(it) })
    }
    entry<SpeakerDetailScreen> {
        SpeakerDetailScreen(
            speakerId = it.speakerId,
            onBack = { popBackStack() },
            onSession = { backStack.add(SessionScreen(it)) },
        )
    }
    entry<SessionScreen> {
        val urlHandler = LocalUriHandler.current
        SessionScreen(
            sessionId = it.sessionId,
            openedForFeedback = it.openedForFeedback,
            onBack = { popBackStack() },
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
            onBack = { popBackStack() },
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
            onBack = { popBackStack() },
        )
    }
    entry<SingleLicenseScreen> {
        SingleLicenseScreen(
            licenseName = it.licenseName,
            licenseContent = it.licenseText,
            onBack = { popBackStack() },
        )
    }
    entry<AboutConferenceScreen> {
        val urlHandler = LocalUriHandler.current
        AboutConference(
            onPrivacyNotice = { backStack.add(VisitorPrivacyNoticeScreen) },
            onGeneralTerms = { backStack.add(TermsOfUseScreen) },
            onWebsiteLink = { urlHandler.openUri(URLs.KOTLINCONF_HOMEPAGE) },
            onBack = { popBackStack() },
            onSpeaker = { speakerId -> backStack.add(SpeakerDetailScreen(speakerId)) },
        )
    }
    entry<CodeOfConductScreen> {
        CodeOfConduct(onBack = { popBackStack() })
    }
    entry<SettingsScreen> {
        SettingsScreen(onBack = { popBackStack() })
    }
    entry<VisitorPrivacyNoticeScreen> {
        VisitorPrivacyNotice(onBack = { popBackStack() })
    }
    entry<AppPrivacyNoticeScreen> {
        AppPrivacyNotice(
            onBack = { popBackStack() },
            onAppTermsOfUse = { backStack.add(AppTermsOfUseScreen) },
        )
    }
    entry<TermsOfUseScreen> {
        VisitorTermsOfUse(
            onBack = { popBackStack() },
            onCodeOfConduct = { backStack.add(CodeOfConductScreen) },
            onVisitorPrivacyNotice = { backStack.add(VisitorPrivacyNoticeScreen) },
        )
    }
    entry<AppTermsOfUseScreen> {
        AppTermsOfUse(
            onBack = { popBackStack() },
            onAppPrivacyNotice = {
                backStack.add(AppPrivacyNoticeScreen)
            },
        )
    }
    entry<PartnersScreen> {
        PartnersScreen(
            onBack = { popBackStack() },
            onPartnerDetail = { partnerId ->
                backStack.add(PartnerDetailScreen(partnerId))
            }
        )
    }
    entry<PartnerDetailScreen> {
        PartnerDetailScreen(
            partnerId = it.partnerId,
            onBack = { popBackStack() },
        )
    }

    entry<AppPrivacyNoticePrompt> {
        AppPrivacyNoticePrompt(
            onRejectNotice = { popBackStack() },
            onAcceptNotice = { popBackStack() },
            onAppTermsOfUse = { backStack.add(AppTermsOfUseScreen) },
            confirmationRequired = true,
        )
    }

    entry<DeveloperMenuScreen> {
        DeveloperMenuScreen(onBack = { popBackStack() })
    }

    entry<NestedMapScreen> {
        NestedMapScreen(
            roomName = it.roomName,
            onBack = { popBackStack() },
        )
    }
}

//fun NavGraphBuilder.startScreens(
//    navController: NavHostController,
//) {
//    navigation<StartScreens>(
//        startDestination = StartPrivacyNoticeScreen
//    ) {
//        composable<StartPrivacyNoticeScreen> {
//            val skipNotifications = LocalFlags.current.supportsNotifications.not()
//            AppPrivacyNoticePrompt(
//                onRejectNotice = {
//                    navController.navigate(if (skipNotifications) MainScreen else StartNotificationsScreen) {
//                        popUpTo<StartScreens> { inclusive = skipNotifications }
//                    }
//                },
//                onAcceptNotice = {
//                    navController.navigate(if (skipNotifications) MainScreen else StartNotificationsScreen) {
//                        popUpTo<StartScreens> { inclusive = skipNotifications }
//                    }
//                },
//                onAppTermsOfUse =  { navController.navigate(AppTermsOfUseScreen) },
//                confirmationRequired = false,
//            )
//        }
//        composable<StartNotificationsScreen> {
//            StartNotificationsScreen(
//                onDone = {
//                    navController.navigate(MainScreen) {
//                        popUpTo<StartScreens> { inclusive = true }
//                    }
//                })
//        }
//    }
//}
