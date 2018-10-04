import UIKit
import MBProgressHUD
import SDWebImage

extension UIImage {
    func circularImage(to size: CGSize? = nil) -> UIImage {
        let newSize = size ?? self.size

        let minEdge = min(newSize.height, newSize.width)
        let size = CGSize(width: minEdge, height: minEdge)

        UIGraphicsBeginImageContextWithOptions(size, false, 0.0)
        let context = UIGraphicsGetCurrentContext()!

        self.draw(in: CGRect(origin: CGPoint.zero, size: size), blendMode: .copy, alpha: 1.0)

        context.setBlendMode(.copy)
        context.setFillColor(UIColor.clear.cgColor)

        let rectPath = UIBezierPath(rect: CGRect(origin: CGPoint.zero, size: size))
        let circlePath = UIBezierPath(ovalIn: CGRect(origin: CGPoint.zero, size: size))
        rectPath.append(circlePath)
        rectPath.usesEvenOddFillRule = true
        rectPath.fill()

        let result = UIGraphicsGetImageFromCurrentImageContext()!
        UIGraphicsEndImageContext()

        return result
    }
}

extension UIView {
    func showPopupText(title: String, text: String = "", delay: TimeInterval = 1.0) {
        let hud = MBProgressHUD(view: self)
        hud.label.text = title
        hud.detailsLabel.text = text
        hud.mode = .text
        hud.removeFromSuperViewOnHide = true
        self.addSubview(hud)
        hud.show(animated: true)
        DispatchQueue.main.asyncAfter(deadline: .now() + delay) {
            hud.hide(animated: true)
        }
    }
}

extension UIViewController {
    func showPopupText(title: String, text: String = "", delay: TimeInterval = 1.0) {
        self.view.showPopupText(title: title, text: text, delay: delay)
    }

    func createErrorHandler(_ message: String? = nil, additionalWork: (() -> ())? = nil) -> (Error) -> () {
        return { error in
            if let work = additionalWork {
                work()
            }

            if let _message = message {
                self.showPopupText(title: _message, text: error.localizedDescription, delay: 1.5)
            } else {
                self.showPopupText(title: "An error occured", text: error.localizedDescription, delay: 1.5)
            }
        }
    }
}

extension UIImageView {
    private static let PLACEHOLDER_IMAGE = UIImage.init(named: "user_default")?.circularImage()

    func loadUserIcon(url: String?) {
        let completionBlock: (UIImage?, Error?, SDImageCacheType, URL?) -> () = { (image, error, cacheType, imageURL) in
            if let existingImage = image {
                self.image = existingImage.circularImage()
            }
        }

        let nsUrl: URL?
        if let setUrl = url {
            nsUrl = URL(string: setUrl)
        } else {
            nsUrl = nil
        }

        self.sd_setImage(
                with: nsUrl,
                placeholderImage: UIImageView.PLACEHOLDER_IMAGE,
                completed: completionBlock)
    }
}
