package org.jetbrains.kotlinconf.ui

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import org.jetbrains.kotlinconf.KotlinConfApplication
import org.jetbrains.kotlinconf.model.KotlinConfDataRepository

class CodeEnterViewModel(app: Application) : AndroidViewModel(app) {
    private val repository: KotlinConfDataRepository =
            getApplication<KotlinConfApplication>().repository

    private val _codeVerified = MediatorLiveData<Boolean>().apply {
        addSource(repository.codeVerified) {
            showLoading.value = false
        }
    }

    val codeVerified: LiveData<Boolean> = _codeVerified
    val showLoading = MutableLiveData<Boolean>()
    private lateinit var navigationManager: NavigationManager

    suspend fun submitCode(code: String) {
        showLoading.value = true
        repository.submitCode(code)
    }

    fun setNavigationManager(navigationManager: NavigationManager) {
        this.navigationManager = navigationManager
    }

    fun showSessionList() {
        navigationManager.showSessionList()
    }
}