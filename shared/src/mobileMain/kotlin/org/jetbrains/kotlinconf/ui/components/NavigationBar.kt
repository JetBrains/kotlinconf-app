package org.jetbrains.kotlinconf.android.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.jetbrains.kotlinconf.R
import org.jetbrains.kotlinconf.android.theme.*

@Composable
fun NavigationBar(
    title: String,
    isLeftVisible: Boolean = true,
    isRightVisible: Boolean = true,
    rightIcon: Int = R.drawable.menu,
    onLeftClick: () -> Unit = {},
    onRightClick: () -> Unit = {},
) {
    Column(
        Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier
                .height(48.dp)
                .background(MaterialTheme.colors.whiteGrey)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (isLeftVisible) {
                IconButton(onClick = onLeftClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.back),
                        "Back",
                        tint = MaterialTheme.colors.greyGrey5
                    )
                }
            } else {
                Spacer(Modifier.width(36.dp))
            }

            Spacer(Modifier.weight(1f))

            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.t2,
                color = grey50
            )

            Spacer(Modifier.weight(1f))

            if (isRightVisible) {
                RightButton(rightIcon, onRightClick = onRightClick)
            } else {
                Spacer(Modifier.width(36.dp))
            }
        }
        Divider(color = MaterialTheme.colors.divider)
    }
}

@Composable
fun RightButton(rightIcon: Int = R.drawable.menu, onRightClick: () -> Unit) {
    IconButton(onClick = onRightClick) {
        Icon(
            painter = painterResource(id = rightIcon),
            "Right",
            tint = MaterialTheme.colors.greyGrey5
        )
    }
}

@Composable
@Preview
fun NavigationBarPreview() {
    KotlinConfTheme {
        NavigationBar(
            title = "Speakers",
            isLeftVisible = true,
            onLeftClick = {},
            onRightClick = {}
        )
    }
}


@Composable
@Preview
fun NavigationBarPreviewWithoutBack() {
    KotlinConfTheme {
        NavigationBar(
            title = "Speakers",
            isLeftVisible = false,
            onLeftClick = {},
            onRightClick = {},
        )
    }
}