import Foundation

import UIKit

extension UIColor {

    @nonobjc class var redOrange: UIColor {
        return UIColor(red: 1.0, green: 63.0 / 255.0, blue: 0.0, alpha: 1.0)
    }

    @nonobjc class var whiteCardTap: UIColor {
        return UIColor(red: 216.0 / 255.0, green: 216.0 / 255.0, blue: 216.0 / 255.0, alpha: 1.0)
    }

    @nonobjc class var defaultGray: UIColor {
        return UIColor(red: 244.0 / 255.0, green: 244.0 / 255.0, blue: 244.0 / 255.0, alpha: 1.0)
    }

    @nonobjc class var blackGray: UIColor {
        return UIColor(red: 74.0 / 255.0, green: 74.0 / 255.0, blue: 74.0 / 255.0, alpha: 1.0)
    }

    @nonobjc class var dayGray: UIColor {
        return UIColor(red: 155.0 / 255.0, green: 155.0 / 255.0, blue: 155.0 / 255.0, alpha: 1.0)
    }

    @nonobjc class var dark: UIColor {
        return UIColor(red: 39.0 / 255.0, green: 40.0 / 255.0, blue: 44.0 / 255.0, alpha: 1.0)
    }

    @nonobjc class var dark20: UIColor {
        return UIColor(red: 39.0 / 255.0, green: 40.0 / 255.0, blue: 44.0 / 255.0, alpha: 0.2)
    }

    @nonobjc class var cardGray: UIColor {
        return UIColor(red: 50.0 / 255.0, green: 50.0 / 255.0, blue: 54.0 / 255.0, alpha: 1.0)
    }

    @nonobjc class var darkGrey50: UIColor {
        return UIColor(red: 39.0 / 255.0, green: 40.0 / 255.0, blue: 44.0 / 255.0, alpha: 0.5)
    }

    @nonobjc class var lightGrey50: UIColor {
        return UIColor(red: 142.0 / 255.0, green: 142.0 / 255.0, blue: 147.0 / 255.0, alpha: 0.5)
    }

    @nonobjc class var deepSkyBlue: UIColor {
        return UIColor(red: 10.0 / 255.0, green: 122.0 / 255.0, blue: 247.0 / 255.0, alpha: 1.0)
    }

    @nonobjc class var darkGreyOpac: UIColor {
        return UIColor(white: 132.0 / 255.0, alpha: 0.3)
    }

    @nonobjc class var white30: UIColor {
        return UIColor(white: 1.0, alpha: 0.3)
    }

    @nonobjc class var white60: UIColor {
        return UIColor(white: 1.0, alpha: 0.6)
    }

    @nonobjc class var pressedOrange: UIColor {
        return UIColor(red: 216.0 / 255.0, green: 53.0 / 255.0, blue: 0.0, alpha: 1.0)
    }

}
// Text styles
extension UIFont {

    class var headerScreen: UIFont {
        return UIFont(name: "BigShouldersDisplay-ExtraBold", size: 64.0)!
    }

    class var headerScreenSmall: UIFont {
        return UIFont(name: "BigShouldersDisplay-ExtraBold", size: 32.0)!
    }

    class var headerListBig: UIFont {
        return UIFont(name: "BigShouldersDisplay-ExtraBold", size: 40.0)!
    }

    class var headerListMed: UIFont {
        return UIFont.systemFont(ofSize: 24.0, weight: .bold)
    }

    class var groupHeader: UIFont {
        return UIFont.systemFont(ofSize: 16.0, weight: .bold)
    }

    class var tag: UIFont {
        return UIFont.systemFont(ofSize: 16.0, weight: .bold)
    }

    class var headerListSmall: UIFont {
        return UIFont.systemFont(ofSize: 16.0, weight: .bold)
    }

    class var headerTextRegular: UIFont {
        return UIFont.systemFont(ofSize: 16.0, weight: .regular)
    }

    class var note: UIFont {
        return UIFont.systemFont(ofSize: 16.0, weight: .regular)
    }

    class var noteList: UIFont {
        return UIFont.systemFont(ofSize: 16.0, weight: .regular)
    }

    class var textRegular: UIFont {
        return UIFont.systemFont(ofSize: 16.0, weight: .regular)
    }

    class var noteListSmall: UIFont {
        return UIFont.systemFont(ofSize: 14.0, weight: .regular)
    }

}

func TextWithLineHeight(text: String, height: CGFloat) -> NSAttributedString {
    let style = NSMutableParagraphStyle()
    style.minimumLineHeight = height
    style.maximumLineHeight = height

    return NSAttributedString(string: text, attributes: [
        NSMutableAttributedString.Key.paragraphStyle: style
    ])
}
