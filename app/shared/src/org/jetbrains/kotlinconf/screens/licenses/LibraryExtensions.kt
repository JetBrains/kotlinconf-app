package org.jetbrains.kotlinconf.screens.licenses

import com.mikepenz.aboutlibraries.entity.Library

internal val Library.author: String
    get() = when {
        developers.isNotEmpty() -> developers.joinToString { it.name.toString() }
        else -> organization?.name ?: ""
    }

internal val Library.licenseName: String
    get() = licenses.firstOrNull()?.name ?: "Unknown license"
