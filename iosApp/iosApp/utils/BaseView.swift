import Foundation
import UIKit
import KotlinConfAPI

extension UIViewController {
    public func showError(error: KotlinThrowable) {
        var title: String = "Error"
        var errorMessage: String!

        switch error {
        case is Unauthorized:
            navigationController?.pushViewController(
                createPage(name: "WelcomePrivacyPolicyController"),
                animated: true
            )
            return
        case is CannotFavorite:
            errorMessage = "Cannot set favorite now"
        case is CannotPostVote:
            title = "Not Allowed"
            errorMessage = "Failed to rate sessions, please check your connection"
        case is CannotDeleteVote:
            title = "Not Allowed"
            errorMessage = "Failed to update session rating, please check your connection"
        case is UpdateProblem:
            errorMessage = "Failed to get data from server, please check your internet connection"
        case is TooEarlyVote:
            title = "Not Allowed"
            errorMessage = "You cannot rate the session before it starts"
        case is TooLateVote:
            title = "Not Allowed"
            errorMessage = "Rating is only permitted up to 2 hours after the session end"
        default:
            return
//            errorMessage = "Unknown Error"
        }
        
        self.showError(title: title, message: errorMessage)
    }

    func showError(title: String, message: String) {
        let alertController = UIAlertController(
            title: title,
            message: message,
            preferredStyle: UIAlertController.Style.alert
        )
        alertController.addAction(UIAlertAction(
            title: "Dismiss",
            style: UIAlertAction.Style.default,
            handler: nil
        ))

        self.present(alertController, animated: true, completion: nil)
    }
}

extension UIView {
    func roundCorners(corners: UIRectCorner, radius: CGFloat) {
        let path = UIBezierPath(
            roundedRect: bounds,
            byRoundingCorners: corners,
            cornerRadii: CGSize(width: radius, height: radius)
        )
        let mask = CAShapeLayer()
        mask.path = path.cgPath
        layer.mask = mask
    }
}

extension CALayer {
    func addBorders(_ size: CGSize, _ xOffset: CGFloat, _ yOffset: CGFloat) -> CALayer {
        let layer = CALayer()
        layer.borderWidth = 1.0
        layer.borderColor = UIColor(red: 1.0, green: 1.0, blue: 1.0, alpha: 0.65).cgColor
        layer.cornerRadius = 7.0

        let size = CGSize(width: size.width, height: size.height + 2.0 * yOffset)

        layer.frame = CGRect(origin: CGPoint(x: 0.0 - xOffset, y: 0.0 - yOffset), size: size)

        self.addSublayer(layer)
        return layer
    }
}
