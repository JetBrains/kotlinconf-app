package org.jetbrains.kotlinconf

import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.channels.Channel
import org.jetbrains.kotlinconf.utils.Logger

@Inject
class PermissionHandler(
    private val logger: Logger,
) {
    var activity: ComponentActivity? = null

    private val permissionResult = Channel<Boolean>()

    suspend fun requestPermissions(permissions: Array<String>): Boolean {
        val activity = activity
        if (activity == null) {
            logger.log("PermissionHandler") { "Requesting permission but activity is not set" }
            return false
        }

        logger.log("PermissionHandler") { "Requesting permission" }
        val permissionLauncher = activity
            .registerForActivityResult<Array<String>, Map<String, @JvmSuppressWildcards Boolean>>(
                RequestMultiplePermissions()
            ) { result ->
                val allGranted = result.all<String, @JvmSuppressWildcards Boolean> { it.value }
                logger.log("PermissionHandler") { "Permission result: $allGranted" }
                permissionResult.trySend(allGranted)
            }
        permissionLauncher.launch(permissions)
        return permissionResult.receive()
    }
}
