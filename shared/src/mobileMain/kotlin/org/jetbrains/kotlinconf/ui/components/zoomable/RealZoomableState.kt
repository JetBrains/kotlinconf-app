@file:Suppress("ConstPropertyName")

package org.jetbrains.kotlinconf.ui.components.zoomable

import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.AnimationVector
import androidx.compose.animation.core.Spring.StiffnessMediumLow
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.core.animateTo
import androidx.compose.animation.core.spring
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.MutatePriority
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isFinite
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.geometry.lerp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ScaleFactor
import androidx.compose.ui.layout.times
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.toOffset
import androidx.compose.ui.util.lerp
import org.jetbrains.kotlinconf.ui.components.zoomable.ContentZoomFactor.Companion.ZoomDeltaEpsilon
import org.jetbrains.kotlinconf.ui.components.zoomable.ZoomableContentLocation.SameAsLayoutBounds
import org.jetbrains.kotlinconf.ui.components.zoomable.internal.MutatePriorities
import org.jetbrains.kotlinconf.ui.components.zoomable.internal.PlaceholderBoundsProvider
import org.jetbrains.kotlinconf.ui.components.zoomable.internal.RealZoomableContentTransformation
import org.jetbrains.kotlinconf.ui.components.zoomable.internal.TransformableState
import org.jetbrains.kotlinconf.ui.components.zoomable.internal.Zero
import org.jetbrains.kotlinconf.ui.components.zoomable.internal.ZoomableSavedState
import org.jetbrains.kotlinconf.ui.components.zoomable.internal.calculateTopLeftToOverlapWith
import org.jetbrains.kotlinconf.ui.components.zoomable.internal.coerceIn
import org.jetbrains.kotlinconf.ui.components.zoomable.internal.div
import org.jetbrains.kotlinconf.ui.components.zoomable.internal.isPositiveAndFinite
import org.jetbrains.kotlinconf.ui.components.zoomable.internal.maxScale
import org.jetbrains.kotlinconf.ui.components.zoomable.internal.minScale
import org.jetbrains.kotlinconf.ui.components.zoomable.internal.minus
import org.jetbrains.kotlinconf.ui.components.zoomable.internal.roundToIntSize
import org.jetbrains.kotlinconf.ui.components.zoomable.internal.times
import org.jetbrains.kotlinconf.ui.components.zoomable.internal.unaryMinus
import org.jetbrains.kotlinconf.ui.components.zoomable.internal.withOrigin
import org.jetbrains.kotlinconf.ui.components.zoomable.internal.withZoomAndTranslate
import kotlin.jvm.JvmInline
import kotlin.math.abs

