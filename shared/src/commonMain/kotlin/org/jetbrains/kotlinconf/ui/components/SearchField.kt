package org.jetbrains.kotlinconf.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.kotlinconf.ui.theme.grey5Black
import org.jetbrains.kotlinconf.ui.theme.greyWhite
import org.jetbrains.kotlinconf.ui.theme.orange

@Composable
fun SearchField(text: String, onTextChange: (String) -> Unit) {
    Box(Modifier.fillMaxWidth()) {
        TextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.body2.copy(
                color = MaterialTheme.colors.greyWhite
            ),
            maxLines = 1,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                backgroundColor = MaterialTheme.colors.grey5Black,
                cursorColor = orange,
                textColor = MaterialTheme.colors.greyWhite,
                focusedBorderColor = MaterialTheme.colors.grey5Black
            ),
        )
        if (text.isNotEmpty()) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Button(
                    onClick = { onTextChange("") },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.grey5Black
                    ),
                    elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
                ) {
                    Text(
                        "Clear",
                        style = MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.greyWhite),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}
