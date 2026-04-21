package org.jetbrains.kotlinconf.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.EaseInCubic
import androidx.compose.animation.core.EaseInExpo
import androidx.compose.animation.core.EaseOutBounce
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.EaseOutExpo
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.layout
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.kotlinconf.ui.generated.resources.UiRes
import org.jetbrains.kotlinconf.ui.generated.resources.kodee_emotion_positive
import org.jetbrains.kotlinconf.ui.generated.resources.kodee_small_positive_filled
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui.theme.PreviewHelper
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

private const val TwoPi: Float = 6.2831855f

private val PartyPalette = listOf(
    Color(0xFF8F00E7),
    Color(0xFFC202D7),
    Color(0xFFE00189),
    Color(0xFFFF5A13),
    Color(0xFFFFD12E),
    Color(0xFF00D1FF),
)

private fun partyLightColor(color: Color, darkTheme: Boolean): Color =
    if (darkTheme) color else lerp(color, Color.White, 0.38f)

private fun partyLightAlpha(alpha: Float, darkTheme: Boolean): Float =
    if (darkTheme) alpha else (alpha * 1.08f).coerceAtMost(1f)

private fun partyLightScrimAlpha(alpha: Float, darkTheme: Boolean): Float =
    if (darkTheme) 0f else (alpha * 0.32f).coerceAtMost(0.26f)

@Composable
private fun PartyAnimation(
    modifier: Modifier = Modifier,
    intensity: () -> Float = { 0f },
    cannon: () -> Float = { 1f },
) {
    Box(modifier = modifier.clipToBounds()) {
        PartyLights(Modifier.fillMaxSize(), intensity)
        PartyKodees(Modifier.fillMaxSize(), intensity)
        Confetti(Modifier.fillMaxSize(), intensity)
        KodeeCannon(Modifier.fillMaxSize(), cannon)
    }
}

private const val CannonMinTapsAtMax = 4
private const val CannonResetIntensity = 0.5f
private const val CannonDurationMs = 2200
private val CannonCooldown = 10.seconds

@Composable
fun PartyEventCard(
    active: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val intensity = remember { Animatable(0f) }
    val cannon = remember { Animatable(1f) }
    var cannonReady by remember { mutableStateOf(true) }
    var tapsAtMax by remember { mutableStateOf(0) }
    val interactionSource = remember { MutableInteractionSource() }
    val intensityProvider = remember { { intensity.value } }
    val cannonProvider = remember { { cannon.value } }

    val onPartyTap = remember {
        {
            val pre = intensity.value
            val target = (pre + 0.35f).coerceAtMost(1f)

            if (pre < CannonResetIntensity) tapsAtMax = 0
            if (target >= 1f) tapsAtMax += 1

            coroutineScope.launch {
                intensity.animateTo(target, tween(160))
                intensity.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(
                        durationMillis = (12000 + target * 18000).toInt(),
                        easing = EaseOutCubic,
                    ),
                )
            }

            if (tapsAtMax >= CannonMinTapsAtMax && cannonReady) {
                cannonReady = false
                tapsAtMax = 0
                coroutineScope.launch {
                    cannon.snapTo(0f)
                    cannon.animateTo(1f, tween(CannonDurationMs))
                }
                coroutineScope.launch {
                    delay(CannonCooldown)
                    cannonReady = true
                }
            }
            Unit
        }
    }

    val boxModifier = if (active) {
        modifier.clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onPartyTap,
        )
    } else {
        modifier
    }

    Box(modifier = boxModifier) {
        AnimatedVisibility(
            visible = active,
            enter = fadeIn(tween(300)),
            exit = fadeOut(tween(300)),
            modifier = Modifier.matchParentSize(),
        ) {
            PartyAnimation(
                modifier = Modifier.fillMaxSize(),
                intensity = intensityProvider,
                cannon = cannonProvider,
            )
        }

        Column {
            content()
            AnimatedVisibility(
                visible = active,
                enter = expandVertically(
                    animationSpec = tween(3200, easing = EaseOutExpo),
                    expandFrom = Alignment.Top,
                ),
                exit = shrinkVertically(
                    animationSpec = tween(1600, easing = EaseInExpo),
                    shrinkTowards = Alignment.Top,
                ),
            ) {
                Spacer(Modifier.height(140.dp))
            }
        }
    }
}

