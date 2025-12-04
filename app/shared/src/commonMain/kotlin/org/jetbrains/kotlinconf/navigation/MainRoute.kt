package org.jetbrains.kotlinconf.navigation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface MainRoute

@Serializable
data object ScheduleScreen : MainRoute

@Serializable
@SerialName("Speakers")
data object SpeakersScreen : MainRoute

@Serializable
@SerialName("Map")
data object MapScreen : MainRoute

@Serializable
@SerialName("Info")
data object InfoScreen : MainRoute
