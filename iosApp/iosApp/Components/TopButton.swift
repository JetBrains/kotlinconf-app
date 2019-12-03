import Foundation
import UIKit

@IBDesignable class TopButton : UIButton {

    override var bounds: CGRect {
        didSet {
            configure()
        }
    }

    @IBInspectable var topLeft: Bool = false {
        didSet {
            configure()
        }
    }

    @IBInspectable var topRight: Bool = false {
        didSet {
            configure()
        }
    }

    @IBInspectable var bottomLeft: Bool = false {
        didSet {
            configure()
        }
    }

    @IBInspectable var bottomRight: Bool = false {
        didSet {
            configure()
        }
    }

    @IBInspectable var radius: CGFloat = 0.0 {
        didSet {
            configure()
        }
    }

    func configure() {
        var corners: UIRectCorner = []
        if (topLeft) { corners = corners.union(.topLeft) }
        if (topRight) { corners = corners.union(.topRight) }
        if (bottomLeft) { corners = corners.union(.bottomLeft) }
        if (bottomRight) { corners = corners.union(.bottomRight) }

        roundCorners(corners: corners, radius: radius)
    }

    func dark() {
        isSelected = true
        backgroundColor = UIColor.dark
    }

    func light() {
        isSelected = false
        backgroundColor = UIColor.white
    }
}
