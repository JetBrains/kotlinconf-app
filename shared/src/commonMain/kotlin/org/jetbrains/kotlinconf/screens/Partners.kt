package org.jetbrains.kotlinconf.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.partners_title
import kotlinconfapp.ui_components.generated.resources.arrow_left_24
import kotlinconfapp.ui_components.generated.resources.main_header_back
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.PARTNERS
import org.jetbrains.kotlinconf.PartnerId
import org.jetbrains.kotlinconf.ScrollToTopHandler
import org.jetbrains.kotlinconf.ui.components.Divider
import org.jetbrains.kotlinconf.ui.components.MainHeaderTitleBar
import org.jetbrains.kotlinconf.ui.components.PartnerCard
import org.jetbrains.kotlinconf.ui.components.SectionTitle
import org.jetbrains.kotlinconf.ui.components.StyledText
import org.jetbrains.kotlinconf.ui.components.TopMenuButton
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme


@Composable
fun Partners(
    onBack: () -> Unit,
    onPartnerDetail: (partnerId: PartnerId) -> Unit,
) {
    Column(Modifier.fillMaxSize()) {
        MainHeaderTitleBar(
            title = stringResource(Res.string.partners_title),
            startContent = {
                TopMenuButton(
                    icon = kotlinconfapp.ui_components.generated.resources.Res.drawable.arrow_left_24,
                    contentDescription = stringResource(kotlinconfapp.ui_components.generated.resources.Res.string.main_header_back),
                    onClick = onBack,
                )
            }
        )

        Divider(thickness = 1.dp, color = KotlinConfTheme.colors.strokePale)

        val lazyListState = rememberLazyListState()
        ScrollToTopHandler(lazyListState)
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            state = lazyListState,
        ) {
            for ((level, partners) in PARTNERS) {
                stickyHeader {
                    StyledText(
                        text = stringResource(level),
                        style = KotlinConfTheme.typography.h1,
                        modifier = Modifier.fillMaxWidth().background(KotlinConfTheme.colors.mainBackground).padding(vertical = 12.dp)
                    )
                }
                items(partners) { partner ->
                    PartnerCard(
                        name = partner.name,
                        logo = partner.logo(KotlinConfTheme.colors.isDark),
                        onClick = { onPartnerDetail(partner.id) }
                    )
                }
            }
        }
    }
}
