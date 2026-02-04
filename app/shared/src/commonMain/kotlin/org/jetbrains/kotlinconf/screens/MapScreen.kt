@file:OptIn(ExperimentalResourceApi::class)

package org.jetbrains.kotlinconf.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.generated.resources.Res
import org.jetbrains.kotlinconf.generated.resources.arrow_left_24
import org.jetbrains.kotlinconf.generated.resources.map_first_floor
import org.jetbrains.kotlinconf.generated.resources.map_ground_floor
import org.jetbrains.kotlinconf.generated.resources.map_how_to_find_venue
import org.jetbrains.kotlinconf.generated.resources.map_title
import org.jetbrains.kotlinconf.generated.resources.map_zoom_in
import org.jetbrains.kotlinconf.generated.resources.map_zoom_out
import org.jetbrains.kotlinconf.generated.resources.minus_24
import org.jetbrains.kotlinconf.generated.resources.navigate_back
import org.jetbrains.kotlinconf.generated.resources.plus_24
import org.jetbrains.kotlinconf.ui.components.ActionButton
import org.jetbrains.kotlinconf.ui.components.HorizontalDivider
import org.jetbrains.kotlinconf.ui.components.IconButton
import org.jetbrains.kotlinconf.ui.components.MainHeaderTitleBar
import org.jetbrains.kotlinconf.ui.components.Switcher
import org.jetbrains.kotlinconf.ui.components.TopMenuButton
import org.jetbrains.kotlinconf.ui.generated.resources.UiRes
import org.jetbrains.kotlinconf.ui.generated.resources.arrow_up_right_24
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.utils.topInsetPadding
import kotlin.math.sqrt

enum class Floor(
    val title: StringResource,
    val resourceLight: String,
    val resourceDark: String,
) {
    GROUND(
        Res.string.map_ground_floor,
        "files/ground-floor.svg",
        "files/ground-floor-dark.svg"
    ),
    FIRST(
        Res.string.map_first_floor,
        "files/first-floor.svg",
        "files/first-floor-dark.svg",
    );
}

val rooms = mapOf(
    "Room #173" to LocationInfo(Floor.FIRST, Offset(0.30f, 0.65f)),
    "Hall D3" to LocationInfo(Floor.GROUND, Offset(0.42f, 0.82f)),
    "Keynote room" to LocationInfo(Floor.GROUND, Offset(0.42f, 0.82f)),
    "Hall D2" to LocationInfo(Floor.GROUND, Offset(0.34f, 0.78f)),
    "Auditorium 15" to LocationInfo(Floor.GROUND, Offset(0.48f, 0.59f)),
    "Auditorium 11+12" to LocationInfo(Floor.FIRST, Offset(0.37f, 0.66f)),
    "Auditorium 10" to LocationInfo(Floor.FIRST, Offset(0.33f, 0.65f)),
    "Auditorium 10 (Lightning talks)" to LocationInfo(Floor.FIRST, Offset(0.33f, 0.65f)),
)
private val venue = LocationInfo(Floor.GROUND, Offset(0.4f, 0.69f))

/**
 * The [offset] here are values for centering the map on the given location.
 * Each value is an x, y pair, indicating where within the SVG image the location is, as a percentage.
 * 0.0f 0.0f focuses the map on the top left corner
 * 0.5f 0.5f focuses the map on the center of the image
 */
data class LocationInfo(
    val floor: Floor,
    val offset: Offset,
)

/**
 * Converts an offset containing x and y percentage locations within the SVG (0f..1f)
 * into real offset values from the middle of the SVG image.
 */
private fun Offset.asSvgOffset(svg: Svg) = Offset(
    svg.width / 2 - svg.width * this.x,
    svg.height / 2 - svg.height * this.y
)

private val Floor.resource: String
    @Composable get() = if (KotlinConfTheme.colors.isDark) resourceDark else resourceLight

@Composable
fun NestedMapScreen(
    roomName: String,
    onBack: (() -> Unit),
) {
    MapScreenImpl(
        location = rooms[roomName] ?: venue,
        onBack = onBack,
        modifier = Modifier.padding(topInsetPadding()),
        onHowToFindVenue = null,
    )
}

