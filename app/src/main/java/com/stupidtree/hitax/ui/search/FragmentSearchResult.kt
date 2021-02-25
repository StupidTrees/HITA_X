package com.stupidtree.hitax.ui.search

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.stupidtree.hitax.R
import com.stupidtree.hitax.ui.base.BaseFragmentClassic
import com.stupidtree.hitax.ui.base.BaseListAdapterClassic
import com.stupidtree.hitax.ui.base.DataState
import com.stupidtree.hitax.ui.search.FragmentSearchResult.SearchListAdapter.SimpleHolder

abstract class FragmentSearchResult<T, V : BaseSearchResultViewModel<T>> :
    BaseFragmentClassic<V>() {

    protected var list: RecyclerView? = null
    protected var result: TextView? = null
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private lateinit var listAdapter: SearchListAdapter
    private var searchRoot: SearchRoot? = null

    abstract fun updateHintText(reload: Boolean, addedSize: Int)
    abstract fun getHolderLayoutId(): Int
    abstract fun bindListHolder(simpleHolder: SimpleHolder?, data: T, position: Int)
    abstract fun onItemClicked(card: View?, data:T,position: Int)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = false
    }


    override fun initViews(view: View) {
        listAdapter = SearchListAdapter(requireContext(), mutableListOf())
        initList(view, listAdapter)
//        list?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//                super.onScrollStateChanged(recyclerView, newState)
//                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                    if (!list?.canScrollVertically(1)
//                        && listRes.size >= searchCore.getPageSize()
//                    ) {
//                        Search(false, false)
//                    }
//                }
//            }
//        })

        viewModel.searchResultLiveData.observe(this) {
            swipeRefreshLayout?.isRefreshing = false
            list?.visibility = View.VISIBLE
            result?.visibility = View.VISIBLE
            if (it.state == DataState.STATE.SUCCESS) {
                if (it.listAction == DataState.LIST_ACTION.REPLACE_ALL) {
                    it.data?.let { it1 -> listAdapter.notifyItemChangedSmooth(it1) }
                    list?.scheduleLayoutAnimation()
                } else {
                    it.data?.let { o ->
                        listAdapter.notifyItemRangeInserted(
                            listAdapter.mBeans.size ?: 0 - o.size, o.size
                        )
                        listAdapter.notifyItemRangeChanged(0, listAdapter.mBeans.size ?: 0)
                    }

                }
                if (listAdapter.mBeans.size == 0) result?.setText(R.string.nothing_found) else updateHintText(
                    it.listAction == DataState.LIST_ACTION.REPLACE_ALL,
                    it.data?.size ?: 0
                )
            } else {
                result?.setText(R.string.fail)
            }

        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is SearchRoot) {
            searchRoot = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        searchRoot = null
    }


    private fun initList(v: View, adapter: RecyclerView.Adapter<SimpleHolder>) {
        list = v.findViewById(R.id.list)
        list?.adapter = adapter
        list?.layoutManager = LinearLayoutManager(requireContext())
        result = v.findViewById(R.id.result)
        swipeRefreshLayout = v.findViewById(R.id.refresh)
        swipeRefreshLayout?.setOnRefreshListener { refresh() }
        swipeRefreshLayout?.setColorSchemeColors(getColorPrimary())
    }


    override fun onStart() {
        super.onStart()
        refresh()
    }

    private fun refresh() {
        searchRoot?.let {
            swipeRefreshLayout?.isRefreshing = viewModel.changeSearchText(it.getSearchText())
        }
    }

    fun setSearchText(searchText: String) {
        if (viewModelInit) {
            viewModel.changeSearchText(searchText)
        }
    }


    interface SearchRoot {
        fun getSearchText(): String
    }


    inner class SearchListAdapter(mContext: Context, beans: MutableList<T>) :
        BaseListAdapterClassic<T, SimpleHolder>(
            mContext, beans
        ) {

        inner class SimpleHolder(itemView: View, var viewType: Int) :
            RecyclerView.ViewHolder(itemView) {
            var title: TextView = itemView.findViewById(R.id.title)
            var subtitle: TextView = itemView.findViewById(R.id.subtitle)
            var picture: ImageView = itemView.findViewById(R.id.picture)
            var tag: TextView = itemView.findViewById(R.id.tag)
            var card: CardView = itemView.findViewById(R.id.card)

        }

        override fun getLayoutId(viewType: Int): Int {
            return getHolderLayoutId()
        }

        override fun createViewHolder(view: View, viewType: Int): SimpleHolder {
            return SimpleHolder(view, viewType)
        }

        override fun bindHolder(holder: SimpleHolder, data: T?, position: Int) {
            holder.card.setOnClickListener { v -> data?.let { onItemClicked(v, it,position) } }
            data?.let { bindListHolder(holder, it, position) }
        }

    }
}