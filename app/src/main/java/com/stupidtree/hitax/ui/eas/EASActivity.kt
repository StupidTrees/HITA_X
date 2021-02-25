package com.stupidtree.hitax.ui.eas

import androidx.viewbinding.ViewBinding
import com.stupidtree.hitax.ui.base.BaseActivity

abstract class EASActivity<T : EASViewModel, V : ViewBinding> : BaseActivity<T, V>() {

    override fun onStart() {
        super.onStart()
        viewModel.startLoginCheck()
    }

}