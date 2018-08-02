package org.jetbrains.kotlinconf.data

import kotlinx.serialization.*

@Serializable
class Vote(
    var sessionId: String? = null,
    var rating: Int? = 0 // -1 is negative, 0 is so-so, 1 is positive
)