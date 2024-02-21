package org.jetbrains.kotlinconf.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.kotlinconf.theme.grey50
import org.jetbrains.kotlinconf.theme.greyWhite
import org.jetbrains.kotlinconf.theme.text2
import org.jetbrains.kotlinconf.theme.whiteGrey
import org.jetbrains.kotlinconf.ui.Floor

@Composable
fun TabButton(tab: Tab, isSelected: Boolean, onSelect: () -> Unit) {
    val background = if (isSelected)
        MaterialTheme.colors.greyWhite
    else
        MaterialTheme.colors.whiteGrey

    val textColor = if (isSelected) {
        MaterialTheme.colors.whiteGrey
    } else {
        grey50
    }

    Box(
        modifier = Modifier
            .padding(top = 16.dp, bottom = 16.dp)
            .background(
                background,
                shape = RoundedCornerShape(4.dp),
            )
            .clickable { onSelect() }
    ) {
        Text(
            text = tab.title,
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp, top = 2.dp, bottom = 2.dp),
            style = text2,
            color = textColor
        )
    }
}

@Preview
@Composable
fun TabButtonPreview() {
    Column {
        TabButton(Floor.FIRST, isSelected = true) {}
        TabButton(Floor.SECOND, isSelected = false) {}
    }
}
