package org.jetbrains.kotlinconf.ui.theme

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.UiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.UiModes.UI_MODE_TYPE_NORMAL

@Preview(
    name = "Light",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
)
@Preview(
    name = "Dark",
    uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL,
    showBackground = true,
    backgroundColor = 0xFF000000,
)
annotation class KotlinConfPreview
