package com.stupidtree.hitax.data.repository

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.MediatorLiveData
import com.stupidtree.component.data.booleanLiveData
import com.stupidtree.component.data.intLiveData
import com.stupidtree.hitax.ui.main.timetable.inner.TimetableStyleSheet

class TimetableStyleRepository(application: Application) {
    private val timetableStyleSP: SharedPreferences = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
    val startDateLiveData = timetableStyleSP.intLiveData(KEY_START_DATE, 830)
    val drawBGLinesLiveData = timetableStyleSP.booleanLiveData(KEY_DRAW_BG_LINE, true)
    val colorEnableLiveData = timetableStyleSP.booleanLiveData(KEY_COLOR_ENABLE, true)
    val fadeEnableLiveData = timetableStyleSP.booleanLiveData(KEY_FADE_ENABLE, true)


    fun putData(key: String, value: Int) {
        timetableStyleSP.edit().putInt(key, value).apply()
    }

    fun putData(key: String, value: Boolean) {
        timetableStyleSP.edit().putBoolean(key, value).apply()
    }

    fun getStyleSheetLiveData(): MediatorLiveData<TimetableStyleSheet> {
        val sheet = MediatorLiveData<TimetableStyleSheet>()
        sheet.value = TimetableStyleSheet()
        sheet.addSource(startDateLiveData) { start ->
            val ts = sheet.value
            ts?.let {
                it.startTime = start
                sheet.value = it
            }
        }
        sheet.addSource(drawBGLinesLiveData) { draw ->
            val ts = sheet.value
            ts?.let {
                it.drawBGLine = draw
                sheet.value = it
            }
        }
        sheet.addSource(colorEnableLiveData) { draw ->
            val ts = sheet.value
            ts?.let {
                it.isColorEnabled = draw
                sheet.value = it
            }
        }
        sheet.addSource(fadeEnableLiveData) { fade ->
            val ts = sheet.value
            ts?.let {
                it.isFadeEnabled = fade
                sheet.value = it
            }
        }
        return sheet
    }

    companion object {
        private var instance: TimetableStyleRepository? = null
        fun getInstance(application: Application): TimetableStyleRepository {
            if (instance == null) instance = TimetableStyleRepository(application)
            return instance!!
        }

        const val SP_NAME = "timetable_style"
        const val KEY_START_DATE = "start_date"
        const val KEY_DRAW_BG_LINE = "draw_bg_line"
        const val KEY_COLOR_ENABLE = "color_enable"
        const val KEY_FADE_ENABLE = "fade_enable"
    }
}