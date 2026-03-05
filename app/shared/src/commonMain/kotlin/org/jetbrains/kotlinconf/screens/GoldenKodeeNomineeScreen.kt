package org.jetbrains.kotlinconf.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
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
import org.jetbrains.kotlinconf.generated.resources.golden_kodee_winner_banner_description
import org.jetbrains.kotlinconf.generated.resources.golden_kodee_winner_banner_title
import org.jetbrains.kotlinconf.ui.components.HorizontalDivider
import org.jetbrains.kotlinconf.ui.components.MainHeaderTitleBar
import org.jetbrains.kotlinconf.ui.components.NomineeTag
import org.jetbrains.kotlinconf.ui.components.SpeakerAvatar
import org.jetbrains.kotlinconf.ui.components.Text
import org.jetbrains.kotlinconf.ui.components.TopMenuButton
import org.jetbrains.kotlinconf.ui.generated.resources.UiRes
import org.jetbrains.kotlinconf.ui.generated.resources.arrow_left_24
import org.jetbrains.kotlinconf.ui.generated.resources.main_header_back
import org.jetbrains.kotlinconf.ui.theme.GoldenKodeeColors
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.utils.bottomInsetPadding
import org.jetbrains.kotlinconf.utils.topInsetPadding

@Composable
fun GoldenKodeeNomineeScreen(
    categoryId: AwardCategoryId,
    nomineeId: NomineeId,
    onBack: () -> Unit,
) {
    val viewModel: GoldenKodeeNomineeViewModel =
        assistedMetroViewModel<GoldenKodeeNomineeViewModel, GoldenKodeeNomineeViewModel.Factory> {
            create(categoryId, nomineeId)
        }
    val nominee = viewModel.nominee.collectAsStateWithLifecycle().value ?: return

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
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            NomineeTag(
                label = stringResource(if (nominee.winner) Res.string.golden_kodee_winner else Res.string.golden_kodee_finalist),
                winner = nominee.winner,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 24.dp),
            )

            Column(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 16.dp)
                    .padding(bottomInsetPadding()),
            ) {
                Text(
                    text = nominee.name,
                    style = KotlinConfTheme.typography.h2,
                    color = KotlinConfTheme.colors.primaryText,
                    modifier = Modifier.semantics { heading() },
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = nominee.position,
                    style = KotlinConfTheme.typography.text2,
                    color = KotlinConfTheme.colors.secondaryText,
                )

                Spacer(Modifier.height(16.dp))

                SpeakerAvatar(
                    photoUrl = nominee.photoUrl,
                    modifier = Modifier
                        .widthIn(max = 400.dp)
                        .fillMaxWidth()
                        .aspectRatio(1f),
                )

                if (nominee.winner) {
                    Spacer(Modifier.height(24.dp))
                    WinnerBanner(
                        isCompact = LocalWindowInfo.current.containerDpSize.width <= 400.dp
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = nominee.bio,
                    style = KotlinConfTheme.typography.text2,
                    color = KotlinConfTheme.colors.longText,
                )

                Spacer(Modifier.height(24.dp))

                Text(
                    text = nominee.projectName,
                    style = KotlinConfTheme.typography.h3,
                    color = KotlinConfTheme.colors.primaryText,
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = nominee.projectDescription,
                    style = KotlinConfTheme.typography.text2,
                    color = KotlinConfTheme.colors.longText,
                )
            }
        }
    }
}

@Composable
private fun WinnerBanner(
    isCompact: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(KotlinConfTheme.shapes.roundedCornerMd)
    ) {
        Image(
            painter = painterResource(Res.drawable.golden_kodee_winner_background),
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop,
        )

        val textContent = @Composable {
            Text(
                stringResource(Res.string.golden_kodee_winner_banner_title),
                style = KotlinConfTheme.typography.h2,
                color = KotlinConfTheme.colors.primaryText
            )
            Spacer(Modifier.height(12.dp))
            Text(
                stringResource(Res.string.golden_kodee_winner_banner_description),
                style = KotlinConfTheme.typography.text2,
                color = KotlinConfTheme.colors.secondaryText
            )
        }

        if (isCompact) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .padding(top = 12.dp),
            ) {
                textContent()
                Spacer(Modifier.height(12.dp))
                Image(
                    painter = painterResource(Res.drawable.golden_kodee_winner),
                    contentDescription = null,
                    modifier = Modifier
                        .size(150.dp)
                        .align(Alignment.CenterHorizontally),
                )
            }
        } else {
            Image(
                painter = painterResource(Res.drawable.golden_kodee_winner),
                contentDescription = null,
                modifier = Modifier.align(Alignment.BottomEnd)
                    .padding(top = 12.dp)
                    .size(150.dp),
            )
            Column(
                Modifier.align(Alignment.CenterStart)
                    .padding(start = 12.dp, end = 150.dp)
                    .padding(vertical = 12.dp)
            ) {
                textContent()
            }
        }
    }
}

@Preview(widthDp = 360)
@Composable
private fun WinnerBannerNarrowPreview() {
    KotlinConfTheme(GoldenKodeeColors) {
        WinnerBanner(isCompact = true)
    }
}

@Preview(widthDp = 500)
@Composable
private fun WinnerBannerWidePreview() {
    KotlinConfTheme(GoldenKodeeColors) {
        WinnerBanner(isCompact = false)
    }
}
