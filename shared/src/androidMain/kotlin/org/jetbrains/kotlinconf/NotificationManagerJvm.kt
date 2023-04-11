package org.jetbrains.kotlinconf

import android.content.*
import androidx.core.app.*
import androidx.work.*
import org.jetbrains.kotlinconf.storage.*
import java.util.concurrent.*
import kotlin.random.*

private val CHANNEL_ID = "KOTLIN_CONF_CHANNEL_ID"

actual class NotificationManager actual constructor(
    private val context: ApplicationContext
) {
    actual fun requestPermission() {
    }

    actual fun schedule(delay: Long, title: String, message: String): String? {
        val data = Data.Builder()
            .putString("title", title)
            .putString("message", message)
            .putInt("icon", context.notificationIcon)
            .build()

        val work = OneTimeWorkRequestBuilder<OneTimeScheduleWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .addTag(title)
            .setInputData(data)
            .build()

        WorkManager.getInstance(context.application).enqueue(work)
        return title
    }

    actual fun cancel(title: String) {
        WorkManager.getInstance(context.application).cancelAllWorkByTag(title)
    }

}

internal class OneTimeScheduleWorker(
    val context: Context,
    workerParams: WorkerParameters,
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val icon = inputData.getInt("icon", 0)
        val title = inputData.getString("title")
        val message = inputData.getString("message")

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(icon)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(context)) {
            notify(Random.nextInt(), builder.build())
        }

        return Result.success()
    }
}