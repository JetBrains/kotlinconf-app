package org.jetbrains.kotlinconf.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutQuad
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.settings_notifications_title
import kotlinconfapp.shared.generated.resources.settings_theme_dark
import kotlinconfapp.shared.generated.resources.settings_theme_light
import kotlinconfapp.shared.generated.resources.settings_theme_system
import kotlinconfapp.shared.generated.resources.settings_theme_title
import kotlinconfapp.shared.generated.resources.settings_title
import kotlinconfapp.shared.generated.resources.theme_dark
import kotlinconfapp.shared.generated.resources.theme_light
import kotlinconfapp.shared.generated.resources.theme_system
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.LocalFlags
import org.jetbrains.kotlinconf.ScreenWithTitle
import org.jetbrains.kotlinconf.Theme
import org.jetbrains.kotlinconf.ui.components.Text
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.koin.compose.viewmodel.koinViewModel
import kotlinconfapp.shared.generated.resources.Res as AppRes

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val graphicsLayer = rememberGraphicsLayer()
    val scope = rememberCoroutineScope()
    var bitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var animationProgress = remember { Animatable(0f) }
    var animationCenter by remember { mutableStateOf(Offset.Zero) }

    val currentTheme by viewModel.theme.collectAsStateWithLifecycle()

    Box(Modifier.fillMaxSize()) {
        SettingsScreenImpl(
            onBack = onBack,
            currentTheme = currentTheme,
            onThemeChange = { theme, center ->
                scope.launch {
                    bitmap = graphicsLayer.toImageBitmap()
                    animationCenter = center
                    animationProgress.snapTo(0f)
                    animationProgress.animateTo(1f, tween(5000, easing = EaseOutQuad))
                    bitmap = null
                }
                viewModel.setTheme(theme)
            },
            viewModel = viewModel,
            modifier = Modifier
                .drawWithContent {
                    graphicsLayer.record {
                        this@drawWithContent.drawContent()
                    }
                    drawLayer(graphicsLayer)
                }
        )

        bitmap?.let { bitmap ->
            Image(
                bitmap = bitmap,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .drawWithContent {
                        val path = Path()
                        // Draw a circle path
                        path.addOval(
                            Rect(
                                center = animationCenter,
                                radius = size.maxDimension * animationProgress.value
                            )
                        )
                        clipPath(path, clipOp = ClipOp.Difference) {
                            this@drawWithContent.drawContent()
                        }
                    },
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
private fun SettingsScreenImpl(
    onBack: () -> Unit,
    currentTheme: Theme,
    onThemeChange: (Theme, Offset) -> Unit,
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier,
) {
    ScreenWithTitle(
        title = stringResource(AppRes.string.settings_title),
        onBack = onBack,
        modifier = modifier,
    ) {
        Column {
            SectionHeading(stringResource(AppRes.string.settings_theme_title))
            ThemeSelector(currentTheme, onThemeChange)

            Spacer(Modifier.height(24.dp))

            if (LocalFlags.current.supportsNotifications) {
                val notificationSettings = viewModel.notificationSettings.collectAsStateWithLifecycle().value
                if (notificationSettings != null) {
                    SectionHeading(stringResource(AppRes.string.settings_notifications_title))
                    NotificationSettings(
                        notificationSettings = notificationSettings,
                        onChangeSettings = { newSettings ->
                            viewModel.setNotificationSettings(newSettings)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeading(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = KotlinConfTheme.typography.h2,
        modifier = modifier.semantics { heading() }
            .padding(top = 16.dp, bottom = 12.dp)
    )
}

private val themes = listOf(Theme.SYSTEM, Theme.DARK, Theme.LIGHT)

@Composable
private fun ThemeSelector(
    currentTheme: Theme,
    onThemeChange: (Theme, Offset) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.selectableGroup()
    ) {
        themes.forEach { theme ->
            ThemeBox(
                theme = theme,
                isSelected = currentTheme == theme,
                onClick = { position -> onThemeChange(theme, position) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun ThemeBox(
    theme: Theme,
    isSelected: Boolean,
    onClick: (Offset) -> Unit,
    modifier: Modifier = Modifier
) {
    var boxPosition by remember { mutableStateOf(Offset.Zero) }
    var clickPosition by remember { mutableStateOf(Offset.Zero) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .onGloballyPositioned { coordinates ->
                boxPosition = coordinates.positionInRoot()
            }
            .pointerInput(Unit) {

                awaitPointerEventScope {
                    while (true) {
                        val eventOnInitialPass = awaitPointerEvent(PointerEventPass.Initial)
                        val firstRelevantChange: PointerInputChange? =
                            eventOnInitialPass.changes.firstOrNull { it.pressed }
                        if (firstRelevantChange != null) {
                            println("Setting click pos to $clickPosition (based on ${firstRelevantChange.position})")
                            clickPosition = boxPosition + firstRelevantChange.position
                        }
                    }
                }
            }
            .selectable(
                selected = isSelected,
                onClick = {
                    println("onClick triggered with $clickPosition")
                    onClick(clickPosition)
                },
                role = Role.RadioButton,
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
            )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .then(
                    if (isSelected) {
                        Modifier
                            .border(
                                width = 2.dp,
                                color = KotlinConfTheme.colors.primaryBackground,
                                shape = RoundedCornerShape(12.dp)
                            )
                    } else Modifier
                )
                .padding(6.dp)
                .border(
                    width = 2.dp,
                    color = KotlinConfTheme.colors.strokePale,
                    shape = RoundedCornerShape(8.dp)
                )
                .clip(RoundedCornerShape(8.dp))
                .heightIn(max = 112.dp)
                .aspectRatio(1f)
        ) {
            Image(
                painter = painterResource(
                    when (theme) {
                        Theme.SYSTEM -> Res.drawable.theme_system
                        Theme.LIGHT -> Res.drawable.theme_light
                        Theme.DARK -> Res.drawable.theme_dark
                    }
                ),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(
                when (theme) {
                    Theme.SYSTEM -> Res.string.settings_theme_system
                    Theme.LIGHT -> Res.string.settings_theme_light
                    Theme.DARK -> Res.string.settings_theme_dark
                }
            ),
            style = KotlinConfTheme.typography.text2,
            color = KotlinConfTheme.colors.primaryText
        )
    }
}
