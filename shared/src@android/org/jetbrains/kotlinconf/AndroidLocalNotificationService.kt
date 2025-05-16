package org.jetbrains.kotlinconf

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager.IMPORTANCE_HIGH
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

const val EXTRA_LOCAL_NOTIFICATION_ID = "localNotificationId"
private const val EXTRA_TITLE = "title"
private const val EXTRA_MESSAGE = "message"
private const val NOTIFICATION_CHANNEL_ID = "channel_all_notifications"
private const val ACTION_SHOW_NOTIFICATION = "org.jetbrains.kotlinconf.SHOW_NOTIFICATION"

class AndroidLocalNotificationService(
    private val timeProvider: TimeProvider,
    private val context: Context,
    private val iconId: Int,
    private val logger: Logger,
) : LocalNotificationService {

    companion object {
        private const val LOG_TAG = "AndroidNotificationService"
    }

    private val notificationManager = NotificationManagerCompat.from(context)
    private val alarmManager = context.getSystemService<AlarmManager>()

    init {
        val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, "All notifications", IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(channel)
    }

    private fun getRelevantAlarmPermission(): String? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            return Manifest.permission.USE_EXACT_ALARM

        if (Build.VERSION.SDK_INT in Build.VERSION_CODES.S..<Build.VERSION_CODES.TIRAMISU)
            return Manifest.permission.SCHEDULE_EXACT_ALARM

        return null
    }

    override suspend fun requestPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true

        val permissions = listOfNotNull(Manifest.permission.POST_NOTIFICATIONS, getRelevantAlarmPermission())

        val permissionHandler = KoinPlatform.getKoin().getOrNull<PermissionHandler>()
        return permissionHandler?.requestPermissions(permissions.toTypedArray()) ?: false
    }

    override fun post(
        localNotificationId: LocalNotificationId,
        title: String,
        message: String,
        time: LocalDateTime?,
    ) {
        logger.log(LOG_TAG) { "Posting notification: $localNotificationId, $title at $time" }
        if (time != null) {
            scheduleNotification(
                title = title,
                message = message,
                localNotificationId = localNotificationId,
                time = time,
            )
        } else {
            showNotification(
                title = title,
                message = message,
                localNotificationId = localNotificationId,
            )
        }
    }

    private fun scheduleNotification(
        title: String,
        message: String,
        localNotificationId: LocalNotificationId,
        time: LocalDateTime
    ) {
        alarmManager ?: return

        val intent = Intent(context, AlarmBroadcastReceiver::class.java)
            .setAction(ACTION_SHOW_NOTIFICATION)
            .putExtra(EXTRA_TITLE, title)
            .putExtra(EXTRA_MESSAGE, message)
            .putExtra(EXTRA_LOCAL_NOTIFICATION_ID, localNotificationId.toString())

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            localNotificationId.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val triggerTime = timeProvider.getNotificationTime(time).toInstant(EVENT_TIME_ZONE)
        val triggerAtMillis = triggerTime.toEpochMilliseconds()

        val alarmPermission = getRelevantAlarmPermission()
        if (alarmPermission != null &&
            ContextCompat.checkSelfPermission(context, alarmPermission) != PackageManager.PERMISSION_GRANTED
        ) {
            logger.log(LOG_TAG) { "No ${alarmPermission}} permission to schedule notification $localNotificationId" }
            return
        }

        logger.log(LOG_TAG) { "Setting alarm for notification $localNotificationId, $triggerTime ($triggerAtMillis)" }
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
    }

    private fun showNotification(
        title: String,
        message: String,
        localNotificationId: LocalNotificationId,
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            logger.log(LOG_TAG) { "Skipping notification $localNotificationId, no permission" }
            return
        }

        val mainActivityIntent = Intent(context, Class.forName("org.jetbrains.kotlinconf.android.MainActivity"))
            .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            .putExtra(EXTRA_LOCAL_NOTIFICATION_ID, localNotificationId.toString())
        val pendingIntent = PendingIntent.getActivity(
            context,
            localNotificationId.hashCode(),
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

        logger.log(LOG_TAG) { "Showing notification: $localNotificationId, $notification" }
        notificationManager.notify(localNotificationId.hashCode(), notification)
    }

    override fun cancel(localNotificationId: LocalNotificationId) {
        logger.log(LOG_TAG) { "Canceling notification $localNotificationId" }

        // Cancel the notification if it's currently shown
        notificationManager.cancel(localNotificationId.hashCode())
        logger.log(LOG_TAG) { "Canceled existing notification $localNotificationId" }

        // Cancel any pending alarms for this notification
        val intent = Intent(context, AlarmBroadcastReceiver::class.java).apply {
            action = ACTION_SHOW_NOTIFICATION
        }
        val pendingIntent: PendingIntent? = PendingIntent.getBroadcast(
            context,
            localNotificationId.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
        )
        pendingIntent?.let { alarmManager?.cancel(it) }
        logger.log(LOG_TAG) { "Canceled scheduled notification $localNotificationId" }
    }
}

class AlarmBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_SHOW_NOTIFICATION) return

        val title = intent.getStringExtra(EXTRA_TITLE) ?: return
        val message = intent.getStringExtra(EXTRA_MESSAGE) ?: return
        val notificationId = intent.getStringExtra(EXTRA_LOCAL_NOTIFICATION_ID) ?: return

        val localNotificationService = KoinPlatform.getKoin().get<LocalNotificationService>()
        localNotificationService.post(
            title = title,
            message = message,
            localNotificationId = LocalNotificationId.parse(notificationId) ?: return,
        )
    }
}
