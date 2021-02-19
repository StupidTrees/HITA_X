package com.stupidtree.hita.ui.timetable

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.stupidtree.hita.data.model.timetable.Timetable
import com.stupidtree.hita.ui.base.BaseFragment

abstract class FragmentTimeTableChild<T:ViewModel,V:ViewBinding> : BaseFragment<T, V>() {
    var root: CurriculumPageRoot? = null

    override fun onDetach() {
        super.onDetach()
        root = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CurriculumPageRoot) {
            root = context
        }
    }

    abstract fun refresh()
    interface CurriculumPageRoot {
        fun onChangeColorSettingsRefresh()
        fun onModifiedCurriculumRefresh()
        fun onCurriculumDeleteRefresh()
        fun getCurriculum(): Timetable?
        fun getTimetableSP(): SharedPreferences
        fun setTabVisibility(visibility:Int)
    }
}