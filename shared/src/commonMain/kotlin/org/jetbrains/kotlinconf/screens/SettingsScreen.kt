package org.jetbrains.kotlinconf.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlinconfapp.shared.generated.resources.Res
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
import org.jetbrains.kotlinconf.ConferenceService
import org.jetbrains.kotlinconf.NotificationSettings
import org.jetbrains.kotlinconf.ScreenWithTitle
import org.jetbrains.kotlinconf.Theme
import org.jetbrains.kotlinconf.ui.components.Divider
import org.jetbrains.kotlinconf.ui.components.StyledText
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import kotlinconfapp.shared.generated.resources.Res as AppRes

@Composable
fun SettingsScreen(
    service: ConferenceService,
    onBack: () -> Unit,
    onNotificationSettingsChange: (NotificationSettings) -> Unit,
) {
    val graphicsLayer = rememberGraphicsLayer()
    val scope = rememberCoroutineScope()
    var bitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var bitmapVisibility = remember { Animatable(1f) }

    val currentTheme by service.theme.collectAsState()

    Box(Modifier.fillMaxSize()) {
        SettingsScreenImpl(
            onBack = onBack,
            currentTheme = currentTheme,
            onThemeChange = { theme ->
                scope.launch {
                    bitmap = graphicsLayer.toImageBitmap()
                    bitmapVisibility.snapTo(1f)
                    bitmapVisibility.animateTo(0f, tween(500))
                    bitmap = null
                }
                service.setTheme(theme)
            },
            onNotificationSettingsChange = onNotificationSettingsChange,
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
                    .fillMaxHeight(fraction = bitmapVisibility.value)
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                contentScale = ContentScale.Crop,
                alignment = Alignment.BottomCenter
            )
        }
    }
}

@Composable
private fun SettingsScreenImpl(
    onBack: () -> Unit,
    currentTheme: Theme,
    onThemeChange: (Theme) -> Unit,
    onNotificationSettingsChange: (NotificationSettings) -> Unit,
    modifier: Modifier = Modifier,
) {
    ScreenWithTitle(
        title = stringResource(AppRes.string.settings_title),
        onBack = onBack,
        modifier = modifier,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StyledText(
                    text = stringResource(AppRes.string.settings_theme_title),
                    style = KotlinConfTheme.typography.h2
                )
                ThemeSelector(
                    currentTheme = currentTheme,
                    onThemeChange = onThemeChange,
                )
            }

            Divider(thickness = 1.dp, color = KotlinConfTheme.colors.strokePale)

            val notiState = rememberNotificationSettingsState()
            NotificationSettings(notiState)

            val model = notiState.model
            LaunchedEffect(model) {
                onNotificationSettingsChange(model)
            }
        }
    }
}

private val themes = listOf(Theme.SYSTEM, Theme.DARK, Theme.LIGHT)

@Composable
private fun ThemeSelector(
    currentTheme: Theme,
    onThemeChange: (Theme) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        themes.forEach { theme ->
            ThemeBox(
                theme = theme,
                isSelected = currentTheme == theme,
                onClick = { onThemeChange(theme) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun ThemeBox(
    theme: Theme,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.semantics(mergeDescendants = true) {},
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
                .clickable(onClick = onClick)
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
        StyledText(
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

