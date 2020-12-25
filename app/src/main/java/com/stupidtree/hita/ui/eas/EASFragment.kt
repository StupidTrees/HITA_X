package com.stupidtree.hita.ui.eas

import android.content.Context
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewbinding.ViewBinding
import com.stupidtree.hita.R
import com.stupidtree.hita.ui.base.BaseFragment

abstract class EASFragment<T : ViewModel, V : ViewBinding> : BaseFragment<T, V>() {
    var jwRoot: JWRoot? = null

    interface JWRoot {
//        val xNXQItems: List<Map<String?, String?>?>?
//        val keyToTitleMap: Map<String?, String?>?
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        jwRoot = if (context is JWRoot) {
            context
        } else {
            throw RuntimeException(
                context.toString()
                        + " must implement JWRoot"
            )
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDetach() {
        super.onDetach()
        jwRoot = null
    }

}