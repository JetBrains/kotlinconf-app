import UIKit
import TagListView_ObjC
import konfios
import MBProgressHUD

class SessionViewController : UIViewController, UITableViewDataSource, UITableViewDelegate, KTSessionDetailsView {

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
    private var session: KTSessionModel! // TODO: This should not be held here. Presenter holds state and it should be enough
    
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
        self.session = session
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
        presenter.rateSessionClicked(newRating: KTSessionRating.good)
    }
    
    @IBAction private func sosoPressed(_ sender: Any?) {
        presenter.rateSessionClicked(newRating: KTSessionRating.ok)
    }
    
    @IBAction private func badPressed(_ sender: Any?) {
        presenter.rateSessionClicked(newRating: KTSessionRating.bad)
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
            userIcon2.isHighlighted = true
        }
    }

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        super.prepare(for: segue, sender: sender)

        if segue.identifier == "Vote", let controller = segue.destination as? VoteViewController {
            controller.session = self.session
        }
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return Int(session.speakers.count)
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "User", for: indexPath) as! SessionUserTableViewCell
        let speaker = session.speakers[indexPath.row]
        cell.setup(for: speaker)
        return cell
    }

    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let speaker = session.speakers[indexPath.row]
        let alert = UIAlertController(title: speaker.fullName, message: speaker.bio, preferredStyle: .actionSheet)

        for link in speaker.links {
            guard let action = link.getAction() else { continue }
            alert.addAction(action)
        }

        alert.addAction(UIAlertAction(title: "Cancel", style: .cancel, handler: nil))

        self.present(alert, animated: true, completion: nil)
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

        return (UIAlertAction(title: "\(title): @\(url.lastPathComponent)", style: .default) { _ in
            if #available(iOS 10.0, *) {
                UIApplication.shared.open(url, options: [:], completionHandler: nil)
            } else {
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
