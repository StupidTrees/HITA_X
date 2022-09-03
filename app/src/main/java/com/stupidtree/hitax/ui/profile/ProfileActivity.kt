package com.stupidtree.hitax.ui.profile

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.material.appbar.AppBarLayout
import com.stupidtree.component.data.DataState
import com.stupidtree.hita.theta.utils.ActivityTools
import com.stupidtree.hitax.R
import com.stupidtree.hitax.data.repository.TimetableRepository
import com.stupidtree.hitax.databinding.ActivityProfileBinding
import com.stupidtree.hitax.utils.ActivityUtils
import com.stupidtree.stupiduser.data.model.UserLocal
import com.stupidtree.stupiduser.data.model.UserProfile
import com.stupidtree.stupiduser.data.repository.LocalUserRepository
import com.stupidtree.stupiduser.util.ImageUtils
import com.stupidtree.style.base.BaseActivity
import com.stupidtree.style.widgets.PopUpText
import com.stupidtree.sync.StupidSync

/**
 * 其他用户（好友、搜索结果等）的资料页面Activity
 */
class ProfileActivity : BaseActivity<ProfileViewModel, ActivityProfileBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbarActionBack(binding.toolbar)
    }


    override fun getViewModelClass(): Class<ProfileViewModel> {
        return ProfileViewModel::class.java
    }

    override fun initViews() {
        setUpLiveData()
        val initElevation = binding.avatarCard.cardElevation
        binding.fab.setBackgroundColor(getColorPrimary())
        binding.appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            val percentage = -verticalOffset.toFloat() / binding.appbar.totalScrollRange.toFloat()
            binding.avatarCard.pivotY = binding.avatarCard.height.toFloat() * 0.5f
            binding.avatarCard.pivotX = binding.avatarCard.width.toFloat() * 0.3f
            binding.avatarCard.scaleX = 1 - percentage
            binding.avatarCard.scaleY = 1 - percentage
            binding.textNickname.alpha = 1 - 2.5f * percentage
            binding.textSignature.alpha = 1 - 2.5f * percentage
            binding.iconGender.alpha = 1 - 2.5f * percentage
            binding.avatarCard.cardElevation = initElevation * (1 - percentage)
        })
        binding.refresh.setColorSchemeColors(getColorPrimary())
        binding.refresh.setOnRefreshListener {
            startRefresh()
        }
        binding.logout.setOnClickListener {
            PopUpText().setTitle(R.string.logout_hint).setOnConfirmListener(
                object : PopUpText.OnConfirmListener {
                    override fun OnConfirm() {
                        viewModel.logout(getThis())
                        TimetableRepository.getInstance(application).actionClearData()
                        StupidSync.clearData()
                        finish()
                    }

                }
            ).show(supportFragmentManager, "logout")
        }
        binding.followingLayout.setOnClickListener {
            viewModel.userProfileLiveData.value?.data?.let {
                ActivityTools.startUserListActivity(
                    getThis(),
                    getString(R.string.users_following, it.nickname),
                    "following",
                    it.id
                )
            }
        }
        binding.fansLayout.setOnClickListener {
            viewModel.userProfileLiveData.value?.data?.let {
                ActivityTools.startUserListActivity(
                    getThis(),
                    getString(R.string.users_fans, it.nickname),
                    "fans",
                    it.id
                )
            }
        }
        binding.postsLayout.setOnClickListener {
            viewModel.userProfileLiveData.value?.data?.let {
                ActivityTools.startArticleListActivity(
                    getThis(),
                    getString(R.string.users_posts, it.nickname),
                    "user",
                    it.id
                )
            }
        }

        binding.fab.backgroundTintList = ColorStateList.valueOf(getColorPrimary())
    }

    private fun setUpLiveData() {
        //为ViewModel中的各种数据设置监听
        viewModel.userProfileLiveData.observe(this) { userProfileDataState ->
            binding.refresh.isRefreshing = false
            if (userProfileDataState?.state === DataState.STATE.SUCCESS) {
                //状态为成功，设置ui
                setProfileView(userProfileDataState.data)
                if (userProfileDataState.data?.id == LocalUserRepository.getInstance(application)
                        .getLoggedInUser().id
                ) {
                    binding.logout.visibility = View.VISIBLE
                    binding.fab.setText(R.string.edit_my_profile)
                    binding.fab.isEnabled = true
                    binding.fab.setIconResource(R.drawable.ic_baseline_edit_24)
                    binding.fab.setOnClickListener { ActivityUtils.startMyProfileActivity(getThis()) }
                } else {
                    binding.logout.visibility = View.GONE
                    binding.fab.isEnabled = true
                    binding.fab.setOnClickListener {
                        viewModel.startFollow(userProfileDataState.data?.followed != true)
                    }
                    if (userProfileDataState.data?.followed == true) {
                        binding.fab.setText(R.string.unfollow)
                        binding.fab.setIconResource(R.drawable.ic_unfollow)
                    } else {
                        binding.fab.setText(R.string.follow)
                        binding.fab.setIconResource(R.drawable.ic_baseline_person_add_24)
                    }

                }
            } else {
                //状态为失败，弹出错误
                Toast.makeText(getThis(), "获取出错", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.followResult.observe(this) {
            if (it.state == DataState.STATE.SUCCESS) {
                startRefresh()
            } else {
                Toast.makeText(getThis(), R.string.fail, Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onStart() {
        super.onStart()
        startRefresh()
    }

    private fun startRefresh() {
        val id = intent.getStringExtra("id")
        if (id != null) {
            viewModel.startRefresh(id)
            binding.refresh.isRefreshing = true
        }
    }

    /**
     * 根据用户资料Model设置UI
     *
     * @param userInfo 用户资料对象
     */
    private fun setProfileView(userInfo: UserProfile?) {
        if (userInfo != null) {
            ImageUtils.loadAvatarInto(getThis(), userInfo.avatar, binding.avatar)
            binding.textUsername.text = userInfo.username
            binding.textNickname.text = userInfo.nickname
            binding.iconGender.visibility = View.VISIBLE
            binding.fans.text = userInfo.fansNum.toString()
            binding.following.text = userInfo.followingNum.toString()
            if (userInfo.signature.isNullOrEmpty()) {
                binding.textSignature.setText(R.string.place_holder_no_signature)
            } else {
                binding.textSignature.text = userInfo.signature
            }
            if (userInfo.gender == UserLocal.GENDER.MALE) {
                binding.iconGender.setImageResource(R.drawable.ic_male_blue_24)
                binding.iconGender.contentDescription = getString(R.string.male)
            } else {
                binding.iconGender.setImageResource(R.drawable.ic_female_pink_24)
                binding.iconGender.contentDescription = getString(R.string.female)
            }
        }
    }


    override fun initViewBinding(): ActivityProfileBinding {
        return ActivityProfileBinding.inflate(layoutInflater)
    }
}