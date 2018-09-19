//
//  RatingViewController.swift
//  konfswift
//
//  Created by James Coggan on 18/09/2018.
//  Copyright © 2018 Yan Zhulanow. All rights reserved.
//

import Foundation
import UIKit
import konfios
import PopupDialog

extension UIViewController {
    func showVotingCodeDialog() {
        let ratingViewController = RatingViewController(nibName: "RatingViewController", bundle: nil)
        
        let popup = PopupDialog(viewController: ratingViewController,
                                buttonAlignment: .horizontal,
                                transitionStyle: .bounceDown,
                                tapGestureDismissal: false,
                                panGestureDismissal: false)
        
        let cancelButton = CancelButton(title: "CANCEL", height: 60, dismissOnTap: false) {
            ratingViewController.presenter.onCancel()
        }
        
        let submitButton = DefaultButton(title: "SUBMIT", height: 60, dismissOnTap: false) {
            let code = ratingViewController.voteText.text!
            ratingViewController.presenter.onSubmit(code: code)
        }
        submitButton.isEnabled = false
        ratingViewController.submitButton = submitButton
        
        popup.addButtons([cancelButton, submitButton])
        self.present(popup, animated: true, completion: nil)
    }

    func showTermsNotAcceptedDialog() {
        let alert = UIAlertController(title: nil, message: "To use application, you need to accept the terms and conditions", preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: NSLocalizedString("OK", comment: ""), style: .default, handler: { _ in }))
        self.present(alert, animated: true, completion: nil)
    }
}

class RatingViewController: UIViewController, KTCodeVerificationView {
    
    @IBOutlet weak var voteText: UITextField!
    @IBOutlet weak var checkBox: UIImageView!
    @IBOutlet weak var privacyLabel: UILabel!
    var submitButton: DefaultButton? = nil
    
    
    private let repository = AppDelegate.me.konfService
    lazy var presenter: KTCodeVerificationPresenter = {
        KTCodeVerificationPresenter(uiContext: UI(), view: self, repository: repository)
    }()

    var _checked = false
    var termsAccepted: Bool {
        get {
            return self._checked
        }
        set(checked) {
            self._checked = checked
            if(checked){
                checkBox.image = UIImage(named:"checked")!
            } else {
                checkBox.image = UIImage(named:"unchecked")!
            }
            submitButton?.isEnabled = checked
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        voteText.delegate = self
        view.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(endEditing)))
        
        checkBox.isUserInteractionEnabled = true
        checkBox.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(checkBoxClicked)))
        privacyLabel.isUserInteractionEnabled = true
        privacyLabel.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(privacyClicked)))
        
        presenter.onCreate()
    }
    
    func setProgress(isLoading: Bool) {
        // TODO: We should have some progress
    }
    
    func dismissView() {
        dismiss(animated: true) { /* no-op */ }
    }
    
    func showTermsAcceptanceRequired() {
        showTermsNotAcceptedDialog()
    }
    
    @objc func checkBoxClicked() {
        termsAccepted = !termsAccepted
        submitButton?.isEnabled = termsAccepted
    }
    
    @objc func privacyClicked() {
        guard let url = URL(string: "https://www.jetbrains.com/company/privacy.html") else { return }
        if #available(iOS 10.0, *) {
            UIApplication.shared.open(url)
        } else {
            UIApplication.shared.openURL(url)
        }
    }
    
    @objc func endEditing() {
        view.endEditing(true)
    }
}

extension RatingViewController: UITextFieldDelegate {
    
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        endEditing()
        return true
    }
}
