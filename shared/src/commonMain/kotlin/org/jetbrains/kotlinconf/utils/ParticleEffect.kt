package org.jetbrains.kotlinconf.utils

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.random.Random

class Particle(
    val glyph: String,
    x: Float,
    y: Float,
    var vx: Float,
    var vy: Float,
    alpha: Float,
    val sizeSp: Float,
) {
    var x by mutableStateOf(x)
    var y by mutableStateOf(y)
    var alpha by mutableStateOf(alpha)

    val rotationDeg: Float = if (vx != 0f || vy != 0f) {
        (atan2(vy, vx) * 180f / PI.toFloat()) + 90f
    } else {
        0f
    }
}

suspend fun animateParticle(
    particle: Particle,
    particles: MutableList<Particle>,
    containerSize: IntSize,
) {
    val gravity = 1800f
    var lastTimeNanos: Long? = null

    while (true) {
        val now = withFrameNanos { it }
        val prev = lastTimeNanos
        lastTimeNanos = now
        if (prev == null) continue
        val dt = (now - prev) / 1_000_000_000f

        particle.vy += gravity * dt
        particle.x += particle.vx * dt
        particle.y += particle.vy * dt

        if (containerSize.height > 0) {
            if (particle.y > containerSize.height * 0.95f) {
                particle.alpha = ((containerSize.height.toFloat() - particle.y) / (containerSize.height * 0.05f)).coerceIn(0f, 1f)
            }

            if (particle.y > containerSize.height + particle.sizeSp * 24) {
                particles.remove(particle)
                break
            }
        }
    }
}

class ParticleSystem internal constructor(
    internal val scope: CoroutineScope,
    val containerSize: IntSize,
) {
    internal val particles = mutableStateListOf<Particle>()

    fun spawnAt(start: Offset, count: Int, glyph: () -> String) {
        repeat(count) {
            val particle = Particle(
                glyph = glyph(),
                x = start.x,
                y = start.y,
                vx = Random.nextFloat() * 700f - 350f,
                vy = -1600f + (Random.nextFloat() * 600f),
                alpha = 1f,
                sizeSp = 24f + Random.nextFloat() * 8f,
            )
            particles += particle
            scope.launch {
                animateParticle(particle, particles, containerSize)
            }
        }
    }
}

@Composable
fun rememberParticleSystem(): ParticleSystem {
    val scope = rememberCoroutineScope()
    val info = LocalWindowInfo.current
    return remember(scope, info) { ParticleSystem(scope, info.containerSize) }
}

@Composable
fun ParticleOverlay(state: ParticleSystem, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        val textMeasurer = rememberTextMeasurer()

        Canvas(modifier = Modifier.fillMaxSize()) {
            state.particles.forEach { p ->
                this.withTransform({
                    this.translate(left = p.x, top = p.y)
                    this.rotate(degrees = p.rotationDeg, pivot = Offset.Zero)
                }) {
                    val layout = textMeasurer.measure(
                        text = AnnotatedString(p.glyph),
                        style = TextStyle(fontSize = p.sizeSp.sp)
                    )
                    this.drawText(
                        textLayoutResult = layout,
                        alpha = p.alpha
                    )
                }
            }
        }
    }
}

