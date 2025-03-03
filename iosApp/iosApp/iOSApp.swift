import SwiftUI
import shared

@main
struct iOSApp: App {
	var body: some Scene {
		WindowGroup {
			ContentView()
		}
	}

    let appDelegate = AppDelegate()

    init() {
        UNUserNotificationCenter.current().delegate = appDelegate
    }
}

class AppDelegate : UIResponder, UIApplicationDelegate, UNUserNotificationCenterDelegate {
    /**
     Makes sure that any notifications requested while the app in the foreground are shown.
    */
    func userNotificationCenter(
      _ center: UNUserNotificationCenter,
      willPresent notification: UNNotification,
      withCompletionHandler completionHandler:
      @escaping (UNNotificationPresentationOptions) -> Void
    ) {
      completionHandler([.banner, .sound, .badge])
    }

    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        didReceive response: UNNotificationResponse,
        withCompletionHandler completionHandler: @escaping () -> Void
    ) {
        IOSNotificationServiceKt.handleNotificationResponse(response: response)
        completionHandler()
    }
}
