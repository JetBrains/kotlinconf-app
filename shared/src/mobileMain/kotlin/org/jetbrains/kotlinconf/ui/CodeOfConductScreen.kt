package org.jetbrains.kotlinconf.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.kotlinconf.ui.components.MarkdownFileView
import org.jetbrains.kotlinconf.ui.components.NavigationBar
import org.jetbrains.kotlinconf.ui.theme.grey5Black
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
        Column(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.grey5Black)
        ) {
            Text(
                "KotlinConf code of conduct",
                style = MaterialTheme.typography.h2,
                modifier = Modifier.padding(16.dp)
            )
            HDivider()
        }
        Column(
            Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            MarkdownFileView("files/code-of-conduct.md")
        }
    }
}