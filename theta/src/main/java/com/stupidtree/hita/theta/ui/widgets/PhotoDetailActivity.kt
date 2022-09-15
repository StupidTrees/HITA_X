package com.stupidtree.hita.theta.ui.widgets

import android.annotation.SuppressLint
import android.app.Application
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.get
import androidx.lifecycle.AndroidViewModel
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.bm.library.PhotoView
import com.bumptech.glide.Glide
import com.stupidtree.hita.theta.databinding.ActivityPhotoDetailBinding
import com.stupidtree.hita.theta.utils.ImageUtils
import com.stupidtree.style.base.BaseActivity

class PhotoDetailActivity :
    BaseActivity<PhotoDetailActivity.PhotoViewModel, ActivityPhotoDetailBinding>() {

    var initIndex = 0
    var absLink = false
    private val ids: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.toolbar.title = ""
        setToolbarActionBack(binding.toolbar)
    }

    @SuppressLint("SetTextI18n")
    override fun initViews() {
        val data = intent.getStringArrayExtra("ids")
        absLink = intent.getBooleanExtra("abs",false)
        initIndex = intent.getIntExtra("init_index", 0)
        if (data?.isNotEmpty() == true) {
            ids.addAll(listOf(*data))
            binding.label.text = (initIndex + 1).toString() + "/" + ids.size
            initPager()
        }
    }

    private fun initPager() {
        val views: Array<PhotoView?> = arrayOfNulls(ids.size)
        binding.pager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            @SuppressLint("SetTextI18n")
            override fun onPageSelected(position: Int) {
                binding.label.text = (position + 1).toString() + "/" + ids.size
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        binding.pager.adapter = object : PagerAdapter() {
            override fun getCount(): Int {
                return ids.size
            }

            override fun isViewFromObject(view: View, `object`: Any): Boolean {
                return view === `object`
            }

            override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
                views[position] = null
                container.removeView(`object` as View)
            }

            override fun instantiateItem(container: ViewGroup, position: Int): Any {
                if (views[position] == null) {
                    val v = PhotoView(getThis())
                    v.scaleType = ImageView.ScaleType.FIT_CENTER
                    if(absLink){
                        Glide.with(getThis()).load(ids[position]).into(v)
                    }else ImageUtils.loadArticleImageRawInto(
                        getThis(),
                        ids[position],
                        v
                    )
                    v.adjustViewBounds = false
                    v.enable()
                    v.setOnClickListener {
                        onBackPressed()
                    }
                    container.addView(v)
                    if(position==initIndex) v.transitionName = "image"
                    views[position] = v
                }

                return views[position]!!
            }
        }
        binding.pager.currentItem = initIndex
    }

    class PhotoViewModel(application: Application) : AndroidViewModel(application)

    override fun initViewBinding(): ActivityPhotoDetailBinding {
        return ActivityPhotoDetailBinding.inflate(layoutInflater)
    }

    override fun getViewModelClass(): Class<PhotoViewModel> {
        return PhotoViewModel::class.java
    }
}