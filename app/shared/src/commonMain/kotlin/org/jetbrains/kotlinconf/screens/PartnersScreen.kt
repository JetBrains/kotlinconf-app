package org.jetbrains.kotlinconf.screens

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.zacsweers.metrox.viewmodel.metroViewModel
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.PartnerId
import org.jetbrains.kotlinconf.ScrollToTopHandler
import org.jetbrains.kotlinconf.generated.resources.Res
import org.jetbrains.kotlinconf.navigation.LocalUseNativeNavigation
import org.jetbrains.kotlinconf.generated.resources.partners_error
import org.jetbrains.kotlinconf.generated.resources.partners_title
import org.jetbrains.kotlinconf.ui.components.HorizontalDivider
import org.jetbrains.kotlinconf.ui.components.MainHeaderTitleBar
import org.jetbrains.kotlinconf.ui.components.MajorError
import org.jetbrains.kotlinconf.ui.components.PartnerCard
import org.jetbrains.kotlinconf.ui.components.Text
import org.jetbrains.kotlinconf.ui.components.TopMenuButton
import org.jetbrains.kotlinconf.ui.generated.resources.UiRes
import org.jetbrains.kotlinconf.ui.generated.resources.arrow_left_24
import org.jetbrains.kotlinconf.ui.generated.resources.main_header_back
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.utils.FadingAnimationSpec
import org.jetbrains.kotlinconf.utils.bottomInsetPadding
import org.jetbrains.kotlinconf.utils.plus
import org.jetbrains.kotlinconf.utils.topInsetPadding


@Composable
fun PartnersScreen(
    onBack: () -> Unit,
    onPartnerDetail: (partnerId: PartnerId) -> Unit,
    viewModel: PartnersViewModel = metroViewModel(),
) {
    val partnerGroups by viewModel.partnerGroups.collectAsStateWithLifecycle()
    val isDark = KotlinConfTheme.colors.isDark

    Column(
        Modifier.fillMaxSize()
            .background(color = KotlinConfTheme.colors.mainBackground)
            .padding(topInsetPadding())
    ) {
        if (!LocalUseNativeNavigation.current) {
            MainHeaderTitleBar(
                title = stringResource(Res.string.partners_title),
                startContent = {
                    TopMenuButton(
                        icon = UiRes.drawable.arrow_left_24,
                        contentDescription = stringResource(UiRes.string.main_header_back),
                        onClick = onBack,
                    )
                }
            )

            HorizontalDivider(thickness = 1.dp, color = KotlinConfTheme.colors.strokePale)
        }

        AnimatedContent(
            targetState = partnerGroups.isNotEmpty(),
            modifier = Modifier.fillMaxSize(),
            transitionSpec = { FadingAnimationSpec },
        ) { hasPartners ->
            if (hasPartners) {
                val lazyListState = rememberLazyListState()
                ScrollToTopHandler(lazyListState)
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                    contentPadding = PaddingValues(vertical = 12.dp) + bottomInsetPadding(),
                    state = lazyListState,
                ) {
                    for (group in partnerGroups) {
                        stickyHeader {
                            Text(
                                text = group.level,
                                style = KotlinConfTheme.typography.h1,
                                modifier = Modifier.fillMaxWidth()
                                    .background(KotlinConfTheme.colors.mainBackground)
                                    .padding(vertical = 12.dp)
                            )
                        }
                        items(group.partners) { partner ->
                            val logoUrl = if (isDark) partner.logoUrlDark else partner.logoUrlLight
                            PartnerCard(
                                name = partner.name,
                                logoUrl = logoUrl,
                                onClick = { onPartnerDetail(partner.id) }
                            )
                        }
                    }
                }
            } else {
                MajorError(
                    message = stringResource(Res.string.partners_error),
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}
