package com.stupidtree.hita.theta.ui.topic

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stupidtree.component.data.DataState
import com.stupidtree.hita.theta.data.model.Topic
import com.stupidtree.hita.theta.databinding.FragmentSwipeListBinding
import com.stupidtree.hita.theta.ui.list.ArticleListViewModel.Companion.PAGE_SIZE
import com.stupidtree.hita.theta.utils.ActivityTools.CHOOSE_TOPIC_RESULT
import com.stupidtree.style.base.BaseFragment
import com.stupidtree.style.base.BaseListAdapter
import com.stupidtree.style.base.FragmentSearchResult

class TopicListFragment :
    BaseFragment<TopicListViewModel, FragmentSwipeListBinding>(), FragmentSearchResult {
    lateinit var listAdapter: TopicListAdapter
    lateinit var mode: String
    var extra = ""
    override fun getViewModelClass(): Class<TopicListViewModel> {
        return TopicListViewModel::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mode = arguments?.getString("mode", "following") ?: "following"
        extra = arguments?.getString("extra", "") ?: ""
    }


    override fun initViews(view: View) {
        listAdapter = TopicListAdapter(binding?.list, requireContext(), mutableListOf())
        binding?.refresh?.setColorSchemeColors(getColorPrimary())
        binding?.list?.adapter = listAdapter
        binding?.list?.layoutManager = LinearLayoutManager(requireContext())
        binding?.refresh?.setOnRefreshListener {
            viewModel.refreshAll(mode, extra)
        }
        listAdapter.setOnItemClickListener(object :
            BaseListAdapter.OnItemClickListener<Topic> {
            override fun onItemClick(data: Topic?, card: View?, position: Int) {
                data?.let {
                    val dt = Intent()
                    dt.putExtra("name", it.name)
                    dt.putExtra("id", it.id)
                    activity?.setResult(RESULT_OK, dt)
                    activity?.finish()
                }
            }
        })
        binding?.list?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (binding?.list?.canScrollVertically(1) != true && listAdapter.itemCount >= PAGE_SIZE) {
                        binding?.refresh?.isRefreshing = true
                        viewModel.loadMore(mode, extra)
                    }
                }
            }
        })
        viewModel.topicsLiveData.observe(this) {
            binding?.refresh?.isRefreshing = false
            if (it.state == DataState.STATE.SUCCESS) {
                when (it.listAction) {
                    DataState.LIST_ACTION.APPEND -> {
                        it.data?.let { it1 ->
                            listAdapter.notifyItemsAppended(it1)
                            if (it1.isNotEmpty()) {
                                binding?.list?.smoothScrollBy(0, 300)
                            }
                        }
                    }
                    else -> {
                        it.data?.let { it1 -> listAdapter.notifyItemChangedSmooth(it1) }
                    }
                }
            }
        }
    }

    var isFirst = true
    override fun onStart() {
        super.onStart()
        if (isFirst) {
            isFirst = false
            refreshAll()
        }
    }

    private fun refreshAll() {
        binding?.refresh?.isRefreshing = true
        viewModel.refreshAll(mode, extra)

    }

    override fun initViewBinding(): FragmentSwipeListBinding {
        return FragmentSwipeListBinding.inflate(layoutInflater)
    }

    companion object {
        fun newInstance(mode: String, extra: String): TopicListFragment {
            val r = TopicListFragment()
            val b = Bundle()
            b.putString("mode", mode)
            b.putString("extra", extra)
            r.arguments = b
            return r
        }
    }

    override fun setSearchText(searchText: String) {
        mode = "search"
        extra = searchText
        refreshAll()
    }
}