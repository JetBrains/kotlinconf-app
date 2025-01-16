package org.jetbrains.kotlinconf

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import org.intellij.markdown.html.urlEncode
import org.jetbrains.compose.reload.DevelopmentEntryPoint
import org.jetbrains.kotlinconf.screens.AboutAppScreen
import org.jetbrains.kotlinconf.screens.AboutConference
import org.jetbrains.kotlinconf.screens.CodeOfConduct
import org.jetbrains.kotlinconf.screens.InfoScreen
import org.jetbrains.kotlinconf.screens.PartnerDetails
import org.jetbrains.kotlinconf.screens.Partners
import org.jetbrains.kotlinconf.screens.Schedule
import org.jetbrains.kotlinconf.screens.Session
import org.jetbrains.kotlinconf.screens.Settings
import org.jetbrains.kotlinconf.screens.Speaker
import org.jetbrains.kotlinconf.screens.Speakers
import org.jetbrains.kotlinconf.screens.StartNotificationsScreen
import org.jetbrains.kotlinconf.screens.StartPrivacyPolicyScreen
import org.jetbrains.kotlinconf.screens.TermsOfUse
import org.jetbrains.kotlinconf.ui.components.StyledText
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.utils.getStoreUrl
import kotlin.reflect.typeOf

const val apiEndpoint = "https://kotlinconf-app-prod.labs.jb.gg"

@Composable
fun App(context: ApplicationContext) {
    val navController = rememberNavController()
    DevelopmentEntryPoint {
        KotlinConfTheme {
            val service = remember {
                ConferenceService(context, apiEndpoint)
            }
            CompositionLocalProvider(LocalNavController provides navController) {
                Box(
                    Modifier.fillMaxSize()
                        .background(KotlinConfTheme.colors.mainBackground),
                    contentAlignment = Alignment.Center
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = StartScreen,
                        modifier = Modifier.fillMaxSize(),
                        enterTransition = enterTransition { it },
                        exitTransition = exitTransition { -it },
                        popEnterTransition = enterTransition { -it },
                        popExitTransition = exitTransition { it },
                    ) {
                        composable<StartScreen> {
                            Column {
                                StyledText(
                                    "Start Screens",
                                    Modifier.clickable { navController.navigate(StartPrivacyPolicyScreen) }
                                )
                                StyledText("About App", Modifier.clickable { navController.navigate(AboutAppScreen) })
                                StyledText("Info", Modifier.clickable { navController.navigate(InfoScreen) })
                                StyledText(
                                    "About Conference",
                                    Modifier.clickable { navController.navigate(AboutConferenceScreen) }
                                )
                                StyledText(
                                    "Code of Conduct",
                                    Modifier.clickable { navController.navigate(CodeOfConductScreen) }
                                )
                                StyledText("Settings", Modifier.clickable { navController.navigate(SettingsScreen) })
                                StyledText(
                                    "Privacy policy for visitors",
                                    Modifier.clickable { navController.navigate(PrivacyPolicyForVisitorsScreen) }
                                )
                                StyledText(
                                    "Terms of use",
                                    Modifier.clickable { navController.navigate(TermsOfUseScreen) }
                                )
                                StyledText("Partners", Modifier.clickable { navController.navigate(PartnersScreen) })
                                StyledText("Schedule", Modifier.clickable { navController.navigate(ScheduleScreen) })
                                StyledText("Speakers", Modifier.clickable { navController.navigate(SpeakersScreen) })
                            }
                        }
                        composable<AboutAppScreen> {
                            // TODO add action handlers
                            val uriHandler = LocalUriHandler.current
                            AboutAppScreen(
                                onBack = { navController.popBackStack() },
                                onGitHubRepo = { uriHandler.openUri("https://github.com/JetBrains/kotlinconf-app") },
                                onRateApp = { getStoreUrl()?.let { uriHandler.openUri(it) } },
                                onSettings = {},
                                onPrivacyPolicy = {},
                                onTermsOfUse = {},
                            )
                        }
                        composable<InfoScreen> {
                            // TODO add action handlers
                            val uriHandler = LocalUriHandler.current
                            InfoScreen(
                                onAboutConf = {},
                                onAboutApp = {},
                                onOurPartners = {},
                                onCodeOfConduct = {},
                                onTwitter = { uriHandler.openUri("https://x.com/kotlinconf") },
                                onSlack = { uriHandler.openUri("https://kotlinlang.slack.com/messages/kotlinconf/") },
                            )
                        }
                        composable<StartPrivacyPolicyScreen> {
                            StartPrivacyPolicyScreen(
                                onRejectPolicy = { navController.popBackStack() },
                                onAcceptPolicy = { navController.navigate(StartNotificationsScreen) },
                            )
                        }
                        composable<StartNotificationsScreen> {
                            StartNotificationsScreen(
                                onDone = { notificationSettings ->
                                    // TODO request notification permission, save settings
                                    navController.popBackStack(StartScreen, inclusive = false)
                                }
                            )
                        }
                        composable<AboutConferenceScreen> {
                            AboutConference()
                        }
                        composable<CodeOfConductScreen> {
                            CodeOfConduct(onBack = navController::popBackStack)
                        }
                        composable<SettingsScreen> {
                            Settings()
                        }
                        composable<PrivacyPolicyForVisitorsScreen> {
                            PrivacyPolicyForVisitors(onBack = navController::popBackStack)
                        }
                        composable<TermsOfUseScreen> {
                            TermsOfUse(onBack = navController::popBackStack)
                        }
                        composable<PartnersScreen> {
                            Partners()
                        }
                        composable<PartnerDetailsScreen> {
                            PartnerDetails(it.toRoute<PartnerDetailsScreen>().partnerId)
                        }
                        composable<SpeakersScreen> {
                            Speakers(service.speakers.collectAsState().value)
                        }
                        composable<SpeakerDetailsScreen>(typeMap = mapOf(typeOf<SpeakerId>() to SpeakerIdNavType)) {
                            val speakerId = it.toRoute<SpeakerDetailsScreen>().speakerId
                            Speaker(service.speakerById(speakerId))
                        }
                        composable<ScheduleScreen> {
                            Schedule(service.agenda.collectAsState().value)
                        }
                        composable<TalkDetailsScreen>(typeMap = mapOf(typeOf<SessionId>() to SessionIdNavType)) {
                            val sessionId = it.toRoute<TalkDetailsScreen>().talkId
                            Session(service.sessionById(sessionId))
                        }
                    }
                }
            }
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
