package org.jetbrains.kotlinconf.ui

import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import org.jetbrains.anko.toast
import org.jetbrains.kotlinconf.KotlinConfApplication
import org.jetbrains.kotlinconf.R
import org.jetbrains.kotlinconf.model.KotlinConfDataRepository
import org.jetbrains.kotlinconf.model.KotlinConfDataRepository.CannotVote
import org.jetbrains.kotlinconf.model.KotlinConfDataRepository.Unauthorized
import org.jetbrains.kotlinconf.presentation.BaseView
import java.net.ConnectException

abstract class BaseFragment: Fragment(), BaseView {

    override fun showError(error: Throwable) {
        error.printStackTrace()
        val message = when (error) {
            is Unauthorized -> R.string.unauthorized_error
            is CannotVote -> R.string.cannot_vote_error
            is ConnectException -> { // It means that user is offline or server is down. It means offline mode
                (context?.applicationContext as? KotlinConfApplication)
                        ?.dataRepository
                        ?.onRefreshListeners
                        ?.forEach { it() } // Some services expect changes after action. This will update them to unchanged state
                return
            }
            else -> R.string.unknown_error
        }
        context?.toast(message)
    }
}