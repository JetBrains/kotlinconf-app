package org.jetbrains.kotlinconf

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.kotlinconf.ui.AboutAppScreen
import org.jetbrains.kotlinconf.ui.AboutConfScreen
import org.jetbrains.kotlinconf.ui.AppPrivacyPolicyScreen
import org.jetbrains.kotlinconf.ui.AppTermsOfUseScreen
import org.jetbrains.kotlinconf.ui.CodeOfConductScreen
import org.jetbrains.kotlinconf.ui.Partner
import org.jetbrains.kotlinconf.ui.Partners
import org.jetbrains.kotlinconf.ui.SearchScreen
import org.jetbrains.kotlinconf.ui.SessionScreen
import org.jetbrains.kotlinconf.ui.SpeakersDetailsScreen
import org.jetbrains.kotlinconf.ui.VisitorsPrivacyPolicyScreen
import org.jetbrains.kotlinconf.ui.VisitorsTermsScreen
import org.jetbrains.kotlinconf.ui.components.NavigationBar
import org.jetbrains.kotlinconf.ui.welcome.WelcomeScreen
import org.jetbrains.kotlinconf.utils.StateFlowClass

typealias View = @Composable (AppController) -> Unit

class AppController(private val service: ConferenceService) {
    private val stack = mutableListOf<View>()
    val last: MutableStateFlow<View?> = MutableStateFlow(null)
    val sessions: StateFlowClass<List<SessionCardView>> = service.sessionCards

    private val currentRoute = MutableStateFlow<String>("")

    fun routeTo(value: String) {
        if (value == currentRoute.value) return

        stack.clear()
        last.value = null
        currentRoute.value = value
    }

    fun push(item: @Composable (AppController) -> Unit) {
        stack.add(item)
        last.value = item
    }

    fun showSession(sessionId: String) {
        push {
            val session: SessionCardView? =
                service.sessionCards.collectAsState().value.firstOrNull { it.id == sessionId }
            val speakers = session?.speakerIds?.map { service.speakerById(it) }
            if (session != null && speakers != null) {
                SessionScreen(
                    id = session.id,
                    time = session.timeLine,
                    title = session.title,
                    description = session.description,
                    location = session.locationLine,
                    isFavorite = session.isFavorite,
                    vote = session.vote,
                    isFinished = session.isFinished,
                    speakers = speakers,
                    tags = session.tags,
                    controller = this
                )
            }
        }
    }

    fun showSpeaker(speakerId: String) {
        push {
            val speakers by service.speakers.collectAsState()

            SpeakersDetailsScreen(
                controller = this, speakers = speakers.all, focusedSpeakerId = speakerId
            )
        }
    }

    fun back() {
        if (stack.isEmpty()) return

        stack.removeLast()
        last.value = stack.lastOrNull()
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
            }, onClose = {}, onAcceptNotifications = {})
        }
    }

    fun showSearch() {
        push {
            val agenda by service.agenda.collectAsState()
            val sessions = agenda.days.flatMap { it.timeSlots.flatMap { it.sessions } }
            val speakers by service.speakers.collectAsState()
            SearchScreen(it, sessions, speakers.all)
        }
    }

    fun showAppInfo() {
        push {
            AboutAppScreen(showAppPrivacyPolicy = { showAppPrivacyPolicy() },
                showAppTerms = { showAppTerms() },
                back = { back() })
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
            CodeOfConductScreen { it.back() }
        }
    }

    fun showPartner(partner: Partner) {
        push {
            Partner(it, partner)
        }
    }

    fun showAboutTheConf() {
        push {
            AboutConfScreen(service,
                showVisitorsPrivacyPolicy = { showVisitorsPrivacy() },
                showVisitorsTerms = { showVisitorsTerms() },
                back = { it.back() })
        }
    }

    fun showAppPrivacyPolicy() {
        push("Privacy policy") {
            AppPrivacyPolicyScreen(false) {}
        }
    }

    fun showAppTerms() {
        push("Terms of use") {
            AppTermsOfUseScreen()
        }
    }

    fun showVisitorsPrivacy() {
        push("Privacy policy") {
            VisitorsPrivacyPolicyScreen()
        }
    }

    fun showVisitorsTerms() {
        push("Terms and Conditions") {
            VisitorsTermsScreen()
        }
    }

    @OptIn(ExperimentalResourceApi::class)
    private fun push(title: String, block: @Composable () -> Unit) {
        push {
            Column {
                NavigationBar(
                    title = title,
                    isRightVisible = false,
                    onLeftClick = { back() },
                )
                block()
            }
        }
    }
}