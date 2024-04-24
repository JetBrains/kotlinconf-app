package org.jetbrains.kotlinconf.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.dp
import kotlinconfapp.shared.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.kotlinconf.ui.Floor
import org.jetbrains.kotlinconf.ui.Svg
import org.jetbrains.kotlinconf.ui.resource

enum class Room(val title: String, val floor: Floor, val x: Float, val y: Float, val zoom: Float? = null) {
    ROOM173("ROOM 173", Floor.FIRST, 550f, 900f),
    ROOM176("ROOM 176", Floor.FIRST, 550f, 900f),
    ROOM178("ROOM 178", Floor.FIRST, 550f, 900f),
    ROOM179("ROOM 179", Floor.FIRST, 550f, 900f),
    ROOM180("ROOM 180", Floor.FIRST, 550f, 900f),
    ROOM181("ROOM 181", Floor.FIRST, 550f, 900f),
    HALL_A("HALL A", Floor.GROUND, 850f, 1150f),
    AUDITORIUM15("AUDITORIUM 15", Floor.FIRST, 800f, 950f),
    AUDITORIUM12("AUDITORIUM 12", Floor.FIRST, 800f, 950f),
    AUDITORIUM11("AUDITORIUM 11", Floor.FIRST, 800f, 950f),
    AUDITORIUM10("AUDITORIUM 10 (LIGHTNING TALKS)", Floor.FIRST, 800f, 950f),
    EXHIBITION("EXHIBITION", Floor.GROUND, 1050f, 750f, 0.89f);

    companion object {
        fun forName(location: String): Room? {
            return entries.firstOrNull { it.title.equals(location, ignoreCase = true) }
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun RoomMap(room: Room) {
    var svg: Svg? by remember { mutableStateOf(null) }

    val path = room.floor.resource
    LaunchedEffect(path) {
        svg = Svg(Res.readBytes(path))
    }

    Box(Modifier.fillMaxWidth().height(343.dp).clipToBounds()) {
        Canvas(modifier = Modifier) {
            translate(-room.x, -room.y) {
                val zoom = room.zoom
                if (zoom != null) {
                    scale(zoom) {
                        svg?.renderTo(this)
                    }
                } else {
                    svg?.renderTo(this)
                }
            }
        }
    }
}