package org.jetbrains.kotlinconf.ui.utils

import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_TYPE_NORMAL
import androidx.compose.ui.tooling.preview.Preview

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.FUNCTION)
@Preview(name = "Light", widthDp = 1000)
@Preview(name = "Dark", widthDp = 1000, uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL)
annotation class WidePreviewLightDark
