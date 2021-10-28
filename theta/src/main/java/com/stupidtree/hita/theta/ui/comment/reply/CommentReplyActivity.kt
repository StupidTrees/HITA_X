package com.stupidtree.hita.theta.ui.comment.reply

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.stupidtree.component.data.DataState
import com.stupidtree.hita.theta.R
import com.stupidtree.hita.theta.data.model.Comment
import com.stupidtree.hita.theta.databinding.ActivityCommentReplyBinding
import com.stupidtree.hita.theta.ui.DirtyArticles
import com.stupidtree.hita.theta.ui.comment.CreateCommentFragment
import com.stupidtree.hita.theta.ui.list.ArticleListViewModel.Companion.PAGE_SIZE
import com.stupidtree.hita.theta.utils.TextTools
import com.stupidtree.stupiduser.data.repository.LocalUserRepository
import com.stupidtree.stupiduser.util.ImageUtils
import com.stupidtree.style.base.BaseActivity
import com.stupidtree.style.base.BaseListAdapter

class CommentReplyActivity : BaseActivity<CommentReplyViewModel, ActivityCommentReplyBinding>(),
    CreateCommentFragment.OnCommentSentListener {

    lateinit var listAdapter: CommentsListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbarActionBack(binding.toolbar)
    }

    override fun initViewBinding(): ActivityCommentReplyBinding {
        return ActivityCommentReplyBinding.inflate(layoutInflater)
    }

    override fun getViewModelClass(): Class<CommentReplyViewModel> {
        return CommentReplyViewModel::class.java
    }

    override fun initViews() {
        binding.refresh.setColorSchemeColors(getColorPrimary())
        listAdapter = CommentsListAdapter(intent.getStringExtra("commentId"), this, mutableListOf())
        binding.authorLayout.setOnClickListener {
            viewModel.commentLiveData.value?.data?.let {
                val c = Class.forName("com.stupidtree.hitax.ui.profile.ProfileActivity")
                val i = Intent(getThis(), c)
                i.putExtra("id", it.authorId)
                startActivity(i)
            }
        }
        binding.clist.adapter = listAdapter
        binding.clist.layoutManager = LinearLayoutManager(this)
        binding.comment.setOnClickListener {

            viewModel.commentLiveData.value?.data?.let {
                CreateCommentFragment.newInstance(it.articleId, it.id, it.id, it.authorId)
                    .setOnCommentSentListener(this@CommentReplyActivity)
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

        })
        binding.clist.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (!binding.nest.canScrollVertically(1) && listAdapter.itemCount >= PAGE_SIZE) {
                        binding.refresh.isRefreshing = true
                        intent.getStringExtra("commentId")?.let {
                            viewModel.loadMore(it)
                        }
                    }
                }
            }
        })
        binding.refresh.setOnRefreshListener {
            intent.getStringExtra("commentId")?.let {
                viewModel.refreshAll(it)
            }
        }
        listAdapter.setOnItemClickListener(object : BaseListAdapter.OnItemClickListener<Comment> {
            override fun onItemClick(data: Comment?, card: View?, position: Int) {
                viewModel.commentLiveData.value?.data?.let { context ->
                    if (data?.authorId == LocalUserRepository.getInstance(getThis())
                            .getLoggedInUser().id
                    ) {
                        val ad: AlertDialog = AlertDialog.Builder(getThis())
                            .setItems(
                                R.array.comment_actions
                            ) { _, which ->
                                when (which) {
                                    0 -> {
                                        data?.id?.let { viewModel.deleteComment(it) }
                                    }
                                    1 -> {
                                        data?.let { reply ->
                                            CreateCommentFragment.newInstance(
                                                context.articleId,
                                                context.id,
                                                reply.id,
                                                reply.authorId
                                            ).setOnCommentSentListener(this@CommentReplyActivity)
                                                .show(supportFragmentManager, "comment")
                                        }
                                    }
                                    2 -> {
                                        val cm =
                                            getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                                        val mClipData =
                                            ClipData.newPlainText("comment", data?.content)
                                        cm.setPrimaryClip(mClipData)
                                    }
                                }
                            }.create()
                        ad.show()

                    } else {
                        data?.let { reply ->
                            CreateCommentFragment.newInstance(
                                context.articleId,
                                context.id,
                                reply.id,
                                reply.authorId
                            ).setOnCommentSentListener(this@CommentReplyActivity)
                                .show(supportFragmentManager, "comment")
                        }
                    }

                }
            }

        })

        viewModel.commentLiveData.observe(this) { ds ->
            if (ds.state == DataState.STATE.SUCCESS) {
                ds.data?.let { data ->
                    binding.content.text = data.content
                    binding.name.text = data.authorName
                    binding.time.text = TextTools.getArticleTimeText(getThis(), data.createTime)
                    binding.likeNum.text = data.likeNum.toString()
                    binding.comment.text = getString(R.string.reply_to_user, data.authorName)
                    binding.likeIcon.setImageResource(
                        if (data.liked) {
                            R.drawable.ic_like_filled
                        } else {
                            R.drawable.ic_like_outline
                        }
                    )
                    ImageUtils.loadAvatarInto(getThis(), data.authorAvatar, binding.avatar)
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
        viewModel.deleteCommentResult.observe(this) {
            if (it.state == DataState.STATE.SUCCESS) {
                intent.getStringExtra("commentId")?.let {
                    viewModel.refreshAll(it)
                }
                Toast.makeText(this, R.string.delete_success, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, R.string.delete_failed, Toast.LENGTH_SHORT).show()
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
            intent.getStringExtra("commentId")?.let {
                viewModel.startRefresh(it)
                viewModel.refreshAll(it)
            }
        }

    }

    override fun onSuccess() {
        intent.getStringExtra("commentId")?.let {
            viewModel.refreshAll(it)
        }
    }

    override fun onFailed() {

    }
}