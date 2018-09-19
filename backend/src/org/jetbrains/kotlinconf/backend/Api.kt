@file:Suppress("NestedLambdaShadowedImplicitParameter")

package org.jetbrains.kotlinconf.backend

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.Frame
import io.ktor.pipeline.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.*
import kotlinx.serialization.json.*
import org.jetbrains.kotlinconf.data.*
import java.time.*
import java.time.format.*
import java.util.*
import java.util.concurrent.*
import kotlin.streams.*

fun Routing.api(database: Database, production: Boolean, sessionizeUrl: String) {
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
fun Routing.apiKeynote(production: Boolean) {
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
POST http://localhost:8080/users
201 or 409

GET http://localhost:8080/users/verify/{token}
200 or 406

GET http://localhost:8080/users/count
1234

GET http://localhost:8080/users/generate/5?secret=TestingSecret
["bjdaf","ilHLs","6Knmk","LA94g","R6WYs"]

DELETE http://localhost:8080/users/bjdaf?secret=TestingSecret
 */
fun Routing.apiUsers(database: Database) {
    route("users") {
        get("verify/{code}") {
            val code = call.parameters["code"] ?: throw BadRequest()
            val responseCode = if (database.validateUser(code)) HttpStatusCode.OK else HttpStatusCode.NotAcceptable
            call.respond(responseCode)
        }
        get("count") {
            call.respondText(database.usersCount().toString())
        }
        get("generate/{number}") {
            requireSecret()
            val codeLength = 5L
            val number = call.parameters["number"]?.toIntOrNull() ?: throw BadRequest()
            val timestamp = LocalDateTime.now(Clock.systemUTC())

            var createdCodes = listOf<String>()
            while (createdCodes.size < number) {
                val missingCodesNum = number - createdCodes.size
                val codes = generateSequence { randomString(codeLength) }
                    .distinct()
                    .take(missingCodesNum)
                    .toList()

                createdCodes += codes.filter { code -> database.createUser(code, "generate", timestamp) }
            }

            call.respond(UserTokens(createdCodes))
        }
        delete("{code}") {
            requireSecret()
            val code = call.parameters["code"] ?: throw BadRequest()
            database.deleteUser(code)
            call.respond(HttpStatusCode.OK)
        }
    }
}

private fun PipelineContext<*, ApplicationCall>.requireSecret() {
    val providedSecret = call.parameters["secret"]
    val realSecret = System.getenv("SERVER_SECRET").takeUnless { it.isNullOrBlank() } ?: "TestingSecret"
    if (providedSecret != realSecret) throw SecretInvalidError()
}

private val random = Random()

private fun randomString(length: Long): String {
    val source = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    return random.ints(length, 0, source.size)
        .asSequence()
        .map(source::get)
        .joinToString("")
}

suspend fun ApplicationCall.validatePrincipal(database: Database): KotlinConfPrincipal {
    return getPrincipalOrNull(database) ?: throw Unauthorized()
}

suspend fun ApplicationCall.getPrincipalOrNull(database: Database): KotlinConfPrincipal? {
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
            val principal = call.validatePrincipal(database)
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
            val principal = call.validatePrincipal(database)
            val favorite = call.receive<Favorite>()
            val sessionId = favorite.sessionId
            database.createFavorite(principal.token, sessionId)
            call.respond(HttpStatusCode.Created)
        }
        delete {
            val principal = call.validatePrincipal(database)
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
fun Routing.apiVote(database: Database, production: Boolean) {
    route("votes") {
        get {
            val principal = call.validatePrincipal(database)
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
            val principal = call.validatePrincipal(database)
            val vote = call.receive<Vote>()
            val sessionId = vote.sessionId ?: throw BadRequest()
            val rating = vote.rating ?: throw BadRequest()

            val session = getSessionizeData().allData.sessions.firstOrNull { it.id == sessionId } ?: throw NotFound()
            val nowTime = simulatedTime(production)
            val startVotesAt = LocalDateTime.parse(session.startsAt, dateFormat)
            val endVotesAt = LocalDateTime.parse(session.endsAt, dateFormat).plusMinutes(15)
            val votingPeriodStarted = startVotesAt?.let { ZonedDateTime.of(it, keynoteTimeZone).isBefore(nowTime) } ?: true
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
            val principal = call.validatePrincipal(database)
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
fun Routing.apiAll(database: Database) {
    get("all") {
        val data = getSessionizeData()
        val responseData = try {
            val principal = call.validatePrincipal(database)
            val votes = database.getVotes(principal.token)
            val favorites = database.getFavorites(principal.token)
            val personalizedData = data.allData.copy(votes = votes, favorites = favorites)
            SessionizeData(personalizedData)
        } catch (e: Unauthorized) {
            data
        }

        call.withETag(responseData.etag, putHeader = true) {
            call.respond(responseData.allData)
        }
    }
}

fun Routing.apiSession() {
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
                outgoing.send(Frame.Text(JSON.stringify(database.getVotesSummary(id))))
            }
        }
    }
}

/*
GET http://localhost:8080/sessionizeSync
*/
fun Routing.apiSynchronize(sessionizeUrl: String) {
    get("sessionizeSync") {
        synchronizeWithSessionize(sessionizeUrl)
        call.respond(HttpStatusCode.OK)
    }
}