@Stable
internal class RealZoomableState internal constructor(
    private val isLayoutPreview: Boolean = false,
) : ZoomableState {

    override val contentTransformation: ZoomableContentTransformation by derivedStateOf {
        val baseZoomFactor = baseZoomFactor
        val gestureState = gestureState

        if (gestureState != null && baseZoomFactor != null) {
            val contentZoom = ContentZoomFactor(baseZoomFactor, gestureState.userZoomFactor)
            RealZoomableContentTransformation(
                isSpecified = true,
                contentSize = gestureState.contentSize,
                scale = contentZoom.finalZoom(),
                scaleMetadata = RealZoomableContentTransformation.ScaleMetadata(
                    initialScale = baseZoomFactor.value,
                    userZoom = gestureState.userZoomFactor.value,
                ),
                offset = -gestureState.offset * contentZoom.finalZoom(),
                centroid = gestureState.lastCentroid,
            )
        } else {
            RealZoomableContentTransformation(
                isSpecified = false,
                contentSize = Size.Zero,
                scale = when {
                    isLayoutPreview -> ScaleFactor(1f, 1f)
                    else -> ScaleFactor.Zero  // Effectively hide the content until an initial zoom value is calculated.
                },
                scaleMetadata = RealZoomableContentTransformation.ScaleMetadata(
                    initialScale = ScaleFactor.Zero,
                    userZoom = 0f,
                ),
                offset = Offset.Zero,
                centroid = null,
            )
        }
    }

    override var autoApplyTransformations: Boolean by mutableStateOf(true)

    override var contentScale: ContentScale by mutableStateOf(ContentScale.Fit)

    override var contentAlignment: Alignment by mutableStateOf(Alignment.Center)

    override val zoomFraction: Float? by derivedStateOf {
        val gestureState = gestureState
        val baseZoomFactor = baseZoomFactor
        if (gestureState != null && baseZoomFactor != null) {
            val min = ContentZoomFactor.minimum(baseZoomFactor, zoomSpec.range).userZoom
            val max = ContentZoomFactor.maximum(baseZoomFactor, zoomSpec.range).userZoom
            val current = gestureState.userZoomFactor.coerceIn(min, max)
            when {
                current == min && min == max -> 1f  // Content can't zoom.
                else -> ((current - min) / (max - min)).value.coerceIn(0f, 1f)
            }
        } else {
            null
        }
    }

    internal var gestureState: GestureState? by mutableStateOf(null)

    private var zoomSpec by mutableStateOf(ZoomSpec())
    internal var layoutDirection: LayoutDirection by mutableStateOf(LayoutDirection.Ltr)

    /**
     * Raw size of the zoomable content without any scaling applied.
     * Used to ensure that the content does not pan/zoom outside its limits.
     */
    private var unscaledContentLocation: ZoomableContentLocation by mutableStateOf(
        SameAsLayoutBounds
    )

    /**
     * Layout bounds of the zoomable content in the UI hierarchy, without any scaling applied.
     */
    internal var contentLayoutSize: Size by mutableStateOf(Size.Zero)

    private val unscaledContentBounds: Rect by derivedStateOf {
        if (isReadyToInteract) {
            unscaledContentLocation.location(
                layoutSize = contentLayoutSize,
                direction = layoutDirection
            )
        } else Rect.Zero
    }

    /**
     * See [BaseZoomFactor].
     */
    private val baseZoomFactor: BaseZoomFactor? by derivedStateOf {
        if (isReadyToInteract) {
            BaseZoomFactor(
                contentScale.computeScaleFactor(
                    srcSize = unscaledContentBounds.size,
                    dstSize = contentLayoutSize,
                )
            ).also {
                check(it.value != ScaleFactor.Zero) {
                    "Base zoom shouldn't be zero. content bounds = $unscaledContentBounds, layout size = $contentLayoutSize"
                }
            }
        } else {
            null
        }
    }

    /** See [PlaceholderBoundsProvider]. */
    private var placeholderBoundsProvider: PlaceholderBoundsProvider? by mutableStateOf(null)

    override val transformedContentBounds: Rect by derivedStateOf {
        with(contentTransformation) {
            if (isSpecified) {
                unscaledContentBounds.withOrigin(transformOrigin) {
                    times(scale).translate(offset)
                }
            } else {
                placeholderBoundsProvider?.calculate(state = this@RealZoomableState) ?: Rect.Zero
            }
        }
    }

    /**
     * Whether sufficient information is available about the content to start
     * listening to pan & zoom gestures.
     */
    internal val isReadyToInteract: Boolean by derivedStateOf {
        contentLayoutSize.minDimension > 0f // Prevent division by zero errors.
                && unscaledContentLocation.size(contentLayoutSize)
            .let { it.isSpecified && it.minDimension > 0f }
    }

    @Suppress("NAME_SHADOWING")
    internal val transformableState = TransformableState { zoomDelta, panDelta, _, centroid ->
        check(panDelta.isFinite && zoomDelta.isFinite() && centroid.isFinite) {
            "Can't transform with zoomDelta=$zoomDelta, panDelta=$panDelta, centroid=$centroid. ${collectDebugInfoForIssue41()}"
        }

        val baseZoomFactor = baseZoomFactor ?: return@TransformableState
        val oldZoom = ContentZoomFactor(
            baseZoom = baseZoomFactor,
            userZoom = gestureState?.userZoomFactor ?: UserZoomFactor(1f),
        )
        check(oldZoom.finalZoom().isPositiveAndFinite()) {
            "Old zoom is invalid/infinite. ${collectDebugInfoForIssue41()}"
        }

        val isZoomingOut = zoomDelta < 1f
        val isZoomingIn = zoomDelta > 1f

        // Apply elasticity if content is being over/under-zoomed.
        val isAtMaxZoom = oldZoom.isAtMaxZoom(zoomSpec.range)
        val isAtMinZoom = oldZoom.isAtMinZoom(zoomSpec.range)
        val zoomDelta = when {
            !zoomSpec.preventOverOrUnderZoom -> zoomDelta
            isZoomingIn && isAtMaxZoom -> 1f + zoomDelta / 250
            isZoomingOut && isAtMinZoom -> 1f - zoomDelta / 250
            else -> zoomDelta
        }
        val newZoom = ContentZoomFactor(
            baseZoom = baseZoomFactor,
            userZoom = oldZoom.userZoom * zoomDelta,
        ).let {
            if (zoomSpec.preventOverOrUnderZoom && (isAtMinZoom || isAtMaxZoom)) {
                it.coerceUserZoomIn(
                    range = zoomSpec.range,
                    leewayPercentForMinZoom = 0.1f,
                    leewayPercentForMaxZoom = 0.4f
                )
            } else {
                it
            }
        }
        check(newZoom.finalZoom().let { it.isPositiveAndFinite() && it.minScale > 0f }) {
            "New zoom is invalid/infinite = $newZoom. ${collectDebugInfoForIssue41("zoomDelta" to zoomDelta)}"
        }

        val oldOffset = gestureState.let {
            if (it != null) {
                it.offset
            } else {
                val defaultAlignmentOffset = contentAlignment.align(
                    size = (unscaledContentBounds.size * baseZoomFactor.value).roundToIntSize(),
                    space = contentLayoutSize.roundToIntSize(),
                    layoutDirection = layoutDirection
                )
                // Take the content's top-left into account because it may not start at 0,0.
                unscaledContentBounds.topLeft + (-defaultAlignmentOffset.toOffset() / oldZoom)
            }
        }

        gestureState = GestureState(
            offset = oldOffset
                .retainCentroidPositionAfterZoom(
                    centroid = centroid,
                    panDelta = panDelta,
                    oldZoom = oldZoom,
                    newZoom = newZoom,
                )
                .coerceWithinBounds(proposedZoom = newZoom),
            userZoomFactor = newZoom.userZoom,
            lastCentroid = centroid,
            contentSize = unscaledContentLocation.size(contentLayoutSize),
        )
    }

    internal fun canConsumePanChange(panDelta: Offset): Boolean {
        val baseZoomFactor = baseZoomFactor
            ?: return false // Content is probably not ready yet. Ignore this gesture.
        val current = gestureState ?: return false

        val currentZoom = ContentZoomFactor(baseZoomFactor, current.userZoomFactor)
        val panDeltaWithZoom = panDelta / currentZoom
        val newOffset = current.offset - panDeltaWithZoom
        check(newOffset.isFinite) {
            "Offset can't be infinite ${collectDebugInfoForIssue41("panDelta" to panDelta)}"
        }

        val newOffsetWithinBounds = newOffset.coerceWithinBounds(proposedZoom = currentZoom)
        val consumedPan = panDeltaWithZoom - (newOffsetWithinBounds - newOffset)
        val isHorizontalPan = abs(panDeltaWithZoom.x) > abs(panDeltaWithZoom.y)

        return (if (isHorizontalPan) abs(consumedPan.x) else abs(consumedPan.y)) > ZoomDeltaEpsilon
    }

    /**
     * Translate this offset such that the visual position of [centroid]
     * remains the same after applying [panDelta] and [newZoom].
     */
    private fun Offset.retainCentroidPositionAfterZoom(
        centroid: Offset,
        panDelta: Offset = Offset.Zero,
        oldZoom: ContentZoomFactor,
        newZoom: ContentZoomFactor,
    ): Offset {
        check(this.isFinite) {
            "Can't center around an infinite offset ${collectDebugInfoForIssue41()}"
        }

        // Copied from androidx samples:
        // https://github.com/androidx/androidx/blob/643b1cfdd7dfbc5ccce1ad951b6999df049678b3/compose/foundation/foundation/samples/src/main/java/androidx/compose/foundation/samples/TransformGestureSamples.kt#L87
        //
        // For natural zooming and rotating, the centroid of the gesture
        // should be the fixed point where zooming and rotating occurs.
        //
        // We compute where the centroid was (in the pre-transformed coordinate
        // space), and then compute where it will be after this delta.
        //
        // We then compute what the new offset should be to keep the centroid
        // visually stationary for rotating and zooming, and also apply the pan.
        //
        // This is comparable to performing a pre-translate + scale + post-translate on
        // a Matrix.
        //
        // I found this maths difficult to understand, so here's another explanation in
        // Ryan Harter's words:
        //
        // The basic idea is that to scale around an arbitrary point, you translate so that
        // that point is in the center, then you rotate, then scale, then move everything back.
        //
        // Note to self: these values are divided by zoom because that's how the final offset
        // for UI is calculated: -offset * zoom.
        //
        //     Move the centroid to the center
        //         of panned content(?)
        //                  |                       Scale
        //                  |                         |                Move back
        //                  |                         |           (+ new translation)
        //                  |                         |                    |
        //     _____________|_____________    ________|_________   ________|_________
        return ((this + centroid / oldZoom) - (centroid / newZoom + panDelta / oldZoom)).also {
            check(it.isFinite) {
                val debugInfo = collectDebugInfoForIssue41(
                    "centroid" to centroid,
                    "panDelta" to panDelta,
                    "oldZoom" to oldZoom,
                    "newZoom" to newZoom,
                )
                "retainCentroidPositionAfterZoom() generated an infinite value. $debugInfo"
            }
        }
    }

    private fun Offset.coerceWithinBounds(proposedZoom: ContentZoomFactor): Offset {
        check(this.isFinite) {
            "Can't coerce an infinite offset ${collectDebugInfoForIssue41("proposedZoom" to proposedZoom)}"
        }

        val scaledTopLeft = unscaledContentBounds.topLeft * proposedZoom

        // Note to self: (-offset * zoom) is the final value used for displaying the content composable.
        return withZoomAndTranslate(
            zoom = -proposedZoom.finalZoom(),
            translate = scaledTopLeft
        ) { offset ->
            val expectedDrawRegion = Rect(
                offset,
                unscaledContentBounds.size * proposedZoom
            ).throwIfDrawRegionIsTooLarge()
            expectedDrawRegion.calculateTopLeftToOverlapWith(
                contentLayoutSize,
                contentAlignment,
                layoutDirection
            )
        }
    }

    private fun Rect.throwIfDrawRegionIsTooLarge(): Rect {
        return also {
            check(size.isSpecified) {
                "The zoomable content is too large to safely calculate its draw region. This can happen if you're using" +
                        " an unusually large value for ZoomSpec#maxZoomFactor (for e.g., Float.MAX_VALUE). Please file an issue" +
                        " on https://github.com/saket/telephoto/issues if you think this is a mistake."
            }
        }
    }

    override suspend fun setContentLocation(location: ZoomableContentLocation) {
        if (unscaledContentLocation != location) {
            unscaledContentLocation = location

            // Refresh synchronously so that the result is available immediately.
            // Otherwise, the old position will be used with this new size and cause a flicker.
            refreshContentTransformation()
        }
    }

    override suspend fun resetZoom(withAnimation: Boolean) {
        if (withAnimation) {
            smoothlyToggleZoom(
                shouldZoomIn = false,
                centroid = Offset.Zero,
            )
        } else {
            gestureState = null
            refreshContentTransformation()
        }
    }

    /**
     * Update the content's position. This is called when values
     * such as [contentScale] and [contentAlignment] are updated.
     */
    internal suspend fun refreshContentTransformation() {
        if (isReadyToInteract) {
            transformableState.transform(MutatePriority.PreventUserInput) {
                transformBy(/* default values */)
            }
        }
    }

    internal suspend fun handleDoubleTapZoomTo(centroid: Offset) {
        val baseZoomFactor = baseZoomFactor ?: return
        val gestureState = gestureState ?: return
        val currentZoom = ContentZoomFactor(baseZoomFactor, gestureState.userZoomFactor)

        smoothlyToggleZoom(
            shouldZoomIn = !currentZoom.isAtMaxZoom(zoomSpec.range),
            centroid = centroid
        )
    }

    private suspend fun smoothlyToggleZoom(
        shouldZoomIn: Boolean,
        centroid: Offset
    ) {
        val startTransformation = gestureState ?: return
        val baseZoomFactor = baseZoomFactor ?: return

        val startZoom = ContentZoomFactor(baseZoomFactor, startTransformation.userZoomFactor)
        val targetZoom = if (shouldZoomIn) {
            ContentZoomFactor.maximum(baseZoomFactor, zoomSpec.range)
        } else {
            ContentZoomFactor.minimum(baseZoomFactor, zoomSpec.range)
        }

        val targetOffset = startTransformation.offset
            .retainCentroidPositionAfterZoom(
                centroid = centroid,
                oldZoom = startZoom,
                newZoom = targetZoom,
            )
            .coerceWithinBounds(proposedZoom = targetZoom)

        transformableState.transform(MutatePriority.UserInput) {
            AnimationState(initialValue = 0f).animateTo(
                targetValue = 1f,
                // Without a low visibility threshold, spring() makes a huge
                // jump on its last frame causing a few frames to be dropped.
                animationSpec = spring(
                    stiffness = StiffnessMediumLow,
                    visibilityThreshold = 0.0001f
                )
            ) {
                val animatedZoom: ContentZoomFactor = startZoom.copy(
                    userZoom = UserZoomFactor(
                        lerp(
                            start = startZoom.userZoom.value,
                            stop = targetZoom.userZoom.value,
                            fraction = value
                        )
                    )
                )
                // For animating the offset, it is necessary to interpolate between values that the UI
                // will see (i.e., -offset * zoom). Otherwise, a curve animation is produced if only the
                // offset is used because the zoom and the offset values animate at different scales.
                val animatedOffsetForUi: Offset = lerp(
                    start = (-startTransformation.offset * startZoom),
                    stop = (-targetOffset * targetZoom),
                    fraction = value
                )

                gestureState = gestureState!!.copy(
                    offset = (-animatedOffsetForUi) / animatedZoom,
                    userZoomFactor = animatedZoom.userZoom,
                    lastCentroid = centroid,
                )
            }
        }
    }

    internal fun isZoomOutsideRange(): Boolean {
        val baseZoomFactor = baseZoomFactor ?: return false
        val userZoomFactor = gestureState?.userZoomFactor ?: return false

        val currentZoom = ContentZoomFactor(baseZoomFactor, userZoomFactor)
        val zoomWithinBounds = currentZoom.coerceUserZoomIn(zoomSpec.range)
        return abs(currentZoom.userZoom.value - zoomWithinBounds.userZoom.value) > ZoomDeltaEpsilon
    }

    internal suspend fun smoothlySettleZoomOnGestureEnd() {
        check(isReadyToInteract) { "shouldn't have gotten called" }
        val start = gestureState!!
        val userZoomWithinBounds = ContentZoomFactor(baseZoomFactor!!, start.userZoomFactor)
            .coerceUserZoomIn(zoomSpec.range)
            .userZoom

        transformableState.transform(MutatePriority.Default) {
            var previous = start.userZoomFactor.value
            AnimationState(initialValue = previous).animateTo(
                targetValue = userZoomWithinBounds.value,
                animationSpec = spring()
            ) {
                transformBy(
                    centroid = start.lastCentroid,
                    zoomChange = if (previous == 0f) 1f else value / previous,
                )
                previous = this.value
            }
        }
    }

    internal suspend fun fling(velocity: Velocity, density: Density) {
        check(velocity.x.isFinite() && velocity.y.isFinite()) { "Invalid velocity = $velocity" }

        val start = gestureState!!
        transformableState.transform(MutatePriorities.FlingAnimation) {
            var previous = start.offset
            AnimationState(
                typeConverter = Offset.VectorConverter,
                initialValue = previous,
                initialVelocityVector = AnimationVector(velocity.x, velocity.y)
            ).animateDecay(splineBasedDecay(density)) {
                transformBy(
                    centroid = start.lastCentroid,
                    panChange = (value - previous).also {
                        check(it.isFinite) {
                            val debugInfo = collectDebugInfoForIssue41(
                                "value" to value,
                                "previous" to previous,
                                "velocity" to velocity,
                            )
                            "Can't fling with an invalid pan = $it. $debugInfo"
                        }
                    }
                )
                previous = value
            }
        }
    }

    // https://github.com/saket/telephoto/issues/41
    private fun collectDebugInfoForIssue41(vararg extras: Pair<String, Any>): String {
        return buildString {
            appendLine()
            extras.forEach { (key, value) ->
                appendLine("$key = $value")
            }
            appendLine("gestureState = $gestureState")
            appendLine("contentTransformation = $contentTransformation")
            appendLine("contentScale = $contentScale")
            appendLine("contentAlignment = $contentAlignment")
            appendLine("isReadyToInteract = $isReadyToInteract")
            appendLine("unscaledContentLocation = $unscaledContentLocation")
            appendLine("unscaledContentBounds = $unscaledContentBounds")
            appendLine("contentLayoutSize = $contentLayoutSize")
            appendLine("zoomSpec = $zoomSpec")
            appendLine("Please share this error message to https://github.com/saket/telephoto/issues/41?")
        }
    }

    companion object {
        internal val Saver = Saver<RealZoomableState, ZoomableSavedState>(
            save = { ZoomableSavedState(it.gestureState) },
            restore = { RealZoomableState() }
        )
    }
}

