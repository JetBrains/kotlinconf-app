package org.jetbrains.kotlinconf.backend

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.date.*
import org.jetbrains.kotlinconf.*
import org.jetbrains.kotlinconf.Votes
import java.time.*

internal fun Route.api(
    store: Store,
    sessionizeUrl: String,
    imagesUrl: String,
    adminSecret: String
) {
    apiUsers(store)
    sessions()
    apiVote(store, adminSecret)
    apiSynchronize(sessionizeUrl, adminSecret)
    apiTime(adminSecret)
    apiSessionizeImagesProxy(imagesUrl)
}

/*
POST http://localhost:8080/sign
1238476512873162837
 */
private fun Route.apiUsers(database: Store) {
    post("sign") {
        val userUUID = call.receive<String>()
        val timestamp = LocalDateTime.now(Clock.systemUTC())
        val created = database.createUser(userUUID, timestamp)
        val code = if (created) HttpStatusCode.Created else HttpStatusCode.Conflict
        call.respond(code)
    }
}

/*
GET http://localhost:8080/vote
Accept: application/json
Authorization: Bearer 1238476512873162837
*/
private fun Route.apiVote(
    database: Store,
    adminSecret: String
) {
    route("vote") {
        get {
            val principal = call.validatePrincipal(database) ?: throw Unauthorized()
            val votes = database.getVotes(principal.token)
            call.respond(Votes(votes))
        }
        post {
            val principal = call.validatePrincipal(database) ?: throw Unauthorized()
            val vote = call.receive<VoteInfo>()
            val sessionId = vote.sessionId

            val session = getSessionizeData().sessions.firstOrNull { it.id == sessionId }
                ?: throw NotFound()

            val nowTime = now()

            val startVotesAt = session.startsAt
            val votingPeriodStarted = nowTime >= startVotesAt.timestamp

            if (!votingPeriodStarted) {
                return@post call.respond(comeBackLater)
            }

            val timestamp = LocalDateTime.now(Clock.systemUTC())
            database.changeVote(principal.token, sessionId, vote.score, timestamp)
            call.respond(HttpStatusCode.OK)
        }

        /**
         * Admin endpoints
         */
        get("all") {
            call.validateSecret(adminSecret)

            val votes = database.getAllVotes()
            call.respond(votes)
        }
    }
    route("feedback") {
        post {
            val principal = call.validatePrincipal(database) ?: throw Unauthorized()
            val feedback = call.receive<FeedbackInfo>()

            val timestamp = LocalDateTime.now(Clock.systemUTC())

            val result = database.setFeedback(
                principal.token, feedback.sessionId, feedback.value, timestamp
            )

            if (result) {
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.Forbidden)
            }
        }
        get("summary") {
            call.validateSecret(adminSecret)
            call.respond(database.getFeedbackSummary())
        }
    }
}

/*
GET http://localhost:8080/conference
Accept: application/json
Authorization: Bearer 1238476512873162837
*/
private fun Route.sessions() {
    get("conference") {
        call.respond(getSessionizeData())
    }
}

/**
 * Admin endpoints
 * GET http://localhost:8080/time
 *
 * POST http://localhost:8080/time/1589568000000
 */
private fun Route.apiTime(adminSecret: String) {
    get("time") {
        call.respond(now())
    }
    post("time/{timestamp}") {
        call.validateSecret(adminSecret)

        val timestamp = call.parameters["timestamp"] ?: error("No time")
        val time = if (timestamp == "null") {
            null
        } else {
            GMTDate(timestamp.toLong())
        }

        updateTime(time)
        call.respond(HttpStatusCode.OK)
    }
}

/*
POST http://localhost:8080/sessionizeSync
*/
private fun Route.apiSynchronize(
    sessionizeUrl: String,
    adminSecret: String
) {
    post("sessionizeSync") {
        call.validateSecret(adminSecret)

        synchronizeWithSessionize(sessionizeUrl)
        call.respond(HttpStatusCode.OK)
    }
}

/*
GET http://localhost:8080/sessionize/image/{imageId}
Authorization: Bearer 1238476512873162837
*/
private fun Route.apiSessionizeImagesProxy(imagesUrl: String) {
    get("sessionize/image/{imageId}") {
        call.respond(fetchSessionizeImage(imagesUrl, call.parameters["imageId"] ?: error("No imageId")))
    }
}


private fun ApplicationCall.validateSecret(adminSecret: String) {
    val principal = principal<KotlinConfPrincipal>()
    if (principal?.token != adminSecret) {
        throw Unauthorized()
    }
}

private suspend fun ApplicationCall.validatePrincipal(database: Store): KotlinConfPrincipal? {
    val principal = principal<KotlinConfPrincipal>() ?: return null
    if (!database.validateUser(principal.token)) return null
    return principal
}

