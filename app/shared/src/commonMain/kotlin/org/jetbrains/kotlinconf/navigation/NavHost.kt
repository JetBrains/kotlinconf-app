package org.jetbrains.kotlinconf.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.coroutines.channels.Channel
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.ConferenceService
import org.jetbrains.kotlinconf.LocalAppGraph
import org.jetbrains.kotlinconf.LocalMapHandler
import org.jetbrains.kotlinconf.LocalNotificationId
import org.jetbrains.kotlinconf.SessionId
import org.jetbrains.kotlinconf.ThemeChangeAnimation
import org.jetbrains.kotlinconf.URLs
import org.jetbrains.kotlinconf.flags.LocalFlags
import org.jetbrains.kotlinconf.generated.resources.Res
import org.jetbrains.kotlinconf.generated.resources.about_app_title
import org.jetbrains.kotlinconf.generated.resources.about_conference_title
import org.jetbrains.kotlinconf.generated.resources.app_privacy_notice_title
import org.jetbrains.kotlinconf.generated.resources.app_terms
import org.jetbrains.kotlinconf.generated.resources.code_of_conduct
import org.jetbrains.kotlinconf.generated.resources.general_terms
import org.jetbrains.kotlinconf.generated.resources.licenses_title
import org.jetbrains.kotlinconf.generated.resources.partners_title
import org.jetbrains.kotlinconf.generated.resources.privacy_notice_for_visitors
import org.jetbrains.kotlinconf.generated.resources.settings_title
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
import org.jetbrains.kotlinconf.screens.SpeakersViewModel
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
    notificationNavRequests.trySend(NavRequest(ScheduleScreen, SessionScreen(sessionId, null)))
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

val LocalUseNativeNavigation = staticCompositionLocalOf { false }

