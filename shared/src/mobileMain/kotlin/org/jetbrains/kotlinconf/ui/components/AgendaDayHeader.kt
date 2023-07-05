package org.jetbrains.kotlinconf.android.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import org.jetbrains.kotlinconf.android.theme.*
import org.jetbrains.kotlinconf.android.ui.*


@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun AgendaDayHeader(title: String) {
    val text = title.uppercase() + "  "
    Column(
        Modifier.background(MaterialTheme.colors.whiteGrey)
    ) {
        Row(
            Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            VDivider(modifier = Modifier.height(24.dp))
        }
        HDivider()
        Row(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.blackWhite)
        ) {
            Text(
                modifier = Modifier
                    .basicMarquee(),
                text = text.repeat(100),
                maxLines = 1,
                style = MaterialTheme.typography.body2.copy(
                    color = MaterialTheme.colors.whiteBlack
                )
            )
        }
        HDivider()
        Row(
            Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            VDivider(modifier = Modifier.height(24.dp))
        }
        HDivider()
    }
}

@Composable
@Preview(showBackground = true)
private fun AgendaDayHeaderPreview() {
    KotlinConfTheme {
        AgendaDayHeader("APRIL 14")
    }
}


@Composable
@Preview(showBackground = true)
private fun AgendaDayHeaderDarkPreview() {
    KotlinConfTheme(true) {
        AgendaDayHeader("APRIL 14")
    }
}