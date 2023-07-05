package org.jetbrains.kotlinconf.android.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import com.jetbrains.kotlinconf.R
import org.jetbrains.kotlinconf.android.theme.*
import org.jetbrains.kotlinconf.android.ui.*

@Composable
fun Party(isFinished: Boolean) {
    Column {
        Image(
            painter = painterResource(id = R.drawable.party),
            contentDescription = "party",
            modifier = Modifier
                .alpha(if (isFinished) 0.5f else 1f)
                .fillMaxWidth(),
            contentScale = ContentScale.Crop,
        )
        HDivider()
    }
}

@Composable
@Preview(showBackground = true)
fun PartyPreview() {
    KotlinConfTheme {
        Column {
            Party(isFinished = false)
            Party(isFinished = true)
        }
    }
}