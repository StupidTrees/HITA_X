package com.stupidtree.hita.theta.ui.search

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.stupidtree.hita.theta.R
import com.stupidtree.hita.theta.databinding.ActivityArticleSearchBinding
import com.stupidtree.hita.theta.ui.list.ArticleListFragment
import com.stupidtree.style.base.BaseActivity

class ArticleSearchActivity : BaseActivity<ArticleSearchViewModel, ActivityArticleSearchBinding>() {
    override fun initViewBinding(): ActivityArticleSearchBinding {
        return ActivityArticleSearchBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbarActionBack(binding.toolbar)
    }

    override fun getViewModelClass(): Class<ArticleSearchViewModel> {
        return ArticleSearchViewModel::class.java
    }

    override fun onEnterAnimationComplete() {
        super.onEnterAnimationComplete()
        popUpKeyboard()
    }
    private fun popUpKeyboard() {
        binding.search.requestFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    override fun initViews() {
        val fragment = ArticleListFragment.newInstance("search")
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment).commit()
        binding.input.setOnEditorActionListener { textView: TextView, i: Int, _: KeyEvent? ->
            if (TextUtils.isEmpty(textView.text.toString())) return@setOnEditorActionListener false
            if (i == EditorInfo.IME_ACTION_GO || i == EditorInfo.IME_ACTION_SEARCH) {
                fragment.setExtraAndRefreshAll(binding.input.text.toString())
                return@setOnEditorActionListener true
            }
            false
        }

    }
}