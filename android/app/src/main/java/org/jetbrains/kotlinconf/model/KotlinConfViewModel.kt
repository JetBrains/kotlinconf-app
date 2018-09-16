package org.jetbrains.kotlinconf.model

import android.arch.lifecycle.*
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.ktor.client.call.typeInfo
import kotlinx.coroutines.CommonPool
import kotlinx.coroutines.android.UI
import kotlinx.coroutines.launch
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.warn
import org.jetbrains.kotlinconf.SessionModel
import org.jetbrains.kotlinconf.api.ApiException
import org.jetbrains.kotlinconf.data.SessionRating
import org.jetbrains.kotlinconf.data.VotingCode
import java.io.File

class KotlinConfViewModel(
    private val context: Context,
    userId: String,
    private val onError: (Error) -> Toast
) : AnkoLogger {
    private val gson: Gson by lazy { GsonBuilder().create() } //().setDateFormat(DATE_FORMAT).create() }
    private val data = KonfAppDataModel(userId)

    private val favoritePreferences: SharedPreferences by lazy {
        context.getSharedPreferences(FAVORITES_PREFERENCES_NAME, MODE_PRIVATE)
    }

    private val ratingPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(VOTES_PREFERENCES_NAME, MODE_PRIVATE)
    }

    private val votingCodePreferences: SharedPreferences by lazy {
        context.getSharedPreferences(CODE_PREFERENCES_NAME, MODE_PRIVATE)
    }

    private val _votingCode: MutableLiveData<String> = MutableLiveData()
    val votingCode: LiveData<VotingCode> = _votingCode

    private val promptPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(PROMPT_PREFERENCES_NAME, MODE_PRIVATE)
    }

    private val _ratings: MutableLiveData<Map<String, SessionRating>> = MutableLiveData()
    val ratings: LiveData<Map<String, SessionRating>> = _ratings

    private val _isUpdating = MutableLiveData<Boolean>()
    val isUpdating: LiveData<Boolean> = _isUpdating

    private val _sessions: MutableLiveData<List<SessionModel>> = MutableLiveData()
    val sessions: LiveData<List<SessionModel>> = _sessions

    private val _favorites = MediatorLiveData<List<SessionModel>>().apply {
        addSource(sessions) { sessions ->
            val favorites = favoritePreferences.getStringSet(FAVORITES_KEY, emptySet())!!
            value = sessions?.filter { session -> favorites.contains(session.id) }
        }
    }
    val favorites: LiveData<List<SessionModel>> = _favorites

    init {
        loadLocalData()
        launch(UI) {
            update()
        }
    }

    suspend fun setFavorite(sessionId: String, isFavorite: Boolean) {
        setLocalFavorite(sessionId, isFavorite)
        data.setFavorite(sessionId, isFavorite)
    }

    suspend fun addRating(sessionId: String, rating: SessionRating) {
        _ratings.value = getAllLocalRatings() + (sessionId to rating)

        try {
            data.addRating(sessionId, rating)
            saveLocalRating(sessionId, rating)
        } catch (cause: ApiException) {
            _ratings.value = getAllLocalRatings()
            val code = cause.response.status
            val error = when (code.value) {
                HTTP_COME_BACK_LATER -> Error.EARLY_TO_VOTE
                HTTP_TOO_LATE -> Error.LATE_TO_VOTE
                else -> Error.FAILED_TO_POST_RATING
            }

            onError(error)
        } catch (cause: Throwable) {
            _ratings.value = getAllLocalRatings()
            onError(Error.FAILED_TO_POST_RATING)
        }
    }

    suspend fun removeRating(sessionId: String) {
        _ratings.value = getAllLocalRatings() - sessionId
        try {
            data.removeRating(sessionId)
            deleteLocalRating(sessionId)
        } catch (cause: Throwable) {
            _ratings.value = getAllLocalRatings()
            onError(Error.FAILED_TO_DELETE_RATING)
        }
    }

    suspend fun update() {
        if (_isUpdating.value == true) return
        _isUpdating.value = true

        try {
            data.update()

            _sessions.value = data.sessions

            syncLocalFavorites()
            syncLocalRatings()
            updateLocalData()
        } catch (cause: Throwable) {
            warn("Failed to get data from server")
            println(cause)
            cause.printStackTrace()
            onError.invoke(Error.FAILED_TO_GET_DATA)
        }

        _isUpdating.value = false
    }

    suspend fun verifyCode(code: VotingCode) {
        try {
            data.verifyCode(code)
            saveVotingCode(code)
        } catch (cause: ApiException) {
            cause.printStackTrace()

            val statusCode = cause.response.status
            val error = when (statusCode.value) {
                HTTP_NOT_ACCEPTABLE -> Error.CODE_INCORRECT
                else -> Error.FAILED_TO_VERIFY_CODE
            }
            onError(error)
        } catch (cause: Throwable) {
            cause.printStackTrace()
            onError(Error.FAILED_TO_VERIFY_CODE)
        }
    }

    var promptShown: Boolean
        get() = promptPreferences.getBoolean(PROMPT_PREFERENCES_KEY, false)
        set(value) = promptPreferences.edit()
            .putBoolean(PROMPT_PREFERENCES_KEY, value)
            .apply()

    private fun loadLocalData() {
        val sessionsFile = File(context.filesDir, CACHED_DATA_FILE_NAME)
        if (!sessionsFile.exists()) return

        _votingCode.value = votingCodePreferences.getString(CODE_VERIFIED_KEY, null)

        val type = typeInfo<List<SessionModel>>().reifiedType
        val localSessions = gson.fromJson<List<SessionModel>>(sessionsFile.readText(), type) ?: return
        _sessions.value = localSessions

        val favorites = favoritePreferences.getStringSet(FAVORITES_KEY, mutableSetOf())
        _favorites.value = sessions.value?.filter { session -> favorites.contains(session.id) }

        _ratings.value = ratingPreferences.all.mapNotNull {
            SessionRating.valueOf(it.value as Int).let { rating -> it.key to rating }
        }.toMap()
    }

    private fun setLocalFavorite(sessionId: String, isFavorite: Boolean) {
        val favorites = favoritePreferences.getStringSet(FAVORITES_KEY, setOf()).toMutableSet()
        if (isFavorite) favorites.add(sessionId) else favorites.remove(sessionId)
        favoritePreferences
            .edit()
            .putStringSet(FAVORITES_KEY, favorites)
            .apply()

        _favorites.value = sessions.value?.filter { session -> favorites.contains(session.id) }
    }

    private fun getAllLocalRatings(): Map<String, SessionRating> = ratingPreferences.all.mapNotNull { entry ->
        entry.key to SessionRating.valueOf(entry.value as Int)
    }.toMap()

    private fun saveLocalRating(sessionId: String, rating: SessionRating) {
        ratingPreferences.edit().putInt(sessionId, rating.value).apply()
        _ratings.value = getAllLocalRatings()
    }

    private fun deleteLocalRating(sessionId: String) {
        ratingPreferences.edit().remove(sessionId).apply()
        _ratings.value = getAllLocalRatings()
    }

    private fun syncLocalFavorites() {
        val sessionIds = data.favorites.map { it.id }
        val favorites = favoritePreferences
            .getStringSet(FAVORITES_KEY, mutableSetOf())!!
            .toMutableSet()

        val missingOnServer = favorites - sessionIds
        launch(CommonPool) {
            missingOnServer.forEach { data.setFavorite(it, true) }
        }

        favorites.addAll(sessionIds)
        favoritePreferences
            .edit()
            .putStringSet(FAVORITES_KEY, favorites)
            .apply()

        _favorites.value = sessions.value?.filter { session -> favorites.contains(session.id) }
    }

    private fun syncLocalRatings() {
        val ratings = data.votes.map {
            val sessionId = it.sessionId
            val rating = SessionRating.valueOf(it.rating)
            sessionId to rating
        }.toMap()

        ratingPreferences.edit().apply {
            clear()
            ratings.forEach { putInt(it.key, it.value.value) }
        }.apply()

        _ratings.value = ratings
    }

    private fun saveVotingCode(code: String) {
        votingCodePreferences.edit()
            .putString(CODE_VERIFIED_KEY, code)
            .apply()
        _votingCode.value = code
    }

    private fun updateLocalData() {
        val sessionsFile = File(context.filesDir, CACHED_DATA_FILE_NAME)
        sessionsFile.delete()
        sessionsFile.createNewFile()
        sessionsFile.writeText(gson.toJson(sessions.value))
    }

    companion object {
        const val FAVORITES_PREFERENCES_NAME = "favorites"
        const val VOTES_PREFERENCES_NAME = "votes"
        const val FAVORITES_KEY = "favorites"
        const val CACHED_DATA_FILE_NAME = "data.json"
        const val CODE_PREFERENCES_NAME = "code"
        const val CODE_VERIFIED_KEY = "code_verified"
        const val PROMPT_PREFERENCES_NAME = "prompt"
        const val PROMPT_PREFERENCES_KEY = "prompt_key"

        const val HTTP_COME_BACK_LATER = 477
        const val HTTP_TOO_LATE = 478
        const val HTTP_NOT_ACCEPTABLE = 406
    }

    enum class Error {
        FAILED_TO_POST_RATING,
        FAILED_TO_DELETE_RATING,
        FAILED_TO_GET_DATA,
        EARLY_TO_VOTE,
        LATE_TO_VOTE,
        FAILED_TO_VERIFY_CODE,
        CODE_INCORRECT
    }
}
