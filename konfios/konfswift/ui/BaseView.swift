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

extension UIViewController: BaseView {
    
    public func showError(error: KotlinThrowable) {
        var errorMessage: String? = nil
        var popupMessage: String? = nil

        switch error {
        case is Unauthorized:
            errorMessage = "Unauthorized"
        case is CannotFavorite:
            errorMessage = "Cannot set favorite now"
        case is CannotPostVote:
            errorMessage = "Failed to rate sessions, please check your connection"
        case is CannotDeleteVote:
            errorMessage = "Failed to update session rating, please check your connection"
        case is UpdateProblem:
            let text = "Failed to get data from server, please check your internet connection"
            if(firstUpdateError) {
                errorMessage = text
                firstUpdateError = false
            } else {
                popupMessage = text
            }
        case is TooEarlyVote:
            errorMessage = "You cannot rate the session before it starts"
        case is TooLateVote:
            errorMessage = "Rating is only permitted up to 15 minutes after the session end"
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
