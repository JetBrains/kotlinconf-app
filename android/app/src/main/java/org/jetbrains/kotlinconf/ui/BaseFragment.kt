package org.jetbrains.kotlinconf.ui

import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import org.jetbrains.anko.toast
import org.jetbrains.kotlinconf.BuildConfig
import org.jetbrains.kotlinconf.R
import org.jetbrains.kotlinconf.api.*
import org.jetbrains.kotlinconf.presentation.BaseView

abstract class BaseFragment : Fragment(), BaseView {
    override fun showError(error: Throwable) {
        commonShowError(error)
    }
}

abstract class BaseDialogFragment : DialogFragment(), BaseView {
    override fun showError(error: Throwable) {
        commonShowError(error)
    }
}

private fun Fragment.commonShowError(error: Throwable) {
    if(BuildConfig.DEBUG) error.printStackTrace()
    val messageId: Int = when (error) {
        is Unauthorized -> R.string.unauthorized_error
        is CannotFavorite -> R.string.cannot_favorite_error
        is CannotPostVote -> R.string.msg_failed_to_post_vote
        is CannotDeleteVote -> R.string.msg_failed_to_delete_vote
        is UpdateProblem -> R.string.msg_failed_to_get_data
        is TooEarlyVote -> R.string.msg_early_vote
        is TooLateVote -> R.string.msg_late_vote
        is FailedToVerifyCode -> R.string.code_verification_failed
        is IncorrectCode -> R.string.code_incorrect
        else -> R.string.unknown_error
    }
    context?.toast(messageId)
}