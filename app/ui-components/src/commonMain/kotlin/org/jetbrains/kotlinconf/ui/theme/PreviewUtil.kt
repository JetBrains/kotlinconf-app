package org.jetbrains.kotlinconf.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PreviewHelper(
    paddingEnabled: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column {
        KotlinConfTheme(darkTheme = false) {
            PreviewColumn(paddingEnabled, content)
        }
        KotlinConfTheme(darkTheme = true) {
            PreviewColumn(paddingEnabled, content)
        }
    }
}

@Composable
private fun PreviewColumn(
    paddingEnabled: Boolean,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = Modifier
            .background(KotlinConfTheme.colors.mainBackground)
            .padding(
                if (paddingEnabled) 8.dp else 0.dp,
            ),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        content()
    }
}