private data class LightSpot(
    val centerXF: Float,
    val centerYF: Float,
    val orbitRadiusF: Float,
    val orbitCycles: Int,
    val orbitPhase: Float,
    val pulseBase: Float,
    val pulseAmp: Float,
    val colorPhase: Float,
)

private val LightSpots = listOf(
    LightSpot(0.18f, 0.32f, 0.16f, 1, 0.0f, 0.55f, 0.12f, 0.0f),
    LightSpot(0.52f, 0.72f, 0.22f, 1, 1.1f, 0.60f, 0.14f, 0.35f),
    LightSpot(0.82f, 0.28f, 0.14f, 2, 2.0f, 0.50f, 0.16f, 0.70f),
    LightSpot(0.28f, 0.82f, 0.20f, 1, 3.0f, 0.55f, 0.12f, 1.15f),
    LightSpot(0.70f, 0.50f, 0.12f, 2, 3.9f, 0.45f, 0.15f, 1.60f),
    LightSpot(0.08f, 0.58f, 0.18f, 1, 4.7f, 0.50f, 0.13f, 2.10f),
    LightSpot(0.92f, 0.68f, 0.16f, 2, 5.4f, 0.55f, 0.14f, 2.55f),
    LightSpot(0.50f, 0.18f, 0.14f, 1, 0.6f, 0.50f, 0.12f, 3.00f),
)

@Composable
private fun PartyLights(
    modifier: Modifier = Modifier,
    intensity: () -> Float,
) {
    val transition = rememberInfiniteTransition(label = "party-lights")
    val darkTheme = KotlinConfTheme.colors.isDark
    val time by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(9000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "party-lights-time",
    )

    Canvas(modifier = modifier) {
        val i = intensity()
        val alphaBoost = 1f + i * 1.2f
        val radiusBoost = 1f + i * 0.18f
        val orbitBoost = 1f + i * 0.35f
        val minDim = size.minDimension
        val paletteSize = PartyPalette.size

        LightSpots.forEach { spot ->
            val orbitAngle = time * spot.orbitCycles * TwoPi + spot.orbitPhase
            val orbitR = minDim * spot.orbitRadiusF * orbitBoost
            val cx = spot.centerXF * size.width + cos(orbitAngle) * orbitR
            val cy = spot.centerYF * size.height + sin(orbitAngle) * orbitR * 0.75f

            val pulseAngle = time * 2f * TwoPi + spot.orbitPhase * 1.3f
            val pulse = (sin(pulseAngle) + 1f) / 2f
            val radius = minDim *
                (spot.pulseBase + spot.pulseAmp * sin(pulseAngle * 0.5f)) *
                radiusBoost
            val alpha = ((0.14f + pulse * 0.42f) * alphaBoost).coerceAtMost(1f)

            val colorProgress = (time * 2f + spot.colorPhase) * paletteSize
            val colorFloor = floor(colorProgress)
            val colorIdx = colorFloor.toInt().mod(paletteSize)
            val nextIdx = (colorIdx + 1).mod(paletteSize)
            val color = lerp(
                PartyPalette[colorIdx],
                PartyPalette[nextIdx],
                colorProgress - colorFloor,
            )
            val lightColor = partyLightColor(color, darkTheme)
            val lightAlpha = partyLightAlpha(alpha, darkTheme)
            val scrimAlpha = partyLightScrimAlpha(alpha, darkTheme)

            val center = Offset(cx, cy)
            if (scrimAlpha > 0f) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = scrimAlpha),
                            Color.White.copy(alpha = scrimAlpha * 0.55f),
                            Color.White.copy(alpha = 0f),
                        ),
                        center = center,
                        radius = radius * 0.88f,
                    ),
                    radius = radius * 0.88f,
                    center = center,
                )
            }
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        lightColor.copy(alpha = lightAlpha),
                        lightColor.copy(alpha = lightAlpha * 0.45f),
                        lightColor.copy(alpha = 0f),
                    ),
                    center = center,
                    radius = radius,
                ),
                radius = radius,
                center = center,
            )
        }

        val sweepAngle = time * TwoPi
        val sweepProgress = time * paletteSize
        val sweepFloor = floor(sweepProgress)
        val sweepIdx = sweepFloor.toInt().mod(paletteSize)
        val sweepNextIdx = (sweepIdx + 1).mod(paletteSize)
        val sweepColor = lerp(
            PartyPalette[sweepIdx],
            PartyPalette[sweepNextIdx],
            sweepProgress - sweepFloor,
        )
        val lightSweepColor = partyLightColor(sweepColor, darkTheme)
        val sweepCenter = Offset(
            size.width * 0.5f + cos(sweepAngle) * size.width * 0.35f,
            size.height * 0.5f + sin(sweepAngle) * size.height * 0.35f,
        )
        val sweepAlpha = partyLightAlpha((0.22f * alphaBoost).coerceAtMost(1f), darkTheme)
        val sweepRadius = minDim * 0.9f
        val sweepScrimAlpha = partyLightScrimAlpha(sweepAlpha, darkTheme) * 0.75f
        if (sweepScrimAlpha > 0f) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = sweepScrimAlpha),
                        Color.White.copy(alpha = 0f),
                    ),
                    center = sweepCenter,
                    radius = sweepRadius * 0.72f,
                ),
                radius = sweepRadius * 0.72f,
                center = sweepCenter,
            )
        }
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    lightSweepColor.copy(alpha = sweepAlpha),
                    lightSweepColor.copy(alpha = 0f),
                ),
                center = sweepCenter,
                radius = sweepRadius,
            ),
            radius = sweepRadius,
            center = sweepCenter,
        )
    }
}

