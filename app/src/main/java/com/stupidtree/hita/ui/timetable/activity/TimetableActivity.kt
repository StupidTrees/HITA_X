package com.stupidtree.hita.ui.timetable.activity

import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.stupidtree.hita.data.model.timetable.Timetable
import com.stupidtree.hita.databinding.ActivityTimeTableBinding
import com.stupidtree.hita.ui.base.BaseActivity
import java.sql.Time

class TimetableActivity :
    BaseActivity<TimetableViewModel, ActivityTimeTableBinding>() {
    private lateinit var pagerAdapter: TimeTablePagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setWindowParams(statusBar = true, darkColor = true, navi = false)
        setToolbarActionBack(binding.mainToolBar)
    }


    override fun initViewBinding(): ActivityTimeTableBinding {
        return ActivityTimeTableBinding.inflate(layoutInflater)
    }

    override fun getViewModelClass(): Class<TimetableViewModel> {
        return TimetableViewModel::class.java
    }

    override fun onStart() {
        super.onStart()
        viewModel.startRefresh()
    }
    override fun initViews() {
        pagerAdapter = TimeTablePagerAdapter(binding.timetableViewpager,supportFragmentManager, 5)
        viewModel.timetableLiveData.observe(this){
            refreshWeekLayout(it)
        }
        binding.timetableViewpager.addOnPageChangeListener(object:ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                viewModel.timetableLiveData.value?.let {
                    refreshWeekLayout(it)
                }
            }

        })
    }

    private fun refreshWeekLayout(timetables:List<Timetable>){
        if(timetables.isEmpty()){
            binding.weekText.text = "无课表"
            return
        }
        val weeks = mutableListOf<Int>()
        val cd = pagerAdapter.getCurrentDate()
        var minTT:Timetable? = null
        var minWk = 99999
        for(tt in timetables){
            val wk = tt.getWeekNumber(cd)
            weeks.add(wk)
            if(wk<minWk){
                minWk = wk
                minTT = tt
            }
        }
        minTT?.let {
            binding.weekText.text = it.name+"-"+minWk
        }

    }
}