/** An intermediate, non-normalized model used for generating [ZoomableContentTransformation]. */
internal data class GestureState(
    val offset: Offset,
    val userZoomFactor: UserZoomFactor,
    val lastCentroid: Offset,
    val contentSize: Size,
)

/**
 * The minimum scale needed to position the content within its layout
 * bounds with respect to [ZoomableState.contentScale].
 * */
@JvmInline
@Immutable
internal value class BaseZoomFactor(val value: ScaleFactor) {
    val maxScale: Float get() = value.maxScale
}

/** Zoom applied by the user on top of [BaseZoomFactor]. */
@JvmInline
@Immutable
internal value class UserZoomFactor(val value: Float)

internal data class ContentZoomFactor(
    private val baseZoom: BaseZoomFactor,
    val userZoom: UserZoomFactor,
) {
    fun finalZoom(): ScaleFactor = baseZoom * userZoom
    private fun finalMaxScale(): Float = finalZoom().maxScale

    fun coerceUserZoomIn(
        range: ZoomRange,
        leewayPercentForMinZoom: Float = 0f,
        leewayPercentForMaxZoom: Float = leewayPercentForMinZoom,
    ): ContentZoomFactor {
        val minUserZoom = minimum(baseZoom, range).userZoom
        val maxUserZoom = maximum(baseZoom, range).userZoom
        return copy(
            userZoom = UserZoomFactor(
                userZoom.value.coerceIn(
                    minimumValue = minUserZoom.value * (1 - leewayPercentForMinZoom),
                    maximumValue = maxUserZoom.value * (1 + leewayPercentForMaxZoom),
                )
            )
        )
    }

    fun isAtMinZoom(range: ZoomRange): Boolean {
        return (finalMaxScale() - minimum(baseZoom, range).finalMaxScale()) < ZoomDeltaEpsilon
    }

    fun isAtMaxZoom(range: ZoomRange): Boolean {
        return (maximum(baseZoom, range).finalMaxScale() - finalMaxScale()) < ZoomDeltaEpsilon
    }

    companion object {
        /** Differences below this value are ignored when comparing two zoom values. */
        const val ZoomDeltaEpsilon = 0.01f

        fun minimum(baseZoom: BaseZoomFactor, range: ZoomRange): ContentZoomFactor {
            return ContentZoomFactor(
                baseZoom = baseZoom,
                userZoom = UserZoomFactor(range.minZoomFactor(baseZoom) / baseZoom.maxScale),
            )
        }

        fun maximum(baseZoom: BaseZoomFactor, range: ZoomRange): ContentZoomFactor {
            return ContentZoomFactor(
                baseZoom = baseZoom,
                userZoom = UserZoomFactor(range.maxZoomFactor(baseZoom) / baseZoom.maxScale),
            )
        }
    }
}

internal data class ZoomRange(
    private val minZoomAsRatioOfBaseZoom: Float = 1f,
    private val maxZoomAsRatioOfSize: Float,
) {

    fun minZoomFactor(baseZoom: BaseZoomFactor): Float {
        return minZoomAsRatioOfBaseZoom * baseZoom.maxScale
    }

    fun maxZoomFactor(baseZoom: BaseZoomFactor): Float {
        // Note to self: the max zoom factor can be less than the min zoom
        // factor if the content is scaled-up by default. This can be tested
        // by setting contentScale = CenterCrop.
        return maxOf(maxZoomAsRatioOfSize, minZoomFactor(baseZoom))
    }
}
