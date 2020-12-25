package com.stupidtree.hita.ui.eas.imp

import android.util.Log
import android.view.View
import com.stupidtree.hita.R
import com.stupidtree.hita.data.model.eas.TermItem
import com.stupidtree.hita.databinding.FragmentEasImportBinding
import com.stupidtree.hita.ui.base.DataState
import com.stupidtree.hita.ui.eas.EASFragment
import com.stupidtree.hita.ui.widgets.PopUpCheckableList
import com.stupidtree.hita.ui.widgets.RoundedBarView

class ImportTimetableFragment :
    EASFragment<ImportTimetableViewModel, FragmentEasImportBinding>() {
    override fun initViews(view: View) {
        viewModel.selectedTermLiveData.observe(this) {
            it?.let {
                binding?.pickXnxq?.setValue(it.name, it.yearCode + it.termCode)
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
            if (it.state == DataState.STATE.SUCCESS && it.data != null) {
                binding?.startDate?.setValue(
                    com.stupidtree.hita.utils.TextUtils
                        .getNormalDateText(requireContext(), it.data!!),
                    it.data!!.timeInMillis.toString()
                )
            }
        }
        binding?.pickXnxq?.onClickListener = object : RoundedBarView.OnClickListener {
            override fun onClick(key: String) {
                val names = mutableListOf<String>()
                for (i in viewModel.getAllTerms()) {
                    names.add(i.name)
                }
                PopUpCheckableList<TermItem>()
                    .setListData(names, viewModel.getAllTerms())
                    .setTitle(getString(R.string.pick_import_term))
                    .setOnConfirmListener(object :
                        PopUpCheckableList.OnConfirmListener<TermItem> {
                        override fun OnConfirm(title: String?, key: TermItem) {
                            binding?.pickXnxq?.setValue(
                                key.name, key.yearCode + key.termCode
                            )
                            viewModel.changeSelectedTerm(key)
                        }

                    }).show(requireFragmentManager(), "terms")
            }


        }
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
}