package org.jetbrains.kotlinconf.utils

internal val GRANTED_PERMISSION = "granted"

internal external object Notification {
    val permission: String
    fun requestPermission(callback: (String) -> Unit)
}