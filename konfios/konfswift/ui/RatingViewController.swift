//
//  RatingViewController.swift
//  konfswift
//
//  Created by James Coggan on 18/09/2018.
//  Copyright © 2018 Yan Zhulanow. All rights reserved.
//

import Foundation
import UIKit

class RatingViewController: UIViewController {
    
    @IBOutlet weak var voteText: UITextField!
    @IBOutlet weak var checkBox: UIImageView!
    @IBOutlet weak var privacyLabel: UILabel!
    
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
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @objc func checkBoxClicked() {
        checked = !checked
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
