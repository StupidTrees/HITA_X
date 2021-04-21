package com.stupidtree.hitax.ui.main.navigation

import android.view.View
import com.stupidtree.hitax.R
import com.stupidtree.hitax.databinding.FragmentNavigationBinding
import com.stupidtree.style.base.BaseFragment
import com.stupidtree.hitax.ui.eas.classroom.EmptyClassroomActivity
import com.stupidtree.hitax.ui.eas.imp.ImportTimetableActivity
import com.stupidtree.hitax.ui.eas.login.PopUpLoginEAS
import com.stupidtree.hitax.utils.ActivityUtils

class NavigationFragment : BaseFragment<NavigationViewModel, FragmentNavigationBinding>() {
    override fun getViewModelClass(): Class<NavigationViewModel> {
        return NavigationViewModel::class.java
    }

    override fun initViews(view: View) {
        viewModel.recentTimetableLiveData.observe(this) {
            if (it == null) {
                binding?.cardRecentTimetable?.setSubtitle(R.string.none)
            } else {
                binding?.cardRecentTimetable?.setSubtitle(it.name)
            }
        }
        viewModel.timetableCountLiveData.observe(this){
            if(it==0){
                binding?.cardTimetable?.setSubtitle(R.string.no_timetable)
            }else{
                binding?.cardTimetable?.setSubtitle(getString(R.string.timetable_count_format,it))
            }

        }
        binding?.cardTimetable?.onCardClickListener = View.OnClickListener {
            ActivityUtils.startTimetableManager(requireContext())
        }
        binding?.cardRecentTimetable?.onCardClickListener = View.OnClickListener {
            viewModel.recentTimetableLiveData.value?.let {
                ActivityUtils.startTimetableDetailActivity(requireContext(), it.id)
            }
        }
        binding?.cardImport?.setOnClickListener{
            ActivityUtils.showEasVerifyWindow(
                requireContext(),
                directTo = ImportTimetableActivity::class.java,
                onResponseListener = object : PopUpLoginEAS.OnResponseListener {
                    override fun onSuccess(window: PopUpLoginEAS) {
                        ActivityUtils.startActivity(requireContext(),ImportTimetableActivity::class.java)
                        window.dismiss()
                    }

                    override fun onFailed(window: PopUpLoginEAS) {

                    }

                })
        }
        binding?.cardEmptyClassroom?.setOnClickListener {
            ActivityUtils.showEasVerifyWindow(
                requireContext(),
                directTo = EmptyClassroomActivity::class.java,
                onResponseListener = object : PopUpLoginEAS.OnResponseListener {
                    override fun onSuccess(window: PopUpLoginEAS) {
                        ActivityUtils.startActivity(requireContext(),EmptyClassroomActivity::class.java)
                        window.dismiss()
                    }

                    override fun onFailed(window: PopUpLoginEAS) {

                    }

                })
        }
        binding?.cardNews?.setOnClickListener {
            ActivityUtils.startThetaActivity(requireActivity())
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.startRefresh()
    }

    override fun initViewBinding(): FragmentNavigationBinding {
        return FragmentNavigationBinding.inflate(layoutInflater)
    }
}