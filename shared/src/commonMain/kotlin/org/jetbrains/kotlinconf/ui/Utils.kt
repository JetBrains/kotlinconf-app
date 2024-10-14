package org.jetbrains.kotlinconf.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.jetbrains.kotlinconf.ui.theme.divider
import org.jetbrains.kotlinconf.ui.theme.grey50
import org.jetbrains.kotlinconf.ui.theme.greyWhite

@Composable
fun HDivider(modifier: Modifier = Modifier) {
    Divider(modifier.background(MaterialTheme.colors.divider))
}

@Composable
fun VDivider(modifier: Modifier = Modifier) {
    Divider(
        modifier
            .background(MaterialTheme.colors.divider)
            .width(1.dp)
    )
}

@Composable
fun LocationRow(location: String, modifier: Modifier = Modifier) {
    Row(modifier) {
        Text(
            location,
            style = MaterialTheme.typography.body2.copy(
                color = grey50
            ),
        )
    }
}

@Composable
fun ColumnScope.SheetBar() {
    Row(
        Modifier
            .width(96.dp)
            .height(4.dp)
            .background(MaterialTheme.colors.greyWhite)
            .clip(RoundedCornerShape(4.dp))
            .align(Alignment.CenterHorizontally)
    ) {}
}
