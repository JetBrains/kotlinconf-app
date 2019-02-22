@file:Suppress("NestedLambdaShadowedImplicitParameter")

package org.jetbrains.kotlinconf.backend

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import org.jetbrains.kotlinconf.data.*
import java.time.*
import java.time.format.*
import java.util.*
import java.util.concurrent.*

internal fun Routing.api(database: Database, production: Boolean, sessionizeUrl: String) {
    apiKeynote(production)
    apiUsers(database)
    apiAll(database)
    apiSession()
    apiVote(database, production)
    apiFavorite(database, production)
    apiSynchronize(sessionizeUrl)
    wsVotes(database, production)
}

/*
GET http://localhost:8080/keynote?datetimeoverride=2017-10-24T10:00-07:00
 */
private fun Routing.apiKeynote(production: Boolean) {
    get("keynote") {
        val nowTime = simulatedTime(production)
        if (nowTime.isAfter(keynoteEndDateTime))
            call.respond(HttpStatusCode.OK)
        else {
            call.respond(comeBackLater)
        }
    }
}

private fun PipelineContext<Unit, ApplicationCall>.simulatedTime(production: Boolean): ZonedDateTime {
    val now = ZonedDateTime.now(keynoteTimeZone)
    return if (production)
        now
    else
        call.parameters["datetimeoverride"]?.let { ZonedDateTime.parse(it) } ?: now
}

/*
POST http://localhost:8080/user
1238476512873162837
 */
private fun Routing.apiUsers(database: Database) {
    route("users") {
        post {
            val userUUID = call.receive<String>()
            val ip = call.request.origin.remoteHost
            val timestamp = LocalDateTime.now(Clock.systemUTC())
            val created = database.createUser(userUUID, ip, timestamp)
            if (created)
                call.respond(HttpStatusCode.Created)
            else
                call.respond(HttpStatusCode.Conflict)
        }
        get("count") {
            call.respondText(database.usersCount().toString())
        }
    }
}

internal suspend fun ApplicationCall.validatePrincipal(database: Database): KotlinConfPrincipal? {
    val principal = principal<KotlinConfPrincipal>() ?: return null
    if (!database.validateUser(principal.token)) return null
    return principal
}

/*
GET http://localhost:8080/favorites
Accept: application/json
Authorization: Bearer 1238476512873162837
*/
internal fun Routing.apiFavorite(database: Database, production: Boolean) {
    route("favorites") {
        get {
            val principal = call.validatePrincipal(database) ?: throw Unauthorized()
            val favorites = database.getFavorites(principal.token)
            call.respond(favorites)
        }
        if (!production) {
            get("all") {
                val favorites = database.getAllFavorites()
                call.respond(favorites)
            }
        }
        post {
            val principal = call.validatePrincipal(database) ?: throw Unauthorized()
            val favorite = call.receive<Favorite>()
            val sessionId = favorite.sessionId
            database.createFavorite(principal.token, sessionId)
            call.respond(HttpStatusCode.Created)
        }
        delete {
            val principal = call.validatePrincipal(database) ?: throw Unauthorized()
            val favorite = call.receive<Favorite>()
            val sessionId = favorite.sessionId
            database.deleteFavorite(principal.token, sessionId)
            call.respond(HttpStatusCode.OK)
        }
    }
}

private val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.US)

