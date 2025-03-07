package org.jetbrains.kotlinconf

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toInstant
import org.jetbrains.kotlinconf.utils.Logger
import org.koin.mp.KoinPlatform

const val EXTRA_NOTIFICATION_ID = "notificationId"
private const val EXTRA_TITLE = "title"
private const val EXTRA_MESSAGE = "message"
private const val NOTIFICATION_CHANNEL_ID = "channel_all_notifications"
private const val ACTION_SHOW_NOTIFICATION = "org.jetbrains.kotlinconf.SHOW_NOTIFICATION"

class AndroidNotificationService(
    private val timeProvider: TimeProvider,
    private val context: Context,
    private val iconId: Int,
    private val logger: Logger,
) : NotificationService {

    companion object {
        private const val LOG_TAG = "AndroidNotificationService"
    }

    private val notificationManager = NotificationManagerCompat.from(context)
    private val alarmManager = context.getSystemService<AlarmManager>()

    override suspend fun requestPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true

        val permissions = arrayOf(Manifest.permission.POST_NOTIFICATIONS, Manifest.permission.USE_EXACT_ALARM)

        val permissionHandler = KoinPlatform.getKoin().getOrNull<PermissionHandler>()
        return permissionHandler?.requestPermissions(permissions) ?: false
    }

    override fun post(
        notificationId: String,
        title: String,
        message: String,
        time: LocalDateTime?,
    ) {
        logger.log(LOG_TAG) { "Posting notification: $notificationId, $title at $time" }
        if (time != null) {
            scheduleNotification(
                title = title,
                message = message,
                notificationId = notificationId,
                time = time,
            )
        } else {
            showNotification(
                title = title,
                message = message,
                notificationId = notificationId,
            )
        }
    }

    private fun scheduleNotification(
        title: String,
        message: String,
        notificationId: String,
        time: LocalDateTime
    ) {
        alarmManager ?: return

        val intent = Intent(context, AlarmBroadcastReceiver::class.java)
            .setAction(ACTION_SHOW_NOTIFICATION)
            .putExtra(EXTRA_TITLE, title)
            .putExtra(EXTRA_MESSAGE, message)
            .putExtra(EXTRA_NOTIFICATION_ID, notificationId)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val triggerTime = timeProvider.getNotificationTime(time).toInstant(EVENT_TIME_ZONE)
        val triggerAtMillis = triggerTime.toEpochMilliseconds()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.USE_EXACT_ALARM)
            != PackageManager.PERMISSION_GRANTED
        ) {
            logger.log(LOG_TAG) { "No permission to schedule notification $notificationId" }
            return
        }

        logger.log(LOG_TAG) { "Setting alarm for notification $notificationId, $triggerTime ($triggerAtMillis)" }
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
    }

    private fun showNotification(
        title: String,
        message: String,
        notificationId: String,
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            logger.log(LOG_TAG) { "Skipping notification $notificationId, no permission" }
            return
        }

        notificationManager.createNotificationChannel(
            NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "All notifications",
                android.app.NotificationManager.IMPORTANCE_HIGH
            )
        )

        val mainActivityIntent = Intent(context, Class.forName("org.jetbrains.kotlinconf.android.MainActivity"))
            .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            .putExtra(EXTRA_NOTIFICATION_ID, notificationId)
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId.hashCode(),
            mainActivityIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(iconId)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        logger.log(LOG_TAG) { "Showing notification: $notificationId, $notification" }
        notificationManager.notify(notificationId.hashCode(), notification)
    }

    override fun cancel(notificationId: String) {
        logger.log(LOG_TAG) { "Canceling notification $notificationId" }

        // Cancel the notification if it's currently shown
        notificationManager.cancel(notificationId.hashCode())
        logger.log(LOG_TAG) { "Canceled existing notification $notificationId" }

        // Cancel any pending alarms for this notification
        val intent = Intent(context, AlarmBroadcastReceiver::class.java).apply {
            action = ACTION_SHOW_NOTIFICATION
        }
        val pendingIntent: PendingIntent? = PendingIntent.getBroadcast(
            context,
            notificationId.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
        )
        pendingIntent?.let { alarmManager?.cancel(it) }
        logger.log(LOG_TAG) { "Canceled scheduled notification $notificationId" }
    }
}

class AlarmBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_SHOW_NOTIFICATION) return

        val title = intent.getStringExtra(EXTRA_TITLE) ?: return
        val message = intent.getStringExtra(EXTRA_MESSAGE) ?: return
        val notificationId = intent.getStringExtra(EXTRA_NOTIFICATION_ID) ?: return

        val notificationService = KoinPlatform.getKoin().get<NotificationService>()
        notificationService.post(
            title = title,
            message = message,
            notificationId = notificationId,
        )
    }
}
