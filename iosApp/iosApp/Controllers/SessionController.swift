import Foundation
import UIKit
import youtube_ios_player_helper
import KotlinConfAPI

class SessionController: UIViewController, UIScrollViewDelegate {
    @IBOutlet weak var speaker1: UIButton!
    @IBOutlet weak var speaker2: UIButton!
    @IBOutlet weak var speaker1Container: UIView!
    @IBOutlet weak var speaker2Container: UIView!
    @IBOutlet weak var video: YTPlayerView!
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var timeLabel: UILabel!
    @IBOutlet weak var descriptionLabel: UILabel!
    @IBOutlet weak var locationLabel: UIButton!
    @IBOutlet weak var voteBar: UIView!
    @IBOutlet weak var scrollView: UIScrollView!
    @IBOutlet weak var speakers: UIStackView!

    @IBOutlet weak var voteButton: UIButton!
    @IBOutlet weak var favoriteButton: UIButton!
    @IBOutlet weak var voteUp: UIButton!
    @IBOutlet weak var voteOk: UIButton!
    @IBOutlet weak var voteDown: UIButton!

    private var ratingObserver: Ktor_ioCloseable? = nil
    private var favoriteObserver: Ktor_ioCloseable? = nil
    private var liveObserver: Ktor_ioCloseable? = nil

    var card: SessionCard!
    private var borders: [CALayer]!

    override func viewDidLoad() {
        super.viewDidLoad()
        scrollView.delegate = self
    }

    @objc func onTouch(sender: UIGestureRecognizer) {
        voteBar.isHidden = true
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)

        let session = card.session
        voteBar.isHidden = true

        // Title
        titleLabel.attributedText = TextWithLineHeight(text: session.title.uppercased(), height: 57)

        // Description
        descriptionLabel.text = session.descriptionText
        descriptionLabel.attributedText = TextWithLineHeight(text: session.descriptionText!, height: 24)

        // Speakers
        speaker1Container.isHidden = true
        speaker2Container.isHidden = true

        if (card.speakers.count > 0) {
            let firstSpeaker = card.speakers[0]
            speaker1Container.isHidden = false
            speaker1.setTitle(firstSpeaker.fullName, for: .normal)
            if (card.speakers.count > 1) {
                speaker2Container.isHidden = false
                let secondSpeaker = card.speakers[1]
                speaker2.setTitle(secondSpeaker.fullName, for: .normal)
            } else {
                speaker2Container.isHidden = true
            }
        }

        // Time
        timeLabel.text = card.time + " " + card.date

        liveObserver = card.isLive.watch { video in
            self.liveChange(video)
        }

        // Location
        let isWorkshop = card.session.isWorkshop()
        locationLabel.setTitle(card.location.displayName(isWorkshop: isWorkshop), for: .normal)

        // Favorite
        favoriteObserver = card.isFavorite.watch { isFavorite in
            self.favoriteChange(isFavorite!.boolValue)
        }

        // Rating
        ratingObserver = card.ratingData.watch { rating in
            self.ratingChange(rating)
        }

        // button borders
        borders = [speaker1, speaker2, locationLabel].map { button in
            let label = button!.titleLabel
            label?.sizeToFit()
            let xOffset: CGFloat = (button?.imageView?.image != nil) ? 21.0 : 12.0

            var size = label!.bounds.size
            size.width += (button?.imageView?.image != nil) ? xOffset + 12.0 : xOffset * 2.0
            return label!.layer.addBorders(size, xOffset, 8)
        }

        speakers.sizeToFit()
    }

    override func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(animated)

        borders.forEach { layer in
            layer.removeFromSuperlayer()
        }
        releaseObservers()
    }

    @IBAction func backButtonTouch(_ sender: Any) {
        self.navigationController?.popViewController(animated: true)
    }

    @IBAction func voteTouch(_ sender: Any) {
        voteBar.isHidden = false
    }

    @IBAction func speaker1Touch(_ sender: Any) {
        showSpeaker(id: 0)
    }

    @IBAction func speaker2Touch(_ sender: Any) {
        showSpeaker(id: 1)
    }

    private func showSpeaker(id: Int) {
        let board = UIStoryboard(name: "Main", bundle: nil)
        let controller = board.instantiateViewController(withIdentifier: "Speaker") as! SpeakerController
        controller.speaker = card.speakers[id]
        self.navigationController?.pushViewController(controller, animated: true)
    }

    @IBAction func locationTouch(_ sender: Any) {
    }

    @IBAction func shareTouch(_ sender: Any) {
        let url = URL(string: card.session.url)!
        let items: [Any] = [card.session.displayTitle, url]
        let share = UIActivityViewController(activityItems: items, applicationActivities: nil)
        present(share, animated: true)
    }

    @IBAction func favoriteTouch(_ sender: Any) {
        Conference.markFavorite(sessionId: card.session.id)
    }

    @IBAction func voteUpTouch(_ sender: Any) {
        Conference.vote(sessionId: card.session.id, rating: RatingData.Companion.init().GOOD)
    }

    @IBAction func voteOkTouch(_ sender: Any) {
        Conference.vote(sessionId: card.session.id, rating: RatingData.Companion.init().OK)
    }

    @IBAction func voteDownTouch(_ sender: Any) {
        Conference.vote(sessionId: card.session.id, rating: RatingData.Companion.init().BAD)
    }

    private func liveChange(_ videoId: NSString?) {
        if (videoId != nil && videoId! != "") {
            self.video.load(withVideoId: String(videoId!))
            self.video.isHidden = false
        } else {
            self.video.isHidden = true
        }
    }

    private func ratingChange(_ rating: RatingData?) {
        voteUp.isSelected = rating == RatingData.Companion.init().GOOD
        voteOk.isSelected = rating == RatingData.Companion.init().OK
        voteDown.isSelected = rating == RatingData.Companion.init().BAD

        let image: UIImage = {
            switch rating {
            case RatingData.Companion.init().GOOD: return UIImage(named: "voteGoodWhite")!
            case RatingData.Companion.init().OK: return UIImage(named: "voteOkWhite")!
            case RatingData.Companion.init().BAD: return UIImage(named: "voteBadWhite")!
            default: return UIImage(named: "voteGoodLight")!
            }
        }()

        voteButton.setImage(image, for: .normal)
        voteBar.isHidden = true
    }

    private func favoriteChange(_ isFavorite: Bool) {
        favoriteButton.isSelected = isFavorite
    }

    func scrollViewWillBeginDragging(_ scrollView: UIScrollView) {
        voteBar.isHidden = true
    }

    private func releaseObservers() {
        ratingObserver?.close()
        favoriteObserver?.close()
        liveObserver?.close()
    }
}
