package com.stupidtree.hita.ui.timetable.detail

import android.content.*
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.stupidtree.hita.R
import com.stupidtree.hita.data.model.timetable.Timetable
import com.stupidtree.hita.databinding.ActivityTimetableDetailBinding
import com.stupidtree.hita.ui.base.BaseActivityWithReceiver
import com.stupidtree.hita.ui.base.BaseTabAdapter
import com.stupidtree.hita.ui.timetable.FragmentTimeTableChild
import com.stupidtree.hita.ui.timetable.subject.SubjectsFragment

class TimetableDetailActivity :
    BaseActivityWithReceiver<TimetableDetailViewModel, ActivityTimetableDetailBinding>(),
    FragmentTimeTableChild.CurriculumPageRoot {

    private var timetableSP: SharedPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setWindowParams(statusBar = true, darkColor = true, navi = false)
        setToolbarActionBack(binding.toolbar)
        timetableSP = getSharedPreferences("timetable_pref", Context.MODE_PRIVATE)
    }

    override fun getIntentFilter(): IntentFilter {
        val re = IntentFilter()
        re.addAction("TIMETABLE_CHANGED")
        return re
    }


    override fun initViews() {
        val titles: Array<String> = resources.getStringArray(R.array.curriculum_tabs)
        val pagerAdapter = object : BaseTabAdapter(supportFragmentManager, 1) {
            override fun initItem(position: Int): Fragment {
                return SubjectsFragment()
            }

            override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
                //super.destroyItem(container, position, `object`)
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return titles[position]
            }
        }
        binding.subjectsViewpager.offscreenPageLimit = 5
        binding.subjectsViewpager.adapter = pagerAdapter
        bindLiveData()
    }

    private fun bindLiveData() {
        viewModel.timetableLiveData.observe(this) {
            binding.subjectsViewpager.visibility = View.VISIBLE
            binding.collapse.title = it.name
            for (fx in supportFragmentManager.fragments) {
                if (fx !is FragmentTimeTableChild<*, *>) continue
                fx.refresh()
            }
        }

    }

    override fun onChangeColorSettingsRefresh() {
    }


    override fun onModifiedCurriculumRefresh() {
    }

    override fun onCurriculumDeleteRefresh() {
    }

    override fun getCurriculum(): Timetable? {
        return viewModel.timetableLiveData.value
    }

    override fun getTimetableSP(): SharedPreferences {
        return timetableSP!!
    }

    override fun setTabVisibility(visibility: Int) {
        //binding.tabs.visibility = visibility
    }

    override fun onStart() {
        super.onStart()
        intent.getStringExtra("id")?.let {
            viewModel.startRefresh(it)
        }
    }

//    //当选择完Excel文件后调用此函数
//    protected override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        if (resultCode == Activity.RESULT_OK) {
//            if (requestCode == CHOOSE_FILE_CODE) {
//                val uri: Uri = data.getData()
//                val sPath1: String
//                sPath1 = FileOperator.getPath(this, uri) // Paul Burke写的函数，根据Uri获得文件路径
//                if (sPath1 == null) return
//                val file = File(sPath1)
//                loadCurriculumTask(this, file, Calendar.getInstance()).executeOnExecutor(HITAApplication.TPE)
//            }
//        }
//        super.onActivityResult(requestCode, resultCode, data)
//    }

//
//    fun onOperationDone(id: String?, task: BaseOperationTask?, params: Array<Boolean?>?, resObject: Any?) {
//        if (resObject is Boolean) {
//            when (id) {
//                "delete" -> {
//                    if (resObject) {
//                        Toast.makeText(HContext, R.string.delete_success, Toast.LENGTH_SHORT).show()
//                    } else {
//                        Toast.makeText(HContext, R.string.delete_failed, Toast.LENGTH_SHORT).show()
//                    }
//                    val i = Intent(TIMETABLE_CHANGED)
//                    LocalBroadcastManager.getInstance(getThis()).sendBroadcast(i)
//                    ActivityMain.saveData()
//                    swapIcon!!.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
//                }
//                "change" -> {
//                    val i2 = Intent(TIMETABLE_CHANGED)
//                    LocalBroadcastManager.getInstance(getThis()).sendBroadcast(i2)
//                    Toast.makeText(getThis(), R.string.curriculum_changed, Toast.LENGTH_SHORT).show()
//                }
//            }
//        } else if (resObject is Pair<*, *>) {
//            val pair = resObject
//            if (pair.first as Boolean) {
//                Toast.makeText(this@ActivityCurriculumManager, getString(R.string.import_excel_success, pair.second), Toast.LENGTH_SHORT).show()
//            } else {
//                Toast.makeText(this@ActivityCurriculumManager, R.string.import_failed, Toast.LENGTH_SHORT).show()
//            }
//            val i3 = Intent(TIMETABLE_CHANGED)
//            LocalBroadcastManager.getInstance(getThis()).sendBroadcast(i3)
//            saveData()
//        }
//    }

//    fun onListRefreshed(id: String?, params: Array<Boolean?>?, result: List<Curriculum?>?) {
//        listAdapter?.notifyItemChangedSmooth(result)
//        var index = -1
//        for (i in pagerData!!.indices) {
//            val c: Curriculum = pagerData!![i]
//            if (c.getCurriculumCode().equals(TimetableCore.getInstance(HContext).getCurrentCurriculumId())) {
//                index = i
//                break
//            }
//        }
//        if (index >= 0) {
//            list.scrollToPosition(index)
//        }
//    }


    override fun initViewBinding(): ActivityTimetableDetailBinding {
        return ActivityTimetableDetailBinding.inflate(layoutInflater)
    }

    override fun getViewModelClass(): Class<TimetableDetailViewModel> {
        return TimetableDetailViewModel::class.java
    }

    override var receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

        }

    }
}