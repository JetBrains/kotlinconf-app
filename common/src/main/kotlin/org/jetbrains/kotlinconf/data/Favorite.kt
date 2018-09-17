package org.jetbrains.kotlinconf.data

import kotlinx.serialization.Serializable

@Serializable
data class Favorite(
    var sessionId: String
)