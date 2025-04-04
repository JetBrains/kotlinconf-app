package org.jetbrains.kotlinconf.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import org.jetbrains.kotlinconf.ConferenceService
import org.jetbrains.kotlinconf.NewsDisplayItem

class NewsDetailViewModel(
    service: ConferenceService,
    newsId: String,
) : ViewModel() {
    val newsItem: StateFlow<NewsDisplayItem?> = service.newsById(newsId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}
