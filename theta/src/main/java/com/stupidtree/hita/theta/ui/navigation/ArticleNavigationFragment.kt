package com.stupidtree.hita.theta.ui.navigation

import android.content.Intent
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.stupidtree.hita.theta.R
import com.stupidtree.hita.theta.databinding.FragmentThetaNavigationBinding
import com.stupidtree.hita.theta.ui.list.ArticleListFragment
import com.stupidtree.hita.theta.ui.search.ArticleSearchActivity
import com.stupidtree.style.base.BaseFragment

class ArticleNavigationFragment :
    BaseFragment<ArticleNavigationViewModel, FragmentThetaNavigationBinding>() {


    lateinit var hotListAdapter: HotTopicListAdapter
    override fun getViewModelClass(): Class<ArticleNavigationViewModel> {
        return ArticleNavigationViewModel::class.java
    }

    override fun onStart() {
        super.onStart()
        viewModel.startRefresh()
    }

    override fun initViews(view: View) {
        hotListAdapter = HotTopicListAdapter(requireContext(), mutableListOf())
        binding?.hotTopics?.adapter = hotListAdapter
        binding?.hotTopics?.layoutManager = GridLayoutManager(requireContext(), 3)
        viewModel.hotTopicsLiveData.observe(this) {
            it?.data?.let { it1 -> hotListAdapter.notifyItemChangedSmooth(it1) }
        }
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