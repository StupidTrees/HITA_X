package com.stupidtree.hitax.ui.timetable.manager

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.stupidtree.hitax.R
import com.stupidtree.hitax.data.model.timetable.Timetable
import com.stupidtree.hitax.databinding.ActivityTimetableManagerBinding
import com.stupidtree.hitax.ui.base.BaseActivity
import com.stupidtree.hitax.ui.base.BaseListAdapter
import com.stupidtree.hitax.utils.ActivityUtils
import com.stupidtree.hitax.utils.EditModeHelper

class TimetableManagerActivity :
        BaseActivity<TimetableManagerViewModel, ActivityTimetableManagerBinding>(), EditModeHelper.EditableContainer<Timetable> {

    private lateinit var listAdapter: TimetableListAdapter
    private var editModeHelper: EditModeHelper<Timetable>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setToolbarActionBack(binding.mainToolBar)
    }

    override fun initViews() {
        listAdapter = TimetableListAdapter(this, mutableListOf())
        editModeHelper = EditModeHelper(this, listAdapter, this)
        editModeHelper?.init(this, R.id.edit_layout, R.layout.edit_mode_bar_3)
        editModeHelper?.smoothSwitch = false
        binding.list.adapter = listAdapter
        binding.list.layoutManager = GridLayoutManager(this, 2)
        listAdapter.setOnItemClickListener(object :
                BaseListAdapter.OnItemClickListener<Timetable> {
            override fun onItemClick(data: Timetable?, card: View?, position: Int) {
                if (data == null) {
                    viewModel.startNewTimetable()
                } else {
                    ActivityUtils.startTimetableDetailActivity(getThis(), data.id)
                }
            }

        })
        listAdapter.setOnItemLongClickListener(object : BaseListAdapter.OnItemLongClickListener<Timetable> {
            override fun onItemLongClick(data: Timetable?, view: View?, position: Int): Boolean {
                editModeHelper?.activateEditMode(position)
                return true
            }
        })
        bindLiveData()
    }

    private fun bindLiveData() {
        viewModel.timetablesLiveData.observe(this) {
            if(listAdapter.beans.isEmpty()){
                listAdapter.notifyDataSetChanged(it)
                binding.list.scheduleLayoutAnimation()
            }else{
                listAdapter.notifyItemChangedSmooth(it, object : BaseListAdapter.RefreshJudge<Timetable> {
                    override fun judge(oldData: Timetable, newData: Timetable): Boolean {
                        return oldData.name != newData.name
                                || oldData.startTime != newData.startTime
                                || oldData.id != newData.id
                    }

                })
            }

        }
    }


//    //当选择完Excel文件后调用此函数
//    protected override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        if (resultCode == Activity.RESULT_OK) {
//            if (requestCode == CHOOSE_FILE_CODE) {
//                val uri: Uri = data.getData()
//                val sPath1: String
//                sPath1 = FileOperator.getPath(this, uri) // Paul Burke写的函数，根据Uri获得文件路径
//                if (sPath1 == null) return
//                val file = File(sPath1)
//                loadCurriculumTask(this, file, Calendar.getInstance()).executeOnExecutor(HITAApplication.TPE)
//            }
//        }
//        super.onActivityResult(requestCode, resultCode, data)
//    }


    companion object {
        private const val CHOOSE_FILE_CODE = 0
    }

    override fun initViewBinding(): ActivityTimetableManagerBinding {
        return ActivityTimetableManagerBinding.inflate(layoutInflater)
    }

    override fun getViewModelClass(): Class<TimetableManagerViewModel> {
        return TimetableManagerViewModel::class.java
    }

    override fun onEditClosed() {

    }

    override fun onEditStarted() {

    }

    override fun onItemCheckedChanged(position: Int, checked: Boolean, currentSelected: Int) {

    }

    override fun onDelete(toDelete: Collection<Timetable>?) {
        val list = mutableListOf<Timetable>()
        if (toDelete != null) {
            for (t in toDelete) {
                list.add(t)
            }
        }
        viewModel.startDeleteTimetables(list)
        editModeHelper?.closeEditMode()
    }

}