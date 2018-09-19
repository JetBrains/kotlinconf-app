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

extension UIViewController: BaseView {
    
    public func showError(error: KotlinThrowable) {
        var message = ""
        
        switch error {
        case is Unauthorized:
            message = "Unauthorized"
        case is CannotFavorite:
            message = "Cannot set favorite now"
        case is CannotPostVote:
            message = "Failed to post vote to server, please check your internet connection"
        case is CannotDeleteVote:
            message = "Failed to delete vote from server, please check your internet connection"
        case is UpdateProblem:
            message = "Failed to get data from server, please check your internet connection"
        case is TooEarlyVote:
            message = "Voting is not allowed before the session starts"
        case is TooLateVote:
            message = "Voting is not allowed later than 15 minutes after the session ends"
        default:
            message = "Unknown Error"
        }
        
//        error.printStackTrace()
        error.cause?.printStackTrace()
        error.cause?.cause?.printStackTrace()
        self.showPopupText(title: message)
    }
}
