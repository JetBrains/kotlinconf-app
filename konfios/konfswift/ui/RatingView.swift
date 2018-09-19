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
                                tapGestureDismissal: true,
                                panGestureDismissal: false)
        
        let cancelButton = CancelButton(title: "CANCEL", height: 60) {
            // no-op
        }
        
        let submitButton = DefaultButton(title: "SUBMIT", height: 60, dismissOnTap: false) {
            if(ratingViewController.checked){
                let code = ratingViewController.voteText.text!
                ratingViewController.presenter.verifyCode(code: code)
            } else {
                self.showTermsNotAcceptepDialog()
            }
        }
        submitButton.isEnabled = false
        ratingViewController.submitButton = submitButton
        
        popup.addButtons([cancelButton, submitButton])
        self.present(popup, animated: true, completion: nil)
    }

    func showTermsNotAcceptepDialog() {
        let alert = UIAlertController(title: nil, message: "Please Accept the terms and conditions to be able to vote", preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: NSLocalizedString("OK", comment: ""), style: .default, handler: { _ in }))
        self.present(alert, animated: true, completion: nil)
    }
}

class RatingViewController: UIViewController, CodeVerificationView {
    @IBOutlet weak var voteText: UITextField!
    @IBOutlet weak var checkBox: UIImageView!
    @IBOutlet weak var privacyLabel: UILabel!
    var submitButton: DefaultButton? = nil
    
    private let repository = AppDelegate.me.konfService
    lazy var presenter: CodeVerificationPresenter = {
        CodeVerificationPresenter(
            uiContext: UI() as! KotlinCoroutineContext,
            view: self,
            repository: repository
        )
    }()
    
    var checked = false
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        voteText.delegate = self
        view.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(endEditing)))
        
        checkBox.isUserInteractionEnabled = true
        checkBox.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(checkBoxClicked)))
        privacyLabel.isUserInteractionEnabled = true
        privacyLabel.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(privacyClicked)))
    }
    
    func setProgress(isLoading: Bool) {
        // TODO: We should have some progress
    }
    
    func dismissView() {
        dismiss(animated: true) { /* no-op */ }
    }
    
    @objc func checkBoxClicked() {
        checked = !checked
        
        submitButton?.isEnabled = checked
        if(checked){
            checkBox.image = UIImage(named:"checked")!
        } else {
            checkBox.image = UIImage(named:"unchecked")!
        }
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
