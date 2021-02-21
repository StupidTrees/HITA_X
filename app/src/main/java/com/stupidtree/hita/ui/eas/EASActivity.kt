package com.stupidtree.hita.ui.eas

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.stupidtree.hita.R
import com.stupidtree.hita.databinding.ActivityEasBinding
import com.stupidtree.hita.ui.base.BaseActivity
import com.stupidtree.hita.ui.base.BaseTabAdapter
import com.stupidtree.hita.ui.base.DataState
import com.stupidtree.hita.ui.eas.imp.ImportTimetableFragment
import com.stupidtree.hita.ui.eas.login.PopUpLoginEAS
import com.stupidtree.hita.utils.ActivityUtils

class EASActivity : BaseActivity<EASViewModel, ActivityEasBinding>(), EASFragment.JWRoot {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setWindowParams(statusBar = true, darkColor = true, navi = false)
        setToolbarActionBack(binding.toolbar)
    }

    public override fun initViews() {
        initPager()
        viewModel.loginCheckResult.observe(this) {
            if (it.state == DataState.STATE.SUCCESS && it.data != true) {
                ActivityUtils.showEasVerifyWindow<EASActivity>(
                    getThis(),
                    cancelable = false,
                    onResponseListener = object : PopUpLoginEAS.OnResponseListener {
                        override fun onSuccess(window: PopUpLoginEAS) {
                            window.dismiss()
                            refreshAll()
                        }

                        override fun onFailed(window: PopUpLoginEAS) {
                            Toast.makeText(getThis(), R.string.login_failed, Toast.LENGTH_SHORT)
                                .show()
                        }
                    })
            }
        }

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
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                val item = binding.navView.menu.getItem(position)
                item.isChecked = true
                binding.title.text = item.title
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        binding.navView.setOnNavigationItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.navigation_import -> binding.pager.currentItem = 0
            }
            binding.title.text = item.title
            true
        }
    }

    fun refreshAll() {
        for (fragment in supportFragmentManager.fragments) {
            if (fragment is EASFragment<*, *> && fragment.isResumed) {
                fragment.refresh()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.startLoginCheck()
    }

    override fun initViewBinding(): ActivityEasBinding {
        return ActivityEasBinding.inflate(layoutInflater)
    }

    override fun getViewModelClass(): Class<EASViewModel> {
        return EASViewModel::class.java
    }
}