private enum class KodeeStyle { Bouncer, Dancer }

private data class KodeeSpec(
    val xF: Float,
    val yF: Float,
    val sizeDp: Int,
    val style: KodeeStyle,
    val phase: Float,
    val flipped: Boolean,
)

private val Kodees = listOf(
    KodeeSpec(0.08f, 0.68f, 54, KodeeStyle.Bouncer, 0f, false),
    KodeeSpec(0.26f, 0.50f, 44, KodeeStyle.Dancer, 0.8f, true),
    KodeeSpec(0.48f, 0.65f, 58, KodeeStyle.Bouncer, 1.7f, false),
    KodeeSpec(0.72f, 0.52f, 46, KodeeStyle.Dancer, 2.5f, false),
    KodeeSpec(0.90f, 0.68f, 50, KodeeStyle.Bouncer, 3.3f, true),
)

@Composable
private fun PartyKodees(
    modifier: Modifier = Modifier,
    intensity: () -> Float,
) {
    val kodeeVector: ImageVector = vectorResource(UiRes.drawable.kodee_emotion_positive)
    val kodeePainter = rememberVectorPainter(kodeeVector)
    val transition = rememberInfiniteTransition(label = "kodees")
    val t by transition.animateFloat(
        initialValue = 0f,
        targetValue = TwoPi,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "kodee-time",
    )

    Box(modifier = modifier) {
        Kodees.forEach { k ->
            val flipSign = if (k.flipped) -1f else 1f
            val sizeDp = k.sizeDp.dp

            Image(
                painter = kodeePainter,
                contentDescription = null,
                modifier = Modifier
                    .layout { measurable, constraints ->
                        val sizePx = sizeDp.roundToPx()
                        val placeable = measurable.measure(Constraints.fixed(sizePx, sizePx))
                        val px = (constraints.maxWidth * k.xF).toInt() - sizePx / 2
                        val py = (constraints.maxHeight * k.yF).toInt() - sizePx / 2
                        layout(constraints.maxWidth, constraints.maxHeight) {
                            placeable.place(px, py)
                        }
                    }
                    .graphicsLayer {
                        val i = intensity()
                        val phase = t + k.phase
                        transformOrigin = TransformOrigin(0.5f, 1f)
                        when (k.style) {
                            KodeeStyle.Bouncer -> {
                                val bounceAmp = (24f + i * 24f).dp.toPx()
                                translationX = 0f
                                translationY = -abs(sin(phase)) * bounceAmp
                                rotationZ = 0f
                                scaleX = flipSign
                                scaleY = 1f
                            }
                            KodeeStyle.Dancer -> {
                                val swayAmp = (5f + i * 10f).dp.toPx()
                                val bopAmp = (6f + i * 12f).dp.toPx()
                                val rotAmp = 16f + i * 18f
                                translationX = cos(phase) * swayAmp
                                translationY = -abs(sin(phase * 2f)) * bopAmp
                                rotationZ = sin(phase) * rotAmp
                                scaleX = flipSign
                                scaleY = 1f
                            }
                        }
                    },
            )
        }
    }
}

