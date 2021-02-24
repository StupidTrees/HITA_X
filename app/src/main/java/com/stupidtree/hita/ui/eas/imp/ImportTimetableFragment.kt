package com.stupidtree.hita.ui.eas.imp

import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.stupidtree.hita.R
import com.stupidtree.hita.data.model.eas.TermItem
import com.stupidtree.hita.data.model.timetable.TimePeriodInDay
import com.stupidtree.hita.databinding.FragmentEasImportBinding
import com.stupidtree.hita.ui.base.BaseListAdapter
import com.stupidtree.hita.ui.base.DataState
import com.stupidtree.hita.ui.eas.EASFragment
import com.stupidtree.hita.ui.widgets.PopUpCalendarPicker
import com.stupidtree.hita.ui.widgets.PopUpCheckableList
import com.stupidtree.hita.ui.widgets.PopUpTimePeriodPicker
import com.stupidtree.hita.utils.ImageUtils
import com.stupidtree.hita.utils.TextTools
import java.util.*


class ImportTimetableFragment :
    EASFragment<ImportTimetableViewModel, FragmentEasImportBinding>() {
    private lateinit var scheduleStructureAdapter: TimetableStructureListAdapter


    override fun initViews(view: View) {
        viewModel.selectedTermLiveData.observe(this) {
            it?.let {
                binding?.termText?.text = it.name
                binding?.cardName?.setTitle(it.name)
            }
        }
        viewModel.termsLiveData.observe(this) { data ->
            binding?.refresh?.isRefreshing = false
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
                binding?.termText?.setText(R.string.load_failed)
            }
        }
        viewModel.startDateLiveData.observe(this) {
            if ((it.state == DataState.STATE.SUCCESS || it.state == DataState.STATE.SPECIAL) && it.data != null) {
                binding?.cardDate?.setTitle(
                    TextTools.getNormalDateText(
                        requireContext(),
                        it.data!!
                    )
                )
            } else {
                binding?.cardDate?.setTitle(R.string.no_valid_date)
            }
        }
        viewModel.scheduleStructureLiveData.observe(this) {
            if (it.data.isNullOrEmpty()) {
                binding?.buttonImport?.background = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.element_rounded_button_bg_grey
                )
                binding?.buttonImport?.isEnabled = false
            } else {
                binding?.buttonImport?.background = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.element_rounded_button_bg_primary
                )
                binding?.buttonImport?.isEnabled = true
            }
            if (it.state == DataState.STATE.SUCCESS) {
                it.data?.let { data ->
                    scheduleStructureAdapter.notifyItemChangedSmooth(data)
                }
            }
        }
        viewModel.importTimetableResultLiveData.observe(this) {
            val iconId: Int
            if (it.state == DataState.STATE.SUCCESS) {
                iconId = R.drawable.ic_baseline_done_24
                Toast.makeText(requireContext(), R.string.import_success, Toast.LENGTH_SHORT).show()
            } else {
                iconId = R.drawable.ic_baseline_error_24
                Toast.makeText(requireContext(), R.string.import_failed, Toast.LENGTH_SHORT).show()
            }
            val bitmap = ImageUtils.getResourceBitmap(requireContext(), iconId)
            binding?.buttonImport?.doneLoadingAnimation(
                getColorPrimary(), bitmap
            )
            binding?.buttonImport?.postDelayed({
                binding?.buttonImport?.revertAnimation()
            }, 600)
        }
        binding?.cardName?.isEnabled = false
        binding?.termPick?.setOnClickListener {
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
                }).show(requireFragmentManager(), "terms")
        }
        binding?.buttonImport?.setOnClickListener {
            if (viewModel.startImportTimetable()) {
                binding?.buttonImport?.startAnimation()
            }
        }
        binding?.cardDate?.onCardClickListener = View.OnClickListener {
            viewModel.startDateLiveData.value?.data?.let {
                PopUpCalendarPicker().setInitValue(it.timeInMillis)
                    .setOnConfirmListener(object : PopUpCalendarPicker.OnConfirmListener {
                        override fun onConfirm(c: Calendar) {
                            viewModel.changeStartDate(c)
                        }
                    }).show(requireFragmentManager(), "pick")
            }

        }
        initList()
    }

    /**
     * 初始化课表结构列表
     */
    private fun initList() {
        scheduleStructureAdapter = TimetableStructureListAdapter(requireContext(), mutableListOf())
        binding?.scheduleStructure?.adapter = scheduleStructureAdapter
        binding?.scheduleStructure?.layoutManager = LinearLayoutManager(requireContext())
        binding?.refresh?.setColorSchemeColors(getColorPrimary())
        binding?.refresh?.setOnRefreshListener {
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

                    }).show(requireFragmentManager(), "pick")
            }

        })
    }


    override fun initViewBinding(): FragmentEasImportBinding {
        return FragmentEasImportBinding.inflate(layoutInflater)
    }

    override fun onStart() {
        super.onStart()
        refresh()
    }

    override fun refresh() {
        binding?.buttonImport?.background = ContextCompat.getDrawable(
            requireContext(),
            R.drawable.element_rounded_button_bg_grey
        )
        binding?.buttonImport?.isEnabled = false
        binding?.refresh?.isRefreshing = true
        viewModel.startRefreshTerms()
    }

    companion object {
        fun newInstance(): ImportTimetableFragment {
            return ImportTimetableFragment()
        }
    }

    override fun getViewModelClass(): Class<ImportTimetableViewModel> {
        return ImportTimetableViewModel::class.java
    }


}