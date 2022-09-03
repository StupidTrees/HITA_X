package com.stupidtree.hitax.ui.eas

import android.app.Activity
import androidx.viewbinding.ViewBinding
import com.stupidtree.style.base.BaseActivity
import com.stupidtree.component.data.DataState
import com.stupidtree.hitax.data.model.eas.EASToken
import com.stupidtree.hitax.ui.eas.login.PopUpLoginEAS
import com.stupidtree.hitax.utils.ActivityUtils

abstract class EASActivity<T : EASViewModel, V : ViewBinding> : BaseActivity<T, V>() {

    override fun onStart() {
        super.onStart()
        refresh()
        viewModel.startLoginCheck()
    }

    abstract fun refresh()

    open fun onLoginCheckSuccess(retry: Boolean) {
        refresh()
    }

    open fun onLoginCheckFailed() {}

    override fun initViews() {
        viewModel.loginCheckResult.observe(this) {
            if (it.state == DataState.STATE.SUCCESS) {
                if (it.data == true) {
                    onLoginCheckSuccess( false)
                } else {
                    ActivityUtils.showEasVerifyWindow<Activity>(
                        getThis(),
                        lock = true,
                        onResponseListener = object :
                            PopUpLoginEAS.OnResponseListener {
                            override fun onSuccess(window: PopUpLoginEAS) {
                                window.dismiss()
                                onLoginCheckSuccess(true)
                            }

                            override fun onFailed(window: PopUpLoginEAS) {
                                onLoginCheckFailed()
                            }

                        })
                }

            }
        }
    }

}