package com.stupidtree.hita.theta.ui.create

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.stupidtree.component.data.DataState
import com.stupidtree.hita.theta.R
import com.stupidtree.hita.theta.ThetaActivity.Companion.POST_GET_NEW
import com.stupidtree.hita.theta.data.model.Article
import com.stupidtree.hita.theta.databinding.ActivityCreateArticleBinding
import com.stupidtree.hita.theta.ui.DirtyArticles
import com.stupidtree.stupiduser.util.ImageUtils
import com.stupidtree.style.base.BaseActivity

class CreateArticleActivity : BaseActivity<CreateArticleViewModel, ActivityCreateArticleBinding>() {

    override fun initViewBinding(): ActivityCreateArticleBinding {
        return ActivityCreateArticleBinding.inflate(layoutInflater)
    }

    override fun getViewModelClass(): Class<CreateArticleViewModel> {
        return CreateArticleViewModel::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbarActionBack(binding.toolbar2)
    }

    override fun onStart() {
        super.onStart()
        viewModel.refreshRepostArticle(intent.getStringExtra("repostId"))

    }
    override fun onEnterAnimationComplete() {
        super.onEnterAnimationComplete()
        popUpKeyboard()
    }
    private fun popUpKeyboard() {
        binding.content.requestFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
    }
    override fun initViews() {
        binding.title.setText(if(intent.getStringExtra("repostId").isNullOrEmpty()) R.string.create_article else R.string.repost)
        binding.done.setCardBackgroundColor(Color.GRAY)
        binding.done.isEnabled = false
        binding.content.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrBlank()) {
                    binding.done.setCardBackgroundColor(getColorPrimary())
                    binding.done.isEnabled = true
                } else {
                    binding.done.setCardBackgroundColor(Color.GRAY)
                    binding.done.isEnabled = false
                }
            }

        })
        binding.done.setOnClickListener {
            viewModel.createArticle(binding.content.text.toString())
        }
        viewModel.postResult.observe(this) {
            if (it.state == DataState.STATE.SUCCESS) {
                DirtyArticles.addAction(DataState.LIST_ACTION.PUSH_HEAD)
                finish()
            }
        }
        viewModel.repostArticleLiveData.observe(this) {
            if (it.state == DataState.STATE.SUCCESS) {
                binding.repost.visibility = View.VISIBLE
                if (it.data?.repostId.isNullOrEmpty()) {
                    ImageUtils.loadAvatarInto(
                        getThis(),
                        it.data?.authorId,
                        binding.repostAvatar
                    )
                    binding.repostAuthor.text = it.data?.authorName
                    binding.repostContent.text = it.data?.content
                } else {
                    ImageUtils.loadAvatarInto(
                        getThis(),
                        it.data?.repostAuthorId,
                        binding.repostAvatar
                    )
                    binding.repostAuthor.text = it.data?.repostAuthorName
                    binding.repostContent.text = it.data?.repostContent
                }
                binding.content.setText("//[u${it.data?.authorId}//@${it.data?.authorName}$]: ${it.data?.content}")
            } else {
                binding.repost.visibility = View.GONE
            }
        }
    }
}