import Foundation
import UIKit
import konfios

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

extension UIViewController {
    func showVotingCodeDialog(block: @escaping () -> () = {}) {
        let tokenEnterControl = CodeDialog(block: block)
        
        present(tokenEnterControl, animated: true, completion: block)
    }
}

class CodeDialog : UIAlertController, CodeVerificationView {
    private let repository = AppDelegate.me.konfService
    private var submit: UIAlertAction!
    private var submitted = false
    private var onSuccess: () -> () = {}
    
    lazy var codeVerificationPresenter: CodeVerificationPresenter = {
        CodeVerificationPresenter(
            uiContext: UI() as! KotlinCoroutineContext,
            view: self,
            repository: repository
        )
    }()
    
    convenience init(block: @escaping () -> ()) {
        self.init(
            title: "Enter Code",
            message: "Check the KotlinConf email for your code.",
            preferredStyle: .alert
        )
        self.onSuccess = block
    }
    
    override func viewDidLoad() {
        submit = UIAlertAction(title: "Submit", style: .default) { (x) in
            let token = self.textFields?[0].text
            if (token == nil || token == "") {
                x.isEnabled = true
                return
            }
            
            self.codeVerificationPresenter.onSubmitButtonClicked(code: token!)
        }
        
        submit.isEnabled = false
        
        let cancel = UIAlertAction(title: "Cancel", style: .cancel, handler: { (_) in })
        
        addTextField { (field) in
            field.placeholder = "code"
            
            NotificationCenter.default.addObserver(
                forName: NSNotification.Name.UITextFieldTextDidChange,
                object: field, queue: OperationQueue.main
            ) { (notification) in
                self.submit.isEnabled = false

                let token = field.text
                if (token == nil || token!.count != 5) {
                    return
                }
                
                self.codeVerificationPresenter.onSubmitButtonClicked(code: token!)
            }
            
        }
        
        addAction(submit)
        addAction(cancel)
    }
    
    
    
    func dismissView() {
        onSuccess()
        self.submit.isEnabled = true
    }
}
