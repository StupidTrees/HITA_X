package com.stupidtree.hita.theta.ui.list

import android.content.Intent
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.AndroidViewModel
import com.stupidtree.hita.theta.R
import com.stupidtree.hita.theta.databinding.FragmentThetaNavigationBinding
import com.stupidtree.hita.theta.ui.search.ArticleSearchActivity
import com.stupidtree.style.base.BaseFragment

class ArticleNavigationFragment :
    BaseFragment<AndroidViewModel, FragmentThetaNavigationBinding>() {
    override fun getViewModelClass(): Class<AndroidViewModel> {
        return AndroidViewModel::class.java
    }

    override fun initViews(view: View) {
        childFragmentManager.beginTransaction()
            .replace(R.id.fragment, ArticleListFragment.newInstance("all")).commit()
        binding?.search?.setOnClickListener {
            val i = Intent(requireContext(), ArticleSearchActivity::class.java)
            val b =
                ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(), it, "search")
            startActivity(i, b.toBundle())
        }
    }

    override fun initViewBinding(): FragmentThetaNavigationBinding {
        return FragmentThetaNavigationBinding.inflate(layoutInflater)
    }

}