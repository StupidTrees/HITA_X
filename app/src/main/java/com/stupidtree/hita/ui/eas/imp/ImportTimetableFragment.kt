package com.stupidtree.hita.ui.eas.imp

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.stupidtree.hita.R
import com.stupidtree.hita.data.model.eas.TermItem
import com.stupidtree.hita.data.model.timetable.TimePeriodInDay
import com.stupidtree.hita.databinding.FragmentEasImportBinding
import com.stupidtree.hita.databinding.FragmentEasImportListItemBinding
import com.stupidtree.hita.ui.base.BaseListAdapter
import com.stupidtree.hita.ui.base.BaseViewHolder
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
    var scheduleStructureAdapter: SListAdapter? = null


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
                    scheduleStructureAdapter?.notifyItemChangedSmooth(data)
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
        binding?.termPick?.setOnClickListener {
            val names = mutableListOf<String>()
            for (i in viewModel.startGetAllTerms()) {
                names.add(i.name)
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
            PopUpCalendarPicker().setInitValue(viewModel.startDateLiveData.value?.data?.timeInMillis)
                .setOnConfirmListener(object : PopUpCalendarPicker.OnConfirmListener {
                    override fun onConfirm(c: Calendar) {
                        viewModel.changeStartDate(c)
                    }
                }).show(requireFragmentManager(), "pick")
        }
        initList()
    }

    /**
     * 初始化课表结构列表
     */
    private fun initList() {
        scheduleStructureAdapter = SListAdapter(requireContext(), mutableListOf())
        binding?.scheduleStructure?.adapter = scheduleStructureAdapter
        binding?.scheduleStructure?.layoutManager = LinearLayoutManager(requireContext())
        binding?.refresh?.setColorSchemeColors(getColorPrimary())
        binding?.refresh?.setOnRefreshListener {
            viewModel.startRefreshTerms()
        }
        scheduleStructureAdapter?.setOnItemClickListener(object :
            BaseListAdapter.OnItemClickListener<TimePeriodInDay> {
            override fun onItemClick(data: TimePeriodInDay, card: View?, position: Int) {
                PopUpTimePeriodPicker().setInitialValue(data.from, data.to)
                    .setDialogTitle(R.string.pick_time_period)
                    .setOnDialogConformListener(object :
                        PopUpTimePeriodPicker.OnDialogConformListener {
                        override fun onClick(
                            timePeriodInDay: TimePeriodInDay
                        ) {
                            viewModel.setStructureData(timePeriodInDay,position)
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


    @SuppressLint("ParcelCreator")
    class SListAdapter(mContext: Context, mBeans: MutableList<TimePeriodInDay>) :
        BaseListAdapter<TimePeriodInDay, SListAdapter.SHolder>(
            mContext, mBeans
        ) {


        class SHolder(viewBinding: FragmentEasImportListItemBinding) :
            BaseViewHolder<FragmentEasImportListItemBinding>(viewBinding)

        override fun getViewBinding(parent: ViewGroup, viewType: Int): ViewBinding {
            return FragmentEasImportListItemBinding.inflate(mInflater, parent, false)
        }

        override fun createViewHolder(viewBinding: ViewBinding, viewType: Int): SHolder {
            return SHolder(viewBinding = viewBinding as FragmentEasImportListItemBinding)
        }

        override fun bindHolder(holder: SHolder, data: TimePeriodInDay?, position: Int) {
            if (position == mBeans.size - 1) {
                holder.binding.divider2.visibility = View.GONE
            } else {
                holder.binding.divider2.visibility = View.VISIBLE
            }
            holder.binding.item.setOnClickListener {
                data?.let { it1 -> mOnItemClickListener?.onItemClick(it1, it, position) }
            }
            holder.binding.title.text =
                mContext.getString(R.string.schedule_list_item_pattern, position + 1)
            data?.let {
                holder.binding.subtitle.text = it.toString()
            }
        }
    }


}