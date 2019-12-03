import Foundation
import UIKit
import KotlinConfAPI

enum Section {
    case all
    case favorites
}

class ScheduleController : UIViewController, UITableViewDelegate, UITableViewDataSource, UISearchBarDelegate, BaloonContainer {
    @IBOutlet weak var scheduleTable: UITableView!
    @IBOutlet weak var headerView: UIView!
    @IBOutlet weak var searchContainer: UIView!
    @IBOutlet weak var stackView: UIStackView!
    @IBOutlet weak var searchBar: UISearchBar!

    var activePopup: UIView? = nil

    let tableHeader = UINib(nibName: "ScheduleHeader", bundle: nil)
        .instantiate(withOwner: nil, options: [:])[0] as! ScheduleHeader

    private var all: [SessionGroup] = []
    private var favorites: [SessionGroup] = []
    private var sessions: [SessionCard] = []
    private var searchResult: [SessionCard] = []

    private var searchActive = false
    private var section: Section = .all

    private var currentTable: [SessionGroup] {
        get {
            return (section == .all) ? all : favorites
        }
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        
        if #available(iOS 13.0, *) {
            overrideUserInterfaceStyle = .light
            searchBar.barStyle = .black
        }

        scheduleTable.register(
            UINib(nibName: "ScheduleTableHeader", bundle: nil),
            forHeaderFooterViewReuseIdentifier: "ScheduleTableHeader"
        )

        scheduleTable.register(
            UINib(nibName: "ScheduleTableSmallHeader", bundle: nil),
            forHeaderFooterViewReuseIdentifier: "ScheduleTableSmallHeader"
        )

        configureTableHeader()

        scheduleTable.delegate = self
        scheduleTable.dataSource = self
        searchBar.delegate = self

        Conference.schedule.watch { data in
            self.onSchedule(sessions: data as! [SessionGroup])
        }

        Conference.favoriteSchedule.watch { data in
            self.onFavorites(sessions: data as! [SessionGroup])
        }

        Conference.sessions.watch { data in
            self.onSessions(sessions: data as! [SessionCard])
        }

        self.view.isUserInteractionEnabled = true
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        self.tabBarController?.tabBar.tintColor = UIColor.redOrange

