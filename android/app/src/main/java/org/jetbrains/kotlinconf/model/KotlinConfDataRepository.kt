package org.jetbrains.kotlinconf.model

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.jetbrains.kotlinconf.*
import org.jetbrains.kotlinconf.api.KotlinConfApi
import org.jetbrains.kotlinconf.api.KotlinConfApi.Companion.DATE_FORMAT
import org.jetbrains.kotlinconf.data.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.warn
import ru.gildor.coroutines.retrofit.awaitResult
import java.io.File

class KotlinConfDataRepository(private val context: Context) : AnkoLogger {

    lateinit var userId: String
    var onError: ((action: Error) -> Unit)? = null

    private val gson: Gson by lazy {
        GsonBuilder()
            .setDateFormat(DATE_FORMAT)
            .create()
    }

    private val kotlinConfApi: KotlinConfApi by lazy {
        KotlinConfApi.create(userId)
    }

    private val favoritePreferences: SharedPreferences by lazy {
        context.getSharedPreferences(FAVORITES_PREFERENCES_NAME, MODE_PRIVATE)
    }

    private val ratingPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(VOTES_PREFERENCES_NAME, MODE_PRIVATE)
    }

    private val _data: MutableLiveData<AllData> = MutableLiveData()

    private val _isUpdating = MutableLiveData<Boolean>()
    val isUpdating: LiveData<Boolean> = _isUpdating

    val sessions: LiveData<List<SessionModel>> = map(_data) { data ->
        data?.sessions?.mapNotNull {
            createSessionModel(it)
        } ?: emptyList()
    }

    private val _favorites = MediatorLiveData<List<SessionModel>>().apply {
        addSource(sessions) { sessions ->
            val favorites = favoritePreferences.getStringSet(FAVORITES_KEY, emptySet())
            value = sessions?.filter { session -> favorites.contains(session.id) }
        }
    }
    val favorites: LiveData<List<SessionModel>> = _favorites

    private val _ratings: MutableLiveData<Map<String, SessionRating>> = MutableLiveData()
    val ratings: LiveData<Map<String, SessionRating>> = _ratings

    private fun createSessionModel(session: Session): SessionModel? {
        return SessionModel.forSession(session,
                speakerProvider = this::getSpeaker,
                categoryProvider = this::getCategoryItem,
                roomProvider = this::getRoom
        )
    }

    private fun getRoom(roomId: Int): Room? = _data.value?.rooms?.find { it.id == roomId }

    private fun getSpeaker(speakerId: String): Speaker? = _data.value?.speakers?.find { it.id == speakerId }

    private fun getCategoryItem(categoryItemId: Int): CategoryItem? {
        return _data.value?.categories
                ?.flatMap { it.items ?: emptyList() }
                ?.find { it?.id == categoryItemId }
    }

    private fun addLocalFavorite(sessionId: String) {
        val favorites = favoritePreferences.getStringSet(FAVORITES_KEY, setOf()).toMutableSet()
        favorites.add(sessionId)
        favoritePreferences
                .edit()
                .putStringSet(FAVORITES_KEY, favorites)
                .apply()

        _favorites.value = sessions.value?.filter { session -> favorites.contains(session.id) }
    }

    private fun deleteLocalFavorite(sessionId: String) {
        val favorites = favoritePreferences.getStringSet(FAVORITES_KEY, setOf()).toMutableSet()
        favorites.remove(sessionId)
        favoritePreferences
                .edit()
                .putStringSet(FAVORITES_KEY, favorites)
                .apply()

        _favorites.value = sessions.value?.filter { session -> favorites.contains(session.id) }
    }

    suspend fun setFavorite(sessionId: String, isFavorite: Boolean) {
        if (isFavorite) {
            addLocalFavorite(sessionId)
            kotlinConfApi.postFavorite(Favorite(sessionId)).awaitResult()
        }
        else {
            deleteLocalFavorite(sessionId)
            kotlinConfApi.deleteFavorite(Favorite(sessionId)).awaitResult()
        }
    }

    private fun getAllLocalRatings(): Map<String, SessionRating> {
        return ratingPreferences.all.mapNotNull { entry ->
            SessionRating.valueOf(entry.value as Int)?.let { rating -> entry.key to rating }
        }.toMap()
    }

    private fun saveLocalRating(sessionId: String, rating: SessionRating) {
        ratingPreferences.edit().putInt(sessionId, rating.value).apply()
        _ratings.value = getAllLocalRatings()
    }

    private fun deleteLocalRating(sessionId: String) {
        ratingPreferences.edit().remove(sessionId).apply()
        _ratings.value = getAllLocalRatings()
    }

    suspend fun addRating(sessionId: String, rating: SessionRating) {
        _ratings.value = getAllLocalRatings() + (sessionId to rating)

        kotlinConfApi
                .postVote(Vote(sessionId = sessionId, rating = rating.value))
                .awaitResult()
                .ifSucceeded {
                    saveLocalRating(sessionId, rating)
                }
                .ifError { code ->
                    _ratings.value = getAllLocalRatings()
                    when (code) {
                        HTTP_COME_BACK_LATER -> onError?.invoke(Error.EARLY_TO_VOTE)
                        HTTP_TOO_LATE -> onError?.invoke(Error.LATE_TO_VOTE)
                        else -> onError?.invoke(Error.FAILED_TO_POST_RATING)
                    }
                }
                .ifException {
                    _ratings.value = getAllLocalRatings()
                    onError?.invoke(Error.FAILED_TO_POST_RATING)
                }
    }

    suspend fun removeRating(sessionId: String) {
        _ratings.value = getAllLocalRatings() - sessionId
        kotlinConfApi
                .deleteVote(Vote(sessionId = sessionId))
                .awaitResult()
                .ifSucceeded {
                    deleteLocalRating(sessionId)
                }
                .ifFailed {
                    _ratings.value = getAllLocalRatings()
                    onError?.invoke(Error.FAILED_TO_DELETE_RATING)
                }
    }

    private fun syncLocalFavorites(allData: AllData) {
        val sessionIds = allData.favorites?.map { it.sessionId } ?: return
        val favorites = favoritePreferences
                .getStringSet(FAVORITES_KEY, mutableSetOf()).toMutableSet()

        val missingOnServer = favorites - sessionIds
        launch(CommonPool) {
            missingOnServer.forEach { kotlinConfApi.postFavorite(Favorite(it)).awaitResult() }
        }

        favorites.addAll(sessionIds)
        favoritePreferences
                .edit()
                .putStringSet(FAVORITES_KEY, favorites)
                .apply()

        _favorites.value = sessions.value?.filter { session -> favorites.contains(session.id) }
    }

    private fun syncLocalRatings(allData: AllData) {
        val ratings = allData.votes?.mapNotNull {
            val sessionId = it.sessionId
            val rating = it.rating?.let { SessionRating.valueOf(it) }
            if (sessionId != null && rating != null) sessionId to rating else null
        }?.toMap()

        ratingPreferences.edit().apply {
            clear()
            ratings?.forEach { putInt(it.key, it.value.value) }
        }.apply()

        _ratings.value = ratings
    }

    private fun updateLocalData(allData: AllData) {
        val allDataFile = File(context.filesDir, CACHED_DATA_FILE_NAME)
        allDataFile.delete()
        allDataFile.createNewFile()
        allDataFile.writeText(gson.toJson(allData))
        _data.value = allData
    }

    fun loadLocalData(): Boolean {
        val allDataFile = File(context.filesDir, CACHED_DATA_FILE_NAME)
        if (!allDataFile.exists()) {
            return false
        }

        val allData = gson.fromJson<AllData>(allDataFile.readText(),
                AllData::class.java) ?: return false

        _data.value = allData

        val favorites = favoritePreferences.getStringSet(FAVORITES_KEY, mutableSetOf())
        _favorites.value = sessions.value?.filter { session -> favorites.contains(session.id) }

        _ratings.value = ratingPreferences.all.mapNotNull {
            SessionRating.valueOf(it.value as Int)?.let { rating -> it.key to rating }
        }.toMap()

        return true
    }

    suspend fun update() {
        if (_isUpdating.value == true) {
            return
        }

        _isUpdating.value = true

        kotlinConfApi
                .getAll()
                .awaitResult()
                .ifSucceeded { allData ->
                    syncLocalFavorites(allData)
                    syncLocalRatings(allData)
                    updateLocalData(allData)
                }
                .ifFailed {
                    warn("Failed to get data from server")
                    onError?.invoke(Error.FAILED_TO_GET_DATA)
                }

        _isUpdating.value = false
    }

    companion object {
        const val FAVORITES_PREFERENCES_NAME = "favorites"
        const val VOTES_PREFERENCES_NAME = "votes"
        const val FAVORITES_KEY = "favorites"
        const val CACHED_DATA_FILE_NAME = "data.json"

        const val HTTP_COME_BACK_LATER = 477
        const val HTTP_TOO_LATE = 478
    }

    enum class Error {
        FAILED_TO_POST_RATING,
        FAILED_TO_DELETE_RATING,
        FAILED_TO_GET_DATA,
        EARLY_TO_VOTE,
        LATE_TO_VOTE
    }
}