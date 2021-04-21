package com.stupidtree.style.base

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

/**
 * 一个简单的ViewPagerAdapter实现
 */
abstract class BaseTabAdapter(fm: FragmentManager?, size: Int) : FragmentStatePagerAdapter(fm!!, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    protected var mFragments: Array<Fragment?> = arrayOfNulls(size)
    protected var size = 0
    var currentFragment: Fragment? = null
        private set
    private var destroyFragment = true
    override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
        try {
            currentFragment = `object` as Fragment
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.setPrimaryItem(container, position, `object`)
    }

    fun setDestroyFragment(destroyFragment: Boolean): BaseTabAdapter {
        this.destroyFragment = destroyFragment
        return this
    }

    override fun getItem(position: Int): Fragment {
        mFragments[position] = initItem(position)
        return mFragments[position]!!
    }

    protected abstract fun initItem(position: Int): Fragment
    override fun getCount(): Int {
        return size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val fragment = super.instantiateItem(container, position) as Fragment
        mFragments[position] = fragment
        return fragment
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        if (destroyFragment) super.destroyItem(container, position, `object`)
        mFragments[position] = null
    }

    init {
        this.size = size
    }
}