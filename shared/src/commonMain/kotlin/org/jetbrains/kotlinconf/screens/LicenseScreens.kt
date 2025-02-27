package org.jetbrains.kotlinconf.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Library
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.licenses_title
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.ScreenWithTitle
import org.jetbrains.kotlinconf.ScrollToTopHandler
import org.jetbrains.kotlinconf.ui.components.StyledText
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.utils.bottomInsetPadding
import org.jetbrains.kotlinconf.utils.plus

private val Library.licenseName: String
    get() = licenses.firstOrNull()?.name ?: "Unknown license"

private val Library.licenseContent: String
    get() = licenses.firstOrNull()?.licenseContent ?: ""

private val Library.author: String
    get() = when {
        developers.isNotEmpty() -> developers.joinToString { it.name.toString() }
        else -> organization?.name ?: ""
    }

@Composable
fun LicensesScreen(
    onLicenseClick: (licenseName: String, licenseText: String) -> Unit,
    onBack: () -> Unit,
) {
    val scrollState = rememberScrollState()
    ScrollToTopHandler(scrollState)
    ScreenWithTitle(
        title = stringResource(Res.string.licenses_title),
        onBack = onBack,
        contentScrollState = scrollState,
    ) {
        val libraries by produceState<Libs?>(initialValue = null) {
            value = withContext(Dispatchers.Default) {
                // To update licenses:
                // gradlew :shared:exportLibraryDefinitions -PaboutLibraries.exportPath=src/commonMain/composeResources/files
                @OptIn(ExperimentalResourceApi::class)
                val json = Res.readBytes("files/aboutlibraries.json").decodeToString()
                Libs.Builder().withJson(json).build()
            }
        }

        LibraryList(
            libraries = libraries,
            onLicenseClick = onLicenseClick,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
fun SingleLicenseScreen(
    licenseName: String,
    licenseContent: String,
    onBack: () -> Unit,
) {
    ScreenWithTitle(title = licenseName, onBack = onBack) {
        StyledText(
            licenseContent,
            style = KotlinConfTheme.typography.text2,
            color = KotlinConfTheme.colors.noteText,
            modifier = Modifier.padding(PaddingValues(vertical = 12.dp) + bottomInsetPadding()),
        )
    }
}

@Composable
private fun LibraryList(
    libraries: Libs?,
    onLicenseClick: (licenseName: String, licenseText: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    libraries ?: return

    val sortedLibraries = remember(libraries) {
        libraries.libraries.sortedBy { it.name }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp) + bottomInsetPadding(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(sortedLibraries) { library ->
            LibraryItem(
                library = library,
                onLicenseClick = {
                    onLicenseClick(library.licenseName, library.licenseContent)
                },
            )
        }
    }
}

@Composable
private fun LibraryItem(
    library: Library,
    onLicenseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = KotlinConfTheme.colors.tileBackground,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onLicenseClick)
            .padding(12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StyledText(
                text = library.name,
                style = KotlinConfTheme.typography.h3,
                color = KotlinConfTheme.colors.primaryText,
                modifier = Modifier.weight(1f)
            )
            StyledText(
                text = library.artifactVersion ?: "",
                style = KotlinConfTheme.typography.text2,
                color = KotlinConfTheme.colors.secondaryText,
                maxLines = 1,
            )
        }
        Spacer(modifier = Modifier.size(4.dp))
        StyledText(
            text = library.author,
            style = KotlinConfTheme.typography.text2,
            color = KotlinConfTheme.colors.secondaryText,
        )
        Spacer(modifier = Modifier.size(4.dp))
        StyledText(
            text = library.licenseName,
            style = KotlinConfTheme.typography.text2,
            color = KotlinConfTheme.colors.primaryText,
        )
    }
}
