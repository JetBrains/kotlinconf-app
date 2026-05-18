package org.jetbrains.kotlinconf.navigation

import androidx.compose.runtime.Composable

@Composable
expect fun BrowserIntegration(navState: NavState)
