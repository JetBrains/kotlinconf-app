package org.jetbrains.kotlinconf.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList

@Composable
actual fun BrowserIntegration(backStack: SnapshotStateList<AppRoute>) {}