package org.jetbrains.kotlinconf.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.serialization.NavBackStackSerializer
import kotlinx.serialization.serializer

@Composable
inline fun <reified T : NavKey> rememberNavBackStack(vararg elements: T): NavBackStack<T> {
    return rememberSerializable(serializer = NavBackStackSerializer(serializer())) {
        NavBackStack(*elements)
    }
}
