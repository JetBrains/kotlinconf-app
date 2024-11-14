package org.jetbrains.kotlinconf.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun PreviewHelper(content: @Composable ColumnScope.() -> Unit) {
    Row {
        KotlinConfTheme(darkTheme = false) {
            Column(Modifier.background(KotlinConfTheme.colors.mainBackground)) {
                content()
            }
        }
        KotlinConfTheme(darkTheme = true) {
            Column(Modifier.background(KotlinConfTheme.colors.mainBackground)) {
                content()
            }
        }
    }
}
