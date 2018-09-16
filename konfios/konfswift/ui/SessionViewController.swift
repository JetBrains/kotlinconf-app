import UIKit
import TagListView_ObjC
import konfios
import MBProgressHUD

class SessionViewController : UIViewController, UITableViewDataSource, UITableViewDelegate, KTSessionDetailsView {
    private let repository = AppDelegate.me.konfService
    private var presenter: KTSessionDetailsPresenter!
    
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
        presenter = KTSessionDetailsPresenter(
            uiContext: UI(),
            view: self,
            sessionId: sessionId,
            repository: repository
        )
//        guard let session = self.session else { return }
//
//        titleLabel.text = session.title
//        timeLabel.text = KTStdlibPair(first: session.startsAt, second: session.endsAt).toReadableString()
//        descriptionLabel.text = session.descriptionText
//
//        updateFavoriteButtonTitle()
//
//        var tags: [String] = []
//        if (session.room != nil) {
//            tags.append(session.room!)
//        }
//
//        if (session.category != nil) {
//            tags.append(session.category!)
//        }
//
//        tagsLabel.text = tags.joined(separator: ", ")
//
////        DispatchQueue.main.async {
////            guard let usersTable = self.usersTable else { return }
////
////            let height: CGFloat, itemCount = usersTable.numberOfRows(inSection: 0)
////            if (itemCount == 0) {
////                height = 0
////            } else {
////                height = CGFloat(itemCount) * usersTable.cellForRow(at: IndexPath(row: 0, section: 0))!.bounds.height
////            }
////
////            usersTable.frame.size.height = height
////
////            self.scrollView.contentSize = CGSize(
////                width: self.scrollView.contentSize.width,
////                height: self.usersTable.frame.maxY + 10
////            )
////        }
//        setupSpeakers()
//
//        DispatchQueue.main.async {
//            self.highlightRatingButtons()
//        }
    }
    
    
    func updateView(loggedIn: Bool, session: KTSessionModel) {
        
    }
    
    func setupRatingButtons(rating: KTSessionRating?) {
        
    }
    
    func setIsFavorite(isFavorite: Bool) {
        
    }
    
    func setRatingClickable(clickable: Bool) {
        
    }

    private func updateFavoriteButtonTitle(isFavorite: Bool? = nil) {
//        let shouldCheck = isFavorite ?? konfService.isFavorite(sessionId: session.id)
//        let image = UIImage(named: shouldCheck ? "star_full" : "star_empty")!
//        favoriteButton.image = image
    }
    
    private func setupSpeakers() {
//        var speakers: [KTSpeaker] = session.speakers
//        userNamesLabel.text = speakers.map { (speaker) -> String in
//            speaker.fullName
//        }.joined(separator: ", ")
//
//        if (speakers.count == 1) {
//            userIcon1.isHidden = false
//            userIcon2.isHidden = true
//
//            userIcon1.loadUserIcon(url: speakers[0].profilePicture!)
//        } else if (speakers.count == 2) {
//            userIcon1.isHidden = false
//            userIcon2.isHidden = false
//
//            userIcon2.loadUserIcon(url: speakers[0].profilePicture!)
//            userIcon1.loadUserIcon(url: speakers[1].profilePicture!)
//        } else {
//            userIcon1.isHidden = true
//            userIcon2.isHighlighted = true
//        }
//
////        for (constraint in headerView.constraints.toList<NSLayoutConstraint>()) {
////            if (constraint.secondItem?.uncheckedCast<UIView>() == userNameStackView
////                && constraint.secondAttribute == NSLayoutAttributeTrailing
////                ) {
////                constraint.constant = if (speakers.size < 3) (56.0 + 56.0 / 2.0 + 10.0 + 20.0) else 20.0
////            }
////        }
    }

    @IBAction func favorited(_ sender: Any) {
//        let favorite = !konfService.isFavorite(sessionId: session.id)
//        konfService.setFavorite(sessionId: session.id, isFavorite: favorite) { (result, error) -> KTStdlibUnit in
//            if (error == nil) {
//                self.updateFavoriteButtonTitle(isFavorite: favorite)
//            }
//            return KTUnit
//        }
    }

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        super.prepare(for: segue, sender: sender)
//
//        if segue.identifier == "Vote", let controller = segue.destination as? VoteViewController {
//            controller.session = self.session
//        }
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 1 //Int(session.speakers.size)
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "User", for: indexPath) as! SessionUserTableViewCell
//        let speaker = session.speakers.get(index: Int32(indexPath.row)) as! KTSpeaker
//        cell.setup(for: speaker)
        return cell
    }

    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
