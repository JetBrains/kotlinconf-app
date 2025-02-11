package org.jetbrains.kotlinconf

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.preference.PreferenceManager
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.SharedPreferencesSettings
import kotlinx.coroutines.channels.Channel
import org.koin.core.module.Module
import org.koin.dsl.module

fun platformModule(
    activity: ComponentActivity,
    application: Application,
    notificationIconId: Int,
): Module {
    val permissionResult = Channel<Boolean>()
    val permissionLauncher = activity.registerForActivityResult(RequestMultiplePermissions()) { result ->
        val allGranted = result.all { it.value }
        permissionResult.trySend(allGranted)
    }

    return module {
        single<ObservableSettings> {
            SharedPreferencesSettings(PreferenceManager.getDefaultSharedPreferences(application))
        }
        single<NotificationService> {
            AndroidNotificationService(
                timeProvider = get(),
                context = application,
                permissionResult = permissionResult,
                permissionLauncher = permissionLauncher,
                iconId = notificationIconId,
            )
        }
    }
}
