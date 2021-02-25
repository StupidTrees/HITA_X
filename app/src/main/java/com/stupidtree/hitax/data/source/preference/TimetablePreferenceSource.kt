package com.stupidtree.hitax.data.source.preference


import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.stupidtree.hitax.data.model.timetable.TimeInDay
import com.stupidtree.hitax.data.model.timetable.TimePeriodInDay


class TimetablePreferenceSource(private val context: Context) {
    private var sharedPreferences: SharedPreferences? = null
    private val preference: SharedPreferences
        get() {
            if (sharedPreferences == null) {
                sharedPreferences = context.getSharedPreferences(SP_NAME_TIMETABLE, Context.MODE_PRIVATE)
            }
            return sharedPreferences!!
        }

    fun getSchedule(): MutableList<TimePeriodInDay> {
        var result: MutableList<TimePeriodInDay> = mutableListOf()
        val total = preference.getInt("class_num", -1)
        if (total < 0) {
            result = undergraduate_default
            saveSchedules(result)
            return result
        }
        for (i in 0 until total) {
            val tp: TimePeriodInDay = Gson().fromJson(sharedPreferences!!.getString("class_$i", "{}"), TimePeriodInDay::class.java)
            result.add(tp)
        }
        return result
    }

    fun saveSchedules(sch: List<TimePeriodInDay>) {
        val editor = preference.edit()
        for (i in sch.indices) {
            editor.putString("class_$i", Gson().toJson(sch[i]))
        }
        editor.putInt("class_num", sch.size).apply()
    }


    companion object {
        private const val SP_NAME_TIMETABLE = "timetable"

        @SuppressLint("StaticFieldLeak")
        private var instance: TimetablePreferenceSource? = null

        @JvmStatic
        fun getInstance(context: Context): TimetablePreferenceSource {
            if (instance == null) {
                instance = TimetablePreferenceSource(context.applicationContext)
            }
            return instance!!
        }


        var undergraduate_default = mutableListOf(
                TimePeriodInDay(TimeInDay(8, 30), TimeInDay(9, 20)),
                TimePeriodInDay(TimeInDay(9, 30), TimeInDay(10, 15)),
                TimePeriodInDay(TimeInDay(10, 30), TimeInDay(11, 20)),
                TimePeriodInDay(TimeInDay(11, 30), TimeInDay(12, 15)),
                TimePeriodInDay(TimeInDay(13, 45), TimeInDay(14, 35)),
                TimePeriodInDay(TimeInDay(14, 40), TimeInDay(15, 30)),
                TimePeriodInDay(TimeInDay(15, 45), TimeInDay(16, 35)),
                TimePeriodInDay(TimeInDay(16, 30), TimeInDay(17, 30)),
                TimePeriodInDay(TimeInDay(18, 30), TimeInDay(19, 20)),
                TimePeriodInDay(TimeInDay(19, 25), TimeInDay(20, 15)),
                TimePeriodInDay(TimeInDay(20, 30), TimeInDay(21, 20)),
                TimePeriodInDay(TimeInDay(21, 25), TimeInDay(22, 15)),
                TimePeriodInDay(TimeInDay(22, 30), TimeInDay(23, 20)))
    }

}