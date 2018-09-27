package org.jetbrains.kotlinconf.ui

import android.content.*
import android.graphics.*
import android.graphics.drawable.*
import android.os.*
import android.support.v4.widget.*
import android.support.v7.widget.*
import android.support.v7.widget.RecyclerView.*
import android.view.*
import android.widget.*
import com.brandongogetap.stickyheaders.*
import com.brandongogetap.stickyheaders.exposed.*
import kotlinx.coroutines.*
import kotlinx.coroutines.android.*
import org.jetbrains.anko.*
import org.jetbrains.anko.recyclerview.v7.*
import org.jetbrains.anko.support.v4.*
import org.jetbrains.kotlinconf.*
import org.jetbrains.kotlinconf.R
import org.jetbrains.kotlinconf.presentation.*
import kotlin.properties.Delegates.observable

abstract class SessionListFragment : BaseFragment(), AnkoComponent<Context>, SessionListView {

    private lateinit var sessionsRecyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    lateinit var sessionsAdapter: SessionsAdapter
    private var sessionsListState: Parcelable? = null

    abstract val title: String

    override var isUpdating: Boolean by observable(false) { _, _, isUpdating ->
        swipeRefreshLayout.isRefreshing = isUpdating
    }

    private val repository by lazy { (activity!!.application as KotlinConfApplication).dataRepository }
    private val navigationManager by lazy { activity as NavigationManager }
    private val searchQueryProvider by lazy { activity as SearchQueryProvider }
    private val presenter by lazy { SessionListPresenter(Dispatchers.Main, this, repository, navigationManager, searchQueryProvider) }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        swipeRefreshLayout.setOnRefreshListener(presenter::onPullRefresh)
        sessionsAdapter = SessionsAdapter(context!!, presenter::showSessionDetails)

        sessionsRecyclerView.layoutManager = StickyLayoutManager(context, sessionsAdapter).apply {
            elevateHeaders(2)
        }
        sessionsRecyclerView.adapter = sessionsAdapter

        sessionsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == SCROLL_STATE_IDLE) {
                    sessionsListState = recyclerView.layoutManager!!.onSaveInstanceState()
                }
            }
        })

        if (savedInstanceState != null) {
            sessionsListState = savedInstanceState.getParcelable(SESSION_LIST_STATE)
        }

        sessionsListState?.let {
            sessionsRecyclerView.layoutManager!!.onRestoreInstanceState(it)
        }

        presenter.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        sessionsListState?.let { outState.putParcelable(SESSION_LIST_STATE, it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return createView(AnkoContext.create(activity!!))
    }

    override fun createView(ui: AnkoContext<Context>): View = with(ui) {
        swipeRefreshLayout = swipeRefreshLayout {
            frameLayout {
                sessionsRecyclerView = recyclerView {
                    addItemDecoration(SessionDividerItemDecoration(context))
                }
            }
        }
        return swipeRefreshLayout
    }

    class SessionsAdapter(
        private val context: Context,
        private val onSessionClick: (SessionModel) -> Unit
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), StickyHeaderHandler {

        private var _data: List<*> = emptyList<Any>()
        override fun getAdapterData(): List<*> = _data

        var sessions: List<SessionModel> = emptyList()
            set(value) {
                field = value
                _data = field
                    .groupBy { it.startsAt?.toReadableDateString() ?: "" }
                    .flatMap { (day, sessions) -> listOf(HeaderItem(day)) + sessions }

                notifyDataSetChanged()
            }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when (holder) {
                is SessionViewHolder -> {
                    val session = _data[position] as SessionModel
                    with(holder) {
                        setTitle(session.title)
                        val detailStrings: List<String> =
                            session.speakers.map { it.fullName } + listOfNotNull(session.roomText)

                        setDetails(detailStrings.joinToString(", "))
                        setStartsAt(session.startsAt?.toReadableTimeString() ?: "")
                        itemView.setOnClickListener { onSessionClick(session) }

                        isFirstInTimeGroup = position == 0 ||
                                _data[position - 1] is HeaderItem ||
                                (_data[position - 1] as SessionModel).startsAt != session.startsAt
                    }
                }
                is HeaderViewHolder -> {
                    val headerItem = _data[position] as HeaderItem
                    holder.setTitle(headerItem.title)
                }
            }
        }

        private val SessionModel.roomText: String?
            get() = room?.let { context.getString(R.string.room_format_list, it) }

        override fun getItemViewType(position: Int): Int {
            return if (_data[position] is HeaderItem)
                R.id.header_item_type
            else
                R.id.session_item_type
        }

        override fun getItemCount(): Int = _data.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return when (viewType) {
                R.id.session_item_type -> SessionViewHolder.create(parent)
                R.id.header_item_type -> HeaderViewHolder.create(parent)
                else -> error("Unknown view type: $viewType")
            }
        }
    }

    data class HeaderItem(val title: String) : StickyHeader

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val titleTextView = view.find<TextView>(R.id.session_list_header_title)
        fun setTitle(title: String) {
            titleTextView.text = title
        }

        companion object HeaderViewHolderFactory : AnkoComponent<ViewGroup> {
            override fun createView(ui: AnkoContext<ViewGroup>): View = with(ui) {
                frameLayout {
                    lparams(width = matchParent, height = wrapContent)
                    backgroundColor = context.theme.getColor(android.R.attr.windowBackground)

                    textView {
                        id = R.id.session_list_header_title
                        textSize = 16f
                    }.lparams(width = matchParent, height = wrapContent) {
                        margin = dip(10)
                        topMargin = dip(6)
                        bottomMargin = dip(6)
                    }
                }
            }

            fun create(parent: ViewGroup): HeaderViewHolder {
                val ankoContext = AnkoContext.create(parent.context, parent)
                return HeaderViewHolder(createView(ankoContext))
            }
        }
    }

    class SessionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView = itemView.find<TextView>(R.id.session_title)
        private val startsAtTextView = itemView.find<TextView>(R.id.session_start)
        private val sessionDetailsTextView = itemView.find<TextView>(R.id.session_details)
        private val sessionLayoutView = itemView.find<View>(R.id.session_layout)

        var isFirstInTimeGroup: Boolean = false
            set(value) {
                field = value
                startsAtTextView.visibility = if (value) View.VISIBLE else View.INVISIBLE
            }

        val titleOffset: Int
            get() = sessionLayoutView.left

        fun setTitle(title: String) {
            titleTextView.text = title
        }

        fun setStartsAt(startEndText: String) {
            startsAtTextView.text = startEndText
        }

        fun setDetails(detailsText: String) {
            sessionDetailsTextView.text = detailsText
        }

        companion object SessionViewHolderFactory : AnkoComponent<ViewGroup> {
            override fun createView(ui: AnkoContext<ViewGroup>): View {
                return with(ui) {
                    relativeLayout {
                        isClickable = true
                        backgroundResource =
                                context.getResourceId(R.attr.selectableItemBackground)

                        textView {
                            id = R.id.session_start
                            textSize = 18f
                            textColor = theme.getColor(R.attr.colorAccent)
                        }.lparams {
                            margin = dip(10)
                        }

                        verticalLayout {
                            id = R.id.session_layout
                            textView {
                                id = R.id.session_title
                                textSize = 18f
                                textColor = Color.BLACK
                            }
                            textView {
                                id = R.id.session_details
                                textSize = 16f
                            }.lparams {
                                topMargin = dip(2)
                            }
                        }.lparams(width = matchParent, height = wrapContent) {
                            margin = dip(10)
                            leftMargin = dip(SESSION_LIST_HEADER_MARGIN)
                        }
                    }
                }
            }

            fun create(parent: ViewGroup): SessionViewHolder {
                val ankoContext = AnkoContext.create(parent.context, parent)
                return SessionViewHolder(createView(ankoContext))
            }
        }
    }

    class SessionDividerItemDecoration(context: Context) : RecyclerView.ItemDecoration() {
        private val divider: Drawable by lazy {
            val styledAttributes =
                context.obtainStyledAttributes(intArrayOf(android.R.attr.listDivider))
            val divider = styledAttributes.getDrawable(0)
            styledAttributes.recycle()
            divider
        }

        override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
            canvas.save()
            val right = parent.width - parent.paddingRight

            // No need to draw line after first header and last item
            for (i in 1 until parent.childCount - 1) {
                val child = parent.getChildAt(i)
                val nextChild = parent.getChildAt(i + 1)

                val viewHolder = parent.getChildViewHolder(child)
                val nextViewHolder = parent.getChildViewHolder(nextChild)
                if (nextViewHolder is SessionViewHolder && !nextViewHolder.isFirstInTimeGroup) {
                    continue
                }

                val left = if (nextViewHolder is HeaderViewHolder)
                    parent.leftPadding
                else
                    (viewHolder as? SessionViewHolder)?.titleOffset ?: parent.leftPadding

                val params = child.layoutParams as RecyclerView.LayoutParams
                val top = child.bottom + params.bottomMargin
                val bottom = top + divider.intrinsicHeight

                divider.setBounds(left, top, right, bottom)
                divider.draw(canvas)
            }
            canvas.restore()
        }
    }

    companion object {
        const val TAG = "SessionListFragment"
        const val SESSION_LIST_STATE = "SessionListState"
        const val SESSION_LIST_HEADER_MARGIN = 70
    }
}