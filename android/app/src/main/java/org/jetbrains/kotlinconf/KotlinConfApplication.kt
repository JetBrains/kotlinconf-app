package org.jetbrains.kotlinconf

import android.app.Application
import org.jetbrains.kotlinconf.api.KotlinConfApi
import org.jetbrains.kotlinconf.model.KotlinConfDataRepositoryImpl
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import okhttp3.MediaType
import okhttp3.RequestBody
import org.jetbrains.anko.*
import retrofit2.Retrofit
import java.util.*

class KotlinConfApplication : Application(), AnkoLogger {
    val repository: KotlinConfDataRepositoryImpl = KotlinConfDataRepositoryImpl(this)

    override fun onCreate() {
        super.onCreate()
        repository.onCreate()
        repository.onError = { action ->
            when (action) {
                KotlinConfDataRepositoryImpl.Error.FAILED_TO_DELETE_RATING ->
                    toast(R.string.msg_failed_to_delete_vote)

                KotlinConfDataRepositoryImpl.Error.FAILED_TO_POST_RATING ->
                    toast(R.string.msg_failed_to_post_vote)

                KotlinConfDataRepositoryImpl.Error.FAILED_TO_GET_DATA ->
                    toast(R.string.msg_failed_to_get_data)

                KotlinConfDataRepositoryImpl.Error.EARLY_TO_VOTE ->
                    toast(R.string.msg_early_vote)

                KotlinConfDataRepositoryImpl.Error.LATE_TO_VOTE ->
                    toast(R.string.msg_late_vote)
            }
        }
    }
}