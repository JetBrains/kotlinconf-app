package org.jetbrains.kotlinconf.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
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
import org.jetbrains.kotlinconf.screens.PartnerDetails
import org.jetbrains.kotlinconf.screens.Partners
import org.jetbrains.kotlinconf.screens.PrivacyPolicyForVisitors
import org.jetbrains.kotlinconf.screens.PrivacyPolicyScreen
import org.jetbrains.kotlinconf.screens.SessionScreen
import org.jetbrains.kotlinconf.screens.SettingsScreen
import org.jetbrains.kotlinconf.screens.SingleLicenseScreen
import org.jetbrains.kotlinconf.screens.SpeakerDetailScreen
import org.jetbrains.kotlinconf.screens.StartNotificationsScreen
import org.jetbrains.kotlinconf.screens.TermsOfUse
import org.jetbrains.kotlinconf.utils.getStoreUrl
import kotlin.reflect.typeOf

@Composable
internal fun KotlinConfNavHost(isOnboardingComplete: Boolean) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = if (isOnboardingComplete) MainScreen else StartScreens,
        modifier = Modifier.fillMaxSize(),
        enterTransition = enterTransition { it },
        exitTransition = exitTransition { -it },
        popEnterTransition = enterTransition { -it },
        popExitTransition = exitTransition { it },
    ) {
        startScreens(
            navController = navController,
        )

        composable<MainScreen> {
            MainScreen(navController)
        }

        composable<SpeakerDetailsScreen>(typeMap = mapOf(typeOf<SpeakerId>() to SpeakerIdNavType)) {
            SpeakerDetailScreen(
                speakerId = it.toRoute<SpeakerDetailsScreen>().speakerId,
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
                onSpeaker = { speakerId -> navController.navigate(SpeakerDetailsScreen(speakerId)) },
            )
        }
        composable<CodeOfConductScreen> {
            CodeOfConduct(onBack = navController::popBackStack)
        }
        composable<SettingsScreen> {
            SettingsScreen(
                onBack = navController::popBackStack,
                onNotificationSettingsChange = {
                    // TODO request notification permission, save settings
                    println("New settings: $it")
                }
            )
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
            Partners(
                onBack = navController::popBackStack,
                onPartnerDetail = { partnerId -> navController.navigate(PartnerDetailsScreen(partnerId)) }
            )
        }
        composable<PartnerDetailsScreen> {
            PartnerDetails(it.toRoute<PartnerDetailsScreen>().partnerId, onBack = navController::popBackStack)
        }
        composable<SessionScreen>(typeMap = mapOf(typeOf<SessionId>() to SessionIdNavType)) {
            SessionScreen(
                sessionId = it.toRoute<SessionScreen>().sessionId,
                onBack = navController::popBackStack,
                onPrivacyPolicyNeeded = { navController.navigate(PrivacyPolicyScreen) },
                onSpeaker = { speakerId -> navController.navigate(SpeakerDetailsScreen(speakerId)) }
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
                    navController.navigate(StartNotificationsScreen)
                },
                onAcceptPolicy = {
                    navController.navigate(StartNotificationsScreen)
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

private fun enterTransition(
    initialOffsetX: (fullWidth: Int) -> Int,
): AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
    slideInHorizontally(animationSpec = tween(300), initialOffsetX = initialOffsetX)
}

private fun exitTransition(
    targetOffsetX: (fullWidth: Int) -> Int,
): AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
    slideOutHorizontally(animationSpec = tween(300), targetOffsetX = targetOffsetX)
}
