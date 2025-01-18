package org.jetbrains.kotlinconf.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mikepenz.aboutlibraries.ui.compose.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.LibraryDefaults
import com.mikepenz.aboutlibraries.ui.compose.rememberLibraries
import kotlinconfapp.shared.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.kotlinconf.ScreenWithTitle
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme

@Composable
fun LicensesScreen(
    onBack: () -> Unit,
) {
    ScreenWithTitle(title = "Licenses", onBack = onBack) {
        val libraries = rememberLibraries {
            // To update licenses:
            // gradlew :shared:exportLibraryDefinitions -PaboutLibraries.exportPath=src/commonMain/composeResources/files
            @OptIn(ExperimentalResourceApi::class)
            Res.readBytes("files/aboutlibraries.json").decodeToString()
        }.value

        if (libraries != null) {
            LibrariesContainer(
                { libraries },
                Modifier.weight(1f),
                colors = LibraryDefaults.libraryColors(
                    backgroundColor = KotlinConfTheme.colors.mainBackground,
                    contentColor = KotlinConfTheme.colors.primaryText,
                    badgeBackgroundColor = KotlinConfTheme.colors.tileBackground,
                    badgeContentColor = KotlinConfTheme.colors.secondaryText,
                    dialogConfirmButtonColor = KotlinConfTheme.colors.accentText,
                )
            )
        }
    }
}
