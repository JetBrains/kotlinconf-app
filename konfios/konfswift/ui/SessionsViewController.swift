import UIKit
import main

class SessionsViewController: UIViewController, UITableViewDataSource, UITableViewDelegate, SessionListView, NavigationManager, SearchQueryProvider {
    
    private var mode = SessionsMode.all
    private var sessionsTableData: [[SessionModel]] = []
    
    private let repository = AppDelegate.me.konfService
    lazy var presenter: SessionListPresenter = {
        SessionListPresenter(
            uiContext: UI(),
            view: self,
            repository: repository,
            navigationManager: self,
            searchQueryProvider: self
        )
    }()
    lazy var mainPresenter: MainPresenter = {
        MainPresenter(
            navigationManager: self,
            repository: repository
        )
    }()
    var searchQuery: String = ""
    
    var isUpdating: Bool {
        get {
            return self.pullToRefresh.isRefreshing
        }
        set {
            if(newValue) {
                self.pullToRefresh.beginRefreshing()
            } else {
                self.pullToRefresh.endRefreshing()
            }
        }
    }
    
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

    override func viewWillAppear(_ animated: Bool) {
        presenter.onCreate()
        mainPresenter.onCreate()
    }
    
    override func viewDidLoad() {
        self.refreshSessions(self)
        tableView.backgroundView = pullToRefresh
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        presenter.onDestroy()
    }
    
    func showSessionList() {
        // no-op, We are on session list
    }
    
    func showSessionDetails(sessionId: String) {
        // TODO: Move opening details to here
    }
    
    func addOnQueryChangedListener(listener: @escaping (String) -> KotlinUnit) {
        // no-op, Search is not supported yet
    }
    
    func showPrivacyPolicyDialog() {
        let storyBoard: UIStoryboard = UIStoryboard(name: "Main", bundle: nil)
        let newViewController = storyBoard.instantiateViewController(withIdentifier: "Terms") as! TermsController
        self.present(newViewController, animated: true, completion: nil)
    }
    
    func showTokenDialog() {
    }
    
    func onUpdate(sessions: [SessionModel], favorites: [SessionModel]) {
        switch self.mode {
        case .all:
            fillDataWith(sessions: sessions)
            break
        case .favorites:
            fillDataWith(sessions: favorites)
            break
        }
        self.tableView?.reloadData()
    }
    
    @IBAction func tabSelected(_ sender: Any) {
        guard let segmentedControl = sender as? UISegmentedControl else { return }
        self.mode = (segmentedControl.selectedSegmentIndex == 0) ? .all : .favorites
        presenter.showData()

        if let tableView = self.tableView, sessionsTableData.count > 0 {
            tableView.scrollToRow(at: IndexPath(item: 0, section: 0), at: .top, animated: true)
        }
    }

    @IBAction func refreshSessions(_ sender: Any) {
        presenter.onPullRefresh()
    }

    // Sessions are grouped by time slots
    private func fillDataWith(sessions: [SessionModel]) {
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
                sessionViewController.sessionId = sessionsTableData[bucket][row].id
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
        guard sessionsTableData.count > section, let session = sessionsTableData[section].first else { return nil }
        return session.startsAt?.toReadableDateTimeString()
    }
}

enum SessionsMode {
    case all
    case favorites
}

class SessionsTableViewCell : UITableViewCell {
    @IBOutlet private weak var titleLabel: UILabel!
    @IBOutlet private weak var subtitleLabel: UILabel!

    func setup(for session: SessionModel) {
        titleLabel.text = session.title
        let speakers: [Speaker] = session.speakers
        let speakersInfo = speakers.map { (speaker) -> String in
            speaker.fullName
        }.joined(separator: ", ")
        

        subtitleLabel.text = speakersInfo + " - " + (session.room ?? "")
    }
}

class BreakTableViewCell : UITableViewCell {
    @IBOutlet private weak var titleLabel: UILabel!

    func setup(for session: SessionModel) {
        backgroundColor = UIColor(patternImage: UIImage(named: "striped_bg")!)
    }
}
