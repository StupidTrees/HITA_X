package com.stupidtree.hita.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.stupidtree.hita.data.AppDatabase
import com.stupidtree.hita.data.model.eas.CourseItem
import com.stupidtree.hita.data.model.eas.EASToken
import com.stupidtree.hita.data.model.eas.TermItem
import com.stupidtree.hita.data.model.timetable.EventItem
import com.stupidtree.hita.data.model.timetable.TimePeriodInDay
import com.stupidtree.hita.data.model.timetable.Timetable
import com.stupidtree.hita.data.source.preference.TimetablePreferenceSource
import com.stupidtree.hita.data.source.web.eas.EASource
import com.stupidtree.hita.data.source.web.service.EASService
import com.stupidtree.hita.ui.base.DataState
import java.sql.Timestamp
import java.util.*

class EASRepository internal constructor(application: Application) {

    private val easService: EASService = EASource()
    private var token:EASToken? = null
    private var eventItemDao = AppDatabase.getDatabase(application).eventItemDao()
    private var timetableDao = AppDatabase.getDatabase(application).timetableDao()
    private var timetablePreferenceSource = TimetablePreferenceSource.getInstance(application)

    /**
     * 进行登录
     */
    fun login(username: String, password: String):LiveData<DataState<Boolean>>{
        return Transformations.map(easService.login(username, password, null)){
            if(it.state==DataState.STATE.SUCCESS && it.data!=null){
                token = it.data
                return@map DataState(true, DataState.STATE.SUCCESS)
            }
            return@map DataState(false, it.state)
        };
    }

    /**
     * 获取学期开始日期
     */
    fun getStartDateOfTerm(term: TermItem):LiveData<DataState<Calendar>>{
        token?.let {
            return easService.getStartDate(it, term)
        }
        val res = MutableLiveData<DataState<Calendar>>()
        res.value = DataState(DataState.STATE.NOT_LOGGED_IN)
        return res
    }


    /**
     * 进行获取学年学期
     */
    fun getAllTerms():LiveData<DataState<List<TermItem>>>{
        return if(token!=null){
            easService.getAllTerms(token!!)
        }else{
            val res = MutableLiveData<DataState<List<TermItem>>>()
            res.value = DataState(DataState.STATE.NOT_LOGGED_IN)
            res
        }
    }

    /**
     * 获取课表结构
     */
    fun getScheduleStructure(term:TermItem): LiveData<DataState<MutableList<TimePeriodInDay>>> {
        return if(token!=null){
            easService.getScheduleStructure(term,token!!)
        }else{
            val res = MutableLiveData<DataState<MutableList<TimePeriodInDay>>>()
            res.value = DataState(DataState.STATE.NOT_LOGGED_IN)
            res
        }
    }

    /**
     * 动作：导入课表
     */
    private var timetableWebLiveData:LiveData<DataState<List<CourseItem>>>?= null
    fun startImportTimetableOfTerm(
            term: TermItem,
            startDate: Calendar,
            schedule:List<TimePeriodInDay>,//课表结构
            importTimetableLiveData:MediatorLiveData<DataState<Boolean>>
    ){
       // Log.e("startIm", term.toString())
        startDate.set(Calendar.DAY_OF_WEEK,1)
        if(token!=null){
            timetableWebLiveData?.let { importTimetableLiveData.removeSource(it) }
            timetableWebLiveData = easService.getTimetableOfTerm(term, token!!)
            importTimetableLiveData.addSource(timetableWebLiveData!!){
                if(it.state==DataState.STATE.SUCCESS){
                    Thread{
                        //更新timetable信息
                        var timetable = timetableDao.getTimetableByEASCode(term.getCode())
                        if(timetable==null){
                            timetable = Timetable()
                        }else{
                            //若存在，则先清空原有课表课程
                            eventItemDao.deleteCourseFromTimetable(timetable.id)
                        }
                        timetable.name = term.yearName+term.termName
                        timetable.startTime = Timestamp(startDate.timeInMillis)
                        timetable.code = term.getCode()
                        timetable.scheduleStructure = schedule
                        timetableDao.saveTimetable(timetable)

                        //添加时间表
                        val events = mutableListOf<EventItem>()
                       // val schedule = timetablePreferenceSource.getSchedule()
                        for(item in it.data!!){
                            val spStart = schedule[item.begin - 1]
                            val spEnd = schedule[item.begin+item.last-2]
                            for(week in item.weeks){
                                val from = getDateAtWOT(startDate,week,item.dow)
                                val to = getDateAtWOT(startDate,week,item.dow)
                                from.set(Calendar.HOUR_OF_DAY,spStart.from.hour)
                                from.set(Calendar.MINUTE,spStart.from.minute)
                                to.set(Calendar.HOUR_OF_DAY,spEnd.to.hour)
                                to.set(Calendar.MINUTE,spEnd.to.minute)
                                val e = EventItem()
                                e.name = item.name.toString()
                                e.from.time = from.timeInMillis
                                e.to.time = to.timeInMillis
                                e.teacher = item.teacher
                                e.place = item.classroom
                                e.timetableId = timetable.id
                                events.add(e)
                            }
                        }
                        eventItemDao.saveEvents(events)
                        importTimetableLiveData.postValue(DataState(false,DataState.STATE.SUCCESS))
                    }.start()
                }else{
                    importTimetableLiveData.value = DataState(DataState.STATE.FETCH_FAILED)
                }
            }
        }else{
            importTimetableLiveData.value = DataState(DataState.STATE.NOT_LOGGED_IN)
        }
    }


    fun getDateAtWOT(startDate: Calendar,WeekOfTerm: Int, DOW: Int): Calendar {
        val temp = Calendar.getInstance()
        val daysToPlus = (WeekOfTerm - 1) * 7
        temp.timeInMillis = startDate.timeInMillis
        temp.add(Calendar.DATE, daysToPlus)
        temp.add(Calendar.DAY_OF_MONTH, DOW - 1)
        return temp
    }

    companion object {
        private var instance: EASRepository? = null
        fun getInstance(application: Application): EASRepository {
            if (instance == null) instance = EASRepository(application)
            return instance!!
        }
    }
}