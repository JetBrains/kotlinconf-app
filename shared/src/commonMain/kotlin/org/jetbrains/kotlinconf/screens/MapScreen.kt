@file:OptIn(ExperimentalResourceApi::class)

package org.jetbrains.kotlinconf.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
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
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.map_first_floor
import kotlinconfapp.shared.generated.resources.map_ground_floor
import kotlinconfapp.shared.generated.resources.map_title
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.ui.components.Divider
import org.jetbrains.kotlinconf.ui.components.MainHeaderTitleBar
import org.jetbrains.kotlinconf.ui.components.Switcher
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme

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
    "Room #173" to LocationInfo(Floor.FIRST, Offset(0.3045643154f, 0.6523076923f)),
    "Hall D3" to LocationInfo(Floor.GROUND, Offset(0.4157676349f, 0.8164102564f)),
    "Hall D2" to LocationInfo(Floor.GROUND, Offset(0.3394190871f, 0.7753846154f)),
    "Auditorium 15" to LocationInfo(Floor.GROUND, Offset(0.4771784232f, 0.5928205128f)),
    "Auditorium 11+12" to LocationInfo(Floor.FIRST, Offset(0.3692946058f, 0.6564102564f)),
    "Auditorium 10 (Lightning talks)" to LocationInfo(Floor.FIRST, Offset(0.3286307054f, 0.6543589744f)),
)
private val venue = LocationInfo(Floor.GROUND, Offset(0.4174273859f, 0.7158974359f))

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
fun MapScreen(roomName: String?) {
    val location = remember(roomName) { rooms[roomName] ?: venue }
    var floorIndex by rememberSaveable { mutableStateOf(location.floor.ordinal) }
    val floor: Floor = remember(floorIndex) { Floor.entries[floorIndex] }
    val path = floor.resource
    val svg: Svg? by produceState(null, path) {
        value = Svg(Res.readBytes(path))
    }

    Column(Modifier.fillMaxSize().background(color = KotlinConfTheme.colors.mainBackground)) {
        MainHeaderTitleBar(
            title = stringResource(Res.string.map_title),
        )
        Divider(thickness = 1.dp, color = KotlinConfTheme.colors.strokePale)

        Switcher(
            items = Floor.entries.map { stringResource(it.title) },
            selectedIndex = floor.ordinal,
            onSelect = { index -> floorIndex = index },
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )

        svg?.let {
            Map(
                svg = it,
                initialZoom = 2f,
                initialOffset = location.offset.asSvgOffset(it),
                zoomRange = 1f..6f
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
        Map(
            svg = it,
            modifier = modifier,
            initialZoom = zoom,
            initialOffset = location.offset.asSvgOffset(it),
            interactive = false,
        )
    }
}

@Composable
private fun Map(
    svg: Svg,
    modifier: Modifier = Modifier,
    initialZoom: Float = 1f,
    initialOffset: Offset = Offset.Zero,
    zoomRange: ClosedFloatingPointRange<Float> = 0.5f..5f,
    interactive: Boolean = true,
) {
    val scale = rememberSaveable(saver = FloatAnimSaver) { Animatable(initialZoom) }
    val offsetX = rememberSaveable(saver = FloatAnimSaver) { Animatable(initialOffset.x) }
    val offsetY = rememberSaveable(saver = FloatAnimSaver) { Animatable(initialOffset.y) }

    val scope = rememberCoroutineScope()

    val validOffsetX = (-svg.width * 0.5f)..(svg.height * 0.5f)
    val validOffsetY = (-svg.height * 0.5f)..(svg.height * 0.5f)

    val interactiveModifiers = if (!interactive) {
        Modifier
    } else {
        Modifier
            .transformable(rememberTransformableState { zoomChange, panChange, _ ->
                if (!interactive) return@rememberTransformableState

                scope.launch {
                    scale.snapTo((scale.value * zoomChange).coerceIn(zoomRange))
                    offsetX.snapTo((offsetX.value + panChange.x / scale.value).coerceIn(validOffsetX))
                    offsetY.snapTo((offsetY.value + panChange.y / scale.value).coerceIn(validOffsetY))
                }
            })
            .pointerInput(Unit) {
                if (!interactive) return@pointerInput

                detectTapGestures(
                    onDoubleTap = { tapOffset ->
                        val spec = tween<Float>(500, easing = EaseOutCubic)

                        if (scale.value >= zoomRange.endInclusive - 0.1f) {
                            scope.launch { scale.animateTo(initialZoom, spec) }
                        } else {
                            val newScale = (scale.value * 2f).coerceIn(zoomRange)

                            val newOffsetX = (offsetX.value + (size.width / 2 - tapOffset.x) / 2 / scale.value)
                                .coerceIn(validOffsetX)
                            val newOffsetY = (offsetY.value + (size.height / 2 - tapOffset.y) / 2 / scale.value)
                                .coerceIn(validOffsetY)

                            scope.launch {
                                async { scale.animateTo(newScale, spec) }
                                async { offsetX.animateTo(newOffsetX, spec) }
                                async { offsetY.animateTo(newOffsetY, spec) }
                            }
                        }
                    }
                )
            }
    }

    Canvas(
        modifier
            .clipToBounds()
            .fillMaxSize()
            .then(interactiveModifiers)
    ) {
        translate(
            left = offsetX.value + (size.width - svg.width) / 2,
            top = offsetY.value + (size.height - svg.height) / 2,
        ) {
            scale(
                scale = scale.value,
                pivot = Offset(svg.width / 2 - offsetX.value, svg.height / 2 - offsetY.value),
            ) {
                svg.renderTo(this)
            }
        }
    }
}

private object FloatAnimSaver : Saver<Animatable<Float, AnimationVector1D>, Float> {
    override fun restore(value: Float): Animatable<Float, AnimationVector1D>? = Animatable(value)
    override fun SaverScope.save(value: Animatable<Float, AnimationVector1D>): Float? = value.value
}

expect class Svg(svgBytes: ByteArray) {
    val width: Float
    val height: Float

    fun renderTo(scope: DrawScope)
}
