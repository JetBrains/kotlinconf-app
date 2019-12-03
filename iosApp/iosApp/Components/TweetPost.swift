import UIKit
import KotlinConfAPI
import Nuke

class TweetPost : UICollectionViewCell {
    @IBOutlet weak var author: UILabel!
    @IBOutlet weak var account: UILabel!
    @IBOutlet weak var time: UILabel!
    @IBOutlet weak var text: UILabel!

    @IBOutlet weak var media: UIImageView!
    @IBOutlet weak var avatar: UIImageView!

    var post: FeedPost! {
        didSet {
            let user = post.user

            author.text = user.name
            account.text = "@" + user.screen_name

            time.text = post.displayDate()
            text.text = post.text

            Nuke.loadImage(with: URL(string: user.profile_image_url_https)!, into: avatar)

            let links = post.entities.media.map { media in
                media.media_url_https
            }.filter { $0 != nil }

            if (links.count > 0) {
                Nuke.loadImage(with: URL(string: links[0]!)!, into: media)
            } else {
                media.image = nil
            }
        }
    }
}
