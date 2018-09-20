//
//  File.swift
//  konfswift
//
//  Created by Marcin Moskala on 16/09/2018.
//  Copyright © 2018 Yan Zhulanow. All rights reserved.
//

import Foundation
import konfios
import UIKit

var firstUpdateError = true

extension UIViewController: KTBaseView {
    
    public func showError(error: KTStdlibThrowable) {
        var errorMessage: String? = nil
        var popupMessage: String? = nil

        switch error {
        case is Unauthorized:
            errorMessage = "Unauthorized"
        case is CannotFavorite:
            errorMessage = "Cannot set favorite now"
        case is CannotPostVote:
            errorMessage = "Failed to post vote to server, please check your internet connection"
        case is CannotDeleteVote:
            errorMessage = "Failed to delete vote from server, please check your internet connection"
        case is UpdateProblem:
            let text = "Failed to get data from server, please check your internet connection"
            if(firstUpdateError) {
                errorMessage = text
                firstUpdateError = false
            } else {
                popupMessage = text
            }
        case is TooEarlyVote:
            errorMessage = "Voting is not allowed before the session starts"
        case is TooLateVote:
            errorMessage = "Voting is not allowed later than 15 minutes after the session ends"
        case is FailedToVerifyCode:
            errorMessage = "Failed to verify code"
        case is IncorrectCode:
            errorMessage = "Sorry, the code entered is incorrect"
        default:
            errorMessage = "Unknown Error"
        }
        
        if let message = errorMessage {
            self.showError(message: message)
        }
        if let message = popupMessage {
            self.showPopupText(title: message)
        }
    }

    func showError(message: String) {
        let alertController = UIAlertController(title: "Error", message: message, preferredStyle: UIAlertControllerStyle.alert)
        alertController.addAction(UIAlertAction(title: "Dismiss", style: UIAlertActionStyle.default,handler: nil))
        self.present(alertController, animated: true, completion: nil)
    }
}
