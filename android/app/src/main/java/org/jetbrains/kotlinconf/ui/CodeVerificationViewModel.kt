package org.jetbrains.kotlinconf.ui

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.kotlinconf.KotlinConfApplication
import org.jetbrains.kotlinconf.data.VotingCode
import org.jetbrains.kotlinconf.model.KotlinConfViewModel

class CodeVerificationViewModel(app: Application) : AndroidViewModel(app), AnkoLogger {
    private val viewModel: KotlinConfViewModel = getApplication<KotlinConfApplication>().viewModel

    suspend fun verifyCode(code: VotingCode) {
        viewModel.verifyCode(code)
    }

    val isCodeVerified: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        addSource(viewModel.votingCode) {
            value = !it.isNullOrBlank()
        }
    }

    fun shouldShowPrompt() = !viewModel.promptShown
    fun setPromptShown() {
        viewModel.promptShown = true
    }
}