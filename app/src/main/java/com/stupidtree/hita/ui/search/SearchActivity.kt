package com.stupidtree.hita.ui.search

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.stupidtree.hita.R
import com.stupidtree.hita.databinding.ActivitySearchBinding
import com.stupidtree.hita.ui.base.BaseActivity
import com.stupidtree.hita.ui.base.BaseTabAdapter
import com.stupidtree.hita.ui.search.teacher.FragmentSearchTeacher
import com.stupidtree.hita.utils.ActivityUtils
import java.util.*

class SearchActivity : BaseActivity<SearchViewModel, ActivitySearchBinding>(),
    FragmentSearchResult.SearchRoot {
    var pagerAdapter: SearchPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setWindowParams(statusBar = true, darkColor = true, navi = false)
        setToolbarActionBack(binding.toolbar)
    }

    override fun onStart() {
        super.onStart()
        if (!searchForPurpose()) {
            popUpKeyboard()
        }
    }

    private fun searchForPurpose(): Boolean {
        val text = intent.getStringExtra("keyword")
        binding.searchview.setText(text)
        val purpose = intent.getStringExtra("type")
        if (text.isNullOrEmpty() || purpose.isNullOrEmpty()) return false
        var index = 0
        when (purpose) {
//            "timetable" -> index = 0
            ActivityUtils.SearchType.TEACHER.name -> index = 0
//            "user" -> index = 2
//            "location" -> index = 3
//            "library" -> index = 4
//            "hitsz" -> index = 5
//            "hitzs" -> index = 6
        }
        binding.pager.currentItem = index
        return true
    }

    override fun initViews() {
        binding.toolbar.title = ""
        binding.searchview.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(textView: TextView, i: Int, keyEvent: KeyEvent): Boolean {
                if (textView.text.toString().isBlank()) return false
                if (i == EditorInfo.IME_ACTION_GO || i == EditorInfo.IME_ACTION_SEARCH) {
                    /*隐藏软键盘*/
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                    // 隐藏软键盘
                    imm?.hideSoftInputFromWindow(window.decorView.windowToken, 0)
                    setSearchText(getSearchText())
                    return true
                }
                return false
            }
        })
        pagerAdapter = SearchPagerAdapter(supportFragmentManager, this)
        binding.pager.adapter = pagerAdapter
        binding.tabs.setupWithViewPager(binding.pager)
    }


    private fun popUpKeyboard() {
        binding.searchview.requestFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    override fun getSearchText(): String {
        return binding.searchview.text.toString()
    }

    fun setSearchText(text: String) {
        for (f in supportFragmentManager.fragments) {
            if (f is FragmentSearchResult<*, *>) {
                f.setSearchText(text)
            }
        }
    }

    class SearchPagerAdapter(fm: FragmentManager, val context: Context) : BaseTabAdapter(fm, 1) {
        private var titles: IntArray = intArrayOf(
            //R.string.tab_search_timetable,
            R.string.tab_search_teacher,
//            R.string.tab_search_user,
//            R.string.tab_search_location,
//            R.string.tab_search_library,
//            R.string.tab_hitsz_website_info,
//            R.string.tab_search_zsw
        )


        override fun getPageTitle(position: Int): CharSequence {
            return context.getString(titles[position])
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            //super.destroyItem(container, position, object);
            mFragments[position] = null
        }

        protected override fun initItem(position: Int): Fragment {
            return FragmentSearchTeacher()
//            when (position) {
//                0 -> FragmentSearchResult_timetable.newInstance()
//                1 -> FragmentSearchResult_teacher.newInstance()
//                2 -> FragmentSearchResult_user.newInstance()
//                3 -> FragmentSearchResult_location.newInstance()
//                4 -> FragmentSearchResult_library.newInstance()
//                5 -> FragmentSearchResult_web.newInstance()
//                else -> FragmentSearchResult_zsw.newInstance()
//            }
        }

    }

    override fun initViewBinding(): ActivitySearchBinding {
        return ActivitySearchBinding.inflate(layoutInflater)
    }

    override fun getViewModelClass(): Class<SearchViewModel> {
        return SearchViewModel::class.java
    }

}