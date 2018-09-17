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

extension UIViewController: KTBaseView {
    
    public func showError(error: KTStdlibThrowable) {
        var message = ""
        
        switch error {
        case is KTUnauthorized:
            message = "Unauthorized"
        case is KTCannotFavorite:
            message = "Cannot set favorite now"
        case is KTCannotPostVote:
            message = "Failed to post vote to server, please check your internet connection"
        case is KTCannotDeleteVote:
            message = "Failed to delete vote from server, please check your internet connection"
        case is KTUpdateProblem:
            message = "Failed to get data from server, please check your internet connection"
        case is KTTooEarlyVote:
            message = "Voting is not allowed before the session starts"
        case is KTTooLateVote:
            message = "Voting is not allowed later than 15 minutes after the session ends"
        default:
            message = "Unknown Error"
        }
        
        self.showPopupText(title: message)
    }
}
