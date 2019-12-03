import Foundation
import UIKit
import UserNotifications
import KotlinConfAPI

class SessionCardView : UIView, Baloon {
    @IBOutlet var mainView: UIView!
    @IBOutlet weak var container: UIView!

    @IBOutlet weak var title: UILabel!
    @IBOutlet weak var speakers: UILabel!
    @IBOutlet weak var fadeOut: UIImageView!

    @IBOutlet weak var liveIcon: UIImageView!
    @IBOutlet weak var liveLabel: UILabel!

    @IBOutlet weak var locationArrow: UIImageView!
    @IBOutlet weak var location: UILabel!

    @IBOutlet weak var voteButton: UIButton!
    @IBOutlet weak var favoriteButton: UIButton!

    @IBOutlet weak var voteUp: UIButton!
    @IBOutlet weak var voteOk: UIButton!
    @IBOutlet weak var voteDown: UIButton!

    @IBOutlet weak var voteBar: UIView!
    @IBOutlet weak var touchView: UIView!
    @IBOutlet weak var locationDistance: NSLayoutConstraint!
    
    var baloonContainer: BaloonContainer? = nil

    private var liveObservable: Ktor_ioCloseable? = nil
    private var ratingObservable: Ktor_ioCloseable? = nil
    private var favoriteObservable: Ktor_ioCloseable? = nil

    var onTouch: () -> Void = {}

    var displayTime = false
    var card: SessionCard! {
        didSet {
            if (liveObservable != nil) {
                liveObservable?.close()
                ratingObservable?.close()
                favoriteObservable?.close()
            }

            title.text = card.session.displayTitle

            speakers.text = card.speakers.map { speaker in
                speaker.fullName
            }.joined(separator: ", ")


            liveObservable = card.isLive.watch { videoId in
                self.setLive(live: videoId != nil && videoId != "")
            }

            favoriteObservable = card.isFavorite.watch { favorite in
                self.setFavorite(favorite: favorite!.boolValue)
            }

            ratingObservable = card.ratingData.watch { rating in
                self.configureVote(rating: rating)
            }

            voteBar.isHidden = true

            if (displayTime) {
                locationArrow.isHidden = true
                location.text = card.displayTime()
                locationDistance.constant = 1.0
            } else {
                let isWorkshop = card.session.isWorkshop()
                location.text = card.location.displayName(isWorkshop: isWorkshop)
                locationDistance.constant = 35.0
            }
        }
    }

    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        configure()
    }

    override init(frame: CGRect) {
        super.init(frame: frame)
        configure()
    }

    private func configure() {
        Bundle.main.loadNibNamed("SessionCardView", owner: self, options: nil)
        addSubview(mainView)
        mainView.frame = self.bounds

        isUserInteractionEnabled = true
        voteBar.isHidden = true

        voteBar.layer.shadowColor = UIColor.black.cgColor
        voteBar.layer.shadowOffset = CGSize(width: 0, height: 0.5)
        voteBar.layer.shadowOpacity = 0.15
    }

    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        super.touchesBegan(touches, with: event)
        let point = touches.first!.location(in: self)

        if (point.y > touchView.frame.height) {
            return
        }

        onTouch()
    }

    private func setLive(live: Bool) {
        liveIcon.isHidden = !live
        liveLabel.isHidden = !live

        liveLabel.text = "Live now"

        if (displayTime) {
            location.text = live ? "" : "    \(card.displayTime())"
            location.isHidden = live
        }
    }

    private func setFavorite(favorite: Bool) {
        favoriteButton.isSelected = favorite
    }

    @IBAction func voteTouch(_ sender: Any) {
        UIView.transition(
            with: voteBar,
            duration: 0.3,
            options: [.transitionCrossDissolve],
            animations: {
                self.voteBar.isHidden = false
            },
            completion: nil
        )

        baloonContainer?.show(popup: self)
    }

    func hide() {
        UIView.transition(
            with: voteBar,
            duration: 0.3,
            options: [.transitionCrossDissolve],
            animations: {
                self.voteBar.isHidden = true
        },
            completion: nil
        )
    }

    @IBAction func vodeGood(_ sender: Any) {
        Conference.vote(sessionId: card.session.id, rating: RatingData.Companion.init().GOOD)
    }

    @IBAction func voteOk(_ sender: Any) {
        Conference.vote(sessionId: card.session.id, rating: RatingData.Companion.init().OK)
    }

    @IBAction func voteBad(_ sender: Any) {
        Conference.vote(sessionId: card.session.id, rating: RatingData.Companion.init().BAD)
    }

    @IBAction func favoriteTouch(_ sender: Any) {
        Conference.markFavorite(sessionId: card.session.id)
    }

    private func configureVote(rating: RatingData?) {
        voteUp.isSelected = rating == RatingData.Companion.init().GOOD
        voteOk.isSelected = rating == RatingData.Companion.init().OK
        voteDown.isSelected = rating == RatingData.Companion.init().BAD

        let image: UIImage = {
            switch rating {
            case RatingData.Companion.init().GOOD: return UIImage(named: "voteGoodOrange")!
            case RatingData.Companion.init().OK: return UIImage(named: "voteOkOrange")!
            case RatingData.Companion.init().BAD: return UIImage(named: "voteBadOrange")!
            default: return UIImage(named: "voteGood")!
            }
        }()

        voteButton.setImage(image, for: .normal)
        baloonContainer?.hide()
    }

    func cleanup() {
        liveObservable?.close()
        ratingObservable?.close()
        favoriteObservable?.close()
    }

    func setupDarkMode() {
        container.backgroundColor = UIColor.cardGray
        title.textColor = UIColor.white
        speakers.textColor = UIColor.white
        location.textColor = UIColor.white60
        liveLabel.textColor = UIColor.white60

        fadeOut.image = UIImage(named: "fadeOutDark")
        locationArrow.image = UIImage(named: "mapLight")

        voteButton.isHidden = true

        favoriteButton.setImage(UIImage(named: "favoriteWhiteEmpty"), for: .normal)
        favoriteButton.setImage(UIImage(named: "favoriteWhite"), for: .selected)
    }
}
