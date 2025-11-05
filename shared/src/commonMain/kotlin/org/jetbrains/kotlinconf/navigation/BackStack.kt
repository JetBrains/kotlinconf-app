package org.jetbrains.kotlinconf.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.navigation3.runtime.NavKey
import androidx.savedstate.compose.serialization.serializers.SnapshotStateListSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer

@Composable
inline fun <reified T : Any> rememberNavBackStack(vararg elements: T): MutableList<T> {
    val elementSerializer = serializer<T>()
    return rememberSerializable(serializer = SnapshotStateListSerializer(elementSerializer)) {
        mutableStateListOf(*elements)
    }
}
