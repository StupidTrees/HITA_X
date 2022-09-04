package com.stupidtree.hitax.ui.about

import android.annotation.SuppressLint
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Html
import com.stupidtree.hitax.R
import com.stupidtree.hitax.databinding.ActivityAboutBinding
import com.stupidtree.style.base.BaseActivity

class ActivityAbout: BaseActivity<AboutViewModel,ActivityAboutBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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