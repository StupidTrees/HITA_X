package com.stupidtree.hita.theta.ui.topic.detail

import android.os.Bundle
import android.util.Log
import com.google.android.material.appbar.AppBarLayout
import com.stupidtree.component.data.DataState
import com.stupidtree.hita.theta.R
import com.stupidtree.hita.theta.databinding.ActivityTopicDetailBinding
import com.stupidtree.hita.theta.ui.list.ArticleListFragment
import com.stupidtree.hita.theta.utils.ImageUtils
import com.stupidtree.stupiduser.util.ImageUtils.dp2px
import com.stupidtree.style.base.BaseActivity

class TopicDetailActivity : BaseActivity<TopicDetailViewModel, ActivityTopicDetailBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.toolbar.title = ""
        binding.collapse.title = ""
        setToolbarActionBack(binding.toolbar)
    }

    override fun initViewBinding(): ActivityTopicDetailBinding {
        return ActivityTopicDetailBinding.inflate(layoutInflater)
    }

    override fun getViewModelClass(): Class<TopicDetailViewModel> {
        return TopicDetailViewModel::class.java
    }

    override fun onStart() {
        super.onStart()
        viewModel.startRefresh(intent.getStringExtra("topicId") ?: "")
    }


    override fun initViews() {

        val fragment =
            ArticleListFragment.newInstance("topic", intent.getStringExtra("topicId") ?: "")
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment).commit()

        binding.appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val scale = 1.0f + verticalOffset / appBarLayout.height.toFloat()
            binding.authorLayout.translationX =
                (binding.toolbar.contentInsetStartWithNavigation + dp2px(
                    getThis(),
                    8f
                )) * (1 - scale)
            binding.authorLayout.scaleX = 0.5f * (1 + scale)
            binding.authorLayout.scaleY = 0.5f * (1 + scale)
            binding.authorLayout.translationY =
                (binding.authorLayout.height / 2) * (1 - binding.authorLayout.scaleY)

            binding.tools.translationY = dp2px(getThis(), 24f) * (1 - scale)
            binding.tools.scaleX = 0.7f + 0.3f * scale
            binding.tools.scaleY = 0.7f + 0.3f * scale
            binding.tools.translationX =
                (binding.tools.width / 2) * (1 - binding.tools.scaleX)

        })

        viewModel.topicLiveData.observe(this) {
            if (it.state == DataState.STATE.SUCCESS) {
                it.data?.let { data ->
                    binding.name.text = data.name
                    binding.description.text = data.description
                    binding.discuss.text = data.articleNum.toString()
                    print(data.avatar)
                    ImageUtils.loadTopicAvatarInto(this,data.avatar,binding.avatar)
                }
            }
        }
    }


}