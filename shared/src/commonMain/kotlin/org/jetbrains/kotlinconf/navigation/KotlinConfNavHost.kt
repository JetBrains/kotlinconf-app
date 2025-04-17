package org.jetbrains.kotlinconf.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.navigation.toRoute
import kotlinx.coroutines.channels.Channel
import org.jetbrains.kotlinconf.LocalFlags
import org.jetbrains.kotlinconf.LocalNotificationId
import org.jetbrains.kotlinconf.PartnerId
import org.jetbrains.kotlinconf.SessionId
import org.jetbrains.kotlinconf.SpeakerId
import org.jetbrains.kotlinconf.URLs
import org.jetbrains.kotlinconf.screens.AboutAppScreen
import org.jetbrains.kotlinconf.screens.AboutConference
import org.jetbrains.kotlinconf.screens.AppPrivacyNotice
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
import org.jetbrains.kotlinconf.screens.VisitorPrivacyNotice
import org.jetbrains.kotlinconf.screens.AppPrivacyNoticePrompt
import org.jetbrains.kotlinconf.screens.SessionScreen
import org.jetbrains.kotlinconf.screens.SettingsScreen
import org.jetbrains.kotlinconf.screens.SingleLicenseScreen
import org.jetbrains.kotlinconf.screens.SpeakerDetailScreen
import org.jetbrains.kotlinconf.screens.StartNotificationsScreen
import org.jetbrains.kotlinconf.screens.VisitorTermsOfUse
import org.jetbrains.kotlinconf.utils.getStoreUrl
import kotlin.jvm.JvmSuppressWildcards
import kotlin.reflect.typeOf

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
    val navController = rememberNavController()

    NotificationHandler(navController)
    PlatformNavHandler(navController)

    val startDestination = if (isOnboardingComplete) MainScreen else StartScreens
    if (popEnterTransition != null && popExitTransition != null) {
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.fillMaxSize(),
            popEnterTransition = popEnterTransition,
            popExitTransition = popExitTransition,
        ) {
            screens(navController)
        }
    } else {
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.fillMaxSize(),
        ) {
            screens(navController)
        }
    }
}


