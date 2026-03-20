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
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.MapData
import org.jetbrains.kotlinconf.RoomData
import org.jetbrains.kotlinconf.generated.resources.Res
import org.jetbrains.kotlinconf.generated.resources.arrow_left_24
import org.jetbrains.kotlinconf.generated.resources.map_error_no_data
import org.jetbrains.kotlinconf.generated.resources.map_how_to_find_venue
import org.jetbrains.kotlinconf.generated.resources.map_title
import org.jetbrains.kotlinconf.generated.resources.map_zoom_in
import org.jetbrains.kotlinconf.generated.resources.map_zoom_out
import org.jetbrains.kotlinconf.generated.resources.minus_24
import org.jetbrains.kotlinconf.generated.resources.navigate_back
import org.jetbrains.kotlinconf.generated.resources.plus_24
import org.jetbrains.kotlinconf.ui.components.HorizontalDivider
import org.jetbrains.kotlinconf.ui.components.MainHeaderTitleBar
import org.jetbrains.kotlinconf.ui.components.OverlayIconButton
import org.jetbrains.kotlinconf.ui.components.OverlayTextButton
import org.jetbrains.kotlinconf.ui.components.Switcher
import org.jetbrains.kotlinconf.ui.components.TopMenuButton
import org.jetbrains.kotlinconf.ui.generated.resources.UiRes
import org.jetbrains.kotlinconf.ui.generated.resources.arrow_up_right_24
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.utils.ErrorLoadingContent
import org.jetbrains.kotlinconf.utils.LocalWindowSize
import org.jetbrains.kotlinconf.utils.WindowSize
import org.jetbrains.kotlinconf.utils.bottomInsetPadding
import org.jetbrains.kotlinconf.utils.plus
import org.jetbrains.kotlinconf.utils.topInsetPadding
import kotlin.math.sqrt

/**
 * Converts an offset containing x and y percentage locations within the SVG (0f..1f)
 * into real offset values from the middle of the SVG image.
 */
private fun Offset.asSvgOffset(svg: Svg) = Offset(
    svg.width / 2 - svg.width * this.x,
    svg.height / 2 - svg.height * this.y
)

@Composable
fun NestedMapScreen(
    roomName: String,
    onBack: (() -> Unit),
    viewModel: MapViewModel = metroViewModel(),
) {
    MapScreenImpl(
        roomName = roomName,
        onBack = onBack,
        viewModel = viewModel,
        onHowToFindVenue = null, // Don't show this feature in the nested screen
        modifier = Modifier.padding(topInsetPadding()),
    )
}

@Composable
fun MapScreen(
    onHowToFindVenue: (String) -> Unit,
    viewModel: MapViewModel = metroViewModel(),
) {
    MapScreenImpl(
        roomName = null,
        onBack = null,
        onHowToFindVenue = onHowToFindVenue,
        viewModel = viewModel,
        modifier = Modifier.padding(topInsetPadding()),
    )
}

@Composable
private fun MapScreenImpl(
    roomName: String?,
    onBack: (() -> Unit)?,
    viewModel: MapViewModel,
    onHowToFindVenue: ((String) -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value
    val useNativeNavigation = viewModel.useNativeNavigation.collectAsStateWithLifecycle(false).value

    Column(modifier.fillMaxSize().background(color = KotlinConfTheme.colors.mainBackground)) {
        if (!useNativeNavigation) {
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
        }

        ErrorLoadingContent(
            state = state,
            errorMessage = stringResource(Res.string.map_error_no_data),
            onRetry = { viewModel.refresh() },
            modifier = Modifier.fillMaxSize(),
        ) { content ->
            val mapData = content.mapData
            val room = roomName?.let { mapData.rooms[it] }
            val initialFloorIndex = room?.floorIndex ?: mapData.defaultFloorIndex
            val initialOffset = Offset(
                room?.offsetX ?: mapData.defaultOffsetX,
                room?.offsetY ?: mapData.defaultOffsetY,
            )

            var floorIndex by rememberSaveable { mutableStateOf(initialFloorIndex) }
            val floor = mapData.floors.getOrNull(floorIndex) ?: return@ErrorLoadingContent

            val svgPath =
                if (KotlinConfTheme.colors.isDark) floor.svgPathDark else floor.svgPathLight
            val svgData = content.svgsByPath[svgPath] ?: return@ErrorLoadingContent

            Column(Modifier.fillMaxSize()) {
                Switcher(
                    items = mapData.floors.map { it.name },
                    shortItems = mapData.floors.map { it.shortName },
                    selectedIndex = floorIndex,
                    onSelect = { index -> floorIndex = index },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                )

                val svg = remember(svgData) { Svg(svgData) }
                val containerSize = LocalWindowInfo.current.containerSize
                val scaleAdjustment =
                    remember { sqrt(containerSize.width / svg.width * (containerSize.height / svg.height)) }

                val venueAddress = mapData.venueAddress
                MapWithControls(
                    svg = svg,
                    initialZoom = mapData.initialZoom * scaleAdjustment,
                    initialOffset = initialOffset.asSvgOffset(svg),
                    zoomRange = (mapData.minZoom * scaleAdjustment)..(mapData.maxZoom * scaleAdjustment),
                    onHowToFindVenue = if (onHowToFindVenue != null && venueAddress != null) {
                        { onHowToFindVenue(venueAddress) }
                    } else {
                        null
                    },
                )
            }
        }
    }
}

@Composable
fun StaticMap(
    modifier: Modifier = Modifier,
    mapData: MapData,
    room: RoomData,
    svgsByPath: Map<String, String>,
) {
    val floorIndex = room.floorIndex
    val offset = Offset(room.offsetX, room.offsetY)

    val floor = mapData.floors[floorIndex]
    val svgData =
        svgsByPath[if (KotlinConfTheme.colors.isDark) floor.svgPathDark else floor.svgPathLight]
            ?: return
    val svg = remember(svgData) { Svg(svgData) }

    BoxWithConstraints {
        val containerSize = constraints
        val scaleAdjustment = containerSize.maxWidth / svg.width
        val initialZoom = scaleAdjustment * mapData.initialZoom * 1.5f

        Map(
            svg = svg,
            state = rememberMapState(initialZoom, offset.asSvgOffset(svg)),
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
    initialZoom: Float,
    initialOffset: Offset,
    zoomRange: ClosedFloatingPointRange<Float>,
    onHowToFindVenue: (() -> Unit)?,
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
            OverlayIconButton(
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
            OverlayIconButton(
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
            val isLargeScreen = LocalWindowSize.current != WindowSize.Compact
            val extraPadding = if (isLargeScreen) bottomInsetPadding() else PaddingValues(0.dp)
            OverlayTextButton(
                label = stringResource(Res.string.map_how_to_find_venue),
                icon = UiRes.drawable.arrow_up_right_24,
                onClick = onHowToFindVenue,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(PaddingValues(bottom = buttonEdgePadding) + extraPadding)
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
                                launch { state.scale.animateTo(newScale, spec) }
                                launch { state.offsetX.animateTo(newOffsetX, spec) }
                                launch { state.offsetY.animateTo(newOffsetY, spec) }
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

expect class Svg(svgString: String) {
    val width: Float
    val height: Float

    fun renderTo(scope: DrawScope)
}
