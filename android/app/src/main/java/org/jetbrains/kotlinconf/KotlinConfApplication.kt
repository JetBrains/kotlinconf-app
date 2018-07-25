package org.jetbrains.kotlinconf

import android.app.Application
import org.jetbrains.kotlinconf.api.KotlinConfApi
import org.jetbrains.kotlinconf.model.KotlinConfDataRepository
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import okhttp3.MediaType
import okhttp3.RequestBody
import org.jetbrains.anko.*
import retrofit2.Retrofit
import ru.gildor.coroutines.retrofit.awaitResult
import java.util.*

class KotlinConfApplication : Application(), AnkoLogger {
    val repository: KotlinConfDataRepository = KotlinConfDataRepository(this)

    override fun onCreate() {
        super.onCreate()
        val userId = getUserId()

        repository.userId = userId
        repository.onError = { action ->
            when (action) {
                KotlinConfDataRepository.Error.FAILED_TO_DELETE_RATING ->
                    toast(R.string.msg_failed_to_delete_vote)

                KotlinConfDataRepository.Error.FAILED_TO_POST_RATING ->
                    toast(R.string.msg_failed_to_post_vote)

                KotlinConfDataRepository.Error.FAILED_TO_GET_DATA ->
                    toast(R.string.msg_failed_to_get_data)

                KotlinConfDataRepository.Error.EARLY_TO_VOTE ->
                    toast(R.string.msg_early_vote)

                KotlinConfDataRepository.Error.LATE_TO_VOTE ->
                    toast(R.string.msg_late_vote)
            }
        }

        launch(UI) {
            val dataLoaded = repository.loadLocalData()
            if (!dataLoaded) {
                repository.update()
            }

            // Get new data from server if new user was created (server db was cleaned)
            if (postUserId(userId) && dataLoaded) {
                repository.update()
            }
        }
    }

    private fun getUserId(): String {
        defaultSharedPreferences.getString(USER_ID_KEY, null)?.let {
            return it
        }

        val userId = "android-" + UUID.randomUUID().toString()
        defaultSharedPreferences
                .edit()
                .putString(USER_ID_KEY, userId)
                .apply()

        return userId
    }

    private suspend fun postUserId(userId: String): Boolean {
        val retrofit = Retrofit.Builder()
                .baseUrl(KotlinConfApi.END_POINT)
                .build()

        val service = retrofit.create(KotlinConfApi::class.java)
        service.postUserId(RequestBody.create(MediaType.parse("text/plain"), userId.toByteArray()))
                .awaitResult()
                .ifSucceeded {
                    info("User successfully created")
                    return true
                }
                .ifError { code ->
                    if (code == 409) {
                        info("User already exists")
                        return false
                    }
                }
                .ifException {
                    warn("Failed to post user id, network problem")
                    return false
                }

        warn("Failed to post user id, unknown error")
        return false
    }

    companion object {
        const val USER_ID_KEY = "UserId"
    }
}