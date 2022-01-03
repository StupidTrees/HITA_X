package com.stupidtree.hitax.ui.eas.score

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.stupidtree.component.data.DataState
import com.stupidtree.hitax.R
import com.stupidtree.hitax.data.model.eas.CourseScoreItem
import com.stupidtree.hitax.data.model.eas.TermItem
import com.stupidtree.hitax.data.source.web.service.EASService
import com.stupidtree.hitax.databinding.ActivityEasScoreFirstBinding
import com.stupidtree.hitax.ui.eas.EASActivity
import com.stupidtree.hitax.ui.eas.classroom.ClassroomItem
import com.stupidtree.hitax.ui.eas.classroom.EmptyClassroomListAdapter
import com.stupidtree.hitax.ui.eas.classroom.detail.EmptyClassroomDetailFragment
import com.stupidtree.style.base.BaseListAdapter
import com.stupidtree.style.widgets.PopUpCheckableList

class ScoreInquiryActivity:
    EASActivity<ScoreInquiryViewModel, ActivityEasScoreFirstBinding>(){
    lateinit var listAdapter: ScoresListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbarActionBack(binding.toolbar)
    }

    private fun bindLiveData(){
        viewModel.termsLiveData.observe(this) { data ->
            if (data.state == DataState.STATE.SUCCESS) {
                if (!data.data.isNullOrEmpty()) {
                    for (t in data.data!!) {
                        if (t.isCurrent) {
                            viewModel.selectedTermLiveData.value = t
                            return@observe
                        }
                    }
                    viewModel.selectedTermLiveData.value = data.data?.get(0)
                }
            } else {
                binding.refresh.isRefreshing = false
                binding.schoolSemesterText.setText(R.string.load_failed)
            }
        }
        viewModel.selectedTermLiveData.observe(this) {
            it?.let {
                binding.refresh.isRefreshing = true
                binding.schoolSemesterText.text = it.name
            }
        }
        viewModel.scoresLiveData.observe(this){
            binding.refresh.isRefreshing = false
            if (it.state == DataState.STATE.SUCCESS) {
                it.data?.let { it1 -> listAdapter.notifyItemChangedSmooth(it1) }
            }
        }
        viewModel.selectedTestTypeLiveData.observe(this){
            it?.let {
                binding.refresh.isRefreshing = true
                binding.testTypeText.text = when(it){
                    EASService.TestType.ALL -> getString(R.string.test_type_all)
                    EASService.TestType.NORMAL -> getString(R.string.test_type_normal)
                    EASService.TestType.RESIT -> getString(R.string.test_type_resit)
                    EASService.TestType.RETAKE -> getString(R.string.test_type_retake)
                }
            }
        }
    }
    override fun initViews() {
        super.initViews()
        bindLiveData()
        binding.refresh.setColorSchemeColors(getColorPrimary())
        binding.refresh.setOnRefreshListener{refresh()}
        listAdapter = ScoresListAdapter(this, mutableListOf())
        binding.scoreStructure.adapter = listAdapter
        binding.scoreStructure.layoutManager = LinearLayoutManager(getThis())
        binding.schoolSemesterLayout.setOnClickListener {
            val names = mutableListOf<String>()
            viewModel.termsLiveData.value?.data?.let {
                for (i in it) {
                    names.add(i.name)
                }
                if (names.isEmpty()) {
                    return@setOnClickListener
                }
                PopUpCheckableList<TermItem>()
                    .setListData(names, it)
                    .setTitle(getString(R.string.pick_quety_term))
                    .setOnConfirmListener(object :
                        PopUpCheckableList.OnConfirmListener<TermItem> {
                        override fun OnConfirm(title: String?, key: TermItem) {
                            viewModel.selectedTermLiveData.value = key
                        }
                    }).show(supportFragmentManager, "terms")
            }
        }
        binding.testTypeLayout.setOnClickListener {
            val names = mutableListOf<String>()
            names.add(getString(R.string.test_type_all))
            names.add(getString(R.string.test_type_normal))
            names.add(getString(R.string.test_type_resit))
            names.add(getString(R.string.test_type_retake))
            val list = ArrayList<EASService.TestType>()
            list.add(EASService.TestType.ALL)
            list.add(EASService.TestType.NORMAL)
            list.add(EASService.TestType.RESIT)
            list.add(EASService.TestType.RETAKE)
            PopUpCheckableList<EASService.TestType>()
                .setListData(names, list)
                .setTitle(getString(R.string.pick_test_type))
                .setOnConfirmListener(object :
                    PopUpCheckableList.OnConfirmListener<EASService.TestType> {
                    override fun OnConfirm(title: String?, key: EASService.TestType) {
                        viewModel.selectedTestTypeLiveData.value = key
                    }
                }).show(supportFragmentManager, "types")
        }
        listAdapter.setOnItemClickListener(object :
            BaseListAdapter.OnItemClickListener<CourseScoreItem> {
            override fun onItemClick(data: CourseScoreItem?, card: View?, position: Int) {
                data?.let {
                    ScoreDetailFragment(it).
                    show(supportFragmentManager, "score_detail")
                }
            }
        })
        viewModel.selectedTestTypeLiveData.value = EASService.TestType.ALL
    }
    override fun getViewModelClass(): Class<ScoreInquiryViewModel> {
        return ScoreInquiryViewModel::class.java
    }

    override fun initViewBinding(): ActivityEasScoreFirstBinding {
        return ActivityEasScoreFirstBinding.inflate(layoutInflater)
    }

    override fun refresh() {
        binding.refresh.isRefreshing = true
        viewModel.startRefresh()
    }
}