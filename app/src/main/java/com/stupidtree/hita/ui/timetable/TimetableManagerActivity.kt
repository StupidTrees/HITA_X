package com.stupidtree.hita.ui.timetable

import android.annotation.SuppressLint
import android.content.*
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.stupidtree.hita.R
import com.stupidtree.hita.data.model.timetable.Timetable
import com.stupidtree.hita.databinding.ActivityTimetableManagerBinding
import com.stupidtree.hita.databinding.DynamicCurriculumItemBinding
import com.stupidtree.hita.ui.base.BaseActivityWithReceiver
import com.stupidtree.hita.ui.base.BaseListAdapter
import com.stupidtree.hita.ui.base.BaseTabAdapter
import com.stupidtree.hita.ui.base.BaseViewHolder
import com.stupidtree.hita.ui.timetable.subject.SubjectsFragment
import com.stupidtree.hita.ui.widgets.PopUpText
import com.stupidtree.hita.utils.TimeUtils
import java.util.*

class TimetableManagerActivity : BaseActivityWithReceiver<TimetableManagerViewModel, ActivityTimetableManagerBinding>(), FragmentTimeTableChild.CurriculumPageRoot {


    private var listAdapter: CListAdapter? = null
    private var timetableSP: SharedPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setWindowParams(statusBar = true, darkColor = true, navi = false)
        setToolbarActionBack(binding.mainToolBar)
        timetableSP = getSharedPreferences("timetable_pref", Context.MODE_PRIVATE)
    }

    override fun getIntentFilter(): IntentFilter {
        val re = IntentFilter()
        re.addAction("TIMETABLE_CHANGED")
        return re
    }


    override fun initViews() {
        //binding.mainToolBar.inflateMenu(R.menu.toolbar_curriculum_manager)
        binding.mainToolBar.setOnMenuItemClickListener { menuItem ->
            if (menuItem.itemId == R.id.add) {
                //FragmentImportCurriculum().show(getSupportFragmentManager(), "import")
            }
            true
        }

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
//        binding.tabs.setupWithViewPager(binding.subjectsViewpager)
//        binding.tabs.tabMode = TabLayout.MODE_FIXED
//        binding.tabs.isTabIndicatorFullWidth = false

        listAdapter = CListAdapter(this, mutableListOf())
        binding.list.adapter = listAdapter
        binding.list.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

        bindLiveData()
    }

    private fun bindLiveData() {
        viewModel.currentTimetableLiveData.observe(this) {
            if (it == null) {
               // binding.tabs.visibility = View.INVISIBLE
                binding.subjectsViewpager.visibility = View.GONE
                binding.noneLayout.visibility = View.VISIBLE
            } else {
               // binding.tabs.visibility = View.VISIBLE
                binding.subjectsViewpager.visibility = View.VISIBLE
                binding.noneLayout.visibility = View.GONE
            }
            //if (curriculumShow.getName().contains("春")) image!!.setImageResource(R.drawable.ic_spring) else if (curriculumShow.getName().contains("夏")) image!!.setImageResource(R.drawable.ic_summer) else if (curriculumShow.getName().contains("秋")) image!!.setImageResource(R.drawable.ic_autumn) else if (curriculumShow.getName().contains("冬")) image!!.setImageResource(R.drawable.ic_winter) else image!!.setImageResource(R.drawable.ic_menu_jwts)
            for (fx in supportFragmentManager.fragments) {
                if (fx !is FragmentTimeTableChild<*, *>) continue
                fx.refresh()
            }
           // binding.subjectsViewpager.currentItem = 0
        }
        viewModel.timetablesLiveData.observe(this) {
            listAdapter?.notifyItemChangedSmooth(it)
            if (viewModel.currentTimetableLiveData.value == null && !it.isEmpty()) {
                viewModel.currentTimetableLiveData.value = it[0]
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
        return viewModel.currentTimetableLiveData.value
    }

    override fun getTimetableSP(): SharedPreferences {
        return timetableSP!!
    }

    override fun setTabVisibility(visibility: Int) {
        //binding.tabs.visibility = visibility
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


    @SuppressLint("ParcelCreator")
    inner class CListAdapter(context: Context, mBeans: MutableList<Timetable>) : BaseListAdapter<Timetable, CListAdapter.CHolder>(context, mBeans) {


        inner class CHolder(itemView: DynamicCurriculumItemBinding) : BaseViewHolder<DynamicCurriculumItemBinding>(itemView)

        override fun getViewBinding(parent: ViewGroup, viewType: Int): ViewBinding {
            return DynamicCurriculumItemBinding.inflate(mInflater, parent, false)
        }

        override fun createViewHolder(viewBinding: ViewBinding, viewType: Int): CHolder {
            return CHolder(viewBinding as DynamicCurriculumItemBinding)
        }

        @SuppressLint("SetTextI18n")
        override fun bindHolder(holder: CHolder, data: Timetable?, position: Int) {
            holder.binding.title.text = data?.name
            val isCurrent: Boolean = data?.id == viewModel.currentTimetableLiveData.value?.id
            holder.binding.check.isChecked = isCurrent
            holder.binding.subtitle.text = TimeUtils.printDate(data?.startTime?.time)
            holder.binding.delete.setOnClickListener { v ->
                v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                PopUpText().setTitle(R.string.attention)
                        .setText(getString(R.string.dialog_message_delete_curriculum))
                        .setOnConfirmListener(object : PopUpText.OnConfirmListener {
                            override fun OnConfirm() {

                            }

                        }).show(supportFragmentManager, "hint")
            }
            holder.binding.check.setOnClickListener(View.OnClickListener { v ->
                if (isCurrent) return@OnClickListener
                v.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                viewModel.currentTimetableLiveData.value = data
                notifyDataSetChanged()
            })
            holder.binding.check.setOnCheckedChangeListener { buttonView, isChecked -> if (buttonView.isPressed) buttonView.isChecked = !isChecked }

        }
    }

    companion object {
        private const val CHOOSE_FILE_CODE = 0
    }

    override fun initViewBinding(): ActivityTimetableManagerBinding {
        return ActivityTimetableManagerBinding.inflate(layoutInflater)
    }

    override fun getViewModelClass(): Class<TimetableManagerViewModel> {
        return TimetableManagerViewModel::class.java
    }

    override var receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

        }

    }
}