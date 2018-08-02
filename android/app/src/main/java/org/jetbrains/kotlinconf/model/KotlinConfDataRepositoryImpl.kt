package org.jetbrains.kotlinconf.model

import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.jetbrains.kotlinconf.presentation.KotlinConfDataRepository
import org.jetbrains.kotlinconf.*
import com.jetbrains.kotlinconf.api.KotlinConfApi
import com.jetbrains.kotlinconf.api.KotlinConfApi.Companion.DATE_FORMAT
import org.jetbrains.kotlinconf.data.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import okhttp3.MediaType
import okhttp3.RequestBody
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.defaultSharedPreferences
import org.jetbrains.anko.info
import org.jetbrains.anko.warn
import retrofit2.Retrofit
import java.io.File
import java.util.*
import kotlin.properties.Delegates.observable

class KotlinConfDataRepositoryImpl(private val context: Context) : AnkoLogger, KotlinConfDataRepository {

    var onError: ((action: Error) -> Unit)? = null
    override var onUpdateListeners: List<()->Unit> = emptyList()

    private val userId: String by lazy { getOrSetUserId() }

    private val gson: Gson by lazy {
        GsonBuilder()
                .setDateFormat(DATE_FORMAT)
                .create()
    }

    private val kotlinConfApi: KotlinConfApi by lazy { KotlinConfApi.create(userId) }

    private val favoritePreferences: SharedPreferences by lazy {
        context.getSharedPreferences(FAVORITES_PREFERENCES_NAME, MODE_PRIVATE)
    }

