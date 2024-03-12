package org.jetbrains.kotlinconf.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.close
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.kotlinconf.ui.HDivider
import org.jetbrains.kotlinconf.ui.painter
import org.jetbrains.kotlinconf.ui.theme.grey5Black
import org.jetbrains.kotlinconf.ui.theme.greyGrey20
import org.jetbrains.kotlinconf.ui.theme.greyGrey5
import org.jetbrains.kotlinconf.ui.theme.greyWhite
import org.jetbrains.kotlinconf.ui.theme.whiteGrey


@OptIn(ExperimentalResourceApi::class)
@Composable
fun MarkdownScreenWithTitle(
    title: String,
    subtitle: String,
    markdownFile: String,
    showCloseButton: Boolean,
    onClose: () -> Unit
) {
    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colors.whiteGrey)
                .verticalScroll(rememberScrollState())
        ) {
            HDivider()
            Column(Modifier.background(color = MaterialTheme.colors.grey5Black).fillMaxWidth()) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.h2.copy(color = MaterialTheme.colors.greyWhite),
                    modifier = Modifier.padding(top = 24.dp, start = 16.dp, end = 16.dp)
                )

                Text(
                    subtitle,
                    style = MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.greyGrey20),
                    modifier = Modifier.padding(all = 16.dp)
                )
            }
            HDivider()
            MarkdownFileView(markdownFile)
            Spacer(Modifier.height(50.dp))
        }
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            if (showCloseButton) {
                IconButton(onClick = onClose, Modifier.size(48.dp, 48.dp)) {
                    Icon(
                        painter = Res.drawable.close.painter(),
                        "Right",
                        tint = MaterialTheme.colors.greyGrey5
                    )
                }
            }
        }
    }
}
