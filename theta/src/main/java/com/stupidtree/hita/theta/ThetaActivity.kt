package com.stupidtree.hita.theta

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.stupidtree.hita.theta.ui.create.CreateArticleActivity
import com.stupidtree.hita.theta.databinding.ActivityThetaBinding
import com.stupidtree.hita.theta.ui.list.ArticleListFragment
import com.stupidtree.hita.theta.ui.user.UserListFragment
import com.stupidtree.hita.theta.ui.list.ArticleNavigationFragment
import com.stupidtree.style.base.BaseActivity
import com.stupidtree.style.base.BaseTabAdapter

/**
 * 很显然，这是主界面
 */
@SuppressLint("NonConstantResourceId")
class ThetaActivity : BaseActivity<ThetaViewModel, ActivityThetaBinding>() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbarActionBack(binding.toolbar)
    }


    override fun initViews() {
        binding.title.setText(R.string.title_following)
        binding.pager.adapter = object : BaseTabAdapter(supportFragmentManager, 2) {
            override fun initItem(position: Int): Fragment {
                return when (position) {
                    0 -> ArticleListFragment.newInstance("following")
                    else -> ArticleNavigationFragment()
                }
            }

            override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
                super.destroyItem(container, position, `object`)
            }
        }
        binding.pager.offscreenPageLimit = 3
        binding.pager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                binding.title.setText(
                    when (position) {
                        0 -> R.string.title_following
                        else -> R.string.title_playground
                    }
                )
                val item = binding.navView.menu.getItem(position)
                item.isChecked = true
                //Objects.requireNonNull(getSupportActionBar()).setTitle(item.getTitle());
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        binding.navView.setOnNavigationItemSelectedListener {
            binding.navView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            binding.pager.currentItem = when (it.itemId) {
                R.id.navigation_home -> 0
                R.id.navigation_navigation -> 1
                else -> 0
            }
            return@setOnNavigationItemSelectedListener true
        }
        binding.add.setOnClickListener {
            val i = Intent(getThis(), CreateArticleActivity::class.java)
            startActivity(i)
        }
    }


    override fun getViewModelClass(): Class<ThetaViewModel> {
        return ThetaViewModel::class.java
    }

    override fun initViewBinding(): ActivityThetaBinding {
        return ActivityThetaBinding.inflate(layoutInflater)
    }


    companion object {
        const val POST_GET_NEW = "COM.STUPIDTREE.THETA.POST.GET_NEW"
        const val POST_REFRESH_ONE = "COM.STUPIDTREE.THETA.POST.REFRESH_ONE"
    }
}