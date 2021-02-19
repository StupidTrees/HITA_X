package com.stupidtree.hita.ui.eas.imp

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
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
import com.stupidtree.hita.ui.widgets.PopUpCheckableList
import com.stupidtree.hita.utils.TextTools
import java.util.*


class ImportTimetableFragment :
    EASFragment<ImportTimetableViewModel, FragmentEasImportBinding>() {
    var scheduleStructureAdapter: SListAdapter? = null


    override fun initViews(view: View) {
        viewModel.selectedTermLiveData.observe(this) {
            it?.let {
                binding?.termText?.text = it.name
                // binding?.pickXnxq?.setValue(it.name, it.yearCode + it.termCode)
            }
        }
        viewModel.termsLiveData.observe(this) { data ->
            if (data.state == DataState.STATE.SUCCESS && data.data != null) {
                binding?.let {
                    if (!data.data.isNullOrEmpty()) {
                        viewModel.changeSelectedTerm(data.data!![0])
                    }
                }
            }
        }
        viewModel.startDateLiveData.observe(this) {
            if ((it.state == DataState.STATE.SUCCESS || it.state == DataState.STATE.SPECIAL)&& it.data != null) {
                binding?.startDate?.setValue(
                    TextTools
                        .getNormalDateText(requireContext(), it.data!!),
                    it.data!!.timeInMillis.toString()
                )
                binding?.calendarView?.date = it.data!!.timeInMillis
            } else {
                binding?.startDate?.setValue(
                    getString(R.string.no_valid_date), ""
                )
            }
        }
        viewModel.scheduleStructureLiveData.observe(this) {
            if (it.state == DataState.STATE.SUCCESS) {
                it.data?.let { data ->
                    scheduleStructureAdapter?.notifyItemChangedSmooth(data)
                }
            }
        }
        viewModel.importTimetableResultLiveData.observe(this) {
            Log.e("it", it.toString())
            val iconId: Int
            if (it.state == DataState.STATE.SUCCESS) {
                iconId = R.drawable.ic_baseline_done_24
                Toast.makeText(requireContext(), R.string.import_success, Toast.LENGTH_SHORT).show()
            } else {
                iconId = R.drawable.ic_baseline_error_24
                Toast.makeText(requireContext(), R.string.import_failed, Toast.LENGTH_SHORT).show()
            }
            val vectorDrawable = ContextCompat.getDrawable(requireContext(), iconId)
            val bitmap = Bitmap.createBitmap(
                vectorDrawable!!.intrinsicWidth,
                vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
            vectorDrawable.draw(canvas)
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
//                        binding?.pickXnxq?.setValue(
//                            key.name, key.yearCode + key.termCode
//                        )
                        viewModel.changeSelectedTerm(key)
                    }

                }).show(requireFragmentManager(), "terms")


        }
        binding?.buttonImport?.setOnClickListener {
            if (viewModel.startImportTimetable()) {
                binding?.buttonImport?.startAnimation()
            }
        }
        binding?.calendarView?.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val c = Calendar.getInstance()
            c.set(year,month,dayOfMonth)
            viewModel.changeStartDate(c)
        }
        initList()
    }

    /**
     * 初始化课表结构列表
     */
    private fun initList() {
        scheduleStructureAdapter = SListAdapter(requireContext(), mutableListOf())
        binding?.scheduleStructure?.adapter = scheduleStructureAdapter
        binding?.scheduleStructure?.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
    }


    override fun initViewBinding(): FragmentEasImportBinding {
        return FragmentEasImportBinding.inflate(layoutInflater)
    }

    override fun onResume() {
        super.onResume()
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
            holder.binding.title.text = mContext.getString(R.string.schedule_list_item_pattern,position + 1)
            data?.let {
                holder.binding.subtitle.text = it.toString()
            }
        }
    }
}