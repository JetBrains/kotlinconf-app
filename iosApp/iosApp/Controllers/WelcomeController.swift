import UIKit
import CoreLocation

class WelcomeController : UIPageViewController, UIPageViewControllerDelegate {
    private(set) lazy var pages: [UIViewController] = {
        return [
            createPage(name: "WelcomePrivacyPolicyController"),
            createPage(name: "WelcomeNotificationsController")
        ]
    }()

    override func viewDidLoad() {
        dataSource = self
        delegate = self
        setViewControllers([pages.first!], direction: .forward, animated: true, completion: nil)

        view.backgroundColor = UIColor.defaultGray
        let dotsView = UIPageControl.appearance()
        dotsView.pageIndicatorTintColor = UIColor.dark20
        dotsView.currentPageIndicatorTintColor = UIColor.blackGray
    }

    func showNextPage() -> Bool {
        let controller = viewControllers!.first
        let index = pages.firstIndex(of: controller!)!

        let page = getPage(index: index + 1)
        if (page == nil) {
            return false
        }

        setViewControllers([page!], direction: .forward, animated: true, completion: nil)
        return true
    }
}

class WelcomePrivacyPolicyController : UIViewController {
    @IBOutlet weak var nextButton: UIButton!
    @IBOutlet weak var privacyPolicy: UILabel!

    override func viewDidLoad() {
        if (hasParentWelcome()) {
            nextButton.setTitle("Next", for: .normal)
        } else {
            nextButton.setTitle("Close", for: .normal)
        }

        let recognizer = UITapGestureRecognizer(target: self, action: #selector(openPrivacyPolicy(_:)))
        privacyPolicy.addGestureRecognizer(recognizer)
    }

    @IBAction func acceptTouch(_ sender: Any) {
        Conference.acceptPrivacyPolicy()
        nextPage()
    }

    @objc func openPrivacyPolicy(_ sender: Any) {
        let url = URL(string: "https://www.jetbrains.com/company/privacy.html")
        UIApplication.shared.open(url!)

    }

    @IBAction func nextTouch(_ sender: Any) {
        nextPage()
    }
}

class WelcomeNotificationsController : UIViewController {
    @IBAction func acceptTouch(_ sender: Any) {
        Conference.requestNotificationPermissions()
        close()
    }

    @IBAction func closeTouch(_ sender: Any) {
        close()
    }
}

extension WelcomeController : UIPageViewControllerDataSource {
    func pageViewController(
        _ pageViewController: UIPageViewController,
        viewControllerBefore viewController: UIViewController
    ) -> UIViewController? {
        let index: Int = pages.firstIndex(of: viewController)!
        return getPage(index: index - 1)
    }

    func pageViewController(
        _ pageViewController: UIPageViewController,
        viewControllerAfter viewController: UIViewController
    ) -> UIViewController? {
        let index: Int = pages.firstIndex(of: viewController)!
        return getPage(index: index + 1)
    }

    func getPage(index: Int) -> UIViewController? {
        if index < 0 || index >= pages.count {
            return nil
        }

        return pages[index]
    }

    func presentationCount(for pageViewController: UIPageViewController) -> Int {
        return pages.count
    }

    func presentationIndex(for pageViewController: UIPageViewController) -> Int {
        return pages.firstIndex(of: pageViewController) ?? 0
    }
}

private extension UIViewController {

    func hasParentWelcome() -> Bool {
        return (parent as? WelcomeController) != nil
    }

    func nextPage() {
        let welcome = (parent as? WelcomeController)
        if (welcome == nil || !welcome!.showNextPage()) {
            close()
        }
    }

    func close() {
        navigationController?.popViewController(animated: true)
    }
}

func createPage(name: String) -> UIViewController {
    return UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: name)
}
