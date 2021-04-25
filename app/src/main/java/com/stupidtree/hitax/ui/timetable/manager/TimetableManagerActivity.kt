package com.stupidtree.hitax.ui.timetable.manager

import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.stupidtree.component.data.DataState
import com.stupidtree.hitax.R
import com.stupidtree.hitax.data.model.timetable.Timetable
import com.stupidtree.hitax.databinding.ActivityTimetableManagerBinding
import com.stupidtree.style.base.BaseActivity
import com.stupidtree.style.base.BaseListAdapter
import com.stupidtree.hitax.utils.ActivityUtils
import com.stupidtree.hitax.utils.EditModeHelper
import com.stupidtree.hitax.utils.ImageUtils
import com.stupidtree.sync.StupidSync
import java.lang.Exception

class TimetableManagerActivity :
    BaseActivity<TimetableManagerViewModel, ActivityTimetableManagerBinding>(),
    EditModeHelper.EditableContainer<Timetable> {

    private lateinit var listAdapter: TimetableListAdapter
    private var editModeHelper: EditModeHelper<Timetable>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbarActionBack(binding.toolbar)
    }

    override fun initViews() {
        binding.toolbar.title = ""
        binding.collapse.title = ""
        binding.appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val scale = 1.0f + verticalOffset / appBarLayout.height.toFloat()
            binding.title.translationX =
                (binding.toolbar.contentInsetStartWithNavigation + ImageUtils.dp2px(
                    getThis(),
                    8f
                )) * (1 - scale)
            binding.title.scaleX = 0.5f * (1 + scale)
            binding.title.scaleY = 0.5f * (1 + scale)
            binding.title.translationY =
                (binding.title.height / 2) * (1 - binding.title.scaleY)

            binding.buttonSync.translationY = ImageUtils.dp2px(getThis(), 24f) * (1 - scale)
            binding.buttonSync.scaleX = 0.7f + 0.3f * scale
            binding.buttonSync.scaleY = 0.7f + 0.3f * scale
            binding.buttonSync.translationX =
                (binding.buttonSync.width / 2) * (1 - binding.buttonSync.scaleX)
        })
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
        listAdapter.setOnItemLongClickListener(object :
            BaseListAdapter.OnItemLongClickListener<Timetable> {
            override fun onItemLongClick(data: Timetable?, view: View?, position: Int): Boolean {
                editModeHelper?.activateEditMode(position)
                return true
            }
        })
        binding.buttonSync.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
            binding.buttonSync.startAnimation()
            StupidSync.sync(object : StupidSync.SyncCallback {
                override fun onSuccess() {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                        binding.buttonSync.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                    } else {
                        binding.buttonSync.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                    }
                    val bitmap =
                        ImageUtils.getResourceBitmap(getThis(), R.drawable.ic_baseline_done_24)
                    binding.buttonSync.doneLoadingAnimation(
                        getColorPrimary(), bitmap
                    )
                    binding.buttonSync.postDelayed({
                        binding.buttonSync.revertAnimation()
                    }, 600)
                    Toast.makeText(getThis(), R.string.sync_success, Toast.LENGTH_SHORT).show()
                }

                override fun onFailed(e: Exception) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                        binding.buttonSync.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                    } else {
                        binding.buttonSync.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                    }
                    val bitmap =
                        ImageUtils.getResourceBitmap(getThis(), R.drawable.ic_baseline_error_24)
                    binding.buttonSync.doneLoadingAnimation(
                        getColorPrimary(), bitmap
                    )
                    binding.buttonSync.postDelayed({
                        binding.buttonSync.revertAnimation()
                    }, 600)
                    Toast.makeText(getThis(), R.string.sync_error, Toast.LENGTH_SHORT).show()
                }

            })
        }
        bindLiveData()
    }

    private fun bindLiveData() {
        viewModel.timetablesLiveData.observe(this) {
            if (listAdapter.beans.isEmpty()) {
                listAdapter.notifyDatasetChanged(it)
                binding.list.scheduleLayoutAnimation()
            } else {
                listAdapter.notifyItemChangedSmooth(
                    it,
                    object : BaseListAdapter.RefreshJudge<Timetable> {
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