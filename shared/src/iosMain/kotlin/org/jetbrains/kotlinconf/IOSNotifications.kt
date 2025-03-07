package org.jetbrains.kotlinconf

import com.mmk.kmpnotifier.notification.PayloadData
import org.jetbrains.kotlinconf.navigation.navigateByLocalNotificationId
import platform.UserNotifications.UNNotificationContent
import platform.UserNotifications.UNNotificationResponse

@Suppress("unused") // Called from Swift
fun handleNotificationResponse(response: UNNotificationResponse) {
    val content: UNNotificationContent = response.notification.request.content
    val notificationId = content.userInfo[IOSLocalNotificationService.LOCAL_NOTIFICATION_ID_KEY] as? String
    if (notificationId != null) {
        // Local notification clicked
        navigateByLocalNotificationId(notificationId)
        return
    }

    // Process push notifications
    // TODO simplify this to NotifierManager.onNotificationClicked(content) and inline notifierManagerListener
    val payloadData = content.userInfo.toPayloadData()
    notifierManagerListener.onNotificationClicked(payloadData)
}

internal fun Map<Any?, *>?.toPayloadData(): PayloadData {
    return this.orEmpty().let { data ->
        data.keys
            .filterNotNull()
            .filterIsInstance<String>()
            .associateWith { key -> data[key] }
    }
}
