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
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.kotlinconf.theme.*

data class TagView(
    val name: String,
    val isActive: Boolean
)

@Composable
fun SearchTag(name: String, isActive: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = if (isActive) orange else MaterialTheme.colors.grey20Grey80,
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

@Preview
@Composable
fun SearchTagPreview() {
    SearchTag("Android", false) {}
}