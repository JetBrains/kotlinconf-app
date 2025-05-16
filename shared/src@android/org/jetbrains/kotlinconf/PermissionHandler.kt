package org.jetbrains.kotlinconf

import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import kotlinx.coroutines.channels.Channel

class PermissionHandler(
    activity: ComponentActivity,
) {
    private val permissionResult = Channel<Boolean>()
    private val permissionLauncher = activity.registerForActivityResult(RequestMultiplePermissions()) { result ->
        val allGranted = result.all { it.value }
        permissionResult.trySend(allGranted)
    }

    suspend fun requestPermissions(permissions: Array<String>): Boolean {
        permissionLauncher.launch(permissions)
        return permissionResult.receive()
    }
}
