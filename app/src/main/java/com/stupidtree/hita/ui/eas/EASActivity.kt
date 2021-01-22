package com.stupidtree.hita.ui.eas

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Pair
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.stupidtree.hita.R
import com.stupidtree.hita.databinding.ActivityEasBinding
import com.stupidtree.hita.ui.base.BaseActivity
import com.stupidtree.hita.ui.base.BaseTabAdapter
import com.stupidtree.hita.ui.base.DataState
import com.stupidtree.hita.ui.eas.imp.ImportTimetableFragment
import java.util.*

class EASActivity : BaseActivity<EASViewModel, ActivityEasBinding>(),EASFragment.JWRoot{


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setWindowParams(statusBar = true, darkColor = true, navi = false)
        setToolbarActionBack(binding.toolbar)
    }

    public override fun initViews() {
        initPager()
    }

    private fun initPager() {

        val titles = listOf("课表导入")
        binding.title.text = binding.navView.menu.getItem(0).title
        binding.pager.adapter = object : BaseTabAdapter(supportFragmentManager, titles.size) {
            override fun initItem(position: Int): Fragment {
                return ImportTimetableFragment.newInstance()
            }

            override fun getPageTitle(position: Int): CharSequence {
                return titles[position]
            }
        }.setDestroyFragment(false)
        binding.pager.offscreenPageLimit = 3
        binding.pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                val item = binding.navView.menu.getItem(position)
                item.isChecked = true
                binding.title.text = item.title
            }
            override fun onPageScrollStateChanged(state: Int) {}
        })
        binding.navView.setOnNavigationItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.navigation_import->binding.pager.currentItem = 0
            }
            binding.title.text = item.title
            true
        }
    }


    override fun initViewBinding(): ActivityEasBinding {
        return ActivityEasBinding.inflate(layoutInflater)
    }

    override fun getViewModelClass(): Class<EASViewModel> {
        return EASViewModel::class.java
    }
}