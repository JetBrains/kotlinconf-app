package org.jetbrains.kotlinconf.ui

import android.app.*
import android.arch.lifecycle.*
import org.jetbrains.anko.*
import org.jetbrains.kotlinconf.*
import org.jetbrains.kotlinconf.data.*
import org.jetbrains.kotlinconf.model.*

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

    fun shouldShowPrompt(): Boolean = !viewModel.promptShown
    fun setPromptShown() {
        viewModel.promptShown = true
    }
}