package org.jetbrains.kotlinconf.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

// Multiplatform dispatcher for IO operations
// Use different name to avoid ambiguity issues.
expect val Dispatchers.IO_MP: CoroutineDispatcher