/*
GET http://localhost:8080/votes
Accept: application/json
Authorization: Bearer 1238476512873162837
*/
internal fun Routing.apiVote(database: Database, production: Boolean) {
    route("votes") {
        get {
            val principal = call.validatePrincipal(database) ?: throw Unauthorized()
            val votes = database.getVotes(principal.token)
            call.respond(votes)
        }
        get("all") {
            val votes = database.getAllVotes()
            call.respond(votes)
        }
        get("summary/{sessionId}") {
            val id = call.parameters["sessionId"] ?: throw BadRequest()
            val votesSummary = database.getVotesSummary(id)
            call.respond(votesSummary)
        }
        post {
            val principal = call.validatePrincipal(database) ?: throw Unauthorized()
            val vote = call.receive<Vote>()
            val sessionId = vote.sessionId
            val rating = vote.rating

            val session = getSessionizeData().allData.sessions.firstOrNull { it.id == sessionId } ?: throw NotFound()
            val nowTime = simulatedTime(production)
            val startVotesAt = LocalDateTime.parse(session.startsAt, dateFormat)
            val endVotesAt = LocalDateTime.parse(session.endsAt, dateFormat).plusMinutes(15)
            val votingPeriodStarted =
                startVotesAt?.let { ZonedDateTime.of(it, keynoteTimeZone).isBefore(nowTime) } ?: true
            val votingPeriodEnded = endVotesAt?.let { ZonedDateTime.of(it, keynoteTimeZone).isBefore(nowTime) } ?: true

            if (!votingPeriodStarted)
                return@post call.respond(comeBackLater)
            if (votingPeriodEnded)
                return@post call.respond(tooLate)

            val timestamp = LocalDateTime.now(Clock.systemUTC())
            if (database.changeVote(principal.token, sessionId, rating, timestamp))
                call.respond(HttpStatusCode.Created)
            else
                call.respond(HttpStatusCode.OK)
            signalSession(sessionId)
        }
        delete {
            val principal = call.validatePrincipal(database) ?: throw Unauthorized()
            val vote = call.receive<Vote>()
            val sessionId = vote.sessionId
            database.deleteVote(principal.token, sessionId)
            call.respond(HttpStatusCode.OK)
            signalSession(sessionId)
        }
    }
}


/*
GET http://localhost:8080/all
Accept: application/json
Authorization: Bearer 1238476512873162837
*/
internal fun Routing.apiAll(database: Database) {
    get("all") {
        val data = getSessionizeData()
        val principal = call.validatePrincipal(database)
        val responseData = if (principal != null) {
            val votes = database.getVotes(principal.token)
            val favorites = database.getFavorites(principal.token)
            val personalizedData = data.allData.copy(votes = votes, favorites = favorites)
            SessionizeData(personalizedData)
        } else data

        call.withETag(responseData.etag, putHeader = true) {
            call.respond(responseData.allData)
        }
    }
}

internal fun Routing.apiSession() {
    route("sessions") {
        get {
            val data = getSessionizeData()
            val sessions = data.allData.sessions
            call.withETag(sessions.hashCode().toString(), putHeader = true) {
                call.respond(sessions)
            }
        }
        get("{sessionId}") {
            val data = getSessionizeData()
            val id = call.parameters["sessionId"] ?: throw BadRequest()
            val sessions = data.allData.sessions?.singleOrNull { it.id == id } ?: throw NotFound()
            call.withETag(sessions.hashCode().toString(), putHeader = true) {
                call.respond(sessions)
            }
        }
    }
}

// maps sessionId to the "session updated" signal (a signal is just Unit -- it carries no additional data).
internal val sessionSignals = ConcurrentHashMap<String, ConflatedBroadcastChannel<Unit>>()

internal fun signalSession(sessionId: String) =
    sessionSignals[sessionId]?.offer(Unit) // offer to anyone who's interested

internal fun trackSession(sessionId: String): ConflatedBroadcastChannel<Unit> =
    sessionSignals.computeIfAbsent(sessionId) { ConflatedBroadcastChannel(Unit) }

internal fun Routing.wsVotes(database: Database, production: Boolean) {
    val route = if (production) fakeSessionId else "{sessionId}"
    webSocket("sessions/$route/votes") {
        val id = call.parameters["sessionId"] ?: fakeSessionId
        trackSession(id).openSubscription().consume {
            consumeEach {
                @UseExperimental(ImplicitReflectionSerializer::class)
                outgoing.send(Frame.Text(Json.stringify(database.getVotesSummary(id))))
            }
        }
    }
}

/*
GET http://localhost:8080/sessionizeSync
*/
internal fun Routing.apiSynchronize(sessionizeUrl: String) {
    get("sessionizeSync") {
        synchronizeWithSessionize(sessionizeUrl)
        call.respond(HttpStatusCode.OK)
    }
}