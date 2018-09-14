import UIKit
import konfios


class SessionsViewController: UIViewController, UITableViewDataSource, UITableViewDelegate {
    private static let SEND_ID_ONCE_KEY = "send_uuid_once"
    private lazy var konfService = AppDelegate.me.konfService
    private var mode = SessionsMode.all
    private var sessionsTableData: [[KTSessionModel]] = []

    lazy var pullToRefresh: UIRefreshControl = {
        let refresher = UIRefreshControl()
        refresher.addTarget(
            self,
            action: #selector(SessionsViewController.refreshSessions(_:)),
            for: UIControlEvents.valueChanged
        )

        return refresher
    }()
    
    @IBOutlet var tableView: UITableView!

    @IBAction func tabSelected(_ sender: Any) {
        guard let segmentedControl = sender as? UISegmentedControl else { return }
        self.mode = (segmentedControl.selectedSegmentIndex == 0) ? .all : .favorites

        self.updateTableContent()

        if let tableView = self.tableView, sessionsTableData.count > 0 {
            tableView.scrollToRow(at: IndexPath(item: 0, section: 0), at: .top, animated: true)
        }
    }

    override func viewDidLoad() {
        self.refreshSessions(self)
        tableView.backgroundView = pullToRefresh
    }

    override func viewWillAppear(_ animated: Bool) {
        self.updateTableContent()
    }

    @IBAction func refreshSessions(_ sender: Any) {        
        konfService.update { (data, error) -> KTStdlibUnit in
            self.pullToRefresh.endRefreshing()
            
            if (error != nil) {
                error?.cause?.printStackTrace()
                self.showPopupText(title: "Failed to refresh")
            } else {
                self.updateTableContent()
            }
            return KTUnit
        }
    }
    
    /**
     * Prepare TableView state
     */
    private func updateTableContent() {
        switch self.mode {
        case .all:
            fillDataWith(sessions: konfService.sessions ?? [])
            break
        case .favorites:
            fillDataWith(sessions: konfService.favorites ?? [])
            break
        }

        self.tableView?.reloadData()
    }
    
    private func fillDataWith(sessions: [KTSessionModel]) {
        sessionsTableData = []
        sessions.forEach({ (session) in
            if sessionsTableData.count == 0 ||
                sessionsTableData.last!.first!.startsAt != session.startsAt  {
                sessionsTableData.append([session]);
                return
            }

            sessionsTableData[sessionsTableData.count - 1].append(session)
        })
    }

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        super.prepare(for: segue, sender: sender)
        switch (segue.identifier ?? "") {
            case "ShowSession":
                guard
                    let selectedCell = sender as? SessionsTableViewCell,
                    let selectedPath = tableView?.indexPath(for: selectedCell)
                else { return }

                let sessionViewController = segue.destination as! SessionViewController
                let bucket = selectedPath.section
                let row = selectedPath.row
                if (sessionsTableData.count <= bucket || sessionsTableData[bucket].count <= row) { return }
//                sessionViewController.session = sessionsTableData[bucket][row]
            default: break
        }
    }


    func numberOfSections(in tableView: UITableView) -> Int {
        return sessionsTableData.count
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if sessionsTableData.count <= section { return 0 }
        return sessionsTableData[section].count
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "Session", for: indexPath) as! SessionsTableViewCell
        let bucket = indexPath.section
        let row = indexPath.row
        if sessionsTableData.count <= bucket || sessionsTableData[bucket].count <= row { return cell }
        cell.setup(for: sessionsTableData[bucket][row])
        return cell
    }
    
    func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        guard let session = sessionsTableData[section].first else { return nil }
        return session.startsAt.toReadableDateTimeString()
    }
}

enum SessionsMode {
    case all
    case favorites
}

class SessionsTableViewCell : UITableViewCell {
    @IBOutlet private weak var titleLabel: UILabel!
    @IBOutlet private weak var subtitleLabel: UILabel!

    func setup(for session: KTSessionModel) {
        titleLabel.text = session.title
        guard let speakers: [KTSpeaker] = session.speakers else {
            return
        }
        
        let speakersInfo = speakers.map { (speaker) -> String in
            speaker.fullName
        }.joined(separator: ", ")
        
        subtitleLabel.text = speakersInfo + " - " + session.room!
    }
}

class BreakTableViewCell : UITableViewCell {
    @IBOutlet private weak var titleLabel: UILabel!

    func setup(for session: KTSessionModel) {
        backgroundColor = UIColor(patternImage: UIImage(named: "striped_bg")!)
    }
}
