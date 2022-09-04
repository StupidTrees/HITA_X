package com.stupidtree.hitax.ui.main

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.HapticFeedbackConstants
import android.view.MenuItem
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout.DrawerListener
import androidx.drawerlayout.widget.DrawerLayout.GONE
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.stupidtree.component.data.DataState
import com.stupidtree.hitax.R
import com.stupidtree.hitax.databinding.ActivityMainBinding
import com.stupidtree.hitax.ui.about.ActivityAbout
import com.stupidtree.hitax.ui.about.UserAgreementDialog
import com.stupidtree.style.base.BaseActivity
import com.stupidtree.style.base.BaseTabAdapter
import com.stupidtree.hitax.ui.eas.EASActivity
import com.stupidtree.hitax.ui.eas.imp.ImportTimetableActivity
import com.stupidtree.hitax.ui.eas.login.PopUpLoginEAS
import com.stupidtree.hitax.ui.event.add.PopupAddEvent
import com.stupidtree.hitax.ui.main.navigation.NavigationFragment
import com.stupidtree.hitax.ui.main.timeline.FragmentTimeLine
import com.stupidtree.hitax.ui.main.timetable.inner.TimetablePageFragment
import com.stupidtree.hitax.ui.main.timetable.outer.TimetableFragment
import com.stupidtree.hitax.ui.main.timetable.panel.FragmentTimetablePanel
import com.stupidtree.hitax.utils.ActivityUtils
import com.stupidtree.hitax.utils.ImageUtils
import com.stupidtree.stupiduser.data.repository.LocalUserRepository
import com.stupidtree.style.widgets.PopUpText
import com.stupidtree.sync.StupidSync
import me.ibrahimsn.lib.OnItemSelectedListener
import java.lang.Exception

/**
 * 很显然，这是主界面
 */
