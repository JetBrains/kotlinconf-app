package org.jetbrains.kotlinconf

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.kotlinconf.ui.AboutConf
import org.jetbrains.kotlinconf.ui.CODE_OF_CONDUCT
import org.jetbrains.kotlinconf.ui.MOBILE_APP_DESCRIPTION
import org.jetbrains.kotlinconf.ui.Menu
import org.jetbrains.kotlinconf.ui.NavigationBar
import org.jetbrains.kotlinconf.ui.Partner
import org.jetbrains.kotlinconf.ui.Partners
import org.jetbrains.kotlinconf.ui.PrivacyPolicyScreen
import org.jetbrains.kotlinconf.ui.Search
import org.jetbrains.kotlinconf.ui.SessionDetailed
import org.jetbrains.kotlinconf.ui.SpeakersFlow
import org.jetbrains.kotlinconf.ui.TermsOfUse
import org.jetbrains.kotlinconf.ui.TextScreen
import org.jetbrains.kotlinconf.ui.welcome.WelcomeScreen

typealias View = @Composable (AppController) -> Unit

@Composable
fun withAppController(
    service: ConferenceService,
    default: @Composable (AppController) -> Unit
) {
    val controller = remember { AppController(service, default) }
    val last by controller.last.collectAsState()
    last(controller)
}

class AppController(
    private val service: ConferenceService,
    val default: @Composable (AppController) -> Unit = {}
) {
    private val stack = mutableListOf<View>()
    val last: MutableStateFlow<View> = MutableStateFlow(default)

    fun push(item: @Composable (AppController) -> Unit) {
        stack.add(item)
        last.value = item
    }

    fun showMenu() {
        push {
            Menu(it)
        }
    }

    fun showSession(sessionId: String) {
        push {
            val session: SessionCardView? = service.sessionsCards.collectAsState()
                .value.firstOrNull { it.id == sessionId }
            val speakers = session?.speakerIds?.map { service.speakerById(it) }
            if (session != null && speakers != null) {
                SessionDetailed(
                    time = session.timeLine,
                    title = session.title,
                    description = session.description,
                    location = session.locationLine,
                    speakers = speakers,
                    isFavorite = session.isFavorite,
                    isFinished = session.isFinished,
                    vote = session.vote,
                    id = session.id,
                    isLightning = session.isLightning,
                    isCodeLab = session.isCodeLab,
                    isAWS = session.isAWSLab,
                    controller = this
                )
            }
        }
    }

    fun showSpeaker(speakerId: String) {
        push {
            val speakers by service.speakers.collectAsState()

            SpeakersFlow(
                controller = this,
                speakers = speakers.all,
                focusedSpeakerId = speakerId
            )
        }
    }

    fun back() {
        if (stack.isEmpty()) return

        stack.removeLast()
        last.value = stack.lastOrNull() ?: default
    }

    fun toggleFavorite(sessionId: String) {
        service.toggleFavorite(sessionId)
    }

    fun vote(sessionId: String, it: Score?) {
        GlobalScope.launch {
            if (!service.vote(sessionId, it)) showPrivacyPolicyPrompt()
        }
    }

    fun sendFeedback(sessionId: String, feedback: String) {
        GlobalScope.launch {
            if (!service.sendFeedback(sessionId, feedback)) showPrivacyPolicyPrompt()
        }
    }

    fun showPrivacyPolicyPrompt() {
        push {
            WelcomeScreen(onAcceptPrivacy = {
                service.acceptPrivacyPolicy()
                it.back()
            }, onRejectPrivacy = {
                it.back()
            }, onClose = {
            }, onAcceptNotifications = {
            })
        }
    }

    fun showSearch() {
        push {
            val agenda by service.agenda.collectAsState()
            val sessions = agenda.days.flatMap { it.timeSlots.flatMap { it.sessions } }
            val speakers by service.speakers.collectAsState()
            Search(it, sessions, speakers.all)
        }
    }

    fun showAppInfo() {
        push {
            TextScreen("’24 mobile app", null, MOBILE_APP_DESCRIPTION) { it.back() }
        }
    }

    fun showPartners() {
        push {
            Partners({
                showPartner(it)
            }, {
                back()
            })
        }
    }

    fun showCodeOfConduct() {
        push {
            TextScreen(
                "Code of Conduct",
                "KotlinConf code of conduct",
                CODE_OF_CONDUCT
            ) { it.back() }
        }
    }

    fun showPartner(name: String) {
        push {
            Partner(it, name, service.partnerDescription(name))
        }
    }

    fun sessionsForSpeaker(id: String): List<SessionCardView> {
        return service.sessionsForSpeaker(id)
    }

    fun showAboutTheConf() {
        push {
            val keynoteSpeakers = service.speakers.value.all.filter {
                it.name == "Roman Elizarov" || it.name == "Svetlana Isakova" || it.name == "Grace Kloba" || it.name == "Egor Tolstoy"
            }

            val secondDaySpeaker = service.speakers.value.all.filter {
                it.name == "Kevlin Henney"
            }
            AboutConf(keynoteSpeakers = keynoteSpeakers, secondDaySpeakers = secondDaySpeaker) {
                it.back()
            }
        }
    }

    @OptIn(ExperimentalResourceApi::class)
    fun showPrivacyPolicy() {
        push {
            Column {
                NavigationBar(
                    title = "Privacy Policy",
                    isRightVisible = false,
                    onLeftClick = { back() },
                )
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    PrivacyPolicyScreen()
                }
            }
        }
    }

    @OptIn(ExperimentalResourceApi::class)
    fun showTerms() {
        push {
            Column {
                NavigationBar(
                    title = "Terms of Use",
                    isRightVisible = false,
                    onLeftClick = { back() },
                )
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    TermsOfUse()
                }
            }
        }
    }
}