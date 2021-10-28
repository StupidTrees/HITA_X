package com.stupidtree.hita.theta.ui.me

import android.view.View
import com.stupidtree.hita.theta.R
import com.stupidtree.hita.theta.databinding.FragmentMeBinding
import com.stupidtree.hita.theta.utils.ActivityTools
import com.stupidtree.stupiduser.data.repository.LocalUserRepository
import com.stupidtree.style.base.BaseFragment

class MeFragment : BaseFragment<NavigationViewModel, FragmentMeBinding>() {
    override fun getViewModelClass(): Class<NavigationViewModel> {
        return NavigationViewModel::class.java
    }

    override fun initViews(view: View) {
        viewModel.commentNumLiveData.observe(this) {
            if (it.data ?: 0 > 0) {
                binding?.commentNum?.visibility = View.VISIBLE
                binding?.commentNum?.text = it.data.toString()
            } else {
                binding?.commentNum?.visibility = View.GONE
            }
        }
        viewModel.followNumLiveData.observe(this) {
            if (it.data ?: 0 > 0) {
                binding?.followNum?.visibility = View.VISIBLE
                binding?.followNum?.text = it.data.toString()
            } else {
                binding?.followNum?.visibility = View.GONE
            }
        }
        viewModel.repostNumLiveData.observe(this) {
            if (it.data ?: 0 > 0) {
                binding?.repostNum?.visibility = View.VISIBLE
                binding?.repostNum?.text = it.data.toString()
            } else {
                binding?.repostNum?.visibility = View.GONE
            }
        }
        viewModel.likeNumLiveData.observe(this) {
            if (it.data ?: 0 > 0) {
                binding?.likeNum?.visibility = View.VISIBLE
                binding?.likeNum?.text = it.data.toString()
            } else {
                binding?.likeNum?.visibility = View.GONE
            }
        }

        binding?.cardRepost?.setOnClickListener {
            ActivityTools.startMessagesActivity(
                requireContext(),
                getString(R.string.repost_from_me),
                "repost"
            )
        }
        binding?.cardComment?.setOnClickListener {
            ActivityTools.startMessagesActivity(
                requireContext(),
                getString(R.string.comment_me),
                "comment"
            )
        }
        binding?.cardLike?.setOnClickListener {
            ActivityTools.startMessagesActivity(
                requireContext(),
                getString(R.string.like_me),
                "like"
            )
        }
        binding?.cardFollow?.setOnClickListener {
            ActivityTools.startMessagesActivity(
                requireContext(),
                getString(R.string.follow_me),
                "follow"
            )
        }
        binding?.starredLayout?.setOnClickListener {
            val lu =  LocalUserRepository.getInstance(requireContext()).getLoggedInUser()
            ActivityTools.startArticleListActivity(
                requireContext(),
                getString(R.string.users_starred,lu.nickname),
                "star",
                lu.id
            )
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.startRefresh()

    }

    override fun onStart() {
        super.onStart()

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
                    ActivityTools.startUserActivity(
                        requireContext(),
                        it.id ?: ""
                    )
                }
            } else {
                //未登录的信息显示
                binding?.username?.setText(R.string.not_log_in)
                binding?.nickname?.setText(R.string.log_in_first)
                binding?.avatar?.setImageResource(R.drawable.place_holder_avatar)
            }
        }
    }

    override fun initViewBinding(): FragmentMeBinding {
        return FragmentMeBinding.inflate(layoutInflater)
    }
}