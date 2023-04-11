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
import kotlinx.coroutines.*
import org.jetbrains.kotlinconf.android.*
import org.jetbrains.kotlinconf.android.theme.*
import org.jetbrains.kotlinconf.android.ui.components.*


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Map() {
    val coroutineScope = rememberCoroutineScope()
    var floor by remember { mutableStateOf("FLOOR 0") }
    val uri = FLOORS(floor)

    var title by remember { mutableStateOf("KotlinConf") }
    var showBottomSheet by remember { mutableStateOf(false) }
    val modalState = rememberBottomSheetScaffoldState()
    var description by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }

    BottomSheetScaffold(
        sheetContent = {
            if (showBottomSheet) {
                BottomSheet(
                    displayName = displayName,
                    logo = LogoForName(title.lowercase()),
                    description = description
                ) {
                    coroutineScope.launch(Dispatchers.Main) {
                        showBottomSheet = false
                    }
                }
            }
        },
        scaffoldState = modalState,
        sheetPeekHeight = if (showBottomSheet) 200.dp else 0.dp
    ) {
        Column {
            TabBar(
                tabs = listOf("FLOOR -1", "FLOOR 0", "FLOOR 1"),
                selected = floor,
                onSelect = { floor = it },
            )
            MapBoxMap(uri) { name, newDisplayName, newDescription ->
                title = name
                description = newDescription
                displayName = newDisplayName
                coroutineScope.launch(Dispatchers.Main) {
                    modalState.bottomSheetState.expand()
                    showBottomSheet = true
                }
            }
        }
    }
}

@Composable
fun BottomSheet(
    displayName: String,
    logo: Int,
    description: String,
    onClose: () -> Unit = {}
) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.whiteGrey)
            .verticalScroll(rememberScrollState())
    ) {
        SheetBar()
        Row {
            Spacer(Modifier.weight(1f))
            IconButton(onClick = onClose) {
                Icon(
                    painter = painterResource(id = R.drawable.close),
                    contentDescription = "close",
                    tint = MaterialTheme.colors.greyWhite
                )
            }
        }

        Text(
            displayName.uppercase(),
            style = MaterialTheme.typography.h2.copy(
                color = MaterialTheme.colors.blackGrey5
            ),
            modifier = Modifier.padding(16.dp)
        )

        Image(
            painter = painterResource(id = logo),
            contentDescription = "logo",
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            description,
            style = MaterialTheme.typography.t2.copy(color = MaterialTheme.colors.greyGrey20),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun MapPreview() {
    KotlinConfTheme {
        Map()
    }
}

@Preview(showSystemUi = true)
@Composable
fun BottomSheetPreview() {
    KotlinConfTheme {
        BottomSheet(
            displayName = "KotlinConf",
            logo = R.drawable.android_google_big,
            description = "KotlinConf is the official Kotlin conference, organized by JetBrains. It is a two-day event with a single track of talks, a hackathon, and a social program. The conference is held in San Francisco, California, USA."
        )
    }
}
