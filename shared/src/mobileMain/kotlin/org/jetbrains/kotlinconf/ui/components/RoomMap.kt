package org.jetbrains.kotlinconf.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.dp
import kotlinconfapp.shared.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.kotlinconf.ui.Floor
import org.jetbrains.kotlinconf.ui.Svg
import org.jetbrains.kotlinconf.ui.resource

enum class Room(val title: String, val floor: Floor, val offset: Offset, val scale: Float) {
    ROOM173("ROOM 173", Floor.FIRST, Offset(0f, 0f), 1f), ROOM176(
        "ROOM 176", Floor.FIRST, Offset(0f, 0f), 1f
    ),
    ROOM178("ROOM 178", Floor.FIRST, Offset(0f, 0f), 1f), ROOM179(
        "ROOM 179", Floor.FIRST, Offset(0f, 0f), 1f
    ),
    ROOM180("ROOM 180", Floor.FIRST, Offset(0f, 0f), 1f), ROOM181(
        "ROOM 181", Floor.FIRST, Offset(0f, 0f), 1f
    ),
    ROOM182("ROOM 182", Floor.FIRST, Offset(0f, 0f), 1f), HALL1(
        "HALL 1", Floor.FIRST, Offset(0f, 0f), 1f
    ),
    AUDITORIUM15("AUDITORIUM 15", Floor.SECOND, Offset(0f, 0f), 1f), AUDITORIUM12(
        "AUDITORIUM 12", Floor.SECOND, Offset(0f, 0f), 1f
    ),
    AUDITORIUM11(
        "AUDITORIUM 11", Floor.SECOND, Offset(0f, 0f), 1f
    ),
    AUDITORIUM10("AUDITORIUM 10 (LIGHTNING TALKS)", Floor.SECOND, Offset(0f, 0f), 1f), ;

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

    Canvas(Modifier.padding(8.dp).fillMaxWidth().height(288.dp)) {
        svg?.renderTo(this)
    }
}