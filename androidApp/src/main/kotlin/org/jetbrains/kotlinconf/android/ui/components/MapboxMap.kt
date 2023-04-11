package org.jetbrains.kotlinconf.android.ui.components

import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.viewinterop.*
import com.mapbox.geojson.*
import com.mapbox.maps.*
import com.mapbox.maps.plugin.gestures.*

val EXHIBITION = CameraOptions.Builder()
    .center(Point.fromLngLat(4.8958040599143544, 52.374888312974583))
    .zoom(18.5)
    .bearing(126.69985108898653)
    .build()

val GROUD_ROOMS = CameraOptions.Builder()
    .center(Point.fromLngLat(4.8966121025625, 52.375221940889247))
    .zoom(18.5)
    .bearing(126.69985108898653)
    .build()

val FLOOR1_ROOMS = CameraOptions.Builder()
    .center(Point.fromLngLat(4.8962740928733979, 52.37500901272729))
    .zoom(18.5)
    .bearing(126.69985108898653)
    .build()

val BERLAGE_ROOM = CameraOptions.Builder()
    .center(Point.fromLngLat(4.895626846724042, 52.374625105863458))
    .zoom(18.5)
    .bearing(126.69985108898653)
    .build()

val VERWEY_KAMER = CameraOptions.Builder()
    .center(Point.fromLngLat(4.8960855441916067, 52.374783815163447))
    .zoom(18.5)
    .bearing(126.69985108898653)
    .build()

@Composable
fun mapByLocation(value: String): String = when (value.lowercase()) {
    "administratiezaal" -> FLOORS("FLOOR 1")
    "berlage zaal" -> FLOORS("FLOOR 1")
    "veilingzaal" -> FLOORS("FLOOR 1")
    "graanbeurszaal" -> FLOORS("FLOOR 0")
    "mendes da costa kamer" -> FLOORS("FLOOR 1")
    "effectenbeurszaal" -> FLOORS("FLOOR 0")
    "verwey kamer" -> FLOORS("FLOOR 1")
    else -> FLOORS("FLOOR 0")
}

fun roomByLocation(value: String): CameraOptions? = when (value.lowercase()) {
    "exhibition" -> EXHIBITION
    "floor 1" -> FLOOR1_ROOMS
    "administratiezaal" -> FLOOR1_ROOMS
    "berlage zaal" -> BERLAGE_ROOM
    "veilingzaal" -> FLOOR1_ROOMS
    "graanbeurszaal" -> GROUD_ROOMS
    "mendes da costa kamer" -> FLOOR1_ROOMS
    "effectenbeurszaal" -> GROUD_ROOMS
    "verwey kamer" -> VERWEY_KAMER
    else -> EXHIBITION
}

@Composable
fun MapBoxMap(
    uri: String,
    cameraOptions: CameraOptions? = null,
    modifier: Modifier = Modifier,
    onClick: (label: String, displayName: String, description: String) -> Unit = { _, _, _ -> }
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            MapView(context).apply {
                getMapboxMap().loadStyleUri(uri)
                if (cameraOptions != null) {
                    getMapboxMap().setCamera(cameraOptions)
                }

                getMapboxMap().addOnMapClickListener {
                    val coordinate = getMapboxMap().pixelForCoordinate(it)
                    getMapboxMap().queryRenderedFeatures(
                        RenderedQueryGeometry(coordinate),
                        RenderedQueryOptions(null, null)
                    ) {
                        if (it.error != null) {
                            return@queryRenderedFeatures
                        }

                        val click = it.value?.firstNotNullOfOrNull { query ->
                            query.feature.getStringProperty("Click")
                        } != null

                        val name = it.value?.firstNotNullOfOrNull { query ->
                            query.feature.getStringProperty("Name")
                        } ?: ""

                        val description = it.value?.firstNotNullOfOrNull { query ->
                            query.feature.getStringProperty("Description")
                        } ?: ""

                        val displayName = it.value?.firstNotNullOfOrNull { query ->
                            query.feature.getStringProperty("DisplayName")
                        }?.takeIf { it.isNotBlank() } ?: name

                        if (click && name.isNotBlank()) {
                            onClick(name, displayName, description)
                        }
                    }
                    true
                }
            }
        }, update = {
            it.getMapboxMap().loadStyleUri(uri)
        }
    )
}

@Composable
internal fun FLOORS(value: String) = if (MaterialTheme.colors.isLight) {
    when (value.lowercase()) {
        "floor -1" -> "mapbox://styles/grigza/clfbf5irz001601o6unu59cwt"
        "floor 0" -> "mapbox://styles/grigza/clfbezi94001501o6al2kguwx"
        "floor 1" -> "mapbox://styles/grigza/clfbej9kp001z01ln5h8philh"
        else -> "mapbox://styles/grigza/clfbezi94001501o6al2kguwx"
    }
} else {
    when (value.lowercase()) {
        "floor -1" -> "mapbox://styles/grigza/cldbumgwj000401ox12d8ufs5"
        "floor 0" -> "mapbox://styles/grigza/cl9yddkcm006414rh0a39ijh4"
        "floor 1" -> "mapbox://styles/grigza/cldbra01x004q01p5ok5zpkd6"
        else -> "mapbox://styles/grigza/cl9yddkcm006414rh0a39ijh4"
    }
}