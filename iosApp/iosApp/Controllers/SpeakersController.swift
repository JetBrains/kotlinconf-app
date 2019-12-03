import Foundation
import UIKit
import KotlinConfAPI
import Nuke

class SpeakersController : UIViewController, UITableViewDataSource, UITableViewDelegate, UIGestureRecognizerDelegate {
    @IBOutlet weak var speakersList: UITableView!
    private var speakers: [SpeakerData] = []

    override func viewDidLoad() {
        super.viewDidLoad()

        if #available(iOS 13.0, *) {
            overrideUserInterfaceStyle = .light
        } else {
            // Fallback on earlier versions
        }

        speakersList.dataSource = self
        speakersList.delegate = self

        Conference.speakers.watch { speakers in
            return self.onSpeakers(speakers: speakers as! [SpeakerData])
        }
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        self.tabBarController?.tabBar.tintColor = UIColor.deepSkyBlue
    }

    override func viewDidAppear(_ animated: Bool) {
        self.navigationController!.interactivePopGestureRecognizer!.isEnabled = false
    }

    override func viewDidDisappear(_ animated: Bool) {
        self.navigationController!.interactivePopGestureRecognizer!.isEnabled = true
    }

    func onSpeakers(speakers: [SpeakerData]) {
        self.speakers = speakers
        speakersList.reloadData()
    }

    func numberOfSections(in tableView: UITableView) -> Int {
        return speakers.count
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 1
    }

    func tableView(_ tableView: UITableView, heightForFooterInSection section: Int) -> CGFloat {
        if (section == speakers.count - 1) {
            return 100
        }

        return 20
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "SpeakerCell") as! SpeakerCellView
        cell.speaker = speakers[indexPath.section]
        return cell
    }

    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: false)

        let board = UIStoryboard(name: "Main", bundle: nil)
        let controller = board.instantiateViewController(withIdentifier: "Speaker") as! SpeakerController

        let speaker = speakers[indexPath.section]
        controller.speaker = speaker

        self.navigationController?.pushViewController(controller, animated: true)
    }

    func tableView(_ tableView: UITableView, willDisplayFooterView view: UIView, forSection section: Int) {
        view.tintColor = UIColor.white
    }
}

class SpeakerCellView : UITableViewCell {
    @IBOutlet weak var speakerName: UILabel!
    @IBOutlet weak var speakerPosition: UILabel!
    @IBOutlet weak var speakerPhoto: UIImageView!

    var speaker: SpeakerData! {
        didSet {
            speakerName.text = speaker.fullName.uppercased()
            speakerPosition.text = speaker.tagLine

            let colorParams: [String: Any] = [
                "inputSaturation": "0.0",
                "inputContrast": "0.93"
            ]

            if let profilePicture = speaker.profilePicture {
                let request = ImageRequest(
                    url: URL(string: profilePicture)!,
                    processors: [ImageProcessor.CoreImageFilter(
                        name: "CIColorControls",
                        parameters: colorParams,
                        identifier: "CIColorControls"
                    )]
                )

                Nuke.loadImage(with: request, into: speakerPhoto)
            }
        }
    }
}
