package org.jetbrains.kotlinconf.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toolingGraphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.kotlinconf.ui.generated.resources.UiRes
import org.jetbrains.kotlinconf.ui.generated.resources.bookmark_24
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui.theme.PreviewHelper
import androidx.compose.ui.tooling.preview.PreviewLightDark

/**
 * Based on the Material 3 Icon implementation.
 */
@Composable
fun Icon(
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = Color.Black,
) {
    val colorFilter = remember(tint) {
        if (tint == Color.Unspecified) null else ColorFilter.tint(tint)
    }
    Box(
        modifier
            .toolingGraphicsLayer()
            .defaultSizeFor(painter)
            .paint(painter, colorFilter = colorFilter, contentScale = ContentScale.Fit)
            .then(
                if (contentDescription != null) {
                    Modifier.semantics {
                        this.contentDescription = contentDescription
                        role = Role.Image
                    }
                } else {
                    Modifier
                }
            )
    )
}

@Composable
fun Icon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = Color.Black,
) {
    Icon(
        painter = rememberVectorPainter(imageVector),
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint,
    )
}

private fun Modifier.defaultSizeFor(painter: Painter) = this.then(
    if (painter.intrinsicSize == Size.Unspecified || painter.intrinsicSize.isInfinite()) {
        Modifier.size(24.dp)
    } else {
        Modifier
    }
)

private fun Size.isInfinite() = width.isInfinite() && height.isInfinite()

@PreviewLightDark
@Composable
private fun IconPreview() = PreviewHelper {
    Icon(
        painter = painterResource(UiRes.drawable.bookmark_24),
        contentDescription = null,
        tint = KotlinConfTheme.colors.primaryText,
    )
}

@PreviewLightDark
@Composable
private fun TintedIconPreview() = PreviewHelper {
    Icon(
        painter = painterResource(UiRes.drawable.bookmark_24),
        contentDescription = null,
        tint = Color(0xFFFF5A13),
    )
}