        if (searchActive) {
            showSearch()
        } else {
            searchContainer.isHidden = true
        }
    }

    override func viewDidAppear(_ animated: Bool) {
        self.navigationController!.interactivePopGestureRecognizer!.isEnabled = false
    }

    override func viewDidDisappear(_ animated: Bool) {
        self.navigationController!.interactivePopGestureRecognizer!.isEnabled = true
    }

    func configureTableHeader() {
        tableHeader.onAllTouch = {
            self.section = .all
            self.scheduleTable.reloadData()
        }

        tableHeader.onFavoritesTouch = {
            self.section = .favorites
            self.scheduleTable.reloadData()
        }

        tableHeader.onSearchTouch = {
            self.showSearch()
        }

        headerView.addSubview(tableHeader)
    }

    private func showSearch() {
        self.searchActive = true
        self.scheduleTable.reloadData()

        self.searchContainer.isHidden = false
        self.headerView.isHidden = true
    }

    @IBAction func onSearchCancel(_ sender: Any) {
        searchActive = false
        scheduleTable.reloadData()

        self.view.endEditing(false)
        self.searchContainer.isHidden = true
        self.headerView.isHidden = false
    }

    func onSchedule(sessions: [SessionGroup]) {
        all = sessions
        if (section == .all) {
            scheduleTable.reloadData()
        }
    }

    func onFavorites(sessions: [SessionGroup]) {
        favorites = sessions
        if (section == .favorites) {
            scheduleTable.reloadData()
        }
    }

    func onSessions(sessions: [SessionCard]) {
        self.sessions = sessions
        searchResult = sessions
    }

    @IBAction
    func onRefresh(_ refreshControl: UIRefreshControl) {
        Conference.refresh()
    }

    func numberOfSections(in tableView: UITableView) -> Int {
        if (searchActive) {
            return 1
        }

        let table = (section == .all) ? all : favorites
        return table.count
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if (searchActive) {
            return searchResult.count
        }

        return currentTable[section].sessions.count
    }

    func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        return createViewForHeader(tableView, section: section)
    }

    func createViewForHeader(_ tableView: UITableView, section: Int) -> UIView? {
        if (searchActive) {
            return nil
        }

        let card = currentTable[section]

        if (card.daySection) {
            let breakHeader = tableView.dequeueReusableHeaderFooterView(
                withIdentifier: "ScheduleTableSmallHeader"
            ) as! ScheduleTableSmallHeader

            breakHeader.displayDay(title: card.title)
            return breakHeader
        }

        if (card.lunchSection) {
            let breakHeader = tableView.dequeueReusableHeaderFooterView(
                withIdentifier: "ScheduleTableHeader"
            ) as! ScheduleTableHeader

            breakHeader.configureLook(month: card.month.value.uppercased(), day: Int(card.day), title: card.title)
            return breakHeader
        }

        let timeHeader = tableView.dequeueReusableHeaderFooterView(
            withIdentifier: "ScheduleTableHeader"
        ) as! ScheduleTableHeader

        timeHeader.configureLook(month: card.month.value.uppercased(), day: Int(card.day), title: card.title)
    
        return timeHeader
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let section = indexPath.section
        let row = indexPath.row

        let result = tableView.dequeueReusableCell(
            withIdentifier: "ScheduleTableCell", for: indexPath
        ) as! ScheduleTableCell

        let card = searchActive ? searchResult[row] : currentTable[section].sessions[row]
        configureCell(cell: result, card: card)

        return result
    }

    func searchBar(_ searchBar: UISearchBar, textDidChange searchText: String) {
        updateSearchResults()
    }

    func searchBarSearchButtonClicked(_ searchBar: UISearchBar) {
        self.view.endEditing(false)
    }

    private func updateSearchResults() {
        let query = searchBar.text?.lowercased()

        if (query == nil || query!.isEmpty) {
            searchResult = sessions
        } else {
            searchResult = sessions.filter { card in
                let title = card.session.title.lowercased()
                let speakers = card.speakers.map { $0.fullName.lowercased() }.joined()
                let room = card.location.name.lowercased()
                return title.contains(query!) || speakers.contains(query!) || room.contains(query!)
            }
        }

        scheduleTable.reloadData()
    }

    private func configureCell(cell: ScheduleTableCell, card: SessionCard) {
        let item = cell.card!
        item.card = card
        item.baloonContainer = self

        cell.card.onTouch = {
            self.showSession(card: card)
        }
    }

    private func showSession(card: SessionCard) {
        let sessionBoard = UIStoryboard(name: "Main", bundle: nil)
        let sessionView = sessionBoard.instantiateViewController(
            withIdentifier: "Session"
        ) as! SessionController

        sessionView.card = card
        self.navigationController?.pushViewController(sessionView, animated: true)
    }

    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        if (searchActive) {
            return 1
        }

        let view = createViewForHeader(tableView, section: section)
        (view as? ScheduleTableHeader)?.titleLabel?.sizeToFit()

        let textHeight = (view as? ScheduleTableHeader)?.titleLabel?.frame.height
        if (textHeight != nil) {
            return textHeight! + 40 + 15
        }

        return 42
    }

    func tableView(_ tableView: UITableView, heightForFooterInSection section: Int) -> CGFloat {
        return 0
    }

    private let bounceGap = CGFloat(10.0)
    private var startOffset = CGFloat(0.0)

    private var active: Baloon? = nil
    func show(popup: Baloon) {
        active?.hide()
        active = popup
    }

    func hide() {
        active?.hide()
        active = nil
    }

    func scrollViewWillBeginDragging(_ scrollView: UIScrollView) {
        startOffset = scrollView.contentOffset.y
        if active != nil {
            active!.hide()
            active = nil
        }

        if (activePopup != nil) {
            activePopup?.isHidden = true
            activePopup = nil
        }
    }

    func scrollViewDidScroll(_ scrollView: UIScrollView) {
        if (searchActive) {
            return
        }

        let currentOffset = scrollView.contentOffset.y
        if (currentOffset > startOffset + bounceGap && !headerView.isHidden) {
            UIView.transition(
                with: headerView,
                duration: 0.2,
                options: [],
                animations: {
                    self.headerView.isHidden = true
            }, completion: nil
            )
        }

        if (currentOffset + bounceGap < startOffset && headerView.isHidden) {
            UIView.transition(
                with: headerView,
                duration: 0.2,
                options: [],
                animations: {
                    self.headerView.isHidden = false
                }, completion: nil
            )
        }
    }
}
