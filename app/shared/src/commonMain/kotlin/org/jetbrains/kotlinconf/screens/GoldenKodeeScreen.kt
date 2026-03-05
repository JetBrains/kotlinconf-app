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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.zacsweers.metrox.viewmodel.metroViewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.kotlinconf.AwardCategory
import org.jetbrains.kotlinconf.AwardCategoryId
import org.jetbrains.kotlinconf.generated.resources.Res
import org.jetbrains.kotlinconf.generated.resources.golden_kodee_backdrop
import org.jetbrains.kotlinconf.generated.resources.golden_kodee_banner
import org.jetbrains.kotlinconf.generated.resources.golden_kodee_title
import org.jetbrains.kotlinconf.generated.resources.golden_kodee_winner_icon
import org.jetbrains.kotlinconf.ui.components.HorizontalDivider
import org.jetbrains.kotlinconf.ui.components.MainHeaderTitleBar
import org.jetbrains.kotlinconf.ui.components.SpeakerAvatar
import org.jetbrains.kotlinconf.ui.components.Text
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.utils.bottomInsetPadding
import org.jetbrains.kotlinconf.utils.plus
import org.jetbrains.kotlinconf.utils.topInsetPadding

@Composable
fun GoldenKodeeScreen(
    onCategoryClick: (AwardCategoryId) -> Unit,
) {
    val viewModel = metroViewModel<GoldenKodeeViewModel>()
    val categories = viewModel.categories.collectAsStateWithLifecycle().value

    Column(
        Modifier.fillMaxSize()
            .background(color = KotlinConfTheme.colors.mainBackground)
            .padding(topInsetPadding())
    ) {
        MainHeaderTitleBar(stringResource(Res.string.golden_kodee_title))
        HorizontalDivider(1.dp, KotlinConfTheme.colors.strokePale)

        val backdrop = painterResource(Res.drawable.golden_kodee_backdrop)
        val backdropAlpha = remember { Animatable(0f) }
        LaunchedEffect(Unit) {
            backdropAlpha.animateTo(1f, tween(3000, 300))
        }
        val windowContainerSize = LocalWindowInfo.current.containerSize
        val infiniteTransition = rememberInfiniteTransition()
        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 200_000, easing = LinearEasing),
            ),
        )
        Box(
            Modifier
                .fillMaxSize()
                .clipToBounds()
                .drawBehind {
                    val alpha = backdropAlpha.value
                    val backdropSize =
                        maxOf(windowContainerSize.width, windowContainerSize.height) * 2f
                    val scale = backdropSize / minOf(
                        backdrop.intrinsicSize.width,
                        backdrop.intrinsicSize.height
                    )
                    val drawWidth = backdrop.intrinsicSize.width * scale
                    val drawHeight = backdrop.intrinsicSize.height * scale
                    val pivotX = drawWidth / 2f
                    val pivotY = drawHeight / 2f
                    translate(
                        left = -pivotX - drawWidth * 0.05f,
                        top = -pivotY - drawHeight * 0.05f
                    ) {
                        rotate(rotation, pivot = Offset(pivotX, pivotY)) {
                            with(backdrop) {
                                draw(size = Size(drawWidth, drawHeight), alpha = alpha)
                            }
                        }
                    }
                },
        ) {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Adaptive(300.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 16.dp) + bottomInsetPadding(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalItemSpacing = 24.dp,
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

                items(categories) { category ->
                    AwardCategoryCard(
                        category = category,
                        onClick = { onCategoryClick(category.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun AwardCategoryCard(
    category: AwardCategory,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .border(1.dp, Color(0x4DC969FF), KotlinConfTheme.shapes.roundedCornerMd)
            .clip(KotlinConfTheme.shapes.roundedCornerMd)
            .clickable(onClick = onClick)
            .background(Color(0xFF59017B))
            .padding(top = 24.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = category.title,
            style = KotlinConfTheme.typography.h2,
            color = KotlinConfTheme.colors.primaryText,
            modifier = Modifier.padding(horizontal = 12.dp),
        )
        Column {
            for (nominee in category.nominees.sortedByDescending { it.winner }) {
                NomineeCard(
                    name = nominee.name,
                    title = nominee.position,
                    photoUrl = nominee.photoUrl,
                    winner = nominee.winner,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(KotlinConfTheme.shapes.roundedCornerMd)
                        .padding(12.dp)
                )
            }
        }
    }
}

@Composable
private fun NomineeCard(
    name: String,
    title: String,
    photoUrl: String,
    winner: Boolean,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(96.dp)) {
            SpeakerAvatar(
                photoUrl = photoUrl,
                modifier = Modifier.size(96.dp),
            )
            if (winner) {
                Image(
                    vectorResource(Res.drawable.golden_kodee_winner_icon),
                    null,
                    modifier = Modifier
                        .graphicsLayer {
                            translationX = 10.dp.toPx()
                            translationY = -10.dp.toPx()
                        }
                        .align(Alignment.TopEnd)
                        .size(28.dp)
                        .border(4.dp, Color(0xFF59017B), RoundedCornerShape(10.dp))
                        .padding(2.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(KotlinConfTheme.colors.primaryBackground)
                        .padding(4.dp)
                )
            }
        }
        Column {
            Text(
                text = name,
                style = KotlinConfTheme.typography.h3,
                color = KotlinConfTheme.colors.primaryText,
            )
            Spacer(modifier = Modifier.size(6.dp))
            Text(
                text = title,
                style = KotlinConfTheme.typography.text2,
                color = KotlinConfTheme.colors.secondaryText,
            )
        }
    }
}
