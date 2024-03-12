package org.jetbrains.kotlinconf.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.kotlinconf.ui.components.MarkdownScreenWithTitle
import org.jetbrains.kotlinconf.ui.components.NavigationBar
import org.jetbrains.kotlinconf.ui.theme.whiteGrey

@OptIn(ExperimentalResourceApi::class)
@Composable
fun CodeOfConductScreen(back: () -> Unit) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colors.whiteGrey)
            .fillMaxHeight()
            .fillMaxWidth()
    ) {
        NavigationBar(
            title = "Code of conduct",
            isLeftVisible = true,
            onLeftClick = back,
            isRightVisible = false
        )
        MarkdownScreenWithTitle(
            "Code of conduct",
            "",
            "files/code-of-conduct.md",
            false
        ) {}
    }
}