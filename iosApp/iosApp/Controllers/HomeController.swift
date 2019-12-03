import Foundation
import UIKit
import KotlinConfAPI

class HomeController : UIViewController, UICollectionViewDataSource, UIGestureRecognizerDelegate, UICollectionViewDelegateFlowLayout {
    @IBOutlet weak var videosView: UICollectionView!
    @IBOutlet weak var feedView: UICollectionView!
    @IBOutlet weak var upcomingFavorites: UIStackView!
    @IBOutlet weak var dontMissLabel: UILabel!
    @IBOutlet weak var liveLabel: UILabel!

    @IBOutlet weak var liveHider: NSLayoutConstraint!
    @IBOutlet weak var videosHider: NSLayoutConstraint!
    @IBOutlet weak var dontMissHider: NSLayoutConstraint!
    @IBOutlet weak var cardsHider: NSLayoutConstraint!

    @IBOutlet weak var progressView: UIProgressView!
    @IBOutlet weak var progressDone: UILabel!
    @IBOutlet weak var progressDoneDescription: UILabel!

    private var liveSessions: [SessionCard] = []
    private var feedData: [FeedPost] = []
    private var upcoming: [SessionCardView] = []

    override func viewDidLoad() {
        super.viewDidLoad()

        if #available(iOS 13.0, *) {
            overrideUserInterfaceStyle = .dark
        }

        videosView.dataSource = self
        videosView.delegate = self

        feedView.dataSource = self
        feedView.delegate = self

        Conference.liveSessions.watch { cards in
            self.onLiveSessions(sessions: cards as! [SessionCard])
        }

        Conference.upcomingFavorites.watch { cards in
            self.onUpcomingFavorites(sessions: cards as! [SessionCard])
        }

        Conference.feed.watch { feed in
            self.onFeedData(feed: feed)
        }

        Conference.votes.watch { votes in
            let count = Float(votes?.count ?? 0)
            let progress = min(1.0, count / Float(Conference.votesCountRequired()))

            let hidden = progress != 1.0


            self.progressDone.isHidden = hidden
            self.progressDoneDescription.isHidden = hidden
            self.progressView.progress = progress
        }

        navigationController!.interactivePopGestureRecognizer!.delegate = self
        self.navigationController!.interactivePopGestureRecognizer!.isEnabled = true
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        self.tabBarController?.tabBar.tintColor = UIColor.redOrange
    }

    override func viewDidAppear(_ animated: Bool) {
        self.navigationController?.interactivePopGestureRecognizer?.isEnabled = false
    }

    override func viewDidDisappear(_ animated: Bool) {
        self.navigationController?.interactivePopGestureRecognizer?.isEnabled = true
    }

    @IBAction func locatorTouch(_ sender: Any) {
        let url = URL(string: "https://apps.apple.com/us/app/id1487944666?ls=1")!
        UIApplication.shared.open(url)
    }

    private func onLiveSessions(sessions: [SessionCard]) {
        let hidden = sessions.count == 0
        videosView.isHidden = hidden
        liveLabel.isHidden = hidden
        if (hidden) {
            liveHider.priority = UILayoutPriority(rawValue: 999)
            videosHider.priority = UILayoutPriority(rawValue: 999)
        } else {
            liveHider.priority = UILayoutPriority(rawValue: 1)
            videosHider.priority = UILayoutPriority(rawValue: 1)
        }

        liveSessions = sessions
        videosView.reloadData()
    }

    private func onUpcomingFavorites(sessions: [SessionCard]) {
        let hidden = sessions.count == 0
        dontMissLabel.isHidden = hidden
        upcomingFavorites.isHidden = hidden
        if (hidden) {
            dontMissHider.priority = UILayoutPriority(rawValue: 999)
            cardsHider.priority = UILayoutPriority(rawValue: 999)
        } else {
            dontMissHider.priority = UILayoutPriority(rawValue: 1)
            cardsHider.priority = UILayoutPriority(rawValue: 1)
        }

        for card in upcoming {
            upcomingFavorites.removeArrangedSubview(card)
            card.cleanup()
        }

        upcoming = sessions.map { card in
            let view = SessionCardView()
            view.card = card
            view.setupDarkMode()

            view.onTouch = {
                self.showScreen(name: "Session", config: { controller in
                    (controller as! SessionController).card = card
                })
            }

            upcomingFavorites.addArrangedSubview(view)
            upcomingFavorites.setCustomSpacing(5.0, after: view)
            return view
        }

    }

    private func onFeedData(feed: FeedData?) {
        if (feed == nil) {
            return
        }

        feedData = feed!.statuses
        feedView.reloadData()
    }

    @IBAction func showPartner(_ sender: UIButton, forEvent event: UIEvent) {
        showScreen(name: "Partner") { controller in
            (controller as! PartnerController).partnerId = sender.tag
        }
    }

    func showScreen(name: String, config: (UIViewController) -> Void = { controller -> Void in return }) {
        let board = UIStoryboard(name: "Main", bundle: nil)
        let controller = board.instantiateViewController(withIdentifier: name)
        config(controller)
        self.navigationController?.pushViewController(controller, animated: true)
    }

    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        switch collectionView {
        case videosView:
            return liveSessions.count
        case feedView:
            return feedData.count
        default:
            return 0
        }
    }

    func collectionView(
        _ collectionView: UICollectionView,
        cellForItemAt indexPath: IndexPath
    ) -> UICollectionViewCell {
        switch collectionView {
        case feedView:
            let item = collectionView.dequeueReusableCell(withReuseIdentifier: "TweetPost", for: indexPath) as! TweetPost
            item.post = feedData[indexPath.row]
            return item
        default:
            let item = collectionView.dequeueReusableCell(
                withReuseIdentifier: "LiveVideo", for: indexPath
            ) as! LiveVideo
            item.card = liveSessions[indexPath.row]
            return item
        }
    }

    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        let smallScreen = UIScreen.main.bounds.width < 375
        let width = smallScreen ? 300 : 340
        let height = collectionView == videosView ? 260 : 440

        return CGSize(width: width, height: height)
    }

    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        switch collectionView {
        case videosView:
            let card = liveSessions[indexPath.row]
            showScreen(name: "Session") { controller in
                (controller as! SessionController).card = card
            }
        case feedView:
            let item = feedData[indexPath.row]
            let userId = item.user.id_str
            let statusId = item.id_str
            let url = URL(string: "https://twitter.com/\(userId)/status/\(statusId)")
            UIApplication.shared.open(url!)
        default:
            return
        }
    }
}

extension HomeController : UIScrollViewDelegate, UICollectionViewDelegate {
    func scrollViewWillEndDragging(
        _ scrollView: UIScrollView,
        withVelocity velocity: CGPoint,
        targetContentOffset: UnsafeMutablePointer<CGPoint>
    ) {
        let view: UICollectionView = {
            switch scrollView {
            default:
                return self.videosView
            }
        }()

        let layout = view.collectionViewLayout as! UICollectionViewFlowLayout
        var offset = targetContentOffset.pointee
        let cellWidth = layout.itemSize.width + layout.minimumLineSpacing

        let leftInset = scrollView.contentInset.left
        let index = round((offset.x + leftInset) / cellWidth)

        offset.x = index * cellWidth - leftInset

        targetContentOffset.pointee = offset
    }
}
