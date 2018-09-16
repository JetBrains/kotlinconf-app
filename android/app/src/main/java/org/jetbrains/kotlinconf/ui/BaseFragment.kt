package org.jetbrains.kotlinconf.ui

import android.support.v4.app.Fragment
import org.jetbrains.anko.toast
import org.jetbrains.kotlinconf.BuildConfig
import org.jetbrains.kotlinconf.R
import org.jetbrains.kotlinconf.model.KotlinConfDataRepository.*
import org.jetbrains.kotlinconf.presentation.BaseView
import java.net.ConnectException

abstract class BaseFragment : Fragment(), BaseView {

    override fun showError(error: Throwable) {
        if(BuildConfig.DEBUG) error.printStackTrace()
        val messageId: Int = when (error) {
            is Unauthorized -> R.string.unauthorized_error
            is CannotFavorite -> R.string.cannot_favorite_error
            is CannotPostVote -> R.string.msg_failed_to_post_vote
            is CannotDeleteVote -> R.string.msg_failed_to_delete_vote
            is UpdateProblem -> R.string.msg_failed_to_get_data
            is TooEarlyVoteError -> R.string.msg_early_vote
            is TooLateVoteError -> R.string.msg_late_vote
            else -> R.string.unknown_error
        }
        context?.toast(messageId)
    }
}