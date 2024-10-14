package org.jetbrains.kotlinconf.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.ui.theme.grey50
import org.jetbrains.kotlinconf.ui.theme.greyWhite
import org.jetbrains.kotlinconf.ui.theme.text2
import org.jetbrains.kotlinconf.ui.theme.whiteGrey

@OptIn(ExperimentalResourceApi::class)
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

    Row(
        modifier = Modifier
            .background(
                background,
                shape = RoundedCornerShape(4.dp),
            )
            .clickable { onSelect() }
            .height(28.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(tab.title),
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp, top = 2.dp, bottom = 2.dp),
            style = text2,
            color = textColor
        )
    }
}