@Composable
fun MapScreen(
    onHowToFindVenue: () -> Unit,
) {
    MapScreenImpl(
        location = venue,
        onBack = null,
        onHowToFindVenue = onHowToFindVenue,
        modifier = Modifier.padding(topInsetPadding()),
    )
}

@Composable
private fun MapScreenImpl(
    location: LocationInfo,
    onBack: (() -> Unit)?,
    modifier: Modifier = Modifier,
    onHowToFindVenue: (() -> Unit)? = null,
) {
    var floorIndex by rememberSaveable { mutableStateOf(location.floor.ordinal) }
    val floor: Floor = remember(floorIndex) { Floor.entries[floorIndex] }
    val path = floor.resource
    val svg: Svg? by produceState(null, path) {
        value = Svg(Res.readBytes(path))
    }

    Column(modifier.fillMaxSize().background(color = KotlinConfTheme.colors.mainBackground)) {
        MainHeaderTitleBar(
            title = stringResource(Res.string.map_title),
            startContent = {
                if (onBack != null) {
                    TopMenuButton(
                        icon = Res.drawable.arrow_left_24,
                        contentDescription = stringResource(Res.string.navigate_back),
                        onClick = onBack,
                    )
                }
            }
        )
        HorizontalDivider(thickness = 1.dp, color = KotlinConfTheme.colors.strokePale)

        Switcher(
            items = Floor.entries.map { stringResource(it.title) },
            selectedIndex = floor.ordinal,
            onSelect = { index -> floorIndex = index },
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )

        svg?.let {
            val containerSize = LocalWindowInfo.current.containerSize
            val scaleAdjustment =
                remember { sqrt(containerSize.width / it.width * (containerSize.height / it.height)) }
            MapWithControls(
                svg = it,
                initialZoom = 2f * scaleAdjustment,
                initialOffset = location.offset.asSvgOffset(it),
                zoomRange = 1f..6f,
                onHowToFindVenue = onHowToFindVenue,
            )
        }
    }
}

@Composable
fun StaticMap(
    roomName: String?,
    zoom: Float = 2f,
    modifier: Modifier = Modifier,
) {
    val location = remember(roomName) { rooms[roomName] ?: venue }
    val path = location.floor.resource
    val svg: Svg? by produceState(null, path) {
        value = Svg(Res.readBytes(path))
    }

    svg?.let {
        val state = rememberMapState(zoom, location.offset.asSvgOffset(it))
        Map(
            svg = it,
            state = state,
            modifier = modifier,
            interactive = false,
        )
    }
}

private class MapState(
    initialZoom: Float,
    initialOffset: Offset,
) {
    val scale = Animatable(initialZoom)
    val offsetX = Animatable(initialOffset.x)
    val offsetY = Animatable(initialOffset.y)
}

private val mapStateSaver = Saver<MapState, List<Float>>(
    save = { listOf(it.scale.value, it.offsetX.value, it.offsetY.value) },
    restore = { values ->
        val (scale, offsetX, offsetY) = values
        MapState(scale, Offset(offsetX, offsetY))
    }
)

@Composable
private fun rememberMapState(initialZoom: Float, initialOffset: Offset): MapState {
    return rememberSaveable(saver = mapStateSaver) {
        MapState(initialZoom, initialOffset)
    }
}

