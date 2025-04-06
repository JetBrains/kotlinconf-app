package org.jetbrains.kotlinconf.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.arrow_left_24
import kotlinconfapp.shared.generated.resources.navigate_back
import kotlinconfapp.shared.generated.resources.news_feed_title
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.ui.components.Divider
import org.jetbrains.kotlinconf.ui.components.MainHeaderTitleBar
import org.jetbrains.kotlinconf.ui.components.MarkdownView
import org.jetbrains.kotlinconf.ui.components.NetworkImage
import org.jetbrains.kotlinconf.ui.components.Text
import org.jetbrains.kotlinconf.ui.components.TopMenuButton
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.utils.topInsetPadding
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun NewsDetailScreen(
    newsId: String,
    onBack: () -> Unit,
    viewModel: NewsDetailViewModel = koinViewModel { parametersOf(newsId) }
) {
    val state = viewModel.newsItem.collectAsState().value

    Column(
        Modifier
            .fillMaxSize()
            .background(color = KotlinConfTheme.colors.mainBackground)
            .padding(topInsetPadding())
    ) {
        MainHeaderTitleBar(
            title = state?.date ?: stringResource(Res.string.news_feed_title),
            startContent = {
                TopMenuButton(
                    icon = Res.drawable.arrow_left_24,
                    contentDescription = stringResource(Res.string.navigate_back),
                    onClick = onBack,
                )
            }
        )

        Divider(thickness = 1.dp, color = KotlinConfTheme.colors.strokePale)

        Column(
            Modifier
                .fillMaxSize()
                .background(color = KotlinConfTheme.colors.mainBackground)
                .verticalScroll(rememberScrollState())
        ) {
            if (state != null) {
                state.photoUrl?.let { url ->
                    NetworkImage(
                        photoUrl = url,
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp, horizontal = 12.dp)
                ) {
                    Text(text = state.title, style = KotlinConfTheme.typography.h2)
                    Spacer(Modifier.height(12.dp))
                    MarkdownView(text = state.content)
                }
            }
        }
    }
}
