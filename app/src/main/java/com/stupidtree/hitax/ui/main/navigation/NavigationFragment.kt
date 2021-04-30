package com.stupidtree.hitax.ui.main.navigation

import android.view.View
import com.stupidtree.hitax.R
import com.stupidtree.hitax.databinding.FragmentNavigationBinding
import com.stupidtree.style.base.BaseFragment
import com.stupidtree.hitax.ui.eas.classroom.EmptyClassroomActivity
import com.stupidtree.hitax.ui.eas.imp.ImportTimetableActivity
import com.stupidtree.hitax.ui.eas.login.PopUpLoginEAS
import com.stupidtree.hitax.utils.ActivityUtils
import com.stupidtree.stupiduser.data.repository.LocalUserRepository

class NavigationFragment : BaseFragment<NavigationViewModel, FragmentNavigationBinding>() {
    override fun getViewModelClass(): Class<NavigationViewModel> {
        return NavigationViewModel::class.java
    }

    override fun initViews(view: View) {
        viewModel.recentTimetableLiveData.observe(this) {
            if (it == null) {
                binding?.recentSubtitle?.setText(R.string.none)
            } else {
                binding?.recentSubtitle?.text = it.name
            }
        }
        viewModel.timetableCountLiveData.observe(this) {
            if (it == 0) {
                binding?.timetableSubtitle?.setText(R.string.no_timetable)
            } else {
                binding?.timetableSubtitle?.text = getString(R.string.timetable_count_format, it)
            }

        }
        viewModel.unreadMessageLiveData.observe(this) {
            if (it.data ?: 0 > 0) {
                binding?.messageNum?.visibility = View.VISIBLE
                binding?.messageNum?.text = it.data.toString()
            } else {
                binding?.messageNum?.visibility = View.GONE
            }
        }
        binding?.cardTimetable?.setOnClickListener {
            ActivityUtils.startTimetableManager(requireContext())
        }
        binding?.cardRecentTimetable?.setOnClickListener {
            viewModel.recentTimetableLiveData.value?.let {
                ActivityUtils.startTimetableDetailActivity(requireContext(), it.id)
            }
        }
        binding?.cardImport?.setOnClickListener {
            ActivityUtils.showEasVerifyWindow(
                requireContext(),
                directTo = ImportTimetableActivity::class.java,
                onResponseListener = object : PopUpLoginEAS.OnResponseListener {
                    override fun onSuccess(window: PopUpLoginEAS) {
                        ActivityUtils.startActivity(
                            requireContext(),
                            ImportTimetableActivity::class.java
                        )
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
                        ActivityUtils.startActivity(
                            requireContext(),
                            EmptyClassroomActivity::class.java
                        )
                        window.dismiss()
                    }

                    override fun onFailed(window: PopUpLoginEAS) {

                    }

                })
        }
        binding?.cardNews?.setOnClickListener {
            ActivityUtils.startThetaActivity(requireActivity())
        }
        binding?.cardSearch?.setOnClickListener {
            ActivityUtils.startSearchActivity(requireContext())
        }
        binding?.search?.setOnClickListener{
            binding?.search?.let{v->
                ActivityUtils.startSearchActivity(requireActivity(),v)
            }

        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.startRefresh()

        LocalUserRepository.getInstance(requireContext()).getLoggedInUser().let {
            if (it.isValid()) { //如果已登录
                //装载头像
                binding?.avatar?.let { it1 ->
                    com.stupidtree.stupiduser.util.ImageUtils.loadAvatarInto(
                        requireContext(),
                        it.avatar,
                        it1
                    )
                }
                binding?.avatar?.let { it1 ->
                    com.stupidtree.stupiduser.util.ImageUtils.loadAvatarInto(
                        requireContext(),
                        it.avatar,
                        it1
                    )
                }
                //设置各种文字
                binding?.username?.text = it.username
                binding?.nickname?.text = it.nickname
                binding?.userCard?.setOnClickListener { v ->
                    ActivityUtils.startProfileActivity(
                        requireContext(),
                        it.id
                    )
                }
            } else {
                //未登录的信息显示
                binding?.username?.setText(R.string.not_log_in)
                binding?.nickname?.setText(R.string.log_in_first)
                binding?.avatar?.setImageResource(R.drawable.place_holder_avatar)
                binding?.userCard?.setOnClickListener {
                    ActivityUtils.startWelcomeActivity(
                        requireContext()
                    )
                }
            }
        }
    }


    override fun initViewBinding(): FragmentNavigationBinding {
        return FragmentNavigationBinding.inflate(layoutInflater)
    }
}