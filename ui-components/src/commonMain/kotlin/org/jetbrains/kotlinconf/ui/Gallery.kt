package org.jetbrains.kotlinconf.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun GalleryApp() {
    Column {
        Column(Modifier.verticalScroll(rememberScrollState())) {
            ActionPreview()
            ButtonPreview()
            CardTagPreview()
            DayHeaderPreview()
            FeedbackFormPreview()
            FilterTagPreview()
            FiltersPreview()
            KodeeIconsPreview()
            LoadingPreview()
            MainHeaderPreview()
            MainNavigationPreview()
            NowButtonPreview()
            NowLabelPreview()
            PageMenuItemPreview()
            PageTitlePreview()
            ParagraphTitlePreview()
            PartnerCardPreview()
            ScrollIndicatorPreview()
            SectionTitlePreview()
            ServiceEventsPreview()
            SettingsItemPreview()
            SpeakerCardPreview()
            SwitcherItemPreview()
            SwitcherPreview()
            TalkCardPreview()
            TogglePreview()
            TopMenuButtonPreview()
        }
    }
}
