package org.jetbrains.kotlinconf.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.ui.generated.resources.UiRes
import org.jetbrains.kotlinconf.ui.generated.resources.clock_28
import org.jetbrains.kotlinconf.ui.generated.resources.clock_28_fill
import org.jetbrains.kotlinconf.ui.generated.resources.info_28
import org.jetbrains.kotlinconf.ui.generated.resources.info_28_fill
import org.jetbrains.kotlinconf.ui.generated.resources.location_28
import org.jetbrains.kotlinconf.ui.generated.resources.location_28_fill
import org.jetbrains.kotlinconf.ui.generated.resources.now
import org.jetbrains.kotlinconf.ui.generated.resources.team_28
import org.jetbrains.kotlinconf.ui.generated.resources.team_28_fill
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui.theme.PreviewHelper

@Composable
private fun NavRailMenuItem(
    iconResource: DrawableResource,
    iconFilledResource: DrawableResource,
    label: String,
    selected: Boolean,
    expanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val contentColor by animateColorAsState(
        if (selected) KotlinConfTheme.colors.primaryText
        else KotlinConfTheme.colors.secondaryText
    )

    val itemModifier = modifier
        .clip(KotlinConfTheme.shapes.roundedCornerMd)
        .fillMaxWidth()
        .selectable(
            selected = selected,
            enabled = true,
            role = Role.Tab,
            onClick = onClick,
        )
        .padding(12.dp)

    val arrangement = Arrangement.spacedBy(8.dp)

    SharedTransitionLayout(
        modifier = itemModifier,
    ) {
        AnimatedContent(expanded, modifier = Modifier.fillMaxWidth()) { isExpanded ->
            val itemContent = @Composable {
                Icon(
                    modifier = Modifier.size(28.dp)
                        .sharedElement(
                            rememberSharedContentState(key = "icon"),
                            animatedVisibilityScope = this@AnimatedContent,
                        ),
                    painter = painterResource(if (selected) iconFilledResource else iconResource),
                    contentDescription = null,
                    tint = contentColor,
                )
                Text(
                    modifier = Modifier.sharedElement(
                        rememberSharedContentState(key = "text"),
                        animatedVisibilityScope = this@AnimatedContent,
                    ),
                    text = label,
                    style = KotlinConfTheme.typography.text2,
                    color = contentColor,
                )
            }

            if (isExpanded) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = arrangement,
                ) {
                    itemContent()
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = arrangement,
                ) {
                    itemContent()
                }
            }
        }
    }
}

@Composable
fun <T : Any> MainNavigationRail(
    currentDestination: MainNavDestination<T>?,
    destinations: List<MainNavDestination<T>>,
    onSelect: (MainNavDestination<T>) -> Unit,
    expanded: Boolean,
    modifier: Modifier = Modifier,
) {
    val width by animateDpAsState(
        if (expanded) 220.dp else 140.dp,
        animationSpec = spring(
            visibilityThreshold = Dp.VisibilityThreshold,
            stiffness = Spring.StiffnessLow
        ),
    )
    Column(
        modifier = modifier
            .width(width)
            .padding(12.dp)
            .animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        destinations.forEach { destination ->
            NavRailMenuItem(
                iconResource = destination.icon,
                iconFilledResource = destination.iconSelected,
                label = stringResource(destination.label),
                selected = destination == currentDestination,
                expanded = expanded,
                onClick = { onSelect(destination) },
            )
        }
    }
}

@Preview
@Composable
internal fun MainNavigationRailPreview() {
    PreviewHelper {
        var currentDestination by remember {
            mutableStateOf(
                MainNavDestination(
                    label = UiRes.string.now,
                    icon = UiRes.drawable.clock_28,
                    iconSelected = UiRes.drawable.clock_28_fill,
                    route = "Schedule"
                )
            )
        }
        MainNavigationRail(
            currentDestination = currentDestination,
            destinations = listOf(
                MainNavDestination(
                    label = UiRes.string.now,
                    icon = UiRes.drawable.info_28,
                    iconSelected = UiRes.drawable.info_28_fill,
                    route = "Info"
                ),
                MainNavDestination(
                    label = UiRes.string.now,
                    icon = UiRes.drawable.clock_28,
                    iconSelected = UiRes.drawable.clock_28_fill,
                    route = "Schedule"
                ),
                MainNavDestination(
                    label = UiRes.string.now,
                    icon = UiRes.drawable.team_28,
                    iconSelected = UiRes.drawable.team_28_fill,
                    route = "Speakers"
                ),
                MainNavDestination(
                    label = UiRes.string.now,
                    icon = UiRes.drawable.location_28,
                    iconSelected = UiRes.drawable.location_28_fill,
                    route = "Map"
                ),
            ),
            onSelect = { currentDestination = it },
            expanded = false,
        )
    }
}

@Preview
@Composable
internal fun MainNavigationRailExpandedPreview() {
    PreviewHelper {
        var currentDestination by remember {
            mutableStateOf(
                MainNavDestination(
                    label = UiRes.string.now,
                    icon = UiRes.drawable.clock_28,
                    iconSelected = UiRes.drawable.clock_28_fill,
                    route = "Schedule"
                )
            )
        }
        MainNavigationRail(
            currentDestination = currentDestination,
            destinations = listOf(
                MainNavDestination(
                    label = UiRes.string.now,
                    icon = UiRes.drawable.info_28,
                    iconSelected = UiRes.drawable.info_28_fill,
                    route = "Info"
                ),
                MainNavDestination(
                    label = UiRes.string.now,
                    icon = UiRes.drawable.clock_28,
                    iconSelected = UiRes.drawable.clock_28_fill,
                    route = "Schedule"
                ),
                MainNavDestination(
                    label = UiRes.string.now,
                    icon = UiRes.drawable.team_28,
                    iconSelected = UiRes.drawable.team_28_fill,
                    route = "Speakers"
                ),
                MainNavDestination(
                    label = UiRes.string.now,
                    icon = UiRes.drawable.location_28,
                    iconSelected = UiRes.drawable.location_28_fill,
                    route = "Map"
                ),
            ),
            onSelect = { currentDestination = it },
            expanded = true,
        )
    }
}
