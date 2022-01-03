package com.stupidtree.hitax.ui.eas.exam

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.stupidtree.component.data.DataState
import com.stupidtree.hitax.data.model.eas.CourseScoreItem
import com.stupidtree.hitax.data.model.eas.ExamItem
import com.stupidtree.hitax.ui.eas.EASActivity
import com.stupidtree.hitax.databinding.ActivityEasExamBinding
import com.stupidtree.hitax.databinding.ActivityEasScoreFirstBinding
import com.stupidtree.hitax.ui.eas.classroom.EmptyClassroomListAdapter
import com.stupidtree.hitax.ui.eas.score.ScoreDetailFragment
import com.stupidtree.hitax.ui.eas.score.ScoreInquiryViewModel
import com.stupidtree.style.base.BaseListAdapter

class ExamActivity :
    EASActivity<ExamViewModel, ActivityEasExamBinding>() {
    lateinit var listAdapter: ExamListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbarActionBack(binding.toolbar)
    }

    private fun bindLiveData(){
        viewModel.examInfoLiveData.observe(this){
            binding.refresh.isRefreshing = false
            if (it.state == DataState.STATE.SUCCESS) {
                it.data?.let { it1 -> listAdapter.notifyItemChangedSmooth(it1) }
            }
        }
    }
    override fun refresh() {
        binding.refresh.isRefreshing = true
        viewModel.startRefresh()
    }

    override fun initViewBinding(): ActivityEasExamBinding {
        return ActivityEasExamBinding.inflate(layoutInflater)
    }

    override fun getViewModelClass(): Class<ExamViewModel> {
        return ExamViewModel::class.java
    }

    override fun initViews() {
        super.initViews()
        bindLiveData()
        binding.refresh.setColorSchemeColors(getColorPrimary())
        listAdapter = ExamListAdapter(this, mutableListOf())
        binding.refresh.setOnRefreshListener {
            refresh()
        }
        binding.examStructure.adapter = listAdapter
        binding.examStructure.layoutManager = LinearLayoutManager(getThis())
        listAdapter.setOnItemClickListener(object :
            BaseListAdapter.OnItemClickListener<ExamItem> {
            override fun onItemClick(data: ExamItem?, card: View?, position: Int) {
                data?.let {
                    ExamDetailFragment(it).
                    show(supportFragmentManager, "exam_detail")
                }
            }
        })
    }
}