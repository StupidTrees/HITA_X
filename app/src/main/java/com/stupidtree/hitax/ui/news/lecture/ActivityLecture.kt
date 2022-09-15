package com.stupidtree.hitax.ui.news.lecture

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stupidtree.component.data.DataState
import com.stupidtree.hitax.databinding.ActivityLectureBinding
import com.stupidtree.hitax.utils.ActivityUtils
import com.stupidtree.style.base.BaseActivity
import com.stupidtree.style.base.BaseListAdapter

class ActivityLecture : BaseActivity<LectureViewModel, ActivityLectureBinding>() {
    private var listAdapter: LectureListAdapter? = null

    fun refresh() {
        binding.pullrefresh.isRefreshing = true
        viewModel.refresh()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbarActionBack(binding.toolbar)
    }

    override fun getViewModelClass(): Class<LectureViewModel> {
        return LectureViewModel::class.java
    }

    override fun initViews() {
        listAdapter = LectureListAdapter(this, mutableListOf())
        binding.pullrefresh.setColorSchemeColors(getColorPrimary(), getColorPrimaryDisabled())
        binding.list.adapter = listAdapter
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(
            this, RecyclerView.VERTICAL, false
        )
        binding.list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val lm = recyclerView.layoutManager as LinearLayoutManager?
                val totalItemCount = recyclerView.adapter!!.itemCount
                val lastVisibleItemPosition = lm!!.findLastVisibleItemPosition()
                val visibleItemCount = recyclerView.childCount
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItemPosition == totalItemCount - 1 && visibleItemCount > 0) {
                    binding.pullrefresh.isRefreshing = true
                    viewModel.loadMore()
                }
            }
        })
        binding.list.layoutManager = layoutManager
        binding.pullrefresh.setOnRefreshListener {
            refresh()
        }
        listAdapter?.setOnItemClickListener(object :BaseListAdapter.OnItemClickListener<Map<String,String>>{
            override fun onItemClick(data: Map<String, String>?, card: View?, position: Int) {

                //ActivityOptionsCompat op = ActivityOptionsCompat.makeSceneTransitionAnimation(FragmentNewsLecture.this.getActivity(),v,"cardview");
                ActivityUtils.startNewsActivity(
                    this@ActivityLecture,
                    data?.get("link")?:"",
                    data?.get("title")?:""
                )

            }

        })
        viewModel.listData.observe(this) {
            binding.pullrefresh.isRefreshing = false
            it.data?.let { data ->
                if (it.listAction == DataState.LIST_ACTION.APPEND) {
                    listAdapter?.notifyItemsAppended(data)
                    if (data.isNotEmpty()) {
                        binding.list.smoothScrollBy(0, 300)
                    }
                } else {
                    listAdapter?.notifyItemChangedSmooth(data)
                }
            }
        }
    }

    var first = true
    override fun onStart() {
        super.onStart()
        if(first){
            refresh()
            first = false
        }
    }
    override fun initViewBinding(): ActivityLectureBinding {
        return ActivityLectureBinding.inflate(layoutInflater)
    }


}