package org.jetbrains.kotlinconf.backend.routes

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.kotlinconf.EVENT_TIME_ZONE
import org.jetbrains.kotlinconf.FeedbackInfo
import org.jetbrains.kotlinconf.VoteInfo
import org.jetbrains.kotlinconf.Votes
import org.jetbrains.kotlinconf.backend.repositories.KotlinConfRepository
import org.jetbrains.kotlinconf.backend.services.SessionizeService
import org.jetbrains.kotlinconf.backend.services.TimeService
import org.jetbrains.kotlinconf.backend.utils.ConferenceConfig
import org.jetbrains.kotlinconf.backend.utils.NotFound
import org.jetbrains.kotlinconf.backend.utils.Unauthorized
import org.koin.ktor.ext.inject

private val COME_BACK_LATER = HttpStatusCode(477, "Come Back Later")

/*
GET http://localhost:8080/vote
Accept: application/json
Authorization: Bearer 1238476512873162837
*/
fun Route.votingRoutes() {
    val repository by inject<KotlinConfRepository>()
    val config: ConferenceConfig by inject()
    val sessionize: SessionizeService by inject()
    val timeService: TimeService by inject()

    route("vote") {
        get {
            val principal = call.validatePrincipal(repository) ?: throw Unauthorized()
            val votes = repository.getVotes(principal.token)
            call.respond(Votes(votes))
        }
        post {
            val principal = call.validatePrincipal(repository) ?: throw Unauthorized()
            val vote = call.receive<VoteInfo>()
            val sessionId = vote.sessionId

            val session = sessionize.getConferenceData().sessions.firstOrNull { it.id == sessionId }
                ?: throw NotFound()
2
            val nowTime = timeService.now()

            val startVotesAt = session.startsAt.toInstant(EVENT_TIME_ZONE)
            val votingPeriodStarted = nowTime >= startVotesAt.toEpochMilliseconds()

            if (!votingPeriodStarted) {
                return@post call.respond(COME_BACK_LATER)
            }

            val timestamp = Clock.System.now().toLocalDateTime(TimeZone.UTC)
            repository.changeVote(principal.token, sessionId, vote.score, timestamp)
            call.respond(HttpStatusCode.OK)
        }

        /**
         * Admin endpoints
         */
        get("all") {
            call.checkAdminKey(config.adminSecret)

            val votes = repository.getAllVotes()
            call.respond(votes)
        }
    }
    route("feedback") {
        post {
            val principal = call.validatePrincipal(repository) ?: throw Unauthorized()
            val feedback = call.receive<FeedbackInfo>()

            val timestamp = Clock.System.now().toLocalDateTime(TimeZone.UTC)

            val result = repository.setFeedback(
                principal.token, feedback.sessionId, feedback.value, timestamp
            )

            if (result) {
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.Forbidden)
            }
        }
        get("summary") {
            call.checkAdminKey(config.adminSecret)
            call.respond(repository.getFeedbackSummary())
        }
    }
}
