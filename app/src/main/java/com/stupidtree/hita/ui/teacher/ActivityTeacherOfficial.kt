package com.stupidtree.hita.ui.teacher

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.appbar.AppBarLayout
import com.stupidtree.hita.R
import com.stupidtree.hita.databinding.ActivityTeacherOfficialBinding
import com.stupidtree.hita.ui.base.BaseActivity
import com.stupidtree.hita.ui.base.DataState

open class ActivityTeacherOfficial :
    BaseActivity<TeacherViewModel, ActivityTeacherOfficialBinding>() {
    var tabTitles: MutableList<String> = mutableListOf()
    var pagerAdapter: PagerAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setWindowParams(statusBar = true, darkColor = true, navi = false)
        setToolbarActionBack(binding.toolbar)
    }

    override fun onStart() {
        super.onStart()
        refresh()
    }


    private fun initToolbar() {
        binding.toolbar.title = ""
        binding.appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val scale = 1.0f + verticalOffset / appBarLayout.height.toFloat()
            binding.cardAvatar.scaleX = scale
            binding.cardAvatar.scaleY = scale
            val mHeadImgScale = 0f
            binding.cardAvatar.translationY = mHeadImgScale * verticalOffset
            binding.cardAvatar.scaleX = scale
            binding.cardAvatar.scaleY = scale
            binding.cardAvatar.translationY = mHeadImgScale * verticalOffset
            if(scale<0.5){
                binding.fab.shrink()
            }else{
                binding.fab.extend()
            }
        })
    }

    private fun initPager() {
        pagerAdapter = object : PagerAdapter() {

            override fun isViewFromObject(view: View, `object`: Any): Boolean {
                return view === `object`
            }

            override fun getCount(): Int {
                return tabTitles.size
            }

            //设置viewpager内部东西的方法，如果viewpager内没有子空间滑动产生不了动画效果
            @SuppressLint("InflateParams")
            override fun instantiateItem(container: ViewGroup, position: Int): Any {
                val v: View =
                    layoutInflater.inflate(R.layout.dynamic_teacher_official_info_page, null, false)
                val textView: TextView = v.findViewById(R.id.text)
                viewModel.teacherPagesLiveData.value?.data?.get(tabTitles[position])?.let {
                    textView.text = Html.fromHtml(it)
                }
                container.addView(v)
                //最后要返回的是控件本身
                return v
            }

            override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
                container.removeView(`object` as View)
            }

            //目的是展示title上的文字，
            override fun getPageTitle(position: Int): CharSequence {
                return tabTitles.get(position)
            }
        }
        binding.pager.adapter = pagerAdapter
        binding.tabs.setupWithViewPager(binding.pager)
    }

    @SuppressLint("ResourceType")
    override fun initViews() {
        initPager()
        initToolbar()
        binding.fab.setBackgroundColor(getColorPrimary())
        binding.fab.visibility = View.INVISIBLE
        binding.fab.setHideMotionSpecResource(R.anim.fab_scale_hide)
        binding.fab.setShowMotionSpecResource(R.anim.fab_scale_show)
        binding.cardAvatar.setOnClickListener {
//            ActivityUtils.showOneImage(
//                getThis(),
//                "http://faculty.hitsz.edu.cn/file/showHP.do?d=$teacherId"
//            )
        }
        binding.fab.setOnClickListener {
            viewModel.teacherProfileLiveData.value?.data?.let { it1 ->
                TeacherContactFragment.newInstance(
                    it1
                ).show(supportFragmentManager, "ftc")
            }
        }
        binding.refresh.setColorSchemeColors(getColorPrimary())
        binding.refresh.setOnRefreshListener { refresh() }
        viewModel.teacherKeyLiveData.observe(this) {
            Glide.with(this).load(
                "http://faculty.hitsz.edu.cn/file/showHP.do?d=" +
                        it.id + "&&w=200&&h=200&&prevfix=200-"
            ).apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.place_holder_avatar)
                .into(binding.teacherAvatar)
            binding.teacherName.text = it.name
        }
        viewModel.teacherProfileLiveData.observe(this) {
            if (it.state == DataState.STATE.SUCCESS) {
                val pos = it.data?.get("post")
                if (pos.isNullOrBlank()) {
                    binding.teacherPost.visibility = View.GONE
                } else {
                    binding.teacherPost.visibility = View.VISIBLE
                    binding.teacherPost.text = pos
                }
                val posi = it.data?.get("position")
                if (posi.isNullOrBlank()) {
                    binding.teacherPosition.visibility = View.GONE
                } else {
                    binding.teacherPosition.visibility = View.VISIBLE
                    binding.teacherPosition.text = posi
                }
                val lab = it.data?.get("label")
                if (lab.isNullOrBlank()) {
                    binding.teacherLabel.visibility = View.GONE
                } else {
                    binding.teacherLabel.visibility = View.VISIBLE
                    binding.teacherLabel.text = lab
                }
            }
        }
        viewModel.teacherPagesLiveData.observe(this) {
            binding.refresh.isRefreshing = false
            binding.fab.show()
            if (it.state == DataState.STATE.SUCCESS) {
                tabTitles.clear()
                it.data?.keys?.let { it1 -> tabTitles.addAll(it1) }
                if (it.data.isNullOrEmpty()) {
                    binding.pager.visibility = View.GONE
                    binding.emptyView.visibility = View.VISIBLE
                } else {
                    binding.emptyView.visibility = View.GONE
                    binding.pager.visibility = View.VISIBLE
                }
                pagerAdapter?.notifyDataSetChanged()
                binding.pager.scheduleLayoutAnimation()
            } else {
                binding.pager.visibility = View.GONE
                binding.emptyView.visibility = View.VISIBLE
            }

        }
    }

    private fun refresh() {
        binding.fab.hide()
        binding.refresh.isRefreshing = true
        intent.getStringExtra("id")?.let { id ->
            intent.getStringExtra("url")?.let { url ->
                viewModel.startRefresh(id, url, intent.getStringExtra("name"))
            }
        }
    }


    override fun initViewBinding(): ActivityTeacherOfficialBinding {
        return ActivityTeacherOfficialBinding.inflate(layoutInflater)
    }

    override fun getViewModelClass(): Class<TeacherViewModel> {
        return TeacherViewModel::class.java
    }
}