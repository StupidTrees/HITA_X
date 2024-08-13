package com.stupidtree.hitax.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.map
import com.stupidtree.hitax.data.AppDatabase
import com.stupidtree.hitax.data.model.timetable.TermSubject
import com.stupidtree.hitax.ui.timetable.detail.TeacherInfo
import com.stupidtree.hitax.utils.ColorTools
import com.stupidtree.sync.StupidSync
import com.stupidtree.sync.data.model.History
import java.util.concurrent.Executors

class SubjectRepository(application: Application) {
    private val historyTag = "subject"
    private val executor = Executors.newSingleThreadScheduledExecutor()
    private val eventItemDao = AppDatabase.getDatabase(application).eventItemDao()
    private val subjectDao = AppDatabase.getDatabase(application).subjectDao()
    private val timetableDao = AppDatabase.getDatabase(application).timetableDao()

    /**
     * 获取所有科目及其进度
     */
    fun getSubjects(timetableId: String): LiveData<List<TermSubject>> {
        return subjectDao.getSubjects(timetableId)
    }


    fun getSubjectById(subjectId: String): LiveData<TermSubject> {
        return subjectDao.getSubjectById(subjectId)
    }

    fun getSubjectColors(subjectId: String): LiveData<TermSubject> {
        return subjectDao.getSubjectById(subjectId)
    }

    fun getTeachersInfo(timetableId: String): LiveData<MutableList<TeacherInfo>> {
        return eventItemDao.getTeachersOfTimetable(timetableId).map{
            val result = mutableListOf<TeacherInfo>()
            val map = mutableMapOf<String, TeacherInfo?>()
            for (t in it) {
                if (map[t.name] == null) {
                    t.name?.let { it2 -> map[it2] = t }
                } else {
                    map[t.name]?.subjectName = map[t.name]?.subjectName + " " + t.subjectName
                }
            }
            for (x in map.values) {
                x?.let { it1 -> result.add(it1) }
            }
            return@map result
        }
    }

    fun getTeachersOfSubject(timetableId: String, subjectId: String): LiveData<List<String>> {
        return eventItemDao.getTeachersOfSubject(timetableId, subjectId)
    }

    /**
     * 动作：保存科目信息
     */
    fun actionSaveSubjectInfo(subject: TermSubject) {
        executor.execute {
            subjectDao.saveSubjectSync(subject)
            StupidSync.putHistorySync(historyTag, History.ACTION.REQUIRE, listOf(subject.id))
        }
    }

    fun actionResetRecentSubjectColors() {
        executor.execute {
            val timetable =
                timetableDao.getTimetableClosestToTimestampSync(System.currentTimeMillis())
            timetable?.let {
                val subjects = subjectDao.getSubjectsSync(it.id)
                for (s in subjects) {
                    s.color = ColorTools.randomColorMaterial()
                }
                subjectDao.saveSubjectsSync(subjects)
                StupidSync.putHistorySync(historyTag, History.ACTION.REQUIRE, subjects.getIds())
            }

        }
    }

    fun actionResetSubjectColors(timetableId: String) {
        executor.execute {
            val subjects = subjectDao.getSubjectsSync(timetableId)
            for (s in subjects) {
                s.color = ColorTools.randomColorMaterial()
            }
            subjectDao.saveSubjectsSync(subjects)
            StupidSync.putHistorySync(historyTag, History.ACTION.REQUIRE, subjects.getIds())
        }
    }


    /**
     * 动作：删除科目及其事件
     */
    fun actionDeleteSubjects(subjects: List<TermSubject>) {
        executor.execute {
            subjectDao.deleteSubjectsSync(subjects)
            val ids = subjects.getIds()
            StupidSync.putHistorySync(historyTag, History.ACTION.REMOVE, ids)
            eventItemDao.deleteEventsFromSubjectsSync(ids)
        }
    }
    fun actionChangeSubjectColor(subjectId:String,color:Int) {
        executor.execute {
            subjectDao.changeSubjectColorSync(subjectId,color)
        }
    }

    /**
     * 计算某一科目的进度
     * @return pair.first = 已完成数目,pair.second = 总数目
     *
     */
    fun getProgressOfSubject(subjectId: String, ts: Long): LiveData<Pair<Int, Int>> {
        val res = MediatorLiveData<Pair<Int, Int>>()
        res.addSource(eventItemDao.countClassesOfSubject(subjectId)) { total ->
            res.addSource(eventItemDao.countClassesBeforeTimeOfSubject(subjectId, ts)) { finished ->
                res.value = Pair(finished, total)
            }
        }
        return res
    }

    companion object {
        private var instance: SubjectRepository? = null
        fun getInstance(application: Application): SubjectRepository {
            if (instance == null) instance = SubjectRepository(application)
            return instance!!
        }
    }

    fun List<TermSubject>.getIds(): List<String> {
        val ids = mutableListOf<String>()
        for (s in this) {
            ids.add(s.id)
        }
        return ids
    }
}