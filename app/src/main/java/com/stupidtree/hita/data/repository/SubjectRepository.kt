package com.stupidtree.hita.data.repository

import android.app.Application
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.stupidtree.hita.data.AppDatabase
import com.stupidtree.hita.data.model.timetable.TermSubject
import com.stupidtree.hita.ui.timetable.subject.TeacherInfo
import com.stupidtree.hita.utils.TimeUtils

class SubjectRepository(application: Application) {
    private val eventItemDao = AppDatabase.getDatabase(application).eventItemDao()
    private val subjectDao = AppDatabase.getDatabase(application).subjectDao()


    /**
     * 获取所有科目及其进度
     */
    fun getSubjectsAndProgress(timetableId: String): LiveData<MutableList<Pair<TermSubject, Float>>> {
        val result = MutableLiveData<MutableList<Pair<TermSubject, Float>>>()
        Thread @WorkerThread {
            val subjects = subjectDao.getSubjects(timetableId)
            val res: MutableList<Pair<TermSubject, Float>> = mutableListOf()
            for (subject in subjects) {
                var finished = 0
                var unfinished = 0
                val events = eventItemDao.getClassesOfSubjectSync(subject.id)
                for (ei in events) {
                    if (TimeUtils.passed(ei.to)) finished++ else unfinished++
                }
                val x: Float = finished.toFloat() / (finished + unfinished)
                res.add(Pair(subject, x))
            }
            result.postValue(res)
        }.start()
        return result
    }


    fun getSubjectById(subjectId: String): LiveData<TermSubject> {
        return subjectDao.getSubjectById(subjectId)
    }

    fun getTeachersInfo(timetableId: String): LiveData<MutableList<TeacherInfo>> {
        return Transformations.map(eventItemDao.getTeachersOfTimetable(timetableId)) {
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

    fun getTeachersOfSubject(timetableId: String,subjectId: String):LiveData<List<String>>{
        return eventItemDao.getTeachersOfSubject(timetableId,subjectId)
    }

    /**
     * 计算某一科目的进度
     * @return pair.first = 已完成数目,pair.second = 总数目
     *
     */
    fun getProgressOfSubject(subjectId: String, ts: Long): LiveData<Pair<Int, Int>> {
        val res = MutableLiveData<Pair<Int, Int>>()
        Thread {
            val total = eventItemDao.countClassesOfSubjectSync(subjectId)
            val finished = eventItemDao.countClassesBeforeTimeOfSubjectSync(
                subjectId,
                ts
            )
            res.postValue(Pair(finished, total))
        }.start()
        return res
    }

    companion object {
        private var instance: SubjectRepository? = null
        fun getInstance(application: Application): SubjectRepository {
            if (instance == null) instance = SubjectRepository(application)
            return instance!!
        }
    }
}