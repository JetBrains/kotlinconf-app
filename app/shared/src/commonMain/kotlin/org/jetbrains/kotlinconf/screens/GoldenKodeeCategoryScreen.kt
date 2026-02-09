package org.jetbrains.kotlinconf.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.AwardCategoryId
import org.jetbrains.kotlinconf.Nominee
import org.jetbrains.kotlinconf.NomineeId
import org.jetbrains.kotlinconf.generated.resources.Res
import org.jetbrains.kotlinconf.generated.resources.golden_kodee_finalist
import org.jetbrains.kotlinconf.generated.resources.golden_kodee_title
import org.jetbrains.kotlinconf.generated.resources.golden_kodee_winner
import org.jetbrains.kotlinconf.ui.components.HorizontalDivider
import org.jetbrains.kotlinconf.ui.components.MainHeaderTitleBar
import org.jetbrains.kotlinconf.ui.components.NomineeTag
import org.jetbrains.kotlinconf.ui.components.SpeakerAvatar
import org.jetbrains.kotlinconf.ui.components.Text
import org.jetbrains.kotlinconf.ui.components.TopMenuButton
import org.jetbrains.kotlinconf.ui.generated.resources.UiRes
import org.jetbrains.kotlinconf.ui.generated.resources.arrow_left_24
import org.jetbrains.kotlinconf.ui.generated.resources.main_header_back
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.utils.bottomInsetPadding
import org.jetbrains.kotlinconf.utils.plus
import org.jetbrains.kotlinconf.utils.topInsetPadding

@Composable
fun GoldenKodeeCategoryScreen(
    categoryId: AwardCategoryId,
    onBack: () -> Unit,
    onNomineeClick: (NomineeId) -> Unit,
) {
    val viewModel: GoldenKodeeCategoryViewModel =
        assistedMetroViewModel<GoldenKodeeCategoryViewModel, GoldenKodeeCategoryViewModel.Factory> {
            create(categoryId)
        }
    val category = viewModel.category.collectAsStateWithLifecycle().value
    val sortedNominees = viewModel.sortedNominees.collectAsStateWithLifecycle().value

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
            }
        )
        HorizontalDivider(thickness = 1.dp, color = KotlinConfTheme.colors.strokePale)

        LazyVerticalGrid(
            columns = GridCells.Adaptive(300.dp),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 24.dp) +
                    bottomInsetPadding(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(bottom = 12.dp),
                ) {
                    Text(
                        text = category?.title ?: "",
                        style = KotlinConfTheme.typography.h1,
                        color = KotlinConfTheme.colors.primaryText,
                        modifier = Modifier.semantics { heading() },
                    )

                    Text(
                        text = category?.description ?: "",
                        style = KotlinConfTheme.typography.text1,
                        color = KotlinConfTheme.colors.longText,
                    )
                }
            }

            items(sortedNominees) { nominee ->
                NomineeCard(
                    nominee = nominee,
                    onClick = { onNomineeClick(nominee.id) },
                )
            }
        }
    }
}

@Composable
private fun NomineeCard(
    nominee: Nominee,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color(0x4DC969FF), KotlinConfTheme.shapes.roundedCornerMd)
            .clip(KotlinConfTheme.shapes.roundedCornerMd)
            .clickable(onClick = onClick)
            .background(KotlinConfTheme.colors.tileBackground),
    ) {
        NomineeTag(
            label = stringResource(if (nominee.winner) Res.string.golden_kodee_winner else Res.string.golden_kodee_finalist),
            winner = nominee.winner,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 24.dp),
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
                .padding(vertical = 12.dp),
        ) {
            SpeakerAvatar(
                photoUrl = nominee.photoUrl,
                modifier = Modifier
                    .size(96.dp)
                    .fillMaxWidth(0.5f)
                    .aspectRatio(1f),
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = nominee.name,
                style = KotlinConfTheme.typography.h3,
                color = KotlinConfTheme.colors.primaryText,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = nominee.position,
                style = KotlinConfTheme.typography.text2,
                color = KotlinConfTheme.colors.secondaryText,
            )

            Spacer(Modifier.height(12.dp))

            HorizontalDivider(1.dp, Color(0x4DC969FF))

            Spacer(Modifier.height(12.dp))

            Text(
                text = nominee.projectName,
                style = KotlinConfTheme.typography.text2,
                color = KotlinConfTheme.colors.primaryText,
            )
        }
    }
}
