package org.jetbrains.kotlinconf.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.kotlinconf.ui.theme.black
import org.jetbrains.kotlinconf.ui.theme.greyWhite
import org.jetbrains.kotlinconf.ui.theme.orange
import org.jetbrains.kotlinconf.ui.theme.tagColor

@Composable
fun SearchTag(name: String, isActive: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = if (isActive) orange else MaterialTheme.colors.tagColor,
            contentColor = if (isActive) black else MaterialTheme.colors.greyWhite
        ),
        shape = MaterialTheme.shapes.medium,
        contentPadding = PaddingValues(12.dp),
        modifier = Modifier.padding(end = 4.dp)
    ) {
        Text(
            name,
            style = MaterialTheme.typography.body2
        )
    }
}
