package org.jetbrains.kotlinconf.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import org.jetbrains.kotlinconf.ConferenceService
import org.jetbrains.kotlinconf.NewsDisplayItem

class NewsListViewModel(
    service: ConferenceService
) : ViewModel() {
    val news: StateFlow<List<NewsDisplayItem>> = service.news
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
