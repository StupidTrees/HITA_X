package com.stupidtree.hita.ui.eas

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Pair
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
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
        binding.jwtsPager.adapter = object : BaseTabAdapter(supportFragmentManager, titles.size) {
            override fun initItem(position: Int): Fragment {
                return ImportTimetableFragment.newInstance()
            }

            override fun getPageTitle(position: Int): CharSequence {
                return titles[position]
            }
        }.setDestroyFragment(false)
        binding.jwtsTab.setupWithViewPager(binding.jwtsPager)
    }


//    fun onOperationDone(
//        id: String?,
//        task: BaseOperationTask<Pair<List<Map<String?, String?>?>?, HashMap<String?, String?>?>?>?,
//        params: Array<Boolean?>?,
//        result: Pair<List<Map<String?, String?>>?, HashMap<String, String>?>?
//    ) {
//        MaterialCircleAnimator.animHide(loading)
//        pager!!.visibility = View.VISIBLE
//        if (result == null) {
//            EASCore.logOut()
//            Toast.makeText(HContext, "页面过期，请返回重新登录！", Toast.LENGTH_SHORT).show()
//            val i = Intent(this@EASActivity, LoginEASActivity::class.java)
//            startActivity(i)
//            finish()
//        } else {
//            if (!TextUtils.isEmpty(intent.getStringExtra("terminal"))) {
//                pager!!.currentItem =
//                    Objects.requireNonNull(intent.getStringExtra("terminal")).toInt()
//            }
//            // Log.e("refresh2", String.valueOf(getSupportFragmentManager().getFragments()));
//            for (f in supportFragmentManager.fragments) {
//                if (f is EASFragment) {
//                    if (f.isResumed) {
//                        (f as EASFragment).Refresh()
//                    } else {
//                        (f as EASFragment).setWillRefreshOnResume(true)
//                    }
//                }
//            }
//        }
//    }
//
//
//
//
//
//    internal class loadBasicInfoTask(listRefreshedListener: OperationListener?) :
//        BaseOperationTask<Pair<List<Map<String?, String?>?>?, HashMap<String?, String?>?>?>(
//            listRefreshedListener
//        ) {
//        protected fun doInBackground(
//            listRefreshedListener: OperationListener<Pair<List<Map<String?, String?>?>?, HashMap<String?, String?>?>?>?,
//            vararg booleans: Boolean?
//        ): Pair<List<Map<String, String>>, HashMap<String, String>>? {
//            val xnxqItems: MutableList<Map<String, String>> = ArrayList()
//            val keyToTitle = HashMap<String, String>()
//            return try {
//                xnxqItems.addAll(EASCore.getXNXQ())
//                keyToTitle.putAll(EASCore.getXKColumnTitles())
//                Pair(xnxqItems, keyToTitle)
//            } catch (e: EASException) {
//                try {
//                    xnxqItems.clear()
//                    if (tryToReLogin()) {
//                        xnxqItems.addAll(EASCore.getXNXQ())
//                        keyToTitle.clear()
//                        keyToTitle.putAll(EASCore.getXKColumnTitles())
//                        Pair(xnxqItems, keyToTitle)
//                    } else null
//                } catch (e2: EASException) {
//                    null
//                }
//            }
//        }
//    }

    override fun initViewBinding(): ActivityEasBinding {
        return ActivityEasBinding.inflate(layoutInflater)
    }

    override fun getViewModelClass(): Class<EASViewModel> {
        return EASViewModel::class.java
    }
}