    private val ratingPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(VOTES_PREFERENCES_NAME, MODE_PRIVATE)
    }

    private var data by observable<AllData?>(null) { _, _, data ->
        sessions = data?.sessions?.mapNotNull { createSessionModel(it) } ?: emptyList()
        val localFavorites = favoritePreferences.getStringSet(FAVORITES_KEY, emptySet())
        favorites = sessions.filter { session -> session.id in localFavorites }
    }
    override var sessions: List<SessionModel> = emptyList()
    override var favorites: List<SessionModel> = emptyList()

    private val _isUpdating = MutableLiveData<Boolean>()
    private var ratings: Map<String, SessionRating> = emptyMap()

    fun onCreate() {
        launch(UI) {
            val dataLoaded = loadLocalData()
            if (!dataLoaded) {
                update()
            }

            // Get new data from server if new user was created (server db was cleaned)
            postUserId(userId, onNewUserPosted = { if (dataLoaded) update() })
        }
    }

    override suspend fun update() {
        if (_isUpdating.value == true) {
            return
        }
        _isUpdating.value = true

        kotlinConfApi.getAll()
                .awaitResult()
                .ifSucceeded { allData ->
                    syncLocalFavorites(allData)
                    syncLocalRatings(allData)
                    updateLocalData(allData)
                    onUpdateListeners.forEach { it() }
                }
                .ifFailed {
                    warn("Failed to get data from server")
                    onError?.invoke(Error.FAILED_TO_GET_DATA)
                }

        _isUpdating.value = false
    }

    override fun getSessionById(id: String): SessionModel = sessions.find { it.id == id }!!

    override fun isFavorite(sessionId: String): Boolean {
        return favorites.any { it.id == sessionId }
    }

    override suspend fun setFavorite(sessionId: String, isFavorite: Boolean) {
        if (isFavorite) {
            addLocalFavorite(sessionId)
            kotlinConfApi.postFavorite(Favorite(sessionId)).awaitResult()
        } else {
            deleteLocalFavorite(sessionId)
            kotlinConfApi.deleteFavorite(Favorite(sessionId)).awaitResult()
        }
    }

    override suspend fun addRating(sessionId: String, rating: SessionRating) {
        ratings = getAllLocalRatings() + (sessionId to rating)

        kotlinConfApi
                .postVote(Vote(sessionId = sessionId, rating = rating.value))
                .awaitResult()
                .ifSucceeded {
                    saveLocalRating(sessionId, rating)
                }
                .ifError { code ->
                    ratings = getAllLocalRatings()
                    when (code) {
                        HTTP_COME_BACK_LATER -> onError?.invoke(Error.EARLY_TO_VOTE)
                        HTTP_TOO_LATE -> onError?.invoke(Error.LATE_TO_VOTE)
                        else -> onError?.invoke(Error.FAILED_TO_POST_RATING)
                    }
                }
                .ifException {
                    ratings = getAllLocalRatings()
                    onError?.invoke(Error.FAILED_TO_POST_RATING)
                }
    }

    override suspend fun removeRating(sessionId: String) {
        ratings = getAllLocalRatings() - sessionId
        kotlinConfApi
                .deleteVote(Vote(sessionId = sessionId))
                .awaitResult()
                .ifSucceeded {
                    deleteLocalRating(sessionId)
                }
                .ifFailed {
                    ratings = getAllLocalRatings()
                    onError?.invoke(Error.FAILED_TO_DELETE_RATING)
                }
    }

    private fun createSessionModel(session: Session) = SessionModel.forSession(session,
            speakerProvider = this::getSpeaker,
            categoryProvider = this::getCategoryItem,
            roomProvider = this::getRoom
    )

    override fun getRating(sessionId: String): SessionRating? = ratings[sessionId]

    private fun getRoom(roomId: Int): Room? = data?.rooms?.find { it.id == roomId }

    private fun getSpeaker(speakerId: String): Speaker? = data?.speakers?.find { it.id == speakerId }

    private fun getCategoryItem(categoryItemId: Int): CategoryItem? {
        return data?.categories
                ?.flatMap { it.items ?: emptyList() }
                ?.find { it?.id == categoryItemId }
    }

    private fun addLocalFavorite(sessionId: String) {
        modifyLocalFavorites { favorites ->
            favorites.add(sessionId)
        }
    }

    private fun deleteLocalFavorite(sessionId: String) {
        modifyLocalFavorites { favorites ->
            favorites.remove(sessionId)
        }
    }

    private fun syncLocalFavorites(allData: AllData) {
        val sessionIds = allData.favorites?.mapNotNull { it.sessionId } ?: return
        modifyLocalFavorites { favorites ->
            val missingOnServer = favorites - sessionIds
            launch(CommonPool) {
                missingOnServer.forEach { kotlinConfApi.postFavorite(Favorite(it)).awaitResult() }
            }

            favorites.addAll(sessionIds)
        }
    }

    private fun modifyLocalFavorites(modifier: (favorites: MutableSet<String>)->Unit) {
        val localFavorites = favoritePreferences.getStringSet(FAVORITES_KEY, setOf()).toMutableSet()
        modifier(localFavorites)
        favoritePreferences
                .edit()
                .putStringSet(FAVORITES_KEY, localFavorites)
                .apply()
        favorites = sessions.filter { session -> localFavorites.contains(session.id) }
    }

    private fun saveLocalRating(sessionId: String, rating: SessionRating) {
        ratingPreferences.edit().putInt(sessionId, rating.value).apply()
        ratings = getAllLocalRatings()
    }

    private fun deleteLocalRating(sessionId: String) {
        ratingPreferences.edit().remove(sessionId).apply()
        ratings = getAllLocalRatings()
    }

    private fun getAllLocalRatings(): Map<String, SessionRating> {
        return ratingPreferences.all
                .mapNotNull { entry -> SessionRating.valueOf(entry.value as Int)?.let { rating -> entry.key to rating } }
                .toMap()
    }

    private fun syncLocalRatings(allData: AllData) {
        val ratings = allData.votes?.mapNotNull {
            val sessionId = it.sessionId
            val rating = it.rating?.let { SessionRating.valueOf(it) }
            if (sessionId != null && rating != null) sessionId to rating else null
        }?.toMap() ?: emptyMap()

        ratingPreferences.edit().apply {
            clear()
            ratings.forEach { (id, rating) -> putInt(id, rating.value) }
        }.apply()

        this.ratings = ratings
    }

    private fun updateLocalData(allData: AllData) {
        File(context.filesDir, CACHED_DATA_FILE_NAME).apply {
            delete()
            createNewFile()
            writeText(gson.toJson(allData))
        }
        data = allData
    }

    private fun loadLocalData(): Boolean {
        val allDataFile = File(context.filesDir, CACHED_DATA_FILE_NAME)
        if (!allDataFile.exists()) {
            return false
        }

        data = gson.fromJson<AllData>(allDataFile.readText(), AllData::class.java) ?: return false

        val favorites = favoritePreferences.getStringSet(FAVORITES_KEY, mutableSetOf())
        this.favorites = sessions.filter { session -> favorites.contains(session.id) }

        ratings = ratingPreferences.all
                .mapNotNull { SessionRating.valueOf(it.value as Int)?.let { rating -> it.key to rating } }
                .toMap()

        return true
    }

    private fun getOrSetUserId(): String {
        context.defaultSharedPreferences.getString(USER_ID_KEY, null)?.let {
            return it
        }

        val userId = "android-${UUID.randomUUID()}"
        context.defaultSharedPreferences
                .edit()
                .putString(USER_ID_KEY, userId)
                .apply()

        return userId
    }

    private suspend fun postUserId(userId: String, onNewUserPosted: suspend () -> Unit) {
        val retrofit = Retrofit.Builder()
                .baseUrl(KotlinConfApi.END_POINT)
                .build()

        val service = retrofit.create(KotlinConfApi::class.java)
        service.postUserId(RequestBody.create(MediaType.parse("text/plain"), userId.toByteArray()))
                .awaitResult()
                .ifSucceeded {
                    info("User successfully created")
                    onNewUserPosted()
                }
                .ifError { code ->
                    if (code == 409) {
                        info("User already exists")
                    } else {
                        warn("Failed to post user id, unknown error with code $code")
                    }
                }
                .ifException {
                    warn("Failed to post user id, network problem")
                }
    }

    companion object {
        const val FAVORITES_PREFERENCES_NAME = "favorites"
        const val VOTES_PREFERENCES_NAME = "votes"
        const val FAVORITES_KEY = "favorites"
        const val CACHED_DATA_FILE_NAME = "data.json"
        const val USER_ID_KEY = "UserId"

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