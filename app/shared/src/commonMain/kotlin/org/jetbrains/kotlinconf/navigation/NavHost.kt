package org.jetbrains.kotlinconf.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import org.jetbrains.kotlinconf.ConferenceService
import org.jetbrains.kotlinconf.LocalAppGraph
import org.jetbrains.kotlinconf.LocalMapHandler
import org.jetbrains.kotlinconf.LocalNotificationId
import org.jetbrains.kotlinconf.SessionId
import org.jetbrains.kotlinconf.ThemeChangeAnimation
import org.jetbrains.kotlinconf.URLs
import org.jetbrains.kotlinconf.flags.LocalFlags
import org.jetbrains.kotlinconf.screens.AboutAppScreen
import org.jetbrains.kotlinconf.screens.AboutConference
import org.jetbrains.kotlinconf.screens.AppPrivacyNotice
import org.jetbrains.kotlinconf.screens.AppPrivacyNoticePrompt
import org.jetbrains.kotlinconf.screens.AppTermsOfUse
import org.jetbrains.kotlinconf.screens.CodeOfConduct
import org.jetbrains.kotlinconf.screens.GoldenKodeeFinalistScreen
import org.jetbrains.kotlinconf.screens.GoldenKodeeScreen
import org.jetbrains.kotlinconf.screens.InfoScreen
import org.jetbrains.kotlinconf.screens.MapScreen
import org.jetbrains.kotlinconf.screens.NestedMapScreen
import org.jetbrains.kotlinconf.screens.PartnerDetailScreen
import org.jetbrains.kotlinconf.screens.PartnersScreen
import org.jetbrains.kotlinconf.screens.ScheduleScreen
import org.jetbrains.kotlinconf.screens.SessionScreen
import org.jetbrains.kotlinconf.screens.SettingsScreen
import org.jetbrains.kotlinconf.screens.SpeakerDetailScreen
import org.jetbrains.kotlinconf.screens.SpeakersScreen
import org.jetbrains.kotlinconf.screens.StartNotificationsScreen
import org.jetbrains.kotlinconf.screens.VisitorPrivacyNotice
import org.jetbrains.kotlinconf.screens.VisitorTermsOfUse
import org.jetbrains.kotlinconf.screens.licenses.LicensesScreen
import org.jetbrains.kotlinconf.screens.licenses.SingleLicenseScreen
import org.jetbrains.kotlinconf.ui.components.Text
import org.jetbrains.kotlinconf.ui.theme.GoldenKodeeColors
import org.jetbrains.kotlinconf.ui.theme.KotlinConfDarkColors
import org.jetbrains.kotlinconf.ui.theme.KotlinConfLightColors
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.utils.DateTimeFormatting
import org.jetbrains.kotlinconf.utils.getStoreUrl
import org.jetbrains.kotlinconf.utils.topInsetPadding
import org.jetbrains.kotlinconf.screens.DeveloperMenuScreen as DeveloperMenuScreenContent

fun navigateByLocalNotificationId(notificationId: String) {
    LocalNotificationId.parse(notificationId)?.let {
        when (it.type) {
            LocalNotificationId.Type.SessionStart, LocalNotificationId.Type.SessionEnd ->
                navigateToSession(SessionId(it.id))
        }
    }
}

fun navigateToSession(sessionId: SessionId) {
    notificationNavRequests.trySend(NavRequest(ScheduleScreen, SessionScreen(sessionId)))
}

data class NavRequest(val topLevelRoute: TopLevelRoute, val targetRoute: AppRoute)

private val notificationNavRequests = Channel<NavRequest>(capacity = 1)

@Composable
private fun NotificationHandler(navigator: Navigator) {
    LaunchedEffect(Unit) {
        while (true) {
            val request: NavRequest = notificationNavRequests.receive()
            navigator.activate(request.topLevelRoute, withReselection = false)
            navigator.add(request.targetRoute)
        }
    }
}

