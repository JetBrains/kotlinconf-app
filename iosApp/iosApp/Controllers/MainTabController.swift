import Foundation
import UIKit
import KotlinConfAPI
import Nuke

class MainTabController : UITabBarController {
    override func viewDidLoad() {
        super.viewDidLoad()

        if (Conference.isFirstLaunch()) {
            navigationController?.pushViewController(
                createPage(name: "Welcome"),
                animated: true
            )
        }

        Conference.errors.watch {error in
            self.showError(error: error!)
        }

        setupDiskCache()

        var lastState: HomeState = HomeState.During()
        Conference.homeState.watch { state in
           if (state == lastState) {
               return
           }
           if (state as? HomeState.Before) != nil {
               let mainBoard = UIStoryboard(name: "Main", bundle: nil)
               let beforeView = mainBoard.instantiateViewController(
                   withIdentifier: "Before"
               )

               self.viewControllers?[0] = beforeView
           }
           if (state as? HomeState.During) != nil {
               let mainBoard = UIStoryboard(name: "Main", bundle: nil)
               let homeView = mainBoard.instantiateViewController(
                  withIdentifier: "Home"
               )

               self.viewControllers?[0] = homeView
           }
            if (state as? HomeState.After) != nil {
               let mainBoard = UIStoryboard(name: "Main", bundle: nil)
               let afterView = mainBoard.instantiateViewController(
                   withIdentifier: "After"
               )

            do {
               self.viewControllers?[0] = afterView
            }
           }

           lastState = state!
        }
    }

    private func setupDiskCache() {
        DataLoader.sharedUrlCache.diskCapacity = 30 * 1024 * 1024

        let pipeline = ImagePipeline {
              let dataCache = try! DataCache(name: "org.jetbrain.kotlinconf.imagecache")
              dataCache.sizeLimit = 200 * 1024 * 1024
              $0.dataCache = dataCache
        }
        ImagePipeline.shared = pipeline
    }
}
