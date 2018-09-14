import UIKit
import konfios

let KTUnit = KTStdlibUnit()

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {
    private static let GENERATE_ID_ONCE_KEY = "generateIdOnce"
    static let UUID_KEY = "vendorId"

    public lazy var konfService = KTKotlinConfDataRepository(
        settingsFactory: nil,
        onError: onError
    )

    var window: UIWindow?

    var userUuid: String {
        // Should be already set in `generateUuidIfNeeded`
        return UserDefaults.standard.string(forKey: AppDelegate.UUID_KEY)!
    }

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplicationLaunchOptionsKey: Any]?
    ) -> Bool {
        generateUuidIfNeeded()
        return true
    }

    private func generateUuidIfNeeded() {
        doOnce(key: AppDelegate.GENERATE_ID_ONCE_KEY) {
            let uuid = "ios-" + (UIDevice.current.identifierForVendor ?? UUID()).uuidString
            UserDefaults.standard.set(uuid, forKey: AppDelegate.UUID_KEY)
            return true
        }
    }

    static var me: AppDelegate {
        return UIApplication.shared.delegate as! AppDelegate
    }

    public func applicationWillTerminate(_ application: UIApplication) {
        self.saveContext()
    }
    
    func saveContext() {
        // todo: save context
    }
    
    func onError(error: KTStdlibThrowable) -> KTStdlibUnit {
        return KTUnit
    }
}

fileprivate func doOnce(key: String, f: () -> Bool) {
    let userDefaults = UserDefaults.standard
    if (!userDefaults.bool(forKey: key)) {
        if (f()) {
            userDefaults.set(true, forKey: key)
        }
    }
}
