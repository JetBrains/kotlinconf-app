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
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import org.jetbrains.kotlinconf.screens.AboutApp
import org.jetbrains.kotlinconf.screens.AboutConference
import org.jetbrains.kotlinconf.screens.CodeOfConduct
import org.jetbrains.kotlinconf.screens.PartnerDetails
import org.jetbrains.kotlinconf.screens.Partners
import org.jetbrains.kotlinconf.screens.Schedule
import org.jetbrains.kotlinconf.screens.Session
import org.jetbrains.kotlinconf.screens.Settings
import org.jetbrains.kotlinconf.screens.TermsOfUse
import org.jetbrains.kotlinconf.screens.Speakers
import org.jetbrains.kotlinconf.screens.Speaker
import org.jetbrains.kotlinconf.ui.components.StyledText
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import kotlin.reflect.typeOf
import org.jetbrains.compose.reload.DevelopmentEntryPoint

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
                                StyledText("Start screen")
                                StyledText("About App", Modifier.clickable { navController.navigate(AboutAppScreen) })
                                StyledText(
                                    "About Conference",
                                    Modifier.clickable { navController.navigate(AboutConferenceScreen) })
                                StyledText(
                                    "Code of Conduct",
                                    Modifier.clickable { navController.navigate(CodeOfConductScreen) })
                                StyledText("Settings", Modifier.clickable { navController.navigate(SettingsScreen) })
                                StyledText(
                                    "Terms of use",
                                    Modifier.clickable { navController.navigate(TermsOfUseScreen) })
                                StyledText("Partners", Modifier.clickable { navController.navigate(PartnersScreen) })
                                StyledText("Schedule", Modifier.clickable { navController.navigate(ScheduleScreen) })
                                StyledText("Speakers", Modifier.clickable { navController.navigate(SpeakersScreen) })
                            }
                        }
                        composable<AboutAppScreen> {
                            AboutApp()
                        }
                        composable<AboutConferenceScreen> {
                            AboutConference()
                        }
                        composable<CodeOfConductScreen> {
                            CodeOfConduct()
                        }
                        composable<SettingsScreen> {
                            Settings()
                        }
                        composable<TermsOfUseScreen> {
                            TermsOfUse()
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
