package com.stupidtree.hita.theta.ui.message

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stupidtree.component.data.DataState
import com.stupidtree.hita.theta.data.model.Message
import com.stupidtree.hita.theta.databinding.ActivityMessagesBinding
import com.stupidtree.hita.theta.ui.list.ArticleListViewModel.Companion.PAGE_SIZE
import com.stupidtree.style.base.BaseActivity
import com.stupidtree.style.base.BaseListAdapter

class MessagesActivity :
    BaseActivity<MessagesViewModel, ActivityMessagesBinding>() {
    lateinit var listAdapter: MessagesListAdapter
    var mode: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.toolbar.title = intent.getStringExtra("title")
        setToolbarActionBack(binding.toolbar)
        mode = intent.getStringExtra("mode") ?: ""
    }

    override fun getViewModelClass(): Class<MessagesViewModel> {
        return MessagesViewModel::class.java
    }

    override fun initViews() {
        listAdapter = MessagesListAdapter(this, mutableListOf())
        binding.refresh.setColorSchemeColors(getColorPrimary())
        binding.list.adapter = listAdapter
        binding.list.layoutManager = LinearLayoutManager(this)
        binding.refresh.setOnRefreshListener {
            viewModel.refreshAll(mode)
        }
        listAdapter.setOnItemClickListener(object :
            BaseListAdapter.OnItemClickListener<Message> {
            override fun onItemClick(data: Message?, card: View?, position: Int) {

            }
        })
        binding.list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (!binding.list.canScrollVertically(1) && listAdapter.itemCount >= PAGE_SIZE) {
                        binding.refresh.isRefreshing = true
                        viewModel.loadMore(mode)
                    }
                }
            }
        })
        viewModel.messagesLiveData.observe(this) {
            binding.refresh.isRefreshing = false
            if (it.state == DataState.STATE.SUCCESS) {
                when (it.listAction) {
                    DataState.LIST_ACTION.APPEND -> {
                        it.data?.let { it1 ->
                            listAdapter.notifyItemsAppended(it1)
                            if (it1.isNotEmpty()) {
                                binding.list.smoothScrollBy(0, 300)
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
        binding.refresh.isRefreshing = true
        viewModel.refreshAll(mode)
    }

    override fun initViewBinding(): ActivityMessagesBinding {
        return ActivityMessagesBinding.inflate(layoutInflater)
    }

}