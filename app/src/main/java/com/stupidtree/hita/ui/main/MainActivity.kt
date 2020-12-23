package com.stupidtree.hita.ui.main

import androidx.lifecycle.AndroidViewModel
import com.stupidtree.hita.R
import com.stupidtree.hita.databinding.ActivityMainBinding
import com.stupidtree.hita.ui.base.BaseActivity
import com.stupidtree.hita.ui.widgets.PopUpSelectableList
import com.stupidtree.hita.utils.ActivityUtils
import java.sql.Array
import java.util.*

class MainActivity : BaseActivity<MainViewModel,ActivityMainBinding>() {

    override fun initViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun getViewModelClass(): Class<MainViewModel> {
        return MainViewModel::class.java
    }


    override fun initViews() {
        binding.button.setOnClickListener {
            ActivityUtils.startLoginEASActivity(getThis())
        }
    }
}