package org.jetbrains.kotlinconf.ui.components

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui.theme.PreviewHelper

private val SettingsItemShape = RoundedCornerShape(8.dp)

@Composable
fun SettingsItem(
    title: String,
    enabled: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    note: String? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier
            .fillMaxWidth()
            .clip(SettingsItemShape)
            .background(KotlinConfTheme.colors.tileBackground)
            .toggleable(
                value = enabled,
                enabled = true,
                role = Role.Switch,
                onValueChange = { onToggle(!enabled) },
                interactionSource = interactionSource,
                indication = LocalIndication.current,
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = KotlinConfTheme.typography.h3,
            )
            if (note != null) {
                Text(
                    text = note,
                    style = KotlinConfTheme.typography.text2,
                    color = KotlinConfTheme.colors.secondaryText,
                )
            }
        }
        Toggle(
            enabled = enabled,
            onToggle = onToggle,
            modifier = Modifier
                .focusProperties { canFocus = false }
                .clearAndSetSemantics {},
            interactionSource = interactionSource,
        )
    }
}

@Preview
@Composable
internal fun SettingsItemPreview() {
    PreviewHelper {
        var enabled1 by remember { mutableStateOf(false) }
        SettingsItem(
            title = "Conference schedule updates",
            note = "We recommend keeping this setting enabled to receive timely notifications about any changes or important information.",
            enabled = enabled1,
            onToggle = { enabled1 = it },
        )

        var enabled2 by remember { mutableStateOf(true) }
        SettingsItem(
            title = "Kodee containment",
            enabled = enabled2,
            onToggle = { enabled2 = it },
        )
    }
}
