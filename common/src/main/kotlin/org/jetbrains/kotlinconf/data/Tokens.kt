package org.jetbrains.kotlinconf.data

import kotlinx.serialization.*

@Serializable
data class UserTokens(val tokens: List<String>)