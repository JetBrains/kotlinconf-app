package org.jetbrains.kotlinconf.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.arrow_left_24
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.kotlinconf.SessionCardView
import org.jetbrains.kotlinconf.ui.components.StyledText

@Composable
fun Session(
    session: SessionCardView,
    onBack: () -> Unit,
) {
    Column {
        Image(
            painterResource(Res.drawable.arrow_left_24),
            "back",
            modifier = Modifier.clickable(onClick = onBack)
        )
        StyledText(session.title)
        StyledText(session.locationLine)
    }
}
