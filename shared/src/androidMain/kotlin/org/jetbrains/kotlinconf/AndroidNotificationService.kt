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
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import kotlinx.coroutines.channels.Channel
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toInstant

private const val EXTRA_TITLE = "title"
private const val EXTRA_MESSAGE = "message"
private const val EXTRA_NOTIFICATION_ID = "notificationId"
private const val EXTRA_ICON_ID = "iconId"
private const val NOTIFICATION_CHANNEL_ID = "channel_all_notifications"
private const val ACTION_SHOW_NOTIFICATION = "org.jetbrains.kotlinconf.SHOW_NOTIFICATION"

class AndroidNotificationService(
    private val timeProvider: TimeProvider,
    private val context: Context,
    private val iconId: Int,
    private val permissionResult: Channel<Boolean>,
    private val permissionLauncher: ActivityResultLauncher<Array<String>>,
) : NotificationService {

    private val notificationManager = NotificationManagerCompat.from(context)
    private val alarmManager = context.getSystemService<AlarmManager>()

    override suspend fun requestPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true

        val permissions = arrayOf(Manifest.permission.POST_NOTIFICATIONS, Manifest.permission.USE_EXACT_ALARM)
        permissionLauncher.launch(permissions)
        return permissionResult.receive()
    }

    override fun post(
        notificationId: String,
        title: String,
        message: String,
        time: LocalDateTime?,
    ) {
        if (time != null) {
            scheduleNotification(
                title = title,
                message = message,
                notificationId = notificationId,
                time = time,
            )
        } else {
            showNotification(
                context = context,
                title = title,
                message = message,
                iconId = iconId,
                notificationManager = notificationManager,
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
            .putExtra(EXTRA_ICON_ID, iconId)

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
            println("No permission to schedule notification $notificationId")
            return
        }

        println("Setting alarm for notification $notificationId, $triggerTime ($triggerAtMillis)")
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
    }

    override fun cancel(notificationId: String) {
        println("Canceling notification $notificationId")

        // Cancel the notification if it's currently shown
        notificationManager.cancel(notificationId.hashCode())
        println("Canceled existing notification $notificationId")

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
        println("Canceled scheduled notification $notificationId")
    }
}

class AlarmBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        println("Received notification alarm")

        if (intent.action != ACTION_SHOW_NOTIFICATION) return

        val title = intent.getStringExtra(EXTRA_TITLE) ?: return
        val message = intent.getStringExtra(EXTRA_MESSAGE) ?: return
        val notificationId = intent.getStringExtra(EXTRA_NOTIFICATION_ID) ?: return
        val iconId = intent.getIntExtra(EXTRA_ICON_ID, 0)
        if (iconId == 0) return

        showNotification(
            context = context,
            title = title,
            message = message,
            iconId = iconId,
            notificationManager = NotificationManagerCompat.from(context),
            notificationId = notificationId,
        )
    }
}

private fun showNotification(
    context: Context,
    title: String,
    message: String,
    iconId: Int,
    notificationManager: NotificationManagerCompat,
    notificationId: String,
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
        != PackageManager.PERMISSION_GRANTED
    ) {
        println("Skipping notification $notificationId, no permission")
        return
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        notificationManager.createNotificationChannel(
            NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "All notifications",
                android.app.NotificationManager.IMPORTANCE_HIGH
            )
        )
    }

    val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setSmallIcon(iconId)
        .setAutoCancel(true)
        .build()

    println("Showing notification: $notification")
    notificationManager.notify(notificationId.hashCode(), notification)
}