class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>(),
    TimetableFragment.MainPageController, FragmentTimeLine.MainPageController {

    /**
     * 抽屉里的View
     */
    private var drawerAvatar: ImageView? = null
    private var drawerNickname: TextView? = null
    private var drawerUsername: TextView? = null
    private var drawerHeader: ViewGroup? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.toolbar)

    }


    private fun setUpDrawer() {
        binding.drawerNavigationview.itemIconTintList = null
        val headerView =
            binding.drawerNavigationview.inflateHeaderView(R.layout.activity_main_nav_header)
        binding.drawer.setStatusBarBackgroundColor(Color.TRANSPARENT)
        binding.drawer.setScrimColor(getBackgroundColorSecondAsTint())
        binding.drawer.drawerElevation = ImageUtils.dp2px(this, 84f).toFloat()
        drawerAvatar = headerView.findViewById(R.id.avatar)
        drawerHeader = headerView.findViewById(R.id.drawer_header)
        drawerNickname = headerView.findViewById(R.id.nickname)
        drawerUsername = headerView.findViewById(R.id.username)
        binding.drawer.addDrawerListener(object : DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                //offset 偏移值
                val mContent = binding.drawer.getChildAt(0)
                val scale = 1 - slideOffset
                val rightScale = 0.8f + scale * 0.2f
                mContent.translationX = -drawerView.measuredWidth * slideOffset
                mContent.pivotX = mContent.measuredWidth.toFloat()
                mContent.pivotY = (mContent.measuredHeight shr 1.toFloat().toInt()).toFloat()
                mContent.invalidate()
                mContent.scaleX = rightScale
                mContent.scaleY = rightScale
            }

            override fun onDrawerOpened(drawerView: View) {
                // setUserViews(viewModel.localUser)
            }

            override fun onDrawerClosed(drawerView: View) {}
            override fun onDrawerStateChanged(newState: Int) {}
        })

        binding.drawerNavigationview.setNavigationItemSelectedListener { item: MenuItem ->
            var jumped = true
            when (item.itemId) {
                R.id.drawer_nav_search -> {
                    ActivityUtils.startSearchActivity(getThis())
                }
                R.id.drawer_nav_ua -> {
                    UserAgreementDialog().show(supportFragmentManager,"ua")
                }
                R.id.drawer_nav_timetable_manager -> {
                    ActivityUtils.startTimetableManager(getThis())
                }
                R.id.drawer_nav_about-> {
                    ActivityUtils.startActivity(getThis(),ActivityAbout::class.java)
                }
                else -> jumped = false
            }
//            if (jumped) {
//                binding.drawer.closeDrawer(GravityCompat.START)
//            }
            jumped
        }
    }

    var checkedUpdate = false
    var lastCheckTs:Long = 0

    override fun onStart() {
        super.onStart()
        viewModel.startRefreshUser()
        try {
            val code = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageManager.getPackageInfo(
                    packageName,
                    0
                ).longVersionCode
            } else {
                packageManager.getPackageInfo(
                    packageName,
                    0
                ).versionCode.toLong()
            }
            if(System.currentTimeMillis()-lastCheckTs>10*60*1000) checkedUpdate = false
            if (!checkedUpdate) {
                if(LocalUserRepository.getInstance(this).getLoggedInUser().isValid()) {
                    checkedUpdate = true
                    lastCheckTs = System.currentTimeMillis()
                }
                viewModel.checkForUpdate(code)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun initViews() {
        setUpDrawer()
        //  binding.title.text = binding.navView.menu.getItem(0).title
        binding.pager.adapter = object : BaseTabAdapter(supportFragmentManager, 3) {
            override fun initItem(position: Int): Fragment {
                return when (position) {
                    0 -> FragmentTimeLine()
                    1 -> TimetableFragment()
                    else -> NavigationFragment()
                }
            }

            override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
                //super.destroyItem(container, position, `object`)
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
                binding.navView.itemActiveIndex = position//.getItem(position).isChecked = true
                when (position) {
                    0 -> {
                        binding.timetableLayout.visibility = GONE
                        binding.navigationLayout.visibility = GONE
                        binding.todayLayout.visibility = VISIBLE
                    }
                    1 -> {
                        binding.timetableLayout.visibility = VISIBLE
                        binding.todayLayout.visibility = GONE
                        binding.navigationLayout.visibility = GONE
                    }
                    2 -> {
                        binding.timetableLayout.visibility = GONE
                        binding.todayLayout.visibility = GONE
                        binding.navigationLayout.visibility = VISIBLE
                    }

                }
//                val item = binding.navView.menu.getItem(position)
//                item.isChecked = true
//                binding.title.text = item.title
                //Objects.requireNonNull(getSupportActionBar()).setTitle(item.getTitle());
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        binding.navView.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelect(pos: Int): Boolean {
                binding.navView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                binding.pager.currentItem = pos
                return true
            }

        }
//        binding.navView.setOnNavigationItemSelectedListener { item: MenuItem ->
//            when (item.itemId) {
//                R.id.navigation_timeline -> binding.pager.currentItem = 0
//                R.id.navigation_timetable -> binding.pager.currentItem = 1
//                R.id.navigation_navigation -> binding.pager.currentItem = 2
//            }
//            binding.navView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
//            true
//        }
        binding.drawerButton.setOnClickListener { binding.drawer.openDrawer(GravityCompat.END) }

        binding.timetableSetting.setOnClickListener {
            FragmentTimetablePanel().show(supportFragmentManager, "panel")
        }

        binding.addEvent.setOnClickListener {
            PopupAddEvent().show(supportFragmentManager,"add_event")
        }
        viewModel.checkUpdateResult.observe(this) {
            if (it.state == DataState.STATE.SUCCESS) {
                it.data?.let { cr ->
                    if (cr.shouldUpdate) {
                        PopUpText().setText("版本：${cr.latestVersionName}\n更新内容：${cr.updateLog}\n" + "是否前往下载？")
                            .setTitle(R.string.new_version_available)
                            .setOnConfirmListener(object : PopUpText.OnConfirmListener {
                                override fun OnConfirm() {
                                    val uri: Uri = Uri.parse(cr.latestUrl);
                                    val intent: Intent = Intent(Intent.ACTION_VIEW, uri);
                                    startActivity(intent)
                                }
                            }).show(supportFragmentManager, "update")
                    }
                }
            }
        }
        viewModel.loggedInUserLiveData.observe(this) {
            Log.e("user", it.toString())
            if (it.isValid()) { //如果已登录
                //装载头像
                com.stupidtree.stupiduser.util.ImageUtils.loadAvatarInto(
                    this,
                    it.avatar,
                    drawerAvatar!!
                )
                //设置各种文字
                drawerUsername?.text = it.username
                drawerNickname?.text = it.nickname
                drawerHeader?.setOnClickListener {
                    ActivityUtils.startProfileActivity(
                        getThis(),
                        LocalUserRepository.getInstance(applicationContext).getLoggedInUser().id
                    )
                }
            } else {
                //未登录的信息显示
                drawerUsername?.setText(R.string.not_log_in)
                drawerNickname?.setText(R.string.log_in_first)
                drawerAvatar?.setImageResource(R.drawable.place_holder_avatar)
                drawerHeader?.setOnClickListener { ActivityUtils.startWelcomeActivity(getThis()) }
            }
        }
    }


    override fun onBackPressed() {
        //super.onBackPressed();
        if (binding.drawer.isDrawerOpen(GravityCompat.END)) {
            binding.drawer.closeDrawer(GravityCompat.END)
            return
        }
        //返回桌面而非退出
        val intent = Intent(Intent.ACTION_MAIN)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addCategory(Intent.CATEGORY_HOME)
        startActivity(intent)
    }


    override fun getViewModelClass(): Class<MainViewModel> {
        return MainViewModel::class.java
    }

    override fun initViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun setTitleText(string: String) {
        binding.timetableTitle.text = string
        binding.timetableNameCard.visibility = VISIBLE
    }

    override fun setTimetableName(String: String) {
        binding.timetableName.text = String
        binding.timetableNameCard.visibility = VISIBLE
    }

    override fun setSingleTitle(string: String) {
        binding.timetableTitle.text = string
        binding.timetableNameCard.visibility = GONE
    }

    override fun setTimelineTitleText(string: String) {
        binding.todayTitle.text = string
    }
}