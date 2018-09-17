package org.jetbrains.kotlinconf.model

import io.ktor.client.call.ReceivePipelineFail
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.JSON
import kotlinx.serialization.list
import kotlinx.serialization.serializer
import org.jetbrains.kotlinconf.SessionModel
import org.jetbrains.kotlinconf.allSessions
import org.jetbrains.kotlinconf.api.*
import org.jetbrains.kotlinconf.data.Favorite
import org.jetbrains.kotlinconf.data.SessionRating
import org.jetbrains.kotlinconf.data.Vote
import org.jetbrains.kotlinconf.data.VotingCode
import org.jetbrains.kotlinconf.favoriteSessions
import org.jetbrains.kotlinconf.presentation.DataRepository
import org.jetbrains.kotlinconf.storage.Settings
import kotlin.properties.Delegates.observable
import kotlin.properties.ReadWriteProperty

class KotlinConfDataRepository(
    private val settings: Settings
) : DataRepository {
    private val api = KotlinConfApi()

    override var sessions: List<SessionModel>? by bindToPreferencesByKey("settingsKey", SessionModel.serializer().list)
    override var favorites: List<SessionModel>? by bindToPreferencesByKey("favoritesKey", SessionModel.serializer().list)
    override var votes: List<Vote>? by bindToPreferencesByKey("votesKey", Vote.serializer().list)
    private var userId: String? by bindToPreferencesByKey("userIdKey", String.serializer())

    override var codePromptShown: Boolean
        get() = settings.getBoolean("codePromptShownKey", false)
        set(value) {
            settings.putBoolean("codePromptShownKey", value)
        }

    override val loggedIn: Boolean
        get() = userId != null

    override var onRefreshListeners: List<() -> Unit> = emptyList()

    override suspend fun update() {
        val state = api.getAll(userId)

        val newSessions = state.allSessions()
        val newFavorites = state.favoriteSessions()
        val newVotes = state.votes
        if (newSessions != sessions || newFavorites != favorites || newVotes != votes) {
            sessions = newSessions
            favorites = newFavorites
            votes = newVotes
            try {
                callRefreshListeners()
            } catch (_: Throwable) {
                throw UpdateProblem()
            }
        }
    }

    override suspend fun verifyCode(code: VotingCode) {
        try {
            api.verifyCode(code)
            userId = code
            update()
        } catch (t: Throwable) {
            val responseStatus = (t.cause as? ApiException)?.response?.status?.value
            if (responseStatus == 406) {
                throw IncorrectCode()
            } else {
                throw FailedToVerifyCode()
            }
        }
    }

    override fun getRating(sessionId: String): SessionRating? =
        votes?.find { sessionId == it.sessionId }
            ?.rating
            ?.let { SessionRating.valueOf(it) }

    override suspend fun addRating(sessionId: String, rating: SessionRating) {
        val userId = userId ?: throw Unauthorized()
        val vote = Vote(sessionId, rating.value)
        try {
            api.postVote(vote, userId)
            votes = votes.orEmpty().plus(vote)
        } catch (pipelineError: ReceivePipelineFail) {
            val apiError = (pipelineError.cause as? ApiException)
            val code = apiError?.response?.status?.value
            throw when (code) {
                477 -> TooEarlyVote()
                478 -> TooLateVote()
                else -> CannotPostVote()
            }
        } catch (e: Throwable) {
            throw CannotPostVote()
        } finally {
            callRefreshListeners()
        }
    }

    override suspend fun removeRating(sessionId: String) {
        val userId = userId ?: throw Unauthorized()
        try {
            api.deleteVote(Vote(sessionId, 0), userId)
            votes = votes?.filter { it.sessionId != sessionId }
        } catch (e: Throwable) {
            throw CannotDeleteVote()
        } finally {
            callRefreshListeners()
        }
    }

    override suspend fun setFavorite(sessionId: String, isFavorite: Boolean) {
        val userId = userId ?: throw Unauthorized()
        val favorite = Favorite(sessionId)
        try {
            favorites = if (isFavorite) {
                api.postFavorite(favorite, userId)
                val favoriteSession = sessions?.firstOrNull { it.id == sessionId }
                    ?: throw Unauthorized()
                favorites.orEmpty().plus(favoriteSession)
            } else {
                api.deleteFavorite(favorite, userId)
                favorites?.filter { it.id != sessionId }
            }
        } catch (_: Throwable) {
            throw CannotFavorite()
        } finally {
            callRefreshListeners()
        }
    }

    private fun callRefreshListeners() {
        onRefreshListeners.forEach { it() }
    }

    /*
     * Local storage
     */

    private inline fun <reified T : Any> read(key: String, elementSerializer: KSerializer<T>) = settings
        .getString(key, "")
        .takeUnless { it.isBlank() }
        ?.let {
            try {
                JSON.parse(elementSerializer, it)
            } catch (_: Throwable) {
                null
            }
        }

    private inline fun <reified T : Any> write(key: String, obj: T?, elementSerializer: KSerializer<T>) {
        settings.putString(key, if (obj == null) "" else JSON.stringify(elementSerializer, obj))
    }

    private inline fun <reified T : Any> bindToPreferencesByKey(
        key: String,
        elementSerializer: KSerializer<T>
    ): ReadWriteProperty<Any?, T?> = observable(read(key, elementSerializer)) { _, _, new ->
        write(key, new, elementSerializer)
    }
}