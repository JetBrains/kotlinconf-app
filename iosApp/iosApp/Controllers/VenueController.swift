import Foundation
import UIKit
import Mapbox
import KotlinConfAPI

enum Floor {
    case ground
    case first
}

class VenueController : UIViewController, MGLMapViewDelegate, BaloonContainer, UIScrollViewDelegate {
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var logoView: UIImageView!
    @IBOutlet weak var sessionsStack: UIStackView!
    @IBOutlet weak var partnerDescription: UILabel!

    @IBOutlet weak var cardsScroll: UIScrollView!
    @IBOutlet weak var mapView: MGLMapView!
    @IBOutlet weak var dragBar: UIView!
    @IBOutlet weak var overlay: UIView!

    @IBOutlet weak var groundFloor: TopButton!
    @IBOutlet weak var firstFloor: TopButton!
    @IBOutlet weak var distance: NSLayoutConstraint!
    @IBOutlet weak var closeButton: UIButton!

    private var initial: CGFloat = 44.0
    private var floor: Floor = .ground
    private var descriptionActive: Bool = false

    private let mapPhotos = [
        7972: "keynote",
        7973: "aud_10_11_12",
        7974: "aud_10_11_12",
        7975: "aud 15",
        7976: "workshop room 20"
    ]


    override func viewDidLoad() {
        super.viewDidLoad()

        if #available(iOS 13.0, *) {
            overrideUserInterfaceStyle = .light
        }

        mapView.delegate = self
        cardsScroll.delegate = self

        let gesture = UIPanGestureRecognizer(
            target: self,
            action: #selector(VenueController.onPan(_:))
        )

        dragBar.addGestureRecognizer(gesture)
        dragBar.isUserInteractionEnabled = true

        let singleTap = UITapGestureRecognizer(target: self, action: #selector(handleMapTap(sender:)))
        mapView.addGestureRecognizer(singleTap)

        setupAttribution()
        mapView.compassViewMargins.y += 50.0

        let room = Conference.room(id:7972)
        if (room != nil) {
            showCard(room: room!)
        }
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        hideDescription()
        self.navigationController!.interactivePopGestureRecognizer!.isEnabled = false
    }

    override func viewDidDisappear(_ animated: Bool) {
        self.navigationController!.interactivePopGestureRecognizer!.isEnabled = true
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        self.tabBarController?.tabBar.tintColor = UIColor.deepSkyBlue
        showFloor()

        mapView.latitude = 55.638271
        mapView.longitude = 12.578472
        mapView.zoomLevel = 16
    }

    @IBAction func groundFloorSelect(_ sender: Any) {
        floor = .ground
        showFloor()
    }

    @IBAction func firstFloorSelect(_ sender: Any) {
        floor = .first
        showFloor()
    }

    @IBAction func onClose(_ sender: Any) {
        hideDescription()
    }

    private func showFloor() {
        if (floor == .ground) {
            mapView.styleURL = URL(string: "mapbox://styles/denisvoronov1/cjzikqjgb41rf1cnnb11cv0xw")
            groundFloor.dark()
            firstFloor.light()
        } else {
            mapView.styleURL = URL(string: "mapbox://styles/denisvoronov1/cjzsessm40k341clcoer2tn9v")
            groundFloor.light()
            firstFloor.dark()
        }
    }

    private var start: CGFloat = 0
    @objc func onPan(_ recognizer: UIPanGestureRecognizer) {
        let state = recognizer.state
        let translation = recognizer.translation(in: self.view)
        let current = distance.constant

        if state == .began {
            start = current
        }

        distance.constant += translation.y
        recognizer.setTranslation(CGPoint.zero, in: self.view)

        if state == .cancelled || state == .ended {
            if (!descriptionActive && start - current > 50) {
                showDescription()
            } else {
                hideDescription()
            }
        }
    }

    @objc @IBAction func handleMapTap(sender: UITapGestureRecognizer) {
        let spot = sender.location(in: mapView)

        let features = mapView.visibleFeatures(at: spot).map { feature in
            feature.attribute(forKey: "name") as? String
        }.filter { $0 != nil }

        let room = Conference.roomByMapName(namesInArea: features as! [String])
        if (room != nil) {
            showCard(room: room!)
            showDescription()
            return
        }

        let partners = features.map { name in
             ConfPartners.partnerByRoomName(name: name!)
        }.filter { $0 != nil }

        if (partners.count > 0) {
            showPartner(partner: partners.first!!)
            showDescription()
            return
        }
    }

    private func showCard(room: RoomData) {
        cleanupCards()
        let cards = Conference.roomSessions(roomId: room.id)
        partnerDescription.isHidden = true
        sessionsStack.isHidden = false

        for card in cards {
            let view = SessionCardView()
            view.displayTime = true
            view.card = card
            view.baloonContainer = self
            setupCard(view)
            sessionsStack.addArrangedSubview(view)
            sessionsStack.setCustomSpacing(5.0, after: view)
        }

        titleLabel.text = room.displayName(isWorkshop: false).uppercased()
        logoView.image = UIImage(named: mapPhotos[Int(room.id)]!)
        logoView.contentMode = .scaleAspectFill
    }

    private func showPartner(partner: Partner) {
        cleanupCards()
        sessionsStack.isHidden = true
        partnerDescription.isHidden = false

        titleLabel.text = partner.title.uppercased()
        partnerDescription.attributedText = TextWithLineHeight(text: ConfPartners.descriptionByName(name: partner.key), height: 24)
        logoView.image = UIImage(named: partnersLogos[partner.key]!)
        logoView.contentMode = .center
    }

    private func cleanupCards() {
        for item in sessionsStack.subviews {
           let cardView = item as! SessionCardView
           cardView.cleanup()
           sessionsStack.removeArrangedSubview(item)
        }
    }

    private func showDescription() {
        descriptionActive = true
        closeButton.isHidden = false
        self.distance.constant = 50
        UIView.animate(withDuration: 0.3, animations: {
            self.view.layoutIfNeeded()
        })
    }

    private func hideDescription() {
        descriptionActive = false
        closeButton.isHidden = true
        hide()
        self.distance.constant = view.frame.height - 300
        UIView.animate(withDuration: 0.3, animations: {
            self.view.layoutIfNeeded()
        })
    }

    private func setupAttribution() {
        mapView.attributionButtonPosition = .topLeft
        mapView.attributionButtonMargins.y = view.frame.height - 330
        mapView.attributionButton.tintColor = UIColor.blackGray
    }

    func scrollViewWillBeginDragging(_ scrollView: UIScrollView) {
        hide()
    }

    private var active: Baloon? = nil

    func show(popup: Baloon) {
        active?.hide()
        active = popup
    }

    func hide() {
        active?.hide()
        active = nil
    }
}
