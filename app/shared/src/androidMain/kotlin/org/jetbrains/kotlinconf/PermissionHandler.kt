package org.jetbrains.kotlinconf

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import kotlinx.coroutines.channels.Channel
import org.jetbrains.kotlinconf.utils.Logger
import org.koin.core.annotation.Singleton

@Singleton
class PermissionHandler(
    private val logger: Logger,
) {
    private var activity: ComponentActivity? = null

    fun initialize(activity: ComponentActivity) {
        this.activity = activity
        permissionLauncher = activity
            .registerForActivityResult<Array<String>, Map<String, @JvmSuppressWildcards Boolean>>(
                RequestMultiplePermissions()
            ) { result ->
                val allGranted = result.all<String, @JvmSuppressWildcards Boolean> { it.value }
                logger.log("PermissionHandler") { "Permission result: $allGranted" }
                permissionResult.trySend(allGranted)
            }
    }

    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    private val permissionResult = Channel<Boolean>()

    suspend fun requestPermissions(permissions: Array<String>): Boolean {
        val activity = activity
        if (activity == null) {
            logger.log("PermissionHandler") { "Requesting permission but activity is not set" }
            return false
        }

        logger.log("PermissionHandler") { "Requesting permission" }
        permissionLauncher.launch(permissions)
        return permissionResult.receive()
    }
}
