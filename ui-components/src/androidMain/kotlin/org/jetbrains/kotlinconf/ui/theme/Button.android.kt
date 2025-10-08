package org.jetbrains.kotlinconf.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import org.jetbrains.kotlinconf.ui.components.Button


@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.FUNCTION)
@Preview(name = "85%", fontScale = 0.85f)
@Preview(name = "100%", fontScale = 1.0f)
@Preview(name = "115%", fontScale = 1.15f, device = "spec:parent=pixel_5,orientation=landscape,navigation=buttons")
@Preview(name = "130%", fontScale = 1.3f)
@Preview(name = "150%", fontScale = 1.5f)
@Preview(name = "180%", fontScale = 1.8f)
@Preview(name = "200%", fontScale = 2f)
annotation class PreviewFontScale

@PreviewFontScale
@Composable
internal fun ButtonPreview() {
    KotlinConfTheme(darkTheme = false) {
        Button("Font scale preview internal", { }, primary = true)
    }
}

@KotlinConfPreview
@Composable
internal fun ButtonPreviewExt() {
    KotlinConfTheme {
        Button("Font scale preview external", { }, primary = false)
    }
}
