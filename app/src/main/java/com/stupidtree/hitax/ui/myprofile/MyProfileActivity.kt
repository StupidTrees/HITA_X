package com.stupidtree.hitax.ui.myprofile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stupidtree.hitax.R
import com.stupidtree.hitax.data.model.service.UserProfile
import com.stupidtree.hitax.ui.base.BaseActivityCompose
import com.stupidtree.hitax.ui.base.DataState
import com.stupidtree.hitax.utils.FileProviderUtils
import com.stupidtree.hitax.utils.GalleryPicker

/**
 * ”我的个人资料“ Activity
 */
class MyProfileActivity : BaseActivityCompose<MyProfileViewModel>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setWindowParams(
            statusBar = true,
            darkColor = true,
            navi = false
        )
        setContent{
            ProfileLayout(viewModel = viewModel)
        }
    }


    @Composable
    fun ProfileLayout(viewModel: MyProfileViewModel) {
        val profile: DataState<UserProfile> by viewModel.userProfileLiveData.observeAsState(
            DataState(DataState.STATE.NOT_LOGGED_IN)
        )
        profile.data?.let {
            Column(modifier = Modifier.padding(4.dp)) {
                Row {
                    Text(getString(R.string.username))
                    Text(it.username ?: "")
                }
                Row {
                    Text(getString(R.string.prompt_nickname))
                    Text(it.nickname ?: "")
                }

            }
        }

    }


    @Preview
    @Composable
    fun Test() {
        ProfileLayout(viewModel)
    }

    override fun initViews() {
        //当viewModel的UserProfile数据发生变更时，通知UI更新
        viewModel.userProfileLiveData.observe(this, { userProfileDataState ->

            if (userProfileDataState.state === DataState.STATE.SUCCESS) {
                userProfileDataState.data?.let {
                    //setUserProfile(it)
                    //profileLayout(it)
                }
            } else {
                Toast.makeText(getThis(), "加载失败", Toast.LENGTH_SHORT).show()
            }
        })
        viewModel.changeAvatarResult?.observe(this, { stringDataState ->
            if (stringDataState.state === DataState.STATE.SUCCESS) {
                Toast.makeText(getThis(), R.string.avatar_change_success, Toast.LENGTH_SHORT).show()
                viewModel.startRefresh()
            } else {
                Toast.makeText(applicationContext, R.string.fail, Toast.LENGTH_SHORT).show()
            }
        })
        viewModel.changeNicknameResult?.observe(this, { stringDataState: DataState<String?> ->
            if (stringDataState.state === DataState.STATE.SUCCESS) {
                Toast.makeText(getThis(), R.string.avatar_change_success, Toast.LENGTH_SHORT).show()
                viewModel.startRefresh()
            } else {
                Toast.makeText(applicationContext, R.string.fail, Toast.LENGTH_SHORT).show()
            }
        })
        viewModel.changeGenderResult.observe(this, { stringDataState ->
            if (stringDataState.state === DataState.STATE.SUCCESS) {
                Toast.makeText(getThis(), R.string.avatar_change_success, Toast.LENGTH_SHORT).show()
                viewModel.startRefresh()
            } else {
                Toast.makeText(applicationContext, R.string.fail, Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.changeSignatureResult?.observe(this) { stringDataState ->
            if (stringDataState.state === DataState.STATE.SUCCESS) {
                Toast.makeText(getThis(), R.string.avatar_change_success, Toast.LENGTH_SHORT).show()
                viewModel.startRefresh()
            } else {
                Toast.makeText(applicationContext, R.string.fail, Toast.LENGTH_SHORT).show()
            }
        }
//        binding.nicknameLayout.setOnClickListener {
//            val up = viewModel.userProfileLiveData.value
//            if (up != null && up.state === DataState.STATE.SUCCESS) {
//                PopUpEditText()
//                    .setTitle(R.string.set_nickname)
//                    .setText(up.data!!.nickname)
//                    .setOnConfirmListener(object : PopUpEditText.OnConfirmListener {
//                        override fun OnConfirm(text: String) {
//                            //控制viewModel发起更改昵称请求
//                            viewModel.startChangeNickname(text)
//                        }
//                    })
//                    .show(supportFragmentManager, "edit")
//            }
//        }
//        //点击更改性别，弹出选择框
//        binding.genderLayout.setOnClickListener {
//            val up = viewModel.userProfileLiveData.value
//            if (up != null && up.state === DataState.STATE.SUCCESS) {
//                PopUpSelectableList<UserLocal.GENDER>()
//                    .setTitle(R.string.choose_gender)
//                    .setInitValue(up.data!!.gender)
//                    .setListData(
//                        arrayOf(getString(R.string.male), getString(R.string.female),getString(R.string.other_gender)),
//                        listOf(UserLocal.GENDER.MALE, UserLocal.GENDER.FEMALE,UserLocal.GENDER.OTHER)
//                    ).setOnConfirmListener(object :
//                        PopUpSelectableList.OnConfirmListener<UserLocal.GENDER> {
//
//                        override fun onConfirm(title: String?, key: UserLocal.GENDER) {
//                            viewModel.startChangeGender(key)
//                        }
//                    })
//                    .show(supportFragmentManager, "select")
//            }
//        }
//
//        binding.signatureLayout.setOnClickListener {
//            val up = viewModel.userProfileLiveData.value
//            if (up != null && up.state === DataState.STATE.SUCCESS) {
//                PopUpEditText()
//                    .setTitle(R.string.choose_signature)
//                    .setText(up.data!!.signature)
//                    .setOnConfirmListener(object : PopUpEditText.OnConfirmListener {
//                        override fun OnConfirm(text: String) {
//                            //控制viewModel发起更改签名请求
//                            viewModel.startChangeSignature(text)
//                        }
//                    })
//                    .show(supportFragmentManager, "edit")
//            }
//        }
//        //点击头像那一栏，调用系统相册选择图片
//        binding.avatarLayout.setOnClickListener { GalleryPicker.choosePhoto(getThis(), false) }
//

    }

    /**
     * 根据用户资料Model，设置UI组件
     *
     * @param profile 用户资料对象
     */
    private fun setUserProfile(profile: UserProfile) {
//        //设置头像
//        ImageUtils.loadAvatarInto(getThis(), profile.id, binding.avatar)
//        //设置各种文本信息
//        binding.nickname.text = profile.nickname
//        if (!profile.signature.isNullOrEmpty()) {
//            binding.signature.text = profile.signature
//        } else {
//            binding.signature.setText(R.string.place_holder_no_signature)
//        }
//        binding.username.text = profile.username
//        binding.gender.setText(when (profile.gender){
//            UserLocal.GENDER.MALE ->R.string.male
//            UserLocal.GENDER.FEMALE->R.string.female
//            UserLocal.GENDER.OTHER->R.string.other_gender
//            else -> R.string.other_gender
//        } )
    }

    override fun onStart() {
        super.onStart()
        //Activity启动时，就通知viewModel进行刷新UI上的用户资料
        viewModel.startRefresh()
    }


    /**
     * 当用户通过系统相册选择图片返回时，将调用本函数
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        when (requestCode) {
            RC_CHOOSE_PHOTO -> {
                //选择图片返回，要跳转到图片裁剪
                if (null == data) {
                    Toast.makeText(this, R.string.no_image_selected, Toast.LENGTH_SHORT).show()
                    return
                }
                val uri = data.data
                if (null == uri) { //如果单个Uri为空，则可能是1:多个数据 2:没有数据
                    Toast.makeText(this, R.string.no_image_selected, Toast.LENGTH_SHORT).show()
                    return
                }
                // 剪裁图片
                GalleryPicker.cropPhoto(
                    getThis(),
                    FileProviderUtils.getFilePathByUri(getThis(), uri),
                    200
                )
            }
            RC_CROP_PHOTO -> {                //裁剪图片返回，此时通知viewModel请求更改头像
                val path = GalleryPicker.getCroppedCacheDir(this)
                    ?.let { FileProviderUtils.getFilePathByUri(this, it) }
                // create RequestBody instance from file
                path?.let { viewModel.startChangeAvatar(it) }
            }
        }
    }


    override fun getViewModelClass(): Class<MyProfileViewModel> {
        return MyProfileViewModel::class.java
    }


    companion object {
        /**
         * 这些是调用系统相册选择、裁剪图片要用到的状态码
         */
        const val RC_CHOOSE_PHOTO = 10
        const val RC_TAKE_PHOTO = 11
        const val RC_CROP_PHOTO = 12
    }

}