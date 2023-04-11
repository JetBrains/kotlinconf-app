package org.jetbrains.kotlinconf.android.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import org.jetbrains.kotlinconf.android.theme.*


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
@Preview
fun TabBarPreview() {
    var selected by remember { mutableStateOf("APRIL 14") }

    KotlinConfTheme {
        TabBar(
            tabs = listOf("APRIL 13", "APRIL 14"),
            selected = selected,
            onSelect = { selected = it }
        )
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
