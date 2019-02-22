import Foundation
import UIKit
import main

class TermsController : UIViewController {
    @IBOutlet weak var privacyLabel: UILabel!
    
    override func viewDidLoad() {
        privacyLabel.isUserInteractionEnabled = true
        privacyLabel.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(privacyClicked)))
    }
    
    @objc func privacyClicked() {
        guard let url = URL(string: "https://www.jetbrains.com/company/privacy.html") else { return }
        if #available(iOS 10.0, *) {
            UIApplication.shared.open(url)
        } else {
            UIApplication.shared.openURL(url)
        }
    }
    
    @IBAction func onAcceptClick(_ sender: Any) {
        AppDelegate.me.konfService.acceptPrivacyPolicy()
        dismiss(animated: true) { }
    }
}
