package org.jetbrains.kotlinconf.ui

import android.support.v4.app.*
import org.jetbrains.anko.*
import org.jetbrains.kotlinconf.*
import org.jetbrains.kotlinconf.api.*
import org.jetbrains.kotlinconf.presentation.*

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
    if (BuildConfig.DEBUG) error.printStackTrace()
    val messageId: Int = when (error) {
        is Unauthorized -> R.string.unauthorized_error
        is CannotFavorite -> R.string.cannot_favorite_error
        is CannotPostVote -> R.string.msg_failed_to_post_rate
        is CannotDeleteVote -> R.string.msg_failed_to_delete_rate
        is UpdateProblem -> R.string.msg_failed_to_get_data
        is TooEarlyVote -> R.string.msg_early_rate
        is TooLateVote -> R.string.msg_late_rate
        else -> R.string.unknown_error
    }
    context?.toast(messageId)
}