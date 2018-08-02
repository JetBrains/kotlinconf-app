package org.jetbrains.kotlinconf.backend

import org.jetbrains.kotlinconf.data.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.pipeline.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.http.cio.websocket.Frame
import io.ktor.websocket.webSocket
import kotlinx.coroutines.experimental.channels.*
import java.time.*
import java.time.format.*
import java.util.*
import java.util.concurrent.*

fun Routing.api(database: Database, production: Boolean) {
    apiKeynote(database, production)
    apiRegister(database, production)
    apiAll(database, production)
    apiSession(database, production)
    apiVote(database, production)
    apiFavorite(database, production)
    wsVotes(database, production)
}

/*
GET http://localhost:8080/keynote?datetimeoverride=2017-10-24T10:00-07:00
 */
fun Routing.apiKeynote(database: Database, production: Boolean) {
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
    return if (production) now else call.parameters["datetimeoverride"]?.let { ZonedDateTime.parse(it) } ?: now
}

/*
POST http://localhost:8080/user
1238476512873162837
 */
fun Routing.apiRegister(database: Database, production: Boolean) {
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

suspend fun ApplicationCall.validatePrincipal(database: Database): KotlinConfPrincipal? {
    val principal = principal<KotlinConfPrincipal>() ?: return null
    if (!database.validateUser(principal.token)) return null
    return principal
}

/*
GET http://localhost:8080/favorites
Accept: application/json
Authorization: Bearer 1238476512873162837
*/
fun Routing.apiFavorite(database: Database, production: Boolean) {
    route("favorites") {
        get {
            val principal = call.validatePrincipal(database) ?: return@get call.respond(HttpStatusCode.Unauthorized)
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
            val principal = call.validatePrincipal(database) ?: return@post call.respond(HttpStatusCode.Unauthorized)
            val favorite = call.receive<Favorite>()
            val sessionId = favorite.sessionId ?: return@post call.respond(HttpStatusCode.BadRequest)
            database.createFavorite(principal.token, sessionId)
            call.respond(HttpStatusCode.Created)
        }
        delete {
            val principal = call.validatePrincipal(database) ?: return@delete call.respond(HttpStatusCode.Unauthorized)
            val favorite = call.receive<Favorite>()
            val sessionId = favorite.sessionId ?: return@delete call.respond(HttpStatusCode.BadRequest)
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
fun Routing.apiVote(database: Database, production: Boolean) {
    route("votes") {
        get {
            val principal = call.validatePrincipal(database) ?: return@get call.respond(HttpStatusCode.Unauthorized)
            val votes = database.getVotes(principal.token)
            call.respond(votes)
        }
        if (production) {
            get("summary/$fakeSessionId") {
                val votesSummary = database.getVotesSummary(fakeSessionId)
                call.respond(votesSummary)
            }
        } else {
            get("all") {
                val votes = database.getAllVotes()
                call.respond(votes)
            }
            get("summary/{sessionId}") {
                val id = call.parameters["sessionId"] ?: return@get call.respond(HttpStatusCode.BadRequest)
                val votesSummary = database.getVotesSummary(id)
                call.respond(votesSummary)
            }
        }
        post {
            val principal = call.validatePrincipal(database) ?: return@post call.respond(HttpStatusCode.Unauthorized)
            val vote = call.receive<Vote>()
            val sessionId = vote.sessionId ?: return@post call.respond(HttpStatusCode.BadRequest)
            val rating = vote.rating ?: return@post call.respond(HttpStatusCode.BadRequest)

            val session = sessionizeData?.allData?.sessions?.firstOrNull { it.id == sessionId } ?: return@post call.respond(HttpStatusCode.NotFound)
            val nowTime = simulatedTime(production)
            val startVotesAt = LocalDateTime.parse(session.startsAt, dateFormat)
            val endVotesAt = LocalDateTime.parse(session.endsAt, dateFormat).plusMinutes(15)
            val votingPeriodStarted = if (startVotesAt != null) {
                ZonedDateTime.of(startVotesAt, keynoteTimeZone).isBefore(nowTime)
            } else true
            val votingPeriodEnded = if (endVotesAt != null) {
                ZonedDateTime.of(endVotesAt, keynoteTimeZone).isBefore(nowTime)
            } else true

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
            val principal = call.validatePrincipal(database) ?: return@delete call.respond(HttpStatusCode.Unauthorized)
            val vote = call.receive<Vote>()
            val sessionId = vote.sessionId ?: return@delete call.respond(HttpStatusCode.BadRequest)
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
fun Routing.apiAll(database: Database, production: Boolean) {
    get("all") {
        val data = sessionizeData ?: return@get call.respond(HttpStatusCode.ServiceUnavailable)
        val principal = call.validatePrincipal(database)
        val responseData = if (principal != null) {
            val votes = database.getVotes(principal.token)
            val favorites = database.getFavorites(principal.token)
            val personalizedData = data.allData.copy(votes = votes, favorites = favorites)
            SessionizeData(personalizedData)
        } else
            data

        call.withETag(responseData.etag, putHeader = true) {
            call.respond(responseData.allData)
        }
    }
}

fun Routing.apiSession(database: Database, production: Boolean) {
    route("sessions") {
        get {
            val data = sessionizeData ?: return@get call.respond(HttpStatusCode.ServiceUnavailable)
            val sessions = data.allData.sessions ?: mutableListOf()
            call.withETag(sessions.hashCode().toString(), putHeader = true) {
                call.respond(sessions)
            }
        }
        get("{sessionId}") {
            val data = sessionizeData ?: return@get call.respond(HttpStatusCode.ServiceUnavailable)
            val id = call.parameters["sessionId"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            val sessions = data.allData.sessions?.singleOrNull { it.id == id } ?: return@get call.respond(HttpStatusCode.NotFound)
            call.withETag(sessions.hashCode().toString(), putHeader = true) {
                call.respond(sessions)
            }
        }
    }
}

// maps sessionId to the "session updated" signal (a signal is just Unit -- it carries no additional data).
val sessionSignals = ConcurrentHashMap<String, ConflatedBroadcastChannel<Unit>>()

fun signalSession(sessionId: String) =
        sessionSignals[sessionId]?.offer(Unit) // offer to anyone who's interested

fun trackSession(sessionId: String): ConflatedBroadcastChannel<Unit> =
        sessionSignals.computeIfAbsent(sessionId) { ConflatedBroadcastChannel(Unit) }

fun Routing.wsVotes(database: Database, production: Boolean) {
    val route = if (production) fakeSessionId else "{sessionId}"
    webSocket("sessions/$route/votes") {
        val id = call.parameters["sessionId"] ?: fakeSessionId
        trackSession(id).openSubscription().consume {
            consumeEach {
                outgoing.send(Frame.Text(gson.toJson(database.getVotesSummary(id))))
            }
        }
    }
}