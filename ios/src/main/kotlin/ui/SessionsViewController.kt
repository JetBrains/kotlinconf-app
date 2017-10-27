import libs.*
import kotlinx.cinterop.*
import platform.UIKit.*
import platform.Foundation.*
import platform.CoreData.*

@ExportObjCClass
@Suppress("CONFLICTING_OVERLOADS")
class SessionsViewController(aDecoder: NSCoder) :
        UIViewController(aDecoder),
        NSFetchedResultsControllerDelegateProtocol,
        UITableViewDataSourceProtocol,
        UITableViewDelegateProtocol
{
    private companion object {
        private val SEND_ID_ONCE_KEY = "sendIfOnce"
    }

    private val favoritesManager = FavoritesManager()

    private var mode: SessionsListMode = SessionsListMode.ALL

    private var _fetchedResultsController: NSFetchedResultsController? = null

    lateinit var pullToRefresh: UIRefreshControl

    @ObjCOutlet lateinit var tableView: UITableView

    private val fetchedResultsController: NSFetchedResultsController
        get() {
            _fetchedResultsController?.let { return it }
            
            val request = NSFetchRequest(entityName = "KSession")
            request.sortDescriptors = nsArrayOf(
                NSSortDescriptor.sortDescriptorWithKey("endsAt", ascending = true),
                NSSortDescriptor.sortDescriptorWithKey("roomName", ascending = true),
                NSSortDescriptor.sortDescriptorWithKey("id", ascending = true))

            if (mode == SessionsListMode.FAVORITES) {
                val favorites = favoritesManager.getFavoriteItemIds()
                request.predicate = NSPredicate.predicateWithFormat("id IN %@", argumentArray = nsArrayOf(favorites))
            }

            val moc = appDelegate.managedObjectContext
            val fetchedResultsController = NSFetchedResultsController(
                request, managedObjectContext = moc, sectionNameKeyPath = "startsAtDate", cacheName = null)
            fetchedResultsController.delegate = this

            _fetchedResultsController = fetchedResultsController
            return fetchedResultsController
        }

    override fun initWithCoder(aDecoder: NSCoder) = initBy(SessionsViewController(aDecoder))

    @ObjCAction
    fun tabSelected(sender: ObjCObject?) {
        val segmentedControl = sender.uncheckedCast<UISegmentedControl>()
        
        mode = if (segmentedControl.selectedSegmentIndex == 0L) SessionsListMode.ALL else SessionsListMode.FAVORITES
        updateResults()
    }

    override fun debugDescription() = "SessionsViewController"

    override fun viewDidLoad() {
        pullToRefresh = UIRefreshControl()
        pullToRefresh.addTarget(this, 
            action = NSSelectorFromString("refreshSessions:"),
             forControlEvents = UIControlEventValueChanged)
        tableView.backgroundView = pullToRefresh

        val moc = appDelegate.managedObjectContext
        val loadSessions = moc.countForFetchRequest(NSFetchRequest(entityName = "KSession"), error = null) <= 0

        if (loadSessions) {
            refreshSessions(this, showProgressPopup = true)
        } else {
            refreshFavorites()
            refreshVotes()
        }

        registerUuid()
    }

    override fun viewWillAppear(animated: Boolean) {
        updateResults()
    }

    private fun registerUuid() {
        // We have to register uuid each time we enter our app, cause the user db may be reset on server
        KonfService(errorHandler = { log(it.localizedDescription) }).registerUser(appDelegate.userUuid)
    }

    @ObjCAction
    fun refreshSessions(sender: ObjCObject?) {
        refreshSessions(sender, showProgressPopup = false)
    }

    private fun refreshSessions(sender: ObjCObject?, showProgressPopup: Boolean) {
        val progressPopup = if (showProgressPopup) showIndeterminateProgress("Loading sessions…") else null

        fun hideProgress() {
            progressPopup?.hideAnimated(true)
            pullToRefresh.endRefreshing()
        }

        val service = KonfService(errorHandler = createErrorHandler("Unable to load sessions.") {
            hideProgress()
        })

        KonfLoader(service).updateSessions {
            hideProgress()
            updateResults()
        }
    }

    private fun refreshFavorites() {
        val service = KonfService(errorHandler = { log(it.localizedDescription) })
        KonfLoader(service).updateFavorites {
            if (mode == SessionsListMode.FAVORITES) {
                updateResults()
            }
        }
    }

    private fun refreshVotes() {
        val service = KonfService(errorHandler = { log(it.localizedDescription) })
        KonfLoader(service).updateVotes()
    }

    private fun updateResults() {
        _fetchedResultsController = null

        try {
            nsTry { errorPtr -> fetchedResultsController.performFetch(error = errorPtr) }
            tableView.reloadData()
        } catch (e: NSErrorException) {
            showPopupText("Unable to load sessions.")
        }
    }

    override fun prepareForSegue(segue: UIStoryboardSegue, sender: ObjCObject?) {
        super.prepareForSegue(segue, sender = sender)

        if (segue.identifier == "ShowSession" && sender != null) {
            val selectedCell = sender.uncheckedCast<SessionsTableViewCell>()
            val selectedPath = tableView.indexPathForCell(selectedCell) ?: return

            val sessionViewController = segue.destinationViewController.uncheckedCast<SessionViewController>()
            sessionViewController.session = fetchedResultsController.objectAtIndexPath(selectedPath)!!.uncheckedCast<KSession>()
        }
    }

    override fun numberOfSectionsInTableView(tableView: UITableView): Long {
        return fetchedResultsController.sections?.count ?: 0
    }

    override fun tableView(tableView: UITableView, didSelectRowAtIndexPath: NSIndexPath) {
        tableView.deselectRowAtIndexPath(didSelectRowAtIndexPath, animated = false)
    }

    override fun tableView(tableView: UITableView, numberOfRowsInSection: Long): Long {
        val sections = fetchedResultsController.sections ?: return 0
        return sections[numberOfRowsInSection].uncheckedCast<NSFetchedResultsSectionInfoProtocol>().numberOfObjects
    }

    override fun tableView(tableView: UITableView, cellForRowAtIndexPath: NSIndexPath): UITableViewCell {
        val cell = tableView.dequeueReusableCellWithIdentifier(
                "Session", forIndexPath = cellForRowAtIndexPath).uncheckedCast<SessionsTableViewCell>()
        val session = fetchedResultsController.objectAtIndexPath(cellForRowAtIndexPath)?.uncheckedCast<KSession>()
        if (session != null) {    
            cell.setup(session)
        }
        return cell
    }
    
    override fun tableView(tableView: UITableView, titleForHeaderInSection: Long): String? {
        val sections = fetchedResultsController.sections ?: return null
        val sectionInfo = sections[titleForHeaderInSection]?.uncheckedCast<NSFetchedResultsSectionInfoProtocol>()
        val session = sectionInfo?.objects?.firstObject?.uncheckedCast<KSession>()
        return renderWeekdayTime(session?.startsAtDate)
    }
}

private enum class SessionsListMode {
    ALL, FAVORITES
}

@ExportObjCClass
class SessionsTableViewCell(aDecoder: NSCoder) : UITableViewCell(aDecoder) {
    @ObjCOutlet lateinit var titleLabel: UILabel
    @ObjCOutlet lateinit var subtitleLabel: UILabel

    override fun initWithCoder(aDecoder: NSCoder) = initBy(SessionsTableViewCell(aDecoder))

    fun setup(session: KSession) {
        titleLabel.text = session.title
        subtitleLabel.text = session.subtitle
    }
}

@ExportObjCClass
class BreakTableViewCell(aDecoder: NSCoder) : UITableViewCell(aDecoder) {
    @ObjCOutlet lateinit var titleLabel: UILabel

    override fun initWithCoder(aDecoder: NSCoder) = initBy(BreakTableViewCell(aDecoder))

    init {
        backgroundColor = UIColor.colorWithPatternImage(UIImage.imageNamed("striped_bg")!!)
    }

    fun setup(session: KSession) {
        titleLabel.text = session.title
    }
}
