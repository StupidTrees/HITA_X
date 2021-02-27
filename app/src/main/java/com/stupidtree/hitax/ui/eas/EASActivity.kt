package com.stupidtree.hitax.ui.eas

import android.app.Activity
import androidx.viewbinding.ViewBinding
import com.stupidtree.hitax.ui.base.BaseActivity
import com.stupidtree.hitax.ui.base.DataState
import com.stupidtree.hitax.ui.eas.login.PopUpLoginEAS
import com.stupidtree.hitax.utils.ActivityUtils

abstract class EASActivity<T : EASViewModel, V : ViewBinding> : BaseActivity<T, V>() {

    override fun onStart() {
        super.onStart()
        refresh()
        viewModel.startLoginCheck()
    }

    abstract fun refresh()

    override fun initViews() {
        viewModel.loginCheckResult.observe(this) {
            if (it.state == DataState.STATE.SUCCESS && it.data != true) {
                ActivityUtils.showEasVerifyWindow<Activity>(getThis(),lock = true,onResponseListener = object:
                    PopUpLoginEAS.OnResponseListener {
                    override fun onSuccess(window: PopUpLoginEAS) {
                        window.dismiss()
                        refresh()
                    }

                    override fun onFailed(window: PopUpLoginEAS) {
                    }

                })
            }
        }
    }

}