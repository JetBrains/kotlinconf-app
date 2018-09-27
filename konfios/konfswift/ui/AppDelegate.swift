import UIKit
import konfios

let KTUnit = KotlinUnit()

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {
    
    public lazy var konfService = { KotlinConfDataRepository(
        uid: generateUuid(),
        settings: PlatformSettings())
    }()

    var window: UIWindow?

    static var me: AppDelegate {
        return UIApplication.shared.delegate as! AppDelegate
    }

    public func applicationWillTerminate(_ application: UIApplication) {
        // TODO: save context
    }
    
    private func generateUuid() -> String {
        return "ios-" + (UIDevice.current.identifierForVendor ?? UUID()).uuidString
    }
}
