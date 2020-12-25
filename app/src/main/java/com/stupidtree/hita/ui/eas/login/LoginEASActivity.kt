package com.stupidtree.hita.ui.eas.login

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.google.android.material.snackbar.Snackbar
import com.stupidtree.hita.R
import com.stupidtree.hita.databinding.ActivityLoginEasBinding
import com.stupidtree.hita.ui.base.BaseActivity
import com.stupidtree.hita.ui.base.DataState
import com.stupidtree.hita.utils.ActivityUtils
import java.util.*

class LoginEASActivity : BaseActivity<LoginEASViewModel, ActivityLoginEasBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setWindowParams(statusBar = true, darkColor = true, navi = false)
        setToolbarActionBack(binding.toolbar)
    }

    override fun initViews() {
        viewModel.loginResultLiveData.observe(this){
            if(it.state==DataState.STATE.SUCCESS){
                Toast.makeText(getThis(),"登录成功",Toast.LENGTH_SHORT).show()
                ActivityUtils.startEASActivity(getThis())
            }else{
                Toast.makeText(getThis(),"登录失败",Toast.LENGTH_SHORT).show()
            }
        }
        binding.login.setOnClickListener {
            if(!binding.username.text.isNullOrEmpty()&&!binding.password.text.isNullOrEmpty()){
                viewModel.startLogin(binding.username.text.toString(),binding.password.text.toString())
            }
        }
    }
    override fun initViewBinding(): ActivityLoginEasBinding {
        return ActivityLoginEasBinding.inflate(layoutInflater)
    }

    override fun getViewModelClass(): Class<LoginEASViewModel> {
        return LoginEASViewModel::class.java
    }
}