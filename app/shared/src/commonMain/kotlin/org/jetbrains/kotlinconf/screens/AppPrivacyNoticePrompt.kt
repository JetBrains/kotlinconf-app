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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.zacsweers.metrox.viewmodel.metroViewModel
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.kotlinconf.ScrollToTopHandler
import org.jetbrains.kotlinconf.generated.resources.Res
import org.jetbrains.kotlinconf.generated.resources.kodee_privacy
import org.jetbrains.kotlinconf.generated.resources.privacy_notice_accept
import org.jetbrains.kotlinconf.generated.resources.privacy_notice_back
import org.jetbrains.kotlinconf.generated.resources.privacy_notice_description
import org.jetbrains.kotlinconf.generated.resources.privacy_notice_read_action
import org.jetbrains.kotlinconf.generated.resources.privacy_notice_reject
import org.jetbrains.kotlinconf.generated.resources.privacy_notice_title
import org.jetbrains.kotlinconf.ui.components.Action
import org.jetbrains.kotlinconf.ui.components.ActionSize
import org.jetbrains.kotlinconf.ui.components.Button
import org.jetbrains.kotlinconf.ui.components.Divider
import org.jetbrains.kotlinconf.ui.components.MainHeaderTitleBar
import org.jetbrains.kotlinconf.ui.components.MarkdownView
import org.jetbrains.kotlinconf.ui.components.Text
import org.jetbrains.kotlinconf.ui.components.TopMenuButton
import org.jetbrains.kotlinconf.ui.generated.resources.UiRes
import org.jetbrains.kotlinconf.ui.generated.resources.arrow_left_24
import org.jetbrains.kotlinconf.ui.generated.resources.arrow_right_24
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.utils.FadingAnimationSpec

@Composable
fun AppPrivacyNoticePrompt(
    onRejectNotice: () -> Unit,
    onAcceptNotice: () -> Unit,
    onAppTermsOfUse: () -> Unit,
    confirmationRequired: Boolean,
    viewModel: PrivacyNoticeViewModel = metroViewModel(),
) {
    var detailsVisible by rememberSaveable { mutableStateOf(false) }
    val noticeState by viewModel.state.collectAsStateWithLifecycle()

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
                        stringResource(Res.string.privacy_notice_title),
                        startContent = {
                            TopMenuButton(
                                icon = UiRes.drawable.arrow_left_24,
                                contentDescription = stringResource(Res.string.privacy_notice_back),
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
                            Res.readBytes("files/app-privacy-notice.md")
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
                        imageVector = vectorResource(Res.drawable.kodee_privacy),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth()
                            .size(160.dp)
                    )
                    Text(
                        stringResource(Res.string.privacy_notice_title),
                        style = KotlinConfTheme.typography.h1
                    )
                    Text(
                        stringResource(Res.string.privacy_notice_description),
                        color = KotlinConfTheme.colors.longText,
                    )
                    Action(
                        stringResource(Res.string.privacy_notice_read_action),
                        icon = UiRes.drawable.arrow_right_24,
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
                label = stringResource(Res.string.privacy_notice_reject),
                onClick = { onRejectNotice() },
                enabled = noticeState !is PrivacyNoticeState.Loading,
            )
            Button(
                label = stringResource(Res.string.privacy_notice_accept),
                onClick = { viewModel.acceptPrivacyNotice(confirmationRequired) },
                modifier = Modifier.weight(1f),
                primary = true,
                enabled = noticeState !is PrivacyNoticeState.Loading,
            )
        }
    }
}
