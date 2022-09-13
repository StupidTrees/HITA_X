package com.stupidtree.hitax.ui.about

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.HapticFeedbackConstants
import android.widget.Toast
import com.stupidtree.component.data.DataState
import com.stupidtree.hitax.R
import com.stupidtree.hitax.databinding.ActivityAboutBinding
import com.stupidtree.hitax.utils.ActivityUtils
import com.stupidtree.hitax.utils.ImageUtils
import com.stupidtree.style.base.BaseActivity
import com.stupidtree.style.widgets.PopUpText

class ActivityAbout: BaseActivity<AboutViewModel,ActivityAboutBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setWindowParams(true,false,false)
        setToolbarActionBack(binding.toolbar)
    }

    override fun initViews() {
        viewModel.aboutPageLiveData.observe(this){ state ->
            state.data?.let {
                binding.aboutInfo.text = Html.fromHtml(it)
            }
        }
        binding.privacyProtocol.setOnClickListener {
             UserAgreementDialog().show(
                 supportFragmentManager,"a"
             )
        }
        binding.check.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
            binding.check.startAnimation()
            val code = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageManager.getPackageInfo(
                    packageName,
                    0
                ).longVersionCode
            } else {
                packageManager.getPackageInfo(
                    packageName,
                    0
                ).versionCode.toLong()
            }
            viewModel.checkForUpdate(code)
        }
        viewModel.checkUpdateResult.observe(this){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                binding.check.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
            } else {
                binding.check.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
            }
            val bitmap =
                ImageUtils.getResourceBitmap(getThis(), if (it.state==DataState.STATE.SUCCESS)
                    R.drawable.ic_baseline_done_24 else R.drawable.ic_baseline_error_24)
            binding.check.doneLoadingAnimation(
                getColorPrimary(), bitmap
            )
            binding.check.postDelayed({
                binding.check.revertAnimation()
            }, 600)
            if (it.state == DataState.STATE.SUCCESS) {
                it.data?.let { cr ->
                    if (cr.shouldUpdate) {
                        ActivityUtils.showUpdateNotificationForce(cr,this)
                    }else{
                        Toast.makeText(this,R.string.already_up_to_date,Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        refresh()
    }

    @SuppressLint("SetTextI18n")
    fun refresh(){
        var packageInfo: PackageInfo? = null
        try {
            packageInfo = packageManager
                .getPackageInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        //获取APP版本versionName
        //获取APP版本versionName
        var versionName: String? = null
        if (packageInfo != null) {
            versionName = packageInfo.versionName
        }
        //获取APP版本versionCode
        //获取APP版本versionCode
        binding.version.text = getString(R.string.version) + versionName
        viewModel.refresh()
    }
    override fun initViewBinding(): ActivityAboutBinding {
        return ActivityAboutBinding.inflate(layoutInflater)
    }

    override fun getViewModelClass(): Class<AboutViewModel> {
        return AboutViewModel::class.java
    }
}