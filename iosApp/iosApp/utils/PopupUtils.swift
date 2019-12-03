import Foundation

protocol Baloon {
    func hide()
}

protocol BaloonContainer {
    func show(popup: Baloon)
    func hide()
}

