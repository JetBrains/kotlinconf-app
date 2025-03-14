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
import org.jetbrains.kotlinconf.PartnerId
import org.jetbrains.kotlinconf.SessionId
import org.jetbrains.kotlinconf.SpeakerId
import org.jetbrains.kotlinconf.URLs
import org.jetbrains.kotlinconf.screens.AboutAppScreen
import org.jetbrains.kotlinconf.screens.AboutConference
import org.jetbrains.kotlinconf.screens.AppPrivacyPolicy
import org.jetbrains.kotlinconf.screens.AppTermsOfUse
import org.jetbrains.kotlinconf.screens.CodeOfConduct
import org.jetbrains.kotlinconf.screens.LicensesScreen
import org.jetbrains.kotlinconf.screens.MainScreen
import org.jetbrains.kotlinconf.screens.NewsDetailScreen
import org.jetbrains.kotlinconf.screens.NewsListScreen
import org.jetbrains.kotlinconf.screens.PartnerDetailScreen
import org.jetbrains.kotlinconf.screens.PartnersScreen
import org.jetbrains.kotlinconf.screens.PrivacyPolicyForVisitors
import org.jetbrains.kotlinconf.screens.PrivacyPolicyScreen
import org.jetbrains.kotlinconf.screens.SessionScreen
import org.jetbrains.kotlinconf.screens.SettingsScreen
import org.jetbrains.kotlinconf.screens.SingleLicenseScreen
import org.jetbrains.kotlinconf.screens.SpeakerDetailScreen
import org.jetbrains.kotlinconf.screens.StartNotificationsScreen
import org.jetbrains.kotlinconf.screens.TermsOfUse
import org.jetbrains.kotlinconf.utils.getStoreUrl
import kotlin.jvm.JvmSuppressWildcards
import kotlin.reflect.typeOf

fun navigateToSession(notificationId: String) {
    val sessionId = SessionId(notificationId.substringBefore("-"))
    sessionIds.trySend(sessionId)
}

private val sessionIds = Channel<SessionId>(capacity = 1)

