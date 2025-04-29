package org.jetbrains.kotlinconf.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinconfapp.shared.generated.resources.kodee_privacy
import kotlinconfapp.shared.generated.resources.privacy_notice_accept
import kotlinconfapp.shared.generated.resources.privacy_notice_back
import kotlinconfapp.shared.generated.resources.privacy_notice_description
import kotlinconfapp.shared.generated.resources.privacy_notice_read_action
import kotlinconfapp.shared.generated.resources.privacy_notice_reject
import kotlinconfapp.shared.generated.resources.privacy_notice_title
import kotlinconfapp.ui_components.generated.resources.Res
import kotlinconfapp.ui_components.generated.resources.arrow_left_24
import kotlinconfapp.ui_components.generated.resources.arrow_right_24
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.kotlinconf.ScrollToTopHandler
import org.jetbrains.kotlinconf.ui.components.Action
import org.jetbrains.kotlinconf.ui.components.ActionSize
import org.jetbrains.kotlinconf.ui.components.Button
import org.jetbrains.kotlinconf.ui.components.Divider
import org.jetbrains.kotlinconf.ui.components.MainHeaderTitleBar
import org.jetbrains.kotlinconf.ui.components.MarkdownView
import org.jetbrains.kotlinconf.ui.components.Text
import org.jetbrains.kotlinconf.ui.components.TopMenuButton
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.utils.FadingAnimationSpec
import org.koin.compose.viewmodel.koinViewModel
import kotlinconfapp.shared.generated.resources.Res as AppRes


@Composable
fun AppPrivacyNoticePrompt(
    onRejectNotice: () -> Unit,
    onAcceptNotice: () -> Unit,
    onAppTermsOfUse: () -> Unit,
    confirmationRequired: Boolean,
    viewModel: PrivacyNoticeViewModel = koinViewModel(),
) {
    var detailsVisible by rememberSaveable { mutableStateOf(false) }
    val noticeState by viewModel.state.collectAsState()

    LaunchedEffect(noticeState) {
        if (noticeState is PrivacyNoticeState.Done) {
            onAcceptNotice()
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(color = KotlinConfTheme.colors.mainBackground)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        AnimatedContent(
            targetState = detailsVisible,
            modifier = Modifier.weight(1f),
            transitionSpec = { FadingAnimationSpec }
        ) { detailsVis ->
            if (detailsVis) {
                Column {
                    MainHeaderTitleBar(
                        stringResource(AppRes.string.privacy_notice_title),
                        startContent = {
                            TopMenuButton(
                                icon = Res.drawable.arrow_left_24,
                                contentDescription = stringResource(AppRes.string.privacy_notice_back),
                                onClick = { detailsVisible = false },
                            )
                        }
                    )
                    Divider(
                        thickness = 1.dp,
                        color = KotlinConfTheme.colors.strokePale,
                    )
                    val scrollState = rememberScrollState()
                    ScrollToTopHandler(scrollState)
                    MarkdownView(
                        loadText = {
                            @OptIn(ExperimentalResourceApi::class)
                            AppRes.readBytes("files/app-privacy-notice.md")
                        },
                        modifier = Modifier.padding(horizontal = 12.dp).verticalScroll(scrollState),
                        onCustomUriClick = { uri ->
                            if (uri == "app-terms.md") {
                                onAppTermsOfUse()
                            }
                        },
                    )
                    Spacer(Modifier.weight(1f))
                    Divider(
                        thickness = 1.dp,
                        color = KotlinConfTheme.colors.strokePale,
                    )
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Image(
                        imageVector = vectorResource(AppRes.drawable.kodee_privacy),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth()
                            .size(160.dp)
                    )
                    Text(
                        stringResource(AppRes.string.privacy_notice_title),
                        style = KotlinConfTheme.typography.h1
                    )
                    Text(
                        stringResource(AppRes.string.privacy_notice_description),
                        color = KotlinConfTheme.colors.longText,
                    )
                    Action(
                        stringResource(AppRes.string.privacy_notice_read_action),
                        icon = Res.drawable.arrow_right_24,
                        size = ActionSize.Large,
                        enabled = true,
                        onClick = { detailsVisible = true }
                    )
                }
                Spacer(Modifier.weight(1f))
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp)
        ) {
            Button(
                label = stringResource(AppRes.string.privacy_notice_reject),
                onClick = { onRejectNotice() },
                enabled = noticeState !is PrivacyNoticeState.Loading,
            )
            Button(
                label = stringResource(AppRes.string.privacy_notice_accept),
                onClick = { viewModel.acceptPrivacyNotice(confirmationRequired) },
                modifier = Modifier.weight(1f),
                primary = true,
                enabled = noticeState !is PrivacyNoticeState.Loading,
            )
        }
    }
}
