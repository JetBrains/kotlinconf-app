package org.jetbrains.kotlinconf.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import kotlinconfapp.ui_components.generated.resources.Res
import kotlinconfapp.ui_components.generated.resources.clock_28
import kotlinconfapp.ui_components.generated.resources.clock_28_fill
import kotlinconfapp.ui_components.generated.resources.info_28
import kotlinconfapp.ui_components.generated.resources.info_28_fill
import kotlinconfapp.ui_components.generated.resources.location_28
import kotlinconfapp.ui_components.generated.resources.location_28_fill
import kotlinconfapp.ui_components.generated.resources.team_28
import kotlinconfapp.ui_components.generated.resources.team_28_fill
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui.theme.PreviewHelper
import kotlin.reflect.KClass

private val MainNavigationButtonShape = RoundedCornerShape(8.dp)

@Composable
private fun MainNavigationButton(
    iconResource: DrawableResource,
    iconFilledResource: DrawableResource,
    contentDescription: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val iconColor by animateColorAsState(
        if (selected) KotlinConfTheme.colors.primaryText
        else KotlinConfTheme.colors.secondaryText
    )
    Icon(
        modifier = modifier
            .clip(MainNavigationButtonShape)
            .selectable(
                selected = selected,
                enabled = true,
                role = Role.Tab,
                onClick = onClick,
            )
            .padding(10.dp)
            .size(28.dp),
        painter = painterResource(if (selected) iconFilledResource else iconResource),
        contentDescription = contentDescription,
        tint = iconColor,
    )
}

data class MainNavDestination(
    val label: String,
    val icon: DrawableResource,
    val route: Any,
    val iconSelected: DrawableResource = icon,
    val routeClass: KClass<*>? = null,
)

@Composable
fun MainNavigation(
    currentDestination: MainNavDestination?,
    destinations: List<MainNavDestination>,
    onSelect: (MainNavDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        destinations.forEach { destination ->
            MainNavigationButton(
                iconResource = destination.icon,
                iconFilledResource = destination.iconSelected,
                contentDescription = destination.label,
                selected = destination == currentDestination,
                onClick = { onSelect(destination) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Preview
@Composable
internal fun MainNavigationPreview() {
    PreviewHelper {
        var currentDestination by remember {
            mutableStateOf(MainNavDestination(
                label = "Schedule",
                icon = Res.drawable.clock_28,
                iconSelected = Res.drawable.clock_28_fill,
                route = "Schedule"
            ))
        }
        MainNavigation(
            currentDestination = currentDestination,
            destinations = listOf(
                MainNavDestination(
                    label = "Info",
                    icon = Res.drawable.info_28,
                    iconSelected = Res.drawable.info_28_fill,
                    route = "Info"
                ),
                MainNavDestination(
                    label = "Schedule",
                    icon = Res.drawable.clock_28,
                    iconSelected = Res.drawable.clock_28_fill,
                    route = "Schedule"
                ),
                MainNavDestination(
                    label = "Speakers",
                    icon = Res.drawable.team_28,
                    iconSelected = Res.drawable.team_28_fill,
                    route = "Speakers"
                ),
                MainNavDestination(
                    label = "Map",
                    icon = Res.drawable.location_28,
                    iconSelected = Res.drawable.location_28_fill,
                    route = "Map"
                ),
            ),
            onSelect = { currentDestination = it },
        )
    }
}
