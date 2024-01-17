package org.jetbrains.kotlinconf.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.kotlinconf.theme.t2
import org.jetbrains.kotlinconf.theme.grey50
import org.jetbrains.kotlinconf.theme.greyWhite
import org.jetbrains.kotlinconf.theme.whiteGrey
import org.jetbrains.kotlinconf.ui.HDivider

@Composable
fun TabBar(
    tabs: List<String>,
    selected: String? = null,
    onSelect: (item: String) -> Unit = {},
) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.whiteGrey)
    ) {
        Box {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                tabs.forEach { tab ->
                    val isActive = selected == tab
                    TabButton(tab, isSelected = isActive) { onSelect(tab) }
                }
            }
        }

        HDivider(Modifier.padding(top = 10.dp))
    }
}

@Composable
fun TabButton(tab: String, isSelected: Boolean, onSelect: () -> Unit) {
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
                shape = RoundedCornerShape(5.dp),
            )
            .clickable { onSelect() }
    ) {
        Text(
            text = tab.uppercase(),
            modifier = Modifier
                .padding(start = 7.dp, end = 8.dp, top = 2.dp, bottom = 2.dp),
            style = MaterialTheme.typography.t2,
            color = textColor
        )
    }
}