//        let speaker = session.speakers.get(index: Int32(indexPath.row)) as! KTSpeaker
//
//        let alert = UIAlertController(title: speaker.fullName, message: speaker.bio, preferredStyle: .actionSheet)
//
//        for link in speaker.links {
//            guard let action = link.getAction() else { continue }
//            alert.addAction(action)
//        }
//
//        alert.addAction(UIAlertAction(title: "Cancel", style: .cancel, handler: nil))
//
//        self.present(alert, animated: true, completion: nil)
    }
    
    @IBAction private func goodPressed(_ sender: Any?) {
        reportRating(rating: KTSessionRating.good)
    }
    
    @IBAction private func sosoPressed(_ sender: Any?) {
        reportRating(rating: KTSessionRating.ok)
    }
    
    @IBAction private func badPressed(_ sender: Any?) {
        reportRating(rating: KTSessionRating.bad)
    }
    
    private func reportRating(rating: KTSessionRating) {
//        konfService.addRating(sessionId: session.id, rating: rating) { (result, error) -> KTStdlibUnit in
//            if (error != nil) {
//                let code = Int((error?.cause as! KTApiException).response.status.value)
//                switch (code) {
//                case 477:
//                    self.showPopupText(title: "Too early to set rating")
//                case 478:
//                    self.showPopupText(title: "Too late to set rating")
//                default:
//                    self.showPopupText(title: "Can't set rating - unknown error")
//                }
//            } else {
//                self.highlightRatingButtons(rating: rating)
//                self.showPopupText(title: result != nil ? "Thank you for the feedback!" : "Your vote was cleared.")
//            }
//            return KTUnit
//        }
    }
    
    private func highlightRatingButtons(rating: KTSessionRating? = nil) {
//        let currentRating = rating ?? konfService.getRating(sessionId: session.id)
//
//        let buttons: [KTSessionRating: UIButton] = [
//            .good: goodButton,
//            .ok: sosoButton,
//            .bad: badButton
//        ]
//
//        for (buttonRating, button) in buttons {
//            button.backgroundColor = (buttonRating == currentRating)
//                ? UIColor.orange
//                : UIColor.groupTableViewBackground
//        }
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

fileprivate extension KTLink {
    func getAction() -> UIAlertAction? {
        guard
            linkType == "Twitter",
            let url = URL(string: self.url)
        else { return nil }

        // + (instancetype)actionWithTitle:(NSString *)title style:(UIAlertActionStyle)style handler:(void (^)(UIAlertAction *action))handler;
        return (UIAlertAction(title: "\(title): @\(url.lastPathComponent)", style: .default) { _ in
            // @property(class, nonatomic, readonly) UIApplication *sharedApplication;

            if #available(iOS 10.0, *) {
                // - (void)openURL:(NSURL *)url options:(NSDictionary<NSString *,id> *)options completionHandler:(void (^)(BOOL success))completion;
                UIApplication.shared.open(url, options: [:], completionHandler: nil)
            } else {
                // - (BOOL)openURL:(NSURL *)url;
                UIApplication.shared.openURL(url)
            }
        })
    }
}

class SessionUserTableViewCell : UITableViewCell {
    @IBOutlet weak var nameLabel: UILabel!
    @IBOutlet weak var icon: UIImageView!

    override init(style: UITableViewCellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        doInit()
    }

    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        doInit()
    }

    private func doInit() {
        let bgColorView = UIView()
        bgColorView.backgroundColor = UIColor.clear
        self.selectedBackgroundView = bgColorView
    }

    func setup(for user: KTSpeaker) {
        nameLabel.text = user.fullName
        icon.loadUserIcon(url: user.profilePicture)
    }
}
