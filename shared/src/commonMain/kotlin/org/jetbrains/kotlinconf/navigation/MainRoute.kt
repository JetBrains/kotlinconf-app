package org.jetbrains.kotlinconf.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface MainRoute : NavKey

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