private enum class ConfettiShape { Rectangle, Circle, Triangle, Strip }

private data class ConfettiParticle(
    val xF: Float,
    val sizeDp: Float,
    val rotSpeed: Float,
    val rotStart: Float,
    val color: Color,
    val phase: Float,
    val driftDp: Float,
    val driftFreq: Float,
    val shape: ConfettiShape,
    val cyclesPerLoop: Int,
    val aspect: Float,
    val revealThreshold: Float,
)

private const val ConfettiStart = 0.2f
private const val DefaultConfettiIntensity = 0.3f
private const val ConfettiRevealBand = 0.18f

private val ConfettiParticles: List<ConfettiParticle> = run {
    val rng = Random(1337)
    val shapes = ConfettiShape.entries
    val count = 56
    List(count) { index ->
        val revealThreshold = ConfettiStart +
            (1f - ConfettiStart) * (index.toFloat() / count)
        ConfettiParticle(
            xF = rng.nextFloat(),
            sizeDp = 5f + rng.nextFloat() * 7f,
            rotSpeed = (rng.nextFloat() * 2f - 1f) * 3.2f,
            rotStart = rng.nextFloat() * 360f,
            color = PartyPalette[rng.nextInt(PartyPalette.size)],
            phase = rng.nextFloat(),
            driftDp = 8f + rng.nextFloat() * 18f,
            driftFreq = 0.7f + rng.nextFloat() * 1.3f,
            shape = shapes[rng.nextInt(shapes.size)],
            cyclesPerLoop = 1 + rng.nextInt(3),
            aspect = 0.35f + rng.nextFloat() * 0.55f,
            revealThreshold = revealThreshold,
        )
    }
}

@Composable
private fun Confetti(
    modifier: Modifier = Modifier,
    intensity: () -> Float,
) {
    val transition = rememberInfiniteTransition(label = "confetti")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "confetti-progress",
    )
    val triangle = remember { Path() }

    Canvas(modifier = modifier) {
        val i = intensity().coerceAtLeast(DefaultConfettiIntensity)
        if (i < ConfettiStart) return@Canvas

        val sizeBoost = 1f + i * 0.45f
        val driftBoost = 1f + i * 0.5f
        val canvasWidth = size.width
        val canvasHeight = size.height

        for (p in ConfettiParticles) {
            // Particles are sorted by ascending revealThreshold; once one doesn't
            // pass, none of the remaining will either, so we can stop iterating.
            if (i < p.revealThreshold) break
            val particleAlpha = ((i - p.revealThreshold) / ConfettiRevealBand).coerceIn(0f, 1f)

            val t = (progress * p.cyclesPerLoop + p.phase).mod(1f)
            val sizePx = p.sizeDp.dp.toPx() * sizeBoost
            val driftPx = p.driftDp.dp.toPx() * driftBoost
            val xBase = p.xF * canvasWidth
            val drift = sin(t * p.driftFreq * TwoPi) * driftPx
            val x = xBase + drift
            val margin = sizePx * 2f
            val y = -margin + t * (canvasHeight + margin * 2f)
            val rotDeg = p.rotStart + p.rotSpeed * t * 720f

            rotate(rotDeg, Offset(x, y)) {
                when (p.shape) {
                    ConfettiShape.Rectangle -> {
                        val h = sizePx * p.aspect
                        drawRect(
                            color = p.color,
                            topLeft = Offset(x - sizePx / 2f, y - h / 2f),
                            size = Size(sizePx, h),
                            alpha = particleAlpha,
                        )
                    }
                    ConfettiShape.Circle -> {
                        drawCircle(
                            color = p.color,
                            radius = sizePx / 2f,
                            center = Offset(x, y),
                            alpha = particleAlpha,
                        )
                    }
                    ConfettiShape.Triangle -> {
                        val r = sizePx / 2f
                        triangle.reset()
                        triangle.moveTo(x, y - r)
                        triangle.lineTo(x - r, y + r)
                        triangle.lineTo(x + r, y + r)
                        triangle.close()
                        drawPath(triangle, p.color, alpha = particleAlpha)
                    }
                    ConfettiShape.Strip -> {
                        val w = sizePx * 1.3f
                        val h = sizePx * 0.22f
                        drawRect(
                            color = p.color,
                            topLeft = Offset(x - w / 2f, y - h / 2f),
                            size = Size(w, h),
                            alpha = particleAlpha,
                        )
                    }
                }
            }
        }
    }
}

