package org.jetbrains.kotlinconf.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.zacsweers.metrox.viewmodel.metroViewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.AwardCategoryId
import org.jetbrains.kotlinconf.Nominee
import org.jetbrains.kotlinconf.NomineeId
import org.jetbrains.kotlinconf.generated.resources.Res
import org.jetbrains.kotlinconf.generated.resources.golden_kodee_backdrop
import org.jetbrains.kotlinconf.generated.resources.golden_kodee_banner
import org.jetbrains.kotlinconf.generated.resources.golden_kodee_finalist
import org.jetbrains.kotlinconf.generated.resources.golden_kodee_winner
import org.jetbrains.kotlinconf.ui.components.CardTag
import org.jetbrains.kotlinconf.ui.components.CardTagSize
import org.jetbrains.kotlinconf.ui.components.SpeakerAvatar
import org.jetbrains.kotlinconf.ui.components.Text
import org.jetbrains.kotlinconf.navigation.LocalUseNativeNavigation
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.utils.LocalWindowSize
import org.jetbrains.kotlinconf.utils.WindowSize
import org.jetbrains.kotlinconf.utils.bottomInsetPadding
import org.jetbrains.kotlinconf.utils.plus
import org.jetbrains.kotlinconf.utils.topInsetPadding

@Composable
fun GoldenKodeeScreen(
    onNomineeClick: (AwardCategoryId, NomineeId) -> Unit,
) {
    val viewModel = metroViewModel<GoldenKodeeViewModel>()
    val categories = viewModel.categories.collectAsStateWithLifecycle().value
    val useNativeNavigation = LocalUseNativeNavigation.current

    Column(
        Modifier.fillMaxSize()
            .background(color = KotlinConfTheme.colors.mainBackground)
    ) {
        val backdropAlpha = remember { Animatable(0f) }
        LaunchedEffect(Unit) {
            backdropAlpha.animateTo(1f, tween(3000, 0))
        }

        BoxWithConstraints(
            Modifier
                .fillMaxSize()
                .clipToBounds()
        ) {
            if (backdropAlpha.value > 0) {
                RadialBackdrop(Modifier.graphicsLayer { alpha = backdropAlpha.value })
            }

            val horizontalPadding = ((maxWidth - 1100.dp) / 2).coerceAtLeast(12.dp)
            LazyVerticalStaggeredGrid(
                modifier = Modifier.align(Alignment.Center).fillMaxSize(),
                columns = StaggeredGridCells.Adaptive(340.dp),
                contentPadding = topInsetPadding() +
                        PaddingValues(horizontal = horizontalPadding, vertical = 16.dp) +
                        if (useNativeNavigation) topInsetPadding() else PaddingValues.Zero +
                        bottomInsetPadding(),
                verticalItemSpacing = 16.dp,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item(span = StaggeredGridItemSpan.FullLine) {
                    Box(
                        Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.golden_kodee_banner),
                            contentDescription = null,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp),
                        )
                    }
                }

                for (category in categories) {
                    item(
                        key = "header-${category.id.id}",
                        span = StaggeredGridItemSpan.FullLine,
                    ) {
                        Text(
                            text = category.title,
                            style = KotlinConfTheme.typography.h2,
                            color = KotlinConfTheme.colors.primaryText,
                            modifier = Modifier
                                .padding(top = 16.dp)
                                .semantics { heading() },
                        )
                    }

                    items(
                        items = category.nominees.sortedByDescending { it.winner },
                        key = { "nominee-${it.id.id}" },
                    ) { nominee ->
                        NomineeRow(
                            nominee = nominee,
                            onClick = { onNomineeClick(category.id, nominee.id) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RadialBackdrop(
    modifier: Modifier = Modifier,
) {
    val windowContainerSize = LocalWindowInfo.current.containerSize
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 160_000, easing = LinearEasing),
        ),
    )
    val density = LocalDensity.current
    val backdropSizePx = maxOf(windowContainerSize.width, windowContainerSize.height) * 2f
    val backdropSizeDp = with(density) { backdropSizePx.toDp() }

    BoxWithConstraints {
        val width = this.constraints.maxWidth
        val height = this.constraints.maxHeight
        Image(
            painter = painterResource(Res.drawable.golden_kodee_backdrop),
            contentDescription = null,
            modifier = modifier
                .requiredSize(backdropSizeDp)
                .graphicsLayer {
                    transformOrigin = TransformOrigin.Center
                    translationX = -width / 1.9f
                    translationY = -height / 1.9f
                    rotationZ = rotation
                },
        )
    }
}

@Composable
private fun NomineeRow(
    nominee: Nominee,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color(0x4DC969FF), KotlinConfTheme.shapes.roundedCornerMd)
            .clip(KotlinConfTheme.shapes.roundedCornerMd)
            .background(Color(0xFF59017B))
            .clickable(onClick = onClick)
            .padding(12.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SpeakerAvatar(
                photoUrl = nominee.photoUrl,
                modifier = Modifier.size(96.dp),
            )
            Column(Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = nominee.name,
                        style = KotlinConfTheme.typography.h3,
                        color = KotlinConfTheme.colors.primaryText,
                    )
                }
                Spacer(Modifier.height(4.dp))
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
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = nominee.bio,
            style = KotlinConfTheme.typography.text2,
            color = KotlinConfTheme.colors.secondaryText,
        )
    }
}