@Composable
internal fun NavHost(
    startRoute: AppRoute,
    isDarkTheme: Boolean,
    onThemeChange: ((Boolean) -> Unit)?,
    onNavigate: ((AppRoute) -> Unit)? = null,
    onActivate: ((TopLevelRoute) -> Unit)? = null,
) {
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

    if (onNavigate != null) {
        LaunchedEffect(navState) {
            snapshotFlow { navState.currentBackstack.toList() }.collect { backstack: List<AppRoute> ->
                val detailRoutes = backstack.drop(1)
                if (detailRoutes.isNotEmpty()) {
                    detailRoutes.forEach { onNavigate(it) }
                    navState.currentBackstack.removeRange(1, navState.currentBackstack.size)
                }
            }
        }
    }

    if (onActivate != null) {
        LaunchedEffect(navState) {
            snapshotFlow { navState.topLevelRoute }.collect { route: TopLevelRoute? ->
                if (route != null) onActivate(route)
            }
        }
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
    val useNativeNavigation by remember { conferenceService.isExternalNavigation() }
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
            CompositionLocalProvider(LocalUseNativeNavigation provides useNativeNavigation) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(KotlinConfTheme.colors.mainBackground)
                    .windowInsetsPadding(
                        WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal)
                    )
            ) {
                val content = @Composable {
                    NavDisplay(
                        entries = navState.toDecoratedEntries(entryProvider),
                        onBack = navigator::goBack,
                    )
                }
                if (useNativeNavigation) {
                    content()
                } else {
                    NavScaffold(
                        navState = navState,
                        navigator = navigator,
                        showGoldenKodee = showGoldenKodee,
                        content = content,
                    )
                }

                val baseUrl = LocalAppGraph.current.baseUrl
                val flagsManager = LocalAppGraph.current.flagsManager
                val currentFlags = LocalFlags.current
                val platformFlags = flagsManager.platformFlags
                val aboutAppTitle = stringResource(Res.string.about_app_title)
                if (baseUrl != URLs.PRODUCTION_URL || currentFlags != platformFlags) {
                    DebugMarker(
                        Modifier
                            .align(Alignment.TopCenter)
                            .padding(topInsetPadding())
                            .clip(KotlinConfTheme.shapes.roundedCornerMd)
                            .clickable { navigator.add(AboutAppScreen(aboutAppTitle)) }
                            .padding(8.dp)
                    )
                }
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
        val appTermsTitle = stringResource(Res.string.app_terms)
        AppPrivacyNoticePrompt(
            onRejectNotice = {
                if (skipNotifications) {
                    navigator.set(ScheduleScreen)
                } else {
                    navigator.set(StartNotificationsScreen)
                }
            },
            onAcceptNotice = {
                if (skipNotifications) {
                    navigator.set(ScheduleScreen)
                } else {
                    navigator.set(StartNotificationsScreen)
                }
            },
            onAppTermsOfUse = { navigator.add(AppTermsOfUseScreen(appTermsTitle)) },
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
            onSession = { id, title -> navigator.add(SessionScreen(id, title)) },
            onPrivacyNoticeNeeded = { navigator.add(AppPrivacyNoticePrompt) },
            tabReselections = navigator.tabReselections(forRoute = ScheduleScreen),
        )
    }

    entry<SpeakersScreen>(metadata = noAnimationMetadata) {
        val viewModel = metroViewModel<SpeakersViewModel>()
//        it.searchText?.let { searchText -> viewModel.setSearchText(searchText) }
        SpeakersScreen(
            onSpeaker = { speaker -> navigator.add(SpeakerDetailScreen(speaker.id, speaker.name, speaker.position)) },
            viewModel = viewModel,
        )
    }

    entry<GoldenKodeeScreen>(metadata = noAnimationMetadata) {
        GoldenKodeeScreen(
            onNomineeClick = { categoryId, nomineeId, name, isWinner ->
                navigator.add(GoldenKodeeFinalistScreen(categoryId, nomineeId, name, if (isWinner) "Winner" else "Finalist"))
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
        val aboutConferenceTitle = stringResource(Res.string.about_conference_title)
        val aboutAppTitle = stringResource(Res.string.about_app_title)
        val partnersTitle = stringResource(Res.string.partners_title)
        val codeOfConductTitle = stringResource(Res.string.code_of_conduct)
        val settingsTitle = stringResource(Res.string.settings_title)
        InfoScreen(
            onAboutConf = { navigator.add(AboutConferenceScreen(aboutConferenceTitle)) },
            onHowToFindVenue = { address -> mapHandler.openNavigation(address) },
            onAboutApp = { navigator.add(AboutAppScreen(aboutAppTitle)) },
            onOurPartners = { navigator.add(PartnersScreen(partnersTitle)) },
            onCodeOfConduct = { navigator.add(CodeOfConductScreen(codeOfConductTitle)) },
            onTwitter = { uriHandler.openUri(URLs.TWITTER) },
            onSlack = { uriHandler.openUri(URLs.SLACK) },
            onBluesky = { uriHandler.openUri(URLs.BLUESKY) },
            onSettings = { navigator.add(SettingsScreen(settingsTitle)) },
        )
    }

    entry<SpeakerDetailScreen> {
        SpeakerDetailScreen(
            speakerId = it.speakerId,
            onBack = onBack,
            onSession = { sessionId, title -> navigator.add(SessionScreen(sessionId, title)) },
        )
    }
    entry<SessionScreen> {
        val urlHandler = LocalUriHandler.current
        SessionScreen(
            sessionId = it.sessionId,
            onBack = onBack,
            onPrivacyNoticeNeeded = { navigator.add(AppPrivacyNoticePrompt) },
            onSpeaker = { speaker -> navigator.add(SpeakerDetailScreen(speaker.id, speaker.name, speaker.position)) },
            onWatchVideo = { videoUrl -> urlHandler.openUri(videoUrl) },
            onNavigateToMap = { roomName ->
                navigator.add(NestedMapScreen(roomName))
            },
        )
    }

    entry<AboutAppScreen> {
        val uriHandler = LocalUriHandler.current
        val appPrivacyNoticeTitle = stringResource(Res.string.app_privacy_notice_title)
        val appTermsTitle = stringResource(Res.string.app_terms)
        val licensesTitle = stringResource(Res.string.licenses_title)
        AboutAppScreen(
            onBack = onBack,
            onGitHubRepo = { uriHandler.openUri(URLs.GITHUB_REPO) },
            onRateApp = { getStoreUrl()?.let { uriHandler.openUri(it) } },
            onPrivacyNotice = { navigator.add(AppPrivacyNoticeScreen(appPrivacyNoticeTitle)) },
            onTermsOfUse = { navigator.add(AppTermsOfUseScreen(appTermsTitle)) },
            onLicenses = { navigator.add(LicensesScreen(licensesTitle)) },
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
        val visitorPrivacyTitle = stringResource(Res.string.privacy_notice_for_visitors)
        val generalTermsTitle = stringResource(Res.string.general_terms)
        AboutConference(
            onPrivacyNotice = { navigator.add(VisitorPrivacyNoticeScreen(visitorPrivacyTitle)) },
            onGeneralTerms = { navigator.add(TermsOfUseScreen(generalTermsTitle)) },
            onWebsiteLink = { urlHandler.openUri(URLs.KOTLINCONF_HOMEPAGE) },
            onBack = onBack,
            onSpeaker = { speaker -> navigator.add(SpeakerDetailScreen(speaker.id, speaker.name, speaker.position)) },
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
        val appTermsTitle = stringResource(Res.string.app_terms)
        AppPrivacyNotice(
            onBack = onBack,
            onAppTermsOfUse = { navigator.add(AppTermsOfUseScreen(appTermsTitle)) },
        )
    }
    entry<TermsOfUseScreen> {
        val codeOfConductTitle = stringResource(Res.string.code_of_conduct)
        val visitorPrivacyTitle = stringResource(Res.string.privacy_notice_for_visitors)
        VisitorTermsOfUse(
            onBack = onBack,
            onCodeOfConduct = { navigator.add(CodeOfConductScreen(codeOfConductTitle)) },
            onVisitorPrivacyNotice = { navigator.add(VisitorPrivacyNoticeScreen(visitorPrivacyTitle)) },
        )
    }
    entry<AppTermsOfUseScreen> {
        val appPrivacyNoticeTitle = stringResource(Res.string.app_privacy_notice_title)
        AppTermsOfUse(
            onBack = onBack,
            onAppPrivacyNotice = { navigator.add(AppPrivacyNoticeScreen(appPrivacyNoticeTitle)) },
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
        val appTermsTitle = stringResource(Res.string.app_terms)
        AppPrivacyNoticePrompt(
            onRejectNotice = onBack,
            onAcceptNotice = onBack,
            onAppTermsOfUse = { navigator.add(AppTermsOfUseScreen(appTermsTitle)) },
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

@Composable
internal fun ScreenContent(
    route: AppRoute,
    onNavigate: (AppRoute) -> Unit,
    onBack: () -> Unit,
    onSet: (AppRoute) -> Unit = {},
    onActivate: (TopLevelRoute) -> Unit = {},
) {
    val uriHandler = LocalUriHandler.current
    val mapHandler = LocalMapHandler.current
    val aboutConferenceTitle = stringResource(Res.string.about_conference_title)
    val aboutAppTitle = stringResource(Res.string.about_app_title)
    val partnersTitle = stringResource(Res.string.partners_title)
    val codeOfConductTitle = stringResource(Res.string.code_of_conduct)
    val settingsTitle = stringResource(Res.string.settings_title)
    val visitorPrivacyTitle = stringResource(Res.string.privacy_notice_for_visitors)
    val appPrivacyNoticeTitle = stringResource(Res.string.app_privacy_notice_title)
    val generalTermsTitle = stringResource(Res.string.general_terms)
    val appTermsTitle = stringResource(Res.string.app_terms)
    val licensesTitle = stringResource(Res.string.licenses_title)
    when (route) {
        is ScheduleScreen -> {
            val service: ConferenceService = LocalAppGraph.current.conferenceService
            LaunchedEffect(Unit) {
                service.completeOnboarding()
            }
            ScheduleScreen(
                onSession = { sessionId, title -> onNavigate(SessionScreen(sessionId, title)) },
                onPrivacyNoticeNeeded = { onNavigate(AppPrivacyNoticePrompt) },
                tabReselections = emptyFlow(),
            )
        }

        is SpeakersScreen -> {
            val viewModel = metroViewModel<SpeakersViewModel>()
            SpeakersScreen(
                onSpeaker = { speaker -> onNavigate(SpeakerDetailScreen(speaker.id, speaker.name, speaker.position)) },
                viewModel = viewModel,
            )
        }

        is GoldenKodeeScreen -> {
            GoldenKodeeScreen(
                onNomineeClick = { categoryId, nomineeId, name, isWinner ->
                    onNavigate(GoldenKodeeFinalistScreen(categoryId, nomineeId, name, if (isWinner) "Winner" else "Finalist"))
                },
            )
        }

        is GoldenKodeeFinalistScreen -> {
            GoldenKodeeFinalistScreen(
                categoryId = route.categoryId,
                nomineeId = route.nomineeId,
                onBack = onBack,
            )
        }

        is MapScreen -> {
            MapScreen(
                onHowToFindVenue = { address -> mapHandler.openNavigation(address) },
            )
        }

        is InfoScreen -> {
            InfoScreen(
                onAboutConf = { onNavigate(AboutConferenceScreen(aboutConferenceTitle)) },
                onHowToFindVenue = { address -> mapHandler.openNavigation(address) },
                onAboutApp = { onNavigate(AboutAppScreen(aboutAppTitle)) },
                onOurPartners = { onNavigate(PartnersScreen(partnersTitle)) },
                onCodeOfConduct = { onNavigate(CodeOfConductScreen(codeOfConductTitle)) },
                onTwitter = { uriHandler.openUri(URLs.TWITTER) },
                onSlack = { uriHandler.openUri(URLs.SLACK) },
                onBluesky = { uriHandler.openUri(URLs.BLUESKY) },
                onSettings = { onNavigate(SettingsScreen(settingsTitle)) },
            )
        }

        is SpeakerDetailScreen -> {
            SpeakerDetailScreen(
                speakerId = route.speakerId,
                onBack = onBack,
                onSession = { sessionId, title -> onNavigate(SessionScreen(sessionId, title)) },
            )
        }

        is SessionScreen -> {
            SessionScreen(
                sessionId = route.sessionId,
                onBack = onBack,
                onPrivacyNoticeNeeded = { onNavigate(AppPrivacyNoticePrompt) },
                onSpeaker = { speaker -> onNavigate(SpeakerDetailScreen(speaker.id, speaker.name, speaker.position)) },
                onWatchVideo = { videoUrl -> uriHandler.openUri(videoUrl) },
                onNavigateToMap = { roomName -> onNavigate(NestedMapScreen(roomName)) },
            )
        }

        is AboutAppScreen -> {
            AboutAppScreen(
                onBack = onBack,
                onGitHubRepo = { uriHandler.openUri(URLs.GITHUB_REPO) },
                onRateApp = { getStoreUrl()?.let { uriHandler.openUri(it) } },
                onPrivacyNotice = { onNavigate(AppPrivacyNoticeScreen(appPrivacyNoticeTitle)) },
                onTermsOfUse = { onNavigate(AppTermsOfUseScreen(appTermsTitle)) },
                onLicenses = { onNavigate(LicensesScreen(licensesTitle)) },
                onJunie = { uriHandler.openUri(URLs.JUNIE_LANDING_PAGE) },
                onDeveloperMenu = { skipDelay -> onNavigate(DeveloperMenuScreen(skipWarningDelay = skipDelay)) },
            )
        }

        is LicensesScreen -> {
            LicensesScreen(
                onLicenseClick = { licenseName, licenseText ->
                    onNavigate(SingleLicenseScreen(licenseName, licenseText))
                },
                onBack = onBack,
            )
        }

        is SingleLicenseScreen -> {
            SingleLicenseScreen(
                licenseName = route.licenseName,
                licenseContent = route.licenseText,
                onBack = onBack,
            )
        }

        is AboutConferenceScreen -> {
            AboutConference(
                onPrivacyNotice = { onNavigate(VisitorPrivacyNoticeScreen(visitorPrivacyTitle)) },
                onGeneralTerms = { onNavigate(TermsOfUseScreen(generalTermsTitle)) },
                onWebsiteLink = { uriHandler.openUri(URLs.KOTLINCONF_HOMEPAGE) },
                onBack = onBack,
                onSpeaker = { speaker -> onNavigate(SpeakerDetailScreen(speaker.id, speaker.name, speaker.position)) },
            )
        }

        is CodeOfConductScreen -> {
            CodeOfConduct(onBack = onBack)
        }

        is SettingsScreen -> {
            SettingsScreen(onBack = onBack)
        }

        is VisitorPrivacyNoticeScreen -> {
            VisitorPrivacyNotice(onBack = onBack)
        }

        is AppPrivacyNoticeScreen -> {
            AppPrivacyNotice(
                onBack = onBack,
                onAppTermsOfUse = { onNavigate(AppTermsOfUseScreen(appTermsTitle)) },
            )
        }

        is TermsOfUseScreen -> {
            VisitorTermsOfUse(
                onBack = onBack,
                onCodeOfConduct = { onNavigate(CodeOfConductScreen(codeOfConductTitle)) },
                onVisitorPrivacyNotice = { onNavigate(VisitorPrivacyNoticeScreen(visitorPrivacyTitle)) },
            )
        }

        is AppTermsOfUseScreen -> {
            AppTermsOfUse(
                onBack = onBack,
                onAppPrivacyNotice = { onNavigate(AppPrivacyNoticeScreen(appPrivacyNoticeTitle)) },
            )
        }

        is PartnersScreen -> {
            PartnersScreen(
                onBack = onBack,
                onPartnerDetail = { partnerId -> onNavigate(PartnerDetailScreen(partnerId)) }
            )
        }

        is PartnerDetailScreen -> {
            PartnerDetailScreen(
                partnerId = route.partnerId,
                onBack = onBack,
            )
        }

        is AppPrivacyNoticePrompt -> {
            AppPrivacyNoticePrompt(
                onRejectNotice = onBack,
                onAcceptNotice = onBack,
                onAppTermsOfUse = { onNavigate(AppTermsOfUseScreen(appTermsTitle)) },
                confirmationRequired = true,
            )
        }

        is DeveloperMenuScreen -> {
            DeveloperMenuScreenContent(
                onBack = onBack,
                skipWarningDelay = route.skipWarningDelay,
            )
        }

        is NestedMapScreen -> {
            NestedMapScreen(
                roomName = route.roomName,
                onBack = onBack,
            )
        }

        is StartPrivacyNoticeScreen -> {
            val skipNotifications = LocalFlags.current.supportsNotifications.not()
            AppPrivacyNoticePrompt(
                onRejectNotice = {
                    if (skipNotifications) onSet(ScheduleScreen) else onNavigate(StartNotificationsScreen)
                },
                onAcceptNotice = {
                    if (skipNotifications) onSet(ScheduleScreen) else onNavigate(StartNotificationsScreen)
                },
                onAppTermsOfUse = { onNavigate(AppTermsOfUseScreen(appTermsTitle)) },
                confirmationRequired = false,
            )
        }

        is StartNotificationsScreen -> {
            StartNotificationsScreen(
                onDone = { onSet(ScheduleScreen) }
            )
        }
    }
}
