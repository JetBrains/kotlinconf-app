import SwiftUI

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

/**
 Makes sure that any notifications requested while the app in the foreground are shown.
*/
class AppDelegate : UIResponder, UIApplicationDelegate, UNUserNotificationCenterDelegate {
    func userNotificationCenter(
      _ center: UNUserNotificationCenter,
      willPresent notification: UNNotification,
      withCompletionHandler completionHandler:
      @escaping (UNNotificationPresentationOptions) -> Void
    ) {
      completionHandler([.banner, .sound, .badge])
    }
}
