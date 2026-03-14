package org.jetbrains.kotlinconf.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.AwardCategoryId
import org.jetbrains.kotlinconf.NomineeId
import org.jetbrains.kotlinconf.generated.resources.Res
import org.jetbrains.kotlinconf.generated.resources.golden_kodee_finalist
import org.jetbrains.kotlinconf.generated.resources.golden_kodee_title
import org.jetbrains.kotlinconf.generated.resources.golden_kodee_winner
import org.jetbrains.kotlinconf.generated.resources.golden_kodee_winner_background
import org.jetbrains.kotlinconf.generated.resources.golden_kodee_winner_banner_title
import org.jetbrains.kotlinconf.ui.components.CardTag
import org.jetbrains.kotlinconf.ui.components.CardTagSize
import org.jetbrains.kotlinconf.ui.components.HorizontalDivider
import org.jetbrains.kotlinconf.ui.components.MainHeaderTitleBar
import org.jetbrains.kotlinconf.ui.components.MarkdownView
import org.jetbrains.kotlinconf.ui.components.SpeakerAvatar
import org.jetbrains.kotlinconf.ui.components.Text
import org.jetbrains.kotlinconf.ui.components.TopMenuButton
import org.jetbrains.kotlinconf.ui.generated.resources.UiRes
import org.jetbrains.kotlinconf.ui.generated.resources.arrow_left_24
import org.jetbrains.kotlinconf.ui.generated.resources.main_header_back
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.utils.bottomInsetPadding
import org.jetbrains.kotlinconf.utils.topInsetPadding

@Composable
fun GoldenKodeeFinalistScreen(
    categoryId: AwardCategoryId,
    nomineeId: NomineeId,
    onBack: () -> Unit,
) {
    val viewModel: GoldenKodeeFinalistViewModel =
        assistedMetroViewModel<GoldenKodeeFinalistViewModel, GoldenKodeeFinalistViewModel.Factory> {
            create(categoryId, nomineeId)
        }
    val nominee = viewModel.nominee.collectAsStateWithLifecycle().value ?: return
    val year = viewModel.year.collectAsStateWithLifecycle().value

    Column(
        Modifier
            .fillMaxSize()
            .background(color = KotlinConfTheme.colors.mainBackground)
            .padding(topInsetPadding())
    ) {
        MainHeaderTitleBar(
            title = stringResource(Res.string.golden_kodee_title),
            startContent = {
                TopMenuButton(
                    icon = UiRes.drawable.arrow_left_24,
                    contentDescription = stringResource(UiRes.string.main_header_back),
                    onClick = onBack,
                )
            },
        )
        HorizontalDivider(thickness = 1.dp, color = KotlinConfTheme.colors.strokePale)

        Box(
            Modifier.fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 16.dp)
                .padding(bottomInsetPadding()),
            contentAlignment = Alignment.TopCenter,
        ) {
            Column(Modifier.widthIn(max = 580.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = nominee.name,
                        style = KotlinConfTheme.typography.h2,
                        color = KotlinConfTheme.colors.primaryText,
                        modifier = Modifier
                            .semantics { heading() }
                            .weight(1f, fill = false),
                    )
                    Spacer(Modifier.width(8.dp))
                    CardTag(
                        label = if (nominee.winner) {
                            stringResource(Res.string.golden_kodee_winner)
                        } else {
                            stringResource(Res.string.golden_kodee_finalist)
                        },
                        selected = nominee.winner,
                        size = CardTagSize.Large,
                    )
                }

                Spacer(Modifier.height(16.dp))

                SpeakerAvatar(
                    photoUrl = nominee.photoUrl,
                    modifier = Modifier
                        .widthIn(max = 400.dp)
                        .fillMaxWidth()
                        .aspectRatio(1f),
                )

                Spacer(Modifier.height(24.dp))

                Text(
                    text = nominee.bio,
                    style = KotlinConfTheme.typography.text1,
                    color = KotlinConfTheme.colors.longText,
                )

                Spacer(Modifier.height(24.dp))

                if (nominee.winner) {
                    val isCompact = LocalWindowInfo.current.containerDpSize.width <= 400.dp
                    if (isCompact) {
                        CompactWinnerBanner(year = year)
                    } else {
                        LargeWinnerBanner(year = year)
                    }
                    Spacer(Modifier.height(24.dp))
                }

                MarkdownView(
                    text = nominee.description,
                )
            }
        }
    }
}

@Composable
private fun CompactWinnerBanner(year: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(KotlinConfTheme.shapes.roundedCornerMd)
    ) {
        Image(
            modifier = Modifier
                .background(Color(0xFF59017B))
                .matchParentSize(),
            painter = painterResource(Res.drawable.golden_kodee_winner_background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
        )

        val textContent = @Composable {
            Text(
                stringResource(Res.string.golden_kodee_winner_banner_title, year),
                style = KotlinConfTheme.typography.h2.copy(textAlign = TextAlign.Center),
                color = KotlinConfTheme.colors.primaryText,
            )
        }

        Column(
            modifier = Modifier.padding(top = 12.dp).padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            textContent()

            Spacer(Modifier.height(12.dp))

            Image(
                painter = painterResource(Res.drawable.golden_kodee_winner),
                contentDescription = null,
                modifier = Modifier.size(190.dp)
            )
        }
    }
}

@Composable
private fun LargeWinnerBanner(year: String) {
    Box {
        Box(
            modifier = Modifier
                .heightIn(min = 135.dp)
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .clip(KotlinConfTheme.shapes.roundedCornerMd)
        ) {
            Image(
                painter = painterResource(Res.drawable.golden_kodee_winner_background),
                contentDescription = null,
                modifier = Modifier
                    .background(Color(0xFF59017B))
                    .matchParentSize(),
                contentScale = ContentScale.Crop,
            )

            val textContent = @Composable {
                Text(
                    stringResource(Res.string.golden_kodee_winner_banner_title, year),
                    style = KotlinConfTheme.typography.h2,
                    color = KotlinConfTheme.colors.primaryText,
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.matchParentSize()
                    .padding(start = 12.dp, end = 180.dp)
                    .padding(vertical = 12.dp),
            ) {
                textContent()
            }
        }

        Image(
            painter = painterResource(Res.drawable.golden_kodee_winner),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(190.dp)
                .graphicsLayer {
                    translationX = 10.dp.toPx()
                }
        )
    }
}
