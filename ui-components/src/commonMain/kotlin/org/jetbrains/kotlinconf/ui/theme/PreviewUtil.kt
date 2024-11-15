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
fun PreviewHelper(content: @Composable ColumnScope.() -> Unit) {
    Column {
        KotlinConfTheme(darkTheme = false) {
            PreviewColumn(content)
        }
        KotlinConfTheme(darkTheme = true) {
            PreviewColumn(content)
        }
    }
}

@Composable
private fun PreviewColumn(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .background(KotlinConfTheme.colors.mainBackground)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        content()
    }
}
