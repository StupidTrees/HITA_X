package com.stupidtree.hita.theta.ui.list

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stupidtree.component.data.DataState
import com.stupidtree.hita.theta.databinding.FragmentArticleListBinding
import com.stupidtree.hita.theta.data.model.Article
import com.stupidtree.hita.theta.ui.DirtyArticles
import com.stupidtree.hita.theta.ui.detail.ArticleDetailActivity
import com.stupidtree.hita.theta.ui.list.ArticleListViewModel.Companion.PAGE_SIZE
import com.stupidtree.style.base.BaseFragment
import com.stupidtree.style.base.BaseListAdapter
import java.util.*

class ArticleListFragment :
    BaseFragment<ArticleListViewModel, FragmentArticleListBinding>() {
    lateinit var listAdapter: ArticleListAdapter
    lateinit var mode: String
    var uuid = UUID.randomUUID().toString()
    var extra = ""
    override fun getViewModelClass(): Class<ArticleListViewModel> {
        return ArticleListViewModel::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mode = arguments?.getString("mode", "following") ?: "following"
        DirtyArticles.register(uuid)
    }

    override fun onDestroy() {
        super.onDestroy()
        DirtyArticles.unregister(uuid)
    }

    override fun initViews(view: View) {
        listAdapter = ArticleListAdapter(uuid, binding?.list, requireContext(), mutableListOf())
        binding?.refresh?.setColorSchemeColors(getColorPrimary())
        binding?.list?.adapter = listAdapter
        binding?.list?.layoutManager = LinearLayoutManager(requireContext())
        binding?.refresh?.setOnRefreshListener {
            viewModel.getNew(mode, listAdapter.getTopTime(), extra)
        }
        listAdapter.setOnItemClickListener(object : BaseListAdapter.OnItemClickListener<Article> {
            override fun onItemClick(data: Article?, card: View?, position: Int) {
                val i = Intent(requireContext(), ArticleDetailActivity::class.java)
                i.putExtra("articleId", data?.id)
                startActivity(i)
            }
        })
        binding?.list?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (binding?.list?.canScrollVertically(1) != true && listAdapter.itemCount >= PAGE_SIZE) {
                        binding?.refresh?.isRefreshing = true
                        viewModel.loadMore(mode, listAdapter.getBottomTime(), extra)
                    }
                }
            }
        })
        viewModel.articlesLiveData.observe(this) {
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
                    DataState.LIST_ACTION.PUSH_HEAD -> {
                        it.data?.let { it1 ->
                            listAdapter.notifyItemsPushHead(it1)
                            if (listAdapter.beans.isNotEmpty()) {
                                binding?.list?.smoothScrollToPosition(0)
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

    /**
     * 搜索
     */
    fun setExtraAndRefreshAll(extra: String) {
        this.extra = extra
        refreshAll()
    }

    override fun onResume() {
        super.onResume()
        when (DirtyArticles.getAction(uuid)) {
            DataState.LIST_ACTION.PUSH_HEAD -> {
                binding?.refresh?.isRefreshing = true
                viewModel.getNew(mode, listAdapter.getTopTime(), extra)
            }
            DataState.LIST_ACTION.REPLACE_ALL -> {
                refreshAll()
            }
            else -> {

            }
        }
        for (id in DirtyArticles.getArticleIds(uuid)) {
            listAdapter.refreshItem(id)
        }
    }

    private fun refreshAll() {
        binding?.refresh?.isRefreshing = true
        viewModel.refreshAll(arguments?.getString("mode", "following") ?: "following", extra)

    }

    override fun initViewBinding(): FragmentArticleListBinding {
        return FragmentArticleListBinding.inflate(layoutInflater)
    }

    companion object {
        fun newInstance(mode: String): ArticleListFragment {
            val r = ArticleListFragment()
            val b = Bundle()
            b.putString("mode", mode)
            r.arguments = b
            return r
        }
    }
}