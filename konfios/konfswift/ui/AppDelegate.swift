import UIKit
import main

let KTUnit = KotlinUnit()

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {
    
    public lazy var konfService = { KotlinConfDataRepository(
        endPoint: ConstantsKt.end_POINT,
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
