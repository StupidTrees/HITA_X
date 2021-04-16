package com.stupidtree.hitax.ui.main.timetable.panel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.stupidtree.component.data.SharedPreferenceBooleanLiveData
import com.stupidtree.component.data.SharedPreferenceIntLiveData
import com.stupidtree.hitax.data.repository.SubjectRepository
import com.stupidtree.hitax.data.repository.TimetableRepository
import com.stupidtree.hitax.data.repository.TimetableStyleRepository
import com.stupidtree.hitax.data.repository.TimetableStyleRepository.Companion.KEY_COLOR_ENABLE
import com.stupidtree.hitax.data.repository.TimetableStyleRepository.Companion.KEY_DRAW_BG_LINE
import com.stupidtree.hitax.data.repository.TimetableStyleRepository.Companion.KEY_FADE_ENABLE
import com.stupidtree.hitax.data.repository.TimetableStyleRepository.Companion.KEY_START_DATE
class TimetablePanelViewModel(application: Application) : AndroidViewModel(application) {

    private val timetableStyleRepository = TimetableStyleRepository.getInstance(application)
    private val subjectRepository = SubjectRepository.getInstance(application)
    private val timetableRepository = TimetableRepository.getInstance(application)

    val startDateLiveData: SharedPreferenceIntLiveData
        get() = timetableStyleRepository.startDateLiveData
    val drawBGLinesLiveData: SharedPreferenceBooleanLiveData
        get() = timetableStyleRepository.drawBGLinesLiveData

    val colorEnableLiveData: SharedPreferenceBooleanLiveData
        get() = timetableStyleRepository.colorEnableLiveData

    val fadeEnableLiveData: SharedPreferenceBooleanLiveData
        get() = timetableStyleRepository.fadeEnableLiveData


    fun changeStartDate(hour: Int, minute: Int) {
        val v = hour * 100 + minute
        timetableStyleRepository.putData(KEY_START_DATE,v)
    }
    fun setDrawBGLines(draw:Boolean) {
        timetableStyleRepository.putData(KEY_DRAW_BG_LINE,draw)
    }
    fun setColorEnable(draw:Boolean) {
        timetableStyleRepository.putData(KEY_COLOR_ENABLE,draw)
    }
    fun setFadeEnable(draw:Boolean) {
        timetableStyleRepository.putData(KEY_FADE_ENABLE,draw)
    }

    fun startResetColor(){
        subjectRepository.actionResetRecentSubjectColors()
    }
}