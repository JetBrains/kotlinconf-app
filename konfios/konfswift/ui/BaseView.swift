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
        case is KTKotlinConfDataRepositoryUnauthorized:
            message = "Unauthorized"
        case is KTKotlinConfDataRepositoryCannotVote:
            message = "Voting is not possible now"
        default:
            message = "Unknown Error"
        }
        
        //  result != nil ? "Thank you for the feedback!" : "Your vote was cleared."
        self.showPopupText(title: message)
    }
}
