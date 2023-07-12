package org.jetbrains.kotlinconf.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.layout.*
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.kotlinconf.android.theme.*
import org.jetbrains.kotlinconf.android.ui.*
import org.jetbrains.kotlinconf.ui.HDivider

@OptIn(ExperimentalResourceApi::class)
@Composable
fun Party(isFinished: Boolean) {
    Column {
        Image(
            painter = painterResource("party"),
            contentDescription = "party",
            modifier = Modifier
                .alpha(if (isFinished) 0.5f else 1f)
                .fillMaxWidth(),
            contentScale = ContentScale.Crop,
        )
        HDivider()
    }
}
