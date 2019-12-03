import Foundation
import UIKit
import youtube_ios_player_helper
import KotlinConfAPI

class LiveVideo : UICollectionViewCell {
    @IBOutlet weak var sessionTitle: UILabel!
    @IBOutlet weak var speaker: UILabel!
    @IBOutlet weak var video: YTPlayerView!
    @IBOutlet weak var location: UILabel!
    @IBOutlet weak var favoriteButton: UIButton!

    var favoriteObservable: Ktor_ioCloseable? = nil
    var liveObservable: Ktor_ioCloseable? = nil

    var card: SessionCard! {
        didSet {
            favoriteObservable?.close()

            sessionTitle.text = card.session.displayTitle
            speaker.text = card.speakers.map { (speaker) -> String in
                speaker.fullName
            }.joined(separator: ", ")

            location.text = card.location.displayName(isWorkshop: false)

            card.isLive.watch { videoId in
                if (videoId != nil && self.video.playerState() != .playing) {
                    self.video.load(withVideoId: String(videoId!))
                }
            }

            favoriteObservable = card.isFavorite.watch { isFavorite in
                self.favoriteButton.isSelected = isFavorite!.boolValue
            }
        }
    }

    @IBAction func favoriteTouch(_ sender: Any) {
        Conference.markFavorite(sessionId: card.session.id)
    }
}
