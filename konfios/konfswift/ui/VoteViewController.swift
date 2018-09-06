import UIKit
import konfios

class VoteViewController : UIViewController {
    private let konfService = AppDelegate.me.konfService
    var session: KTSessionModel!

    @IBOutlet private weak var titleLabel: UILabel!

    @IBOutlet weak var titleBackground: UIView!
    @IBOutlet weak var goodButton: UIButton!
    @IBOutlet weak var sosoButton: UIButton!
    @IBOutlet weak var badButton: UIButton!

    override func viewDidLoad() {
        for view in [goodButton, sosoButton, badButton, titleBackground] {
            view?.layer.cornerRadius = 5
        }
    }

    private func highlightRatingButtons(rating: KTSessionRating? = nil) {
        let currentRating = rating ?? konfService.getRating(sessionId: session.id)

        let buttons: [KTSessionRating: UIButton] = [
            .good: goodButton,
            .ok: sosoButton,
            .bad: badButton
        ]

        for (buttonRating, button) in buttons {
            button.backgroundColor = (buttonRating == currentRating)
                ? UIColor.orange
                : UIColor.groupTableViewBackground
        }
    }

    override func viewWillAppear(_ animated: Bool) {
        guard let session = self.session else { return }
        titleLabel.text = session.title

        // Buttons are not highlighted without this for some reason
        DispatchQueue.main.async {
            self.highlightRatingButtons()
        }
    }

    private func reportRating(_ rating: KTSessionRating) {
        guard let session = self.session else { return }

        konfService.addRating(sessionId: session.id, rating: rating) { (result, error) -> KTStdlibUnit in
            if (error != nil) {
                switch (error) {
                    
//                case KTKonfServiceCompanion().early_SUBMITTION_ERROR:
//                    self.showPopupText(title: "Too early to set rating")
//                case KSFKonfServiceCompanion().late_SUBMITTION_ERROR:
//                    self.showPopupText(title: "Too late to set rating")
                default:
                    self.showPopupText(title: "Can't set rating - unknown error")
                }
            } else {
                self.highlightRatingButtons(rating: rating)
                self.showPopupText(title: result != nil ? "Thank you for the feedback!" : "Your vote was cleared.")
            }
            return KTUnit
        }
    }

    @IBAction private func goodPressed(_ sender: Any) {
        reportRating(.good)
    }

    @IBAction private func sosoPressed(_ sender: Any) {
        reportRating(.ok)
    }

    @IBAction private func badPressed(_ sender: Any) {
        reportRating(.bad)
    }
}
