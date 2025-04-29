package org.jetbrains.kotlinconf.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.news_feed_empty
import kotlinconfapp.shared.generated.resources.news_feed_title
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.ScreenWithTitle
import org.jetbrains.kotlinconf.ScrollToTopHandler
import org.jetbrains.kotlinconf.ui.components.MajorError
import org.jetbrains.kotlinconf.ui.components.NewsCard
import org.jetbrains.kotlinconf.utils.bottomInsetPadding
import org.jetbrains.kotlinconf.utils.plus
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NewsListScreen(
    onNewsClick: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: NewsListViewModel = koinViewModel(),
) {
    ScreenWithTitle(
        title = stringResource(Res.string.news_feed_title),
        onBack = onBack,
    ) {
        val news by viewModel.news.collectAsState()

        if (news.isNotEmpty()) {
            val listState = rememberLazyListState()
            ScrollToTopHandler(listState)
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(vertical = 16.dp) + bottomInsetPadding(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                state = listState,
            ) {
                items(news) { newsItem ->
                    NewsCard(
                        title = newsItem.title,
                        date = newsItem.date,
                        photoUrl = newsItem.photoUrl,
                        onClick = { onNewsClick(newsItem.id) }
                    )
                }
            }
        } else {
            Box(Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                MajorError(message = stringResource(Res.string.news_feed_empty))
            }
        }
    }
}
