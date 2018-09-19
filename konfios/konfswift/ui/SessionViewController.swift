import UIKit
import TagListView_ObjC
import konfios
import MBProgressHUD
import PopupDialog

class SessionViewController : UIViewController, KTSessionDetailsView {

    private let repository = AppDelegate.me.konfService
    private lazy var presenter: KTSessionDetailsPresenter = {
        KTSessionDetailsPresenter(
            uiContext: UI(),
            view: self,
            sessionId: sessionId,
            repository: repository
        )
    }()
    
    var sessionId = ""
    
    @IBOutlet private weak var scrollView: UIScrollView!
    
    @IBOutlet private weak var titleLabel: UILabel!
    @IBOutlet private weak var timeLabel: UILabel!
    @IBOutlet private weak var tagsLabel: UILabel!
    @IBOutlet private weak var descriptionLabel: UILabel!
    
    @IBOutlet private weak var headerView: UIView!
    @IBOutlet private weak var userNameStackView: UIStackView!
    @IBOutlet private weak var userNamesLabel: UILabel!
    @IBOutlet private weak var userIcon1: UIImageView!
    @IBOutlet private weak var userIcon2: UIImageView!
    
    @IBOutlet private weak var favoriteButton: UIBarButtonItem!

    @IBOutlet private weak var sessionForm: UIView!
    @IBOutlet private weak var goodButton: UIButton!
    @IBOutlet private weak var sosoButton: UIButton!
    @IBOutlet private weak var badButton: UIButton!

    override func viewWillAppear(_ animated: Bool) {
        presenter.onCreate()
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        presenter.onDestroy()
    }
    
    func updateView(loggedIn: Bool, session: KTSessionModel) {
        titleLabel.text = session.title
        timeLabel.text = KTStdlibPair(first: session.startsAt, second: session.endsAt).toReadableString()
        
        let description = session.descriptionText
        descriptionLabel.text = description

        let tags: [String] = [session.room, session.category]
            .compactMap { $0 } // To remove nil
        tagsLabel.text = tags.joined(separator: ", ")

        setupSpeakers(speakers: session.speakers)
    }
    
    func setupRatingButtons(rating: KTSessionRating?) {
        let buttons: [KTSessionRating: UIButton] = [
            .good: goodButton,
            .ok: sosoButton,
            .bad: badButton
        ]

        for (buttonRating, button) in buttons {
            button.backgroundColor = (buttonRating == rating)
                ? UIColor.orange
                : UIColor.groupTableViewBackground
        }
    }
    
    func setIsFavorite(isFavorite: Bool) {
        let image = UIImage(named: isFavorite ? "star_full" : "star_empty")!
        favoriteButton.image = image
    }
    
    func setRatingClickable(clickable: Bool) {
        // TODO: This is a temporary click block to not let user make more than one voting at the time because anotherone would remove first one
    }

    @IBAction private func favorited(_ sender: Any) {
        presenter.onFavoriteButtonClicked()
    }
    
    @IBAction private func goodPressed(_ sender: Any?) {
        let codePresent = false // Todo: check if we have the code already
        if(codePresent){
            presenter.rateSessionClicked(newRating: .good)
        } else {
            showVotingCodeDialog()
        }
    }
    
    @IBAction private func sosoPressed(_ sender: Any?) {
        let codePresent = false // Todo: check if we have the code already
        if(codePresent){
            presenter.rateSessionClicked(newRating: .ok)
        } else {
            showVotingCodeDialog()
        }

    }
    
    @IBAction private func badPressed(_ sender: Any?) {
        let codePresent = false // Todo: check if we have the code already
        if(codePresent){
            presenter.rateSessionClicked(newRating: .bad)
        } else {
            showVotingCodeDialog()
        }
    }
    
    private func showVotingCodeDialog() {
        
        let ratingViewController = RatingViewController(nibName: "RatingViewController", bundle: nil)
        
        // Create the dialog
        let popup = PopupDialog(viewController: ratingViewController,
                                buttonAlignment: .horizontal,
                                transitionStyle: .bounceDown,
                                tapGestureDismissal: true,
                                panGestureDismissal: false)
        
        // Cancel button
        let buttonOne = CancelButton(title: "CANCEL", height: 60) {
            // Do nothing
        }
        
        // Submit button
        let buttonTwo = DefaultButton(title: "SUBMIT", height: 60) {
            if(ratingViewController.checked){
                print("Vote code:" + ratingViewController.voteText.text!)
            } else {
                print("Accept the terms!")
            }
        }
        
        // Add buttons to dialog
        popup.addButtons([buttonOne, buttonTwo])
        
        // Present dialog
        present(popup, animated: true, completion: nil)
    }
    
    private func setupSpeakers(speakers: [KTSpeaker]) {
        userNamesLabel.text = speakers.map { (speaker) -> String in speaker.fullName }.joined(separator: ", ")
        
        if (speakers.count == 1) {
            userIcon1.isHidden = false
            userIcon2.isHidden = true
            userIcon1.loadUserIcon(url: speakers[0].profilePicture!)
        } else if (speakers.count == 2) {
            userIcon1.isHidden = false
            userIcon2.isHidden = false
            userIcon2.loadUserIcon(url: speakers[0].profilePicture!)
            userIcon1.loadUserIcon(url: speakers[1].profilePicture!)
        } else {
            userIcon1.isHidden = true
            userIcon2.isHidden = true
        }
    }
    
    private func showIndeterminateProgress(message: String) -> MBProgressHUD {
        let hud = MBProgressHUD(for: self.view)!
        hud.label.text = message
        hud.removeFromSuperViewOnHide = true
        self.view.addSubview(hud)
        hud.show(animated: true)
        return hud
    }
}