private data class CannonParticle(
    val fromLeft: Boolean,
    val angle: Float,
    val speed: Float,
    val sizeDp: Float,
    val rotStart: Float,
    val rotSpeed: Float,
    val launchDelay: Float,
)

private val CannonParticles: List<CannonParticle> = run {
    val rng = Random(4242)
    val count = 32
    val leftBase = -1.309f
    val rightBase = -1.833f
    val spread = 0.9f
    List(count) { index ->
        val fromLeft = index % 2 == 0
        val base = if (fromLeft) leftBase else rightBase
        CannonParticle(
            fromLeft = fromLeft,
            angle = base + (rng.nextFloat() - 0.5f) * spread,
            speed = 0.6f + rng.nextFloat() * 0.45f,
            sizeDp = 14f + rng.nextFloat() * 8f,
            rotStart = rng.nextFloat() * 360f,
            rotSpeed = (rng.nextFloat() * 2f - 1f) * 540f,
            launchDelay = rng.nextFloat() * 0.1f,
        )
    }
}

@Composable
private fun KodeeCannon(
    modifier: Modifier = Modifier,
    progress: () -> Float,
) {
    val facePainter = rememberVectorPainter(
        vectorResource(UiRes.drawable.kodee_small_positive_filled)
    )

    Canvas(modifier = modifier) {
        val p = progress()
        if (p >= 1f) return@Canvas

        val minDim = size.minDimension
        val maxDist = minDim * 2.2f
        val gravityPull = minDim * 1.2f
        val leftX = 0f
        val rightX = size.width
        val originY = size.height

        CannonParticles.forEach { particle ->
            val localT = (p - particle.launchDelay) / (1f - particle.launchDelay)
            if (localT <= 0f) return@forEach
            val t = localT.coerceAtMost(1f)

            val dist = t * particle.speed * maxDist
            val dx = cos(particle.angle) * dist
            val dy = sin(particle.angle) * dist + t * t * gravityPull
            val originX = if (particle.fromLeft) leftX else rightX
            val x = originX + dx
            val y = originY + dy

            val fadeIn = (t / 0.05f).coerceIn(0f, 1f)
            val fadeOut = ((1f - t) / 0.25f).coerceIn(0f, 1f)
            val alpha = fadeIn * fadeOut
            if (alpha <= 0f) return@forEach

            val rotDeg = particle.rotStart + particle.rotSpeed * t
            val facePx = particle.sizeDp.dp.toPx()

            rotate(rotDeg, Offset(x, y)) {
                translate(left = x - facePx / 2f, top = y - facePx / 2f) {
                    with(facePainter) {
                        draw(size = Size(facePx, facePx), alpha = alpha)
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun PartyAnimationPreview() = PreviewHelper {
    PartyAnimation(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        intensity = { 1f },
    )
}

@PreviewLightDark
@Composable
private fun PartyAnimationBaselinePreview() = PreviewHelper {
    PartyAnimation(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        intensity = { 0f },
    )
}