@Composable
internal fun NavHost(
    isOnboardingComplete: Boolean,
    isDarkTheme: Boolean,
    onThemeChange: ((Boolean) -> Unit)?,
) {
    val startRoute = remember {
        if (isOnboardingComplete) ScheduleScreen else StartPrivacyNoticeScreen
    }

    val navState = rememberNavState(
        startRoute = startRoute,
        topLevelRoutes = setOf(
            ScheduleScreen,
            SpeakersScreen,
            GoldenKodeeScreen,
            MapScreen,
            InfoScreen,
        ),
        primaryTopLevelRoute = ScheduleScreen,
    )

    val topLevelBackEnabled = LocalFlags.current.enableBackOnTopLevelScreens
    val navigator = remember(navState, topLevelBackEnabled) {
        Navigator(navState, topLevelBackEnabled)
    }

    BrowserIntegration(navState)

    NotificationHandler(navigator)

    val entryProvider = entryProvider {
        screens(
            navigator = navigator,
            onBack = { navigator.goBack() },
        )
    }

    val conferenceService = LocalAppGraph.current.conferenceService
    val showGoldenKodee by remember { conferenceService.goldenKodeeData.map { it != null } }
        .collectAsStateWithLifecycle(false)

    val isGoldenKodee = navState.topLevelRoute is GoldenKodeeScreen

    ThemeChangeAnimation(
        isDarkTheme = isDarkTheme,
        enabled = navState.currentBackstack.lastOrNull() is SettingsScreen,
    ) { appliedIsDarkTheme ->
        val colors = when {
            isGoldenKodee -> GoldenKodeeColors
            appliedIsDarkTheme -> KotlinConfDarkColors
            else -> KotlinConfLightColors
        }

        if (onThemeChange != null) {
            LaunchedEffect(colors) { onThemeChange(colors.isDark) }
        }

        KotlinConfTheme(
            rippleEnabled = LocalFlags.current.rippleEnabled,
            colors = colors,
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(KotlinConfTheme.colors.mainBackground)
            ) {
                NavScaffold(
                    navState = navState,
                    navigator = navigator,
                    showGoldenKodee = showGoldenKodee,
                ) {
                    NavDisplay(
                        entries = navState.toDecoratedEntries(entryProvider),
                        onBack = navigator::goBack,
                    )
                }

                val baseUrl = LocalAppGraph.current.baseUrl
                val flagsManager = LocalAppGraph.current.flagsManager
                val currentFlags = LocalFlags.current
                val platformFlags = flagsManager.platformFlags
                if (baseUrl != URLs.PRODUCTION_URL || currentFlags != platformFlags) {
                    DebugMarker(
                        Modifier
                            .align(Alignment.TopCenter)
                            .padding(topInsetPadding())
                            .clip(KotlinConfTheme.shapes.roundedCornerMd)
                            .clickable { navigator.add(AboutAppScreen) }
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DebugMarker(modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier,
    ) {
        Box(
            Modifier.size(10.dp)
                .clip(CircleShape)
                .background(KotlinConfTheme.colors.orangeText)
        )
        Text(
            text = "Testing",
            color = KotlinConfTheme.colors.orangeText,
            style = KotlinConfTheme.typography.text2,
            maxLines = 1,
        )
        if (LocalFlags.current.useFakeTime) {
            val dateTime by LocalAppGraph.current.timeProvider.time.collectAsStateWithLifecycle()
            Text(
                text = "Fake time: ${DateTimeFormatting.dateAndTime(dateTime)}",
                color = KotlinConfTheme.colors.orangeText,
                style = KotlinConfTheme.typography.text2,
                maxLines = 1,
            )
        }
    }
}

private val noAnimationTransition = EnterTransition.None togetherWith ExitTransition.None

private val noAnimationMetadata =
    NavDisplay.transitionSpec { noAnimationTransition } +
            NavDisplay.popTransitionSpec { noAnimationTransition } +
            NavDisplay.predictivePopTransitionSpec { noAnimationTransition }

private fun EntryProviderScope<AppRoute>.screens(
    navigator: Navigator,
    onBack: () -> Unit,
) {
    entry<StartPrivacyNoticeScreen> {
        val skipNotifications = LocalFlags.current.supportsNotifications.not()
        AppPrivacyNoticePrompt(
            onRejectNotice = {
                if (skipNotifications) {
                    navigator.set(ScheduleScreen)
                } else {
                    navigator.add(StartNotificationsScreen)
                }
            },
            onAcceptNotice = {
                if (skipNotifications) {
                    navigator.set(ScheduleScreen)
                } else {
                    navigator.add(StartNotificationsScreen)
                }
            },
            onAppTermsOfUse = { navigator.add(AppTermsOfUseScreen) },
            confirmationRequired = false,
        )
    }
    entry<StartNotificationsScreen> {
        StartNotificationsScreen(
            onDone = {
                navigator.set(ScheduleScreen)
            }
        )
    }

    entry<ScheduleScreen>(metadata = noAnimationMetadata) {
        val service: ConferenceService = LocalAppGraph.current.conferenceService
        LaunchedEffect(Unit) {
            service.completeOnboarding()
        }
        ScheduleScreen(
            onSession = { navigator.add(SessionScreen(it)) },
            onPrivacyNoticeNeeded = { navigator.add(AppPrivacyNoticePrompt) },
            tabReselections = navigator.tabReselections(forRoute = ScheduleScreen),
        )
    }

    entry<SpeakersScreen>(metadata = noAnimationMetadata) {
        SpeakersScreen(
            onSpeaker = { navigator.add(SpeakerDetailScreen(it)) }
        )
    }

    entry<GoldenKodeeScreen>(metadata = noAnimationMetadata) {
        GoldenKodeeScreen(
            onNomineeClick = { categoryId, nomineeId ->
                navigator.add(GoldenKodeeFinalistScreen(categoryId, nomineeId))
            },
        )
    }

    entry<GoldenKodeeFinalistScreen> {
        GoldenKodeeFinalistScreen(
            categoryId = it.categoryId,
            nomineeId = it.nomineeId,
            onBack = onBack,
        )
    }

    entry<MapScreen>(metadata = noAnimationMetadata) {
        val mapHandler = LocalMapHandler.current
        MapScreen(
            onHowToFindVenue = { address -> mapHandler.openNavigation(address) },
        )
    }

    entry<InfoScreen>(metadata = noAnimationMetadata) {
        val uriHandler = LocalUriHandler.current
        val mapHandler = LocalMapHandler.current
        InfoScreen(
            onAboutConf = { navigator.add(AboutConferenceScreen) },
            onHowToFindVenue = { address -> mapHandler.openNavigation(address) },
            onAboutApp = { navigator.add(AboutAppScreen) },
            onOurPartners = { navigator.add(PartnersScreen) },
            onCodeOfConduct = { navigator.add(CodeOfConductScreen) },
            onTwitter = { uriHandler.openUri(URLs.TWITTER) },
            onSlack = { uriHandler.openUri(URLs.SLACK) },
            onBluesky = { uriHandler.openUri(URLs.BLUESKY) },
            onSettings = { navigator.add(SettingsScreen) },
        )
    }

    entry<SpeakerDetailScreen> {
        SpeakerDetailScreen(
            speakerId = it.speakerId,
            onBack = onBack,
            onSession = { sessionId -> navigator.add(SessionScreen(sessionId)) },
        )
    }
    entry<SessionScreen> {
        val urlHandler = LocalUriHandler.current
        SessionScreen(
            sessionId = it.sessionId,
            onBack = onBack,
            onPrivacyNoticeNeeded = { navigator.add(AppPrivacyNoticePrompt) },
            onSpeaker = { speakerId -> navigator.add(SpeakerDetailScreen(speakerId)) },
            onWatchVideo = { videoUrl -> urlHandler.openUri(videoUrl) },
            onNavigateToMap = { roomName ->
                navigator.add(NestedMapScreen(roomName))
            },
        )
    }

    entry<AboutAppScreen> {
        val uriHandler = LocalUriHandler.current
        AboutAppScreen(
            onBack = onBack,
            onGitHubRepo = { uriHandler.openUri(URLs.GITHUB_REPO) },
            onRateApp = { getStoreUrl()?.let { uriHandler.openUri(it) } },
            onPrivacyNotice = { navigator.add(AppPrivacyNoticeScreen) },
            onTermsOfUse = { navigator.add(AppTermsOfUseScreen) },
            onLicenses = { navigator.add(LicensesScreen) },
            onJunie = { uriHandler.openUri(URLs.JUNIE_LANDING_PAGE) },
            onDeveloperMenu = { skipDelay -> navigator.add(DeveloperMenuScreen(skipWarningDelay = skipDelay)) },
        )
    }
    entry<LicensesScreen> {
        LicensesScreen(
            onLicenseClick = { licenseName, licenseText ->
                navigator.add(SingleLicenseScreen(licenseName, licenseText))
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
            onPrivacyNotice = { navigator.add(VisitorPrivacyNoticeScreen) },
            onGeneralTerms = { navigator.add(TermsOfUseScreen) },
            onWebsiteLink = { urlHandler.openUri(URLs.KOTLINCONF_HOMEPAGE) },
            onBack = onBack,
            onSpeaker = { speakerId -> navigator.add(SpeakerDetailScreen(speakerId)) },
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
            onAppTermsOfUse = { navigator.add(AppTermsOfUseScreen) },
        )
    }
    entry<TermsOfUseScreen> {
        VisitorTermsOfUse(
            onBack = onBack,
            onCodeOfConduct = { navigator.add(CodeOfConductScreen) },
            onVisitorPrivacyNotice = { navigator.add(VisitorPrivacyNoticeScreen) },
        )
    }
    entry<AppTermsOfUseScreen> {
        AppTermsOfUse(
            onBack = onBack,
            onAppPrivacyNotice = {
                navigator.add(AppPrivacyNoticeScreen)
            },
        )
    }
    entry<PartnersScreen> {
        PartnersScreen(
            onBack = onBack,
            onPartnerDetail = { partnerId ->
                navigator.add(PartnerDetailScreen(partnerId))
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
            onAppTermsOfUse = { navigator.add(AppTermsOfUseScreen) },
            confirmationRequired = true,
        )
    }

    entry<DeveloperMenuScreen> {
        DeveloperMenuScreenContent(
            onBack = onBack,
            skipWarningDelay = it.skipWarningDelay,
        )
    }

    entry<NestedMapScreen> {
        NestedMapScreen(
            roomName = it.roomName,
            onBack = onBack,
        )
    }
}
