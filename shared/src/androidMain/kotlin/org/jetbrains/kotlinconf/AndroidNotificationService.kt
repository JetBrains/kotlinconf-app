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
import org.jetbrains.kotlinconf.utils.Logger
import org.koin.mp.KoinPlatformTools

private const val LOG_TAG = "AndroidNotificationService"
private const val EXTRA_TITLE = "title"
private const val EXTRA_MESSAGE = "message"
const val EXTRA_NOTIFICATION_ID = "notificationId"
private const val EXTRA_ICON_ID = "iconId"
private const val NOTIFICATION_CHANNEL_ID = "channel_all_notifications"
private const val ACTION_SHOW_NOTIFICATION = "org.jetbrains.kotlinconf.SHOW_NOTIFICATION"

class AndroidNotificationService(
    private val timeProvider: TimeProvider,
    private val context: Context,
    private val iconId: Int,
    private val permissionResult: Channel<Boolean>,
    private val permissionLauncher: ActivityResultLauncher<Array<String>>,
    private val logger: Logger,
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
                context = context,
                title = title,
                message = message,
                iconId = iconId,
                notificationManager = notificationManager,
                notificationId = notificationId,
                logger = logger,
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
            logger.log(LOG_TAG) { "No permission to schedule notification $notificationId" }
            return
        }

        logger.log(LOG_TAG) { "Setting alarm for notification $notificationId, $triggerTime ($triggerAtMillis)" }
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
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
        // Grab logger from Koin if available
        val logger = KoinPlatformTools.defaultContext().getOrNull()?.getOrNull<Logger>()

        logger?.log(LOG_TAG) { "Received notification alarm" }

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
            logger = logger,
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
    logger: Logger?,
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
        != PackageManager.PERMISSION_GRANTED
    ) {
        logger?.log(LOG_TAG) { "Skipping notification $notificationId, no permission" }
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

    logger?.log(LOG_TAG) { "Showing notification: $notificationId, $notification" }
    notificationManager.notify(notificationId.hashCode(), notification)
}
