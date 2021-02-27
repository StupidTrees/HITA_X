package com.stupidtree.hitax.ui.eas.imp

import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.stupidtree.hitax.R
import com.stupidtree.hitax.data.model.eas.TermItem
import com.stupidtree.hitax.data.model.timetable.TimePeriodInDay
import com.stupidtree.hitax.databinding.ActivityEasImportBinding
import com.stupidtree.hitax.ui.base.BaseListAdapter
import com.stupidtree.hitax.ui.base.DataState
import com.stupidtree.hitax.ui.eas.EASActivity
import com.stupidtree.hitax.ui.widgets.PopUpCalendarPicker
import com.stupidtree.hitax.ui.widgets.PopUpCheckableList
import com.stupidtree.hitax.ui.widgets.PopUpTimePeriodPicker
import com.stupidtree.hitax.utils.ImageUtils
import com.stupidtree.hitax.utils.ImageUtils.dp2px
import com.stupidtree.hitax.utils.TextTools
import java.util.*


class ImportTimetableActivity :
    EASActivity<ImportTimetableViewModel, ActivityEasImportBinding>() {

    private lateinit var scheduleStructureAdapter: TimetableStructureListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setWindowParams(statusBar = true, darkColor = true, navi = false)
        setToolbarActionBack(binding.toolbar)
    }

    override fun initViews() {
        super.initViews()
        bindLiveData()
        initList()
        binding.toolbar.title = ""
        binding.collapse.title = ""
        binding.appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val scale = 1.0f + verticalOffset / appBarLayout.height.toFloat()
            binding.termPick.translationX =
                (binding.toolbar.contentInsetStartWithNavigation + dp2px(
                    getThis(),
                    8f
                )) * (1 - scale)
            binding.termPick.scaleX = 0.5f * (1 + scale)
            binding.termPick.scaleY = 0.5f * (1 + scale)
            binding.termPick.translationY =
                (binding.termPick.height / 2) * (1 - binding.termPick.scaleY)

            binding.buttonImport.translationY = dp2px(getThis(), 24f) * (1 - scale)
            binding.buttonImport.scaleX = 0.7f + 0.3f * scale
            binding.buttonImport.scaleY = 0.7f + 0.3f * scale
            binding.buttonImport.translationX =
                (binding.buttonImport.width / 2) * (1 - binding.buttonImport.scaleX)

        })
        binding.cardName.isEnabled = false
        binding.termPick.setOnClickListener {
            val names = mutableListOf<String>()
            for (i in viewModel.startGetAllTerms()) {
                names.add(i.name)
            }
            if (names.isEmpty()) {
                return@setOnClickListener
            }
            PopUpCheckableList<TermItem>()
                .setListData(names, viewModel.startGetAllTerms())
                .setTitle(getString(R.string.pick_import_term))
                .setOnConfirmListener(object :
                    PopUpCheckableList.OnConfirmListener<TermItem> {
                    override fun OnConfirm(title: String?, key: TermItem) {
                        viewModel.changeSelectedTerm(key)
                    }
                }).show(supportFragmentManager, "terms")
        }
        binding.buttonImport.setOnClickListener {
            if (viewModel.startImportTimetable()) {
                it.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                binding.buttonImport.startAnimation()
            }
        }
        binding.cardDate.onCardClickListener = View.OnClickListener {
            viewModel.startDateLiveData.value?.data?.let {
                PopUpCalendarPicker().setInitValue(it.timeInMillis)
                    .setOnConfirmListener(object : PopUpCalendarPicker.OnConfirmListener {
                        override fun onConfirm(c: Calendar) {
                            viewModel.changeStartDate(c)
                        }
                    }).show(supportFragmentManager, "pick")
            }

        }

    }


    private fun bindLiveData() {
        viewModel.selectedTermLiveData.observe(this) {
            it?.let {
                binding.termText.text = it.name
                binding.cardName.setTitle(it.name)
            }
        }
        viewModel.termsLiveData.observe(this) { data ->
            binding.refresh.isRefreshing = false
            if (data.state == DataState.STATE.SUCCESS) {
                if (!data.data.isNullOrEmpty()) {
                    for (t in data.data!!) {
                        if (t.isCurrent) {
                            viewModel.changeSelectedTerm(t)
                            return@observe
                        }
                    }
                    viewModel.changeSelectedTerm(data.data!![0])
                }
            } else {
                binding.termText.setText(R.string.load_failed)
            }
        }
        viewModel.startDateLiveData.observe(this) {
            if ((it.state == DataState.STATE.SUCCESS || it.state == DataState.STATE.SPECIAL) && it.data != null) {
                binding.cardDate.setTitle(
                    TextTools.getNormalDateText(
                        getThis(),
                        it.data!!
                    )
                )
            } else {
                binding.cardDate.setTitle(R.string.no_valid_date)
            }
        }
        viewModel.scheduleStructureLiveData.observe(this) {
            if (it.data.isNullOrEmpty()) {
                binding.buttonImport.background = ContextCompat.getDrawable(
                    getThis(),
                    R.drawable.element_rounded_button_bg_grey
                )
                binding.buttonImport.isEnabled = false
            } else {
                binding.buttonImport.background = ContextCompat.getDrawable(
                    getThis(),
                    R.drawable.element_rounded_button_bg_primary
                )
                binding.buttonImport.isEnabled = true
            }
            if (it.state == DataState.STATE.SUCCESS) {
                it.data?.let { data ->
                    scheduleStructureAdapter.notifyItemChangedSmooth(data)
                }
            }
        }
        viewModel.importTimetableResultLiveData.observe(this) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                binding.buttonImport.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
            } else {
                binding.buttonImport.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
            }
            val iconId: Int
            if (it.state == DataState.STATE.SUCCESS) {
                iconId = R.drawable.ic_baseline_done_24
                Toast.makeText(getThis(), R.string.import_success, Toast.LENGTH_SHORT).show()
            } else {
                iconId = R.drawable.ic_baseline_error_24
                Toast.makeText(getThis(), R.string.import_failed, Toast.LENGTH_SHORT).show()
            }
            val bitmap = ImageUtils.getResourceBitmap(getThis(), iconId)
            binding.buttonImport.doneLoadingAnimation(
                getColorPrimary(), bitmap
            )
            binding.buttonImport.postDelayed({
                binding.buttonImport.revertAnimation()
            }, 600)
        }
    }

    /**
     * 初始化课表结构列表
     */
    private fun initList() {
        scheduleStructureAdapter = TimetableStructureListAdapter(getThis(), mutableListOf())
        binding.scheduleStructure.adapter = scheduleStructureAdapter
        binding.scheduleStructure.layoutManager = LinearLayoutManager(getThis())
        binding.refresh.setColorSchemeColors(getColorPrimary())
        binding.refresh.setOnRefreshListener {
            viewModel.startRefreshTerms()
        }
        scheduleStructureAdapter.setOnItemClickListener(object :
            BaseListAdapter.OnItemClickListener<TimePeriodInDay> {
            override fun onItemClick(data: TimePeriodInDay?, card: View?, position: Int) {
                if (data == null) return
                PopUpTimePeriodPicker().setInitialValue(data.from, data.to)
                    .setDialogTitle(R.string.pick_time_period)
                    .setOnDialogConformListener(object :
                        PopUpTimePeriodPicker.OnDialogConformListener {
                        override fun onClick(
                            timePeriodInDay: TimePeriodInDay
                        ) {
                            viewModel.setStructureData(timePeriodInDay, position)
                        }

                    }).show(supportFragmentManager, "pick")
            }

        })
    }


    override fun initViewBinding(): ActivityEasImportBinding {
        return ActivityEasImportBinding.inflate(layoutInflater)
    }



    override fun refresh() {
        binding.buttonImport.background = ContextCompat.getDrawable(
            getThis(),
            R.drawable.element_rounded_button_bg_grey
        )
        binding.buttonImport.isEnabled = false
        binding.refresh.isRefreshing = true
        viewModel.startRefreshTerms()
    }

    override fun getViewModelClass(): Class<ImportTimetableViewModel> {
        return ImportTimetableViewModel::class.java
    }
}