@Composable
private fun MapWithControls(
    svg: Svg,
    initialZoom: Float = 1f,
    initialOffset: Offset = Offset.Zero,
    zoomRange: ClosedFloatingPointRange<Float> = 0.5f..5f,
    onHowToFindVenue: (() -> Unit)? = null,
) {
    val state = rememberMapState(initialZoom, initialOffset)
    val scope = rememberCoroutineScope()
    val spec = remember { tween<Float>(500, easing = EaseOutCubic) }

    Box(Modifier.fillMaxSize()) {
        Map(
            svg = svg,
            state = state,
            zoomRange = zoomRange,
            interactive = true,
        )

        val buttonEdgePadding = 12.dp

        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = buttonEdgePadding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            IconButton(
                icon = Res.drawable.plus_24,
                enabled = state.scale.value < zoomRange.endInclusive,
                onClick = {
                    scope.launch {
                        state.scale.animateTo(
                            targetValue = (state.scale.value * 2f).coerceIn(zoomRange),
                            animationSpec = spec,
                        )
                    }
                },
                contentDescription = stringResource(Res.string.map_zoom_in),
            )
            IconButton(
                icon = Res.drawable.minus_24,
                enabled = state.scale.value > zoomRange.start,
                onClick = {
                    scope.launch {
                        state.scale.animateTo(
                            targetValue = (state.scale.value / 2f).coerceIn(zoomRange),
                            animationSpec = spec,
                        )
                    }
                },
                contentDescription = stringResource(Res.string.map_zoom_out),
            )
        }

        if (onHowToFindVenue != null) {
            ActionButton(
                label = stringResource(Res.string.map_how_to_find_venue),
                icon = UiRes.drawable.arrow_up_right_24,
                onClick = onHowToFindVenue,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = buttonEdgePadding)
            )
        }
    }
}

@Composable
private fun Map(
    svg: Svg,
    state: MapState,
    modifier: Modifier = Modifier,
    zoomRange: ClosedFloatingPointRange<Float> = 0.5f..5f,
    interactive: Boolean = true,
) {
    val scope = rememberCoroutineScope()
    val spec = remember { tween<Float>(500, easing = EaseOutCubic) }

    val validOffsetX = (-svg.width * 0.5f)..(svg.width * 0.5f)
    val validOffsetY = (-svg.height * 0.5f)..(svg.height * 0.5f)

    val interactiveModifiers = if (!interactive) {
        Modifier
    } else {
        Modifier
            .transformable(rememberTransformableState { zoomChange, panChange, _ ->
                scope.launch {
                    state.scale.snapTo((state.scale.value * zoomChange).coerceIn(zoomRange))
                    state.offsetX.snapTo(
                        (state.offsetX.value + panChange.x / state.scale.value).coerceIn(
                            validOffsetX
                        )
                    )
                    state.offsetY.snapTo(
                        (state.offsetY.value + panChange.y / state.scale.value).coerceIn(
                            validOffsetY
                        )
                    )
                }
            })
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = { tapOffset ->
                        if (state.scale.value >= zoomRange.endInclusive - 0.1f) {
                            scope.launch { state.scale.animateTo(state.scale.value / 2f, spec) }
                        } else {
                            val newScale = (state.scale.value * 2f).coerceIn(zoomRange)

                            val newOffsetX =
                                (state.offsetX.value + (size.width / 2 - tapOffset.x) / 2 / state.scale.value)
                                    .coerceIn(validOffsetX)
                            val newOffsetY =
                                (state.offsetY.value + (size.height / 2 - tapOffset.y) / 2 / state.scale.value)
                                    .coerceIn(validOffsetY)

                            scope.launch {
                                async { state.scale.animateTo(newScale, spec) }
                                async { state.offsetX.animateTo(newOffsetX, spec) }
                                async { state.offsetY.animateTo(newOffsetY, spec) }
                            }
                        }
                    }
                )
            }
    }

    Canvas(
        modifier
            .fillMaxSize()
            .clipToBounds()
            .then(interactiveModifiers)
    ) {
        translate(
            left = state.offsetX.value + (size.width - svg.width) / 2,
            top = state.offsetY.value + (size.height - svg.height) / 2,
        ) {
            scale(
                scale = state.scale.value,
                pivot = Offset(
                    svg.width / 2 - state.offsetX.value,
                    svg.height / 2 - state.offsetY.value
                ),
            ) {
                svg.renderTo(this)
            }
        }
    }
}

expect class Svg(svgBytes: ByteArray) {
    val width: Float
    val height: Float

    fun renderTo(scope: DrawScope)
}
