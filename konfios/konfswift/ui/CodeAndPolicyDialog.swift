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
    func showVotingCodeDialog(privacyPolicyAcceptedBefore: Bool = true) {
        let ratingViewController = CodeAndPolicyDialogController(nibName: "RatingViewController", bundle: nil)
        ratingViewController.checked = privacyPolicyAcceptedBefore
        
        let popup = PopupDialog(
            viewController: ratingViewController,
            buttonAlignment: .horizontal,
            transitionStyle: .bounceDown,
            tapGestureDismissal: false,
            panGestureDismissal: false
        )
        
        let cancelButton = CancelButton(title: "CANCEL", height: 60, dismissOnTap: false) {
            if(ratingViewController.checked){
                ratingViewController.privacyPolicyPresenter.onAcceptPrivacyPolicyClicked()
                ratingViewController.dismissView()
            } else {
                ratingViewController.privacyLabel.shake()
            }
        }
        
        let submitButton = DefaultButton(title: "SUBMIT", height: 60, dismissOnTap: false) {
            if(ratingViewController.checked){
                let code = ratingViewController.voteText.text!
                if(!code.isEmpty) {
                    ratingViewController.privacyPolicyPresenter.onAcceptPrivacyPolicyClicked()
                    ratingViewController.codeVerificationPresenter.onSubmitButtonClicked(code: code)
                } else {
                    ratingViewController.voteText.shake()
                }
            } else {
                ratingViewController.privacyLabel.shake()
            }
        }
        ratingViewController.submitButton = submitButton
        
        popup.addButtons([cancelButton, submitButton])
        self.present(popup, animated: true, completion: nil)
    }
}

class CodeAndPolicyDialogController: UIViewController, CodeVerificationView {
    @IBOutlet weak var voteText: UITextField!
    @IBOutlet weak var checkBox: UIImageView!
    @IBOutlet weak var privacyLabel: UILabel!
    var submitButton: DefaultButton? = nil
    
    private let repository = AppDelegate.me.konfService
    
    lazy var privacyPolicyPresenter: PrivacyPolicyPresenter = {
        PrivacyPolicyPresenter(
            repository: repository
        )
    }()
    
    lazy var codeVerificationPresenter: CodeVerificationPresenter = {
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
        updateCheckBox()
    }
    
    func setProgress(isLoading: Bool) {
        // TODO: We should have some progress
    }
    
    func dismissView() {
        dismiss(animated: true) { /* no-op */ }
    }
    
    @objc func checkBoxClicked() {
        checked = !checked
        updateCheckBox()
    }
    
    func updateCheckBox() {
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

extension CodeAndPolicyDialogController: UITextFieldDelegate {
    
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        endEditing()
        return true
    }
}

extension UIView {
    
    func shake() {
        let animation = CABasicAnimation(keyPath: "position")
        animation.duration = 0.07
        animation.repeatCount = 3
        animation.autoreverses = true
        animation.fromValue = NSValue(cgPoint: CGPoint(x: self.center.x - 10, y: self.center.y))
        animation.toValue = NSValue(cgPoint: CGPoint(x: self.center.x + 10, y: self.center.y))
        self.layer.add(animation, forKey: "position")
    }
    
}
