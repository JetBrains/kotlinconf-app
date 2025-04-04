package org.jetbrains.kotlinconf.backend.model

import kotlinx.serialization.Serializable


@Serializable
data class GitHubItem(
    val name: String,
    val path: String,
    val type: String,
    val download_url: String? = null
)