package org.jetbrains.kotlinconf.model

import com.russhwolf.settings.PlatformSettings
import com.russhwolf.settings.set
import io.ktor.util.date.GMTDate
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialContext
import kotlinx.serialization.json.JSON
import kotlinx.serialization.list
import kotlinx.serialization.serializer
import org.jetbrains.kotlinconf.*
import org.jetbrains.kotlinconf.api.*
import org.jetbrains.kotlinconf.data.*
import org.jetbrains.kotlinconf.presentation.*
import kotlin.properties.Delegates.observable
import kotlin.properties.ReadWriteProperty
import kotlin.random.Random

class KotlinConfDataRepository(
        settingsFactory: PlatformSettings.Factory,
        val onError: (Throwable) -> Unit
) : DataRepository {
    private val settings = settingsFactory.create("")
    private val api = KotlinConfApi()

    private val jsonContext = SerialContext()
            .apply { registerSerializer(GMTDate::class, GMTDateSerializer) }
    private val serializer = JSON(context = jsonContext)

    override var sessions: List<SessionModel>? by bindToPreferencesByKey("settingsKey", SessionModel::class.serializer().list)
    override var favorites: List<SessionModel>? by bindToPreferencesByKey("favoritesKey", SessionModel::class.serializer().list)
    override var votes: List<Vote>? by bindToPreferencesByKey("votesKey", Vote::class.serializer().list)
    var userId: String? by bindToPreferencesByKey("userIdKey", String.serializer())

    override val loggedIn: Boolean
        get() = userId != null

    override var onRefreshListeners: List<() -> Unit> = emptyList()

    override suspend fun update() = tryOrError {
        if (!loggedIn) {
            val id = (1..10)
                    .map { Random.nextInt(10) }
                    .fold("") { acc, i -> acc + i }
            api.createUser(id)
            userId = id
        }

        val state = api.getAll(userId)

        val newSessions = state.allSessions()
        val newFavorites = state.favoriteSessions()
        val newVotes = state.votes
        if (newSessions != sessions || newFavorites != favorites || newVotes != votes) {
            sessions = newSessions
            favorites = newFavorites
            votes = newVotes
            callRefreshListeners()
        }
    }

    override fun getRating(sessionId: String): SessionRating =
            SessionRating.valueOf(votes?.find { sessionId == it.sessionId }?.rating ?: 0)

    override suspend fun addRating(sessionId: String, rating: SessionRating) = tryOrError {
        val userId = userId ?: throw Unauthorized()
        val vote = Vote(sessionId, rating.value)
        api.postVote(vote, userId)
        votes = votes.orEmpty().plus(vote)
        callRefreshListeners()
    }

    override suspend fun removeRating(sessionId: String) = tryOrError {
        val userId = userId ?: throw Unauthorized()
        api.postVote(Vote(sessionId, 0), userId)
        votes = votes?.filter { it.sessionId != sessionId }
        callRefreshListeners()
    }

    override suspend fun setFavorite(sessionId: String, isFavorite: Boolean) = tryOrError {
        val userId = userId ?: throw Unauthorized()
        val favorite = Favorite(sessionId)
        favorites = if (isFavorite) {
            api.postFavorite(favorite, userId)
            val favoriteSession = sessions?.firstOrNull { it.id == sessionId } ?: throw Unauthorized()
            favorites.orEmpty().plus(favoriteSession)
        } else {
            api.deleteFavorite(favorite, userId)
            favorites?.filter { it.id != sessionId }
        }
        callRefreshListeners()
    }

    private fun callRefreshListeners() {
        onRefreshListeners.forEach { it() }
    }

    /**
     * Native callback API
     */
    override fun update(callback: NativeCallback<Unit>) {
        wrapCallback(callback) {
            update()
        }
    }

    override fun addRating(sessionId: String, rating: SessionRating, callback: NativeCallback<Unit>) {
        wrapCallback(callback) {
            addRating(sessionId, rating)
        }
    }

    override fun removeRating(sessionId: String, callback: NativeCallback<Unit>) {
        wrapCallback(callback) {
            removeRating(sessionId)
        }
    }

    override fun setFavorite(sessionId: String, isFavorite: Boolean, callback: NativeCallback<Unit>) {
        wrapCallback(callback) {
            setFavorite(sessionId, isFavorite)
        }
    }

    private suspend fun tryOrError(f: suspend () -> Unit) {
        try {
            f()
        } catch (t: Throwable) {
            onError(t)
        }
    }

    enum class Error {
        FAILED_TO_DELETE_RATING,
        FAILED_TO_POST_RATING,
        FAILED_TO_GET_DATA,
        EARLY_TO_VOTE,
        LATE_TO_VOTE,
        UNKNOWN,
    }

    class Unauthorized : Throwable()

    /*
     * Local storage
     */

    private inline fun <reified T : Any> read(key: String, elementSerializer: KSerializer<T>) = settings.getString(key, "")
            .takeUnless { it.isBlank() }
            ?.let { serializer.parse(elementSerializer, it) }

    private inline fun <reified T : Any> write(key: String, obj: T, elementSerializer: KSerializer<T>) {
        settings[key] = serializer.stringify(elementSerializer, obj)
    }

    private inline fun <reified T : Any> bindToPreferencesByKey(
            key: String,
            elementSerializer: KSerializer<T>
    ): ReadWriteProperty<Any?, T?> = observable(read(key, elementSerializer)) { _, _, new ->
        if (new != null) write(key, new, elementSerializer) else settings.remove(key)
    }
}