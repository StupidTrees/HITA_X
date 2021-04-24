package com.stupidtree.hita.theta.ui.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.stupidtree.component.data.DataState
import com.stupidtree.hita.theta.R
import com.stupidtree.hita.theta.data.model.Comment
import com.stupidtree.hita.theta.databinding.ActivityArticleDetailBinding
import com.stupidtree.hita.theta.ui.DirtyArticles
import com.stupidtree.hita.theta.ui.comment.CreateCommentFragment
import com.stupidtree.hita.theta.ui.create.CreateArticleActivity
import com.stupidtree.hita.theta.ui.list.ArticleListViewModel
import com.stupidtree.hita.theta.utils.TextTools
import com.stupidtree.stupiduser.util.ImageUtils
import com.stupidtree.style.base.BaseActivity
import com.stupidtree.style.base.BaseListAdapter

class ArticleDetailActivity : BaseActivity<ArticleDetailViewModel, ActivityArticleDetailBinding>(),
    CreateCommentFragment.OnCommentSentListener {
    lateinit var imageListAdapter:ImageListAdapter
    lateinit var listAdapter: HotCommentsListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.toolbar.title = ""
        setToolbarActionBack(binding.toolbar)
    }

    override fun initViewBinding(): ActivityArticleDetailBinding {
        return ActivityArticleDetailBinding.inflate(layoutInflater)
    }

    override fun getViewModelClass(): Class<ArticleDetailViewModel> {
        return ArticleDetailViewModel::class.java
    }

    override fun initViews() {
        binding.refresh.setColorSchemeColors(getColorPrimary())
        imageListAdapter = ImageListAdapter(this, mutableListOf())
        listAdapter = HotCommentsListAdapter(this, mutableListOf())
        binding.authorLayout.setOnClickListener {
            viewModel.articleLiveData.value?.data?.let {
                val c = Class.forName("com.stupidtree.hitax.ui.profile.ProfileActivity")
                val i = Intent(getThis(), c)
                i.putExtra("id", it.authorId)
                startActivity(i)
            }
        }
        binding.list.adapter = imageListAdapter
        binding.list.layoutManager = GridLayoutManager(this,3)
        binding.clist.adapter = listAdapter
        binding.clist.layoutManager = LinearLayoutManager(this)
        binding.comment.setOnClickListener {
            viewModel.articleLiveData.value?.data?.let {
                CreateCommentFragment.newInstance(it.id, null, null, it.authorId)
                    .setOnCommentSentListener(this@ArticleDetailActivity)
                    .show(supportFragmentManager, "comment")
            }
        }
        binding.like.setOnClickListener {
            viewModel.like()
        }
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

            binding.delete.translationY = dp2px(getThis(), 24f) * (1 - scale)
            binding.delete.scaleX = 0.7f + 0.3f * scale
            binding.delete.scaleY = 0.7f + 0.3f * scale
            binding.delete.translationX =
                (binding.delete.width / 2) * (1 - binding.delete.scaleX)

        })
        binding.repost.setOnClickListener {
            val i = Intent(getThis(), CreateArticleActivity::class.java)
            i.putExtra("repostId", viewModel.articleLiveData.value?.data?.id)
            startActivity(i)
        }
        listAdapter.setOnItemClickListener(object : BaseListAdapter.OnItemClickListener<Comment> {
            override fun onItemClick(data: Comment?, card: View?, position: Int) {
                viewModel.articleLiveData.value?.data?.let {
                    data?.let { it1 ->
                        CreateCommentFragment.newInstance(it.id, it1.id, it1.id, it1.authorId)
                            .setOnCommentSentListener(this@ArticleDetailActivity)
                            .show(supportFragmentManager, "comment")
                    }
                }
            }

        })

        viewModel.articleLiveData.observe(this) { ds ->
            if (ds.state == DataState.STATE.SUCCESS) {
                ds.data?.let {
                    ImageUtils.loadAvatarInto(getThis(), it.authorAvatar, binding.postAvatar)
                    if(!it.images.isNullOrEmpty()){
                        imageListAdapter.notifyItemChangedSmooth(it.images)
                    }

                    binding.postAuthor.text = it.authorName
                    binding.content.text = it.content
                    binding.likeNum.text = it.likeNum.toString()
                    binding.postTime.text = TextTools.getArticleTimeText(getThis(), it.createTime)
                    binding.likeIcon.setImageResource(
                        if (it.liked) {
                            R.drawable.ic_like_filled
                        } else {
                            R.drawable.ic_like_outline
                        }
                    )
                    if (it.repostId.isNullOrEmpty()) {
                        binding.repostLayout.visibility = View.GONE
                    } else {
                        binding.repostLayout.setOnClickListener { _ ->
                            val i = Intent(getThis(), ArticleDetailActivity::class.java)
                            i.putExtra("articleId", it.repostId)
                            startActivity(i)
                        }
                        binding.repostLayout.visibility = View.VISIBLE
                        binding.repostAuthor.text = it.repostAuthorName
                        binding.repostContent.text = it.repostContent
                        binding.repostTime.text =
                            TextTools.getArticleTimeText(getThis(), it.repostTime)
                        ImageUtils.loadAvatarInto(
                            getThis(),
                            it.repostAuthorAvatar,
                            binding.repostAvatar
                        )
                        if (it.repostId.isNullOrEmpty()) {
                            binding.repostLayout.visibility = View.GONE
                        } else {
                            binding.repostLayout.visibility = View.VISIBLE
                            binding.repostLayout.setOnClickListener { v ->
                                val i = Intent(getThis(), ArticleDetailActivity::class.java)
                                i.putExtra("articleId", it.repostId)
                                getThis().startActivity(i)
                            }
                            binding.repostAuthor.text = it.repostAuthorName
                            binding.repostContent.text = it.repostContent
                            binding.repostTime.text =
                                TextTools.getArticleTimeText(getThis(), it.repostTime)
                            ImageUtils.loadAvatarInto(
                                getThis(),
                                it.repostAuthorAvatar,
                                binding.repostAvatar
                            )
                            if (!it.repostImages.isNullOrEmpty()) {
                                binding.repostImgLayout.visibility = View.VISIBLE
                                binding.repostImg1.visibility = View.VISIBLE
                                binding.repostImg2.visibility = View.VISIBLE
                                binding.repostImg3.visibility = View.VISIBLE
                                if (it.repostImages.size < 3) {
                                    binding.repostImg3.visibility = View.GONE
                                } else {
                                    com.stupidtree.hita.theta.utils.ImageUtils.loadArticleImageInto(
                                        getThis(),
                                        it.repostImages.get(2),
                                        binding.repostImg3
                                    )
                                }
                                if (it.repostImages.size < 2) {
                                    binding.repostImg2.visibility = View.GONE
                                } else {
                                    com.stupidtree.hita.theta.utils.ImageUtils.loadArticleImageInto(
                                        getThis(),
                                        it.repostImages.get(1),
                                        binding.repostImg2
                                    )
                                }
                                com.stupidtree.hita.theta.utils.ImageUtils.loadArticleImageInto(
                                    getThis(),
                                    it.repostImages.get(0),
                                    binding.repostImg1
                                )

                            } else {
                                binding.repostImgLayout.visibility = View.GONE
                            }
                        }
                    }
                }

            }
        }
        viewModel.likeResultLiveData.observe(this) { dataState ->
            dataState.data?.let {
                addDirtyId()
                binding.likeNum.text = it.likeNum.toString()
                binding.likeIcon.setImageResource(
                    if (it.liked) {
                        R.drawable.ic_like_filled
                    } else {
                        R.drawable.ic_like_outline
                    }
                )
            }
        }
        viewModel.commentsLiveData.observe(this) {
            if (it.state == DataState.STATE.SUCCESS) {
                addDirtyId()
                binding.refresh.isRefreshing = false
                if (it.listAction == DataState.LIST_ACTION.APPEND) {
                    it.data?.let { it1 ->
                        listAdapter.notifyItemsAppended(it1)
                        if (it1.isNotEmpty()) {
                            binding.clist.smoothScrollBy(0, 300)
                        }
                    }
                } else {
                    it.data?.let { it1 ->
                        listAdapter.notifyItemChangedSmooth(it1)
                    }
                }
            }
        }
        binding.clist.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (!binding.nest.canScrollVertically(1) && listAdapter.itemCount >= ArticleListViewModel.PAGE_SIZE) {
                        binding.refresh.isRefreshing = true
                        intent.getStringExtra("articleId")?.let {
                            viewModel.loadMore(it)
                        }
                    }
                }
            }
        })
        binding.refresh.setOnRefreshListener {
            intent.getStringExtra("articleId")?.let {
                viewModel.refreshAll(it)
            }
        }
    }

    private fun dp2px(context: Context, dipValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dipValue * scale + 0.5f).toInt()
    }

    private fun addDirtyId() {
        intent.getStringExtra("articleId")?.let {
            DirtyArticles.addDirtyId(it)
        }
    }

    var first = true
    override fun onStart() {
        super.onStart()
        if (first) {
            first = false
            intent.getStringExtra("articleId")?.let {
                viewModel.startRefresh(it)
                viewModel.refreshAll(it)
            }
        }

    }

    override fun onSuccess() {
        intent.getStringExtra("articleId")?.let {
            viewModel.refreshAll(it)
        }
    }

    override fun onFailed() {

    }
}