fun NavGraphBuilder.screens(navController: NavHostController) {
    startScreens(
        navController = navController,
    )

    composable<MainScreen> {
        MainScreen(
            rootNavController = navController,
        )
    }

    composable<SpeakerDetailScreen>(typeMap = mapOf(typeOf<SpeakerId>() to SpeakerIdNavType)) {
        SpeakerDetailScreen(
            speakerId = it.toRoute<SpeakerDetailScreen>().speakerId,
            onBack = navController::navigateUp,
            onSession = { navController.navigate(SessionScreen(it)) },
        )
    }
    composable<AboutAppScreen> {
        val uriHandler = LocalUriHandler.current
        AboutAppScreen(
            onBack = navController::navigateUp,
            onGitHubRepo = { uriHandler.openUri(URLs.GITHUB_REPO) },
            onRateApp = { getStoreUrl()?.let { uriHandler.openUri(it) } },
            onSettings = { navController.navigate(SettingsScreen) },
            onPrivacyNotice = { navController.navigate(AppPrivacyNoticeScreen) },
            onTermsOfUse = { navController.navigate(AppTermsOfUseScreen) },
            onLicenses = { navController.navigate(LicensesScreen) },
            onJunie = { uriHandler.openUri(URLs.JUNIE_LANDING_PAGE) },
            onDeveloperMenu = { navController.navigate(DeveloperMenuScreen) },
        )
    }
    composable<LicensesScreen> {
        LicensesScreen(
            onLicenseClick = { licenseName, licenseText ->
                navController.navigate(SingleLicenseScreen(licenseName, licenseText))
            },
            onBack = navController::navigateUp,
        )
    }
    composable<SingleLicenseScreen> {
        val params = it.toRoute<SingleLicenseScreen>()
        SingleLicenseScreen(
            licenseName = params.licenseName,
            licenseContent = params.licenseText,
            onBack = navController::navigateUp,
        )
    }
    composable<AboutConferenceScreen> {
        val urlHandler = LocalUriHandler.current
        AboutConference(
            onPrivacyNotice = { navController.navigate(VisitorPrivacyNoticeScreen) },
            onGeneralTerms = { navController.navigate(TermsOfUseScreen) },
            onWebsiteLink = { urlHandler.openUri(URLs.KOTLINCONF_HOMEPAGE) },
            onBack = navController::navigateUp,
            onSpeaker = { speakerId -> navController.navigate(SpeakerDetailScreen(speakerId)) },
        )
    }
    composable<CodeOfConductScreen> {
        CodeOfConduct(onBack = navController::navigateUp)
    }
    composable<SettingsScreen> {
        SettingsScreen(onBack = navController::navigateUp)
    }
    composable<VisitorPrivacyNoticeScreen> {
        VisitorPrivacyNotice(onBack = navController::navigateUp)
    }
    composable<AppPrivacyNoticeScreen> {
        AppPrivacyNotice(
            onBack = navController::navigateUp,
            onAppTermsOfUse = { navController.navigate(AppTermsOfUseScreen) },
        )
    }
    composable<TermsOfUseScreen> {
        VisitorTermsOfUse(
            onBack = navController::navigateUp,
            onCodeOfConduct = { navController.navigate(CodeOfConductScreen) },
            onVisitorPrivacyNotice = { navController.navigate(VisitorPrivacyNoticeScreen) },
        )
    }
    composable<AppTermsOfUseScreen> {
        AppTermsOfUse(
            onBack = navController::navigateUp,
            onAppPrivacyNotice = {
                navController.navigate(AppPrivacyNoticeScreen)
            },
        )
    }
    composable<PartnersScreen> {
        PartnersScreen(
            onBack = navController::navigateUp,
            onPartnerDetail = { partnerId ->
                navController.navigate(PartnerDetailScreen(partnerId))
            }
        )
    }
    composable<PartnerDetailScreen>(typeMap = mapOf(typeOf<PartnerId>() to PartnerIdNavType)) {
        PartnerDetailScreen(
            partnerId = it.toRoute<PartnerDetailScreen>().partnerId,
            onBack = navController::navigateUp,
        )
    }
    composable<SessionScreen>(typeMap = mapOf(typeOf<SessionId>() to SessionIdNavType)) {
        val params = it.toRoute<SessionScreen>()
        val urlHandler = LocalUriHandler.current
        SessionScreen(
            sessionId = params.sessionId,
            openedForFeedback = params.openedForFeedback,
            onBack = navController::navigateUp,
            onPrivacyNoticeNeeded = { navController.navigate(AppPrivacyNoticePrompt) },
            onSpeaker = { speakerId -> navController.navigate(SpeakerDetailScreen(speakerId)) },
            onWatchVideo = { videoUrl -> urlHandler.openUri(videoUrl) },
            onNavigateToMap = { roomName ->
                navController.navigate(NestedMapScreen(roomName))
            },
        )
    }
    composable<AppPrivacyNoticePrompt> {
        AppPrivacyNoticePrompt(
            onRejectNotice = navController::navigateUp,
            onAcceptNotice = navController::navigateUp,
            onAppTermsOfUse =  { navController.navigate(AppTermsOfUseScreen) },
            confirmationRequired = true,
        )
    }

    composable<NewsListScreen> {
        NewsListScreen(
            onNewsClick = { newsId -> navController.navigate(NewsDetailScreen(newsId)) },
            onBack = navController::navigateUp,
        )
    }
    composable<NewsDetailScreen> {
        val newsId = it.toRoute<NewsDetailScreen>().newsId
        NewsDetailScreen(newsId, onBack = navController::navigateUp)
    }

    composable<DeveloperMenuScreen> {
        DeveloperMenuScreen(onBack = navController::navigateUp)
    }

    composable<NestedMapScreen> {
        val nestedMapParam = it.toRoute<NestedMapScreen>()
        NestedMapScreen(
            roomName = nestedMapParam.roomName,
            onBack = navController::navigateUp,
        )
    }
}

fun NavGraphBuilder.startScreens(
    navController: NavHostController,
) {
    navigation<StartScreens>(
        startDestination = StartPrivacyNoticeScreen
    ) {
        composable<StartPrivacyNoticeScreen> {
            val skipNotifications = LocalFlags.current.supportsNotifications.not()
            AppPrivacyNoticePrompt(
                onRejectNotice = {
                    navController.navigate(if (skipNotifications) MainScreen else StartNotificationsScreen) {
                        popUpTo<StartScreens> { inclusive = skipNotifications }
                    }
                },
                onAcceptNotice = {
                    navController.navigate(if (skipNotifications) MainScreen else StartNotificationsScreen) {
                        popUpTo<StartScreens> { inclusive = skipNotifications }
                    }
                },
                onAppTermsOfUse =  { navController.navigate(AppTermsOfUseScreen) },
                confirmationRequired = false,
            )
        }
        composable<StartNotificationsScreen> {
            StartNotificationsScreen(
                onDone = {
                    navController.navigate(MainScreen) {
                        popUpTo<StartScreens> { inclusive = true }
                    }
                })
        }
    }
}
