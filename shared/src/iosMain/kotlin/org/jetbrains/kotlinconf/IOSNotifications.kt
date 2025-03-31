package org.jetbrains.kotlinconf

import com.mmk.kmpnotifier.extensions.onNotificationClicked
import com.mmk.kmpnotifier.notification.NotifierManager
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
    NotifierManager.onNotificationClicked(content)
}