@Composable
internal fun KotlinConfNavHost(
    isOnboardingComplete: Boolean,
    popEnterTransition: @JvmSuppressWildcards (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition)?,
    popExitTransition: @JvmSuppressWildcards (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition)?,
) {
    val navController = rememberNavController()

    LaunchedEffect(Unit) {
        while (true) {
            val sessionId = sessionIds.receive()
            navController.navigate(SessionScreen(sessionId))
        }
    }

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
        MainScreen(navController)
    }

    composable<SpeakerDetailScreen>(typeMap = mapOf(typeOf<SpeakerId>() to SpeakerIdNavType)) {
        SpeakerDetailScreen(
            speakerId = it.toRoute<SpeakerDetailScreen>().speakerId,
            onBack = navController::popBackStack,
            onSession = { navController.navigate(SessionScreen(it)) }
        )
    }
    composable<AboutAppScreen> {
        val uriHandler = LocalUriHandler.current
        AboutAppScreen(
            onBack = { navController.popBackStack() },
            onGitHubRepo = { uriHandler.openUri(URLs.GITHUB_REPO) },
            onRateApp = { getStoreUrl()?.let { uriHandler.openUri(it) } },
            onSettings = { navController.navigate(SettingsScreen) },
            onPrivacyPolicy = { navController.navigate(AppPrivacyPolicyScreen) },
            onTermsOfUse = { navController.navigate(AppTermsOfUseScreen) },
            onLicenses = { navController.navigate(LicensesScreen) },
        )
    }
    composable<LicensesScreen> {
        LicensesScreen(
            onLicenseClick = { licenseName, licenseText ->
                navController.navigate(SingleLicenseScreen(licenseName, licenseText))
            },
            onBack = navController::popBackStack,
        )
    }
    composable<SingleLicenseScreen> {
        val params = it.toRoute<SingleLicenseScreen>()
        SingleLicenseScreen(
            licenseName = params.licenseName,
            licenseContent = params.licenseText,
            onBack = navController::popBackStack,
        )
    }
    composable<AboutConferenceScreen> {
        val urlHandler = LocalUriHandler.current
        AboutConference(
            onPrivacyPolicy = { navController.navigate(PrivacyPolicyForVisitorsScreen) },
            onGeneralTerms = { navController.navigate(TermsOfUseScreen) },
            onWebsiteLink = { urlHandler.openUri(URLs.KOTLINCONF_HOMEPAGE) },
            onBack = navController::popBackStack,
            onSpeaker = { speakerId -> navController.navigate(SpeakerDetailScreen(speakerId)) },
        )
    }
    composable<CodeOfConductScreen> {
        CodeOfConduct(onBack = navController::popBackStack)
    }
    composable<SettingsScreen> {
        SettingsScreen(onBack = navController::popBackStack)
    }
    composable<PrivacyPolicyForVisitorsScreen> {
        PrivacyPolicyForVisitors(onBack = navController::popBackStack)
    }
    composable<AppPrivacyPolicyScreen> {
        AppPrivacyPolicy(onBack = navController::popBackStack)
    }
    composable<TermsOfUseScreen> {
        TermsOfUse(onBack = navController::popBackStack)
    }
    composable<AppTermsOfUseScreen> {
        AppTermsOfUse(onBack = navController::popBackStack)
    }
    composable<PartnersScreen> {
        PartnersScreen(
            onBack = navController::popBackStack,
            onPartnerDetail = { partnerId ->
                // TODO: get partner's details (description and location on the exhibition floor) or remove the details screen
                // navController.navigate(PartnerDetailScreen(partnerId))
            }
        )
    }
    composable<PartnerDetailScreen>(typeMap = mapOf(typeOf<PartnerId>() to PartnerIdNavType)) {
        PartnerDetailScreen(
            partnerId = it.toRoute<PartnerDetailScreen>().partnerId,
            onBack = navController::popBackStack,
        )
    }
    composable<SessionScreen>(typeMap = mapOf(typeOf<SessionId>() to SessionIdNavType)) {
        SessionScreen(
            sessionId = it.toRoute<SessionScreen>().sessionId,
            onBack = navController::popBackStack,
            onPrivacyPolicyNeeded = { navController.navigate(PrivacyPolicyScreen) },
            onSpeaker = { speakerId -> navController.navigate(SpeakerDetailScreen(speakerId)) }
        )
    }
    composable<PrivacyPolicyScreen> {
        PrivacyPolicyScreen(
            onRejectPolicy = navController::popBackStack,
            onAcceptPolicy = navController::popBackStack,
        )
    }

    composable<NewsListScreen> {
        NewsListScreen(
            onNewsClick = { newsId -> navController.navigate(NewsDetailScreen(newsId)) },
            onBack = navController::popBackStack,
        )
    }
    composable<NewsDetailScreen> {
        val newsId = it.toRoute<NewsDetailScreen>().newsId
        NewsDetailScreen(newsId, onBack = navController::popBackStack)
    }
}

fun NavGraphBuilder.startScreens(
    navController: NavHostController,
) {
    navigation<StartScreens>(
        startDestination = StartPrivacyPolicyScreen
    ) {
        composable<StartPrivacyPolicyScreen> {
            PrivacyPolicyScreen(
                onRejectPolicy = {
                    navController.navigate(StartNotificationsScreen) {
                        popUpTo<StartScreens>()
                    }
                },
                onAcceptPolicy = {
                    navController.navigate(StartNotificationsScreen) {
                        popUpTo<StartScreens>()
                    }
                },
            )
        }
        composable<StartNotificationsScreen> {
            StartNotificationsScreen(
                onDone = {
                    navController.navigate(MainScreen) {
                        popUpTo<StartScreens> { inclusive = true }
                    }
                }
            )
        }
    }
}
