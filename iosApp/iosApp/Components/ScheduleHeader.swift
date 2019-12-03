import Foundation
import UIKit

class ScheduleHeader : UIView {
    @IBOutlet weak var allSessions: TopButton!
    @IBOutlet weak var favorites: TopButton!

    var onSearchTouch: () -> Void = {}
    var onAllTouch: () -> Void = {}
    var onFavoritesTouch: () -> Void = {}

    @IBAction func allSessionsTouch(_ sender: Any) {
        favorites.light()
        allSessions.dark()
        onAllTouch()
    }

    @IBAction func favoritesTouch(_ sender: Any) {
        favorites.dark()
        allSessions.light()
        onFavoritesTouch()
    }

    @IBAction func onSearchTouch(_ sender: Any) {
        onSearchTouch()
    }

}
