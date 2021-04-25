package com.stupidtree.hita.theta.ui.list.activity

import android.os.Bundle
import com.stupidtree.hita.theta.R
import com.stupidtree.hita.theta.databinding.ActivityUserListBinding
import com.stupidtree.hita.theta.ui.list.ArticleListFragment
import com.stupidtree.hita.theta.ui.user.UserListFragment
import com.stupidtree.style.base.BaseActivity

class ArticleListActivity : BaseActivity<ArticleListViewModel, ActivityUserListBinding>() {
    override fun initViewBinding(): ActivityUserListBinding {
        return ActivityUserListBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.toolbar.title = intent.getStringExtra("title")
        setToolbarActionBack(binding.toolbar)
    }

    override fun getViewModelClass(): Class<ArticleListViewModel> {
        return ArticleListViewModel::class.java
    }

    override fun initViews() {
        val fragment =
            ArticleListFragment.newInstance(
                intent.getStringExtra("mode") ?: "",
                intent.getStringExtra("extra") ?: ""
            )
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment).commit()
    }
}